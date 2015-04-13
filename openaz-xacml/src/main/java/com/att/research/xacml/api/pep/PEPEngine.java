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

package com.att.research.xacml.api.pep;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;

/**
 * PEPEngine is the interface that applications use to make policy queries against a XACML 3.0 policy engine.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public interface PEPEngine {
	/**
	 * Instantiates a Policy Decision Point (PDP) to evaluate the given {@link com.att.research.xacml.api.Request} using its
	 * Policy Sets to determine if the given <code>Request</code> is allowed.
	 * 
	 * @param pepRequest the <code>Request</code> to evaluate
	 * @return a {@link com.att.research.xacml.api.Response} indicating the decision
	 */
	public Response decide(Request pepRequest) throws PEPException;
}
