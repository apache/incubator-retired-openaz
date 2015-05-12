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
package com.att.research.xacmlatt.pdp.std.functions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.DataType;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.Status;
import com.att.research.xacml.api.XACML;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;

/**
 * FunctionDefinitionArithmetic extends {@link com.att.research.xacmlatt.pdp.std.functions.FunctionDefinitionHomogeneousSimple} to
 * implement the XACML Arithmetic predicates as functions taking one or two arguments of the same data type and returning a single value of the same type.
 *
 * In Java there is no way to do arithmetic operations generically, so we need to have individual code for each operation on each class within this class.
 *
 * In the first implementation of XACML we had separate files for each XACML Function.
 * This release combines multiple Functions in fewer files to minimize code duplication.
 * This file supports the following XACML codes:
 * 		integer-add
 * 		double-add
 * 		integer-subtract
 * 		double-subtract
 * 		integer-multiply
 * 		double-multiply
 * 		integer-divide
 * 		double-divide
 * 		integer-mod
 * 		integer-abs
 * 		double-abs
 * 		round
 * 		floor
 *
 *
 * @param <T> the java class for the data type of the function arguments
 */
public class FunctionDefinitionArithmetic<T extends Number> extends FunctionDefinitionHomogeneousSimple<T,T> {

    /**
     * List of arithmetic operations.
     *
     *
     */
    public enum OPERATION {ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD, ABS, ROUND, FLOOR };

    // operation to be used in this instance of the Arightmetic class
    private final OPERATION operation;

    // result variables used by all functions, one for each type
    private AttributeValue<BigInteger>	integerResult;
    private AttributeValue<Double>	doubleResult;

