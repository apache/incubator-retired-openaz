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

package org.apache.openaz.xacml.api.pdp;

import java.util.Properties;

import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.FactoryFinder;
import org.apache.openaz.xacml.util.XACMLProperties;

/**
 * PDPEngineFactory provides the interface for creating {@link org.apache.openaz.xacml.pep.PDPEngine}
 * instances.
 */
public abstract class PDPEngineFactory {
    private static final String	FACTORYID = XACMLProperties.PROP_PDPENGINEFACTORY;
    private static final String	DEFAULT_FACTORY_CLASSNAME = "org.apache.openaz.xacml.pdp.OpenAZPDPEngineFactory";

    private Decision defaultBehavior = Decision.INDETERMINATE;
    private ScopeResolver scopeResolver;

    protected static Decision getConfiguredDefaultBehavior() {
        String defaultDecisionString = XACMLProperties.getProperty(XACMLProperties.PROP_PDP_BEHAVIOR);
        if (defaultDecisionString != null && defaultDecisionString.length() > 0) {
            return Decision.get(defaultDecisionString);
        }
        return null;
    }

    protected static Decision getConfiguredDefaultBehavior(Properties properties) {
        String defaultDecisionString = properties.getProperty(XACMLProperties.PROP_PDP_BEHAVIOR);
        if (defaultDecisionString != null && defaultDecisionString.length() > 0) {
            return Decision.get(defaultDecisionString);
        }
        return null;
    }

    /**
     * The constructor is protected to prevent instantiation of the class.
     */
    protected PDPEngineFactory() {
    }

    /**
     * Creates a new <code>PDPEngineFactory</code> instance by examining initialization resources from various
     * places to determine the class to instantiate and return.
     *
     * @return an instance of an object that extends <code>PDPEngineFactory</code> to use in creating
     *         <code>PDPEngine</code> objects.
     */
    public static PDPEngineFactory newInstance() throws FactoryException {
        PDPEngineFactory pdpEngineFactory = FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME,
                                                               PDPEngineFactory.class);
        Decision defaultDecisionBehavior = getConfiguredDefaultBehavior();
        if (defaultDecisionBehavior != null) {
            pdpEngineFactory.setDefaultBehavior(defaultDecisionBehavior);
        }
        return pdpEngineFactory;
    }

    /**
     * Creates a new <code>PDPEngineFactory</code> instance by examining initialization resources from various
     * places to determine the class to instantiate and return.
     *
     * @return an instance of an object that extends <code>PDPEngineFactory</code> to use in creating
     *         <code>PDPEngine</code> objects.
     */
    public static PDPEngineFactory newInstance(Properties properties) throws FactoryException {
        PDPEngineFactory pdpEngineFactory = FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME,
                                                               PDPEngineFactory.class, properties);
        Decision defaultDecisionBehavior = getConfiguredDefaultBehavior(properties);
        if (defaultDecisionBehavior != null) {
            pdpEngineFactory.setDefaultBehavior(defaultDecisionBehavior);
        }
        return pdpEngineFactory;
    }

    /**
     * Creates a new <code>PDPEngineFactory</code> instance using the given class name and
     * <code>ClassLoader</code>. If the <code>ClassLoader</code> is null, use the default thread class loader.
     *
     * @param factoryClassName the <code>String</code> name of the factory class to instantiate
     * @param classLoader the <code>ClassLoader</code> to use to load the factory class
     * @return an instance of an object that extends <code>PDPEngineFactory</code> to use in creating
     *         <code>PDPEngine</code> objects.
     */
    public static PDPEngineFactory newInstance(String factoryClassName, ClassLoader classLoader)
        throws FactoryException {
        PDPEngineFactory pdpEngineFactory = FactoryFinder.newInstance(factoryClassName,
                                                                      PDPEngineFactory.class, classLoader,
                                                                      false);
        Decision defaultDecisionBehavior = getConfiguredDefaultBehavior();
        if (defaultDecisionBehavior != null) {
            pdpEngineFactory.setDefaultBehavior(defaultDecisionBehavior);
        }
        return pdpEngineFactory;
    }

    /**
     * Creates a new <code>PDPEngineFactory</code> instance using the given class name and the default thread
     * class loader.
     *
     * @param factoryClassName the <code>String</code> name of the factory class to instantiate
     * @return an instance of an object that extends <code>PDPEngineFactory</code> to use in creating
     *         <code>PDPEngine</code> objects.
     */
    public static PDPEngineFactory newInstance(String factoryClassName) throws FactoryException {
        PDPEngineFactory pdpEngineFactory = FactoryFinder.newInstance(factoryClassName,
                                                                      PDPEngineFactory.class, null, true);
        Decision defaultDecisionBehavior = getConfiguredDefaultBehavior();
        if (defaultDecisionBehavior != null) {
            pdpEngineFactory.setDefaultBehavior(defaultDecisionBehavior);
        }
        return pdpEngineFactory;
    }

    /**
     * Creates a new <code>PDPEngine</code> using the default policy set and {@link org.apache.openaz.xacml.api.pip.PIPFinder}.
     *
     * @return a new <code>PDPEngine</code>
     */
    public abstract PDPEngine newEngine() throws FactoryException;

    /**
     * Creates a new <code>PDPEngine</code> using the default policy set and {@link org.apache.openaz.xacml.api.pip.PIPFinder}.
     *
     * @return a new <code>PDPEngine</code>
     */
    public abstract PDPEngine newEngine(Properties properties) throws FactoryException;

    /*
     * TODO: There needs to be an interface where you can request a PDPEngine based on a set of profiles. This
     * could be quite complex, with required and optional profile values specified.
     */

    /**
     * Gets the default <code>Decision</code> that a <code>PDPEngine</code> created from this
     * <code>PDPEngineFactory</code> will return when there is no applicable root policy.
     *
     * @return the <code>Decision</code> that a <code>PDPEngine</code> created from this
     *         <code>PDPEngineFactory</code. will return when there is no applicable root policy.
     */
    public Decision getDefaultBehavior() {
        return this.defaultBehavior;
    }

    /**
     * Sets the default <code>Decision</code> that a <code>PDPEngine</code> created from this
     * <code>PDPEngineFactory</code> will return when there is no applicable root policy.
     *
     * @param decision the <code>Decision</code> to return
     */
    public void setDefaultBehavior(Decision decision) {
        this.defaultBehavior = decision;
    }

    /**
     * Gets the current {@link ScopeResolver} used by <code>PDPEngine</code>s created with this
     * <code>PDPEngineFactory</code> will use to expand scope attributes in a <code>Request</code>.
     *
     * @return the current <code>ScopeResolver</code> for this <code>PDPEngineFactory</code>
     */
    public ScopeResolver getScopeResolver() {
        return this.scopeResolver;
    }

    /**
     * Sets the <code>ScopeResolver</code> used by <code>PDPEngine</code>s created with this
     * <code>PDPEngineFactory</code>.
     *
     * @param scopeResolverIn
     */
    public void setScopeResolver(ScopeResolver scopeResolverIn) {
        this.scopeResolver = scopeResolverIn;
    }

}
