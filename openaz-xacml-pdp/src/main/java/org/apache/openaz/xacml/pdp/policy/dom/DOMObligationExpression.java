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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.ObligationExpression;
import org.apache.openaz.xacml.pdp.policy.Policy;
import org.apache.openaz.xacml.pdp.policy.RuleEffect;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMObligationExpression extends {@link org.apache.openaz.xacml.pdp.policy.ObligationExpression} with
 * methods for creation from {@link org.w3c.dom.Node}s.
 */
public class DOMObligationExpression extends ObligationExpression {
    private static final Log logger = LogFactory.getLog(DOMObligationExpression.class);

    protected DOMObligationExpression() {
    }

    /**
     * Creates a new <code>ObligationExpression</code> by parsing the given <code>Node</code> representing a
     * XACML ObligationExpression element.
     *
     * @param nodeObligationExpression the <code>Node</code> representing the XACML ObligationExpression
     *            element
     * @param policy the {@link org.apache.openaz.xacml.pdp.policy.Policy} encompassing the
     *            ObligationExpression element
     * @return a new <code>ObligationExpression</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static ObligationExpression newInstance(Node nodeObligationExpression, Policy policy)
        throws DOMStructureException {
        Element elementObligationExpression = DOMUtil.getElement(nodeObligationExpression);
        boolean bLenient = DOMProperties.isLenient();

        DOMObligationExpression domObligationExpression = new DOMObligationExpression();

        try {
            NodeList children = elementObligationExpression.getChildNodes();
            int numChildren;
            if (children != null && (numChildren = children.getLength()) > 0) {
                for (int i = 0; i < numChildren; i++) {
                    Node child = children.item(i);
                    if (DOMUtil.isElement(child)) {
                        if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                            && XACML3.ELEMENT_ATTRIBUTEASSIGNMENTEXPRESSION.equals(child.getLocalName())) {
                            domObligationExpression
                                .addAttributeAssignmentExpression(DOMAttributeAssignmentExpression
                                    .newInstance(child, policy));
                        } else if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeObligationExpression);
                        }
                    }
                }
            }

            domObligationExpression
                .setObligationId(DOMUtil.getIdentifierAttribute(elementObligationExpression,
                                                                XACML3.ATTRIBUTE_OBLIGATIONID, !bLenient));

            String string = DOMUtil.getStringAttribute(elementObligationExpression,
                                                       XACML3.ATTRIBUTE_FULFILLON, !bLenient);
            RuleEffect ruleEffectType = RuleEffect.getRuleEffect(string);
            if (ruleEffectType == null) {
                if (!bLenient) {
                    throw new DOMStructureException(nodeObligationExpression,
                                                    "Invalid EffectType \"" + string + "\" in \""
                                                        + DOMUtil.getNodeLabel(nodeObligationExpression)
                                                        + "\"");
                }
            } else {
                domObligationExpression.setRuleEffect(ruleEffectType);
            }
        } catch (DOMStructureException ex) {
            domObligationExpression.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }
        return domObligationExpression;
    }

    public static boolean repair(Node nodeObligationExpression) throws DOMStructureException {
        Element elementObligationExpression = DOMUtil.getElement(nodeObligationExpression);
        boolean result = false;

        NodeList children = elementObligationExpression.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_ATTRIBUTEASSIGNMENTEXPRESSION.equals(child.getLocalName())) {
                        result = DOMAttributeAssignmentExpression.repair(child) || result;
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementObligationExpression.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        result = DOMUtil.repairIdentifierAttribute(elementObligationExpression,
                                                   XACML3.ATTRIBUTE_OBLIGATIONID, logger) || result;
        result = DOMUtil.repairStringAttribute(elementObligationExpression, XACML3.ATTRIBUTE_FULFILLON,
                                               RuleEffect.DENY.getName(), logger) || result;

        String string = DOMUtil.getStringAttribute(elementObligationExpression, XACML3.ATTRIBUTE_FULFILLON);
        RuleEffect ruleEffectType = RuleEffect.getRuleEffect(string);
        if (ruleEffectType == null) {
            logger.warn("Setting invalid RuleEffect " + string + " to " + RuleEffect.DENY.getName());
            elementObligationExpression.setAttribute(XACML3.ATTRIBUTE_FULFILLON, RuleEffect.DENY.getName());
            result = true;
        }

        return result;
    }

    /**
     * Creates a <code>List</code> of <code>ObligationExpression</code>s by parsing the given
     * <code>Node</code> representing a XACML ObligationExpressions element.
     *
     * @param nodeObligationExpressions the <code>Node</code> representing the XACML ObligationExpressions
     *            element
     * @param policy the <code>Policy</code> encompassing the ObligationExpressions element
     * @return a new <code>List</code> of <code>ObligationExpression</code>s parsed from the given
     *         <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static List<ObligationExpression> newList(Node nodeObligationExpressions, Policy policy)
        throws DOMStructureException {
        Element elementObligationExpressions = DOMUtil.getElement(nodeObligationExpressions);
        boolean bLenient = DOMProperties.isLenient();

        List<ObligationExpression> listObligationExpressions = new ArrayList<ObligationExpression>();

        NodeList children = elementObligationExpressions.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_OBLIGATIONEXPRESSION.equals(child.getLocalName())) {
                        listObligationExpressions.add(DOMObligationExpression.newInstance(child, policy));
                    } else if (!bLenient) {
                        throw DOMUtil.newUnexpectedElementException(child, elementObligationExpressions);
                    }
                }
            }
        }

        if (listObligationExpressions.size() == 0 && !bLenient) {
            throw DOMUtil.newMissingElementException(elementObligationExpressions, XACML3.XMLNS,
                                                     XACML3.ELEMENT_OBLIGATIONEXPRESSION);
        }

        return listObligationExpressions;
    }

    public static boolean repairList(Node nodeObligationExpressions) throws DOMStructureException {
        Element elementObligationExpressions = DOMUtil.getElement(nodeObligationExpressions);
        boolean result = false;

        boolean sawObligationExpression = false;
        NodeList children = elementObligationExpressions.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_OBLIGATIONEXPRESSION.equals(child.getLocalName())) {
                        result = DOMObligationExpression.repair(child) || result;
                        sawObligationExpression = true;
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementObligationExpressions.removeChild(child);
                        result = true;
                    }
                }
            }
        }
        if (!sawObligationExpression) {
            throw DOMUtil.newMissingElementException(elementObligationExpressions, XACML3.XMLNS,
                                                     XACML3.ELEMENT_OBLIGATIONEXPRESSION);
        }

        return result;
    }

}
