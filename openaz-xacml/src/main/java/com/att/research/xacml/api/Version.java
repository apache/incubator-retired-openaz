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
 * Version is the interface that objects that represent XACML VersionType attributes and elements implement.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public interface Version extends Comparable<Version>, SemanticString {
	/**
	 * Gets the <code>String</code> representation of this <code>Version</code>.
	 * 
	 * @return the <code>String</code> representation of this <code>Version</code>
	 */
	public String getVersion();
	
	/**
	 * Gets the integer array representation of this <code>Version</code>.
	 * 
	 * @return the integer array representation of this <code>Version</code>
	 */
	public int[] getVersionDigits();
	
}
