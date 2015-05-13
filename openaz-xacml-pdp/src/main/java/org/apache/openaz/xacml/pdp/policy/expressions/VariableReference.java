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
package org.apache.openaz.xacml.pdp.policy.expressions;

import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.api.trace.Traceable;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.policy.Expression;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.Policy;
import org.apache.openaz.xacml.pdp.policy.PolicyDefaults;
import org.apache.openaz.xacml.pdp.policy.VariableDefinition;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.trace.StdTraceEvent;

/**
 * VariableReference extends {@link org.apache.openaz.xacml.pdp.policy.Expression} to implement the XACML
 * VariableReference element.
 */
public class VariableReference extends Expression implements Traceable {
    private static final ExpressionResult ER_SE_NO_EXPRESSION = ExpressionResult
        .newError(new StdStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR,
                                "Missing Expression for VariableDefinition"));

    private Policy policy;
    private String variableId;
    private VariableDefinition variableDefinition;

    protected VariableDefinition getVariableDefinition() {
        if (this.variableDefinition == null) {
            Policy thisPolicy = this.getPolicy();
            if (thisPolicy != null) {
                String thisVariableId = this.getVariableId();
                if (thisVariableId != null) {
                    this.variableDefinition = thisPolicy.getVariableDefinition(thisVariableId);
                }
            }
        }
        return this.variableDefinition;
    }

    public VariableReference(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public VariableReference(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public VariableReference() {
    }

    public VariableReference(Policy policyIn, String variableIdIn) {
        this.policy = policyIn;
        this.variableId = variableIdIn;
    }

    public Policy getPolicy() {
        return this.policy;
    }

    public void setPolicy(Policy policyIn) {
        this.policy = policyIn;
    }

    public String getVariableId() {
        return this.variableId;
    }

    public void setVariableId(String variableIdIn) {
        this.variableId = variableIdIn;
    }

    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, PolicyDefaults policyDefaults)
        throws EvaluationException {
        if (!this.validate()) {
            return ExpressionResult.newError(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }

        VariableDefinition variableDefinition = this.getVariableDefinition();
        if (variableDefinition == null) {
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                           "No VariableDefinition found for \""
                                                               + this.getVariableId() + "\""));
        }
        Expression expression = variableDefinition.getExpression();
        if (expression == null) {
            return ER_SE_NO_EXPRESSION;
        }

        ExpressionResult result = expression.evaluate(evaluationContext, policyDefaults);

        if (evaluationContext.isTracing()) {
            evaluationContext.trace(new StdTraceEvent<ExpressionResult>("Variable", this, result));
        }

        return result;
    }

    @Override
    protected boolean validateComponent() {
        if (this.getVariableId() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing VariableId");
            return false;
        } else if (this.getPolicy() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "VariableReference not in a Policy");
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

        String stringToDump;
        if ((stringToDump = this.getVariableId()) != null) {
            stringBuilder.append(",variableId=");
            stringBuilder.append(stringToDump);
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    public String getTraceId() {
        return this.variableId;
    }

    @Override
    public Traceable getCause() {
        return this.policy;
    }

}
