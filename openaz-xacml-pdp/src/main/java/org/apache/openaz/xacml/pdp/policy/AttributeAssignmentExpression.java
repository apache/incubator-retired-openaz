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
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.std.StdMutableAttributeAssignment;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * AttributeAssignmentExpression extends {@link PolicyComponent} to represent a XACML
 * AttributeAssignmentExpression element.
 */
public class AttributeAssignmentExpression extends PolicyComponent {
    private static final AttributeAssignmentResult AAR_NULL_EXPRESSION = new AttributeAssignmentResult(
                                                                                                       new StdStatus(
                                                                                                                     StdStatusCode.STATUS_CODE_SYNTAX_ERROR,
                                                                                                                     "Null expression"));
    private static final AttributeAssignmentResult AAR_NULL_EXPRESSION_RESULT = new AttributeAssignmentResult(
                                                                                                              new StdStatus(
                                                                                                                            StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                                                                            "Null expression result"));

    private Expression expression;
    private Identifier attributeId;
    private Identifier category;
    private String issuer;

    public AttributeAssignmentExpression(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public AttributeAssignmentExpression(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public AttributeAssignmentExpression() {
    }

    public AttributeAssignmentExpression(Identifier categoryIn, Identifier attributeIdIn, String issuerIn,
                                         Expression expressionIn) {
        this.category = categoryIn;
        this.attributeId = attributeIdIn;
        this.issuer = issuerIn;
        this.expression = expressionIn;
    }

    public Identifier getCategory() {
        return this.category;
    }

    public void setCategory(Identifier identifier) {
        this.category = identifier;
    }

    public Identifier getAttributeId() {
        return this.attributeId;
    }

    public void setAttributeId(Identifier identifier) {
        this.attributeId = identifier;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public void setIssuer(String string) {
        this.issuer = string;
    }

    public Expression getExpression() {
        return this.expression;
    }

    public void setExpression(Expression expressionIn) {
        this.expression = expressionIn;
    }

    public AttributeAssignmentResult evaluate(EvaluationContext evaluationContext,
                                              PolicyDefaults policyDefaults) throws EvaluationException {
        if (!this.validate()) {
            return new AttributeAssignmentResult(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }

        Expression thisExpression = this.getExpression();
        if (thisExpression == null) {
            return AAR_NULL_EXPRESSION;
        }

        ExpressionResult thisExpressionResult = thisExpression.evaluate(evaluationContext, policyDefaults);
        if (thisExpressionResult == null) {
            return AAR_NULL_EXPRESSION_RESULT;
        } else if (!thisExpressionResult.isOk()) {
            return new AttributeAssignmentResult(thisExpressionResult.getStatus());
        } else {
            List<AttributeAssignment> listAttributeAssignments = new ArrayList<AttributeAssignment>();
            if (thisExpressionResult.isBag()) {
                Bag bagValues = thisExpressionResult.getBag();
                if (bagValues == null || bagValues.size() == 0) {
                    listAttributeAssignments.add(new StdMutableAttributeAssignment(this.getCategory(), this
                        .getAttributeId(), this.getIssuer(), null));
                } else {
                    Iterator<AttributeValue<?>> iterBagValues = bagValues.getAttributeValues();
                    while (iterBagValues.hasNext()) {
                        AttributeValue<?> attributeValue = iterBagValues.next();
                        listAttributeAssignments.add(new StdMutableAttributeAssignment(this.getCategory(),
                                                                                       this.getAttributeId(),
                                                                                       this.getIssuer(),
                                                                                       attributeValue));
                    }
                }
            } else {
                listAttributeAssignments.add(new StdMutableAttributeAssignment(this.getCategory(), this
                    .getAttributeId(), this.getIssuer(), thisExpressionResult.getValue()));
            }
            return new AttributeAssignmentResult(listAttributeAssignments);
        }
    }

    @Override
    protected boolean validateComponent() {
        if (this.getAttributeId() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing AttributeId");
            return false;
        } else if (this.getExpression() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing Expression");
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
        if ((objectToDump = this.getCategory()) != null) {
            stringBuilder.append(",category=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getAttributeId()) != null) {
            stringBuilder.append(",attributeId=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getExpression()) != null) {
            stringBuilder.append(",expression=");
            stringBuilder.append(objectToDump.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
