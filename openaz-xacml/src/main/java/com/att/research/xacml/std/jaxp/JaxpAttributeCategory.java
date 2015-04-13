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
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesType;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.AttributeCategory;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdAttributeCategory;

/**
 * JaxpAttributeCategory extends {@link com.att.research.xacml.std.StdAttributeCategory} with methods for creation from
 * JAXP elements.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class JaxpAttributeCategory {

	protected JaxpAttributeCategory() {
	}
	
	public static AttributeCategory newInstance(AttributesType attributesType) {
		if (attributesType == null) {
			throw new NullPointerException("Null AttributesType");
		} else if (attributesType.getCategory() == null) {
			throw new IllegalArgumentException("Null categoryId for AttributesType");
		}
		Identifier identifierCategory	= new IdentifierImpl(attributesType.getCategory());
		List<Attribute> listAttributes	= new ArrayList<Attribute>();
		
		if (attributesType.getAttribute() != null && attributesType.getAttribute().size() > 0) {
			Iterator<AttributeType>	iterAttributeTypes	= attributesType.getAttribute().iterator();
			while (iterAttributeTypes.hasNext()) {
				listAttributes.add(JaxpAttribute.newInstance(identifierCategory, iterAttributeTypes.next()));
			}
		}
		return new StdAttributeCategory(identifierCategory, listAttributes);
	}

}
