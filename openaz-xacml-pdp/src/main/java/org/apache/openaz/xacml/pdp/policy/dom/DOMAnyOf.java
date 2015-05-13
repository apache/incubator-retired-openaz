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
import org.apache.openaz.xacml.pdp.policy.AnyOf;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMAnyOf extends {@link org.apache.openaz.xacml.pdp.policy.AnyOf} with methods for creation from DOM
 * {@link org.w3c.dom.Node}s.
 */
public class DOMAnyOf extends AnyOf {
    private static final Log logger = LogFactory.getLog(DOMAnyOf.class);

    protected DOMAnyOf() {
    }

    /**
     * Creates a new <code>DOMAnyOf</code> by parsing the given <code>Node</code> representing a XACML AnyOf
     * element.
     *
     * @param nodeAnyOf the <code>Node</code> representing the XACML AnyOf element
     * @return a new <code>DOMAnyOf</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the given <code>Node</code>.
     */
    public static AnyOf newInstance(Node nodeAnyOf) throws DOMStructureException {
        Element elementAnyOf = DOMUtil.getElement(nodeAnyOf);
        boolean bLenient = DOMProperties.isLenient();

        DOMAnyOf domAnyOf = new DOMAnyOf();

        try {
            NodeList children = elementAnyOf.getChildNodes();
            int numChildren;
            if (children != null && (numChildren = children.getLength()) > 0) {
                for (int i = 0; i < numChildren; i++) {
                    Node child = children.item(i);
                    if (DOMUtil.isElement(child)) {
                        if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                            && XACML3.ELEMENT_ALLOF.equals(child.getLocalName())) {
                            domAnyOf.addAllOf(DOMAllOf.newInstance(child));
                        } else if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeAnyOf);
                        }
                    }
                }
            }
        } catch (DOMStructureException ex) {
            domAnyOf.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }

        return domAnyOf;
    }

    public static boolean repair(Node nodeAnyOf) throws DOMStructureException {
        Element elementAnyOf = DOMUtil.getElement(nodeAnyOf);
        boolean result = false;

        NodeList children = elementAnyOf.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_ALLOF.equals(child.getLocalName())) {
                        result = DOMAllOf.repair(child) || result;
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementAnyOf.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        return result;
    }
}
