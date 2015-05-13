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

import java.util.List;

import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.policy.Bag;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;

/**
 * FunctionDefinitionBag implements {@link org.apache.openaz.xacml.pdp.policy.FunctionDefinition} to
 * implement the XACML 'type'-bag predicates as functions taking 0, 1 or multiple arguments of the same data
 * type and returning a <code>Bag</code>. In the first implementation of XACML we had separate files for each
 * XACML Function. This release combines multiple Functions in fewer files to minimize code duplication. This
 * file supports the following XACML codes: string-bag boolean-bag integer-bag double-bag time-bag date-bag
 * dateTime-bag anyURI-bag hexBinary-bag base64Binary-bag dayTimeDuration-bag (version 1 and3)
 * yearMonthDuration-bag (version 1 and 3) x500Name-bag rfc822Name-bag ipAddress-bag dnsName-bag
 *
 * @param <I> the java class for the data type of the function Input arguments, which is also the "type" of the
 *            returned bag
 */
public class FunctionDefinitionBag<I> extends FunctionDefinitionBase<I, I> {

    /**
     * Constructor - need dataType input because of java Generic type-erasure during compilation.
     *
     * @param idIn
     * @param dataTypeArgsIn
     */
    public FunctionDefinitionBag(Identifier idIn, DataType<I> dataTypeArgsIn) {
        super(idIn, dataTypeArgsIn, dataTypeArgsIn, true);
    }

    /**
     * Evaluates this <code>FunctionDefinition</code> on the given <code>List</code> of
     * {@link org.apache.openaz.xacml.pdp.policy.FunctionArgument}s.
     *
     * @param evaluationContext the {@link org.apache.openaz.xacml.pdp.eval.EvaluationContext} to use in the
     *            evaluation
     * @param arguments the <code>List</code> of <code>FunctionArgument</code>s for the evaluation
     * @return an {@link org.apache.openaz.xacml.pdp.policy.ExpressionResult} with the results of the call
     */
    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {

        // create a list to put the values into
        Bag elementBag = new Bag();

        // see if we have arguments
        if (arguments != null && arguments.size() > 0) {

            // for each arg, evaluate it, check type, and put on the list
            for (FunctionArgument argument : arguments) {
                // get the argument, evaluate it and check status
                ConvertedArgument<I> convertedArgument = new ConvertedArgument<I>(argument,
                                                                                  this.getDataTypeArgs(),
                                                                                  false);

                // check the status
                if (!convertedArgument.isOk()) {
                    return ExpressionResult.newError(getFunctionStatus(convertedArgument.getStatus()));
                }

                // Special case: Most methods want the value contained in the AttributeValue object inside the
                // FunctionArgument.
                // This one wants the AttributeValue itself.
                // We use the ConvertedArgument constructor to validate that the argument is ok, then use the
                // AttributeValue
                // from the FunctionArgument.
                elementBag.add(argument.getValue());
            }
        }

        // return it
        return ExpressionResult.newBag(elementBag);
    }

}
