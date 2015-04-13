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

/**
 * FunctionArgument is the interface implemented by objects that can serve as arguments to a {@link com.att.research.xacmlatt.pdp.policy.FunctionDefinition}
 * <code>evaluate</code> call.
 * 
 * @author car
 * @version $Revision: 1.3 $
 */
public interface FunctionArgument {
	/**
	 * Gets the {@link com.att.research.xacml.api.Status} from the evaluation of this <code>FunctionArgument</code>.
	 * 
	 * @return the <code>Status</code> from the evaluation of this <code>FunctionArgument</code>>
	 */
	public Status getStatus();
	
	/**
	 * Determines if this <code>FunctionArgument</code> is OK and can have its <code>AttributeValue</code> or
	 * <code>Bag</code> retrieved.
	 * 
	 * @return true if this <code>FunctionArgument</code> is OK, otherwise false.
	 */
	public boolean isOk();
	
	/**
	 * Determines if this <code>FunctionArgument</code> represents a bag of values.
	 * 
	 * @return true if this <code>FunctionArgument</code> represents a bag of values, else false.
	 */
	public boolean isBag();
	
	/**
	 * Gets the single <code>AttributeValue</code> representing the value of this <code>FunctionArgument</code>.  If
	 * this <code>FunctionArgument</code> represents a bag, the value returned is up to the implementation.
	 * 
	 * @return the single <code>AttributeValue</code> representing the value of this <code>FunctionArgument</code>.
	 */
	public AttributeValue<?> getValue();
	
	/**
	 * Gets the {@link Bag} value for this <code>FunctionArgument</code> if the
	 * argument represents a <code>Bag</code>, (i.e. <code>isBag</code> returns true).
	 * 
	 * @return the <code>Bag</code> value for this <code>FunctionArgument</code>.
	 */
	public Bag getBag();
	
}
