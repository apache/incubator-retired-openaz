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
package org.apache.openaz.xacml.pdp.std.functions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.api.XACML;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;
import org.apache.openaz.xacml.std.StdAttributeValue;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * FunctionDefinitionNumberTypeConversion extends {@link FunctionDefinitionHomogeneousSimple} to implement the
 * XACML predicates foc converting <code>Double</code> to <code>Integer</code> and vice versa. In the first
 * implementation of XACML we had separate files for each XACML Function. This release combines multiple
 * Functions in fewer files to minimize code duplication. This file supports the following XACML codes:
 * double-to-integer integer-to-double
 *
 * @param <O> the java class for the data type of the function Output
 * @param <I> the java class for the data type of the function Input argument
 */
public class FunctionDefinitionNumberTypeConversion<O extends Number, I extends Number> extends
    FunctionDefinitionHomogeneousSimple<O, I> {

    public FunctionDefinitionNumberTypeConversion(Identifier idIn, DataType<O> outputType, DataType<I> argType) {
        super(idIn, outputType, argType, 1);

    }

    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {
        List<I> convertedArguments = new ArrayList<I>();
        Status status = this.validateArguments(arguments, convertedArguments);

        /*
         * If the function arguments are not correct, just return an error status immediately
         */
        if (!status.getStatusCode().equals(StdStatusCode.STATUS_CODE_OK)) {
            return ExpressionResult.newError(getFunctionStatus(status));
        }

        /*
         * Numeric operations cannot be operated on generically in java, so we have to check the types and
         * handle separately. Whichever type the argument is, convert it to the other
         */
        ExpressionResult expressionResult;
        try {
            if (convertedArguments.get(0).getClass() == BigInteger.class) {
                AttributeValue<Double> doubleResult = new StdAttributeValue<Double>(
                                                                                    XACML.ID_DATATYPE_DOUBLE,
                                                                                    new Double(
                                                                                               ((BigInteger)convertedArguments
                                                                                                   .get(0))
                                                                                                   .toString()));
                expressionResult = ExpressionResult.newSingle(doubleResult);
            } else {
                AttributeValue<BigInteger> integerResult = new StdAttributeValue<BigInteger>(
                                                                                             XACML.ID_DATATYPE_INTEGER,
                                                                                             BigInteger
                                                                                                 .valueOf(((Double)convertedArguments
                                                                                                     .get(0))
                                                                                                     .intValue()));
                expressionResult = ExpressionResult.newSingle(integerResult);
            }
        } catch (Exception e) {
            String message = e.getMessage();
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, this
                .getShortFunctionId() + " " + message));
        }

        return expressionResult;
    }

}
