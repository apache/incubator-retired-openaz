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
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.rest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.pap.PAPException;
import org.apache.openaz.xacml.api.pap.PDPStatus;
import org.apache.openaz.xacml.api.pap.PDPStatus.Status;
import org.apache.openaz.xacml.api.pdp.PDPEngine;
import org.apache.openaz.xacml.api.pdp.PDPEngineFactory;
import org.apache.openaz.xacml.api.pip.PIPEngine;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPFinderFactory;
import org.apache.openaz.xacml.pdp.policy.PolicyDef;
import org.apache.openaz.xacml.pdp.policy.dom.DOMPolicyDef;
import org.apache.openaz.xacml.pdp.std.StdPolicyFinderFactory;
import org.apache.openaz.xacml.std.pap.StdPDPPIPConfig;
import org.apache.openaz.xacml.std.pap.StdPDPPolicy;
import org.apache.openaz.xacml.std.pap.StdPDPStatus;
import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.XACMLProperties;

import com.google.common.base.Splitter;

/**
 * Does the work for loading policy and PIP configurations sent from the PAP servlet.
 */
public class XACMLPdpLoader {
    private static final Log logger = LogFactory.getLog(XACMLPdpLoader.class);

    public static synchronized PDPEngine loadEngine(StdPDPStatus status, Properties policyProperties,
                                                    Properties pipProperties) {
        logger.info("loadEngine: " + policyProperties + " " + pipProperties);
        //
        // First load our policies
        //
        try {
            //
            // Were we given some properties?
            //
            if (policyProperties == null) {
                //
                // On init we have no incoming configuration, so just
                // Load our current saved configuration
                //
                policyProperties = new Properties();
                try (InputStream is = Files.newInputStream(getPDPPolicyCache())) {
                    policyProperties.load(is);
                }
            }

            //
            // Get our policy cache up-to-date
            //
            // Side effects of this include:
            // - downloading of policies from remote locations, and
            // - creating new "<PolicyId>.file" properties for files existing local
            //
            XACMLPdpLoader.cachePolicies(policyProperties);
            //
            // Validate the policies
            //
            XACMLPdpLoader.validatePolicies(policyProperties, status);
            if (logger.isDebugEnabled()) {
                logger.debug("Status: " + status);
            }
        } catch (Exception e) {
            String error = "Failed to load Policy Cache properties file: " + e.getMessage();
            logger.error(error, e);
            status.addLoadError(error);
            status.setStatus(PDPStatus.Status.LOAD_ERRORS);
        }
        //
        // Load our PIP configuration
        //
        try {
            //
            // Were we given some properties to use?
            //
            if (pipProperties == null) {
                //
                // Load our current saved configuration
                //
                pipProperties = new Properties();
                try (InputStream is = Files.newInputStream(getPIPConfig())) {
                    pipProperties.load(is);
                }
            }
            //
            // Validate our PIP configurations
            //
            XACMLPdpLoader.validatePipConfiguration(pipProperties, status);
            if (logger.isDebugEnabled()) {
                logger.debug("Status: " + status);
            }
        } catch (Exception e) {
            String error = "Failed to load/validate Pip Config properties file: " + e.getMessage();
            logger.error(error, e);
            status.addLoadError(error);
            status.setStatus(PDPStatus.Status.LOAD_ERRORS);
        }
        //
        // Were they validated?
        //
        if (status.getStatus() == Status.LOAD_ERRORS) {
            logger.error("there were load errors");
            return null;
        }
        //
        // Reset our official properties the PDP factory
        // uses to configure the PDP engine.
        //
        XACMLRest.loadXacmlProperties(policyProperties, pipProperties);
        //
        // Dump ALL our properties that we are trying to load
        //
        try {
            logger.info(XACMLProperties.getProperties().toString());
        } catch (IOException e) {
            logger.error("Failed to get XACML Properties", e);
        }
        //
        // Now load the PDP engine
        //
        PDPEngineFactory factory = null;
        PDPEngine engine = null;
        try {
            factory = PDPEngineFactory.newInstance();
            engine = factory.newEngine();
            logger.info("Loaded new PDP engine.");
            status.setStatus(Status.UP_TO_DATE);
        } catch (FactoryException e) {
            String error = "Failed to create new PDP Engine";
            logger.error(error, e);
            status.addLoadError(error);
        }
        return engine;
    }

