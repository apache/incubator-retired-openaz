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
import org.apache.openaz.xacml.api.RequestAttributesReference;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdRequestAttributesReference;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOMRequestAttributesReference extends {@link org.apache.openaz.xacml.std.StdRequestAttributesReference} with
 * methods for creation from DOM {@link org.w3c.dom.Node}s.
 */
public class DOMRequestAttributesReference {
    private static final Log logger = LogFactory.getLog(DOMRequestAttributesReference.class);

    protected DOMRequestAttributesReference() {
    }

    /**
     * Creates a new <code>DOMRequestAttributesReference</code> by parsing the given root <code>Node</code> of
     * a XACML AttributesReference element.
     *
     * @param nodeAttributesReference the <code>Node</code> to parse
     * @return a new <code>DOMRequestAttributesReference</code>
     * @throws org.apache.openaz.xacml.std.dom.DOMStructureException if the conversion cannot be made
     */
    public static RequestAttributesReference newInstance(Node nodeAttributesReference)
        throws DOMStructureException {
        Element elementAttributesReference = DOMUtil.getElement(nodeAttributesReference);
        boolean bLenient = DOMProperties.isLenient();

        return new StdRequestAttributesReference(DOMUtil.getStringAttribute(elementAttributesReference,
                                                                            XACML3.ATTRIBUTE_REFERENCEID,
                                                                            !bLenient));
    }

    public static boolean repair(Node nodeAttributesReference) throws DOMStructureException {
        Element elementAttributesReference = DOMUtil.getElement(nodeAttributesReference);
        return DOMUtil.repairStringAttribute(elementAttributesReference, XACML3.ATTRIBUTE_REFERENCEID, null,
                                             logger);
    }

}
