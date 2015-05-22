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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
import org.apache.http.entity.ContentType;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.Response;
import org.apache.openaz.xacml.api.pap.PDPStatus.Status;
import org.apache.openaz.xacml.api.pdp.PDPEngine;
import org.apache.openaz.xacml.api.pdp.PDPException;
import org.apache.openaz.xacml.std.dom.DOMRequest;
import org.apache.openaz.xacml.std.dom.DOMResponse;
import org.apache.openaz.xacml.std.json.JSONRequest;
import org.apache.openaz.xacml.std.json.JSONResponse;
import org.apache.openaz.xacml.std.pap.StdPDPStatus;
import org.apache.openaz.xacml.util.XACMLProperties;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class XacmlPdpServlet This is an implementation of the XACML 3.0 RESTful Interface
 * with added features to support simple PAP RESTful API for policy publishing and PIP configuration changes.
 * If you are running this the first time, then we recommend you look at the xacml.pdp.properties file. This
 * properties file has all the default parameter settings. If you are running the servlet as is, then we
 * recommend setting up you're container to run it on port 8080 with context "/pdp". Wherever the default
 * working directory is set to, a "config" directory will be created that holds the policy and pip cache. This
 * setting is located in the xacml.pdp.properties file. When you are ready to customize, you can create a
 * separate xacml.pdp.properties on you're local file system and setup the parameters as you wish. Just set
 * the Java VM System variable to point to that file:
 * -Dxacml.properties=/opt/app/xacml/etc/xacml.pdp.properties Or if you only want to change one or two
 * properties, simply set the Java VM System variable for that property. -Dxacml.rest.pdp.register=false
 */
@WebServlet(description = "Implements the XACML PDP RESTful API and client PAP API.", urlPatterns = {
    "/"
}, loadOnStartup = 1, initParams = {
                          @WebInitParam(name = "XACML_PROPERTIES_NAME", value = "xacml.pdp.properties", description = "The location of the PDP xacml.pdp.properties file holding configuration information.")
            })
public class XACMLPdpServlet extends HttpServlet implements Runnable {
    private static final long serialVersionUID = 1L;
    //
    // Our application debug log
    //
    private static final Log logger = LogFactory.getLog(XACMLPdpServlet.class);
    //
    // This logger is specifically only for Xacml requests and their corresponding response.
    // It's output ideally should be sent to a separate file from the application logger.
    //
    private static final Log requestLogger = LogFactory.getLog("xacml.request");
    //
    // This thread may getting invoked on startup, to let the PAP know
    // that we are up and running.
    //
    private Thread registerThread = null;
    private XACMLPdpRegisterThread registerRunnable = null;
    //
    // This is our PDP engine pointer. There is a synchronized lock used
    // for access to the pointer. In case we are servicing PEP requests while
    // an update is occurring from the PAP.
    //
    private PDPEngine pdpEngine = null;
    private static final Object pdpEngineLock = new Object();
    //
    // This is our PDP's status. What policies are loaded (or not) and
    // what PIP configurations are loaded (or not).
    // There is a synchronized lock used for access to the object.
    //
    private static volatile StdPDPStatus status = new StdPDPStatus();
    private static final Object pdpStatusLock = new Object();

    //
    // Queue of PUT requests
    //
    public static class PutRequest {
        public Properties policyProperties = null;
        public Properties pipConfigProperties = null;

        PutRequest(Properties policies, Properties pips) {
            this.policyProperties = policies;
            this.pipConfigProperties = pips;
        }
    }

    public static volatile BlockingQueue<PutRequest> queue = new LinkedBlockingQueue<PutRequest>(2);
    //
    // This is our configuration thread that attempts to load
    // a new configuration request.
    //
    private Thread configThread = null;
    private volatile boolean configThreadTerminate = false;

    /**
     * Default constructor.
     */
    public XACMLPdpServlet() {
    }

