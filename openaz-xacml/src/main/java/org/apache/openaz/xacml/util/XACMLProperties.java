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
package org.apache.openaz.xacml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Splitter;

/**
 * XACMLProperties is a wrapper around a <code>Properties</code> object loaded from a standard location for
 * XACML properties.
 */
public class XACMLProperties {
    private static final Log logger = LogFactory.getLog(XACMLProperties.class);

    public static final String XACML_PROPERTIES_NAME = "xacml.properties";
    public static final String XACML_PROPERTIES_FILE = System.getProperty("java.home") + File.separator
                                                       + "lib" + File.separator + XACML_PROPERTIES_NAME;

    public static final String PROP_DATATYPEFACTORY = "xacml.dataTypeFactory";
    public static final String PROP_PDPENGINEFACTORY = "xacml.pdpEngineFactory";
    public static final String PROP_PEPENGINEFACTORY = "xacml.pepEngineFactory";
    public static final String PROP_PIPFINDERFACTORY = "xacml.pipFinderFactory";
    public static final String PROP_TRACEENGINEFACTORY = "xacml.traceEngineFactory";

    public static final String PROP_ROOTPOLICIES = "xacml.rootPolicies";
    public static final String PROP_REFERENCEDPOLICIES = "xacml.referencedPolicies";

    public static final String PROP_PDP_BEHAVIOR = "xacml.pdp.behavior";
    public static final String PROP_PIP_ENGINES = "xacml.pip.engines";

    // Alternative types of PAP Engine
    public static final String PROP_PAP_PAPENGINEFACTORY = "xacml.PAP.papEngineFactory";
    public static final String PROP_AC_PAPENGINEFACTORY = "xacml.AC.papEngineFactory";

    private static volatile Properties properties = new Properties();
    private static boolean needCache = true;

    private static File getPropertiesFile() {
        String propertiesFileName = System.getProperty(XACML_PROPERTIES_NAME);
        if (propertiesFileName == null) {
            propertiesFileName = XACML_PROPERTIES_FILE;
        }
        return new File(propertiesFileName);
    }

    protected XACMLProperties() {
    }

