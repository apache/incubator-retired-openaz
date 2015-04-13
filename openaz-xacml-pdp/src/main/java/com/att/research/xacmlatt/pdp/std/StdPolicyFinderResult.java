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
package com.att.research.xacmlatt.pdp.std;

import com.att.research.xacml.api.Status;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacmlatt.pdp.policy.PolicyDef;
import com.att.research.xacmlatt.pdp.policy.PolicyFinderResult;

/**
 * StdPolicyFinderResult implements the {@link com.att.research.xacmlatt.pdp.policy.PolicyFinderResult} interface.
 * 
 * @author car
 * @version $Revision: 1.1 $
 * @param <T> the java class extending {@link com.att.research.xacmlatt.pdp.policy.PolicyDef} held by the <code>StdPolicyFinderResult</code>
 */
public class StdPolicyFinderResult<T extends PolicyDef> implements PolicyFinderResult<T> {
	private Status status;
	private T policyDef;
	
	public StdPolicyFinderResult(Status statusIn, T policyDefIn) {
		this.status	= (statusIn == null ? StdStatus.STATUS_OK : statusIn);
		this.policyDef	= policyDefIn;
	}
	
	public StdPolicyFinderResult(Status statusIn) {
		this(statusIn, null);
	}
	
	public StdPolicyFinderResult(T policyDefIn) {
		this(null, policyDefIn);
	}

	@Override
	public Status getStatus() {
		return this.status;
	}

	@Override
	public T getPolicyDef() {
		return this.policyDef;
	}

}
