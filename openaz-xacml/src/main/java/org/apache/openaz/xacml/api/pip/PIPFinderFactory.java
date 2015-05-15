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
package org.apache.openaz.xacml.api.pip;

import java.util.Properties;

import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.FactoryFinder;
import org.apache.openaz.xacml.util.XACMLProperties;

/**
 * PIPFinderFactory is the factory class for getting the configured {@link PIPFinder}.
 */
public abstract class PIPFinderFactory {
    private static final String	FACTORYID = XACMLProperties.PROP_PIPFINDERFACTORY;
    private static final String	DEFAULT_FACTORY_CLASSNAME = "org.apache.openaz.xacml.std.pip.StdPIPFinderFactory";

    protected Properties properties = null;

    /**
     * Protected constructor so this class cannot be instantiated.
     */
    protected PIPFinderFactory() {
    }

    /**
     * Gets an instance of the configured <code>PIPFinderFactory</code> class.
     *
     * @return the configured <code>PIPFinderFactory</code>
     * @throws FactoryException if there is an error instantiating the factory
     */
    public static PIPFinderFactory newInstance() throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PIPFinderFactory.class);
    }

    public static PIPFinderFactory newInstance(Properties properties) throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PIPFinderFactory.class, properties);
    }

    public static PIPFinderFactory newInstance(String factoryClassName, ClassLoader classLoader)
        throws FactoryException {
        return FactoryFinder.newInstance(factoryClassName, PIPFinderFactory.class, classLoader, false);
    }

    public static PIPFinderFactory newInstance(String factoryClassName) throws FactoryException {
        return FactoryFinder.newInstance(factoryClassName, PIPFinderFactory.class, null, true);
    }

    /**
     * Gets an instance of the configured <code>PIPFinder</code> class.
     *
     * @return an instance of the configured <code>PIPFinder</code>
     */
    abstract public PIPFinder getFinder() throws PIPException;

    /**
     * Gets an instance of the configured <code>PIPFinder</code> class.
     *
     * @return an instance of the configured <code>PIPFinder</code>
     */
    abstract public PIPFinder getFinder(Properties properties) throws PIPException;
}
