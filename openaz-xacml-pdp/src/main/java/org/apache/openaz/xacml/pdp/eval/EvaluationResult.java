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

package org.apache.openaz.xacml.pdp.eval;

import java.util.Collection;

import org.apache.openaz.xacml.api.Advice;
import org.apache.openaz.xacml.api.AttributeCategory;
import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.IdReference;
import org.apache.openaz.xacml.api.Obligation;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.std.StdMutableResult;

/**
 * EvaluationResult extends {@link org.apache.openaz.xacml.std.StdMutableResult} with methods useful within a
 * PDP implementation
 */
public class EvaluationResult extends StdMutableResult {
    public EvaluationResult() {
        super();
    }

    public EvaluationResult(Decision decisionIn, Status statusIn) {
        super(decisionIn, statusIn);
    }

    public EvaluationResult(Status statusIn) {
        super(statusIn);
    }

    public EvaluationResult(Decision decisionIn) {
        super(decisionIn);
    }

    public EvaluationResult(Decision decisionIn, Collection<Obligation> obligationsIn,
                            Collection<Advice> adviceIn, Collection<AttributeCategory> attributesIn,
                            Collection<IdReference> policyIdentifiersIn,
                            Collection<IdReference> policySetIdentifiersIn) {
        super(decisionIn, obligationsIn, adviceIn, attributesIn, policyIdentifiersIn, policySetIdentifiersIn);
    }

    /**
     * Creates an <code>EvaluationResult</code> generally from a
     * {@link org.apache.openaz.xacml.pdp.policy.Rule} <code>evaluation</code> call.
     *
     * @param decisionIn the <code>Decision</code>
     * @param obligationsIn the <code>Collection</code> of <code>Obligation</code>s
     * @param adviceIn the <code>Collection</code> of <code>Advice</code> objects
     */
    public EvaluationResult(Decision decisionIn, Collection<Obligation> obligationsIn,
                            Collection<Advice> adviceIn) {
        super(decisionIn, obligationsIn, adviceIn, null, null, null);
    }

    public void merge(EvaluationResult evaluationResult) {
        if (this.getStatus() == null) {
            this.setStatus(evaluationResult.getStatus());
        } else {
            this.getStatus().merge(evaluationResult.getStatus());
        }
        this.addObligations(evaluationResult.getObligations());
        this.addAdvice(evaluationResult.getAssociatedAdvice());
        this.addAttributeCategories(evaluationResult.getAttributes());
        this.addPolicyIdentifiers(evaluationResult.getPolicyIdentifiers());
        this.addPolicySetIdentifiers(evaluationResult.getPolicySetIdentifiers());
    }
}
