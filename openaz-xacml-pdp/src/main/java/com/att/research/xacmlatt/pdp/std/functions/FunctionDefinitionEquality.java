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
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;

/**
 * FunctionDefinitionEquality extends {@link com.att.research.xacmlatt.pdp.std.functions.FunctionDefinitionHomogeneousSimple} to
 * implement the XACML Equality predicates as functions taking two arguments of the same data type and returning a <code>Boolean</code>.
 *
 * In the first implementation of XACML we had separate files for each XACML Function.
 * This release combines multiple Functions in fewer files to minimize code duplication.
 * This file supports the following XACML codes:
 *              string-equal
 *              boolean-equal
 *              integer-equal
 *              double-equal
 *              date-equal
 *              time-equal
 *              dateTime-equal
 *              dayTimeDuration-equal
 *              yearMonthDuration-equal
 *              anyURI-equal
 *
 *
 * @param <I> the java class for the data type of the function Input arguments
 */
public class FunctionDefinitionEquality<I> extends FunctionDefinitionHomogeneousSimple<Boolean, I> {

    /**
     * Determines if the two <code>T</code> values are equal using the java <code>equals</code> method.  Derived classes
     * may override this if the <code>equals</code> method is not sufficient.
     *
     * @param v1 the first object to compare
     * @param v2 the second object to compare
     * @return true if the two objects are the same, else false
     */
    protected boolean isEqual(I v1, I v2) {
        return v1.equals(v2);
    }

    public FunctionDefinitionEquality(Identifier idIn, DataType<I> dataTypeArgsIn) {
        super(idIn, DataTypes.DT_BOOLEAN, dataTypeArgsIn, 2);
    }

    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {
        List<I> convertedArguments      = new ArrayList<I>();
        Status status                           = this.validateArguments(arguments, convertedArguments);

        /*
         * If the function arguments are not correct, just return an error status immediately
         */
        if (!status.getStatusCode().equals(StdStatusCode.STATUS_CODE_OK)) {
            return ExpressionResult.newError(getFunctionStatus(status));
        }

        /*
         * Now just perform the equality operation.
         */
        if (this.isEqual(convertedArguments.get(0), convertedArguments.get(1))) {
            return ER_TRUE;
        } else {
            return ER_FALSE;
        }
    }

}