    public static synchronized void validatePolicies(Properties properties, StdPDPStatus status)
        throws PAPException {
        Set<String> rootPolicies = XACMLProperties.getRootPolicyIDs(properties);
        Set<String> refPolicies = XACMLProperties.getReferencedPolicyIDs(properties);

        for (String id : rootPolicies) {
            loadPolicy(properties, status, id, true);
        }
        // remember which policies were root policies
        status.addAllLoadedRootPolicies(status.getLoadedPolicies());

        for (String id : refPolicies) {
            loadPolicy(properties, status, id, false);
        }

        logger.info("Loaded " + status.getLoadedPolicies().size() + " policies, failed to load "
                    + status.getFailedPolicies().size() + " policies, "
                    + status.getLoadedRootPolicies().size() + " root policies");
        if (status.getLoadedRootPolicies().size() == 0) {
            logger.warn("NO ROOT POLICIES LOADED!!!  Cannot serve PEP Requests.");
            status.addLoadWarning("NO ROOT POLICIES LOADED!!!  Cannot serve PEP Requests.");
        }
    }

    public static synchronized void loadPolicy(Properties properties, StdPDPStatus status, String id,
                                               boolean isRoot) throws PAPException {
        PolicyDef policy = null;
        String location = null;
        URI locationURI = null;
        boolean isFile = false;
        try {
            location = properties.getProperty(id + ".file");
            if (location == null) {
                location = properties.getProperty(id + ".url");
                if (location != null) {
                    //
                    // Construct the URL
                    //
                    locationURI = URI.create(location);
                    URL url = locationURI.toURL();
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.setRequestProperty(XACMLRestProperties.PROP_PDP_HTTP_HEADER_ID,
                                                     XACMLProperties
                                                         .getProperty(XACMLRestProperties.PROP_PDP_ID));
                    //
                    // Now construct the output file name
                    //
                    Path outFile = Paths.get(getPDPConfig().toAbsolutePath().toString(), id);
                    //
                    // Copy it to disk
                    //
                    try (FileOutputStream fos = new FileOutputStream(outFile.toFile())) {
                        IOUtils.copy(urlConnection.getInputStream(), fos);
                    }
                    //
                    // Now try to load
                    //
                    isFile = true;
                    try (InputStream fis = Files.newInputStream(outFile)) {
                        policy = DOMPolicyDef.load(fis);
                    }
                    //
                    // Save it
                    //
                    properties.setProperty(id + ".file", outFile.toAbsolutePath().toString());
                }
            } else {
                isFile = true;
                locationURI = Paths.get(location).toUri();
                try (InputStream is = Files.newInputStream(Paths.get(location))) {
                    policy = DOMPolicyDef.load(is);
                }
            }
            if (policy != null) {
                status.addLoadedPolicy(new StdPDPPolicy(id, isRoot, locationURI, properties));
                logger.info("Loaded policy: " + policy.getIdentifier() + " version: "
                            + policy.getVersion().stringValue());
            } else {
                String error = "Failed to load policy " + location;
                logger.error(error);
                status.setStatus(PDPStatus.Status.LOAD_ERRORS);
                status.addLoadError(error);
                status.addFailedPolicy(new StdPDPPolicy(id, isRoot));
            }
        } catch (Exception e) {
            logger.error("Failed to load policy '" + id + "' from location '" + location + "'", e);
            status.setStatus(PDPStatus.Status.LOAD_ERRORS);
            status.addFailedPolicy(new StdPDPPolicy(id, isRoot));
            //
            // Is it a file?
            //
            if (isFile) {
                //
                // Let's remove it
                //
                try {
                    logger.error("Corrupted policy file, deleting: " + location);
                    Files.delete(Paths.get(location));
                } catch (IOException e1) {
                    logger.error(e1);
                }
            }
            throw new PAPException("Failed to load policy '" + id + "' from location '" + location + "'");
        }
    }