    public static Properties getProperties() throws IOException {
        if (needCache) {
            synchronized (properties) {
                if (needCache) {
                    File fileProperties = getPropertiesFile();
                    if (fileProperties.exists() && fileProperties.canRead()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Loading properties from " + fileProperties.getAbsolutePath());
                        }
                        try (InputStream is = new FileInputStream(fileProperties)) {
                            properties.load(is);
                        }
                    } else {
                        logger.warn("Properties file " + fileProperties.getAbsolutePath()
                                    + " cannot be read.");
                    }
                    needCache = false;
                }
            }
        }
        return properties;
    }

    public static void reloadProperties() {
        synchronized (properties) {
            properties = new Properties();
            needCache = true;
        }
    }

    public static String getProperty(String propertyName, String defaultValue) {
        String value = System.getProperty(propertyName);
        if (value == null) {
            Properties properties = null;
            try {
                properties = getProperties();
                value = properties.getProperty(propertyName);
            } catch (IOException ex) {
                logger.debug("Error getting property: " + propertyName, ex);
            }
        }
        return value == null ? defaultValue : value;
    }

    public static void setProperty(String propertyName, String propertyValue) {
        try {
            getProperties().setProperty(propertyName, propertyValue);
        } catch (IOException ex) {
            logger.debug("Error setting property: " + propertyName, ex);
        }
    }

    public static String getProperty(String propertyName) {
        return getProperty(propertyName, null);
    }

    /**
     * Get the policy-related properties from the given set of properties. These may or may not include ".url"
     * entries for each policy. The caller determines whether it should include them or not and sets checkURLs
     * appropriately. If checkURLs is false and there are ".url" entries, they are put into the result set
     * anyway.
     *
     * @param current
     * @param checkURLs
     * @return
     * @throws Exception
     */
    public static Properties getPolicyProperties(Properties current, boolean checkURLs) throws Exception {
        Properties props = new Properties();
        String[] lists = new String[2];
        lists[0] = current.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
        lists[1] = current.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
        // require that PROP_ROOTPOLICIES exist, even when it is empty
        if (lists[0] != null) {
            props.setProperty(XACMLProperties.PROP_ROOTPOLICIES, lists[0]);
        } else {
            logger.error("Missing property: " + XACMLProperties.PROP_ROOTPOLICIES);
            throw new Exception("Missing property: " + XACMLProperties.PROP_ROOTPOLICIES);
        }
        // require that PROP_REFERENCEDPOLICIES exist, even when it is empty
        if (lists[1] != null) {
            props.setProperty(XACMLProperties.PROP_REFERENCEDPOLICIES, lists[1]);
        } else {
            logger.error("Missing property: " + XACMLProperties.PROP_REFERENCEDPOLICIES);
            throw new Exception("Missing property: " + XACMLProperties.PROP_REFERENCEDPOLICIES);
        }
        Set<Object> keys = current.keySet();
        for (String list : lists) {
            if (list == null || list.length() == 0) {
                continue;
            }
            Iterable<String> policies = Splitter.on(',').trimResults().omitEmptyStrings().split(list);
            if (policies == null) {
                continue;
            }
            for (String policy : policies) {
                for (Object key : keys) {
                    if (key.toString().startsWith(policy)) {
                        props.setProperty(key.toString(), current.getProperty(key.toString()));
                    }
                }
                if (checkURLs) {
                    // every policy must have a ".url" property
                    String urlString = (String)props.get(policy + ".url");
                    if (urlString == null) {
                        logger.error("Policy '" + policy + "' has no .url property");
                        throw new Exception("Policy '" + policy + "' has no .url property");
                    }
                    // the .url must be a valid URL
                    try {
                        // if this does not throw an exception the URL is ok
                        new URL(urlString);
                    } catch (MalformedURLException e) {
                        logger.error("Policy '" + policy + "' has bad .url property");
                        throw new Exception("Policy '" + policy + "' has bad .url property");
                    }
                }
            }
        }
        return props;
    }

    /**
     * Used only when we want just xacml.rootPolicies and xacml.referencedPolicies without any ".url" entries.
     *
     * @return
     * @throws Exception
     */
    public static Properties getPolicyProperties() throws Exception {
        return getPolicyProperties(XACMLProperties.getPolicyProperties(), false);
    }

    public static Set<String> getRootPolicyIDs(Properties props) {
        Set<String> ids = new HashSet<String>();
        String roots = props.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
        if (roots == null) {
            return ids;
        }
        Iterable<String> policies = Splitter.on(',').trimResults().omitEmptyStrings().split(roots);
        for (String id : policies) {
            ids.add(id);
        }
        return ids;
    }

    public static Set<String> getReferencedPolicyIDs(Properties props) {
        Set<String> ids = new HashSet<String>();
        String refs = props.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES);
        if (refs == null) {
            return ids;
        }
        Iterable<String> policies = Splitter.on(',').trimResults().omitEmptyStrings().split(refs);
        for (String id : policies) {
            ids.add(id);
        }
        return ids;
    }

    public static Set<String> getPolicyIDs(Properties props) {
        Set<String> ids = XACMLProperties.getRootPolicyIDs(props);
        ids.addAll(XACMLProperties.getReferencedPolicyIDs(props));
        return ids;
    }

    public static Properties getPipProperties(Properties current) throws Exception {
        Properties props = new Properties();
        String list = current.getProperty(XACMLProperties.PROP_PIP_ENGINES);
        // require that PROP_PIP_ENGINES exist, even when it is empty
        if (list != null) {
            props.setProperty(XACMLProperties.PROP_PIP_ENGINES, list);
        } else {
            throw new Exception("Missing property: " + XACMLProperties.PROP_PIP_ENGINES);
        }
        if (list == null || list.length() == 0) {
            return props;
        }
        Iterable<String> pips = Splitter.on(',').trimResults().omitEmptyStrings().split(list);
        if (pips == null) {
            return props;
        }
        Set<Object> keys = current.keySet();
        for (String pip : pips) {
            for (Object key : keys) {
                if (key.toString().startsWith(pip)) {
                    props.setProperty(key.toString(), current.getProperty(key.toString()));
                }
            }
        }
        return props;
    }

    public static Properties getPipProperties() throws Exception {
        return getPipProperties(XACMLProperties.getPipProperties());
    }
}
