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
 * VersionMatch is the interface that objects implementing references to {@link Version} objects
 * must implement.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public interface VersionMatch {
	/**
	 * Gets the <code>String</code> representation of the <code>Version</code> matching pattern.
	 * 
	 * @return the <code>String</code> representation of the <code>Version</code> matching pattern.
	 */
	public String getVersionMatch();
	
	/**
	 * Determines if the pattern in this <code>VersionMatch</code> matches the given <code>Version</code> based on the
	 * given comparison code.  Comparison code values are:
	 * 	0 - match only if version numbers match the pattern
	 * -1 - match if the version numbers <= the pattern
	 *  1 - match if the version numbers >= the pattern
	 *  
	 * Wildcard values are considered to match any comparison code
	 * 
	 * @param version the <code>Version</code> to match against
	 * @param cmp integer comparision code
	 * @return true if this pattern matches the given <code>Version</code> else false
	 */
	public boolean match(Version version, int cmp);
}
