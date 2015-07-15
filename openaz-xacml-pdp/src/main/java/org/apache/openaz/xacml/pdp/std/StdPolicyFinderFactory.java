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
package org.apache.openaz.xacml.pdp.std;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.pdp.policy.CombiningAlgorithm;
import org.apache.openaz.xacml.pdp.policy.CombiningAlgorithmFactory;
import org.apache.openaz.xacml.pdp.policy.Policy;
import org.apache.openaz.xacml.pdp.policy.PolicyDef;
import org.apache.openaz.xacml.pdp.policy.PolicyFinder;
import org.apache.openaz.xacml.pdp.policy.PolicyFinderFactory;
import org.apache.openaz.xacml.pdp.policy.PolicySet;
import org.apache.openaz.xacml.pdp.policy.PolicySetChild;
import org.apache.openaz.xacml.pdp.policy.Target;
import org.apache.openaz.xacml.pdp.policy.dom.DOMPolicyDef;
import org.apache.openaz.xacml.pdp.util.OpenAZPDPProperties;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.StdVersion;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.XACMLProperties;

import com.google.common.base.Splitter;

/**
 * StdPolicyFinderFactory extends {@link org.apache.openaz.xacml.pdp.policy.PolicyFinderFactory} with the
 * <code>getPolicyFinder</code> method to get a single instance of the {@link StdPolicyFinder}. The root
 * {@link org.apache.openaz.xacml.pdp.policy.PolicyDef} is loaded from a file whose name is specified as a
 * system property or in the $java.home/lib/xacml.properties property set.
 */
public class StdPolicyFinderFactory extends PolicyFinderFactory {
    public static final String PROP_FILE = ".file";
    public static final String PROP_URL = ".url";

    private Log logger = LogFactory.getLog(this.getClass());
    private List<PolicyDef> rootPolicies;
    private List<PolicyDef> referencedPolicies;
    private boolean needsInit = true;

