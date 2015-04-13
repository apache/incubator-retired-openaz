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
package com.att.research.xacml.api.pdp;

import java.util.Iterator;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.Status;

/**
 * ScopeResolverResult is the interface for objects returned by the {@link ScopeResolver}'s
 * <code>resolveScope</code> method.
 * 
 * @author car
 * @version $Revision$
 */
public interface ScopeResolverResult {
	/*
	 * Gets the {@link com.att.research.xacml.api.Status} for the scope resolution request.
	 * 
	 * @return the <code>Status</code> of the scope resolution request
	 */
	public Status getStatus();
	
	/*
	 * Gets an <code>Iterator</code> over {@link com.att.research.xacml.api.Attribute}s resolved from a scope resolution request.
	 * 
	 * @return an <code>Iterator</code> over the <code>Attribute</code>s resolved from a scope resolution request.
	 */
	public Iterator<Attribute> getAttributes();
}
