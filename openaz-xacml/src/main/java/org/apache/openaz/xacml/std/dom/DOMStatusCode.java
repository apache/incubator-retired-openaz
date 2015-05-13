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
package org.apache.openaz.xacml.std.dom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMStatusCode extends {@link org.apache.openaz.xacml.comomon.std.StdStatusCode} with methods for creation
 * from DOM {@link org.w3c.dom.Node}s.
 */
public class DOMStatusCode {
    private static final Log logger = LogFactory.getLog(DOMStatusCode.class);

    protected DOMStatusCode() {
    }

    /**
     * Creates a new <code>DOMStatusCode</code> by parsing the given <code>Node</code> representing a XACML
     * StatusCode element.
     *
     * @param nodeStatusCode the <code>Node</code> representing a StatusCode element
     * @return a new <code>DOMStatusCode</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if the conversion cannot be made
     */
    public static StatusCode newInstance(Node nodeStatusCode) throws DOMStructureException {
        Element elementStatusCode = DOMUtil.getElement(nodeStatusCode);
        boolean bLenient = DOMProperties.isLenient();

        Identifier identifierStatusCode = DOMUtil.getIdentifierAttribute(elementStatusCode,
                                                                         XACML3.ATTRIBUTE_VALUE, !bLenient);
        StatusCode statusCodeChild = null;

        NodeList children = elementStatusCode.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        if (child.getLocalName().equals(XACML3.ELEMENT_STATUSCODE)) {
                            if (statusCodeChild != null) {
                                if (!bLenient) {
                                    throw DOMUtil.newUnexpectedElementException(child, nodeStatusCode);
                                }
                            } else {
                                statusCodeChild = DOMStatusCode.newInstance(child);
                            }
                        } else {
                            if (!bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodeStatusCode);
                            }
                        }
                    } else {
                        if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeStatusCode);
                        }
                    }
                }
            }
        }
        return new StdStatusCode(identifierStatusCode, statusCodeChild);
    }

    public static boolean repair(Node nodeStatusCode) throws DOMStructureException {
        Element elementStatusCode = DOMUtil.getElement(nodeStatusCode);
        boolean result = false;

        result = DOMUtil.repairIdentifierAttribute(elementStatusCode, XACML3.ATTRIBUTE_VALUE, logger)
                 || result;

        NodeList children = elementStatusCode.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        result = DOMStatusCode.repair(child) || result;
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementStatusCode.removeChild(child);
                        result = true;
                    }
                }
            }
        }
        return result;
    }

}
