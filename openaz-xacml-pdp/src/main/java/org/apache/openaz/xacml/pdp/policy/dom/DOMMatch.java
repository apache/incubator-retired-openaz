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
import org.apache.openaz.xacml.pdp.policy.Match;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMAttributeValue;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMMatch extends {@link org.apache.openaz.xacml.pdp.policy.Match} with methods for creation from DOM
 * {@link org.w3c.dom.Node}s.
 */
public class DOMMatch extends Match {
    private static Log logger = LogFactory.getLog(DOMMatch.class);

    protected DOMMatch() {
    }

    /**
     * Creates a new <code>DOMMatch</code> by parsing the given <code>Node</code> representing a XACML Match
     * element.
     *
     * @param nodeMatch the <code>Node</code> representing the XACML Match element
     * @return a new <code>DOMMatch</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the given <code>Node</code>
     */
    public static Match newInstance(Node nodeMatch) throws DOMStructureException {
        Element elementMatch = DOMUtil.getElement(nodeMatch);
        boolean bLenient = DOMProperties.isLenient();

        DOMMatch domMatch = new DOMMatch();

        try {
            NodeList children = elementMatch.getChildNodes();
            int numChildren;

            if (children != null && (numChildren = children.getLength()) > 0) {
                for (int i = 0; i < numChildren; i++) {
                    Node child = children.item(i);
                    if (DOMUtil.isElement(child)) {
                        if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                            String childName = child.getLocalName();
                            if (XACML3.ELEMENT_ATTRIBUTEVALUE.equals(childName)) {
                                domMatch.setAttributeValue(DOMAttributeValue.newInstance(child, null));
                            } else if (XACML3.ELEMENT_ATTRIBUTEDESIGNATOR.equals(childName)) {
                                if (domMatch.getAttributeRetrievalBase() != null && !bLenient) {
                                    throw DOMUtil.newUnexpectedElementException(child, nodeMatch);
                                }
                                domMatch.setAttributeRetrievalBase(DOMAttributeDesignator.newInstance(child));
                            } else if (XACML3.ELEMENT_ATTRIBUTESELECTOR.equals(childName)) {
                                if (domMatch.getAttributeRetrievalBase() != null) {
                                    throw DOMUtil.newUnexpectedElementException(child, nodeMatch);
                                }
                                domMatch.setAttributeRetrievalBase(DOMAttributeSelector.newInstance(child));
                            } else if (!bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodeMatch);
                            }
                        } else if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeMatch);
                        }
                    }
                }
            }

            /*
             * We have to see exactly one of these
             */
            if (domMatch.getAttributeRetrievalBase() == null && !bLenient) {
                throw DOMUtil.newMissingElementException(nodeMatch, XACML3.XMLNS,
                                                         XACML3.ELEMENT_ATTRIBUTEDESIGNATOR + " or "
                                                             + XACML3.ELEMENT_ATTRIBUTESELECTOR);
            } else if (domMatch.getAttributeValue() == null && !bLenient) {
                throw DOMUtil.newMissingElementException(nodeMatch, XACML3.XMLNS,
                                                         XACML3.ELEMENT_ATTRIBUTEVALUE);
            }

            domMatch.setMatchId(DOMUtil.getIdentifierAttribute(elementMatch, XACML3.ATTRIBUTE_MATCHID,
                                                               !bLenient));

        } catch (DOMStructureException ex) {
            domMatch.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }
        return domMatch;
    }

    public static boolean repair(Node nodeMatch) throws DOMStructureException {
        Element elementMatch = DOMUtil.getElement(nodeMatch);
        boolean result = false;

        NodeList children = elementMatch.getChildNodes();
        int numChildren;
        boolean sawAttributeRetrievalBase = false;
        boolean sawAttributeValue = false;

        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_ATTRIBUTEVALUE.equals(childName)) {
                            if (sawAttributeValue) {
                                logger.warn("Unexpected element " + child.getNodeName());
                                elementMatch.removeChild(child);
                                result = true;
                            } else {
                                result = DOMAttributeValue.repair(child) || result;
                                sawAttributeValue = true;
                            }
                        } else if (XACML3.ELEMENT_ATTRIBUTEDESIGNATOR.equals(childName)) {
                            if (sawAttributeRetrievalBase) {
                                logger.warn("Unexpected element " + child.getNodeName());
                                elementMatch.removeChild(child);
                                result = true;
                            } else {
                                result = DOMAttributeDesignator.repair(child) || result;
                                sawAttributeRetrievalBase = true;
                            }
                        } else if (XACML3.ELEMENT_ATTRIBUTESELECTOR.equals(childName)) {
                            if (sawAttributeRetrievalBase) {
                                logger.warn("Unexpected element " + child.getNodeName());
                                elementMatch.removeChild(child);
                                result = true;
                            } else {
                                result = DOMAttributeSelector.repair(child) || result;
                                sawAttributeRetrievalBase = true;
                            }
                        } else {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementMatch.removeChild(child);
                            result = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementMatch.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        /*
         * We have to see exactly one of these
         */
        if (!sawAttributeRetrievalBase) {
            throw DOMUtil.newMissingElementException(nodeMatch, XACML3.XMLNS,
                                                     XACML3.ELEMENT_ATTRIBUTEDESIGNATOR + " or "
                                                         + XACML3.ELEMENT_ATTRIBUTESELECTOR);
        } else if (!sawAttributeValue) {
            throw DOMUtil.newMissingElementException(nodeMatch, XACML3.XMLNS, XACML3.ELEMENT_ATTRIBUTEVALUE);
        }
        result = DOMUtil.repairIdentifierAttribute(elementMatch, XACML3.ATTRIBUTE_MATCHID, logger) || result;

        return result;
    }
}
