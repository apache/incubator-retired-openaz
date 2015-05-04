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
package com.att.research.xacmlatt.pdp.eval;

import com.att.research.xacml.api.IdReferenceMatch;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPFinder;
import com.att.research.xacml.api.pip.PIPRequest;
import com.att.research.xacml.api.pip.PIPResponse;
import com.att.research.xacml.api.trace.TraceEngine;
import com.att.research.xacmlatt.pdp.policy.Policy;
import com.att.research.xacmlatt.pdp.policy.PolicyDef;
import com.att.research.xacmlatt.pdp.policy.PolicyFinderResult;
import com.att.research.xacmlatt.pdp.policy.PolicySet;

/**
 * EvaluationContext provides the interface that the PDP uses to evaluate its set of Policies and PolicySets against
 * a {@link com.att.research.xacml.api.Request}.
 *
 */
public interface EvaluationContext extends PIPFinder, TraceEngine {
    /**
     * Gets the original <code>Request</code> provided to the <code>ATTPDPEngine</code>'s <code>decide</code> method.
     *
     * @return the <code>Request</code> provided to the <code>ATTPDPEngine</code>'s <code>decide</code> method.
     */
    public Request getRequest();

    /**
     * Gets the root {@link com.att.research.xacmlatt.pdp.policy.PolicyDef} from the policy store
     * configured by the particular implementation of the <code>PolicyFinderFactory</code> class.
     *
     * @return a <code>PolicyFinderResult</code> with the root <code>PolicyDef</code>
     */
    public abstract PolicyFinderResult<PolicyDef> getRootPolicyDef();

    /**
     * Gets the {@link com.att.research.xacmlatt.pdp.policy.Policy} that matches the given {@link com.att.research.xacml.api.IdReferenceMatch}.
     *
     * @param idReferenceMatch the <code>IdReferenceMatch</code> to search for
     * @return a <code>PolicyFinderResult</code> with the <code>Policy</code> matching the given <code>IdReferenceMatch</code>
     */
    public abstract PolicyFinderResult<Policy> getPolicy(IdReferenceMatch idReferenceMatch);

    /**
     * Gets the {@link com.att.research.xacmlatt.pdp.policy.PolicySet} that matches the given {@link com.att.research.xacml.api.IdReferenceMatch}.
     *
     * @param idReferenceMatch the <code>IdReferenceMatch</code> to search for
     * @return a <code>PolicyFinderResult</code> with the <code>PolicySet</code> matching the given <code>IdReferenceMatch</code>.
     */
    public abstract PolicyFinderResult<PolicySet> getPolicySet(IdReferenceMatch idReferenceMatch);

    /**
     * Gets the {@link com.att.research.xacml.api.pip.PIPResponse} containing {@link com.att.research.xacml.api.Attribute}s that
     * match the given {@link com.att.research.xacml.api.pip.PIPRequest} from this <code>EvaluationContext</code>.
     *
     * @param pipRequest the <code>PIPRequest</code> specifying which <code>Attribute</code>s to retrieve
     * @return the <code>PIPResponse</code> containing the {@link com.att.research.xacml.api.Status} and <code>Attribute</code>s
     * @throws EvaluationException if there is an error retrieving the <code>Attribute</code>s
     */
    public PIPResponse getAttributes(PIPRequest pipRequest) throws PIPException;
}
