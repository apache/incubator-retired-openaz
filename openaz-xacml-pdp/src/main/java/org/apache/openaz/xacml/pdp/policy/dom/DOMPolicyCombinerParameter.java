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
 * DOMPolicyCombinerParameter extends {@link org.apache.openaz.xacml.pdp.policy.TargetedCombinerParameter}
 * for {@link org.apache.openaz.xacml.pdp.policy.Policy}s with methods for creation from DOM
 * {@link org.w3c.dom.Node}s.
 */
public class DOMPolicyCombinerParameter extends TargetedCombinerParameter<Identifier, PolicySetChild> {
    private static final Log logger = LogFactory.getLog(DOMPolicyCombinerParameter.class);

    protected DOMPolicyCombinerParameter() {

    }

    /**
     * Creates a new <code>TargetedCombinerParameter</code> for <code>Policy</code>s by parsing the given
     * <code>Node</code> representing a XACML PolicyCombinerParameter element.
     *
     * @param nodeCombinerParameter the <code>Node</code> representing the XACML PolicyCombinerParameter
     *            element
     * @return a new <code>TargetedCombinerParameter</code> for <code>Policy</code>s parsed from the given
     *         <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static TargetedCombinerParameter<Identifier, PolicySetChild> newInstance(Node nodeCombinerParameter)
        throws DOMStructureException {
        Element elementPolicyCombinerParameter = DOMUtil.getElement(nodeCombinerParameter);
        boolean bLenient = DOMProperties.isLenient();

        DOMPolicyCombinerParameter domPolicyCombinerParameter = new DOMPolicyCombinerParameter();

        try {
            NodeList children = elementPolicyCombinerParameter.getChildNodes();
            int numChildren;
            if (children != null && (numChildren = children.getLength()) > 0) {
                for (int i = 0; i < numChildren; i++) {
                    Node child = children.item(i);
                    if (DOMUtil.isElement(child)) {
                        if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                            && XACML3.ELEMENT_ATTRIBUTEVALUE.equals(child.getLocalName())) {
                            domPolicyCombinerParameter.setAttributeValue(DOMAttributeValue.newInstance(child,
                                                                                                       null));
                        } else if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeCombinerParameter);
                        }
                    }
                }
            }
            if (domPolicyCombinerParameter.getAttributeValue() == null && !bLenient) {
                throw DOMUtil.newMissingElementException(nodeCombinerParameter, XACML3.XMLNS,
                                                         XACML3.ELEMENT_ATTRIBUTEVALUE);
            }
            domPolicyCombinerParameter.setName(DOMUtil.getStringAttribute(elementPolicyCombinerParameter,
                                                                          XACML3.ATTRIBUTE_PARAMETERNAME,
                                                                          !bLenient));
            domPolicyCombinerParameter.setTargetId(DOMUtil
                .getIdentifierAttribute(elementPolicyCombinerParameter, XACML3.ATTRIBUTE_POLICYIDREF,
                                        !bLenient));

        } catch (DOMStructureException ex) {
            domPolicyCombinerParameter.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }

        return domPolicyCombinerParameter;

    }

    public static boolean repair(Node nodePolicyCombinerParameter) throws DOMStructureException {
        Element elementPolicyCombinerParameter = DOMUtil.getElement(nodePolicyCombinerParameter);
        boolean result = false;

        NodeList children = elementPolicyCombinerParameter.getChildNodes();
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
                            elementPolicyCombinerParameter.removeChild(child);
                            result = true;
                        } else {
                            sawAttributeValue = true;
                            result = DOMAttributeValue.repair(child) || result;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementPolicyCombinerParameter.removeChild(child);
                        result = true;
                    }
                }
            }
        }
        if (!sawAttributeValue) {
            throw DOMUtil.newMissingElementException(nodePolicyCombinerParameter, XACML3.XMLNS,
                                                     XACML3.ELEMENT_ATTRIBUTEVALUE);
        }
        result = DOMUtil.repairStringAttribute(elementPolicyCombinerParameter,
                                               XACML3.ATTRIBUTE_PARAMETERNAME, "parameter", logger) || result;
        result = DOMUtil.repairIdentifierAttribute(elementPolicyCombinerParameter,
                                                   XACML3.ATTRIBUTE_POLICYIDREF, logger) || result;

        return result;
    }
}
