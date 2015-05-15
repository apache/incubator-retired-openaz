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
package org.apache.openaz.xacml.api;

import java.util.Properties;

import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.FactoryFinder;

/**
 * Abstract class for mapping data type {@link Identifier}s to {@link DataType} objects. The static
 * <code>newInstance</code> method looks for the class name of the class extending
 * <code>DataTypeFactory</code> by looking for the property: {@code xacml.dataTypeFactory} in the following
 * places (in order): 1. System properties 2. The xacml.properties file which is located by: a)
 * {@code System.getProperty("xacml.properties")} b) {@code java.home/xacml.properties} 3. If the class name
 * is not found in one of these properties, the default is:
 * {@code org.apache.openaz.xacml.std.StdDataTypeFactory}
 */
public abstract class DataTypeFactory {
    private static final String	FACTORYID					= "xacml.dataTypeFactory";
    private static final String	DEFAULT_FACTORY_CLASSNAME	= "org.apache.openaz.xacml.std.StdDataTypeFactory";

    /**
     * Protected constructor so this class cannot be instantiated.
     */
    protected DataTypeFactory() {

    }

    /**
     * Maps the given {@link Identifier} representing a XACML data type id to a {@link DataType} object
     * implementing that data type.
     *
     * @param dataTypeId the <code>Identifier</code> of the <code>DataType</code> to retrieve.
     * @return the <code>DataType</code> with the given <code>Identifier</code> or null if there is no match.
     */
    public abstract DataType<?> getDataType(Identifier dataTypeId);

    /**
     * Creates an instance of the <code>DataTypeFactory</code> using default configuration information.
     *
     * @return the default <code>DataTypeFactory</code>
     * @throws FactoryException
     */
    public static DataTypeFactory newInstance() throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, DataTypeFactory.class);
    }

    /**
     * Creates an instance of the <code>DataTypeFactory</code> using default configuration information.
     *
     * @param properties
     * @return the default <code>DataTypeFactory</code>
     * @throws FactoryException
     */
    public static DataTypeFactory newInstance(Properties properties) throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, DataTypeFactory.class, properties);
    }

    /**
     * Creates an instance of the <code>DataTypeFactory</code> using the given class name.
     *
     * @param className the <code>String</code> class name of the <code>DataTypeFactory</code> to create
     * @return the <code>DataTypeFactory</code> for the given class name.
     */
    public static DataTypeFactory newInstance(String className) throws FactoryException {
        return FactoryFinder.newInstance(className, DataTypeFactory.class, null, true);
    }

    /**
     * Creates an instance of the <code>DataTypeFactory</code> using the given class name using the given
     * <code>ClassLoader</code>.
     *
     * @param className the <code>String</code> class name of the <code>DataTypeFactory</code> to create
     * @param classLoader the <code>ClassLoader</code> to use to load the class with the given class name
     * @return the <code>DataTypeFactory</code> for the given class name
     */
    public static DataTypeFactory newInstance(String className, ClassLoader classLoader)
        throws FactoryException {
        return FactoryFinder.newInstance(className, DataTypeFactory.class, classLoader, false);
    }
}
