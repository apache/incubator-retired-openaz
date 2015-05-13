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

/**
 * PIPEngine is the interface that objects implement that do look-up of
 * {@link org.apache.openaz.xacml.api.Attribute}s.
 */
public interface PIPEngine {
    /**
     * Gets the <code>String</code> name identifying this <code>PIPEngine</code>. Names do not need to be
     * unique.
     *
     * @return the <code>String</code> name of this <code>PIPEngine</code>>
     */
    String getName();

    /**
     * Gets the <code>String</code> description of this <code>PIPEngine</code>.
     *
     * @return the <code>String</code> description of this <code>PIPEngine</code>.
     */
    String getDescription();

    /**
     * Returns a list of PIPRequests required by the Engine to return an attribute(s).
     *
     * @return Collection of required attributes
     */
    Collection<PIPRequest> attributesRequired();

    /**
     * Returns a list of PIPRequest objects that the Engine can return.
     *
     * @return Collection of provided attributes
     */
    Collection<PIPRequest> attributesProvided();

    /**
     * Retrieves <code>Attribute</code>s that match the given
     * {@link org.apache.openaz.xacml.api.pip.PIPRequest}. The
     * {@link org.apache.openaz.xacml.api.pip.PIPResponse} may contain multiple <code>Attribute</code>s and
     * they do not need to match the <code>PIPRequest</code>. In this way, a <code>PIPEngine</code> may
     * compute multiple related <code>Attribute</code>s at once.
     *
     * @param pipRequest the <code>PIPRequest</code> defining which <code>Attribute</code>s should be
     *            retrieved
     * @param pipFinder the <code>PIPFinder</code> to use for retrieving supporting attribute values
     * @return a {@link org.apache.openaz.xacml.pip.PIPResponse} with the results of the request
     * @throws PIPException if there is an error retrieving the <code>Attribute</code>s.
     */
    PIPResponse getAttributes(PIPRequest pipRequest, PIPFinder pipFinder) throws PIPException;
}
