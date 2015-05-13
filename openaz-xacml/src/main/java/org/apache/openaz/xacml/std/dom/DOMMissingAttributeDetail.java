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
import org.apache.openaz.xacml.api.MissingAttributeDetail;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdMissingAttributeDetail;
import org.apache.openaz.xacml.std.StdMutableMissingAttributeDetail;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMMissingAttributeDetail extends {@link org.apache.openaz.xacml.std.StdMutableMissingAttributeDetail} with
 * methods for creation from DOM {@link org.w3c.dom.Node}s.
 */
public class DOMMissingAttributeDetail {
    private static final Log logger = LogFactory.getLog(DOMMissingAttributeDetail.class);

    protected DOMMissingAttributeDetail() {
    }

    /**
     * Creates a new <code>DOMMissingAttributeDetail</code> by parsing the given <code>Node</code> as a XACML
     * MissingAttributeDetail element.
     *
     * @param nodeMissingAttributeDetail the <code>Node</code> representing the MissingAttributeDetail element
     * @return a new <code>DOMMissingAttributeDetail</code> parsed from the given <code>Node</code>
     * @throws org.apache.openaz.xacml.std.dom.DOMStructureException if the conversion is not possible
     */
    public static MissingAttributeDetail newInstance(Node nodeMissingAttributeDetail)
        throws DOMStructureException {
        Element elementMissingAttributeDetail = DOMUtil.getElement(nodeMissingAttributeDetail);
        boolean bLenient = DOMProperties.isLenient();
        StdMutableMissingAttributeDetail mutableMissingAttributeDetail = new StdMutableMissingAttributeDetail();

        mutableMissingAttributeDetail.setCategory(DOMUtil
            .getIdentifierAttribute(elementMissingAttributeDetail, XACML3.ATTRIBUTE_CATEGORY, !bLenient));
        mutableMissingAttributeDetail.setAttributeId(DOMUtil
            .getIdentifierAttribute(elementMissingAttributeDetail, XACML3.ATTRIBUTE_ATTRIBUTEID, !bLenient));
        mutableMissingAttributeDetail.setDataTypeId(DOMUtil
            .getIdentifierAttribute(elementMissingAttributeDetail, XACML3.ATTRIBUTE_DATATYPE, !bLenient));
        mutableMissingAttributeDetail.setIssuer(DOMUtil.getStringAttribute(elementMissingAttributeDetail,
                                                                           XACML3.ATTRIBUTE_ISSUER));

        NodeList children = elementMissingAttributeDetail.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        if (XACML3.ELEMENT_ATTRIBUTEVALUE.equals(child.getLocalName())) {
                            mutableMissingAttributeDetail.addAttributeValue(DOMAttributeValue
                                .newInstance(child, mutableMissingAttributeDetail.getCategory()));
                        } else {
                            if (!bLenient) {
                                throw DOMUtil
                                    .newUnexpectedElementException(child, nodeMissingAttributeDetail);
                            }
                        }
                    } else {
                        if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeMissingAttributeDetail);
                        }
                    }
                }
            }
        }

        return new StdMissingAttributeDetail(mutableMissingAttributeDetail);
    }

    public static boolean repair(Node nodeMissingAttributeDetail) throws DOMStructureException {
        Element elementMissingAttributeDetail = DOMUtil.getElement(nodeMissingAttributeDetail);
        boolean result = false;

        result = DOMUtil.repairIdentifierAttribute(elementMissingAttributeDetail, XACML3.ATTRIBUTE_CATEGORY,
                                                   logger) || result;
        result = DOMUtil.repairIdentifierAttribute(elementMissingAttributeDetail,
                                                   XACML3.ATTRIBUTE_ATTRIBUTEID, logger) || result;
        result = DOMUtil.repairIdentifierAttribute(elementMissingAttributeDetail, XACML3.ATTRIBUTE_DATATYPE,
                                                   logger) || result;

        return result;
    }

}
