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

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * FunctionArgumentExpression implements the {@link FunctionArgument} interface for unevaluated
 * {@link Expression}s.
 */
public class FunctionArgumentExpression implements FunctionArgument {
    private static final Status STATUS_NULL_EXPRESSION_RESULT = new StdStatus(
                                                                              StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                              "Null expression result");

    private Expression expression;
    private EvaluationContext evaluationContext;
    private ExpressionResult expressionResult;
    private PolicyDefaults policyDefaults;

    protected ExpressionResult evaluateExpression() {
        if (this.getExpression() != null && this.getEvaluationContext() != null) {
            try {
                this.expressionResult = this.getExpression().evaluate(this.getEvaluationContext(),
                                                                      this.getPolicyDefaults());
            } catch (EvaluationException ex) {
                this.expressionResult = ExpressionResult
                    .newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, ex.getMessage()));
            }
        }
        return this.expressionResult;
    }

    public FunctionArgumentExpression() {
    }

    public FunctionArgumentExpression(Expression expressionIn, EvaluationContext evaluationContextIn,
                                      PolicyDefaults policyDefaultsIn) {
        this.expression = expressionIn;
        this.evaluationContext = evaluationContextIn;
        this.policyDefaults = policyDefaultsIn;
    }

    protected ExpressionResult getExpressionResult() {
        return this.expressionResult;
    }

    protected Expression getExpression() {
        return this.expression;
    }

    protected EvaluationContext getEvaluationContext() {
        return this.evaluationContext;
    }

    protected PolicyDefaults getPolicyDefaults() {

        return this.policyDefaults;
    }

    @Override
    public Status getStatus() {
        ExpressionResult thisExpressionResult = this.getExpressionResult();
        if (thisExpressionResult == null) {
            thisExpressionResult = this.evaluateExpression();
        }
        return (thisExpressionResult == null ? STATUS_NULL_EXPRESSION_RESULT : thisExpressionResult
            .getStatus());
    }

    @Override
    public boolean isOk() {
        Status thisStatus = this.getStatus();
        return (thisStatus == null ? true : thisStatus.isOk());
    }

    @Override
    public boolean isBag() {
        ExpressionResult thisExpressionResult = this.getExpressionResult();
        if (thisExpressionResult == null) {
            thisExpressionResult = this.evaluateExpression();
        }
        return (thisExpressionResult == null ? false : thisExpressionResult.isBag());
    }

    @Override
    public AttributeValue<?> getValue() {
        ExpressionResult thisExpressionResult = this.getExpressionResult();
        if (thisExpressionResult == null) {
            thisExpressionResult = this.evaluateExpression();
        }
        return (thisExpressionResult == null ? null : thisExpressionResult.getValue());
    }

    @Override
    public Bag getBag() {
        ExpressionResult thisExpressionResult = this.getExpressionResult();
        if (thisExpressionResult == null) {
            thisExpressionResult = this.evaluateExpression();
        }
        return (thisExpressionResult == null ? null : thisExpressionResult.getBag());
    }
}
