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

import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.IdReferenceMatch;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.EvaluationResult;
import org.apache.openaz.xacml.pdp.eval.MatchResult;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * PolicyIdReferenceBase extends {@link PolicySetChild} to implement a XACML PolicyIdReference element.
 */
public abstract class PolicyIdReferenceBase<T extends PolicyDef> extends PolicySetChild {
    private IdReferenceMatch idReferenceMatch;
    private T referencee;

    @Override
    protected boolean validateComponent() {
        if (super.validateComponent()) {
            if (this.getIdReferenceMatch() == null) {
                this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing reference id");
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * If the <code>T</code> referencee has not been set, this method will try and find it in the given
     * <code>EvaluationContext</code> and return it.
     *
     * @param evaluationContext the <code>EvaluationContext</code> to search for the referencee
     * @return the <code>T</code> referencee if found, else null
     * @throws org.apache.openaz.xacml.pdp.eval.EvaluationException if there is an error attempting to
     *             locate the referenced <code>T</code>.
     */
    protected abstract T ensureReferencee(EvaluationContext evaluationContext) throws EvaluationException;

    public PolicyIdReferenceBase(PolicySet policySetParent, StatusCode statusCodeIn, String statusMessageIn) {
        super(policySetParent, statusCodeIn, statusMessageIn);
    }

    public PolicyIdReferenceBase(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public PolicyIdReferenceBase(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public PolicyIdReferenceBase(PolicySet policySetParent) {
        super(policySetParent);
    }

    public PolicyIdReferenceBase() {
    }

    /**
     * Gets the {@link org.apache.openaz.xacml.api.IdReferenceMatch} for this
     * <code>PolicyIdReferenceBase</code>.
     *
     * @return the <code>IdReferenceMatch</code> for this <code>PolicyIdReference</code>.
     */
    public IdReferenceMatch getIdReferenceMatch() {
        return this.idReferenceMatch;
    }

    public void setIdReferenceMatch(IdReferenceMatch idReferenceMatchIn) {
        this.idReferenceMatch = idReferenceMatchIn;
    }

    /**
     * Sets the <code>PolicyDef</code> object referred to by this <code>PolicyIdReferenceBase</code>.
     *
     * @return the <code>PolicyDef</code> object referred to by this <code>PolicyIdReferenceBase</code>
     */
    public T getReferencee() {
        return this.referencee;
    }

    public void setReferencee(T referenceeIn) {
        this.referencee = referenceeIn;
    }

    @Override
    public EvaluationResult evaluate(EvaluationContext evaluationContext) throws EvaluationException {
        T thisReferencee = this.ensureReferencee(evaluationContext);
        if (thisReferencee == null) {
            return new EvaluationResult(Decision.INDETERMINATE,
                                        new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                      "Could not find referencee for "
                                                          + this.getIdReferenceMatch().toString()));
        } else {
            return thisReferencee.evaluate(evaluationContext);
        }
    }

    @Override
    public MatchResult match(EvaluationContext evaluationContext) throws EvaluationException {
        T thisReferencee = this.ensureReferencee(evaluationContext);
        if (thisReferencee == null) {
            return new MatchResult(MatchResult.MatchCode.INDETERMINATE,
                                   new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                 "Could not find referencee for "
                                                     + this.getIdReferenceMatch().toString()));
        } else {
            return thisReferencee.match(evaluationContext);
        }
    }

}
