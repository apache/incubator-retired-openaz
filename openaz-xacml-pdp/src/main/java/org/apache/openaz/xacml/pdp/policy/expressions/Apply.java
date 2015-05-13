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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.policy.Expression;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;
import org.apache.openaz.xacml.pdp.policy.FunctionArgumentExpression;
import org.apache.openaz.xacml.pdp.policy.FunctionDefinition;
import org.apache.openaz.xacml.pdp.policy.FunctionDefinitionFactory;
import org.apache.openaz.xacml.pdp.policy.PolicyDefaults;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.util.FactoryException;

/**
 * Apply extends {@link org.apache.openaz.xacml.pdp.policy.Expression} to implement the XACML Apply
 * Expression element.
 */
public class Apply extends Expression {
    private Identifier functionId;
    private FunctionDefinition functionDefinition;
    private String description;
    private List<Expression> arguments = new ArrayList<Expression>();

    protected List<Expression> getArgumentList() {
        return this.arguments;
    }

    protected void clearArgumentList() {
        this.getArgumentList().clear();
    }

    public Apply(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public Apply(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public Apply() {
    }

    public Apply(Identifier functionIdIn, String descriptionIn, Collection<Expression> argumentsIn) {
        this.functionId = functionIdIn;
        this.description = descriptionIn;
        if (argumentsIn != null) {
            this.arguments.addAll(argumentsIn);
        }
    }

    public Identifier getFunctionId() {
        return this.functionId;
    }

    public void setFunctionId(Identifier identifier) {
        this.functionId = identifier;
        this.functionDefinition = null;
    }

    /**
     * Gets and caches the {@link org.apache.openaz.xacml.pdp.policy.FunctionDefinition} matching the
     * <code>Identifier</code> for the FunctionId in this <code>Apply</code>.
     *
     * @return the <code>FunctionDefinition</code> for the <code>Identifier</code> for the Function Id for
     *         this <code>Apply</code>
     */
    public FunctionDefinition getFunctionDefinition() {
        if (this.functionDefinition == null) {
            Identifier thisFunctionId = this.getFunctionId();
            if (thisFunctionId != null) {
                try {
                    this.functionDefinition = FunctionDefinitionFactory.newInstance()
                        .getFunctionDefinition(thisFunctionId);
                } catch (FactoryException ex) {
                    this.setStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                   "FactoryException getting FunctionDefinition");
                }
            }
        }
        return this.functionDefinition;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String string) {
        this.description = string;
    }

    public Iterator<Expression> getArguments() {
        return this.getArgumentList().iterator();
    }

    public void setArguments(Collection<Expression> listExpressions) {
        this.clearArgumentList();
        if (listExpressions != null) {
            this.addArguments(listExpressions);
        }
    }

    public void addArgument(Expression expression) {
        this.getArgumentList().add(expression);
    }

    public void addArguments(Collection<Expression> listExpressions) {
        this.getArgumentList().addAll(listExpressions);
    }

    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, PolicyDefaults policyDefaults)
        throws EvaluationException {
        if (!this.validate()) {
            return ExpressionResult.newError(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }

        /*
         * Get the FunctionDefinition
         */
        FunctionDefinition thisFunctionDefinition = this.getFunctionDefinition();
        if (thisFunctionDefinition == null) {
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                           "Unknown Function \""
                                                               + this.getFunctionId().toString() + "\""));
        }

        /*
         * Get all of the arguments and convert them into FunctionArgument objects.
         */
        List<FunctionArgument> listFunctionArguments = new ArrayList<FunctionArgument>();
        Iterator<Expression> iterExpressionArguments = this.getArguments();
        if (iterExpressionArguments != null) {
            while (iterExpressionArguments.hasNext()) {
                listFunctionArguments.add(new FunctionArgumentExpression(iterExpressionArguments.next(),
                                                                         evaluationContext, policyDefaults));
            }
        }

        /*
         * Apply the FunctionDefinition to the arguments
         */
        return thisFunctionDefinition.evaluate(evaluationContext, listFunctionArguments);
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
