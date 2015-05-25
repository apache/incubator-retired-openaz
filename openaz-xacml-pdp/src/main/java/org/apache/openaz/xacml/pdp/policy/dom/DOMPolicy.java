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

import java.io.File;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.CombiningAlgorithm;
import org.apache.openaz.xacml.pdp.policy.CombiningAlgorithmFactory;
import org.apache.openaz.xacml.pdp.policy.Policy;
import org.apache.openaz.xacml.pdp.policy.PolicyDefaults;
import org.apache.openaz.xacml.pdp.policy.PolicySet;
import org.apache.openaz.xacml.pdp.policy.Rule;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMPolicy extends {@link org.apache.openaz.xacml.pdp.policy.Policy} with methods for creation from a DOM
 * {@link org.w3c.dom.Node}.
 */
public class DOMPolicy {
    private static final Log logger = LogFactory.getLog(DOMPolicy.class);

    /**
     * Creates a new <code>DOMPolicy</code> to be configured from a DOM <code>Node</code>.
     */
    protected DOMPolicy() {
    }

    /**
     * Creates a new <code>DOMPolicy</code> by parsing the given <code>Node</code> representing a XACML Policy
     * element.
     *
     * @param nodePolicy the <code>Node</code> representing the Policy element
     * @param policyDefaultsParent the <code>PolicyDefaults</code> of the parent element of the Policy element
     *            or null if this is the root
     * @return a new <code>DOMPolicy</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static Policy newInstance(Node nodePolicy, PolicySet policySetParent,
                                     PolicyDefaults policyDefaultsParent) throws DOMStructureException {
        Element elementPolicy = DOMUtil.getElement(nodePolicy);
        boolean bLenient = DOMProperties.isLenient();

        Policy domPolicy = new Policy(policySetParent);

        Identifier identifier;
        Integer integer;
        Iterator<?> iterator;

        try {
            NodeList children = elementPolicy.getChildNodes();
            int numChildren;
            if (children != null && (numChildren = children.getLength()) > 0) {
                /*
                 * Run through once, quickly, to set the PolicyDefaults for the new DOMPolicySet
                 */
                for (int i = 0; i < numChildren; i++) {
                    Node child = children.item(i);
                    if (DOMUtil.isNamespaceElement(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_POLICYDEFAULTS.equals(child.getLocalName())) {
                        if (domPolicy.getPolicyDefaults() != null && !bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodePolicy);
                        }
                        domPolicy.setPolicyDefaults(DOMPolicyDefaults
                            .newInstance(child, policyDefaultsParent));
                    }
                }
                if (domPolicy.getPolicyDefaults() == null) {
                    domPolicy.setPolicyDefaults(policyDefaultsParent);
                }

