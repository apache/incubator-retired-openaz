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

/**
 * ScopeQualifier enumerates the values of the "scope" attribute in requests.
 * 
 * @author car
 * @version $Revision$
 */
public enum ScopeQualifier {
	CHILDREN("Children"),
	DESCENDANTS("Descendants"),
	IMMEDIATE("Immediate")
	;
	
	private String name;
	
	private ScopeQualifier(String nameIn) {
		this.name	= nameIn;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static ScopeQualifier getScopeQualifier(String name) {
		for (ScopeQualifier sc: ScopeQualifier.values()) {
			if (sc.getName().equals(name)) {
				return sc;
			}
		}
		return null;
	}
}
