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
package org.apache.openaz.xacml.api.pap;

import java.util.Properties;

import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.FactoryFinder;
import org.apache.openaz.xacml.util.XACMLProperties;

public abstract class PAPEngineFactory {
    private static final String	FACTORYID	= XACMLProperties.PROP_PAP_PAPENGINEFACTORY;
    private static final String	DEFAULT_FACTORY_CLASSNAME	= "org.apache.openaz.xacml.std.pap.StdEngineFactory";

    /**
     * The constructor is protected to prevent instantiation of the class.
     */
    protected PAPEngineFactory() {
    }

    /**
     * Creates a new <code>PAPEngineFactory</code> instance by examining initialization resources from various
     * places to determine the class to instantiate and return.
     *
     * @return an instance of an object that extends <code>PAPEngineFactory</code> to use in creating
     *         <code>PAPEngine</code> objects.
     * @throws FactoryException
     */
    public static PAPEngineFactory newInstance() throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PAPEngineFactory.class);
    }

    /**
     * Creates a new <code>PAPEngineFactory</code> instance by examining initialization resources from various
     * places to determine the class to instantiate and return.
     *
     * @return an instance of an object that extends <code>PAPEngineFactory</code> to use in creating
     *         <code>PAPEngine</code> objects.
     * @throws FactoryException
     */
    public static PAPEngineFactory newInstance(Properties properties) throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, PAPEngineFactory.class, properties);
    }

    /**
     * Creates a new <code>PAPEngineFactory</code> instance using the given class name and
     * <code>ClassLoader</code>. If the <code>ClassLoader</code> is null, use the default thread class loader.
     *
     * @param factoryClassName the <code>String</code> name of the factory class to instantiate
     * @param classLoader the <code>ClassLoader</code> to use to load the factory class
     * @return an instance of an object that extends <code>PAPEngineFactory</code> to use in creating
     *         <code>PAPEngine</code> objects.
     */
    public static PAPEngineFactory newInstance(String factoryClassName, ClassLoader classLoader)
        throws FactoryException {
        return FactoryFinder.newInstance(factoryClassName, PAPEngineFactory.class, classLoader, false);
    }

    /**
     * Creates a new <code>PAPEngineFactory</code> instance using the given class name and the default thread
     * class loader.
     *
     * @param factoryClassName the <code>String</code> name of the factory class to instantiate
     * @return an instance of an object that extends <code>PAPEngineFactory</code> to use in creating
     *         <code>PAPEngine</code> objects.
     */
    public static PAPEngineFactory newInstance(String factoryClassName) throws FactoryException {
        return FactoryFinder.newInstance(factoryClassName, PAPEngineFactory.class, null, true);
    }

    /**
     * Creates a new <code>PAPEngine</code> based on the configured <code>PAPEngineFactory</code>.
     *
     * @return a new <code>PAPEngine</code>
     * @throws org.apache.openaz.xacml.api.pap.PAPException
     */
    public abstract PAPEngine newEngine() throws FactoryException, PAPException;

    /**
     * Creates a new <code>PAPEngine</code> based on the configured <code>PAPEngineFactory</code>.
     *
     * @return a new <code>PAPEngine</code>
     * @throws org.apache.openaz.xacml.api.pap.PAPException
     */
    public abstract PAPEngine newEngine(Properties properties) throws FactoryException, PAPException;
}
