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

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.pdp.util.OpenAZPDPProperties;
import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.FactoryFinder;

/**
 * FunctionDefinitionFactory is an abstract class for mapping function
 * {@link org.apache.openaz.xacml.api.Identifier} ids to {@link FunctionDefinition} objects.
 */
public abstract class FunctionDefinitionFactory {
     private static final String FACTORYID = OpenAZPDPProperties.PROP_FUNCTIONDEFINITIONFACTORY;
     private static final String DEFAULT_FACTORY_CLASSNAME = "org.apache.openaz.xacml.pdp.std.StdFunctionDefinitionFactory";

    protected FunctionDefinitionFactory() {
    }

    /**
     * Maps the given <code>Identifier</code> representing a XACML function to a
     * <code>FunctionDefinition</code> object.
     *
     * @param functionId the <code>Identifier</code> of the <code>FunctionDefinition</code> to retrieve
     * @return the <code>FunctionDefinition</code> for the given <code>Identifier</code> or null if not found
     */
    public abstract FunctionDefinition getFunctionDefinition(Identifier functionId);

    /**
     * Creates an instance of the <code>FunctionDefinitionFactory</code> using default configuration
     * information.
     *
     * @return the default <code>FunctionDefinitionFactory</code>
     */
    public static FunctionDefinitionFactory newInstance() throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, FunctionDefinitionFactory.class);
    }

    /**
     * Creates an instance of the <code>FunctionDefinitionFactory</code> using default configuration
     * information.
     *
     * @return the default <code>FunctionDefinitionFactory</code>
     */
    public static FunctionDefinitionFactory newInstance(Properties properties) throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, FunctionDefinitionFactory.class,
                                  properties);
    }

    /**
     * Creates an instance of the <code>FunctionDefinitionFactory</code> using the given class name.
     *
     * @param className the <code>String</code> class name of the <code>FunctionDefinitionFactory</code> to
     *            create
     * @return the <code>FunctionDefinitionFactory</code> for the given class name.
     */
    public static FunctionDefinitionFactory newInstance(String className) throws FactoryException {
        return FactoryFinder.newInstance(className, FunctionDefinitionFactory.class, null, true);
    }

    /**
     * Creates an instance of the <code>FunctionDefinitionFactory</code> using the given class name using the
     * given <code>ClassLoader</code>.
     *
     * @param className the <code>String</code> class name of the <code>FunctionDefinitionFactory</code> to
     *            create
     * @param classLoader the <code>ClassLoader</code> to use to load the class with the given class name
     * @return the <code>FunctionDefinitionFactory</code> for the given class name
     */
    public static FunctionDefinitionFactory newInstance(String className, ClassLoader classLoader)
        throws FactoryException {
        return FactoryFinder.newInstance(className, FunctionDefinitionFactory.class, classLoader, false);
    }
}
