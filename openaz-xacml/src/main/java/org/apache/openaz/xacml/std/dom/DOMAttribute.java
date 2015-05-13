/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

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
package org.apache.openaz.xacml.std.dom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdAttribute;
import org.apache.openaz.xacml.std.StdMutableAttribute;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMAttribute extends {@link org.apache.openaz.xacml.std.StdMutableAttribute} with methods for creation from
 * DOM <code>Node</code>s.
 */
public class DOMAttribute {
    private static Log logger = LogFactory.getLog(DOMAttribute.class);

    protected DOMAttribute() {

    }

    /**
     * Creates a new <code>DOMAttribute</code> by parsing the given {@link org.w3c.dom.Node}.
     *
     * @param category the {@link org.apache.openaz.xacml.common.Identfier} for the category of the new
     *            <code>DOMAttribute</code>.
     * @param nodeAttribute the <code>Node</code> for the <code>DOMAttribute</code>
     * @return a new <code>DOMAttribute</code> parsed from the given <code>Node</code>.
     * @throws IllegalArgumentException if there is an error converting the <code>Node</code> to a
     *             <code>DOMAttribute</code>
     */
    public static Attribute newInstance(Identifier category, Node nodeAttribute) throws DOMStructureException {
        Element elementAttribute = DOMUtil.getElement(nodeAttribute);
        boolean bLenient = DOMProperties.isLenient();
        StdMutableAttribute mutableAttribute = new StdMutableAttribute();

        mutableAttribute.setCategory(category);
        mutableAttribute.setAttributeId(DOMUtil.getIdentifierAttribute(nodeAttribute,
                                                                       XACML3.ATTRIBUTE_ATTRIBUTEID,
                                                                       !bLenient));

        NodeList children = elementAttribute.getChildNodes();
        int numChildren;
        boolean sawAttributeValue = false;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        if (XACML3.ELEMENT_ATTRIBUTEVALUE.equals(child.getLocalName())) {
                            mutableAttribute.addValue(DOMAttributeValue.newInstance(child, category));
                            sawAttributeValue = true;
                        } else {
                            if (!bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodeAttribute);
                            }
                        }
                    } else {
                        if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeAttribute);
                        }
                    }
                }
            }
        }

        if (!sawAttributeValue && !bLenient) {
            throw DOMUtil.newMissingElementException(nodeAttribute, XACML3.XMLNS,
                                                     XACML3.ELEMENT_ATTRIBUTEVALUE);
        }

        mutableAttribute.setIssuer(DOMUtil.getStringAttribute(nodeAttribute, XACML3.ATTRIBUTE_ISSUER));
        mutableAttribute.setIncludeInResults(DOMUtil.getBooleanAttribute(elementAttribute,
                                                                         XACML3.ATTRIBUTE_INCLUDEINRESULT,
                                                                         !bLenient));

        return new StdAttribute(mutableAttribute);
    }

    public static boolean repair(Node nodeAttribute) throws DOMStructureException {
        Element elementAttribute = DOMUtil.getElement(nodeAttribute);
        boolean result = false;

        result = DOMUtil.repairIdentifierAttribute(elementAttribute, XACML3.ATTRIBUTE_ATTRIBUTEID, logger)
                 || result;
        result = DOMUtil.repairBooleanAttribute(elementAttribute, XACML3.ATTRIBUTE_INCLUDEINRESULT, false,
                                                logger) || result;

        NodeList children = elementAttribute.getChildNodes();
        int numChildren;
        boolean sawAttributeValue = false;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        if (XACML3.ELEMENT_ATTRIBUTEVALUE.equals(child.getLocalName())) {
                            result = DOMAttributeValue.repair(child) || result;
                            sawAttributeValue = true;
                        } else {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementAttribute.removeChild(child);
                            result = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementAttribute.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        if (!sawAttributeValue) {
            throw new DOMStructureException(
                                            DOMUtil
                                                .newMissingAttributeException(elementAttribute,
                                                                              XACML3.ELEMENT_ATTRIBUTEVALUE));
        }

        return result;
    }

}
