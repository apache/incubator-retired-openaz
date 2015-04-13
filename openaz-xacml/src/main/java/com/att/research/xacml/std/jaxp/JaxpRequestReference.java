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
package com.att.research.xacml.std.jaxp;

import java.util.Iterator;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesReferenceType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestReferenceType;

import com.att.research.xacml.std.StdMutableRequestReference;

/**
 * JaxpRequestReference extends {@link com.att.research.xacml.std.StdMutableRequestReference} with methods for
 * creation form JAXP elements.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class JaxpRequestReference extends StdMutableRequestReference {

	protected JaxpRequestReference() {
	}
	
	public static JaxpRequestReference newInstance(RequestReferenceType requestReferenceType) {
		if (requestReferenceType == null) {
			throw new NullPointerException("Null RequestReferenceType");
		} else if (requestReferenceType.getAttributesReference() == null || requestReferenceType.getAttributesReference().size() == 0) {
			throw new IllegalArgumentException("No AttributesReferenceTypes in RequestReferenceType");
		}
		JaxpRequestReference	jaxpRequestReference	= new JaxpRequestReference();
		Iterator<AttributesReferenceType>	iterAttributesReferenceTypes	= requestReferenceType.getAttributesReference().iterator();
		while (iterAttributesReferenceTypes.hasNext()) {
			jaxpRequestReference.add(JaxpRequestAttributesReference.newInstances(iterAttributesReferenceTypes.next()));
		}
		return jaxpRequestReference;
	}

}
