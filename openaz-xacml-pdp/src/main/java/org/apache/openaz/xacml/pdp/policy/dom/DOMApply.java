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
package org.apache.openaz.xacml.pdp.policy.dom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.Policy;
import org.apache.openaz.xacml.pdp.policy.expressions.Apply;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMApply extends {@link org.apache.openaz.xacml.pdp.policy.expressions.Apply} with methods for creation
 * from DOM {@link org.w3c.dom.Node}s.
 */
public class DOMApply extends Apply {
    private static final Log logger = LogFactory.getLog(DOMApply.class);

    protected DOMApply() {
    }

    /**
     * Creates a new <code>Apply</code> by parsing the given
     * <code>Node</core> representing a XACML Apply element.
     *
     * @param nodeApply the <code>Node</code> representing the XACML Apply element
     * @param policy the <code>Policy</code> encompassing the Apply element
     * @return a new <code>Apply</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static Apply newInstance(Node nodeApply, Policy policy) throws DOMStructureException {
        Element elementApply = DOMUtil.getElement(nodeApply);
        boolean bLenient = DOMProperties.isLenient();

        DOMApply domApply = new DOMApply();

        try {
            NodeList children = nodeApply.getChildNodes();
            if (children != null) {
                int numChildren = children.getLength();
                for (int i = 0; i < numChildren; i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE
                        && XACML3.XMLNS.equals(child.getNamespaceURI())) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_DESCRIPTION.equals(childName)) {
                            domApply.setDescription(child.getTextContent());
                        } else if (DOMExpression.isExpression(child)) {
                            domApply.addArgument(DOMExpression.newInstance(child, policy));
                        } else if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeApply);
                        }
                    }
                }
            }

            domApply.setFunctionId(DOMUtil.getIdentifierAttribute(elementApply, XACML3.ATTRIBUTE_FUNCTIONID,
                                                                  !bLenient));
        } catch (DOMStructureException ex) {
            domApply.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }

        return domApply;
    }

    public static boolean repair(Node nodeApply) throws DOMStructureException {
        Element elementApply = DOMUtil.getElement(nodeApply);
        boolean result = false;

        NodeList children = nodeApply.getChildNodes();
        if (children != null) {
            int numChildren = children.getLength();
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE && XACML3.XMLNS.equals(child.getNamespaceURI())) {
                    String childName = child.getLocalName();
                    if (XACML3.ELEMENT_DESCRIPTION.equals(childName)) { //NOPMD
                        // TODO
                    } else if (DOMExpression.isExpression(child)) {
                        result = DOMExpression.repair(child) || result;
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementApply.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        result = DOMUtil.repairIdentifierAttribute(elementApply, XACML3.ATTRIBUTE_FUNCTIONID,
                                                   XACML3.ID_FUNCTION_STRING_EQUAL, logger) || result;

        return result;
    }
}
