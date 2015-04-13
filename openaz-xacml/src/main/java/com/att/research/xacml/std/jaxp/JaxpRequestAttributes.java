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

import org.w3c.dom.Node;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.RequestAttributes;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdRequestAttributes;

/**
 * JaxpRequestAttributes extends {@link com.att.research.xacml.std.StdRequestAttributes} with methods for creation from JAXP elements.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class JaxpRequestAttributes {

	protected JaxpRequestAttributes() {
	}
	
	public static RequestAttributes newInstance(AttributesType attributesType) {
		if (attributesType == null) {
			throw new NullPointerException("Null AttributesType");
		} else if (attributesType.getCategory() == null) {
			throw new IllegalArgumentException("Null categoryId for AttributesType");
		}
		Identifier identifierCategory	= new IdentifierImpl(attributesType.getCategory());
		Node nodeContentRoot			= null;
		List<Attribute> listAttributes	= new ArrayList<Attribute>();
		
		if (attributesType.getContent() != null && attributesType.getContent().getContent() != null && attributesType.getContent().getContent().size() > 0) {
			// The XACML Spec says there is only one child node, so we only need the first element of the list, and it should be an Element
			// unless someone happens to use XACML schema types in their Content node, which could be a problem.
			//
			Object	contentObject	= attributesType.getContent().getContent().get(0);
			if (contentObject instanceof Node) {
				nodeContentRoot	= (Node)contentObject;
			}
		}
		if (attributesType.getAttribute() != null && attributesType.getAttribute().size() > 0) {
			Iterator<AttributeType>	iterAttributeTypes	= attributesType.getAttribute().iterator();
			while (iterAttributeTypes.hasNext()) {
				listAttributes.add(JaxpAttribute.newInstance(identifierCategory, iterAttributeTypes.next()));
			}
		}
		return new StdRequestAttributes(identifierCategory, listAttributes, nodeContentRoot, attributesType.getId());
	}

}
