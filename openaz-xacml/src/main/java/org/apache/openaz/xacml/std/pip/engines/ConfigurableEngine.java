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
package org.apache.openaz.xacml.std.pip.engines;

import java.util.Properties;

import org.apache.openaz.xacml.api.pip.PIPEngine;
import org.apache.openaz.xacml.api.pip.PIPException;

/**
 * ConfigurableEngine extends the {@link org.apache.openaz.xacml.api.pip.PIPEngine} interface with methods for
 * configuring the engine from a <code>Properties</code> object.
 */
public interface ConfigurableEngine extends PIPEngine {
    /**
     * Configures this <code>ConfigurableEngine</code> from the given <code>Properties</code>.
     *
     * @param id the <code>String</code> name for this <code>ConfigurableEngine</code> used also to locate
     *            properties
     * @param properties the <code>Properties</code> containing the configuration parameters for this
     *            <code>ConfigurableEngine</code>
     * @throws org.apache.openaz.xacml.api.pip.PIPException if there is an error configuring the
     *             <code>ConfigurableEngine</code>
     */
    void configure(String id, Properties properties) throws PIPException;
}
