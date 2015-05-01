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

import java.util.ArrayList;
import java.util.List;

import com.att.research.xacml.api.DataType;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.Status;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;

/**
 * FunctionDefinitionNumberTypeConversion extends {@link com.att.research.xacmlatt.pdp.std.functions.FunctionDefinitionHomogeneousSimple} to
 * implement the XACML predicates for converting <code>String</code> to <code>DataType<?></code> and vice versa.
 *
 * In the first implementation of XACML we had separate files for each XACML Function.
 * This release combines multiple Functions in fewer files to minimize code duplication.
 * This file supports the following XACML codes:
 * 		boolean-from-string
 * 		string-from-boolean
 * 		integer-from-string
 * 		string-from-integer
 * 		double-from-string
 * 		string-from-double
 * 		time-from-string
 * 		string-from-time
 * 		date-from-string
 * 		string-from-date
 * 		dateTime-from-string
 * 		string-from-dateTime
 * 		anyURI-from-string
 * 		string-from-anyURI
 * 		dayTimeDuration-from-string
 * 		string-from-dayTimeDuration
 * 		yearMonthDuration-from-string
 * 		string-from-yearMonthDuration
 * 		x500Name-from-string
 * 		string-from-x500Name
 * 		rfc822Name-from-string
 * 		string-from-rfc822Name
 * 		ipAddress-from-string
 * 		string-from-ipAddress
 * 		dnsName-from-string
 * 		string-from-dnsName
 *
 * @author glenngriffin
 * @version $Revision: 1.1 $
 *
 * @param <O> the java class for the data type of the function Output
 * @param <I> the java class for the data type of the function Input argument
 */
public class FunctionDefinitionStringConversion<O,I> extends FunctionDefinitionHomogeneousSimple<O, I> {

    public FunctionDefinitionStringConversion(Identifier idIn, DataType<O> outputType, DataType<I> argType) {
        super(idIn, outputType, argType, 1);
    }

    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {
        List<I> convertedArguments	= new ArrayList<I>();
        Status status				= this.validateArguments(arguments, convertedArguments);

        /*
         * If the function arguments are not correct, just return an error status immediately
         */
        if (!status.getStatusCode().equals(StdStatusCode.STATUS_CODE_OK)) {
            return ExpressionResult.newError(getFunctionStatus(status));
        }

        /*
         * Do different conversion depending on which way we are going (to/from String)
         */
        if (this.getDataTypeId().equals(DataTypes.DT_STRING.getId())) {
            // converting TO String
            try {
                String output = this.getDataTypeArgs().toStringValue(convertedArguments.get(0));
                return ExpressionResult.newSingle(new StdAttributeValue<String>(this.getDataTypeId(), output));
            } catch (Exception e) {
                String message = e.getMessage();
                if (e.getCause() != null) {
                    message = e.getCause().getMessage();
                }
                // untested - not clear how this could happen
                return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, this.getShortFunctionId() +  " " + message));
            }
        } else {
            // converting FROM String to object of DataType
            try {
                O output = this.getDataType().convert(convertedArguments.get(0));
                return ExpressionResult.newSingle(new StdAttributeValue<O>(this.getDataTypeId(), output));
            } catch (Exception e) {
                String message = e.getMessage();
                if (e.getCause() != null) {
                    message = e.getCause().getMessage();
                }
                return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, this.getShortFunctionId() + " " + message ));
            }
        }

    }

}
