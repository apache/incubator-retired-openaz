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
package org.apache.openaz.xacml.pdp.policy;

import java.util.Properties;

import org.apache.openaz.xacml.pdp.util.OpenAZPDPProperties;
import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.FactoryFinder;

/**
 * PolicyFinderFactory provides methods for loading XACML 3.0 policies and policy sets that are used by the
 * {@link org.apache.openaz.xacml.pdp.PDPEngine} to evaluate requests.
 */
public abstract class PolicyFinderFactory {
    private static final String FACTORYID = OpenAZPDPProperties.PROP_POLICYFINDERFACTORY;
    private static final String DEFAULT_FACTORY_CLASSNAME = "org.apache.openaz.xacml.pdp.std.StdPolicyFinderFactory";

    protected PolicyFinderFactory() {
    }

    public static PolicyFinderFactory newInstance() throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PolicyFinderFactory.class);
    }

    public static PolicyFinderFactory newInstance(Properties properties) throws FactoryException {
        return FactoryFinder
            .find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PolicyFinderFactory.class, properties);
    }

    public static PolicyFinderFactory newInstance(String className, ClassLoader classLoader)
        throws FactoryException {
        return FactoryFinder.newInstance(className, PolicyFinderFactory.class, classLoader, false);
    }

    public static PolicyFinderFactory newInstance(String className) throws FactoryException {
        return FactoryFinder.newInstance(className, PolicyFinderFactory.class, null, true);
    }

    /**
     * Gets the configured {@link PolicyFinder}.
     *
     * @return the configured <code>PolicyFinder</code>
     */
    abstract public PolicyFinder getPolicyFinder() throws FactoryException;

    /**
     * Gets the configured {@link PolicyFinder}.
     *
     * @return the configured <code>PolicyFinder</code>
     */
    abstract public PolicyFinder getPolicyFinder(Properties properties) throws FactoryException;
}
