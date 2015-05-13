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
package org.apache.openaz.xacml.std.pip.engines;

import java.util.Collection;
import java.util.Properties;

import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPRequest;

public interface ConfigurableResolver {
    /**
     * Configures this <code>JDBCResolver</code> using the given <code>Properties</code>>
     *
     * @param id the <code>String</code> identifier for locating properties for this <code>JDBCResolver</code>
     * @param properties the <code>Properties</code> to search for properties
     * @param defaultIssuer the default issuer value if none is defined specifically.
     * @throws org.apache.openaz.xacml.api.pip.PIPException if there is an error configuring this
     *             <code>JDBCResolver</code>
     */
    void configure(String id, Properties properties, String defaultIssuer) throws PIPException;

    /**
     * Adds attributes required by the resolver to return an attribute.
     *
     * @param attributes - A modifiable collection
     */
    void attributesRequired(Collection<PIPRequest> attributes);

    /**
     * Adds attributes provided by the resolver.
     *
     * @param attributes - A modifiable collection
     */
    void attributesProvided(Collection<PIPRequest> attributes);

}