    public static synchronized void validatePipConfiguration(Properties properties, StdPDPStatus status)
        throws PAPException {
        try {
            PIPFinderFactory factory = PIPFinderFactory.newInstance(properties);
            if (factory == null) {
                throw new FactoryException("Could not create PIP Finder Factory: "
                                           + properties.getProperty(XACMLProperties.PROP_PIPFINDERFACTORY));
            }
            PIPFinder finder = factory.getFinder(properties);
            //
            // Check for this, although it should always return something
            //
            if (finder == null) {
                logger.error("pip finder factory returned a null engine.");
                throw new PIPException("Could not create PIP Finder");
            } else {
                logger.info("Loaded PIP finder");
            }
            for (PIPEngine engine : finder.getPIPEngines()) {
                logger.info("Configured PIP Engine: " + engine.getName());
                StdPDPPIPConfig config = new StdPDPPIPConfig();
                config.setName(engine.getName());
                status.addLoadedPipConfig(config);
            }
        } catch (FactoryException | PIPException e) {
            logger.error("validate PIP configuration failed: " + e.getLocalizedMessage());
            status.addLoadError(e.getLocalizedMessage());
            status.setStatus(Status.LOAD_ERRORS);
            throw new PAPException(e);
        }
    }

    /**
     * Iterates the policies defined in the props object to ensure they are loaded locally. Policies are
     * searched for in the following order: - see if the current properties has a "&lt;PolicyID&gt;.file"
     * entry and that file exists in the local directory - if not, see if the file exists in the local
     * directory; if so create a ".file" property for it. - if not, get the "&lt;PolicyID&gt;.url" property
     * and try to GET the policy from that location (and set the ".file" property) If the ".file" property is
     * created, then true is returned to tell the caller that the props object changed.
     *
     * @param props
     * @return true/false if anything was changed in the props object
     * @throws PAPException
     */
    public static synchronized boolean cachePolicies(Properties props) throws PAPException {
        boolean changed = false;
        String[] lists = new String[2];
        lists[0] = props.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
        lists[1] = props.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
        for (String list : lists) {
            //
            // Check for a null or empty parameter
            //
            if (list == null || list.length() == 0) {
                continue;
            }
            Iterable<String> policies = Splitter.on(',').trimResults().omitEmptyStrings().split(list);
            for (String policy : policies) {
                boolean policyExists = false;

                // First look for ".file" property and verify the file exists
                String propLocation = props.getProperty(policy + StdPolicyFinderFactory.PROP_FILE);
                if (propLocation != null) {
                    //
                    // Does it exist?
                    //
                    policyExists = Files.exists(Paths.get(propLocation));
                    if (!policyExists) {
                        logger.warn("Policy file " + policy + " expected at " + propLocation
                                    + " does NOT exist.");
                    }
                }

                // If ".file" property does not exist, try looking for the local file anyway
                // (it might exist without having a ".file" property set for it)
                if (!policyExists) {
                    //
                    // Now construct the output file name
                    //
                    Path outFile = Paths.get(getPDPConfig().toAbsolutePath().toString(), policy);
                    //
                    // Double check to see if we pulled it at some point
                    //
                    policyExists = Files.exists(outFile);
                    if (policyExists) {
                        //
                        // Set the property so the PDP engine doesn't have
                        // to pull it from the URL but rather the FILE.
                        //
                        logger.info("Policy does exist: " + outFile.toAbsolutePath().toString());
                        props.setProperty(policy + StdPolicyFinderFactory.PROP_FILE, outFile.toAbsolutePath()
                            .toString());
                        //
                        // Indicate that there were changes made to the properties
                        //
                        changed = true;
                    } else {

                        // File does not exist locally, so we need to get it from the location given in the
                        // ".url" property (which MUST exist)

                        //
                        // There better be a URL to retrieve it
                        //
                        propLocation = props.getProperty(policy + StdPolicyFinderFactory.PROP_URL);
                        if (propLocation != null) {
                            //
                            // Get it
                            //
                            URL url = null;
                            try {
                                //
                                // Create the URL
                                //
                                url = new URL(propLocation);
                                logger.info("Pulling " + url.toString());
                                //
                                // Open the connection
                                //
                                URLConnection urlConnection = url.openConnection();
                                urlConnection
                                    .setRequestProperty(XACMLRestProperties.PROP_PDP_HTTP_HEADER_ID,
                                                        XACMLProperties
                                                            .getProperty(XACMLRestProperties.PROP_PDP_ID));
                                //
                                // Copy it to disk
                                //
                                try (InputStream is = urlConnection.getInputStream();
                                    OutputStream os = new FileOutputStream(outFile.toFile())) {
                                    IOUtils.copy(is, os);
                                }
                                //
                                // Now save it in the properties as a .file
                                //
                                logger.info("Pulled policy: " + outFile.toAbsolutePath().toString());
                                props.setProperty(policy + StdPolicyFinderFactory.PROP_FILE, outFile
                                    .toAbsolutePath().toString());
                                //
                                // Indicate that there were changes made to the properties
                                //
                                changed = true;
                            } catch (Exception e) {
                                if (e instanceof MalformedURLException) {
                                    logger.error("Policy '" + policy
                                                 + "' had bad URL in new configuration, URL='" + propLocation
                                                 + "'");
                                } else {
                                    logger.error("Error while retrieving policy " + policy + " from URL "
                                                 + url.toString() + ", e=" + e);
                                }
                            }
                        } else {
                            logger.error("Policy " + policy + " does NOT exist and does NOT have a URL");
                        }
                    }
                }
            }
        }
        return changed;
    }