                for (int i = 0; i < numChildren; i++) {
                    Node child = children.item(i);
                    if (DOMUtil.isElement(child) && DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_DESCRIPTION.equals(childName)) {
                            if (domPolicy.getDescription() != null && !bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodePolicy);
                            }
                            domPolicy.setDescription(child.getTextContent());
                        } else if (XACML3.ELEMENT_POLICYISSUER.equals(childName)) {
                            if (domPolicy.getPolicyIssuer() != null && !bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodePolicy);
                            }
                            domPolicy.setPolicyIssuer(DOMPolicyIssuer.newInstance(child));
                        } else if (XACML3.ELEMENT_POLICYDEFAULTS.equals(childName)) { //NOPMD
                            // TODO
                        } else if (XACML3.ELEMENT_TARGET.equals(childName)) {
                            if (domPolicy.getTarget() != null && !bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodePolicy);
                            }
                            domPolicy.setTarget(DOMTarget.newInstance(child));
                        } else if (XACML3.ELEMENT_COMBINERPARAMETERS.equals(childName)) {
                            domPolicy.addCombinerParameters(DOMCombinerParameter.newList(child));
                        } else if (XACML3.ELEMENT_RULECOMBINERPARAMETERS.equals(childName)) {
                            domPolicy.addRuleCombinerParameter(DOMRuleCombinerParameters
                                                               .newInstance(child));
                        } else if (XACML3.ELEMENT_VARIABLEDEFINITION.equals(childName)) {
                            domPolicy.addVariableDefinition(DOMVariableDefinition.newInstance(child,
                                                                                              domPolicy));
                        } else if (XACML3.ELEMENT_RULE.equals(childName)) {
                            domPolicy.addRule(DOMRule.newInstance(child, domPolicy));
                        } else if (XACML3.ELEMENT_OBLIGATIONEXPRESSIONS.equals(childName)) {
                            if ((iterator = domPolicy.getObligationExpressions()) != null
                                && iterator.hasNext() && !bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodePolicy);
                            }
                            domPolicy
                            .setObligationExpressions(DOMObligationExpression.newList(child,
                                                                                      domPolicy));
                        } else if (XACML3.ELEMENT_ADVICEEXPRESSIONS.equals(childName)) {
                            if ((iterator = domPolicy.getAdviceExpressions()) != null
                                && iterator.hasNext() && !bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodePolicy);
                            }
                            domPolicy.setAdviceExpressions(DOMAdviceExpression.newList(child, domPolicy));
                        } else if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodePolicy);
                        }
                    }
                }
            }
            domPolicy.setIdentifier(DOMUtil.getIdentifierAttribute(elementPolicy, XACML3.ATTRIBUTE_POLICYID,
                                                                   !bLenient));
            domPolicy.setVersion(DOMUtil.getVersionAttribute(elementPolicy, XACML3.ATTRIBUTE_VERSION,
                                                             !bLenient));

            identifier = DOMUtil.getIdentifierAttribute(elementPolicy, XACML3.ATTRIBUTE_RULECOMBININGALGID,
                                                        !bLenient);
            CombiningAlgorithm<Rule> combiningAlgorithmRule = null;
            try {
                combiningAlgorithmRule = CombiningAlgorithmFactory.newInstance()
                    .getRuleCombiningAlgorithm(identifier);
            } catch (FactoryException ex) {
                if (!bLenient) {
                    throw new DOMStructureException("Failed to get CombiningAlgorithm", ex);
                }
            }
            if (combiningAlgorithmRule == null && !bLenient) {
                throw new DOMStructureException(elementPolicy, "Unknown rule combining algorithm \""
                                                               + identifier.toString() + "\" in \""
                                                               + DOMUtil.getNodeLabel(nodePolicy));
            } else {
                domPolicy.setRuleCombiningAlgorithm(combiningAlgorithmRule);
            }

            if ((integer = DOMUtil.getIntegerAttribute(elementPolicy, XACML3.ATTRIBUTE_MAXDELEGATIONDEPTH)) != null) {
                domPolicy.setMaxDelegationDepth(integer);
            }
        } catch (DOMStructureException ex) {
            domPolicy.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }

        return domPolicy;
    }

    public static boolean repair(Node nodePolicy) throws DOMStructureException {
        Element elementPolicy = DOMUtil.getElement(nodePolicy);
        boolean result = false;

        NodeList children = elementPolicy.getChildNodes();
        int numChildren;
        boolean sawDescription = false;
        boolean sawIssuer = false;
        boolean sawTarget = false;
        boolean sawPolicyDefaults = false;
        boolean sawObligationExprs = false;
        boolean sawAdviceExprs = false;

        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child) && DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                    String childName = child.getLocalName();
                    if (XACML3.ELEMENT_DESCRIPTION.equals(childName)) {
                        if (sawDescription) {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementPolicy.removeChild(child);
                            result = true;
                        } else {
                            sawDescription = true;
                        }
                    } else if (XACML3.ELEMENT_POLICYISSUER.equals(childName)) {
                        if (sawIssuer) {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementPolicy.removeChild(child);
                            result = true;
                        } else {
                            sawDescription = true;
                            result = DOMPolicyIssuer.repair(child) || result;
                        }
                    } else if (XACML3.ELEMENT_POLICYDEFAULTS.equals(childName)) {
                        if (sawPolicyDefaults) {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementPolicy.removeChild(child);
                            result = true;
                        } else {
                            sawPolicyDefaults = true;
                            result = DOMPolicyDefaults.repair(child) || result;
                        }
                    } else if (XACML3.ELEMENT_TARGET.equals(childName)) {
                        if (sawTarget) {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementPolicy.removeChild(child);
                            result = true;
                        } else {
                            sawTarget = true;
                            result = DOMTarget.repair(child) || result;
                        }
                    } else if (XACML3.ELEMENT_COMBINERPARAMETERS.equals(childName)) {
                        result = DOMCombinerParameter.repair(child) || result;
                    } else if (XACML3.ELEMENT_RULECOMBINERPARAMETERS.equals(childName)) {
                        result = DOMRuleCombinerParameters.repair(child) || result;
                    } else if (XACML3.ELEMENT_VARIABLEDEFINITION.equals(childName)) {
                        result = DOMVariableDefinition.repair(child) || result;
                    } else if (XACML3.ELEMENT_RULE.equals(childName)) {
                        result = DOMRule.repair(child) || result;
                    } else if (XACML3.ELEMENT_OBLIGATIONEXPRESSIONS.equals(childName)) {
                        if (sawObligationExprs) {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementPolicy.removeChild(child);
                            result = true;
                        } else {
                            sawObligationExprs = true;
                            result = DOMObligationExpression.repairList(child) || result;
                        }
                    } else if (XACML3.ELEMENT_ADVICEEXPRESSIONS.equals(childName)) {
                        if (sawAdviceExprs) {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementPolicy.removeChild(child);
                            result = true;
                        } else {
                            sawAdviceExprs = true;
                            result = DOMAdviceExpression.repairList(child) || result;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementPolicy.removeChild(child);
                        result = true;
                    }
                }
            }
        }
        result = DOMUtil.repairIdentifierAttribute(elementPolicy, XACML3.ATTRIBUTE_POLICYID, logger)
                 || result;
        result = DOMUtil.repairVersionAttribute(elementPolicy, XACML3.ATTRIBUTE_VERSION, logger) || result;
        result = DOMUtil.repairIdentifierAttribute(elementPolicy, XACML3.ATTRIBUTE_RULECOMBININGALGID,
                                                   XACML3.ID_RULE_DENY_OVERRIDES, logger) || result;

        Identifier identifier = DOMUtil.getIdentifierAttribute(elementPolicy,
                                                               XACML3.ATTRIBUTE_RULECOMBININGALGID);
        CombiningAlgorithm<Rule> combiningAlgorithmRule = null;
        try {
            combiningAlgorithmRule = CombiningAlgorithmFactory.newInstance()
                .getRuleCombiningAlgorithm(identifier);
        } catch (FactoryException ex) {
            combiningAlgorithmRule = null;
        }
        if (combiningAlgorithmRule == null) {
            logger.warn("Setting invalid " + XACML3.ATTRIBUTE_RULECOMBININGALGID + " attribute "
                        + identifier.stringValue() + " to " + XACML3.ID_RULE_DENY_OVERRIDES.stringValue());
            elementPolicy.setAttribute(XACML3.ATTRIBUTE_RULECOMBININGALGID,
                                       XACML3.ID_RULE_DENY_OVERRIDES.stringValue());
            result = true;
        }
        return result;
    }

    public static void main(String args[]) {
        try {
            for (String fileName : args) {
                File filePolicy = new File(fileName);
                if (filePolicy.exists() && filePolicy.canRead()) {
                    try {
                        Document documentPolicy = DOMUtil.loadDocument(filePolicy);
                        if (documentPolicy.getFirstChild() == null) {
                            System.err.println(fileName + ": Error: No Policy found");
                        } else if (!XACML3.ELEMENT_POLICY.equals(documentPolicy.getFirstChild()
                            .getLocalName())) {
                            System.err.println(fileName + ": Error: Not a Policy documnt");
                        } else {
                            Policy policy = DOMPolicy.newInstance(documentPolicy.getFirstChild(), null, null);
                            System.out.println(fileName + ": validate()=" + policy.validate());
                            System.out.println(StringUtils.prettyPrint(policy.toString()));
                        }
                    } catch (Exception ex) {
                        System.err.println("Exception processing policy file \"" + fileName + "\"");
                        ex.printStackTrace(System.err);
                    }
                } else {
                    System.err.println("Cannot read policy file \"" + fileName + "\"");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
        System.exit(0);
    }
}
