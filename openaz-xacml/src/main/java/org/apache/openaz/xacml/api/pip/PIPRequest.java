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
package org.apache.openaz.xacml.api.pip;

import org.apache.openaz.xacml.api.Identifier;

/**
 * PIPRequest is the interface that objects implement to represent a request to a {@link PIPEngine} to
 * retrieve attributes with values that meet a given request.
 */
public interface PIPRequest {
    /**
     * Gets the {@link org.apache.openaz.xacml.api.Identifier} of the category of the attributes to retrieve.
     *
     * @return the <code>Identifier</code> for the category of the attributes to retrieve
     */
    Identifier getCategory();

    /**
     * Gets the <code>Identifier</code> of the attributes to retrieve.
     *
     * @return the <code>Identifier</code> of the attributes to retrieve.
     */
    Identifier getAttributeId();

    /**
     * Gets the <code>Identifier</code> of the requested data type for attribute values.
     *
     * @return the <code>Identifier</code> of the requested data type for attribute values
     */
    Identifier getDataTypeId();

    /**
     * Gets the <code>String</code> issuer identifier for the attributes to retrieve.
     *
     * @return the <code>String</code> issuer identifier for the attributes to retrieve or null if there is no
     *         requirement to match the issuer.
     */
    String getIssuer();

}
