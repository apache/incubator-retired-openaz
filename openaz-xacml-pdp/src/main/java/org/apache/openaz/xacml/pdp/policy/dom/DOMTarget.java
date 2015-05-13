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
import org.apache.openaz.xacml.pdp.policy.Target;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMTarget extends {@link org.apache.openaz.xacml.pdp.policy.Target} with methods for creation from DOM
 * {@link org.w3c.dom.Node}s.
 */
public class DOMTarget extends Target {
    private static final Log logger = LogFactory.getLog(DOMTarget.class);

    /**
     * Creates an empty <code>DOMTarget</code>.
     */
    protected DOMTarget() {
    }

    /**
     * Creates a new <code>DOMTarget</code> by parsing the given <code>Node</code> representing a XACML Target
     * element.
     *
     * @param nodeTarget the <code>Node</code> representing the XACML Target element
     * @return a new <code>DOMTarget</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static Target newInstance(Node nodeTarget) throws DOMStructureException {
        Element elementTarget = DOMUtil.getElement(nodeTarget);
        boolean bLenient = DOMProperties.isLenient();

        DOMTarget domTarget = new DOMTarget();
        try {
            NodeList children = elementTarget.getChildNodes();
            int numChildren;
            if (children != null && (numChildren = children.getLength()) > 0) {
                for (int i = 0; i < numChildren; i++) {
                    Node child = children.item(i);
                    if (DOMUtil.isElement(child)) {
                        if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                            && XACML3.ELEMENT_ANYOF.equals(child.getLocalName())) {
                            domTarget.addAnyOf(DOMAnyOf.newInstance(child));
                        } else if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeTarget);
                        }
                    }
                }
            }
        } catch (DOMStructureException ex) {
            domTarget.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }

        return domTarget;
    }

    public static boolean repair(Node nodeTarget) throws DOMStructureException {
        Element elementTarget = DOMUtil.getElement(nodeTarget);
        boolean result = false;

        NodeList children = elementTarget.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_ANYOF.equals(child.getLocalName())) {
                        result = DOMAnyOf.repair(child) || result;
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementTarget.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        return result;
    }
}
