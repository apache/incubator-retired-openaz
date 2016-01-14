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

package org.apache.openaz.xacml.admin.util;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.admin.jpa.Datatype;
import org.apache.openaz.xacml.admin.jpa.FunctionArgument;
import org.apache.openaz.xacml.admin.jpa.FunctionDefinition;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.IdentifierImpl;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeSelectorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.FunctionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableDefinitionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableReferenceType;

public class XACMLFunctionValidator {
	private static Log logger	= LogFactory.getLog(XACMLFunctionValidator.class);
	
	public static boolean	validNumberOfArguments(ApplyType apply) {
		try {
			//
			// Sanity check
			//
			if (apply == null) {
				throw new IllegalArgumentException("Must supply a non-null apply object.");
			}
			//
			// Get the function
			//
			FunctionDefinition function = JPAUtils.getFunctionIDMap().get(apply.getFunctionId());
			if (function == null) {
				throw new Exception("Invalid function id: " + apply.getFunctionId());
			}
			//
			// Now check argument list, do we have the minimum?
			//
			List<JAXBElement<?>> applyArgs = apply.getExpression();
			if (applyArgs.isEmpty()) {
				//
				// May not need any args
				//
				if (function.getArgLb() > 0) {
					throw new Exception ("Number of Args mismatch, expecting at least " + 
							function.getArgLb() + " arguments but have zero.");
				}
			} else {
				if (applyArgs.size() < function.getArgLb()) {
					throw new Exception ("Number of Args mismatch, expecting at least " + 
										function.getArgLb() + " arguments but have " + applyArgs.size());
				}
			}
			//
			// Is there an upper bound?
			//
			if (function.getArgUb() != -1 && applyArgs.size() != function.getArgUb()) {
				throw new Exception ("Number of Args mismatch, expecting at most " + 
						function.getArgUb() + " arguments but have " + applyArgs.size());
			}
		} catch (Exception e) {
			logger.error("Number of arguments incorrect: " + e);
			return false;
		}
		return true;
	}
	
	public static boolean	canHaveMoreArguments(ConditionType condition) {
		if (condition.getExpression() == null || condition.getExpression().getValue() == null) {
			return true;
		}
		Object expression = condition.getExpression().getValue();
		if (expression instanceof ApplyType) {
			return XACMLFunctionValidator.canHaveMoreArguments((ApplyType) expression);
		}
		return false;
	}
	
	public static boolean canHaveMoreArguments(VariableDefinitionType variable) {
		if (variable.getExpression() == null || variable.getExpression().getValue() == null) {
			return true;
		}
		Object expression = variable.getExpression().getValue();
		if (expression instanceof ApplyType) {
			return XACMLFunctionValidator.canHaveMoreArguments((ApplyType) expression);
		}
		return false;
	}
	
	public static boolean canHaveMoreArguments(AttributeAssignmentExpressionType assignment) {
		if (assignment.getExpression() == null || assignment.getExpression().getValue() == null) {
			return true;
		}
		Object expression = assignment.getExpression().getValue();
		if (expression instanceof ApplyType) {
			return XACMLFunctionValidator.canHaveMoreArguments((ApplyType) expression);
		}
		return false;
	}
	
	public static boolean	canHaveMoreArguments(ApplyType apply) {
		//
		// Sanity check
		//
		if (apply == null) {
			throw new IllegalArgumentException("Must supply a non-null apply object.");
		}
		//
		// Get the function
		//
		FunctionDefinition function = JPAUtils.getFunctionIDMap().get(apply.getFunctionId());
		if (function == null) {
			throw new IllegalArgumentException("Invalid function id: " + apply.getFunctionId());
		}
		//
		// Is there no upper bound?
		//
		if (function.getArgUb() == -1) {
			//
			// No limit to the number of arguments
			//
			return true;
		}
		//
		// There is an upper bound - have we met it?
		//
		List<JAXBElement<?>> applyArgs = apply.getExpression();
		if (applyArgs.size() < function.getArgUb()) {
			//
			// We have not met the upper bound, so yes we can
			// add more arguments.
			//
			return true;
		}
		return false;
	}
	
	public static boolean	isFunctionAvailable(FunctionDefinition function, ApplyType apply, FunctionArgument argument) {
		//
		// Does it return correct datatype?
		//
		if (argument != null) {
			//
			// Does it match?
			//
			if (function.getDatatypeBean().getXacmlId().equals(argument.getDatatypeBean().getXacmlId())) {
				return false;
			}
			if (function.getIsBagReturn() != argument.getIsBag()) {
				return false;
			}
		}
		//
		// Check each argument
		//
		int i;
		for (i = 0; i < apply.getExpression().size(); i++) {
			//
			// Get the argument
			//
			JAXBElement<?> arg = apply.getExpression().get(i);
			//
			// Get what the argument should be
			//
			FunctionArgument functionArg = XACMLFunctionValidator.getFunctionArgument(i + 1, function);
			//
			// Sanity check
			//
			if (arg == null || arg.getValue() == null || functionArg == null) {
				//
				// Something screwy with the list, just return not available. They will have
				// delete items that are bad.
				//
				return false;
			}
			//
			// Does it match?
			//
			if (XACMLFunctionValidator.isArgumentValid(arg.getValue(), functionArg) == false) {
				return false;
			}
		}
		//
		// Done checking, we don't care if there are more args needed. Just want to know if this
		// function can fit with the existing arguments.
		//
		return true;
	}
	
