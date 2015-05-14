/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.pap.PAPEngine;
import org.apache.openaz.xacml.api.pap.PAPEngineFactory;
import org.apache.openaz.xacml.api.pap.PAPException;
import org.apache.openaz.xacml.api.pap.PDP;
import org.apache.openaz.xacml.api.pap.PDPGroup;
import org.apache.openaz.xacml.api.pap.PDPPolicy;
import org.apache.openaz.xacml.api.pap.PDPStatus;
import org.apache.openaz.xacml.std.pap.StdPDP;
import org.apache.openaz.xacml.std.pap.StdPDPGroup;
import org.apache.openaz.xacml.std.pap.StdPDPItemSetChangeNotifier.StdItemSetChangeListener;
import org.apache.openaz.xacml.std.pap.StdPDPStatus;
import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.XACMLProperties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;

/**
 * Servlet implementation class XacmlPapServlet
 */
@WebServlet(description = "Implements the XACML PAP RESTful API.", urlPatterns = {
    "/"
}, loadOnStartup = 1, initParams = {
                          @WebInitParam(name = "XACML_PROPERTIES_NAME", value = "xacml.pap.properties", description = "The location of the properties file holding configuration information.")
            })
public class XACMLPapServlet extends HttpServlet implements StdItemSetChangeListener, Runnable {
    private static final long serialVersionUID = 1L;
    private static final Log logger = LogFactory.getLog(XACMLPapServlet.class);

    /*
     * papEngine - This is our engine workhorse that manages the PDP Groups and Nodes.
     */
    private PAPEngine papEngine = null;

    /*
     * This PAP instance's own URL. Need this when creating URLs to send to the PDPs so they can GET the
     * Policy files from this process.
     */
    private static String papURL = null;

    /*
     * List of Admin Console URLs. Used to send notifications when configuration changes. The
     * CopyOnWriteArrayList *should* protect from concurrency errors. This list is seldom changed but often
     * read, so the costs of this approach make sense.
     */
    private static final CopyOnWriteArrayList<String> adminConsoleURLStringList = new CopyOnWriteArrayList<String>();

    /*
     * This thread may be invoked upon startup to initiate sending PDP policy/pip configuration when this
     * servlet starts. Its configurable by the admin.
     */
    private Thread initiateThread = null;

    /*
     * // The heartbeat thread.
     */
    private static Heartbeat heartbeat = null;
    private static Thread heartbeatThread = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public XACMLPapServlet() {
        super();
    }

    /**
     * @see Servlet#init(ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            //
            // Initialize
            //
            XACMLRest.xacmlInit(config);
            //
            // Load the properties
            //
            XACMLRest.loadXacmlProperties(null, null);
            //
            // Load our PAP engine, first create a factory
            //
            PAPEngineFactory factory = PAPEngineFactory.newInstance(XACMLProperties
                .getProperty(XACMLProperties.PROP_PAP_PAPENGINEFACTORY));
            //
            // The factory knows how to go about creating a PAP Engine
            //
            this.papEngine = factory.newEngine();
            //
            // we are about to call the PDPs and give them their configuration.
            // To do that we need to have the URL of this PAP so we can construct the Policy file URLs
            //
            XACMLPapServlet.papURL = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
            //
            // Sanity check that a URL was defined somewhere, its essential.
            //
            // How to check that its valid? We can validate the form, but since we are in the init() method we
            // are not fully loaded yet so we really couldn't ping ourself to see if the URL will work. One
            // will have to look for errors in the PDP logs to determine if they are failing to initiate a
            // request to this servlet.
            //
            if (XACMLPapServlet.papURL == null) {
                throw new PAPException("The property " + XACMLRestProperties.PROP_PAP_URL + " is not valid: "
                                       + XACMLPapServlet.papURL);
            }
            //
            // Configurable - have the PAP servlet initiate sending the latest PDP policy/pip configuration
            // to all its known PDP nodes.
            //
            // Note: parseBoolean will return false if there is no property defined. This is fine for a
            // default.
            //
            if (Boolean.parseBoolean(XACMLProperties
                .getProperty(XACMLRestProperties.PROP_PAP_INITIATE_PDP_CONFIG))) {
                this.initiateThread = new Thread(this);
                this.initiateThread.start();
            }
            //
            // After startup, the PAP does Heartbeats to each of the PDPs periodically
            //
            XACMLPapServlet.heartbeat = new Heartbeat(this.papEngine);
            XACMLPapServlet.heartbeatThread = new Thread(XACMLPapServlet.heartbeat);
            XACMLPapServlet.heartbeatThread.start();
        } catch (FactoryException | PAPException e) {
            logger.error("Failed to create engine", e);
            throw new ServletException("PAP not initialized; error: " + e);
        } catch (Exception e) {
            logger.error("Failed to create engine - unexpected error: ", e);
            throw new ServletException("PAP not initialized; unexpected error: " + e);
        }
    }

    /**
     * Thread used only during PAP startup to initiate change messages to all known PDPs. This must be on a
     * separate thread so that any GET requests from the PDPs during this update can be serviced.
     */
    @Override
    public void run() {
        //
        // send the current configuration to all the PDPs that we know about
        //
        changed();
    }

