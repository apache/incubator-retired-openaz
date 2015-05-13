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

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.XACML;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.PolicyDefaults;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMPolicyDefaults extends {@link org.apache.openaz.xacml.pdp.policy.PolicyDefaults} with methods for
 * creation from DOM {@org.w3c.dom.Node}s.
 */
public class DOMPolicyDefaults extends PolicyDefaults {
    private static final Log logger = LogFactory.getLog(DOMPolicyDefaults.class);

    protected DOMPolicyDefaults(URI xpathVersionIn, PolicyDefaults policyDefaultsParentIn) {
        super(xpathVersionIn, policyDefaultsParentIn);
    }

    /**
     * Creates a new <code>DOMPolicyDefaults</code> by parsing the given <code>Node</code> representing a
     * XACML PolicyDefaults element.
     *
     * @param nodePolicyDefaults the <code>Node</code> representing the PolicyDefaults element.
     * @param policyDefaultsParent the <code>PolicyDefaults</code> parent for the new
     *            <code>DOMPolicyDefaults</code>
     * @return a new <code>DOMPolicyDefaults</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if the conversion is not possible
     */
    public static PolicyDefaults newInstance(Node nodePolicyDefaults, PolicyDefaults policyDefaultsParent)
        throws DOMStructureException {
        Element elementPolicyDefaults = DOMUtil.getElement(nodePolicyDefaults);
        boolean bLenient = DOMProperties.isLenient();

        URI uriXPathVersion = null;

        NodeList children = elementPolicyDefaults.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_XPATHVERSION.equals(child.getLocalName())) {
                        uriXPathVersion = DOMUtil.getURIContent(child);
                    } else if (!bLenient) {
                        throw DOMUtil.newUnexpectedElementException(child, nodePolicyDefaults);
                    }
                }
            }
        }
        return new DOMPolicyDefaults(uriXPathVersion, policyDefaultsParent);
    }

    public static boolean repair(Node nodePolicyDefaults) throws DOMStructureException {
        Element elementPolicyDefaults = DOMUtil.getElement(nodePolicyDefaults);
        boolean result = false;

        NodeList children = elementPolicyDefaults.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_XPATHVERSION.equals(child.getLocalName())) {
                        try {
                            DOMUtil.getURIContent(child);
                        } catch (DOMStructureException ex) {
                            logger.warn("Setting invalid " + XACML3.ELEMENT_XPATHVERSION + " attribute "
                                        + child.getTextContent() + " to " + XACML.XPATHVERSION_2_0);
                            child.setTextContent(XACML.XPATHVERSION_2_0);
                            result = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementPolicyDefaults.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        return result;
    }
}
