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

package org.apache.openaz.xacml.api.pep;

import java.util.Properties;

import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.FactoryFinder;
import org.apache.openaz.xacml.util.XACMLProperties;

/**
 * PEPEngineFactory provides the interface for creating {@link PEPEngine} instances.
 */
public abstract class PEPEngineFactory {
    private static final String	FACTORYID = XACMLProperties.PROP_PEPENGINEFACTORY;
    private static final String	DEFAULT_FACTORY_CLASSNAME = "org.apache.openaz.xacml.std.pep.StdEngineFactory";

    /**
     * The constructor is protected to prevent instantiation of the class.
     */
    protected PEPEngineFactory() {
    }

    /**
     * Creates a new <code>PEPEngineFactory</code> instance by examining initialization resources from various
     * places to determine the class to instantiate and return.
     *
     * @return an instance of an object that extends <code>PEPEngineFactory</code> to use in creating
     *         <code>PEPEngine</code> objects.
     */
    public static PEPEngineFactory newInstance() throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PEPEngineFactory.class);
    }

    /**
     * Creates a new <code>PEPEngineFactory</code> instance by examining initialization resources from various
     * places to determine the class to instantiate and return.
     *
     * @return an instance of an object that extends <code>PEPEngineFactory</code> to use in creating
     *         <code>PEPEngine</code> objects.
     */
    public static PEPEngineFactory newInstance(Properties properties) throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PEPEngineFactory.class, properties);
    }

    /**
     * Creates a new <code>PEPEngineFactory</code> instance using the given class name and
     * <code>ClassLoader</code>. If the <code>ClassLoader</code> is null, use the default thread class loader.
     *
     * @param factoryClassName the <code>String</code> name of the factory class to instantiate
     * @param classLoader the <code>ClassLoader</code> to use to load the factory class
     * @return an instance of an object that extends <code>PEPEngineFactory</code> to use in creating
     *         <code>PEPEngine</code> objects.
     */
    public static PEPEngineFactory newInstance(String factoryClassName, ClassLoader classLoader)
        throws FactoryException {
        return FactoryFinder.newInstance(factoryClassName, PEPEngineFactory.class, classLoader, false);
    }

    /**
     * Creates a new <code>PEPEngineFactory</code> instance using the given class name and the default thread
     * class loader.
     *
     * @param factoryClassName the <code>String</code> name of the factory class to instantiate
     * @return an instance of an object that extends <code>PEPEngineFactory</code> to use in creating
     *         <code>PEPEngine</code> objects.
     */
    public static PEPEngineFactory newInstance(String factoryClassName) throws FactoryException {
        return FactoryFinder.newInstance(factoryClassName, PEPEngineFactory.class, null, true);
    }

    /**
     * Creates a new <code>PEPEngine</code> based on the configured <code>PEPEngineFactory</code>.
     *
     * @return a new <code>PEPEngine</code>
     */
    public abstract PEPEngine newEngine() throws PEPException;

    /**
     * Creates a new <code>PEPEngine</code> based on the configured <code>PEPEngineFactory</code>.
     *
     * @return a new <code>PEPEngine</code>
     */
    public abstract PEPEngine newEngine(Properties properties) throws PEPException;
}
