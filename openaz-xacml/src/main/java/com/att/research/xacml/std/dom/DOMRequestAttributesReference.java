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
package com.att.research.xacml.std.dom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.att.research.xacml.api.RequestAttributesReference;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.StdRequestAttributesReference;

/**
 * DOMRequestAttributesReference extends {@link com.att.research.xacml.std.StdRequestAttributesReference} with methods for creation from
 * DOM {@link org.w3c.dom.Node}s.
 * 
 * @author car
 * @version $Revision: 1.2 $
 */
public class DOMRequestAttributesReference {
	private static final Log logger	= LogFactory.getLog(DOMRequestAttributesReference.class);
	
	protected DOMRequestAttributesReference() {
	}
	
	/**
	 * Creates a new <code>DOMRequestAttributesReference</code> by parsing the given root <code>Node</code> of a XACML AttributesReference element.
	 * 
	 * @param nodeAttributesReference the <code>Node</code> to parse
	 * @return a new <code>DOMRequestAttributesReference</code>
	 * @throws com.att.research.xacml.std.dom.DOMStructureException if the conversion cannot be made
	 */
	public static RequestAttributesReference newInstance(Node nodeAttributesReference) throws DOMStructureException {
		Element	elementAttributesReference	= DOMUtil.getElement(nodeAttributesReference);
		boolean bLenient					= DOMProperties.isLenient();
		
		return new StdRequestAttributesReference(DOMUtil.getStringAttribute(elementAttributesReference, XACML3.ATTRIBUTE_REFERENCEID, !bLenient));
	}
	
	public static boolean repair(Node nodeAttributesReference) throws DOMStructureException {
		Element	elementAttributesReference	= DOMUtil.getElement(nodeAttributesReference);
		return DOMUtil.repairStringAttribute(elementAttributesReference, XACML3.ATTRIBUTE_REFERENCEID, null, logger);
	}

}
