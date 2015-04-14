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

import java.util.Iterator;
import java.util.List;

import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.DataType;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.policy.Bag;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;

/**
 * FunctionDefinitionBagIsIn implements {@link com.att.research.xacmlatt.pdp.policy.FunctionDefinition} to
 * implement the XACML 'type'-is-in predicates as functions taking two arguments, the first of <code>type</code> and the second of type <code>Bag</code>,
 * and returning a <code>Boolean</code> for whether the first argument is contained in the second.
 * 
 * In the first implementation of XACML we had separate files for each XACML Function.
 * This release combines multiple Functions in fewer files to minimize code duplication.
 * This file supports the following XACML codes:
 * 		string-is-in
 * 		boolean-is-in
 * 		integer-is-in
 * 		double-is-in
 * 		time-is-in
 * 		date-is-in
 * 		dateTime-is-in
 * 		anyURI-is-in
 * 		hexBinary-is-in
 * 		base64Binary-is-in
 * 		dayTimeDuration-is-in (version 1 and3)
 * 		yearMonthDuration-is-in (version 1 and 3)
 * 		x500Name-is-in
 * 		rfc822Name-is-in
 * 		ipAddress-is-in
 * 		dnsName-is-in
 * 
 * 
 * @author glenngriffin
 * @version $Revision: 1.1 $
 * 
 * @param <I> the java class for the data type of the elements in the Input argument Bag
 * 
 * The Output for these functions is always a Boolean.
 */
public class FunctionDefinitionBagIsIn<I> extends FunctionDefinitionBase<Boolean, I> {

	
	/**
	 * Constructor - need dataType input because of java Generic type-erasure during compilation.
	 * 
	 * @param idIn
	 * @param dataTypeArgsIn
	 */
	public FunctionDefinitionBagIsIn(Identifier idIn, DataType<I> dataTypeArgsIn) {
		super(idIn, DataTypes.DT_BOOLEAN, dataTypeArgsIn, false);

	}

	/**
	 * Evaluates this <code>FunctionDefinition</code> on the given <code>List</code> of{@link com.att.research.xacmlatt.pdp.policy.FunctionArgument}s.
	 * 
	 * @param evaluationContext the {@link com.att.research.xacmlatt.pdp.eval.EvaluationContext} to use in the evaluation
	 * @param arguments the <code>List</code> of <code>FunctionArgument</code>s for the evaluation
	 * @return an {@link com.att.research.xacmlatt.pdp.policy.ExpressionResult} with the results of the call
	 */
	@Override
	public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {

		if (arguments == null || arguments.size() != 2) {
			return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, this.getShortFunctionId() + " Expected 2 arguments, got " + 
					((arguments == null) ? "null" : arguments.size()) ));
		}
		
		// get the thing to look for in the bag
		FunctionArgument elementArgument = arguments.get(0);

		ConvertedArgument<I> convertedTargetArgument = new ConvertedArgument<I>(elementArgument, this.getDataTypeArgs(), false);
		if ( ! convertedTargetArgument.isOk()) {
			return ExpressionResult.newError(getFunctionStatus(convertedTargetArgument.getStatus()));
		}
		
		// Special case: Most methods want the value contained in the AttributeValue object inside the FunctionArgument.
		// This one wants the AttributeValue itself.
		// We use the ConvertedArgument constructor to validate that the argument is ok, then use the AttributeValue
		// from the FunctionArgument.
		AttributeValue<?> attributeValueElement	= elementArgument.getValue();

		// now get the bag
		FunctionArgument bagArgument = arguments.get(1);
		ConvertedArgument<Bag> convertedBagArgument = new ConvertedArgument<Bag>(bagArgument, null, true);

		if ( ! convertedBagArgument.isOk()) {
			return ExpressionResult.newError(getFunctionStatus(convertedBagArgument.getStatus()));
		}
	
		Bag bag = convertedBagArgument.getBag();

		Iterator<AttributeValue<?>> iterBagContents	= bag.getAttributeValues();
		while (iterBagContents.hasNext()) {
			AttributeValue<?> attributeValueBagContents	= iterBagContents.next();
			
			/*
			 * Should we be checking the type of the bag contents and returning an error if the bag contents are not of the
			 * right type?  The spec does not say this, so we just use the AttributeValue.equals() method for now.
			 */
			if (attributeValueElement.equals(attributeValueBagContents)) {
				return ER_TRUE;
			}
		}
		
		return ER_FALSE;
	}

	
	

}