    public static synchronized Path getPDPPolicyCache() throws PAPException {
        Path config = getPDPConfig();
        Path policyProperties = Paths.get(config.toAbsolutePath().toString(), "xacml.policy.properties");
        if (Files.notExists(policyProperties)) {
            logger.warn(policyProperties.toAbsolutePath().toString() + " does NOT exist.");
            //
            // Try to create the file
            //
            try {
                Files.createFile(policyProperties);
            } catch (IOException e) {
                logger.error("Failed to create policy properties file: "
                             + policyProperties.toAbsolutePath().toString());
                throw new PAPException("Failed to create policy properties file: "
                                       + policyProperties.toAbsolutePath().toString());
            }
        }
        return policyProperties;
    }

    public static synchronized Path getPIPConfig() throws PAPException {
        Path config = getPDPConfig();
        Path pipConfigProperties = Paths.get(config.toAbsolutePath().toString(), "xacml.pip.properties");
        if (Files.notExists(pipConfigProperties)) {
            logger.warn(pipConfigProperties.toAbsolutePath().toString() + " does NOT exist.");
            //
            // Try to create the file
            //
            try {
                Files.createFile(pipConfigProperties);
            } catch (IOException e) {
                logger.error("Failed to create pip properties file: "
                             + pipConfigProperties.toAbsolutePath().toString());
                throw new PAPException("Failed to create pip properties file: "
                                       + pipConfigProperties.toAbsolutePath().toString());
            }
        }
        return pipConfigProperties;
    }

    public static synchronized Path getPDPConfig() throws PAPException {
        Path config = Paths.get(XACMLProperties.getProperty(XACMLRestProperties.PROP_PDP_CONFIG));
        if (Files.notExists(config)) {
            logger.warn(config.toAbsolutePath().toString() + " does NOT exist.");
            //
            // Try to create the directory
            //
            try {
                Files.createDirectories(config);
            } catch (IOException e) {
                logger.error("Failed to create config directory: " + config.toAbsolutePath().toString(), e);
                throw new PAPException("Failed to create config directory: "
                                       + config.toAbsolutePath().toString());
            }
        }
        return config;
    }

}
