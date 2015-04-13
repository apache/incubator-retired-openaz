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

/**
 * SemanticString is an interface for objects that have semantically significant <code>String</code> representations.  It differentiates
 * objects whose <code>toString</code> method represents debugging information rather than something that is meaningful at runtime.
 * 
 * @author car
 * @version $Revision$
 */
public interface SemanticString {
	/**
	 * Gets the semantically significant <code>String</code> representation of the object implementing this interface.
	 * 
	 * @return the semantically significant <code>String</code> representation of the object implementing this interface.
	 */
	public String stringValue();
}
