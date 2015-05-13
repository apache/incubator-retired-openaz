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
import org.apache.openaz.xacml.pdp.policy.PolicySetChild;
import org.apache.openaz.xacml.pdp.policy.TargetedCombinerParameter;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMAttributeValue;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMPolicySetCombinerParameter extends
 * {@link org.apache.openaz.xacml.pdp.policy.TargetedCombinerParameter} for
 * {@link org.apache.openaz.xacml.pdp.policy.PolicySet}s with methods for creation from DOM
 * {@link org.w3c.dom.Node}s.
 */
public class DOMPolicySetCombinerParameter extends TargetedCombinerParameter<Identifier, PolicySetChild> {
    public static final Log logger = LogFactory.getLog(DOMPolicySetCombinerParameter.class);

    protected DOMPolicySetCombinerParameter() {

    }

    /**
     * Creates a new <code>TargetedCombinerParameter</code> for <code>PolicySet</code>s by parsing the given
     * <code>Node</code> representing a XACML PolicySetCombinerParameter element.
     *
     * @param nodeCombinerParameter the <code>Node</code> representing the XACML PolicySetCombinerParameter
     *            element
     * @return a new <code>TargetedCombinerParameter</code> for <code>PolicySet</code>s parsed from the given
     *         <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static TargetedCombinerParameter<Identifier, PolicySetChild> newInstance(Node nodeCombinerParameter)
        throws DOMStructureException {
        Element elementPolicySetCombinerParameter = DOMUtil.getElement(nodeCombinerParameter);
        boolean bLenient = DOMProperties.isLenient();

        DOMPolicySetCombinerParameter domPolicySetCombinerParameter = new DOMPolicySetCombinerParameter();

        try {
            NodeList children = elementPolicySetCombinerParameter.getChildNodes();
            int numChildren;
            if (children != null && (numChildren = children.getLength()) > 0) {
                for (int i = 0; i < numChildren; i++) {
                    Node child = children.item(i);
                    if (DOMUtil.isElement(child)) {
                        if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                            && XACML3.ELEMENT_ATTRIBUTEVALUE.equals(child.getLocalName())) {
                            if (domPolicySetCombinerParameter.getAttributeValue() != null && !bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodeCombinerParameter);
                            }
                            domPolicySetCombinerParameter.setAttributeValue(DOMAttributeValue
                                .newInstance(child, null));
                        } else if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeCombinerParameter);
                        }
                    }
                }
            }
            if (domPolicySetCombinerParameter.getAttributeValue() == null && !bLenient) {
                throw DOMUtil.newMissingElementException(elementPolicySetCombinerParameter, XACML3.XMLNS,
                                                         XACML3.ELEMENT_ATTRIBUTEVALUE);
            }
            domPolicySetCombinerParameter.setName(DOMUtil
                .getStringAttribute(elementPolicySetCombinerParameter, XACML3.ATTRIBUTE_PARAMETERNAME,
                                    !bLenient));
            domPolicySetCombinerParameter.setTargetId(DOMUtil
                .getIdentifierAttribute(elementPolicySetCombinerParameter, XACML3.ATTRIBUTE_POLICYSETIDREF,
                                        !bLenient));
        } catch (DOMStructureException ex) {
            domPolicySetCombinerParameter.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }

        return domPolicySetCombinerParameter;

    }

    public static boolean repair(Node nodeCombinerParameter) throws DOMStructureException {
        Element elementPolicySetCombinerParameter = DOMUtil.getElement(nodeCombinerParameter);
        boolean result = false;

        NodeList children = elementPolicySetCombinerParameter.getChildNodes();
        int numChildren;
        boolean sawAttributeValue = false;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_ATTRIBUTEVALUE.equals(child.getLocalName())) {
                        if (sawAttributeValue) {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementPolicySetCombinerParameter.removeChild(child);
                            result = true;
                        } else {
                            sawAttributeValue = true;
                            result = DOMAttributeValue.repair(child) || result;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementPolicySetCombinerParameter.removeChild(child);
                        result = true;
                    }
                }
            }
        }
        if (!sawAttributeValue) {
            throw DOMUtil.newMissingElementException(elementPolicySetCombinerParameter, XACML3.XMLNS,
                                                     XACML3.ELEMENT_ATTRIBUTEVALUE);
        }
        result = DOMUtil.repairStringAttribute(elementPolicySetCombinerParameter,
                                               XACML3.ATTRIBUTE_PARAMETERNAME, "parameter", logger) || result;
        result = DOMUtil.repairIdentifierAttribute(elementPolicySetCombinerParameter,
                                                   XACML3.ATTRIBUTE_POLICYSETIDREF, logger) || result;
        return result;
    }
}
