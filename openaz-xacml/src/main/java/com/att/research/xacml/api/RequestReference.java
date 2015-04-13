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
 * Defines the API for objects that represent XACML RequestReference elements.  RequestReference elements contain zero or more AttributesReference
 * elements and are used in multi-requests.
 *  
 * @author Christopher A. Rath
 * @version $Revision: 1.1 $
 */
public interface RequestReference {
	/**
	 * Gets the <code>Collection</code> of {@link com.att.research.xacml.api.RequestAttributesReference}s contained
	 * in this <code>RequestReference</code>.  If there are no <code>RequestAttributesReference}s this method must return an empty <code>Collection</code>.
	 * 
	 * @return the <code>Collection</code> of <code>RequestAttributesReference</code>s contained in this <code>RequestReference</code>.
	 */
	public Collection<RequestAttributesReference> getAttributesReferences();
	
	/**
	 * {@inheritDoc}
	 * 
	 * Implementations of this interface must override the <code>equals</code> method with the following semantics:
	 * 
	 * 		Two <code>RequestReference</code>s (<code>r1</code> and <code>r2</code>) are equal if:
	 * 
	 * 			{@code r1.getAttributesReferences()} is pair-wise equal to {@code r2.getAttributesReferences()}
	 */
	@Override
	public boolean equals(Object obj);
}
