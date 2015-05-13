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

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.RequestDefaults;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdRequestDefaults;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMRequestDefaults extends {@link org.apache.openaz.xacml.std.StdRequestDefaults} with methods for creation
 * from DOM {@link org.w3c.dom.Node}s.
 */
public class DOMRequestDefaults {
    private static final Log logger = LogFactory.getLog(DOMRequestDefaults.class);

    protected DOMRequestDefaults() {
        super();
    }

    /**
     * Creates a new <code>DOMRequestDefaults</code> by parsing the given <code>Node</code> representing a
     * XACML RequestDefaults element.
     *
     * @param nodeRequestDefaults the <code>Node</code> representing the XACML RequestDefaults element
     * @return a new <code>DOMRequestDefaults</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if the conversion cannot be made
     */
    public static RequestDefaults newInstance(Node nodeRequestDefaults) throws DOMStructureException {
        Element elementRequestDefaults = DOMUtil.getElement(nodeRequestDefaults);
        boolean bLenient = DOMProperties.isLenient();

        URI uriXPathVersion = null;

        NodeList children = elementRequestDefaults.getChildNodes();
        int numChildren;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)
                        && XACML3.ELEMENT_XPATHVERSION.equals(child.getLocalName())) {
                        uriXPathVersion = DOMUtil.getURIContent(child);
                    } else {
                        if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeRequestDefaults);
                        }
                    }
                }
            }
        }
        return new StdRequestDefaults(uriXPathVersion);
    }

    public static boolean repair(Node nodeRequestDefaults) throws DOMStructureException {
        Element elementRequestDefaults = DOMUtil.getElement(nodeRequestDefaults);
        boolean result = false;

        NodeList children = elementRequestDefaults.getChildNodes();
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
                            logger.warn("Deleting invalid XPathVersion " + child.getTextContent());
                            elementRequestDefaults.removeChild(child);
                            result = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementRequestDefaults.removeChild(child);
                        result = true;
                    }
                }
            }
        }

        return result;
    }

}
