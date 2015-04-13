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

import com.att.research.xacml.api.Status;

/**
 * PolicyFinderResult is the interface for return values of the methods in the {@link com.att.research.xacmlatt.pdp.policy.PolicyFinderFactory} interface.
 * 
 * @author car
 * @version $Revision: 1.1 $
 * @param <T> the class extending {@link PolicyDef} contained as a result in this <code>PolicyFinderResult</code>
 */
public interface PolicyFinderResult<T extends PolicyDef> {
	/**
	 * Gets the {@link com.att.research.xacml.api.Status} of the method call.
	 * 
	 * @return the <code>Status</code> of the method call
	 */
	public Status getStatus();
	
	/**
	 * Gets the {@link PolicyDef} returned by the method if the status is OK.
	 * 
	 * @return the <code>T</code> returned by the method if the status is OK.
	 */
	public T getPolicyDef();
}