	public static FunctionDefinition	validateApply(ApplyType apply, FunctionArgument argument) {
		//
		// Sanity check
		//
		if (apply == null) {
			throw new IllegalArgumentException("Must supply a non-null apply object.");
		}
		try {
			//
			// Get the function
			//
			FunctionDefinition function = JPAUtils.getFunctionIDMap().get(apply.getFunctionId());
			if (function == null) {
				throw new Exception("Invalid function id: " + apply.getFunctionId());
			}
			//
			// Does it return correct datatype?
			//
			if (argument != null) {
				//
				// Does it match?
				//
				if (argument.getDatatypeBean() != null) {
					if (! function.getDatatypeBean().getXacmlId().equals(argument.getDatatypeBean().getXacmlId())) {
						throw new Exception("Function return datatype(" + function.getDatatypeBean() + 
								") does not match expected argument datatype (" + argument.getDatatypeBean());
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Argument datatype bean is null - any datatype should work.");
					}
				}
				if (function.getIsBagReturn() != argument.getIsBag()) {
					throw new Exception("Function is bag (" + function.getIsBagReturn() + 
							") does not match argument isBag(" + argument.getIsBag());
				}
			}
			//
			// Now check argument list, do we have the minimum?
			//
			List<JAXBElement<?>> applyArgs = apply.getExpression();
			if (applyArgs == null) {
				//
				// May not need any args
				//
				if (function.getArgLb() > 0) {
					throw new Exception ("Number of Args mismatch, expecting at least " + 
							function.getArgLb() + " arguments but have zero.");
				}
			} else {
				if (applyArgs.size() < function.getArgLb()) {
					throw new Exception ("Number of Args mismatch, expecting at least " + 
										function.getArgLb() + " arguments but have " + applyArgs.size());
				}
			}
			//
			// Is there an upper bound?
			//
			if (function.getArgUb() != -1 && applyArgs.size() != function.getArgUb()) {
				throw new Exception ("Number of Args mismatch, expecting at most " + 
						function.getArgUb() + " arguments but have " + applyArgs.size());
			}
			//
			// Check each argument
			//
			int i;
			for (i = 0; i < applyArgs.size(); i++) {
				//
				// Get the argument
				//
				JAXBElement<?> arg = applyArgs.get(i);
				//
				// Get what the argument should be
				//
				FunctionArgument functionArg = XACMLFunctionValidator.getFunctionArgument(i + 1, function);
				//
				// Sanity check
				//
				if (arg == null || arg.getValue() == null || functionArg == null) {
					throw new NullPointerException("An argument is null: Element" + arg + " Function Arg: " + functionArg);
				}
				//
				// Does it match?
				//
				if (XACMLFunctionValidator.isArgumentValid(arg.getValue(), functionArg) == false) {
					throw new Exception("Invalid Argument: " + arg.getValue());
				}
			}
			//
			// Done checking, just return the function which has the datatype
			// and if it is a bag.
			//
			return function;
		} catch(Exception e) {
			logger.error("Function is not valid: " + apply.getFunctionId() + " argument: " + argument + " " + e);
			return null;
		}
	}
	
