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
import org.apache.openaz.xacml.pdp.policy.expressions.AttributeDesignator;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOMAttributeDesignator extends {@link org.apache.openaz.xacml.pdp.policy.expressions.AttributeDesignator}
 * with methods for creation from DOM {@link org.w3c.dom.Node}s.
 */
public class DOMAttributeDesignator extends AttributeDesignator {
    private static final Log logger = LogFactory.getLog(DOMAttributeDesignator.class);

    protected DOMAttributeDesignator() {
    }

    /**
     * Creates a new <code>DOMAttributeDesignator</code> by parsing the given <code>Node</code> representing a
     * XACML AttributeDesignator element.
     *
     * @param nodeAttributeDesignator the <code>Node</code> representing the XACML AttributeDesignator element
     * @return a new <code>DOMAttributeDesignator</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static AttributeDesignator newInstance(Node nodeAttributeDesignator) throws DOMStructureException {
        Element elementAttributeDesignator = DOMUtil.getElement(nodeAttributeDesignator);
        boolean bLenient = DOMProperties.isLenient();

        DOMAttributeDesignator domAttributeDesignator = new DOMAttributeDesignator();

        try {
            domAttributeDesignator.setCategory(DOMUtil.getIdentifierAttribute(elementAttributeDesignator,
                                                                              XACML3.ATTRIBUTE_CATEGORY,
                                                                              !bLenient));
            domAttributeDesignator.setAttributeId(DOMUtil
                .getIdentifierAttribute(elementAttributeDesignator, XACML3.ATTRIBUTE_ATTRIBUTEID, !bLenient));
            domAttributeDesignator.setDataTypeId(DOMUtil.getIdentifierAttribute(elementAttributeDesignator,
                                                                                XACML3.ATTRIBUTE_DATATYPE,
                                                                                !bLenient));

            String string;
            if ((string = DOMUtil.getStringAttribute(elementAttributeDesignator, XACML3.ATTRIBUTE_ISSUER)) != null) {
                domAttributeDesignator.setIssuer(string);
            }
            Boolean mustBePresent = DOMUtil.getBooleanAttribute(elementAttributeDesignator,
                                                                XACML3.ATTRIBUTE_MUSTBEPRESENT, !bLenient);
            if (mustBePresent != null) {
                domAttributeDesignator.setMustBePresent(mustBePresent);
            }
        } catch (DOMStructureException ex) {
            domAttributeDesignator.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }

        return domAttributeDesignator;
    }

    public static boolean repair(Node nodeAttributeDesignator) throws DOMStructureException {
        Element elementAttributeDesignator = DOMUtil.getElement(nodeAttributeDesignator);
        boolean result = false;

        result = DOMUtil.repairIdentifierAttribute(elementAttributeDesignator, XACML3.ATTRIBUTE_CATEGORY,
                                                   logger) || result;
        result = DOMUtil.repairIdentifierAttribute(elementAttributeDesignator, XACML3.ATTRIBUTE_ATTRIBUTEID,
                                                   logger) || result;
        result = DOMUtil.repairIdentifierAttribute(elementAttributeDesignator, XACML3.ATTRIBUTE_DATATYPE,
                                                   logger) || result;
        result = DOMUtil.repairBooleanAttribute(elementAttributeDesignator, XACML3.ATTRIBUTE_MUSTBEPRESENT,
                                                false, logger) || result;

        return result;
    }

}
