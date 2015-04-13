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

package com.att.research.xacml.api.trace;

/**
 * Defines the API for objects that can be set as the "cause" in a {@link com.att.research.xacml.api.trace.TraceEvent}.  Objects
 * cause a <code>TraceEvent</code> through methods called during the evaluation of a XACML Policy or PolicySet.
 * 
 * @author Christopher A. Rath
 * @version $Revision$
 */
public interface Traceable {
	/**
	 * Gets the <code>String</code> identifier for the object that caused the {@link com.att.research.xacml.api.trace.TraceEvent} as a result
	 * of a policy evaluation method.
	 * Implementations must not return <code>null</code>.
	 * 
	 * @return the <code>String</code> identifier for the object that caused the <code>TraceEvent</code>.
	 */
	public String getTraceId();
	
	/**
	 * Gets the <code>Traceable</code> that caused the evaluation method on this <code>Traceable</code> to be called.  If
	 * there is no known causing object, this method should return <code>null</code>.
	 *  
	 * @return the <code>Traceable</code> that caused the evaluation method on this <code>Traceable</code> to be called.
	 */
	public Traceable getCause();
}
