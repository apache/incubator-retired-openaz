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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdMutableAttribute;

/**
 * JaxpAttribute extends {@link com.att.research.xacml.std.StdMutableAttribute} with methods for creation from JAXP elements.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class JaxpAttribute extends StdMutableAttribute {

	protected JaxpAttribute(Identifier attributeIdIn, Identifier categoryIdIn, List<AttributeValue<?>> valuesIn, String issuerIn, boolean includeInResultsIn) {
		super(attributeIdIn, categoryIdIn, valuesIn, issuerIn, includeInResultsIn);
	}

	public static JaxpAttribute newInstance(Identifier categoryId, AttributeType attributeType) {
		if (categoryId == null) {
			throw new NullPointerException("Null categoryId");
		} else if (attributeType == null) {
			throw new NullPointerException("Null AttributeType");
		} else if (attributeType.getAttributeId() == null) {
			throw new IllegalArgumentException("Null attributeId in AttributeType");
		} else if (attributeType.getAttributeValue() == null) {
			throw new IllegalArgumentException("Null attributeValue in AttributeType");
		}
		Identifier						attributeId				= new IdentifierImpl(attributeType.getAttributeId());
		List<AttributeValue<?>>			values					= new ArrayList<AttributeValue<?>>();
		Iterator<AttributeValueType>	iterAttributeValueTypes	= attributeType.getAttributeValue().iterator();
		while (iterAttributeValueTypes.hasNext()) {
			values.add(JaxpAttributeValue.newInstance(iterAttributeValueTypes.next()));
		}
		
		return new JaxpAttribute(attributeId, categoryId, values, attributeType.getIssuer(), attributeType.isIncludeInResult());
	}
}
