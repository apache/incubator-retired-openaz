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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.Condition;
import org.apache.openaz.xacml.pdp.policy.Policy;
import org.apache.openaz.xacml.pdp.policy.Rule;
import org.apache.openaz.xacml.pdp.policy.RuleEffect;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMRule extends {@link org.apache.openaz.xacml.pdp.policy.Rule} with methods for creation from DOM
 * {@link org.w3c.dom.Node}s.
 */
public class DOMRule extends Rule {
    private static final Log logger = LogFactory.getLog(DOMRule.class);

    protected DOMRule() {
    }

    /**
     * Creates a new <code>Rule</code> by parsing the given <code>Node</code> representing a XACML Rule
     * element.
     *
     * @param nodeRule the <code>Node</code> representing the XACML Rule element
     * @param policy the {@link org.apache.openaz.xacml.pdp.policy.Policy} encompassing the Rule element
     * @return a new <code>Rule</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static Rule newInstance(Node nodeRule, Policy policy) throws DOMStructureException {
        Element elementRule = DOMUtil.getElement(nodeRule);
        boolean bLenient = DOMProperties.isLenient();

        DOMRule domRule = new DOMRule();

        domRule.setPolicy(policy);

        Iterator<?> iterator;

        try {
            NodeList children = elementRule.getChildNodes();
            int numChildren;
            if (children != null && (numChildren = children.getLength()) > 0) {
                for (int i = 0; i < numChildren; i++) {
                    Node child = children.item(i);
                    if (DOMUtil.isElement(child)) {
                        if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                            String childName = child.getLocalName();
                            if (XACML3.ELEMENT_DESCRIPTION.equals(childName)) {
                                if (domRule.getDescription() != null && !bLenient) {
                                    throw DOMUtil.newUnexpectedElementException(child, nodeRule);
                                }
                                domRule.setDescription(child.getTextContent());
                            } else if (XACML3.ELEMENT_TARGET.equals(childName)) {
                                if (domRule.getTarget() != null && !bLenient) {
                                    throw DOMUtil.newUnexpectedElementException(child, nodeRule);
                                }
                                domRule.setTarget(DOMTarget.newInstance(child));
                            } else if (XACML3.ELEMENT_CONDITION.equals(childName)) {
                                if (domRule.getCondition() != null && !bLenient) {
                                    throw DOMUtil.newUnexpectedElementException(child, nodeRule);
                                }
                                Node nodeExpression = DOMUtil.getFirstChildElement(child);
                                if (nodeExpression == null && !bLenient) {
                                    throw DOMUtil.newMissingElementException(child, XACML3.XMLNS,
                                                                             XACML3.ELEMENT_EXPRESSION);
                                }
                                domRule.setCondition(new Condition(DOMExpression.newInstance(nodeExpression,
                                                                                             policy)));
                            } else if (XACML3.ELEMENT_OBLIGATIONEXPRESSIONS.equals(childName)) {
                                if ((iterator = domRule.getObligationExpressions()) != null
                                    && iterator.hasNext() && !bLenient) {
                                    throw DOMUtil.newUnexpectedElementException(child, nodeRule);
                                }
                                domRule.setObligationExpressions(DOMObligationExpression.newList(child,
                                                                                                 policy));
                            } else if (XACML3.ELEMENT_ADVICEEXPRESSIONS.equals(childName)) {
                                if ((iterator = domRule.getAdviceExpressions()) != null && iterator.hasNext()
                                    && !bLenient) {
                                    throw DOMUtil.newUnexpectedElementException(child, nodeRule);
                                }
                                domRule.setAdviceExpressions(DOMAdviceExpression.newList(child, policy));
                            } else if (!bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodeRule);
                            }
                        } else if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeRule);
                        }
                    }
                }
            }

            domRule.setRuleId(DOMUtil.getStringAttribute(elementRule, XACML3.ATTRIBUTE_RULEID, !bLenient));
            String string = DOMUtil.getStringAttribute(elementRule, XACML3.ATTRIBUTE_EFFECT, !bLenient);
            RuleEffect ruleEffect = RuleEffect.getRuleEffect(string);
            if (ruleEffect == null && !bLenient) {
                throw new DOMStructureException(elementRule, "Unknown RuleEffect \"" + string + "\" in \""
                                                             + DOMUtil.getNodeLabel(nodeRule) + "\"");
            }
            domRule.setRuleEffect(ruleEffect);

        } catch (DOMStructureException ex) {
            domRule.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }
        return domRule;
    }

    public static boolean repair(Node nodeRule) throws DOMStructureException {
        Element elementRule = DOMUtil.getElement(nodeRule);
        boolean result = false;

        NodeList children = elementRule.getChildNodes();
        int numChildren;
        boolean sawDescription = false;
        boolean sawTarget = false;
        boolean sawCondition = false;
        boolean sawObligationExpressions = false;
        boolean sawAdviceExpressions = false;

        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_DESCRIPTION.equals(childName)) {
                            if (sawDescription) {
                                logger.warn("Unexpected element " + child.getNodeName());
                                elementRule.removeChild(child);
                                result = true;
                            } else {
                                sawDescription = true;
                            }
                        } else if (XACML3.ELEMENT_TARGET.equals(childName)) {
                            if (sawTarget) {
                                logger.warn("Unexpected element " + child.getNodeName());
                                elementRule.removeChild(child);
                                result = true;
                            } else {
                                sawTarget = true;
                                result = DOMTarget.repair(child) || result;
                            }
                        } else if (XACML3.ELEMENT_CONDITION.equals(childName)) {
                            if (sawCondition) {
                                logger.warn("Unexpected element " + child.getNodeName());
                                elementRule.removeChild(child);
                                result = true;
                            } else {
                                sawCondition = true;
                                Node nodeExpression = DOMUtil.getFirstChildElement(child);
                                if (nodeExpression == null) {
                                    throw DOMUtil.newMissingElementException(child, XACML3.XMLNS,
                                                                             XACML3.ELEMENT_EXPRESSION);
                                }
                                result = DOMExpression.repair(nodeExpression) || result;
                            }
                        } else if (XACML3.ELEMENT_OBLIGATIONEXPRESSIONS.equals(childName)) {
                            if (sawObligationExpressions) {
                                logger.warn("Unexpected element " + child.getNodeName());
                                elementRule.removeChild(child);
                                result = true;
                            } else {
                                sawObligationExpressions = true;
                                result = DOMObligationExpression.repairList(child) || result;
                            }
                        } else if (XACML3.ELEMENT_ADVICEEXPRESSIONS.equals(childName)) {
                            if (sawAdviceExpressions) {
                                logger.warn("Unexpected element " + child.getNodeName());
                                elementRule.removeChild(child);
                                result = true;
                            } else {
                                sawAdviceExpressions = true;
                                result = DOMAdviceExpression.repairList(child) || result;
                            }
                        } else {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementRule.removeChild(child);
                            result = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementRule.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        result = DOMUtil.repairStringAttribute(elementRule, XACML3.ATTRIBUTE_RULEID, IdentifierImpl.gensym()
            .stringValue(), logger)
                 || result;
        result = DOMUtil.repairStringAttribute(elementRule, XACML3.ATTRIBUTE_EFFECT,
                                               RuleEffect.DENY.getName(), logger)
                 || result;

        String string = DOMUtil.getStringAttribute(elementRule, XACML3.ATTRIBUTE_EFFECT);
        RuleEffect ruleEffect = RuleEffect.getRuleEffect(string);
        if (ruleEffect == null) {
            logger.warn("Setting invalid " + XACML3.ATTRIBUTE_EFFECT + " attribute " + string + " to "
                        + RuleEffect.DENY.getName());
            elementRule.setAttribute(XACML3.ATTRIBUTE_EFFECT, RuleEffect.DENY.getName());
            result = true;
        }

        return result;
    }
}
