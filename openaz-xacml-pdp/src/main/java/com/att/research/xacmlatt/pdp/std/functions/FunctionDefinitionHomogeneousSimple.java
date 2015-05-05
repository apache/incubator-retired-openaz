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

import java.util.List;

import com.att.research.xacml.api.DataType;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.Status;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;

/**
 * FunctionDefinitionHomogeneousSimple extends
 * {@link com.att.research.xacmlatt.pdp.std.functions.FunctionDefinitionSimple} with utility methods for
 * ensuring the types of the arguments passed in the <code>evaluate</code> method matches the parameterized
 * type, and the number of arguments is correct. When evaluated the resulting arguments must be simple data
 * types, not bags. The various functions have the following needs with respect to their arguments:
 * <UL>
 * <LI>The argument list size is pre-defined and all arguments can be evaluated at once before the function is
 * called.
 * <LI>The argument list size is pre-defined but the arguments must be evaluated one at a time by the
 * function.
 * <LI>The argument list size is not pre-defined.
 * </UL>
 * To support those needs this class includes methods for checking the list size and evaluating a single
 * argument as well as combining those operations in a single method to make it simpler for the calling
 * function.
 *
 * @param <O> the java class for the value of the Output return result from the <code>FunctionDefinition</code>
 * @param <I> the java class for the value of the Input
 *            {@link com.att.research.xacmlatt.pdp.policy.FunctionArgument}s in the <code>evaluate</code>
 *            method
 */
public abstract class FunctionDefinitionHomogeneousSimple<O, I> extends FunctionDefinitionBase<O, I> {

    // null means that number of arguments is variable
    private Integer numArgs;

    /**
     * Constructor
     *
     * @param idIn
     * @param dataTypeReturnIn
     * @param dataTypeArgsIn
     * @param nArgs
     */
    public FunctionDefinitionHomogeneousSimple(Identifier idIn, DataType<O> dataTypeReturnIn,
                                               DataType<I> dataTypeArgsIn, Integer nArgs) {
        super(idIn, dataTypeReturnIn, dataTypeArgsIn, false);
        this.numArgs = nArgs;
    }

    /**
     * Gets the number of arguments expected to this <code>FunctionDefinition</code>. For functions without a
     * pre-defined number of arguments this is not used.
     *
     * @return the number of arguments expected to this <code>FunctionDefinition</code>.
     */
    public Integer getNumArgs() {
        return this.numArgs;
    }

    /**
     * Validates the given <code>List</code> of <code>FunctionArgument</code>s has the correct count and
     * <code>DataType</code> and evaluates expressions. This combines both the argument list length check and
     * the evaluation of all arguments on that list.
     *
     * @param listFunctionArguments the <code>List</code> of <code>FunctionArgument</code>s to validate
     * @return a {@link com.att.research.xacml.api.Status} indication with an error if the arguments are not
     *         valid
     */
    public Status validateArguments(List<FunctionArgument> listFunctionArguments, List<I> convertedValues) {
        /*
         * See if we have to validate the number of arguments
         */
        Status listLengthStatus = validateArgumentListLength(listFunctionArguments);
        if (!listLengthStatus.isOk()) {
            return listLengthStatus;
        }

        /*
         * Now validate the types of the arguments
         */
        for (int i = 0; i < listFunctionArguments.size(); i++) {
            FunctionArgument functionArgument = listFunctionArguments.get(i);
            ConvertedArgument<I> argument = new ConvertedArgument<I>(functionArgument, getDataTypeArgs(),
                                                                     false);
            if (!argument.isOk()) {
                // when a Status is returned that indicates an error, tell caller which arg had problem
                Status decoratedStatus = new StdStatus(argument.getStatus().getStatusCode(), argument
                    .getStatus().getStatusMessage() + " at arg index " + i);
                return decoratedStatus;
            }
            if (convertedValues != null) {
                convertedValues.add(argument.getValue());
            }
        }

        /*
         * Everything passed the data type test, so we are good to go
         */
        return StdStatus.STATUS_OK;
    }

    /**
     * Validates the given <code>List</code> of <code>FunctionArgument</code>s has the correct count.
     *
     * @param listFunctionArguments the <code>List</code> of <code>FunctionArgument</code>s to validate
     * @return a {@link com.att.research.xacml.api.Status} indication with an error if the arguments are not
     *         valid
     */
    public Status validateArgumentListLength(List<FunctionArgument> listFunctionArguments) {
        /*
         * See if we have to validate the number of arguments
         */
        if ((listFunctionArguments == null && this.numArgs > 0)
            || (listFunctionArguments != null && this.numArgs != listFunctionArguments.size())) {
            return new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                 "Expected " + this.numArgs + " arguments, got "
                                     + ((listFunctionArguments == null) ? 0 : listFunctionArguments.size()));
        }

        /*
         * Everything passed the data type test, so we are good to go
         */
        return StdStatus.STATUS_OK;
    }

}
