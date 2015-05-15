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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.RequestAttributes;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdRequestAttributes;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMRequestAttributes extends {@link org.apache.openaz.xacml.std.StdRequestAttributes} with methods for
 * creation from DOM {@link org.w3c.dom.Node}s.
 */
public class DOMRequestAttributes {
    private static final Log logger = LogFactory.getLog(DOMRequestAttributes.class);

    protected DOMRequestAttributes() {
    }

    /**
     * Creates a new <code>DOMRequestAttributes</code> from the given root <code>Node</code> of a XACML
     * Attributes element.
     *
     * @param nodeRequestAttributes the <code>Node</code> representing the Attributes element
     * @return a new <code>DOMRequestAttributes</code> from the given <code>Node</code>
     * @throws DOMStructureException if the conversion cannot be made
     */
    public static RequestAttributes newInstance(Node nodeRequestAttributes) throws DOMStructureException {
        Element elementRequestAttributes = DOMUtil.getElement(nodeRequestAttributes);
        boolean bLenient = DOMProperties.isLenient();

        Identifier identifierCategory = DOMUtil.getIdentifierAttribute(elementRequestAttributes,
                                                                       XACML3.ATTRIBUTE_CATEGORY, !bLenient);
        String xmlId = DOMUtil.getXmlId(elementRequestAttributes);
        Node nodeContentRoot = null;
        List<Attribute> listAttributes = new ArrayList<Attribute>();
        boolean sawContent = false;

        NodeList children = elementRequestAttributes.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_CONTENT.equals(childName)) {
                            if (sawContent && !bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, elementRequestAttributes);
                            }
                            sawContent = true;
                            /*
                             * Get the single root element node
                             */
                            NodeList grandchildren = child.getChildNodes();
                            int numGrandchildren;
                            if (grandchildren != null && (numGrandchildren = grandchildren.getLength()) > 0) {
                                for (int j = 0; j < numGrandchildren; j++) {
                                    Node grandchild = grandchildren.item(j);
                                    if (DOMUtil.isElement(grandchild)) {
                                        if (nodeContentRoot != null) {
                                            if (!bLenient) {
                                                throw DOMUtil
                                                    .newUnexpectedElementException(grandchild, child);
                                            }
                                        } else {
                                            nodeContentRoot = DOMUtil.getDirectDocumentChild(grandchild);
                                        }
                                    }
                                }
                            }
                            if (nodeContentRoot == null && !bLenient) {
                                throw DOMUtil.newMissingContentException(child);
                            }
                        } else if (XACML3.ELEMENT_ATTRIBUTE.equals(childName)) {
                            listAttributes.add(DOMAttribute.newInstance(identifierCategory, child));
                        } else {
                            if (!bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodeRequestAttributes);
                            }
                        }
                    } else {
                        if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeRequestAttributes);
                        }
                    }
                }
            }
        }

        return new StdRequestAttributes(identifierCategory, listAttributes, nodeContentRoot, xmlId);
    }

    public static boolean repair(Node nodeRequestAttributes) throws DOMStructureException {
        Element elementRequestAttributes = DOMUtil.getElement(nodeRequestAttributes);
        boolean result = false;

        result = DOMUtil.repairIdentifierAttribute(elementRequestAttributes, XACML3.ATTRIBUTE_CATEGORY,
                                                   logger) || result;
        NodeList children = elementRequestAttributes.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_ATTRIBUTE.equals(childName)) {
                            result = DOMAttribute.repair(child) || result;
                        //} else if (XACML3.ELEMENT_CONTENT.equals(childName)) {
                        } else {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementRequestAttributes.removeChild(child);
                            result = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementRequestAttributes.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        return result;
    }

}