    /**
     * Loads the <code>PolicyDef</code> for the given <code>String</code> identifier by looking first for a
     * ".file" property associated with the ID and using that to load from a <code>File</code> and looking for
     * a ".url" property associated with the ID and using that to load from a <code>URL</code>.
     *
     * @param policyId the <code>String</code> identifier for the policy
     * @return a <code>PolicyDef</code> loaded from the given identifier
     */
    protected PolicyDef loadPolicyDef(String policyId, Properties properties) {
        String propLocation = properties.getProperty(policyId + PROP_FILE);
        if (propLocation != null) {
            File fileLocation = new File(propLocation);
            if (!fileLocation.exists()) {
                this.logger.error("Policy file " + fileLocation.getAbsolutePath() + " does not exist.");
            } else if (!fileLocation.canRead()) {
                this.logger.error("Policy file " + fileLocation.getAbsolutePath() + " cannot be read.");
            } else {
                try {
                    this.logger.info("Loading policy file " + fileLocation);
                    PolicyDef policyDef = DOMPolicyDef.load(fileLocation);
                    if (policyDef != null) {
                        return policyDef;
                    }
                } catch (DOMStructureException ex) {
                    this.logger.error("Error loading policy file " + fileLocation.getAbsolutePath() + ": "
                                      + ex.getMessage(), ex);
                    return new Policy(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
                }
            }
        }

        if ((propLocation = properties.getProperty(policyId + PROP_URL)) != null) {
            URLConnection urlConnection = null;
            try {
                URL url = new URL(propLocation);
                urlConnection = url.openConnection();
                this.logger.info("Loading policy file " + url.toString());
            } catch (MalformedURLException ex) {
                this.logger.error("Invalid URL " + propLocation + ": " + ex.getMessage(), ex);
            } catch (IOException ex) {
                this.logger.error("IOException opening URL " + propLocation + ": " + ex.getMessage(), ex);
            }
            
            if (urlConnection != null) {
                try (InputStream is = urlConnection.getInputStream()) {
                    PolicyDef policyDef = DOMPolicyDef.load(is);
                    if (policyDef != null) {
                        return policyDef;
                    }
                } catch (IOException ex) {
                    this.logger.error("IOException opening URL " + propLocation + ": " + ex.getMessage(), ex);
                } catch (DOMStructureException ex) {
                    this.logger.error("Invalid Policy " + propLocation + ": " + ex.getMessage(), ex);
                    return new Policy(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
                }
            }
        }

        this.logger.error("No known location for Policy " + policyId);
        return null;
    }

    /**
     * Finds the identifiers for all of the policies referenced by the given property name in the
     * <code>XACMLProperties</code> and loads them using the requested loading method.
     *
     * @param propertyName the <code>String</code> name of the property containing the list of policy
     *            identifiers
     * @return a <code>List</code> of <code>PolicyDef</code>s loaded from the given property name
     */
    protected List<PolicyDef> getPolicyDefs(String propertyName, Properties properties) {
        String policyIds = properties.getProperty(propertyName);
        if (policyIds == null || policyIds.length() == 0) {
            return null;
        }

        Iterable<String> policyIdArray = Splitter.on(',').trimResults().omitEmptyStrings().split(policyIds);
        if (policyIdArray == null) {
            return null;
        }

        List<PolicyDef> listPolicyDefs = new ArrayList<PolicyDef>();
        for (String policyId : policyIdArray) {
            PolicyDef policyDef = this.loadPolicyDef(policyId, properties);
            if (policyDef != null) {
                listPolicyDefs.add(policyDef);
            }
        }
        return listPolicyDefs;
    }

    protected synchronized void init(Properties properties) {
        if (this.needsInit) {
            //
            // Check for property that combines root policies into one policyset
            //
            String combiningAlgorithm = properties
                .getProperty(OpenAZPDPProperties.PROP_POLICYFINDERFACTORY_COMBINEROOTPOLICIES);
            if (combiningAlgorithm != null) {
                try {
                    logger.info("Combining root policies with " + combiningAlgorithm);
                    //
                    // Find the combining algorithm
                    //
                    CombiningAlgorithm<PolicySetChild> algorithm = CombiningAlgorithmFactory.newInstance()
                        .getPolicyCombiningAlgorithm(new IdentifierImpl(combiningAlgorithm));
                    //
                    // Create our root policy
                    //
                    PolicySet root = new PolicySet();
                    root.setIdentifier(new IdentifierImpl(UUID.randomUUID().toString()));
                    root.setVersion(StdVersion.newInstance("1.0"));
                    root.setTarget(new Target());
                    //
                    // Set the algorithm
                    //
                    root.setPolicyCombiningAlgorithm(algorithm);
                    //
                    // Load all our root policies
                    //
                    for (PolicyDef policy : this.getPolicyDefs(XACMLProperties.PROP_ROOTPOLICIES, properties)) {
                        root.addChild(policy);
                    }
                    //
                    // Set this policy as the root
                    //
                    this.rootPolicies = new ArrayList<>();
                    this.rootPolicies.add(root);
                } catch (FactoryException | ParseException e) {
                    logger.error("Failed to load Combining Algorithm Factory: " + e.getLocalizedMessage());
                }
            } else {
                this.rootPolicies = this.getPolicyDefs(XACMLProperties.PROP_ROOTPOLICIES, properties);
            }

            this.referencedPolicies = this.getPolicyDefs(XACMLProperties.PROP_REFERENCEDPOLICIES, properties);
            this.needsInit = false;
        }
    }

    public StdPolicyFinderFactory() {
    }

    @Override
    public PolicyFinder getPolicyFinder() throws FactoryException {
        try {
            this.init(XACMLProperties.getProperties());
        } catch (IOException e) {
            throw new FactoryException(e);
        }
        return new StdPolicyFinder(this.rootPolicies, this.referencedPolicies);
    }

    @Override
    public PolicyFinder getPolicyFinder(Properties properties) throws FactoryException {
        this.init(properties);
        return new StdPolicyFinder(this.rootPolicies, this.referencedPolicies);
    }
}
