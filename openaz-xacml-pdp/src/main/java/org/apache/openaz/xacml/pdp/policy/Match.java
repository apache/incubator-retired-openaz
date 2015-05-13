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

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.api.XACML;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.MatchResult;
import org.apache.openaz.xacml.pdp.eval.Matchable;
import org.apache.openaz.xacml.pdp.policy.expressions.AttributeRetrievalBase;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.apache.openaz.xacml.util.FactoryException;

/**
 * Match extends {@link org.apache.openaz.xacml.pdp.policy.PolicyComponent} and implements the
 * {@link org.apache.openaz.xacml.pdp.eval.Matchable} interface to represent a XACML Match element.
 */
public class Match extends PolicyComponent implements Matchable {
    private Identifier matchId;
    private AttributeValue<?> attributeValue;
    private AttributeRetrievalBase attributeRetrievalBase;
    private PolicyDefaults policyDefaults;
    private FunctionDefinition functionDefinition;

    protected FunctionDefinition getFunctionDefinition() {
        Identifier functionDefinitionId = this.getMatchId();
        if (this.functionDefinition == null && functionDefinitionId != null) {
            try {
                this.functionDefinition = FunctionDefinitionFactory.newInstance()
                    .getFunctionDefinition(functionDefinitionId);
            } catch (FactoryException ex) {
                this.setStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                               "FactoryException getting FunctionDefinition");
            }
        }
        return this.functionDefinition;
    }

    public Match(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public Match(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public Match() {
    }

    public Match(Identifier matchIdIn, AttributeValue<?> attributeValueIn,
                 AttributeRetrievalBase attributeRetrievalBaseIn, PolicyDefaults policyDefaultsIn) {
        this(StdStatusCode.STATUS_CODE_OK);
        this.matchId = matchIdIn;
        this.attributeValue = attributeValueIn;
        this.attributeRetrievalBase = attributeRetrievalBaseIn;
        this.policyDefaults = policyDefaultsIn;
    }

    public Identifier getMatchId() {
        return this.matchId;
    }

    public void setMatchId(Identifier matchIdIn) {
        this.matchId = matchIdIn;
    }

    public AttributeValue<?> getAttributeValue() {
        return this.attributeValue;
    }

    public void setAttributeValue(AttributeValue<?> attributeValueIn) {
        this.attributeValue = attributeValueIn;
    }

    public AttributeRetrievalBase getAttributeRetrievalBase() {
        return this.attributeRetrievalBase;
    }

    public void setAttributeRetrievalBase(AttributeRetrievalBase attributeRetrievalBaseIn) {
        this.attributeRetrievalBase = attributeRetrievalBaseIn;
    }

    public PolicyDefaults getPolicyDefaults() {
        return this.policyDefaults;
    }

    public void setPolicyDefaults(PolicyDefaults policyDefaultsIn) {
        this.policyDefaults = policyDefaultsIn;
    }

    private static MatchResult match(EvaluationContext evaluationContext,
                                     FunctionDefinition functionDefinition, FunctionArgument arg1,
                                     FunctionArgument arg2) throws EvaluationException {
        List<FunctionArgument> listArguments = new ArrayList<FunctionArgument>(2);
        listArguments.add(arg1);
        listArguments.add(arg2);

        ExpressionResult expressionResult = functionDefinition.evaluate(evaluationContext, listArguments);
        assert expressionResult != null;
        if (!expressionResult.isOk()) {
            return new MatchResult(expressionResult.getStatus());
        }

        AttributeValue<Boolean> attributeValueResult = null;
        try {
            attributeValueResult = DataTypes.DT_BOOLEAN.convertAttributeValue(expressionResult.getValue());
        } catch (DataTypeException ex) {
            return new MatchResult(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, ex.getMessage()));
        }
        if (attributeValueResult == null) {
            return new MatchResult(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                 "Non-boolean result from Match Function "
                                                     + functionDefinition.getId() + " on "
                                                     + expressionResult.getValue().toString()));
        } else if (attributeValueResult.getValue().booleanValue()) {
            return MatchResult.MM_MATCH;
        } else {
            return MatchResult.MM_NOMATCH;
        }

    }

    @Override
    public MatchResult match(EvaluationContext evaluationContext) throws EvaluationException {
        if (!this.validate()) {
            return new MatchResult(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }

        FunctionDefinition functionDefinitionMatch = this.getFunctionDefinition();
        assert functionDefinitionMatch != null;

        AttributeValue<?> attributeValue = this.getAttributeValue();
        assert attributeValue != null;
        FunctionArgument functionArgument1 = new FunctionArgumentAttributeValue(attributeValue);

        AttributeRetrievalBase attributeRetrievalBase = this.getAttributeRetrievalBase();
        assert attributeRetrievalBase != null;

        ExpressionResult expressionResult = attributeRetrievalBase.evaluate(evaluationContext,
                                                                            this.getPolicyDefaults());
        assert expressionResult != null;
        if (!expressionResult.isOk()) {
            return new MatchResult(expressionResult.getStatus());
        }

        if (expressionResult.isBag()) {
            MatchResult matchResult = MatchResult.MM_NOMATCH;
            Bag bagAttributeValues = expressionResult.getBag();
            if (bagAttributeValues != null) {
                Iterator<AttributeValue<?>> iterAttributeValues = bagAttributeValues.getAttributeValues();
                while (matchResult.getMatchCode() != MatchResult.MatchCode.MATCH
                       && iterAttributeValues.hasNext()) {
                    MatchResult matchResultValue = match(evaluationContext,
                                                         functionDefinitionMatch,
                                                         functionArgument1,
                                                         new FunctionArgumentAttributeValue(
                                                                                            iterAttributeValues
                                                                                                .next()));
                    switch (matchResultValue.getMatchCode()) {
                    case INDETERMINATE:
                        if (matchResult.getMatchCode() != MatchResult.MatchCode.INDETERMINATE) {
                            matchResult = matchResultValue;
                        }
                        break;
                    case MATCH:
                        matchResult = matchResultValue;
                        break;
                    case NOMATCH:
                        break;
                    }
                }
            }
            return matchResult;
        } else {
            /*
             * There is a single value, so add it as the second argument and do the one function evaluation
             */
            AttributeValue<?> attributeValueExpressionResult = expressionResult.getValue();
            if (attributeValueExpressionResult == null) {
                return new MatchResult(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                     "Null AttributeValue"));
            }

            return match(evaluationContext, functionDefinitionMatch, functionArgument1,
                         new FunctionArgumentAttributeValue(attributeValueExpressionResult));
        }
    }

    @Override
    protected boolean validateComponent() {
        FunctionDefinition functionDefinitionHere;
        if (this.getAttributeValue() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing AttributeValue");
            return false;
        } else if (this.getMatchId() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing MatchId");
            return false;
        } else if ((functionDefinitionHere = this.getFunctionDefinition()) == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Unknown MatchId \""
                                                                   + this.getMatchId().toString() + "\"");
            return false;
        } else if (functionDefinitionHere.returnsBag()) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "FunctionDefinition returns a bag");
            return false;
        } else if (functionDefinitionHere.getDataTypeId() == null
                   || !functionDefinitionHere.getDataTypeId().equals(XACML.ID_DATATYPE_BOOLEAN)) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR,
                           "Non-Boolean return type for FunctionDefinition");
            return false;
        } else if (this.getAttributeRetrievalBase() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR,
                           "Missing AttributeSelector or AttributeDesignator");
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
        if ((objectToDump = this.getMatchId()) != null) {
            stringBuilder.append(",matchId=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getAttributeValue()) != null) {
            stringBuilder.append(",attributeValue=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getAttributeRetrievalBase()) != null) {
            stringBuilder.append(",attributeRetrieval=");
            stringBuilder.append(objectToDump.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
