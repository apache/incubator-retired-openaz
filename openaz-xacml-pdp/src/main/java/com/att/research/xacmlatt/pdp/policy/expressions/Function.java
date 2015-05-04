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
package com.att.research.xacmlatt.pdp.policy.expressions;

import java.net.URI;

import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.StatusCode;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.eval.EvaluationException;
import com.att.research.xacmlatt.pdp.policy.Expression;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.PolicyDefaults;

/**
 * Function extends {@link com.att.research.xacmlatt.pdp.policy.Expression} to implement the XACML Function element.
 *
 */
public class Function extends Expression {
    private Identifier functionId;
    private AttributeValue<URI> attributeValue;
    private ExpressionResult expressionResultOk;

    protected ExpressionResult getExpressionResultOk() {
        if (this.expressionResultOk == null) {
            this.expressionResultOk	= ExpressionResult.newSingle(this.getAttributeValue());
        }
        return this.expressionResultOk;
    }

    public Function(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public Function(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public Function() {
    }

    public Function(Identifier functionIdIn) {
        this.functionId	= functionIdIn;
    }

    public Identifier getFunctionId() {
        return this.functionId;
    }

    public void setFunctionId(Identifier identifier) {
        this.functionId	= identifier;
        this.attributeValue	= null;
        this.expressionResultOk	= null;
    }

    public AttributeValue<URI> getAttributeValue() {
        if (this.attributeValue == null) {
            Identifier thisFunctionId	= this.getFunctionId();
            if (thisFunctionId != null) {
                try {
                    this.attributeValue	= DataTypes.DT_ANYURI.createAttributeValue(thisFunctionId);
                } catch (DataTypeException ex) {
                    this.attributeValue	= null;
                }
            }
        }
        return this.attributeValue;
    }

    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, PolicyDefaults policyDefaults) throws EvaluationException {
        if (!this.validate()) {
            return ExpressionResult.newError(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        } else {
            return this.getExpressionResultOk();
        }
    }

    @Override
    protected boolean validateComponent() {
        if (this.getFunctionId() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing FunctionId");
            return false;
        } else {
            this.setStatus(StdStatusCode.STATUS_CODE_OK, null);
            return true;
        }
    }

}
