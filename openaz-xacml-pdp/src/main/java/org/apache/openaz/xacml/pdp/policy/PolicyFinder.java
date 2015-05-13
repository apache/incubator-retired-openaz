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
package org.apache.openaz.xacml.pdp.policy;

import org.apache.openaz.xacml.api.IdReferenceMatch;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;

/**
 * PolicyFinder is the interface for objects that can locate XACML Policies and PolicySets by identifier and
 * contains the root Policy or Policy set. The interface is designed to allow for finders that can retrieve a
 * root policy from a repository based on matching a {@link org.apache.openaz.xacml.api.Request}.
 */
public interface PolicyFinder {
    /**
     * Gets the root {@link PolicyDef} from the policy store configured by the particular implementation of
     * the <code>PolicyFinderFactory</code> class that is applicable to the
     * {@link org.apache.openaz.xacml.api.Request} in the given
     * {@link org.apache.openaz.xacml.pdp.eval.EvaluationContext}.
     *
     * @return a <code>PolicyFinderResult</code> with the root <code>PolicyDef</code>
     */
    PolicyFinderResult<PolicyDef> getRootPolicyDef(EvaluationContext evaluationContext);

    /**
     * Gets the {@link Policy} that matches the given {@link org.apache.openaz.xacml.api.IdReferenceMatch}.
     *
     * @param idReferenceMatch the <code>IdReferenceMatch</code> to search for
     * @return a <code>PolicyFinderResult</code> with the <code>Policy</code> matching the given
     *         <code>IdReferenceMatch</code>
     */
    PolicyFinderResult<Policy> getPolicy(IdReferenceMatch idReferenceMatch);

    /**
     * Gets the {@link PolicySet} that matches the given {@link org.apache.openaz.xacml.api.IdReferenceMatch}.
     *
     * @param idReferenceMatch the <code>IdReferenceMatch</code> to search for
     * @return a <code>PolicyFinderResult</code> with the <code>PolicySet</code> matching the given
     *         <code>IdReferenceMatch</code>.
     */
    PolicyFinderResult<PolicySet> getPolicySet(IdReferenceMatch idReferenceMatch);

}
