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
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.rest.impl;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPFinderFactory;
import org.apache.openaz.xacml.std.pip.finders.ConfigurableEngineFinder;
import org.apache.openaz.xacml.util.XACMLProperties;

public class XACMLPdpPIPFinderFactory extends PIPFinderFactory {
    private ConfigurableEngineFinder pipFinder;

    private static Log logger = LogFactory.getLog(XACMLPdpPIPFinderFactory.class);

    public XACMLPdpPIPFinderFactory() {
    }

    public XACMLPdpPIPFinderFactory(Properties properties) { //NOPMD
    }

    @Override
    public PIPFinder getFinder() throws PIPException {
        if (pipFinder == null) {
            synchronized (this) {
                if (pipFinder == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Creating default configurable engine finder");
                    }
                    pipFinder = new ConfigurableEngineFinder();
                    Properties xacmlProperties = null;
                    try {
                        xacmlProperties = XACMLProperties.getProperties();
                    } catch (Exception ex) {
                        logger.error("Exception getting XACML properties: " + ex.getMessage(), ex);
                        return null;
                    }
                    if (xacmlProperties != null) {
                        pipFinder.configure(xacmlProperties);
                    }
                }
            }
        }
        return pipFinder;
    }

    @Override
    public PIPFinder getFinder(Properties properties) throws PIPException {
        if (pipFinder == null) {
            synchronized (this) {
                if (pipFinder == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Creating configurable engine finder using: " + properties);
                    }
                    pipFinder = new ConfigurableEngineFinder();
                    pipFinder.configure(properties);
                }
            }
        }
        return this.pipFinder;
    }
}
