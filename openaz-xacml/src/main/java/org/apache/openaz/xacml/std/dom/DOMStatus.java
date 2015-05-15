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
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdMutableStatus;
import org.apache.openaz.xacml.std.StdStatus;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMStatus extends {@link org.apache.openaz.xacml.std.StdMutableStatus} with methods for creation from DOM
 * {@link org.w3c.dom.Node}s.
 */
public class DOMStatus {
    private static final Log logger = LogFactory.getLog(DOMStatus.class);

    protected DOMStatus() {
    }

    /**
     * Creates a new <code>DOMStatus</code> by parsing the given <code>Node</code> representing a XACML Status
     * element.
     *
     * @param nodeStatus the <code>Node</code> representing the Status element
     * @return a new <code>DOMStatus</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if the conversion cannot be made
     */
    public static Status newInstance(Node nodeStatus) throws DOMStructureException {
        Element elementStatus = DOMUtil.getElement(nodeStatus);
        boolean bLenient = DOMProperties.isLenient();

        StdMutableStatus mutableStatus = new StdMutableStatus();

        NodeList children = elementStatus.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_STATUSCODE.equals(childName)) {
                            mutableStatus.setStatusCode(DOMStatusCode.newInstance(child));
                        } else if (XACML3.ELEMENT_STATUSMESSAGE.equals(childName)) {
                            mutableStatus.setStatusMessage(child.getTextContent());
                        } else if (XACML3.ELEMENT_STATUSDETAIL.equals(childName)) {
                            mutableStatus.setStatusDetail(DOMStatusDetail.newInstance(child));
                        } else {
                            if (!bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodeStatus);
                            }
                        }
                    } else {
                        if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeStatus);
                        }
                    }
                }
            }
        }

        if (mutableStatus.getStatusCode() == null && !bLenient) {
            throw DOMUtil.newMissingElementException(nodeStatus, XACML3.XMLNS, XACML3.ELEMENT_STATUSCODE);
        }

        return new StdStatus(mutableStatus);
    }

    public static boolean repair(Node nodeStatus) throws DOMStructureException {
        Element elementStatus = DOMUtil.getElement(nodeStatus);
        boolean result = false;

        NodeList children = elementStatus.getChildNodes();
        int numChildren;
        boolean sawStatusCode = false;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_STATUSCODE.equals(childName)) {
                            result = DOMStatusCode.repair(child) || result;
                            sawStatusCode = true;
                        //} else if (XACML3.ELEMENT_STATUSMESSAGE.equals(childName)) {
                        } else if (XACML3.ELEMENT_STATUSDETAIL.equals(childName)) {
                            result = DOMStatusDetail.repair(child) || result;
                        } else {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementStatus.removeChild(child);
                            result = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementStatus.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        if (!sawStatusCode) {
            throw DOMUtil.newMissingElementException(nodeStatus, XACML3.XMLNS, XACML3.ELEMENT_STATUSCODE);
        }

        return result;
    }
}
