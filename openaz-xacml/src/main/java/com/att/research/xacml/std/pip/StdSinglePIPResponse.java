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
package com.att.research.xacml.std.pip;

import com.att.research.xacml.api.Attribute;

/**
 * StdSinglePIPResponse extends {@link com.att.research.xacml.std.pip.StdMutablePIPResponse} with methods for
 * retrieving a single {@link com.att.research.xacml.api.Attribute}.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class StdSinglePIPResponse extends StdMutablePIPResponse {
	private Attribute singleAttribute;
	
	public StdSinglePIPResponse(Attribute attribute) {
		super(attribute);
		this.singleAttribute	= attribute;
	}
	
	public Attribute getSingleAttribute() {
		return this.singleAttribute;
	}
}
