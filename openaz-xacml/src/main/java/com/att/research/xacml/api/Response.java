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

package com.att.research.xacml.api;

import java.util.Collection;

/**
 * Defines the API for objects that represent XACML Response documents.  Response documents wrap the Result elements for individual XACML 
 * decision requests.
 *  
 * @author Christopher A. Rath
 * @version $Revision: 1.2 $
 */
public interface Response {
	/**
	 * Gets the <code>Collection</code> of {@link Result}s objects in this <code>Response</code>.  If
	 * there are no <code>Result</code>s, this method must return an empty <code>Collection</code>.
	 * 
	 * @return the <code>Collection</code> of {@link Result}s objects in this <code>Response</code>.
	 */
	public Collection<Result> getResults();
	
	/**
	 * {@inheritDoc}
	 * 
	 * Implementations of this interface must override the <code>equals</code> method with the following semantics:
	 * 
	 * 		Two <code>Response</code>s (<code>r1</code> and <code>r2</code>) are equal if:
	 * 			{@code r1.getResults()} is pairwise equal to {@code r2.getResults()}
	 */
	@Override
	public boolean equals(Object obj);
}
