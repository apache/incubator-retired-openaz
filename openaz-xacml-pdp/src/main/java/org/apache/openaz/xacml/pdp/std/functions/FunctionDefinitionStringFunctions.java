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
import java.util.List;

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;
import org.apache.openaz.xacml.std.StdAttributeValue;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.datatypes.DataTypes;

/**
 * FunctionDefinitionStringFunctions implements
 * {@link com.att.research.xacmlatt.pdp.policy.FunctionDefinition} to implement the XACML String Functions
 * predicates except for the conversions between <code>String</code> and <code>DataType</code> which are
 * contained in <code>FunctionDefinitionStringConversion</code>. The functions in this file do not have a lot
 * in common except that the return data type is known and the input argument types are either known or of the
 * generic type. In the first implementation of XACML we had separate files for each XACML Function. This
 * release combines multiple Functions in fewer files to minimize code duplication. This file supports the
 * following XACML codes: string-concatenate string-starts-with anyURI-starts-with string-ends-with
 * anyURI-ends-with string-contains anyURI-contains string-substring anyURI-substring
 *
 * @param <I> the java class for the data type of the function Input arguments
 * @param <O> the java class for the data type of the function Output - needed because different functions
 *            within this class have different output types
 */
public class FunctionDefinitionStringFunctions<O, I> extends FunctionDefinitionBase<O, I> {

    /**
     * List of String operations.
     */
    public enum OPERATION {
        CONCATENATE,
        STARTS_WITH,
        ENDS_WITH,
        CONTAINS,
        SUBSTRING
    };

    // operation to be used in this instance of the StringFunctions class
    private final OPERATION operation;

    /**
     * Constructor - need dataTypeArgs input because of java Generic type-erasure during compilation.
     *
     * @param idIn
     * @param dataTypeArgsIn
     */
    public FunctionDefinitionStringFunctions(Identifier idIn, DataType<O> dataTypeIn,
                                             DataType<I> dataTypeArgsIn, OPERATION op) {
        super(idIn, dataTypeIn, dataTypeArgsIn, false);
        this.operation = op;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {

        if (arguments == null
            || operation == OPERATION.CONCATENATE && arguments.size() < 2
            || operation == OPERATION.SUBSTRING && arguments.size() != 3
            || operation != OPERATION.SUBSTRING && operation != OPERATION.CONCATENATE && arguments.size() != 2) {
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                           this.getShortFunctionId()
                                                               + " Expected "
                                                               + (operation == OPERATION.SUBSTRING
                                                                   ? 3 : operation == OPERATION.CONCATENATE
                                                                       ? "2 or more " : 2)
                                                               + " arguments, got "
                                                               + ((arguments == null) ? "null" : arguments
                                                                   .size())));
        }

        ExpressionResult expressionResult = null;

        String firstArgumentAsString = null;
        String secondArgumentAsString = null;

        Integer secondArgumentAsInteger = null;
        Integer thirdArgumentAsInteger = null;