    /**
     * Constructor
     *
     * @param idIn
     * @param dataTypeArgsIn
     * @param op
     */
    public FunctionDefinitionArithmetic(Identifier idIn, DataType<T> dataTypeArgsIn, OPERATION op, int nArgs) {
        // for Arithmetic functions, the output type is the same as the input type (no mixing of Ints and Doubles!)
        super(idIn, dataTypeArgsIn, dataTypeArgsIn, nArgs);

        // save the operation to be used in this instance
        operation = op;
    }


    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {
        List<T> convertedArguments	= new ArrayList<T>();
        Status status				= this.validateArguments(arguments, convertedArguments);

        /*
         * If the function arguments are not correct, just return an error status immediately
         */
        if (!status.getStatusCode().equals(StdStatusCode.STATUS_CODE_OK)) {
            return ExpressionResult.newError(getFunctionStatus(status));
        }

        /*
         * Now perform the requested operation.
         */
        ExpressionResult expressionResult = null;

        try {
            switch (operation) {
            case ADD:
                if (this.getDataType() == DataTypes.DT_INTEGER) {
                    integerResult	= new StdAttributeValue<BigInteger>(XACML.ID_DATATYPE_INTEGER, ((BigInteger)convertedArguments.get(0)).add( (BigInteger)convertedArguments.get(1)) );
                    expressionResult = ExpressionResult.newSingle(integerResult);
                } else {
                    doubleResult	= new StdAttributeValue<Double>(XACML.ID_DATATYPE_DOUBLE, (Double)convertedArguments.get(0) + (Double)convertedArguments.get(1));
                    expressionResult = ExpressionResult.newSingle(doubleResult);
                }
                break;
            case SUBTRACT:
                if (this.getDataType() == DataTypes.DT_INTEGER) {
                    integerResult	= new StdAttributeValue<BigInteger>(XACML.ID_DATATYPE_INTEGER, ((BigInteger)convertedArguments.get(0)).subtract( (BigInteger)convertedArguments.get(1)) );
                    expressionResult = ExpressionResult.newSingle(integerResult);
                } else {
                    doubleResult	= new StdAttributeValue<Double>(XACML.ID_DATATYPE_DOUBLE, (Double)convertedArguments.get(0) - (Double)convertedArguments.get(1));
                    expressionResult = ExpressionResult.newSingle(doubleResult);
                }
                break;
            case MULTIPLY:
                if (this.getDataType() == DataTypes.DT_INTEGER) {
                    integerResult	= new StdAttributeValue<BigInteger>(XACML.ID_DATATYPE_INTEGER, ((BigInteger)convertedArguments.get(0)).multiply((BigInteger)convertedArguments.get(1)) );
                    expressionResult = ExpressionResult.newSingle(integerResult);
                } else {
                    doubleResult	= new StdAttributeValue<Double>(XACML.ID_DATATYPE_DOUBLE, (Double)convertedArguments.get(0) * (Double)convertedArguments.get(1));
                    expressionResult = ExpressionResult.newSingle(doubleResult);
                }
                break;
            case DIVIDE:
                if (this.getDataType() == DataTypes.DT_INTEGER) {
                    if ( ((BigInteger)convertedArguments.get(1)).equals(new BigInteger("0")) ) {
                        return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, this.getShortFunctionId() +" Divide by 0 error: "+
                                                         arguments.get(0).getValue().getValue().toString() + ", " + arguments.get(1).getValue().getValue().toString()));
                    }
                    integerResult	= new StdAttributeValue<BigInteger>(XACML.ID_DATATYPE_INTEGER, ((BigInteger)convertedArguments.get(0)).divide((BigInteger)convertedArguments.get(1)) );
                    expressionResult = ExpressionResult.newSingle(integerResult);
                } else {
                    if ((Double)convertedArguments.get(1) == 0) {
                        return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, this.getShortFunctionId() +" Divide by 0 error: "+
                                                         arguments.get(0).getValue().getValue().toString() + ", " + arguments.get(1).getValue().getValue().toString()));
                    }
                    doubleResult	= new StdAttributeValue<Double>(XACML.ID_DATATYPE_DOUBLE, (Double)convertedArguments.get(0) / (Double)convertedArguments.get(1));
                    expressionResult = ExpressionResult.newSingle(doubleResult);
                }
                break;
            case MOD:
                if ( ((BigInteger)convertedArguments.get(1)).equals(new BigInteger("0")) ) {
                    return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, this.getShortFunctionId() +" Divide by 0 error: "+
                                                     arguments.get(0).getValue().getValue().toString() + ", " + arguments.get(1).getValue().getValue().toString()));
                }
                integerResult	= new StdAttributeValue<BigInteger>(XACML.ID_DATATYPE_INTEGER, ((BigInteger)convertedArguments.get(0)).remainder((BigInteger)convertedArguments.get(1)) );
                expressionResult = ExpressionResult.newSingle(integerResult);
                break;
            case ABS:
                if (this.getDataType() == DataTypes.DT_INTEGER) {
                    integerResult	= new StdAttributeValue<BigInteger>(XACML.ID_DATATYPE_INTEGER, ((BigInteger)convertedArguments.get(0)).abs() );
                    expressionResult = ExpressionResult.newSingle(integerResult);
                } else {
                    doubleResult	= new StdAttributeValue<Double>(XACML.ID_DATATYPE_DOUBLE, Math.abs((Double)convertedArguments.get(0)));
                    expressionResult = ExpressionResult.newSingle(doubleResult);
                }
                break;
            case ROUND:
                doubleResult	= new StdAttributeValue<Double>(XACML.ID_DATATYPE_DOUBLE, (double)(Math.round((Double)convertedArguments.get(0))) );
                expressionResult = ExpressionResult.newSingle(doubleResult);
                break;
            case FLOOR:
                doubleResult	= new StdAttributeValue<Double>(XACML.ID_DATATYPE_DOUBLE, Math.floor((Double)convertedArguments.get(0)));
                expressionResult = ExpressionResult.newSingle(doubleResult);
                break;
            }
        } catch (Exception e) {
            String message = e.getMessage();
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            String args = arguments.get(0).getValue().toString();
            if (arguments.size() > 1) {
                args += ", " + arguments.get(1).getValue().toString();
            }
            expressionResult = ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, this.getShortFunctionId() + " " + message +
                               " args: " + args + " " + e.getMessage() ));
        }

        return expressionResult;
    }

}
