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

import java.util.Collection;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.Status;

/**
 * PIPResponse is the interface that objects implement that represent a response from a {@link PIPEngine}.
 */
public interface PIPResponse {
    /**
     * Gets the {@link org.apache.openaz.xacml.api.Status} of the request to retrieve attributes from a
     * <code>PIPEngine</code>.
     *
     * @return the <code>Status</code> of the request to retrieve attributes from a <code>PIPEngine</code>
     */
    Status getStatus();

    /**
     * Gets the <code>Collection</code> of {@link org.apache.openaz.xacml.api.Attribute}s returned from a
     * {@link PIPEngine}. The caller must not modify the returned <code>Collection</code>. The implementation
     * is free to enforce this with unmodifiable collections.
     *
     * @return The <code>Collection</code> of <code>Attribute</code>s returned or an empty list if none are
     *         found
     */
    Collection<Attribute> getAttributes();

    /**
     * Determines if this <code>PIPResponse</code> is simple or not. A simple <code>PIPResponse</code>
     * contains a single {@link org.apache.openaz.xacml.api.Attribute} whose
     * {@link org.apache.openaz.xacml.api.AttributeValue}s are all of the same data type.
     *
     * @return true if this <code>PIPResponse</code> is simple, else false.
     */
    boolean isSimple();
}
