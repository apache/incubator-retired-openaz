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
package com.att.research.xacmlatt.pdp.policy;

import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.Status;
import com.att.research.xacml.std.StdStatus;

/**
 * FunctionArgumentAttributeValue implements {@link FunctionArgument} for a single
 * {@link com.att.research.xacml.api.AttributeValue} 
 * @author car
 *
 */
public class FunctionArgumentAttributeValue implements FunctionArgument {
	private AttributeValue<?> attributeValue;
	
	/**
	 * Creates a new <code>FunctionArgumentAttributeValue</code> from the given <code>AttributeValue</code>.
	 * 
	 * @param attributeValueIn the <code>AttributeValue</code> for the new <code>FunctionArgumentAttributeValue</code>.
	 */
	public FunctionArgumentAttributeValue(AttributeValue<?> attributeValueIn) {
		this.attributeValue	= attributeValueIn;
	}
	
	@Override
	public Status getStatus() {
		return StdStatus.STATUS_OK;
	}
	
	@Override
	public boolean isOk() {
		return true;
	}

	@Override
	public boolean isBag() {
		return false;
	}

	@Override
	public AttributeValue<?> getValue() {
		return this.attributeValue;
	}

	@Override
	public Bag getBag() {
		return null;
	}
}
