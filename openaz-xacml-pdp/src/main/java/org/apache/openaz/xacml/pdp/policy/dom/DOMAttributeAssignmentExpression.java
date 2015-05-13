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
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.AttributeAssignmentExpression;
import org.apache.openaz.xacml.pdp.policy.Policy;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOMAttributeAssignmentExpression extends
 * {@link org.apache.openaz.xacml.pdp.policy.AttributeAssignmentExpression} with methods for creation from
 * {@link org.w3c.dom.Node}s.
 */
public class DOMAttributeAssignmentExpression extends AttributeAssignmentExpression {
    private static final Log logger = LogFactory.getLog(DOMAttributeAssignmentExpression.class);

    protected DOMAttributeAssignmentExpression() {
    }

    /**
     * Creates a new <code>AttributeAssignmentExpression</code> by parsing the given <code>Node</code>
     * representing a XACML AttributeAssignmentExpression element.
     *
     * @param nodeAttributeAssignmentExpression the <code>Node</code> representing the XACML
     *            AttributeAssignmentExpression element
     * @return a new <code>AttributeAssignmentExpression</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static AttributeAssignmentExpression newInstance(Node nodeAttributeAssignmentExpression,
                                                            Policy policy) throws DOMStructureException {
        Element elementAttributeAssignmentExpression = DOMUtil.getElement(nodeAttributeAssignmentExpression);
        boolean bLenient = DOMProperties.isLenient();

        DOMAttributeAssignmentExpression domAttributeAssignmentExpression = new DOMAttributeAssignmentExpression();

        try {
            Node node = DOMUtil.getFirstChildElement(elementAttributeAssignmentExpression);
            if (node == null) {
                if (!bLenient) {
                    throw DOMUtil.newMissingElementException(elementAttributeAssignmentExpression,
                                                             XACML3.XMLNS, XACML3.ELEMENT_EXPRESSION);
                }
            } else {
                domAttributeAssignmentExpression.setExpression(DOMExpression.newInstance(node, policy));
            }

            Identifier identifier;
            domAttributeAssignmentExpression.setAttributeId(DOMUtil
                .getIdentifierAttribute(elementAttributeAssignmentExpression, XACML3.ATTRIBUTE_ATTRIBUTEID,
                                        !bLenient));
            if ((identifier = DOMUtil.getIdentifierAttribute(elementAttributeAssignmentExpression,
                                                             XACML3.ATTRIBUTE_CATEGORY)) != null) {
                domAttributeAssignmentExpression.setCategory(identifier);
            }

            String issuer = DOMUtil.getStringAttribute(elementAttributeAssignmentExpression,
                                                       XACML3.ATTRIBUTE_ISSUER);
            if (issuer != null) {
                domAttributeAssignmentExpression.setIssuer(issuer);
            }
        } catch (DOMStructureException ex) {
            domAttributeAssignmentExpression.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR,
                                                       ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }

        return domAttributeAssignmentExpression;
    }

    public static boolean repair(Node nodeAttributeAssignmentExpression) throws DOMStructureException {
        Element elementAttributeAssignmentExpression = DOMUtil.getElement(nodeAttributeAssignmentExpression);
        boolean result = false;

        if (DOMUtil.getFirstChildElement(elementAttributeAssignmentExpression) == null) {
            /*
             * See if we can repair the <AttributeAssignmentExpression
             * DataType="">string</AttributeAssignmentExpression> pattern
             */
            Identifier identifier = DOMUtil.getIdentifierAttribute(elementAttributeAssignmentExpression,
                                                                   XACML3.ATTRIBUTE_DATATYPE);
            String textContent = elementAttributeAssignmentExpression.getTextContent();
            if (textContent != null) {
                textContent = textContent.trim();
            }
            if (textContent != null && textContent.length() > 0 && identifier != null) {
                Element attributeValue = elementAttributeAssignmentExpression.getOwnerDocument()
                    .createElementNS(XACML3.XMLNS, XACML3.ELEMENT_ATTRIBUTEVALUE);
                attributeValue.setAttribute(XACML3.ATTRIBUTE_DATATYPE, identifier.stringValue());
                attributeValue.setTextContent(textContent);
                logger.warn("Adding a new AttributeValue using the DataType from the AttributeAssignment");
                elementAttributeAssignmentExpression.removeAttribute(XACML3.ATTRIBUTE_DATATYPE);
                while (elementAttributeAssignmentExpression.hasChildNodes()) {
                    elementAttributeAssignmentExpression.removeChild(elementAttributeAssignmentExpression
                        .getFirstChild());
                }
                elementAttributeAssignmentExpression.appendChild(attributeValue);
                result = true;
            } else {
                throw DOMUtil.newMissingElementException(elementAttributeAssignmentExpression, XACML3.XMLNS,
                                                         XACML3.ELEMENT_EXPRESSION);
            }
        }
        result = DOMUtil.repairIdentifierAttribute(elementAttributeAssignmentExpression,
                                                   XACML3.ATTRIBUTE_ATTRIBUTEID, logger) || result;

        return result;
    }
}
