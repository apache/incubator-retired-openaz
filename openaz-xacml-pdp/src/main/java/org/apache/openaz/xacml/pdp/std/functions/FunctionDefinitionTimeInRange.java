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

import java.util.ArrayList;
import java.util.List;

import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.apache.openaz.xacml.std.datatypes.ISO8601Time;

/**
 * FunctionDefinitionTimeInRange implements {@link org.apache.openaz.xacml.pdp.policy.FunctionDefinition} to
 * implement the XACML time-in-range predicates as a function taking three arguments of type <code>Time</code>
 * and returning a <code>Boolean</code>. In the first implementation of XACML we had separate files for each
 * XACML Function. This release combines multiple Functions in fewer files to minimize code duplication. This
 * file supports the following XACML codes: time-in-range
 *
 * @param <I> the java class for the data type of the function Input arguments.
 */
public class FunctionDefinitionTimeInRange<I> extends FunctionDefinitionHomogeneousSimple<Boolean, I> {

    /**
     * Constructor - need dataType input because of java Generic type-erasure during compilation.
     *
     * @param idIn
     * @param dataTypeArgsIn
     */
    public FunctionDefinitionTimeInRange(Identifier idIn, DataType<I> dataTypeArgsIn) {
        super(idIn, DataTypes.DT_BOOLEAN, dataTypeArgsIn, 3);
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

        int compareResultLow;
        int compareResultHigh;
        try {

            compareResultLow = ((ISO8601Time)convertedArguments.get(1))
                .compareTo((ISO8601Time)convertedArguments.get(0));
            compareResultHigh = ((ISO8601Time)convertedArguments.get(2))
                .compareTo((ISO8601Time)convertedArguments.get(0));
        } catch (Exception e) {
            String message = e.getMessage();
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, this
                .getShortFunctionId() + " " + message));
        }

        // is arg 0 within the inclusive range of the other two?
        if (compareResultLow <= 0 && compareResultHigh >= 0) {
            return ER_TRUE;
        } else {
            return ER_FALSE;
        }
    }

}
