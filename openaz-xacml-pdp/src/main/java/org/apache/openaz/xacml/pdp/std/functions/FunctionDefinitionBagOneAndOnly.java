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

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.policy.Bag;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * FunctionDefinitionBagOneAndOnly implements {@link org.apache.openaz.xacml.pdp.policy.FunctionDefinition} to
 * implement the XACML 'type'-one-and-only predicates as functions taking one <code>Bag</code> argument and returning the single element in that bag of the 'type'.
 *
 * In the first implementation of XACML we had separate files for each XACML Function.
 * This release combines multiple Functions in fewer files to minimize code duplication.
 * This file supports the following XACML codes:
 * 		string-one-and-only
 * 		boolean-one-and-only
 * 		integer-one-and-only
 * 		double-one-and-only
 * 		time-one-and-only
 * 		date-one-and-only
 * 		dateTime-one-and-only
 * 		anyURI-one-and-only
 * 		hexBinary-one-and-only
 * 		base64Binary-one-and-only
 * 		dayTimeDuration-one-and-only (version 1 and3)
 * 		yearMonthDuration-one-and-only (version 1 and 3)
 * 		x500Name-one-and-only
 * 		rfc822Name-one-and-only
 * 		ipAddress-one-and-only
 * 		dnsName-one-and-only
 *
 *
 *
 * @param <I> the java class for the data type of the elements in the bag handed to this as the Input argument,
 * 	which is also the type of the return value
 *
 */
public class FunctionDefinitionBagOneAndOnly<I> extends FunctionDefinitionBase<I,I> {


    /**
     * Constructor - need dataType input because of java Generic type-erasure during compilation.
     *
     * @param idIn
     * @param dataTypeArgsIn
     */
    public FunctionDefinitionBagOneAndOnly(Identifier idIn, DataType<I> dataTypeArgsIn) {
        super(idIn, dataTypeArgsIn, dataTypeArgsIn, false);
    }

    /**
     * Evaluates this <code>FunctionDefinition</code> on the given <code>List</code> of{@link org.apache.openaz.xacml.pdp.policy.FunctionArgument}s.
     *
     * @param evaluationContext the {@link org.apache.openaz.xacml.pdp.eval.EvaluationContext} to use in the evaluation
     * @param arguments the <code>List</code> of <code>FunctionArgument</code>s for the evaluation
     * @return an {@link org.apache.openaz.xacml.pdp.policy.ExpressionResult} with the results of the call
     */
    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {

        if (arguments == null || arguments.size() != 1) {
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, getShortFunctionId() + " Expected 1 argument, got " +
                                             ((arguments == null) ? "null" : arguments.size()) ));
        }

        FunctionArgument argument = arguments.get(0);
        ConvertedArgument<Bag> convertedArgument = new ConvertedArgument<Bag>(argument, null, true);

        if ( ! convertedArgument.isOk()) {
            return ExpressionResult.newError(getFunctionStatus(convertedArgument.getStatus()));
        }

        Bag bag = convertedArgument.getBag();

        if (bag.size() != 1) {
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, getShortFunctionId() +
                                             " Expected 1 but Bag has " + bag.size() + " elements"));
        }

        // get the single value from the bag
        AttributeValue<?> attributeValueOneAndOnly	= bag.getAttributeValues().next();
        assert(attributeValueOneAndOnly != null);

        // make sure it has the right type
        //
        if (!this.getDataTypeId().equals(attributeValueOneAndOnly.getDataTypeId())) {
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, getShortFunctionId() +
                                             " Element in bag of wrong type. Expected " +
                                             this.getShortDataTypeId(this.getDataTypeId()) + " got " + this.getShortDataTypeId(attributeValueOneAndOnly.getDataTypeId())));
        }
        return ExpressionResult.newSingle(attributeValueOneAndOnly);
    }





}
