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
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.datatypes.DataTypes;

/**
 * Condition extends {@link org.apache.openaz.xacml.pdp.policy.PolicyComponent} to represent the XACML
 * Condition element in a XACML Rule.
 */
public class Condition extends PolicyComponent {
    private static final Status STATUS_PE_RETURNED_BAG = new StdStatus(
                                                                       StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                       "Condition Expression returned a bag");
    private static final ExpressionResultBoolean ERB_RETURNED_BAG = new ExpressionResultBoolean(
                                                                                                STATUS_PE_RETURNED_BAG);
    private static final Status STATUS_PE_RETURNED_NULL = new StdStatus(
                                                                        StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                        "Null value from Condition Expression");
    private static final ExpressionResultBoolean ERB_RETURNED_NULL = new ExpressionResultBoolean(
                                                                                                 STATUS_PE_RETURNED_NULL);
    private static final Status STATUS_PE_RETURNED_NON_BOOLEAN = new StdStatus(
                                                                               StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                               "Non-boolean value from Condition Expression");
    private static final ExpressionResultBoolean ERB_RETURNED_NON_BOOLEAN = new ExpressionResultBoolean(
                                                                                                        STATUS_PE_RETURNED_NON_BOOLEAN);
    private static final Status STATUS_PE_INVALID_BOOLEAN = new StdStatus(
                                                                          StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                          "Invalid Boolean value");
    private static final ExpressionResultBoolean ERB_INVALID_BOOLEAN = new ExpressionResultBoolean(
                                                                                                   STATUS_PE_INVALID_BOOLEAN);

    private Expression expression;

    /**
     * Creates a <code>Condition</code> with the given {@link org.apache.openaz.xacml.api.StatusCode} and
     * <code>String</code> status message.
     *
     * @param statusCodeIn the <code>StatusCode</code> for the <code>Condition</code>
     * @param statusMessageIn the <code>String</code> status message for the <code>Condition</code>
     */
    public Condition(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    /**
     * Creates a <code>Condition</code> with the given <code>StatusCode</code. and a null status message.
     *
     * @param statusCodeIn the <code>StatusCode</code> for the <code>Condition</code>
     */
    public Condition(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    /**
     * Creates an empty <code>Condition</code>
     */
    public Condition() {
    }

    /**
     * Creates a new <code>Condition</code> with the given
     * {@link org.apache.openaz.xacml.pdp.policy.Expression} and a default OK <code>StatusCode</code>.
     *
     * @param expressionIn the <code>Expression</code> for the <code>Condition</code>
     */
    public Condition(Expression expressionIn) {
        this.expression = expressionIn;
    }

    public Expression getExpression() {
        return this.expression;
    }

    public void setExpression(Expression expressionIn) {
        this.expression = expressionIn;
    }

    @Override
    protected boolean validateComponent() {
        if (this.getExpression() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing Expression");
            return false;
        } else {
            this.setStatus(StdStatusCode.STATUS_CODE_OK, null);
            return true;
        }
    }

    /**
     * Evaluates the <code>Expression</code> in this <code>Condition</code> in the given
     * {@link org.apache.openaz.xacml.pdp.eval.EvaluationContext}. and validates that the result is a
     * boolean.
     *
     * @param evaluationContext the <code>EvaluationContext</code> in which to evaluate this
     *            <code>Expression</code>
     * @param policyDefaults the {@link org.apache.openaz.xacml.pdp.policy.PolicyDefaults} to use in evaluating
     *            this <code>Expression</code>
     * @return a {@link org.apache.openaz.xacml.pdp.policy.ExpressionResult}
     */
    public ExpressionResultBoolean evaluate(EvaluationContext evaluationContext, PolicyDefaults policyDefaults)
        throws EvaluationException {
        if (!this.validate()) {
            return new ExpressionResultBoolean(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }

        /*
         * Evaluate the expression
         */
        ExpressionResult expressionResult = this.getExpression().evaluate(evaluationContext, policyDefaults);
        assert expressionResult != null;

        if (!expressionResult.isOk()) {
            return new ExpressionResultBoolean(expressionResult.getStatus());
        }

        /*
         * Ensure the result is a single element of type boolean
         */
        if (expressionResult.isBag()) {
            return ERB_RETURNED_BAG;
        }
        AttributeValue<?> attributeValueResult = expressionResult.getValue();
        if (attributeValueResult == null) {
            return ERB_RETURNED_NULL;
        } else if (!DataTypes.DT_BOOLEAN.getId().equals(attributeValueResult.getDataTypeId())) {
            return ERB_RETURNED_NON_BOOLEAN;
        }

        /*
         * Otherwise it is a valid condition evaluation
         */
        Boolean booleanValue = null;
        try {
            booleanValue = DataTypes.DT_BOOLEAN.convert(attributeValueResult.getValue());
        } catch (DataTypeException ex) {
            return new ExpressionResultBoolean(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                             ex.getMessage()));
        }
        if (booleanValue == null) {
            return ERB_INVALID_BOOLEAN;
        } else {
            return (booleanValue.booleanValue()
                ? ExpressionResultBoolean.ERB_TRUE : ExpressionResultBoolean.ERB_FALSE);
        }
    }

}
