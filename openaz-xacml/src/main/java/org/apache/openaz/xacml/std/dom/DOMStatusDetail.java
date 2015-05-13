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
import org.apache.openaz.xacml.api.StatusDetail;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdMutableStatusDetail;
import org.apache.openaz.xacml.std.StdStatusDetail;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMStatusDetail extends {@link org.apache.openaz.xacml.std.StdMutableStatusDetail} with methods for
 * construction from DOM {@link org.w3c.dom.Node}s.
 */
public class DOMStatusDetail {
    private static final Log logger = LogFactory.getLog(DOMStatusDetail.class);

    protected DOMStatusDetail() {
    }

    /**
     * Creates a new <code>DOMStatusDetail</code> by parsing the given <code>Node</code> representing a XACML
     * StatusDetail element.
     *
     * @param nodeStatusDetail the <code>Node</code> representing the StatusDetail element
     * @return a new <code>DOMStatusDetail</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if the conversion cannot be made
     */
    public static StatusDetail newInstance(Node nodeStatusDetail) throws DOMStructureException {
        Element elementStatusDetail = DOMUtil.getElement(nodeStatusDetail);
        boolean bLenient = DOMProperties.isLenient();

        StdMutableStatusDetail mutableStatusDetail = new StdMutableStatusDetail();

        NodeList children = elementStatusDetail.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_MISSINGATTRIBUTEDETAIL.equals(child.getLocalName())) {
                        mutableStatusDetail.addMissingAttributeDetail(DOMMissingAttributeDetail
                            .newInstance(child));
                    } else {
                        if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeStatusDetail);
                        }
                    }
                }
            }
        }

        return new StdStatusDetail(mutableStatusDetail);
    }

    public static boolean repair(Node nodeStatusDetail) throws DOMStructureException {
        Element elementStatusDetail = DOMUtil.getElement(nodeStatusDetail);
        boolean result = false;

        NodeList children = elementStatusDetail.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_MISSINGATTRIBUTEDETAIL.equals(child.getLocalName())) {
                        result = DOMMissingAttributeDetail.repair(child) || result;
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementStatusDetail.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        return result;
    }
}
