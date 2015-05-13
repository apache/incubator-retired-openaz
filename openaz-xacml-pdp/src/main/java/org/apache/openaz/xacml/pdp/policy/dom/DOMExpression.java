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
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.Expression;
import org.apache.openaz.xacml.pdp.policy.Policy;
import org.apache.openaz.xacml.pdp.policy.expressions.AttributeValueExpression;
import org.apache.openaz.xacml.pdp.policy.expressions.Function;
import org.apache.openaz.xacml.pdp.policy.expressions.VariableReference;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMAttributeValue;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOMExpression extends {@link org.apache.openaz.xacml.pdp.policy.Expression} with methods for creation
 * from DOM {@link org.w3c.dom.Node}s.
 */
public abstract class DOMExpression extends Expression {
    private static final Log logger = LogFactory.getLog(DOMExpression.class);

    protected DOMExpression() {
    }

    public static boolean isExpression(Node nodeExpression) {
        String nodeName = nodeExpression.getLocalName();
        return XACML3.ELEMENT_APPLY.equals(nodeName) || XACML3.ELEMENT_ATTRIBUTEDESIGNATOR.equals(nodeName)
                || XACML3.ELEMENT_ATTRIBUTESELECTOR.equals(nodeName)
                || XACML3.ELEMENT_ATTRIBUTEVALUE.equals(nodeName) || XACML3.ELEMENT_FUNCTION.equals(nodeName) || XACML3.ELEMENT_VARIABLEREFERENCE
            .equals(nodeName);
    }

    /**
     * Creates a new <code>Expression</code> of the appropriate sub-type based on the name of the given
     * <code>Node</code>.
     *
     * @param nodeExpression the <code>Node</code> to parse
     * @param policy the {@link org.apache.openaz.xacml.pdp.policy.Policy} containing the Expression element
     * @return a new <code>Expression</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static Expression newInstance(Node nodeExpression, Policy policy) throws DOMStructureException {
        Element elementExpression = DOMUtil.getElement(nodeExpression);
        boolean bLenient = DOMProperties.isLenient();

        if (DOMUtil.isInNamespace(elementExpression, XACML3.XMLNS)) {
            if (elementExpression.getLocalName().equals(XACML3.ELEMENT_APPLY)) {
                return DOMApply.newInstance(elementExpression, policy);
            } else if (elementExpression.getLocalName().equals(XACML3.ELEMENT_ATTRIBUTEDESIGNATOR)) {
                return DOMAttributeDesignator.newInstance(elementExpression);
            } else if (elementExpression.getLocalName().equals(XACML3.ELEMENT_ATTRIBUTESELECTOR)) {
                return DOMAttributeSelector.newInstance(elementExpression);
            } else if (elementExpression.getLocalName().equals(XACML3.ELEMENT_ATTRIBUTEVALUE)) {
                AttributeValue<?> attributeValue = null;
                try {
                    attributeValue = DOMAttributeValue.newInstance(elementExpression, null);
                } catch (DOMStructureException ex) {
                    return new AttributeValueExpression(StdStatusCode.STATUS_CODE_SYNTAX_ERROR,
                                                        ex.getMessage());
                }
                return new AttributeValueExpression(attributeValue);
            } else if (elementExpression.getLocalName().equals(XACML3.ELEMENT_FUNCTION)) {
                return new Function(DOMUtil.getIdentifierAttribute(elementExpression,
                                                                   XACML3.ATTRIBUTE_FUNCTIONID));
            } else if (elementExpression.getLocalName().equals(XACML3.ELEMENT_VARIABLEREFERENCE)) {
                return new VariableReference(policy, DOMUtil.getStringAttribute(elementExpression,
                                                                                XACML3.ATTRIBUTE_VARIABLEID));
            } else if (!bLenient) {
                throw DOMUtil.newUnexpectedElementException(nodeExpression);
            } else {
                return null;
            }
        } else if (!bLenient) {
            throw DOMUtil.newUnexpectedElementException(nodeExpression);
        } else {
            return null;
        }
    }

    public static boolean repair(Node nodeExpression) throws DOMStructureException {
        Element elementExpression = DOMUtil.getElement(nodeExpression);
        if (DOMUtil.isInNamespace(elementExpression, XACML3.XMLNS)) {
            if (elementExpression.getLocalName().equals(XACML3.ELEMENT_APPLY)) {
                return DOMApply.repair(elementExpression);
            } else if (elementExpression.getLocalName().equals(XACML3.ELEMENT_ATTRIBUTEDESIGNATOR)) {
                return DOMAttributeDesignator.repair(elementExpression);
            } else if (elementExpression.getLocalName().equals(XACML3.ELEMENT_ATTRIBUTESELECTOR)) {
                return DOMAttributeSelector.repair(elementExpression);
            } else if (elementExpression.getLocalName().equals(XACML3.ELEMENT_ATTRIBUTEVALUE)) {
                return DOMAttributeValue.repair(elementExpression);
            } else if (elementExpression.getLocalName().equals(XACML3.ELEMENT_FUNCTION)) {
                return DOMUtil.repairIdentifierAttribute(elementExpression, XACML3.ATTRIBUTE_FUNCTIONID,
                                                         XACML3.ID_FUNCTION_STRING_EQUAL, logger);
            } else if (elementExpression.getLocalName().equals(XACML3.ELEMENT_VARIABLEREFERENCE)) {
                return DOMUtil.repairStringAttribute(elementExpression, XACML3.ATTRIBUTE_VARIABLEID,
                                                     "variableId", logger);
            } else {
                throw DOMUtil.newUnexpectedElementException(nodeExpression);
            }
        } else {
            throw DOMUtil.newUnexpectedElementException(nodeExpression);
        }
    }
}
