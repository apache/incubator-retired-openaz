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

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.policy.Expression;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.PolicyDefaults;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * AttributeValueExpression extends {@link org.apache.openaz.xacml.pdp.policy.Expression} to represent XACML
 * AttributeValue elements in an Expression context.
 */
public class AttributeValueExpression extends Expression {
    private AttributeValue<?> attributeValue;

    public AttributeValueExpression(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public AttributeValueExpression(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public AttributeValueExpression() {
    }

    public AttributeValueExpression(AttributeValue<?> attributeValueIn) {
        this.attributeValue = attributeValueIn;
    }

    public AttributeValue<?> getAttributeValue() {
        return this.attributeValue;
    }

    public void setAttributeValue(AttributeValue<?> attributeValueIn) {
        this.attributeValue = attributeValueIn;
    }

    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, PolicyDefaults policyDefaults)
        throws EvaluationException {
        if (!this.validate()) {
            return ExpressionResult.newError(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }

        return ExpressionResult.newSingle(this.getAttributeValue());
    }

    @Override
    protected boolean validateComponent() {
        if (this.getAttributeValue() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing AttributeValue");
            return false;
        } else {
            this.setStatus(StdStatusCode.STATUS_CODE_OK, null);
            return true;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        Object objectToDump;
        if ((objectToDump = this.getAttributeValue()) != null) {
            stringBuilder.append("attributeValue=");
            stringBuilder.append(objectToDump.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
