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
package org.apache.openaz.xacml.std;

import java.util.Collection;
import java.util.Iterator;

import org.apache.openaz.xacml.api.AttributeCategory;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.RequestAttributes;
import org.apache.openaz.xacml.api.RequestDefaults;
import org.apache.openaz.xacml.api.RequestReference;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.Request} interface.
 */
public class StdRequest extends Wrapper<Request> implements Request {
    /**
     * Creates a new <code>StdRequest</code> that is a copy of the given
     * {@link org.apache.openaz.xacml.api.Request}.
     *
     * @param request the <code>Request</code> to copy
     */
    public StdRequest(Request request) {
        super(request);
    }

    /**
     * Creates a new <code>StdMutableRequest</code> with the given parameters.
     *
     * @param statusIn the {@link org.apache.openaz.xacml.api.Status} of the <code>StdMutableRequest</code>
     *            representing its validity
     * @param requestDefaultsIn the {@link org.apache.openaz.xacml.api.RequestDefaults} representing the XACML
     *            RequestDefaults
     * @param returnPolicyIdListIn a boolean indicating whether XACML PolicyId and PolicySetIds should be
     *            returned with the Results
     * @param combinedDecisionIn a boolean indicating whether multiple Decision Request Results should be
     *            combined into a single Result
     * @param listRequestAttributes a <code>Collection</code> of
     *            {@link org.apache.openaz.xacml.api.RequestAttributes} defining the parameters of the Request
     * @param listRequestReferences a <code>Collection</code> of
     *            {@link org.apache.openaz.xacml.api.RequestReference}s for multiple decision requests
     */
    public StdRequest(Status statusIn, RequestDefaults requestDefaultsIn, boolean returnPolicyIdListIn,
                      boolean combinedDecisionIn, Collection<RequestAttributes> listRequestAttributes,
                      Collection<RequestReference> listRequestReferences) {
        this(new StdMutableRequest(statusIn, requestDefaultsIn, returnPolicyIdListIn, combinedDecisionIn,
                                   listRequestAttributes, listRequestReferences));
    }

    /**
     * Creates a new <code>StdMutableRequest</code> with the given parameters and a default
     * {@link org.apache.openaz.xacmo.api.Status} of OK.
     *
     * @param requestDefaultsIn the {@link org.apache.openaz.xacml.api.RequestDefaults} representing the XACML
     *            RequestDefaults
     * @param returnPolicyIdListIn a boolean indicating whether XACML PolicyId and PolicySetIds should be
     *            returned with the Results
     * @param combinedDecisionIn a boolean indicating whether multiple Decision Request Results should be
     *            combined into a single Result
     * @param listRequestAttributes a <code>Collection</code> of
     *            {@link org.apache.openaz.xacml.api.RequestAttributes} defining the parameters of the Request
     * @param listRequestReferences a <code>Collection</code> of
     *            {@link org.apache.openaz.xacml.api.RequestReference}s for multiple decision requests
     */
    public StdRequest(RequestDefaults requestDefaultsIn, boolean returnPolicyIdListIn,
                      boolean combinedDecisionIn, Collection<RequestAttributes> listRequestAttributes,
                      Collection<RequestReference> listRequestReferences) {
        this(new StdMutableRequest(requestDefaultsIn, returnPolicyIdListIn, combinedDecisionIn,
                                   listRequestAttributes, listRequestReferences));
    }

    /**
     * Creates a new <code>StdRequest</code> with the given {@link org.apache.openaz.xacml.api.Status} and
     * defaults for all other attributes.
     *
     * @param statusIn the <code>Status</code> for the new <code>StdRequest</code>.
     */
    public StdRequest(Status statusIn) {
        this(new StdMutableRequest(statusIn));
    }

    @Override
    public RequestDefaults getRequestDefaults() {
        return this.getWrappedObject().getRequestDefaults();
    }

    @Override
    public boolean getReturnPolicyIdList() {
        return this.getWrappedObject().getReturnPolicyIdList();
    }

    @Override
    public boolean getCombinedDecision() {
        return this.getWrappedObject().getCombinedDecision();
    }

    @Override
    public Collection<RequestAttributes> getRequestAttributes() {
        return this.getWrappedObject().getRequestAttributes();
    }

    @Override
    public Collection<AttributeCategory> getRequestAttributesIncludedInResult() {
        return this.getWrappedObject().getRequestAttributesIncludedInResult();
    }

    @Override
    public Iterator<RequestAttributes> getRequestAttributes(Identifier category) {
        return this.getWrappedObject().getRequestAttributes(category);
    }

    @Override
    public RequestAttributes getRequestAttributesByXmlId(String xmlId) {
        return this.getWrappedObject().getRequestAttributesByXmlId(xmlId);
    }

    @Override
    public Collection<RequestReference> getMultiRequests() {
        return this.getWrappedObject().getMultiRequests();
    }

    @Override
    public Status getStatus() {
        return this.getWrappedObject().getStatus();
    }

}
