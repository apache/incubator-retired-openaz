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
import org.apache.openaz.xacml.api.Obligation;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdMutableObligation;
import org.apache.openaz.xacml.std.StdObligation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides static methods for creating {@link org.apache.openaz.xacml.api.Obligation} objects from a
 * {@link org.w3c.dom.Node}.
 */
public class DOMObligation {
    private static final Log logger = LogFactory.getLog(DOMObligation.class);

    protected DOMObligation() {
    }

    /**
     * Creates a new <code>Obligation</code> by parsing the given <code>Node</code> as a XACML Obligation
     * element.
     *
     * @param nodeObligation the <code>Node</code> representing the Obligation element
     * @return a new <code>DOMObligation</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if the conversion cannot be made
     */
    public static Obligation newInstance(Node nodeObligation) throws DOMStructureException {
        Element elementObligation = DOMUtil.getElement(nodeObligation);
        boolean bLenient = DOMProperties.isLenient();
        StdMutableObligation mutableObligation = new StdMutableObligation();

        mutableObligation.setId(DOMUtil.getIdentifierAttribute(elementObligation,
                                                               XACML3.ATTRIBUTE_OBLIGATIONID, !bLenient));

        NodeList children = elementObligation.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        if (XACML3.ELEMENT_ATTRIBUTEASSIGNMENT.equals(child.getLocalName())) {
                            mutableObligation.addAttributeAssignment(DOMAttributeAssignment
                                .newInstance(child));
                        } else {
                            if (!bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodeObligation);
                            }
                        }
                    } else {
                        if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeObligation);
                        }
                    }
                }
            }
        }

        return new StdObligation(mutableObligation);
    }

    public static boolean repair(Node nodeObligation) throws DOMStructureException {
        Element elementObligation = DOMUtil.getElement(nodeObligation);
        boolean result = false;

        result = DOMUtil.repairIdentifierAttribute(elementObligation, XACML3.ATTRIBUTE_OBLIGATIONID, logger)
                 || result;

        NodeList children = elementObligation.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        if (XACML3.ELEMENT_ATTRIBUTEASSIGNMENT.equals(child.getLocalName())) {
                            result = DOMAttributeAssignment.repair(child) || result;
                        } else {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementObligation.removeChild(child);
                            result = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementObligation.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Creates a <code>List</code> of <code>Obligation</code>s by parsing the given <code>Node</code>
     * representing a XACML Obligations element.
     *
     * @param nodeObligations the <code>Node</code> representing the XACML Obligations element
     * @return a <code>List</code> of <code>Obligation</code>s parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static List<Obligation> newList(Node nodeObligations) throws DOMStructureException {
        Element elementObligations = DOMUtil.getElement(nodeObligations);
        boolean bLenient = DOMProperties.isLenient();

        List<Obligation> listObligations = new ArrayList<Obligation>();

        NodeList children = elementObligations.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        if (XACML3.ELEMENT_OBLIGATION.equals(child.getLocalName())) {
                            listObligations.add(DOMObligation.newInstance(child));
                        } else {
                            if (!bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodeObligations);
                            }
                        }
                    } else {
                        if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeObligations);
                        }
                    }
                }
            }
        }
        return listObligations;
    }

    public static boolean repairList(Node nodeObligations) throws DOMStructureException {
        Element elementObligations = DOMUtil.getElement(nodeObligations);
        boolean result = false;

        NodeList children = elementObligations.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        if (XACML3.ELEMENT_OBLIGATION.equals(child.getLocalName())) {
                            result = result || DOMObligation.repair(child);
                        } else {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementObligations.removeChild(child);
                            result = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementObligations.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        return result;
    }

}