	private static boolean isArgumentValid(Object value, FunctionArgument functionArg) {
		if (value instanceof ApplyType) {
			//
			// Recursively validate the Apply.
			//
			FunctionDefinition function = XACMLFunctionValidator.validateApply((ApplyType) value, functionArg);
			if (functionArg.getDatatypeBean() == null || function.getDatatypeBean().getId() == functionArg.getDatatypeBean().getId()) {
				if (function.getIsBagReturn() == functionArg.getIsBag()) {
					return true;
				}
				logger.error("isBag does not match: " + function.getIsBagReturn() + " " + functionArg.getIsBag());
			}
			logger.error("Datatypes do not match: " + function.getDatatypeBean().getShortName() + " " + functionArg.getDatatypeBean().getShortName());
		} else if (value instanceof AttributeValueType) {
			AttributeValueType val = (AttributeValueType) value;
			Datatype datatype = JPAUtils.findDatatype(new IdentifierImpl(val.getDataType()));
			if (functionArg.getDatatypeBean() == null || datatype.getId() == functionArg.getDatatypeBean().getId()) {
				//
				// TODO Is bag?
				//
				return true;
			}
			logger.error("Datatypes do not match: " + datatype.getShortName() + " " + functionArg.getDatatypeBean().getShortName());
		} else if (value instanceof AttributeDesignatorType) {
			AttributeDesignatorType designator = (AttributeDesignatorType) value;
			Datatype datatype = JPAUtils.findDatatype(new IdentifierImpl(designator.getDataType()));
			if (functionArg.getDatatypeBean() == null || datatype.getId() == functionArg.getDatatypeBean().getId()) {
				//
				// TODO Is bag?
				//
				return true;
			}
			logger.error("Datatypes do not match: " + datatype.getShortName() + " " + functionArg.getDatatypeBean().getShortName());
		} else if (value instanceof AttributeSelectorType) {
			AttributeSelectorType selector = (AttributeSelectorType) value;
			Datatype datatype = JPAUtils.findDatatype(new IdentifierImpl(selector.getDataType()));
			if (functionArg.getDatatypeBean() == null || datatype.getId() == functionArg.getDatatypeBean().getId()) {
				//
				// TODO Is bag?
				//
				return true;
			}
			logger.error("Datatypes do not match: " + datatype.getShortName() + " " + functionArg.getDatatypeBean().getShortName());
		} else if (value instanceof VariableReferenceType) {
			//
			// We can't verify this at this time.
			// The user can define variables in other parts of the policy file
			// or another policy file. This should be flagged if the user performs
			// simulation and other testing on the policy before deployment.
			//
			return true;
		} else if (value instanceof FunctionType) {
			//
			// Does this function exist?
			//
			FunctionDefinition function = JPAUtils.findFunction(((FunctionType) value).getFunctionId());
			if (function == null) {
				//
				// Could not find function
				//
				logger.warn("Could not find function in our database: " + ((FunctionType) value).getFunctionId());
				return false;
			}
			//
			// Does this function return the correct data type?
			//
			if (functionArg.getDatatypeBean() == null || function.getDatatypeBean().getId() == functionArg.getDatatypeBean().getId()) {
				return true;
			}			
			logger.error("Datatypes do not match: " + function.getDatatypeBean().getShortName() + " " + functionArg.getDatatypeBean().getShortName());
		}		
		return false;
	}

	public static FunctionArgument getFunctionArgument(int index, FunctionDefinition function) {
		if (index < 1) {
			throw new IllegalArgumentException("The index must be 1-based");
		}
		//
		// Setup what the actual lower bound and upper bounds are
		// within the list.
		//
//		int lowerBound = function.getArgLb();
//		if (lowerBound == 0) {
//			lowerBound = 1;
//		}
		int upperBound = function.getArgUb();
		if (upperBound == -1) {
			upperBound = function.getFunctionArguments().size();
		}
		//
		// The list may not be sorted, so make sure we find
		// the actual argument index
		//
		int argumentIndex = index;
		if (index >= upperBound) {
			argumentIndex = upperBound;
		}
		for (FunctionArgument arg : function.getFunctionArguments()) {
			if (arg.getArgIndex() == argumentIndex) {
				return arg;
			}
		}
		return null;
	}

	public static boolean validateCondition(ConditionType condition) {
		if (condition.getExpression() == null) {
			return false;
		}
		Object expression = condition.getExpression().getValue();
		if (expression instanceof ApplyType) {
			FunctionDefinition function = XACMLFunctionValidator.validateApply((ApplyType) expression, null);
			if (function == null) {
				return false;
			}
			if (function.isBagReturn()) {
				return false;
			}
			if (function.getDatatypeBean() == null) {
				return false;
			}
			return function.getDatatypeBean().getXacmlId().equals(XACML3.ID_DATATYPE_BOOLEAN.stringValue());
		}
		if (expression instanceof AttributeDesignatorType) {
			return ((AttributeDesignatorType) expression).getDataType().equals(XACML3.ID_DATATYPE_BOOLEAN.stringValue());
		}
		if (expression instanceof AttributeSelectorType) {
			return ((AttributeSelectorType) expression).getDataType().equals(XACML3.ID_DATATYPE_BOOLEAN.stringValue());
		}
		if (expression instanceof AttributeValueType) {
			return ((AttributeValueType) expression).getDataType().equals(XACML3.ID_DATATYPE_BOOLEAN.stringValue());
		}
		if (expression instanceof VariableReferenceType) {
			//
			// Really unknown - the variable may or may not have been defined
			//
			return true;
		}
		return false;
	}

	public static boolean validateVariable(VariableDefinitionType variable) {
		if (variable.getExpression() == null) {
			return false;
		}
		Object expression = variable.getExpression().getValue();
		if (expression instanceof ApplyType) {
			FunctionDefinition function = XACMLFunctionValidator.validateApply((ApplyType) expression, null);
			if (function == null) {
				return false;
			}
			return true;
		}
		if (expression instanceof AttributeDesignatorType) {
			return true;
		}
		if (expression instanceof AttributeSelectorType) {
			return true;
		}
		if (expression instanceof AttributeValueType) {
			return true;
		}
		if (expression instanceof VariableReferenceType) {
			return true;
		}
		return false;
	}

	public static boolean validateAssignment(AttributeAssignmentExpressionType assignmentExpression) {
		if (assignmentExpression.getExpression() == null) {
			return false;
		}
		Object expression = assignmentExpression.getExpression().getValue();
		if (expression instanceof ApplyType) {
			FunctionDefinition function = XACMLFunctionValidator.validateApply((ApplyType) expression, null);
			if (function == null) {
				return false;
			}
			//
			// TODO
			//
		}
		//
		// TODO
		//
		return true;
	}

}