    /**
     * @see Servlet#destroy() Depending on how this servlet is run, we may or may not care about cleaning up
     *      the resources. For now we assume that we do care.
     */
    @Override
    public void destroy() {
        //
        // Make sure our threads are destroyed
        //
        if (XACMLPapServlet.heartbeatThread != null) {
            //
            // stop the heartbeat
            //
            try {
                if (XACMLPapServlet.heartbeat != null) {
                    XACMLPapServlet.heartbeat.terminate();
                }
                XACMLPapServlet.heartbeatThread.interrupt();
                XACMLPapServlet.heartbeatThread.join();
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
        if (this.initiateThread != null) {
            try {
                this.initiateThread.interrupt();
                this.initiateThread.join();
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

    /**
     * Called by: - PDP nodes to register themselves with the PAP, and - Admin Console to make changes in the
     * PDP Groups.
     *
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {
        try {

            XACMLRest.dumpRequest(request);

            // since getParameter reads the content string, explicitly get the content before doing that.
            // Simply getting the inputStream seems to protect it against being consumed by getParameter.
            request.getInputStream();

            //
            // Is this from the Admin Console?
            //
            String groupId = request.getParameter("groupId");
            if (groupId != null) {
                //
                // this is from the Admin Console, so handle separately
                //
                doACPost(request, response, groupId);
                return;
            }

            //
            // Request is from a PDP.
            // It is coming up and asking for its config
            //

            //
            // Get the PDP's ID
            //
            String id = this.getPDPID(request);
            logger.info("doPost from: " + id);
            //
            // Get the PDP Object
            //
            PDP pdp = this.papEngine.getPDP(id);
            //
            // Is it known?
            //
            if (pdp == null) {
                logger.info("Unknown PDP: " + id);
                try {
                    this.papEngine.newPDP(id, this.papEngine.getDefaultGroup(), id,
                                          "Registered on first startup");
                } catch (NullPointerException | PAPException e) {
                    logger.error("Failed to create new PDP", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
                // get the PDP we just created
                pdp = this.papEngine.getPDP(id);
                if (pdp == null) {
                    String message = "Failed to create new PDP for id: " + id;
                    logger.error(message);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                    return;
                }
            }
            //
            // Get the PDP's Group
            //
            PDPGroup group = this.papEngine.getPDPGroup(pdp);
            if (group == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                   "PDP not associated with any group, even the default");
                return;
            }
            //
            // Determine what group the PDP node is in and get
            // its policy/pip properties.
            //
            Properties policies = group.getPolicyProperties();
            Properties pipconfig = group.getPipConfigProperties();
            //
            // Get the current policy/pip configuration that the PDP has
            //
            Properties pdpProperties = new Properties();
            pdpProperties.load(request.getInputStream());
            logger.info("PDP Current Properties: " + pdpProperties.toString());
            logger.info("Policies: " + (policies != null ? policies.toString() : "null"));
            logger.info("Pip config: " + (pipconfig != null ? pipconfig.toString() : "null"));
            //
            // Validate the node's properties
            //
            boolean isCurrent = this.isPDPCurrent(policies, pipconfig, pdpProperties);
            //
            // Send back current configuration
            //
            if (!isCurrent) {
                //
                // Tell the PDP we are sending back the current policies/pip config
                //
                logger.info("PDP configuration NOT current.");
                if (policies != null) {
                    //
                    // Put URL's into the properties in case the PDP needs to
                    // retrieve them.
                    //
                    this.populatePolicyURL(request.getRequestURL(), policies);
                    //
                    // Copy the properties to the output stream
                    //
                    policies.store(response.getOutputStream(), "");
                }
                if (pipconfig != null) {
                    //
                    // Copy the properties to the output stream
                    //
                    pipconfig.store(response.getOutputStream(), "");
                }
                //
                // We are good - and we are sending them information
                //
                response.setStatus(HttpServletResponse.SC_OK);
                // TODO - Correct?
                setPDPSummaryStatus(pdp, PDPStatus.Status.OUT_OF_SYNCH);
            } else {
                //
                // Tell them they are good
                //
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);

                // TODO - Correct?
                setPDPSummaryStatus(pdp, PDPStatus.Status.UP_TO_DATE);

            }
            //
            // tell the AC that something changed
            //
            notifyAC();
        } catch (PAPException e) {
            logger.debug("POST exception: " + e, e);
            response.sendError(500, e.getMessage());
            return;
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {
        try {
            XACMLRest.dumpRequest(request);

            // Is this from the Admin Console?
            String groupId = request.getParameter("groupId");
            if (groupId != null) {
                // this is from the Admin Console, so handle separately
                doACGet(request, response, groupId);
                return;
            }
            //
            // Get the PDP's ID
            //
            String id = this.getPDPID(request);
            logger.info("doGet from: " + id);
            //
            // Get the PDP Object
            //
            PDP pdp = this.papEngine.getPDP(id);
            //
            // Is it known?
            //
            if (pdp == null) {
                //
                // Check if request came from localhost
                //
                String message = "Unknown PDP: " + id + " from " + request.getRemoteHost() + " us: "
                                 + request.getLocalAddr();
                logger.info(message);
                if (request.getRemoteHost().equals("localhost")
                    || request.getRemoteHost().equals("127.0.0.1") //NOPMD
                    || request.getRemoteHost().equals(request.getLocalAddr())) {
                    //
                    // Return status information - basically all the groups
                    //
                    Set<PDPGroup> groups = papEngine.getPDPGroups();

                    // convert response object to JSON and include in the response
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(response.getOutputStream(), groups);
                    response.setHeader("content-type", "application/json");
                    response.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
                return;
            }
            //
            // Get the PDP's Group
            //
            PDPGroup group = this.papEngine.getPDPGroup(pdp);
            if (group == null) {
                String message = "No group associated with pdp " + pdp.getId();
                logger.warn(message);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
                return;
            }
            //
            // Which policy do they want?
            //
            String policyId = request.getParameter("id");
            if (policyId == null) {
                String message = "Did not specify an id for the policy";
                logger.warn(message);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
                return;
            }
            PDPPolicy policy = group.getPolicy(policyId);
            if (policy == null) {
                String message = "Unknown policy: " + policyId;
                logger.warn(message);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
                return;
            }
            //
            // Get its stream
            //
            try (InputStream is = policy.getStream(); OutputStream os = response.getOutputStream()) {
                //
                // Send the policy back
                //
                IOUtils.copy(is, os);

                response.setStatus(HttpServletResponse.SC_OK);
            } catch (PAPException e) {
                String message = "Failed to open policy id " + policyId;
                logger.error(message);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
            }
        } catch (PAPException e) {
            logger.error("GET exception: " + e, e);
            response.sendError(500, e.getMessage());
            return;
        }
    }

    protected String getPDPID(HttpServletRequest request) {
        String pdpURL = request.getHeader(XACMLRestProperties.PROP_PDP_HTTP_HEADER_ID);
        if (pdpURL == null || pdpURL.isEmpty()) {
            //
            // Should send back its port for identification
            //
            logger.warn("PDP did not send custom header");
            pdpURL = "";
        }
        return pdpURL;
    }

    private boolean isPDPCurrent(Properties policies, Properties pipconfig, Properties pdpProperties) {
        String localRootPolicies = policies.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
        String localReferencedPolicies = policies.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
        if (localRootPolicies == null || localReferencedPolicies == null) {
            logger.warn("Missing property on PAP server: RootPolicies=" + localRootPolicies
                        + "  ReferencedPolicies=" + localReferencedPolicies);
            return false;
        }
        //
        // Compare the policies and pipconfig properties to the pdpProperties
        //
        try {
            //
            // the policy properties includes only xacml.rootPolicies and
            // xacml.referencedPolicies without any .url entries
            //
            Properties pdpPolicies = XACMLProperties.getPolicyProperties(pdpProperties, false);
            Properties pdpPipConfig = XACMLProperties.getPipProperties(pdpProperties);
            if (localRootPolicies.equals(pdpPolicies.getProperty(XACMLProperties.PROP_ROOTPOLICIES))
                && localReferencedPolicies.equals(pdpPolicies
                    .getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES)) && pdpPipConfig.equals(pipconfig)) {
                //
                // The PDP is current
                //
                return true;
            }
        } catch (Exception e) { //NOPMD
            // we get here if the PDP did not include either xacml.rootPolicies or xacml.pip.engines,
            // or if there are policies that do not have a corresponding ".url" property.
            // Either of these cases means that the PDP is not up-to-date, so just drop-through to return
            // false.
        }
        return false;
    }

    private void populatePolicyURL(StringBuffer urlPath, Properties policies) {
        String lists[] = new String[2];
        lists[0] = policies.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
        lists[1] = policies.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
        for (String list : lists) {
            if (list != null && !list.isEmpty()) {
                for (String id : Splitter.on(',').trimResults().omitEmptyStrings().split(list)) {
                    String url = urlPath + "?id=" + id;
                    logger.info("Policy URL for " + id + ": " + url);
                    policies.setProperty(id + ".url", url);
                }
            }
        }
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {
        XACMLRest.dumpRequest(request);
        //
        // since getParameter reads the content string, explicitly get the content before doing that.
        // Simply getting the inputStream seems to protect it against being consumed by getParameter.
        //
        request.getInputStream();
        //
        // See if this is Admin Console registering itself with us
        //
        String acURLString = request.getParameter("adminConsoleURL");
        if (acURLString != null) {
            //
            // remember this Admin Console for future updates
            //
            if (!adminConsoleURLStringList.contains(acURLString)) {
                adminConsoleURLStringList.add(acURLString);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Admin Console registering with URL: " + acURLString);
            }
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        //
        // Is this some other operation from the Admin Console?
        //
        String groupId = request.getParameter("groupId");
        if (groupId != null) {
            //
            // this is from the Admin Console, so handle separately
            //
            doACPut(request, response, groupId);
            return;
        }
        //
        // We do not expect anything from anywhere else.
        // This method is here in case we ever need to support other operations.
        //
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request does not have groupId");
    }

    /**
     * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        XACMLRest.dumpRequest(request);
        //
        // Is this from the Admin Console?
        //
        String groupId = request.getParameter("groupId");
        if (groupId != null) {
            //
            // this is from the Admin Console, so handle separately
            //
            doACDelete(request, response, groupId);
            return;
        }
        //
        // We do not expect anything from anywhere else.
        // This method is here in case we ever need to support other operations.
        //
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request does not have groupId");
    }

    //
    // Admin Console request handling
    //

    /**
     * Requests from the Admin Console to GET info about the Groups and PDPs
     *
     * @param request
     * @param response
     * @param groupId
     * @throws ServletException
     * @throws java.io.IOException
     */
    private void doACGet(HttpServletRequest request, HttpServletResponse response, String groupId)
        throws ServletException, IOException {
        try {
            String parameterDefault = request.getParameter("default");
            String pdpId = request.getParameter("pdpId");
            String pdpGroup = request.getParameter("getPDPGroup");
            if ("".equals(groupId)) {
                // request IS from AC but does not identify a group by name
                if (parameterDefault != null) {
                    // Request is for the Default group (whatever its id)
                    PDPGroup group = papEngine.getDefaultGroup();

                    // convert response object to JSON and include in the response
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(response.getOutputStream(), group);

                    if (logger.isDebugEnabled()) {
                        logger.debug("GET Default group req from '" + request.getRequestURL() + "'");
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader("content-type", "application/json");
                    response.getOutputStream().close();
                    return;

                } else if (pdpId != null) {
                    // Request is related to a PDP
                    if (pdpGroup == null) {
                        // Request is for the PDP itself
                        // Request is for the (unspecified) group containing a given PDP
                        PDP pdp = papEngine.getPDP(pdpId);

                        // convert response object to JSON and include in the response
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.writeValue(response.getOutputStream(), pdp);

                        if (logger.isDebugEnabled()) {
                            logger
                                .debug("GET pdp '" + pdpId + "' req from '" + request.getRequestURL() + "'");
                        }
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setHeader("content-type", "application/json");
                        response.getOutputStream().close();
                        return;

                    } else {
                        // Request is for the (unspecified) group containing a given PDP
                        PDP pdp = papEngine.getPDP(pdpId);
                        PDPGroup group = papEngine.getPDPGroup(pdp);

                        // convert response object to JSON and include in the response
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.writeValue(response.getOutputStream(), group);

                        if (logger.isDebugEnabled()) {
                            logger.debug("GET PDP '" + pdpId + "' Group req from '" + request.getRequestURL()
                                         + "'");
                        }
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setHeader("content-type", "application/json");
                        response.getOutputStream().close();
                        return;
                    }

                } else {
                    // request is for top-level properties about all groups
                    Set<PDPGroup> groups = papEngine.getPDPGroups();

                    // convert response object to JSON and include in the response
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(response.getOutputStream(), groups);

                    // TODO
                    // In "notification" section, ALSO need to tell AC about other changes (made by other
                    // ACs)?'
                    // TODO add new PDP notification (or just "config changed" notification) in appropriate
                    // place
                    if (logger.isDebugEnabled()) {
                        logger.debug("GET All groups req");
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader("content-type", "application/json");
                    response.getOutputStream().close();
                    return;
                }
            }

            // for all other GET operations the group must exist before the operation can be done
            PDPGroup group = papEngine.getGroup(groupId);
            if (group == null) {
                logger.error("Unknown groupId '" + groupId + "'");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown groupId '" + groupId + "'");
                return;
            }

            // Figure out which request this is based on the parameters
            String policyId = request.getParameter("policyId");

            if (policyId != null) {
                // // retrieve a policy
                // PDPPolicy policy = papEngine.getPDPPolicy(policyId);
                //
                // // convert response object to JSON and include in the response
                // ObjectMapper mapper = new ObjectMapper();
                // mapper.writeValue(response.getOutputStream(), pdp);
                //
                // logger.debug("GET group '" + group.getId() + "' req from '" + request.getRequestURL() +
                // "'");
                // response.setStatus(HttpServletResponse.SC_OK);
                // response.setHeader("content-type", "application/json");
                // response.getOutputStream().close();
                // return;
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "GET Policy not implemented");

            } else {
                // No other parameters, so return the identified Group

                // convert response object to JSON and include in the response
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(response.getOutputStream(), group);

                if (logger.isDebugEnabled()) {
                    logger.debug("GET group '" + group.getId() + "' req from '" + request.getRequestURL()
                                 + "'");
                }
                response.setStatus(HttpServletResponse.SC_OK);
                response.setHeader("content-type", "application/json");
                response.getOutputStream().close();
                return;
            }

            //
            // Currently there are no other GET calls from the AC.
            // The AC uses the "GET All Groups" operation to fill its local cache and uses that cache for all
            // other GETs without calling the PAP.
            // Other GETs that could be called:
            // Specific Group (groupId=<groupId>)
            // A Policy (groupId=<groupId> policyId=<policyId>)
            // A PDP (groupId=<groupId> pdpId=<pdpId>)

            // TODO - implement other GET operations if needed

            logger.error("UNIMPLEMENTED ");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
        } catch (PAPException e) {
            logger.error("AC Get exception: " + e, e);
            response.sendError(500, e.getMessage());
            return;
        }

    }

    /**
     * Requests from the Admin Console for operations not on single specific objects
     *
     * @param request
     * @param response
     * @param groupId
     * @throws ServletException
     * @throws java.io.IOException
     */
    private void doACPost(HttpServletRequest request, HttpServletResponse response, String groupId)
        throws ServletException, IOException {
        try {
            String groupName = request.getParameter("groupName");
            String groupDescription = request.getParameter("groupDescription");
            if (groupName != null && groupDescription != null) {
                // Args: group=<groupId> groupName=<name> groupDescription=<description> <= create a new group
                String unescapedName = URLDecoder.decode(groupName, "UTF-8");
                String unescapedDescription = URLDecoder.decode(groupDescription, "UTF-8");
                try {
                    papEngine.newGroup(unescapedName, unescapedDescription);
                } catch (Exception e) {
                    logger.error("Unable to create new group: " + e.getLocalizedMessage());
                    response.sendError(500, "Unable to create new group '" + groupId + "'");
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (logger.isDebugEnabled()) {
                    logger.debug("New Group '" + groupId + "' created");
                }
                // tell the Admin Consoles there is a change
                notifyAC();
                // new group by definition has no PDPs, so no need to notify them of changes
                return;
            }

            // for all remaining POST operations the group must exist before the operation can be done
            PDPGroup group = papEngine.getGroup(groupId);
            if (group == null) {
                logger.error("Unknown groupId '" + groupId + "'");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown groupId '" + groupId + "'");
                return;
            }

            // determine the operation needed based on the parameters in the request
            if (request.getParameter("policyId") != null) {
                // Args: group=<groupId> policy=<policyId> <= copy file
                // copy a policy from the request contents into a file in the group's directory on this
                // machine
                String policyId = request.getParameter("policyId");
                try {
                    ((StdPDPGroup)group).copyPolicyToFile(policyId, request.getInputStream());
                } catch (Exception e) {
                    String message = "Policy '" + policyId + "' not copied to group '" + groupId + "': " + e;
                    logger.error(message);
                    response.sendError(500, message);
                    return;
                }
                // policy file copied ok
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (logger.isDebugEnabled()) {
                    logger.debug("policy '" + policyId + "' copied to directory for group '" + groupId + "'");
                }
                return;

            } else if (request.getParameter("default") != null) {
                // Args: group=<groupId> default=true <= make default
                // change the current default group to be the one identified in the request.
                //
                // This is a POST operation rather than a PUT "update group" because of the side-effect that
                // the current default group is also changed.
                // It should never be the case that multiple groups are currently marked as the default, but
                // protect against that anyway.
                try {
                    papEngine.SetDefaultGroup(group);
                } catch (Exception e) {
                    logger.error("Unable to set group: " + e.getLocalizedMessage());
                    response.sendError(500, "Unable to set group '" + groupId + "' to default");
                    return;
                }

                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (logger.isDebugEnabled()) {
                    logger.debug("Group '" + groupId + "' set to be default");
                }
                // Notify the Admin Consoles that something changed
                // For now the AC cannot handle anything more detailed than the whole set of PDPGroups, so
                // just notify on that
                // TODO - Future: FIGURE OUT WHAT LEVEL TO NOTIFY: 2 groups or entire set - currently notify
                // AC to update whole configuration of all groups
                notifyAC();
                // This does not affect any PDPs in the existing groups, so no need to notify them of this
                // change
                return;

            } else if (request.getParameter("pdpId") != null) {
                // Args: group=<groupId> pdpId=<pdpId> <= move PDP to group
                String pdpId = request.getParameter("pdpId");
                PDP pdp = papEngine.getPDP(pdpId);

                PDPGroup originalGroup = papEngine.getPDPGroup(pdp);

                papEngine.movePDP(pdp, group);

                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (logger.isDebugEnabled()) {
                    logger.debug("PDP '" + pdp.getId() + "' moved to group '" + group.getId()
                                 + "' set to be default");
                }

                // update the status of both the original group and the new one
                ((StdPDPGroup)originalGroup).resetStatus();
                ((StdPDPGroup)group).resetStatus();

                // Notify the Admin Consoles that something changed
                // For now the AC cannot handle anything more detailed than the whole set of PDPGroups, so
                // just notify on that
                notifyAC();
                // Need to notify the PDP that it's config may have changed
                pdpChanged(pdp);
                return;

            }
        } catch (PAPException e) {
            logger.error("AC POST exception: " + e, e);
            response.sendError(500, e.getMessage());
            return;
        }
    }

    /**
     * Requests from the Admin Console to create new items or update existing ones
     *
     * @param request
     * @param response
     * @param groupId
     * @throws ServletException
     * @throws java.io.IOException
     */
    private void doACPut(HttpServletRequest request, HttpServletResponse response, String groupId)
        throws ServletException, IOException {
        try {

            // for PUT operations the group may or may not need to exist before the operation can be done
            PDPGroup group = papEngine.getGroup(groupId);

            // determine the operation needed based on the parameters in the request

            // for remaining operations the group must exist before the operation can be done
            if (group == null) {
                logger.error("Unknown groupId '" + groupId + "'");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown groupId '" + groupId + "'");
                return;
            }
            if (request.getParameter("policy") != null) {
                // group=<groupId> policy=<policyId> contents=policy file <= Create new policy file in group
                // dir, or replace it if it already exists (do not touch properties)
                // TODO - currently this is done by the AC, but it should be done here by getting the policy
                // file out of the contents and saving to disk
                logger.error("PARTIALLY IMPLEMENTED!!!  ACTUAL CHANGES SHOULD BE MADE BY PAP SERVLET!!! ");
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            } else if (request.getParameter("pdpId") != null) {
                // ARGS: group=<groupId> pdpId=<pdpId/URL> <= create a new PDP or Update an Existing one

                String pdpId = request.getParameter("pdpId");

                // get the request content into a String
                String json = null;
                // read the inputStream into a buffer (trick found online scans entire input looking for
                // end-of-file)
                Scanner scanner = new Scanner(request.getInputStream());
                scanner.useDelimiter("\\A");
                json = scanner.hasNext() ? scanner.next() : "";
                scanner.close();
                logger.info("JSON request from AC: " + json);

                // convert Object sent as JSON into local object
                ObjectMapper mapper = new ObjectMapper();

                Object objectFromJSON = mapper.readValue(json, StdPDP.class);

                if (pdpId == null || objectFromJSON == null || !(objectFromJSON instanceof StdPDP)
                    || ((StdPDP)objectFromJSON).getId() == null
                    || !((StdPDP)objectFromJSON).getId().equals(pdpId)) {
                    logger.error("PDP new/update had bad input. pdpId=" + pdpId + " objectFromJSON="
                                 + objectFromJSON);
                    response.sendError(500, "Bad input, pdpid=" + pdpId + " object=" + objectFromJSON);
                }
                StdPDP pdp = (StdPDP)objectFromJSON;

                if (papEngine.getPDP(pdpId) == null) {
                    // this is a request to create a new PDP object
                    papEngine.newPDP(pdp.getId(), group, pdp.getName(), pdp.getDescription());
                } else {
                    // this is a request to update the pdp
                    papEngine.updatePDP(pdp);
                }

                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (logger.isDebugEnabled()) {
                    logger.debug("PDP '" + pdpId + "' created/updated");
                }

                // adjust the group's state including the new PDP
                ((StdPDPGroup)group).resetStatus();

                // tell the Admin Consoles there is a change
                notifyAC();
                // this might affect the PDP, so notify it of the change
                pdpChanged(pdp);
                return;
            } else if (request.getParameter("pipId") != null) {
                // group=<groupId> pipId=<pipEngineId> contents=pip properties <= add a PIP to pip config, or
                // replace it if it already exists (lenient operation)
                // TODO
                logger.error("UNIMPLEMENTED ");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
                return;
            } else {
                // Assume that this is an update of an existing PDP Group
                // ARGS: group=<groupId> <= Update an Existing Group

                // get the request content into a String
                String json = null;
                // read the inputStream into a buffer (trick found online scans entire input looking for
                // end-of-file)
                Scanner scanner = new Scanner(request.getInputStream());
                scanner.useDelimiter("\\A");
                json = scanner.hasNext() ? scanner.next() : "";
                scanner.close();
                logger.info("JSON request from AC: " + json);

                // convert Object sent as JSON into local object
                ObjectMapper mapper = new ObjectMapper();

                Object objectFromJSON = mapper.readValue(json, StdPDPGroup.class);

                if (objectFromJSON == null || !(objectFromJSON instanceof StdPDPGroup)
                    || !((StdPDPGroup)objectFromJSON).getId().equals(group.getId())) {
                    logger.error("Group update had bad input. id=" + group.getId() + " objectFromJSON="
                                 + objectFromJSON);
                    response.sendError(500, "Bad input, id=" + group.getId() + " object=" + objectFromJSON);
                }

                // The Path on the PAP side is not carried on the RESTful interface with the AC
                // (because it is local to the PAP)
                // so we need to fill that in before submitting the group for update
                ((StdPDPGroup)objectFromJSON).setDirectory(((StdPDPGroup)group).getDirectory());

                papEngine.updateGroup((StdPDPGroup)objectFromJSON);

                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (logger.isDebugEnabled()) {
                    logger.debug("Group '" + group.getId() + "' updated");
                }
                // tell the Admin Consoles there is a change
                notifyAC();
                // Group changed, which might include changing the policies
                groupChanged(group);
                return;
            }
        } catch (PAPException e) {
            logger.error("AC PUT exception: " + e, e);
            response.sendError(500, e.getMessage());
            return;
        }
    }

    /**
     * Requests from the Admin Console to delete/remove items
     *
     * @param request
     * @param response
     * @param groupId
     * @throws ServletException
     * @throws java.io.IOException
     */
    private void doACDelete(HttpServletRequest request, HttpServletResponse response, String groupId)
        throws ServletException, IOException {
        try {
            // for all DELETE operations the group must exist before the operation can be done
            PDPGroup group = papEngine.getGroup(groupId);
            if (group == null) {
                logger.error("Unknown groupId '" + groupId + "'");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown groupId '" + groupId + "'");
                return;
            }
            // determine the operation needed based on the parameters in the request
            if (request.getParameter("policy") != null) {
                // group=<groupId> policy=<policyId> [delete=<true|false>] <= delete policy file from group
                // TODO
                logger.error("UNIMPLEMENTED ");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
                return;
            } else if (request.getParameter("pdpId") != null) {
                // ARGS: group=<groupId> pdpId=<pdpId> <= delete PDP
                String pdpId = request.getParameter("pdpId");
                PDP pdp = papEngine.getPDP(pdpId);

                papEngine.removePDP(pdp);

                // adjust the status of the group, which may have changed when we removed this PDP
                ((StdPDPGroup)group).resetStatus();

                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                notifyAC();

                // update the PDP and tell it that it has NO Policies (which prevents it from serving PEP
                // Requests)
                pdpChanged(pdp);
                return;
            } else if (request.getParameter("pipId") != null) {
                // group=<groupId> pipId=<pipEngineId> <= delete PIP config for given engine
                // TODO
                logger.error("UNIMPLEMENTED ");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "UNIMPLEMENTED");
                return;
            } else {
                // ARGS: group=<groupId> movePDPsToGroupId=<movePDPsToGroupId> <= delete a group and move all
                // its PDPs to the given group
                String moveToGroupId = request.getParameter("movePDPsToGroupId");
                PDPGroup moveToGroup = null;
                if (moveToGroupId != null) {
                    moveToGroup = papEngine.getGroup(moveToGroupId);
                }

                // get list of PDPs in the group being deleted so we can notify them that they got changed
                Set<PDP> movedPDPs = new HashSet<PDP>();
                movedPDPs.addAll(group.getPdps());

                // do the move/remove
                papEngine.removeGroup(group, moveToGroup);

                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                notifyAC();
                // notify any PDPs in the removed set that their config may have changed
                for (PDP pdp : movedPDPs) {
                    pdpChanged(pdp);
                }
                return;
            }

        } catch (PAPException e) {
            logger.error("AC DELETE exception: " + e, e);
            response.sendError(500, e.getMessage());
            return;
        }
    }

    //
    // Heartbeat thread - periodically check on PDPs' status
    //

    /**
     * Heartbeat with all known PDPs. Implementation note: The PDPs are contacted Sequentially, not in
     * Parallel. If we did this in parallel using multiple threads we would simultaneously use - 1 thread and
     * - 1 connection for EACH PDP. This could become a resource problem since we already use multiple threads
     * and connections for updating the PDPs when user changes occur. Using separate threads can also make it
     * tricky dealing with timeouts on PDPs that are non-responsive. The Sequential operation does a heartbeat
     * request to each PDP one at a time. This has the flaw that any PDPs that do not respond will hold up the
     * entire heartbeat sequence until they timeout. If there are a lot of non-responsive PDPs and the timeout
     * is large-ish (the default is 20 seconds) it could take a long time to cycle through all of the PDPs.
     * That means that this may not notice a PDP being down in a predictable time.
     */
    private class Heartbeat implements Runnable {
        private PAPEngine papEngine;
        private Set<PDP> pdps = new HashSet<PDP>();
        private int heartbeatInterval;
        private int heartbeatTimeout;

        public volatile boolean isRunning = false;

        public synchronized boolean isRunning() {
            return this.isRunning;
        }

        public synchronized void terminate() {
            this.isRunning = false;
        }

        public Heartbeat(PAPEngine engine) {
            this.papEngine = engine;
            this.heartbeatInterval = Integer.parseInt(XACMLProperties
                .getProperty(XACMLRestProperties.PROP_PAP_HEARTBEAT_INTERVAL, "10000"));
            this.heartbeatTimeout = Integer.parseInt(XACMLProperties
                .getProperty(XACMLRestProperties.PROP_PAP_HEARTBEAT_TIMEOUT, "10000"));
        }

        @Override
        public void run() {
            //
            // Set ourselves as running
            //
            synchronized (this) {
                this.isRunning = true;
            }
            HashMap<String, URL> idToURLMap = new HashMap<String, URL>();
            try {
                while (this.isRunning()) {
                    // Wait the given time
                    Thread.sleep(heartbeatInterval);

                    // get the list of PDPs (may have changed since last time)
                    pdps.clear();
                    synchronized (papEngine) {
                        try {
                            for (PDPGroup g : papEngine.getPDPGroups()) {
                                for (PDP p : g.getPdps()) {
                                    pdps.add(p);
                                }
                            }
                        } catch (PAPException e) {
                            logger
                                .error("Heartbeat unable to read PDPs from PAPEngine: " + e.getMessage(), e);
                        }
                    }
                    //
                    // Check for shutdown
                    //
                    if (!this.isRunning()) {
                        logger.info("isRunning is false, getting out of loop.");
                        break;
                    }

                    // try to get the summary status from each PDP
                    boolean changeSeen = false;
                    for (PDP pdp : pdps) {
                        //
                        // Check for shutdown
                        //
                        if (!this.isRunning()) {
                            logger.info("isRunning is false, getting out of loop.");
                            break;
                        }
                        // the id of the PDP is its url (though we add a query parameter)
                        URL pdpURL = idToURLMap.get(pdp.getId());
                        if (pdpURL == null) {
                            // haven't seen this PDP before
                            String fullURLString = null;
                            try {
                                fullURLString = pdp.getId() + "?type=hb";
                                pdpURL = new URL(fullURLString);
                                idToURLMap.put(pdp.getId(), pdpURL);
                            } catch (MalformedURLException e) {
                                logger.error("PDP id '" + fullURLString + "' is not a valid URL: " + e, e);
                                continue;
                            }
                        }

                        // Do a GET with type HeartBeat
                        String newStatus = "";

                        HttpURLConnection connection = null;
                        try {

                            //
                            // Open up the connection
                            //
                            connection = (HttpURLConnection)pdpURL.openConnection();
                            //
                            // Setup our method and headers
                            //
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(heartbeatTimeout);
                            //
                            // Do the connect
                            //
                            connection.connect();
                            if (connection.getResponseCode() == 204) {
                                newStatus = connection
                                    .getHeaderField(XACMLRestProperties.PROP_PDP_HTTP_HEADER_HB);
                                if (logger.isDebugEnabled()) {
                                    logger
                                        .debug("Heartbeat '" + pdp.getId() + "' status='" + newStatus + "'");
                                }
                            } else {
                                // anything else is an unexpected result
                                newStatus = PDPStatus.Status.UNKNOWN.toString();
                                logger.error("Heartbeat connect response code "
                                             + connection.getResponseCode() + ": " + pdp.getId());
                            }
                        } catch (UnknownHostException e) {
                            newStatus = PDPStatus.Status.NO_SUCH_HOST.toString();
                            logger.error("Heartbeat '" + pdp.getId() + "' NO_SUCH_HOST");
                        } catch (SocketTimeoutException e) {
                            newStatus = PDPStatus.Status.CANNOT_CONNECT.toString();
                            logger.error("Heartbeat '" + pdp.getId() + "' connection timeout: " + e);
                        } catch (ConnectException e) {
                            newStatus = PDPStatus.Status.CANNOT_CONNECT.toString();
                            logger.error("Heartbeat '" + pdp.getId() + "' cannot connect: " + e);
                        } catch (Exception e) {
                            newStatus = PDPStatus.Status.UNKNOWN.toString();
                            logger.error("Heartbeat '" + pdp.getId() + "' connect exception: " + e, e);
                        } finally {
                            // cleanup the connection
                            connection.disconnect();
                        }

                        if (!pdp.getStatus().getStatus().toString().equals(newStatus)) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("previous status='" + pdp.getStatus().getStatus()
                                             + "'  new Status='" + newStatus + "'");
                            }
                            try {
                                setPDPSummaryStatus(pdp, newStatus);
                            } catch (PAPException e) {
                                logger.error("Unable to set state for PDP '" + pdp.getId() + "': " + e, e);
                            }
                            changeSeen = true;
                        }

                    }
                    //
                    // Check for shutdown
                    //
                    if (!this.isRunning()) {
                        logger.info("isRunning is false, getting out of loop.");
                        break;
                    }

                    // if any of the PDPs changed state, tell the ACs to update
                    if (changeSeen) {
                        notifyAC();
                    }

                }
            } catch (InterruptedException e) {
                logger.error("Heartbeat interrupted.  Shutting down");
                this.terminate();
            }
        }
    }

    //
    // HELPER to change Group status when PDP status is changed
    //
    // (Must NOT be called from a method that is synchronized on the papEngine or it may deadlock)
    //

    private void setPDPSummaryStatus(PDP pdp, PDPStatus.Status newStatus) throws PAPException {
        setPDPSummaryStatus(pdp, newStatus.toString());
    }

    private void setPDPSummaryStatus(PDP pdp, String newStatus) throws PAPException {
        synchronized (papEngine) {
            StdPDPStatus status = (StdPDPStatus)pdp.getStatus();
            status.setStatus(PDPStatus.Status.valueOf(newStatus));
            ((StdPDP)pdp).setStatus(status);

            // now adjust the group
            StdPDPGroup group = (StdPDPGroup)papEngine.getPDPGroup(pdp);
            // if the PDP was just deleted it may transiently exist but not be in a group
            if (group != null) {
                group.resetStatus();
            }
        }
    }

    //
    // Callback methods telling this servlet to notify PDPs of changes made by the PAP StdEngine
    // in the PDP group directories
    //

    @Override
    public void changed() {
        // all PDPs in all groups need to be updated/sync'd
        Set<PDPGroup> groups;
        try {
            groups = papEngine.getPDPGroups();
        } catch (PAPException e) {
            logger.error("getPDPGroups failed: " + e.getLocalizedMessage());
            throw new RuntimeException("Unable to get Groups: " + e);
        }
        for (PDPGroup group : groups) {
            groupChanged(group);
        }
    }

    @Override
    public void groupChanged(PDPGroup group) {
        // all PDPs within one group need to be updated/sync'd
        for (PDP pdp : group.getPdps()) {
            pdpChanged(pdp);
        }
    }

    @Override
    public void pdpChanged(PDP pdp) {
        // kick off a thread to do an event notification for each PDP.
        // This needs to be on a separate thread so that PDPs that do not respond (down, non-existent, etc)
        // do not block the PSP response to the AC, which would freeze the GUI until all PDPs sequentially
        // respond or time-out.
        Thread t = new Thread(new UpdatePDPThread(pdp));
        t.start();
    }

    private class UpdatePDPThread implements Runnable {
        private PDP pdp;

        // remember which PDP to notify
        public UpdatePDPThread(PDP pdp) {
            this.pdp = pdp;
        }

        @Override
        public void run() {
            // send the current configuration to one PDP
            HttpURLConnection connection = null;
            try {

                //
                // the Id of the PDP is its URL
                //
                if (logger.isDebugEnabled()) {
                    logger.debug("creating url for id '" + pdp.getId() + "'");
                }
                // TODO - currently always send both policies and pips. Do we care enough to add code to allow
                // sending just one or the other?
                // TODO (need to change "cache=", implying getting some input saying which to change)
                URL url = new URL(pdp.getId() + "?cache=all");

                //
                // Open up the connection
                //
                connection = (HttpURLConnection)url.openConnection();
                //
                // Setup our method and headers
                //
                connection.setRequestMethod("PUT");
                // connection.setRequestProperty("Accept", "text/x-java-properties");
                connection.setRequestProperty("Content-Type", "text/x-java-properties");
                // connection.setUseCaches(false);
                //
                // Adding this in. It seems the HttpUrlConnection class does NOT
                // properly forward our headers for POST re-direction. It does so
                // for a GET re-direction.
                //
                // So we need to handle this ourselves.
                //
                // TODO - is this needed for a PUT? seems better to leave in for now?
                // connection.setInstanceFollowRedirects(false);
                //
                // PLD - MUST be able to handle re-directs.
                //
                connection.setInstanceFollowRedirects(true);
                connection.setDoOutput(true);
                // connection.setDoInput(true);
                try (OutputStream os = connection.getOutputStream()) {

                    PDPGroup group = papEngine.getPDPGroup(pdp);
                    // if the PDP was just deleted, there is no group, but we want to send an update anyway
                    if (group == null) {
                        // create blank properties files
                        Properties policyProperties = new Properties();
                        policyProperties.put(XACMLProperties.PROP_ROOTPOLICIES, "");
                        policyProperties.put(XACMLProperties.PROP_REFERENCEDPOLICIES, "");
                        policyProperties.store(os, "");

                        Properties pipProps = new Properties();
                        pipProps.setProperty(XACMLProperties.PROP_PIP_ENGINES, "");
                        pipProps.store(os, "");

                    } else {
                        // send properties from the current group
                        group.getPolicyProperties().store(os, "");
                        Properties policyLocations = new Properties();
                        for (PDPPolicy policy : group.getPolicies()) {
                            policyLocations.put(policy.getId() + ".url", XACMLPapServlet.papURL + "?id="
                                                                         + policy.getId());
                        }
                        policyLocations.store(os, "");
                        group.getPipConfigProperties().store(os, "");
                    }

                } catch (Exception e) {
                    logger.error("Failed to send property file to " + pdp.getId(), e);
                    // Since this is a server-side error, it probably does not reflect a problem on the
                    // client,
                    // so do not change the PDP status.
                    return;
                }
                //
                // Do the connect
                //
                connection.connect();
                if (connection.getResponseCode() == 204) {
                    logger.info("Success. We are configured correctly.");
                    setPDPSummaryStatus(pdp, PDPStatus.Status.UP_TO_DATE);
                } else if (connection.getResponseCode() == 200) {
                    logger.info("Success. PDP needs to update its configuration.");
                    setPDPSummaryStatus(pdp, PDPStatus.Status.OUT_OF_SYNCH);
                } else {
                    logger.warn("Failed: " + connection.getResponseCode() + "  message: "
                                + connection.getResponseMessage());
                    setPDPSummaryStatus(pdp, PDPStatus.Status.UNKNOWN);
                }
            } catch (Exception e) {
                logger.error("Unable to sync config with PDP '" + pdp.getId() + "': " + e, e);
                try {
                    setPDPSummaryStatus(pdp, PDPStatus.Status.UNKNOWN);
                } catch (PAPException e1) {
                    logger.error("Unable to set status of PDP '" + pdp.getId() + "' to UNKNOWN: " + e, e);
                }
            } finally {
                // cleanup the connection
                connection.disconnect();

                // tell the AC to update it's status info
                notifyAC();
            }

        }
    }

    //
    // RESTful Interface from PAP to ACs notifying them of changes
    //

    private void notifyAC() {
        // kick off a thread to do one event notification for all registered ACs
        // This needs to be on a separate thread so that ACs can make calls back to PAP to get the updated
        // Group data
        // as part of processing this message on their end.
        Thread t = new Thread(new NotifyACThread());
        t.start();
    }

    private class NotifyACThread implements Runnable {

        @Override
        public void run() {
            List<String> disconnectedACs = new ArrayList<String>();
            // logger.debug("LIST SIZE="+adminConsoleURLStringList.size());

            // There should be no Concurrent exception here because the list is a CopyOnWriteArrayList.
            // The "for each" loop uses the collection's iterator under the covers, so it should be correct.
            for (String acURL : adminConsoleURLStringList) {
                HttpURLConnection connection = null;
                try {

                    acURL += "?PAPNotification=true";

                    // TODO - Currently we just tell AC that "Something changed" without being specific. Do we
                    // want to tell it which group/pdp changed?
                    // TODO - If so, put correct parameters into the Query string here
                    acURL += "&objectType=all" + "&action=update";

                    if (logger.isDebugEnabled()) {
                        logger.debug("creating url for id '" + acURL + "'");
                    }
                    // TODO - currently always send both policies and pips. Do we care enough to add code to
                    // allow sending just one or the other?
                    // TODO (need to change "cache=", implying getting some input saying which to change)

                    URL url = new URL(acURL);

                    //
                    // Open up the connection
                    //
                    connection = (HttpURLConnection)url.openConnection();
                    //
                    // Setup our method and headers
                    //
                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Content-Type", "text/x-java-properties");
                    //
                    // Adding this in. It seems the HttpUrlConnection class does NOT
                    // properly forward our headers for POST re-direction. It does so
                    // for a GET re-direction.
                    //
                    // So we need to handle this ourselves.
                    //
                    // TODO - is this needed for a PUT? seems better to leave in for now?
                    connection.setInstanceFollowRedirects(false);
                    //
                    // Do not include any data in the PUT because this is just a
                    // notification to the AC.
                    // The AC will use GETs back to the PAP to get what it needs
                    // to fill in the screens.
                    //

                    //
                    // Do the connect
                    //
                    connection.connect();
                    if (connection.getResponseCode() == 204) {
                        logger.info("Success. We updated correctly.");
                    } else {
                        logger.warn("Failed: " + connection.getResponseCode() + "  message: "
                                    + connection.getResponseMessage());
                    }

                } catch (Exception e) {
                    logger.error("Unable to sync config AC '" + acURL + "': " + e, e);
                    disconnectedACs.add(acURL);
                } finally {
                    // cleanup the connection
                    connection.disconnect();
                }
            }

            // remove any ACs that are no longer connected
            if (disconnectedACs.size() > 0) {
                adminConsoleURLStringList.removeAll(disconnectedACs);
            }

        }
    }

}
