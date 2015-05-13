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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Obligation;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.std.StdMutableObligation;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * ObligationExpression extends {@link PolicyComponent} to implement the XACML ObligationExpression element.
 */
public class ObligationExpression extends PolicyComponent {
    private Identifier obligationId;
    private RuleEffect ruleEffect;
    private List<AttributeAssignmentExpression> attributeAssignmentExpressions;

    protected List<AttributeAssignmentExpression> getAttributeAssignmentExpressionList(boolean bNoNull) {
        if (this.attributeAssignmentExpressions == null && bNoNull) {
            this.attributeAssignmentExpressions = new ArrayList<AttributeAssignmentExpression>();
        }
        return this.attributeAssignmentExpressions;
    }

    protected void clearAttributeAssignmentExpressions() {
        if (this.attributeAssignmentExpressions != null) {
            this.attributeAssignmentExpressions.clear();
        }
    }

    public ObligationExpression(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public ObligationExpression(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public ObligationExpression() {
    }

    public Identifier getObligationId() {
        return this.obligationId;
    }

    public void setObligationId(Identifier identifier) {
        this.obligationId = identifier;
    }

    public RuleEffect getRuleEffect() {
        return this.ruleEffect;
    }

    public void setRuleEffect(RuleEffect ruleEffectIn) {
        this.ruleEffect = ruleEffectIn;
    }

    public Iterator<AttributeAssignmentExpression> getAttributeAssignmentExpressions() {
        List<AttributeAssignmentExpression> listAttributeAssignmentExpressions = this
            .getAttributeAssignmentExpressionList(false);
        return (listAttributeAssignmentExpressions == null ? null : listAttributeAssignmentExpressions
            .iterator());
    }

    public void setAttributeAssignmentExpressions(Collection<AttributeAssignmentExpression> attributeAssignmentExpressionsIn) {
        this.clearAttributeAssignmentExpressions();
        if (attributeAssignmentExpressionsIn != null) {
            this.addAttributeAssignmentExpressions(attributeAssignmentExpressionsIn);
        }
    }

    public void addAttributeAssignmentExpression(AttributeAssignmentExpression attributeAssignmentExpression) {
        List<AttributeAssignmentExpression> listAttributeAssignmentExpressions = this
            .getAttributeAssignmentExpressionList(true);
        listAttributeAssignmentExpressions.add(attributeAssignmentExpression);
    }

    public void addAttributeAssignmentExpressions(Collection<AttributeAssignmentExpression> attributeAssignmentExpressionsIn) {
        List<AttributeAssignmentExpression> listAttributeAssignmentExpressions = this
            .getAttributeAssignmentExpressionList(true);
        listAttributeAssignmentExpressions.addAll(attributeAssignmentExpressionsIn);
    }

    /**
     * Evaluates this <code>ObligationExpression</code> in the given
     * {@link org.apache.openaz.xacml.pdp.eval.EvaluationContext} to get an
     * {@link org.apache.openaz.xacml.api.Obligation} to include in a PDP result.
     *
     * @param evaluationContext the <code>EvaluationContext</code> in which to evaluate this
     *            <code>ObligationExpression</code>
     * @param policyDefaults the <code>PolicyDefaults</code> to apply to the evaluation
     * @return a new <code>Obliagion</code> from this <code>ObligationExpression</code>
     * @throws org.apache.openaz.xacml.pdp.eval.EvaluationException if there is an error evaluating any of
     *             the <code>AttributeAssignmentExpression</code>s
     */
    public Obligation evaluate(EvaluationContext evaluationContext, PolicyDefaults policyDefaults)
        throws EvaluationException {
        if (!this.validate()) {
            return null;
        }
        List<AttributeAssignment> listAttributeAssignments = new ArrayList<AttributeAssignment>();
        Iterator<AttributeAssignmentExpression> iterAttributeAssignmentExpressions = this
            .getAttributeAssignmentExpressions();
        if (iterAttributeAssignmentExpressions != null) {
            while (iterAttributeAssignmentExpressions.hasNext()) {
                AttributeAssignmentResult attributeAssignmentResult = iterAttributeAssignmentExpressions
                    .next().evaluate(evaluationContext, policyDefaults);
                if (attributeAssignmentResult.isOk()
                    && attributeAssignmentResult.getNumAttributeAssignments() > 0) {
                    Iterator<AttributeAssignment> iterAttributeAssignments = attributeAssignmentResult
                        .getAttributeAssignments();
                    while (iterAttributeAssignments.hasNext()) {
                        listAttributeAssignments.add(iterAttributeAssignments.next());
                    }
                }
            }
        }
        return new StdMutableObligation(this.getObligationId(), listAttributeAssignments);
    }

    /**
     * Evaluates a <code>Collection</code> of <code>ObligationExpression</code>s in the given
     * <code>EvaluationContext</code> and returns a <code>List</code> of <code>Obligation</code>s.
     *
     * @param evaluationContext
     * @param policyDefaults
     * @param listObligationExpressions
     * @return
     * @throws org.apache.openaz.xacml.pdp.eval.EvaluationException
     */
    public static List<Obligation> evaluate(EvaluationContext evaluationContext,
                                            PolicyDefaults policyDefaults, Decision decision,
                                            Collection<ObligationExpression> listObligationExpressions)
        throws EvaluationException {
        List<Obligation> listObligations = new ArrayList<Obligation>();
        Iterator<ObligationExpression> iterObligationExpressions = listObligationExpressions.iterator();
        while (iterObligationExpressions.hasNext()) {
            ObligationExpression obligationExpression = iterObligationExpressions.next();
            obligationExpression.validateComponent();
            if (!obligationExpression.isOk()) {
                throw new EvaluationException(obligationExpression.getStatusMessage());
            }
            if (decision == null || obligationExpression.getRuleEffect().getDecision().equals(decision)) {
                Obligation obligation = obligationExpression.evaluate(evaluationContext, policyDefaults);
                if (obligation != null) {
                    listObligations.add(obligation);
                }
            }
        }
        return listObligations;
    }

    @Override
    protected boolean validateComponent() {
        if (this.getObligationId() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing ObligationId attribute");
            return false;
        } else if (this.getRuleEffect() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing FulfillOn attribute");
            return false;
        } else {
            this.setStatus(StdStatusCode.STATUS_CODE_OK, null);
            return true;
        }
    }

}