        // most of the functions take 2 args, but SUBSTRING takes 3 AND concatenate takes 2 or more
        if (operation == OPERATION.CONCATENATE) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < arguments.size(); i++) {
                FunctionArgument functionArgument = arguments.get(i);
                ConvertedArgument<I> convertedArgument = new ConvertedArgument<I>(functionArgument,
                                                                                  this.getDataTypeArgs(),
                                                                                  false);
                if (!convertedArgument.isOk()) {
                    return ExpressionResult.newError(getFunctionStatus(convertedArgument.getStatus()));
                }
                try {
                    String argumentAsString = this.getDataTypeArgs().toStringValue(convertedArgument
                                                                                       .getValue());
                    builder.append(argumentAsString);
                } catch (DataTypeException e) {
                    String message = e.getMessage();
                    if (e.getCause() != null) {
                        message = e.getCause().getMessage();
                    }
                    return ExpressionResult
                        .newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, this
                            .getShortFunctionId() + " " + message));
                }
            }
            AttributeValue<String> stringResult = new StdAttributeValue<String>(XACML.ID_DATATYPE_STRING,
                                                                                builder.toString());
            expressionResult = ExpressionResult.newSingle(stringResult);
            return expressionResult;

        } else if (operation == OPERATION.SUBSTRING) {
            // first arg is of generic type
            FunctionArgument functionArgument = arguments.get(0);
            ConvertedArgument<I> convertedArgument0 = new ConvertedArgument<I>(functionArgument,
                                                                               this.getDataTypeArgs(), false);
            if (!convertedArgument0.isOk()) {
                return ExpressionResult.newError(getFunctionStatus(convertedArgument0.getStatus()));
            }
            try {
                firstArgumentAsString = this.getDataTypeArgs().toStringValue(convertedArgument0.getValue());
            } catch (DataTypeException e) {
                String message = e.getMessage();
                if (e.getCause() != null) {
                    message = e.getCause().getMessage();
                }
                return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                               this.getShortFunctionId() + " " + message));
            }

            functionArgument = arguments.get(1);
            ConvertedArgument<BigInteger> convertedArgumentInt = new ConvertedArgument<BigInteger>(
                                                                                                   functionArgument,
                                                                                                   DataTypes.DT_INTEGER,
                                                                                                   false);
            if (!convertedArgumentInt.isOk()) {
                return ExpressionResult.newError(getFunctionStatus(convertedArgumentInt.getStatus()));
            }
            secondArgumentAsInteger = convertedArgumentInt.getValue().intValue();
            if (secondArgumentAsInteger < 0 || secondArgumentAsInteger > firstArgumentAsString.length()) {
                return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                               this.getShortFunctionId() + " "
                                                                   + "Start point '"
                                                                   + secondArgumentAsInteger
                                                                   + "' out of range 0-"
                                                                   + firstArgumentAsString.length()
                                                                   + " for string='" + firstArgumentAsString
                                                                   + "'"));
            }

            functionArgument = arguments.get(2);
            convertedArgumentInt = new ConvertedArgument<BigInteger>(functionArgument, DataTypes.DT_INTEGER,
                                                                     false);
            if (!convertedArgumentInt.isOk()) {
                return ExpressionResult.newError(getFunctionStatus(convertedArgumentInt.getStatus()));
            }
            thirdArgumentAsInteger = convertedArgumentInt.getValue().intValue();
            // special case: -1 means "to end of string"
            if (thirdArgumentAsInteger < -1 || thirdArgumentAsInteger > firstArgumentAsString.length()) {
                return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                               this.getShortFunctionId() + " "
                                                                   + "End point '" + thirdArgumentAsInteger
                                                                   + "' out of range 0-"
                                                                   + firstArgumentAsString.length()
                                                                   + " for string='" + firstArgumentAsString
                                                                   + "'"));
            }
            if (thirdArgumentAsInteger != -1 && thirdArgumentAsInteger < secondArgumentAsInteger) {
                return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                               this.getShortFunctionId() + " "
                                                                   + "End point '" + thirdArgumentAsInteger
                                                                   + "' less than start point '"
                                                                   + secondArgumentAsString + "'"
                                                                   + " for string='" + firstArgumentAsString
                                                                   + "'"));
            }

        } else {
            // expect 2 args, one String and one of Generic type
            FunctionArgument functionArgument = arguments.get(0);
            ConvertedArgument<String> convertedArgument0 = new ConvertedArgument<String>(functionArgument,
                                                                                         DataTypes.DT_STRING,
                                                                                         false);
            if (!convertedArgument0.isOk()) {
                return ExpressionResult.newError(getFunctionStatus(convertedArgument0.getStatus()));
            }
            firstArgumentAsString = convertedArgument0.getValue();

            functionArgument = arguments.get(1);
            ConvertedArgument<I> convertedArgument1 = new ConvertedArgument<I>(functionArgument,
                                                                               this.getDataTypeArgs(), false);
            if (!convertedArgument1.isOk()) {
                return ExpressionResult.newError(getFunctionStatus(convertedArgument1.getStatus()));
            }
            try {
                secondArgumentAsString = this.getDataTypeArgs().toStringValue(convertedArgument1.getValue());
            } catch (DataTypeException e) {
                String message = e.getMessage();
                if (e.getCause() != null) {
                    message = e.getCause().getMessage();
                }
                return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                               this.getShortFunctionId() + " " + message
                                                                   + " " + message));
            }

        }

        // arguments are ready - do the operation

        switch (operation) {
        case STARTS_WITH:
            if (secondArgumentAsString.startsWith(firstArgumentAsString)) {
                return ER_TRUE;
            } else {
                return ER_FALSE;
            }

        case ENDS_WITH:
            if (secondArgumentAsString.endsWith(firstArgumentAsString)) {
                return ER_TRUE;
            } else {
                return ER_FALSE;
            }

        case CONTAINS:
            if (secondArgumentAsString.contains(firstArgumentAsString)) {
                return ER_TRUE;
            } else {
                return ER_FALSE;
            }

        case SUBSTRING:
            String substring = null;
            if (thirdArgumentAsInteger == -1) {
                // from start point to end of string
                substring = firstArgumentAsString.substring(secondArgumentAsInteger);
            } else {
                substring = firstArgumentAsString.substring(secondArgumentAsInteger, thirdArgumentAsInteger);
            }
            AttributeValue<String> stringResult = new StdAttributeValue<String>(XACML.ID_DATATYPE_STRING,
                                                                                substring);
            expressionResult = ExpressionResult.newSingle(stringResult);
            break;
        }

        return expressionResult;

    }

}
