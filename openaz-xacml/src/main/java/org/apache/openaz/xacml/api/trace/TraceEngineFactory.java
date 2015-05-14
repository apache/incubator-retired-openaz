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

package org.apache.openaz.xacml.api.trace;

import java.util.Properties;

import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.FactoryFinder;
import org.apache.openaz.xacml.util.XACMLProperties;

/**
 * Provides methods for creating instances of the {@link TraceEngine} interface. This may be used by PDP, PEP,
 * or PIP implementations to provide tracing facilities that are useful for validating that XACML Policies and
 * PolicySets operate as expected.
 */
public abstract class TraceEngineFactory {
    private static final String	FACTORYID = XACMLProperties.PROP_TRACEENGINEFACTORY;
    private static final String	DEFAULT_FACTORY_CLASSNAME = "org.apache.openaz.xacml.std.trace.NullTraceEngineFactory";

    protected Properties properties = null;

    protected TraceEngineFactory() {
    }

    protected TraceEngineFactory(Properties properties) {
        this.properties = properties;
    }

    /**
     * Gets an instance of the <code>TraceEngineFactory</code> class using standard factory lookup methods defined by
     * the {@link org.apache.openaz.xacml.util.FactoryFinder} class.
     *
     * @return an instance of the <code>TraceEngineFactory</code> class.
     * @throws FactoryException if there is an error finding a <code>TraceEngineFactory</code>
     */
    public static TraceEngineFactory newInstance() throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, TraceEngineFactory.class);
    }

    /**
     * Gets an instance of the <code>TraceEngineFactory</code> class using the given <code>String</code> class
     * name, and <code>ClassLoader</code>>
     *
     * @param className the <code>String</code> name of the <code>Class</code> extending
     *            <code>TraceEngineFactory</code> to load
     * @param classLoader the <code>ClassLoader</code> to use
     * @return an instance of the <code>TraceEngineFactory</code>
     * @throws FactoryException if there is an error loading the <code>TraceEngineFactory</code> class or
     *             creating an instance from it.
     */
    public static TraceEngineFactory newInstance(String className, ClassLoader classLoader)
        throws FactoryException {
        return FactoryFinder.newInstance(className, TraceEngineFactory.class, classLoader, false);
    }

    /**
     * Gets an instance of the <code>TraceEngineFactory</code> class using the given <code>String</code> class
     * name, and the standard <code>ClassLoader</code>>
     *
     * @param className the <code>String</code> name of the <code>Class</code> extending
     *            <code>TraceEngineFactory</code> to load
     * @return an instance of the <code>TraceEngineFactory</code>
     * @throws FactoryException if there is an error loading the <code>TraceEngineFactory</code> class or
     *             creating an instance from it.
     */
    public static TraceEngineFactory newInstance(String className) throws FactoryException {
        return FactoryFinder.newInstance(className, TraceEngineFactory.class, null, true);
    }

    /**
     * Gets an instance of the {@link TraceEngine} interface to use for posting {@link TraceEvent}s.
     *
     * @return an instance of the <code>TraceEngine</code> interface
     */
    public abstract TraceEngine getTraceEngine();

}
