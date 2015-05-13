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
package org.apache.openaz.xacml.std.pip.finders;

import java.util.Collection;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.std.pip.engines.ConfigurableEngine;
import org.apache.openaz.xacml.util.AttributeUtils;

/**
 * ConfigurableEngineFinder extends {@link EngineFinder} with a method for configuring it from a
 * <code>Properties</code> object.
 */
public class ConfigurableEngineFinder extends EngineFinder {
    private static final String PROP_PIP_ENGINES = "xacml.pip.engines";
    private static final String CLASSNAME = ".classname";

    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Creates an instance of the given <code>String</code> className for an object implementing the
     * <code>ConfigurableEngine</code> interface.
     *
     * @param className the <code>String</code> class name of the engine
     * @return an instance of the given class name
     * @throws org.apache.openaz.xacml.api.pip.PIPException
     */
    protected ConfigurableEngine newEngine(String className) throws PIPException {
        Class<?> classForEngine = null;
        try {
            classForEngine = Class.forName(className);
            if (!ConfigurableEngine.class.isAssignableFrom(classForEngine)) {
                throw new ClassNotFoundException("Engine class \"" + className
                                                 + "\" does not implement ConfigurableEngine");
            }
            return ConfigurableEngine.class.cast(classForEngine.newInstance());
        } catch (Exception ex) {
            throw new PIPException("Exception getting Class for \"" + className + "\""
                                   + ex.getLocalizedMessage());
        }
    }

    protected void configureEngine(String engineId, Properties properties) throws PIPException {
        /*
         * Get the class name for the engine
         */
        String engineClassName = properties.getProperty(engineId + CLASSNAME);
        if (engineClassName == null) {
            throw new PIPException("No " + CLASSNAME + " property for PIP engine \"" + engineId + "\"");
        }

        /*
         * Get an instance of the engine
         */
        ConfigurableEngine configurableEngine = newEngine(engineClassName);

        /*
         * Configure the engine
         */
        configurableEngine.configure(engineId, properties);

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Engine " + engineId + " Provides: ");
            Collection<PIPRequest> attributes = configurableEngine.attributesProvided();
            for (PIPRequest attribute : attributes) {
                this.logger.debug(System.lineSeparator() + AttributeUtils.prettyPrint(attribute));
            }
            this.logger.debug("Engine " + engineId + " Requires: ");
            attributes = configurableEngine.attributesRequired();
            for (PIPRequest attribute : attributes) {
                this.logger.debug(System.lineSeparator() + AttributeUtils.prettyPrint(attribute));
            }
        }

        /*
         * Register the engine
         */
        this.register(configurableEngine);
    }

    public ConfigurableEngineFinder() {
    }

    /**
     * Gets the "org.apache.openaz.xacml.pip.engines" property from the given <code>Properties</code> to find
     * the list of PIP engines that should be created, configured, and registered.
     *
     * @param properties the <code>Properties</code> containing the engine configurations
     * @throws org.apache.openaz.xacml.api.pip.PIPException if there is an error creating and configuring the
     *             engines
     */
    public void configure(Properties properties) throws PIPException {
        String engineIds = properties.getProperty(PROP_PIP_ENGINES);
        if (engineIds == null || engineIds.length() == 0) {
            return;
        }

        /*
         * Split the engines by comma
         */
        String[] engineIdArray = engineIds.split("[,]", 0);
        if (engineIdArray == null || engineIdArray.length == 0) {
            return;
        }

        /*
         * For each engine ID, configure the engine and register it
         */
        for (String engineId : engineIdArray) {
            try {
                this.configureEngine(engineId, properties);
            } catch (PIPException ex) {
                this.logger.error("Exception configuring engine with id \"" + engineId + "\"", ex);
            }
        }
    }

}
