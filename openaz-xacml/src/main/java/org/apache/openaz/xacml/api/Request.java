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

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides the API for objects that represent XACML Request elements. Requests are used to specify the
 * contents of a XACML decision request.
 */
public interface Request {

    /**
     * Gets the {@link org.apache.openaz.xacml.api.RequestDefaults} representing the XACML RequestDefaults for
     * this <code>Request</code>.
     *
     * @return the <code>RequestDefaults</code> representing the XACML RequestDefaults for this
     *         <code>Request</code>.
     */
    RequestDefaults getRequestDefaults();

    /**
     * Returns true if the list of XACML PolicyIds should be returned for this <code>Request</code>.
     *
     * @return true if XACML PolicyIds should be returned, otherwise false
     */
    boolean getReturnPolicyIdList();

    /**
     * Returns true if the results from multiple individual decisions for this <code>Request</code> should be
     * combined into a single XACML Result.
     *
     * @return true if multiple results should be combined, otherwise false.
     */
    boolean getCombinedDecision();

    /**
     * Gets the <code>Collection</code> of {@link org.apache.openaz.xacml.api.RequestAttributes} representing
     * XACML Attributes elements for this <code>Request</code>. The <code>Collection</code> should not be
     * modified. Implementations are free to use unmodifiable lists to enforce this.
     *
     * @return the <code>Collection</code> of <code>RequestAttributes</code> representing XACML Attributes
     *         elements for this <code>Request</code>.
     */
    Collection<RequestAttributes> getRequestAttributes();

    /**
     * Gets the <code>Collection</code> of {@link org.apache.openaz.xacml.api.RequestAttributes} representing
     * XACML Attributes elements for this <code>Request</code> that contain
     * {@link org.apache.openaz.xacml.api.Attribute}s where <code>getIncludeInResults</code> is true.
     *
     * @return a <code>Collection</code> of <code>RequestAttributes</code> containing one or more
     *         <code>Attribute</code>s to include in results.
     */
    Collection<AttributeCategory> getRequestAttributesIncludedInResult();

    /**
     * Gets an <code>Iterator</code> over all of the {@link org.apache.openaz.xacml.api.RequestAttributes}
     * objects found in this <code>Request</code> with the given {@link org.apache.openaz.xacml.api.Identifier}
     * representing a XACML Category.
     *
     * @param category the <code>Identifier</code> representing the XACML Category of the
     *            <code>ReqestAttributes</code> to retrieve.
     * @return an <code>Iterator</code> over all of the <code>RequestAttributes</code> whose Category matches
     *         the given <code>Identifier</code>
     */
    Iterator<RequestAttributes> getRequestAttributes(Identifier category);

    /**
     * Gets a single matching <code>RequestAttributes</code> representing the XACML Attributes element with
     * whose xml:Id matches the given <code>String</code>>
     *
     * @param xmlId the <code>String</code> representing the xml:Id of the <code>RequestAttributes</code> to
     *            retrieve
     * @return the single matching <code>RequestAttributes</code> object or null if not found
     */
    RequestAttributes getRequestAttributesByXmlId(String xmlId);

    /**
     * Gets the <code>Collection</code> of {@link org.apache.openaz.xacml.api.RequestReference}s representing
     * XACML MultiRequest elements in this <code>Request</code>.
     *
     * @return the <code>Collection</code> of <code>RequestAttributes</code> representing XACML MultiRequest
     *         elements in this <code>Request</code>.
     */
    Collection<RequestReference> getMultiRequests();

    /**
     * Gets the {@link Status} representing the XACML Status element for the Request represented by this
     * <code>Request</code>.
     *
     * @return the <code>Status</code> representing the XACML Status element for the Request represented by
     *         this <code>Request</code>.
     */
    Status getStatus();

    /**
     * {@inheritDoc} Implementations of the <code>Request</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>Requests</code> (<code>r1</code> and
     * <code>r2</code>) are equals if:
     * {@code r1.getRequestDefaults() == null && r2.getRequestDefaults() == null} OR
     * {@code r1.getRequestDefaults().equals(r2.getRequestDefaults())} AND
     * {@code r1.getReturnPolicyIdList() == r2.getReturnPolicyIdList()} AND
     * {@code r1.getCombinedDecision() == r2.getCombinedDecision()} AND {@code r1.getRequestAttributes()} is
     * pairwise equal to {@code r2.getRequestAttributes()} AND {@code r1.getMultiRequests()} is pairwise equal
     * to {@code r2.getMultiRequests()}
     */
    @Override
    boolean equals(Object obj);
}
