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

import org.apache.openaz.xacml.api.Advice;
import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.std.StdMutableAdvice;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.util.StringUtils;

/**
 * AdviceExpression extends {@link PolicyComponent} to implement the XACML AdviceExpression element.
 */
public class AdviceExpression extends PolicyComponent {
    private List<AttributeAssignmentExpression> listAttributeAssignmentExpressions = new ArrayList<AttributeAssignmentExpression>();
    private Identifier adviceId;
    private RuleEffect appliesTo;

    protected List<AttributeAssignmentExpression> getAttributeAssignmentExpressionList() {
        return this.listAttributeAssignmentExpressions;
    }

    protected void clearAttributeAssignmentExpressionList() {
        this.getAttributeAssignmentExpressionList().clear();
    }

    public AdviceExpression(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public AdviceExpression(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public AdviceExpression() {
    }

    public AdviceExpression(Identifier adviceIdIn, RuleEffect ruleEffectIn,
                            Collection<AttributeAssignmentExpression> attributeAssignmentExpressions) {
        this.adviceId = adviceIdIn;
        this.appliesTo = ruleEffectIn;
        if (attributeAssignmentExpressions != null) {
            this.listAttributeAssignmentExpressions.addAll(attributeAssignmentExpressions);
        }
    }

    public Identifier getAdviceId() {
        return this.adviceId;
    }

    public void setAdviceId(Identifier identifier) {
        this.adviceId = identifier;
    }

    public RuleEffect getAppliesTo() {
        return this.appliesTo;
    }

    public void setAppliesTo(RuleEffect ruleEffect) {
        this.appliesTo = ruleEffect;
    }

    public Iterator<AttributeAssignmentExpression> getAttributeAssignmentExpressions() {
        return this.getAttributeAssignmentExpressionList().iterator();
    }

    public void setAttributeAssignmentExpressions(Collection<AttributeAssignmentExpression> attributeAssignmentExpressions) {
        this.clearAttributeAssignmentExpressionList();
    }

    public void addAttributeAssignmentExpression(AttributeAssignmentExpression attributeAssignmentExpression) {
        this.getAttributeAssignmentExpressionList().add(attributeAssignmentExpression);
    }

    public void addAttributeAssignmentExpressions(Collection<AttributeAssignmentExpression> attributeAssignmentExpressions) {
        this.getAttributeAssignmentExpressionList().addAll(attributeAssignmentExpressions);
    }

    /**
     * Evaluates the <code>AttributeAssignmentExpression</code>s in this <code>AdviceExpression</code> to
     * generate an {@link org.apache.openaz.xacml.api.Advice} object.
     *
     * @param evaluationContext the {@link org.apache.openaz.xacml.pdp.eval.EvaluationContext} in which to
     *            evaluate the <code>AttributeAssignmentExpression</code>s
     * @param policyDefaults the {@link PolicyDefaults} for the evaluation
     * @return a new <code>Advice</code> evaluated from this <code>AdviceExpression</code>
     * @throws org.apache.openaz.xacml.pdp.eval.EvaluationException if there is an error in the evaluation
     */
    public Advice evaluate(EvaluationContext evaluationContext, PolicyDefaults policyDefaults)
        throws EvaluationException {
        if (!this.validate()) {
            return null;
        }

        List<AttributeAssignment> attributeAssignments = new ArrayList<AttributeAssignment>();
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
                        attributeAssignments.add(iterAttributeAssignments.next());
                    }
                }
            }
        }

        return new StdMutableAdvice(this.getAdviceId(), attributeAssignments);
    }

    /**
     * Evaluates a <code>Collection</code> of <code>AdviceExpression</code>s in the given
     * <code>EvaluationContext</code> and returns a <code>List</code> of <code>Advice</code>s.
     *
     * @param evaluationContext
     * @param policyDefaults
     * @param listAdviceExpressions
     * @return
     * @throws org.apache.openaz.xacml.pdp.eval.EvaluationException
     */
    public static List<Advice> evaluate(EvaluationContext evaluationContext, PolicyDefaults policyDefaults,
                                        Decision decision, Collection<AdviceExpression> listAdviceExpressions)
        throws EvaluationException {
        List<Advice> listAdvices = new ArrayList<Advice>();
        Iterator<AdviceExpression> iterAdviceExpressions = listAdviceExpressions.iterator();
        while (iterAdviceExpressions.hasNext()) {
            AdviceExpression adviceExpression = iterAdviceExpressions.next();
            adviceExpression.validateComponent();
            if (!adviceExpression.isOk()) {
                throw new EvaluationException(adviceExpression.getStatusMessage());
            }
            if (decision == null || adviceExpression.getAppliesTo().getDecision().equals(decision)) {
                Advice advice = adviceExpression.evaluate(evaluationContext, policyDefaults);
                if (advice != null) {
                    listAdvices.add(advice);
                }
            }
        }
        return listAdvices;
    }

    @Override
    protected boolean validateComponent() {
        if (this.getAdviceId() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing AdviceId");
            return false;
        } else if (this.getAppliesTo() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing AppliesTo");
            return false;
        } else {
            this.setStatus(StdStatusCode.STATUS_CODE_OK, null);
            return true;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        Object objectToDump;
        if ((objectToDump = this.getAdviceId()) != null) {
            stringBuilder.append(",adviceId=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getAppliesTo()) != null) {
            stringBuilder.append(",appliesTo=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = StringUtils.toString(this.getAttributeAssignmentExpressions())) != null) {
            stringBuilder.append(",attributeAssignmentExpressions=");
            stringBuilder.append((String)objectToDump);
        }

        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