    /**
     * @see Servlet#init(ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        //
        // Initialize
        //
        XACMLRest.xacmlInit(config);
        //
        // Load our engine - this will use the latest configuration
        // that was saved to disk and set our initial status object.
        //
        PDPEngine engine = XACMLPdpLoader.loadEngine(XACMLPdpServlet.status, null, null);
        if (engine != null) {
            synchronized (pdpEngineLock) {
                pdpEngine = engine;
            }
        }
        //
        // Kick off our thread to register with the PAP servlet.
        //
        if (Boolean.parseBoolean(XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_REGISTER))) {
            this.registerRunnable = new XACMLPdpRegisterThread();
            this.registerThread = new Thread(this.registerRunnable);
            this.registerThread.start();
        }
        //
        // This is our thread that manages incoming configuration
        // changes.
        //
        this.configThread = new Thread(this);
        this.configThread.start();
    }

    /**
     * @see Servlet#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        logger.info("Destroying....");
        //
        // Make sure the register thread is not running
        //
        if (this.registerRunnable != null) {
            try {
                this.registerRunnable.terminate();
                if (this.registerThread != null) {
                    this.registerThread.interrupt();
                    this.registerThread.join();
                }
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
        //
        // Make sure the configure thread is not running
        //
        this.configThreadTerminate = true;
        try {
            this.configThread.interrupt();
            this.configThread.join();
        } catch (InterruptedException e) {
            logger.error(e);
        }
        logger.info("Destroyed.");
    }

    /**
     * PUT - The PAP engine sends configuration information using HTTP PUT request. One parameter is expected:
     * config=[policy|pip|all] policy - Expect a properties file that contains updated lists of the root and
     * referenced policies that the PDP should be using for PEP requests. Specifically should AT LEAST contain
     * the following properties: xacml.rootPolicies xacml.referencedPolicies In addition, any relevant
     * information needed by the PDP to load or retrieve the policies to store in its cache. EXAMPLE:
     * xacml.rootPolicies=PolicyA.1, PolicyB.1
     * PolicyA.1.url=http://localhost:9090/PAP?id=b2d7b86d-d8f1-4adf-ba9d-b68b2a90bee1&version=1
     * PolicyB.1.url=http://localhost:9090/PAP/id=be962404-27f6-41d8-9521-5acb7f0238be&version=1
     * xacml.referencedPolicies=RefPolicyC.1, RefPolicyD.1
     * RefPolicyC.1.url=http://localhost:9090/PAP?id=foobar&version=1
     * RefPolicyD.1.url=http://localhost:9090/PAP/id=example&version=1 pip - Expect a properties file that
     * contain PIP engine configuration properties. Specifically should AT LEAST the following property:
     * xacml.pip.engines In addition, any relevant information needed by the PDP to load and configure the
     * PIPs. EXAMPLE: xacml.pip.engines=foo,bar foo.classname=com.foo foo.sample=abc foo.example=xyz ......
     * bar.classname=com.bar ...... all - Expect ALL new configuration properties for the PDP
     *
     * @see HttpServlet#doPut(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {
        //
        // Dump our request out
        //
        if (logger.isDebugEnabled()) {
            XACMLRest.dumpRequest(request);
        }
        //
        // What is being PUT?
        //
        String cache = request.getParameter("cache");
        //
        // Should be a list of policy and pip configurations in Java properties format
        //
        if (cache != null && request.getContentType().equals("text/x-java-properties")) {
            if (request.getContentLength() > Integer.parseInt(XACMLProperties
                .getProperty("MAX_CONTENT_LENGTH", "32767"))) {
                String message = "Content-Length larger than server will accept.";
                logger.info(message);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
                return;
            }
            this.doPutConfig(cache, request, response);
        } else {
            String message = "Invalid cache: '" + cache + "' or content-type: '" + request.getContentType()
                             + "'";
            logger.error(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return;
        }
    }

    protected void doPutConfig(String config, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        try {
            // prevent multiple configuration changes from stacking up
            if (XACMLPdpServlet.queue.remainingCapacity() <= 0) {
                logger.error("Queue capacity reached");
                response.sendError(HttpServletResponse.SC_CONFLICT,
                                   "Multiple configuration changes waiting processing.");
                return;
            }
            //
            // Read the properties data into an object.
            //
            Properties newProperties = new Properties();
            newProperties.load(request.getInputStream());
            // should have something in the request
            if (newProperties.size() == 0) {
                logger.error("No properties in PUT");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                   "PUT must contain at least one property");
                return;
            }
            //
            // Which set of properties are they sending us? Whatever they send gets
            // put on the queue (if there is room).
            //
            if (config.equals("policies")) {
                newProperties = XACMLProperties.getPolicyProperties(newProperties, true);
                if (newProperties.size() == 0) {
                    logger.error("No policy properties in PUT");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       "PUT with cache=policies must contain at least one policy property");
                    return;
                }
                XACMLPdpServlet.queue.offer(new PutRequest(newProperties, null));
            } else if (config.equals("pips")) {
                newProperties = XACMLProperties.getPipProperties(newProperties);
                if (newProperties.size() == 0) {
                    logger.error("No pips properties in PUT");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       "PUT with cache=pips must contain at least one pip property");
                    return;
                }
                XACMLPdpServlet.queue.offer(new PutRequest(null, newProperties));
            } else if (config.equals("all")) {
                Properties newPolicyProperties = XACMLProperties.getPolicyProperties(newProperties, true);
                if (newPolicyProperties.size() == 0) {
                    logger.error("No policy properties in PUT");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       "PUT with cache=all must contain at least one policy property");
                    return;
                }
                Properties newPipProperties = XACMLProperties.getPipProperties(newProperties);
                if (newPipProperties.size() == 0) {
                    logger.error("No pips properties in PUT");
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                       "PUT with cache=all must contain at least one pip property");
                    return;
                }
                XACMLPdpServlet.queue.offer(new PutRequest(newPolicyProperties, newPipProperties));
            } else {
                //
                // Invalid value
                //
                logger.error("Invalid config value: " + config);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                   "Config must be one of 'policies', 'pips', 'all'");
                return;
            }
        } catch (Exception e) {
            logger.error("Failed to process new configuration.", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
    }

    /**
     * Parameters: type=hb|config|Status 1. HeartBeat Status HeartBeat OK - All Policies are Loaded, All PIPs
     * are Loaded LOADING_IN_PROGRESS - Currently loading a new policy set/pip configuration
     * LAST_UPDATE_FAILED - Need to track the items that failed during last update LOAD_FAILURE - ??? Need to
     * determine what information is sent and how 2. Configuration 3. Status return the StdPDPStatus object in
     * the Response content
     *
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {
        XACMLRest.dumpRequest(request);
        //
        // What are they requesting?
        //
        boolean returnHB = false;
        response.setHeader("Cache-Control", "no-cache");
        String type = request.getParameter("type");
        // type might be null, so use equals on string constants
        if ("config".equals(type)) {
            response.setContentType("text/x-java-properties");
            try {
                String lists = XACMLProperties.PROP_ROOTPOLICIES + "="
                               + XACMLProperties.getProperty(XACMLProperties.PROP_ROOTPOLICIES, "");
                lists = lists + "\n" + XACMLProperties.PROP_REFERENCEDPOLICIES + "="
                        + XACMLProperties.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES, "") + "\n";
                try (InputStream listInputStream = new ByteArrayInputStream(lists.getBytes());
                    InputStream pipInputStream = Files.newInputStream(XACMLPdpLoader.getPIPConfig());
                    OutputStream os = response.getOutputStream()) {
                    IOUtils.copy(listInputStream, os);
                    IOUtils.copy(pipInputStream, os);
                }
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (Exception e) {
                logger.error("Failed to copy property file", e);
                response.sendError(400, "Failed to copy Property file");
            }

        } else if ("hb".equals(type)) {
            returnHB = true;
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } else if ("Status".equals(type)) {
            // convert response object to JSON and include in the response
            synchronized (pdpStatusLock) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(response.getOutputStream(), status);
            }
            response.setStatus(HttpServletResponse.SC_OK);

        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "type not 'config' or 'hb'");
        }
        if (returnHB) {
            synchronized (pdpStatusLock) {
                response
                    .addHeader(XACMLRestProperties.PROP_PDP_HTTP_HEADER_HB, status.getStatus().toString());
            }
        }
    }

    /**
     * POST - We expect XACML requests to be posted by PEP applications. They can be in the form of XML or
     * JSON according to the XACML 3.0 Specifications for both.
     *
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
        IOException {
        //
        // no point in doing any work if we know from the get-go that we cannot do anything with the request
        //
        if (status.getLoadedRootPolicies().size() == 0) {
            logger.warn("Request from PEP at " + request.getRequestURI()
                        + " for service when PDP has No Root Policies loaded");
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return;
        }

        XACMLRest.dumpRequest(request);
        //
        // Set our no-cache header
        //
        response.setHeader("Cache-Control", "no-cache");
        //
        // They must send a Content-Type
        //
        if (request.getContentType() == null) {
            logger.warn("Must specify a Content-Type");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no content-type given");
            return;
        }
        //
        // Limit the Content-Length to something reasonable
        //
        if (request.getContentLength() > Integer.parseInt(XACMLProperties.getProperty("MAX_CONTENT_LENGTH",
                                                                                      "32767"))) {
            String message = "Content-Length larger than server will accept.";
            logger.info(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return;
        }
        if (request.getContentLength() <= 0) {
            String message = "Content-Length is negative";
            logger.info(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return;
        }
        ContentType contentType = null;
        try {
            contentType = ContentType.parse(request.getContentType());
        } catch (Exception e) {
            String message = "Parsing Content-Type: " + request.getContentType() + ", error="
                             + e.getMessage();
            logger.error(message, e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return;
        }
        //
        // What exactly did they send us?
        //
        String incomingRequestString = null;
        Request pdpRequest = null;
        if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_JSON.getMimeType())
            || contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_XML.getMimeType())
            || contentType.getMimeType().equalsIgnoreCase("application/xacml+xml")) {
            //
            // Read in the string
            //
            StringBuilder buffer = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                incomingRequestString = buffer.toString();
            }
            logger.info(incomingRequestString);
            //
            // Parse into a request
            //
            try {
                if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_JSON.getMimeType())) {
                    pdpRequest = JSONRequest.load(incomingRequestString);
                } else if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_XML
                                                                          .getMimeType())
                           || contentType.getMimeType().equalsIgnoreCase("application/xacml+xml")) {
                    pdpRequest = DOMRequest.load(incomingRequestString);
                }
            } catch (Exception e) {
                logger.error("Could not parse request", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                return;
            }
        } else {
            String message = "unsupported content type" + request.getContentType();
            logger.error(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return;
        }
        //
        // Did we successfully get and parse a request?
        //
        if (pdpRequest == null || pdpRequest.getRequestAttributes() == null
            || pdpRequest.getRequestAttributes().size() <= 0) {
            String message = "Zero Attributes found in the request";
            logger.error(message);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
            return;
        }
        //
        // Run it
        //
        try {
            //
            // Get the pointer to the PDP Engine
            //
            PDPEngine myEngine = null;
            synchronized (pdpEngineLock) {
                myEngine = this.pdpEngine;
            }
            if (myEngine == null) {
                String message = "No engine loaded.";
                logger.error(message);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                return;
            }
            //
            // Send the request and save the response
            //
            long lTimeStart, lTimeEnd;
            Response pdpResponse = null;

            // TODO - Make this unnecessary
            // TODO It seems that the PDP Engine is not thread-safe, so when a configuration change occurs in
            // the middle of processing
            // TODO a PEP Request, that Request fails (it throws a NullPointerException in the decide()
            // method).
            // TODO Using synchronize will slow down processing of PEP requests, possibly by a significant
            // amount.
            // TODO Since configuration changes are rare, it would be A Very Good Thing if we could eliminate
            // this sychronized block.
            // TODO
            // TODO This problem was found by starting one PDP then
            // TODO RestLoadTest switching between 2 configurations, 1 second apart
            // TODO both configurations contain the datarouter policy
            // TODO both configurations already have all policies cached in the PDPs config directory
            // TODO RestLoadTest started with the Datarouter test requests, 5 threads, no interval
            // TODO With that configuration this code (without the synchronized) throws a NullPointerException
            // TODO within a few seconds.
            //
            synchronized (pdpEngineLock) {
                myEngine = this.pdpEngine;
                try {
                    lTimeStart = System.currentTimeMillis();
                    pdpResponse = myEngine.decide(pdpRequest);
                    lTimeEnd = System.currentTimeMillis();
                } catch (PDPException e) {
                    String message = "Exception during decide: " + e.getMessage();
                    logger.error(message);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                    return;
                }
            }
            requestLogger.info(lTimeStart + "=" + incomingRequestString);
            if (logger.isDebugEnabled()) {
                logger.debug("Request time: " + (lTimeEnd - lTimeStart) + "ms");
            }
            //
            // Convert Response to appropriate Content-Type
            //
            if (pdpResponse == null) {
                requestLogger.info(lTimeStart + "=" + "{}");
                throw new Exception("Failed to get response from PDP engine.");
            }
            //
            // Set our content-type
            //
            response.setContentType(contentType.getMimeType());
            //
            // Convert the PDP response object to a String to
            // return to our caller as well as dump to our loggers.
            //
            String outgoingResponseString = "";
            if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_JSON.getMimeType())) {
                //
                // Get it as a String. This is not very efficient but we need to log our
                // results for auditing.
                //
                outgoingResponseString = JSONResponse.toString(pdpResponse, logger.isDebugEnabled());
                if (logger.isDebugEnabled()) {
                    logger.debug(outgoingResponseString);
                    //
                    // Get rid of whitespace
                    //
                    outgoingResponseString = JSONResponse.toString(pdpResponse, false);
                }
            } else if (contentType.getMimeType().equalsIgnoreCase(ContentType.APPLICATION_XML.getMimeType())
                       || contentType.getMimeType().equalsIgnoreCase("application/xacml+xml")) {
                //
                // Get it as a String. This is not very efficient but we need to log our
                // results for auditing.
                //
                outgoingResponseString = DOMResponse.toString(pdpResponse, logger.isDebugEnabled());
                if (logger.isDebugEnabled()) {
                    logger.debug(outgoingResponseString);
                    //
                    // Get rid of whitespace
                    //
                    outgoingResponseString = DOMResponse.toString(pdpResponse, false);
                }
            }
            //
            // lTimeStart is used as an ID within the requestLogger to match up
            // request's with responses.
            //
            requestLogger.info(lTimeStart + "=" + outgoingResponseString);
            response.getWriter().print(outgoingResponseString);
        } catch (Exception e) {
            String message = "Exception executing request: " + e;
            logger.error(message, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void run() {
        //
        // Keep running until we are told to terminate
        //
        try {
            while (!this.configThreadTerminate) {
                PutRequest request = XACMLPdpServlet.queue.take();
                StdPDPStatus newStatus = new StdPDPStatus();

                // TODO - This is related to the problem discussed in the doPost() method about the PDPEngine
                // not being thread-safe.
                // TODO See that discussion, and when the PDPEngine is made thread-safe it should be ok to
                // move the loadEngine out of
                // TODO the synchronized block.
                // TODO However, since configuration changes should be rare we may not care about changing
                // this.
                PDPEngine newEngine = null;
                synchronized (pdpStatusLock) {
                    XACMLPdpServlet.status.setStatus(Status.UPDATING_CONFIGURATION);
                    newEngine = XACMLPdpLoader.loadEngine(newStatus, request.policyProperties,
                                                          request.pipConfigProperties);
                }
                // PDPEngine newEngine = XACMLPdpLoader.loadEngine(newStatus, request.policyProperties,
                // request.pipConfigProperties);
                if (newEngine != null) {
                    synchronized (XACMLPdpServlet.pdpEngineLock) {
                        this.pdpEngine = newEngine;
                        try {
                            logger.info("Saving configuration.");
                            if (request.policyProperties != null) {
                                try (OutputStream os = Files.newOutputStream(XACMLPdpLoader
                                    .getPDPPolicyCache())) {
                                    request.policyProperties.store(os, "");
                                }
                            }
                            if (request.pipConfigProperties != null) {
                                try (OutputStream os = Files.newOutputStream(XACMLPdpLoader.getPIPConfig())) {
                                    request.pipConfigProperties.store(os, "");
                                }
                            }
                            newStatus.setStatus(Status.UP_TO_DATE);

                        } catch (Exception e) {
                            logger.error("Failed to store new properties.");
                            newStatus.setStatus(Status.LOAD_ERRORS);
                            newStatus.addLoadWarning("Unable to save configuration: " + e.getMessage());
                        }
                    }
                } else {
                    newStatus.setStatus(Status.LAST_UPDATE_FAILED);
                }
                synchronized (pdpStatusLock) {
                    XACMLPdpServlet.status.set(newStatus);
                }
            }
        } catch (InterruptedException e) {
            logger.error("interrupted");
        }
    }
}
