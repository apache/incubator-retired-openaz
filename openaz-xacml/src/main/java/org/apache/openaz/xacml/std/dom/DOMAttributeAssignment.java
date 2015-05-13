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
import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.DataTypeFactory;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdAttributeAssignment;
import org.apache.openaz.xacml.std.StdMutableAttributeAssignment;
import org.apache.openaz.xacml.util.FactoryException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DOMAttributeAssignment {
    private static final Log logger = LogFactory.getLog(DOMAttributeAssignment.class);

    protected DOMAttributeAssignment() {
    }

    /**
     * Creates a new <code>DOMAttributeAssignment</code> from the given <code>Node</code> by parsing it as a
     * XACML 3.0 AttributeAssignment element.
     *
     * @param nodeAttributeAssignment the root <code>Node</code> of the AttributeAssignment element
     * @return a new <code>DOMAttributeAssignment</code> parsed from the given AttributeAssignment
     *         <code>Node</code>
     * @throws IllegalArgumentException
     */
    public static AttributeAssignment newInstance(Node nodeAttributeAssignment) throws DOMStructureException {
        Element elementAttributeAssignment = DOMUtil.getElement(nodeAttributeAssignment);
        boolean bLenient = DOMProperties.isLenient();
        StdMutableAttributeAssignment mutableAttributeAssignment = new StdMutableAttributeAssignment();

        mutableAttributeAssignment.setAttributeId(DOMUtil
            .getIdentifierAttribute(elementAttributeAssignment, XACML3.ATTRIBUTE_ATTRIBUTEID, !bLenient));
        Identifier identifierDataTypeId = DOMUtil
            .getIdentifierAttribute(elementAttributeAssignment, XACML3.ATTRIBUTE_DATATYPE, !bLenient);
        DataTypeFactory dataTypeFactory = null;
        try {
            dataTypeFactory = DataTypeFactory.newInstance();
            if (dataTypeFactory == null) {
                throw new DOMStructureException("Failed to get DataTypeFactory");
            }
        } catch (FactoryException ex) {
            throw new DOMStructureException("FactoryException loading DataTypeFactory: " + ex.getMessage(),
                                            ex);
        }
        DataType<?> dataType = dataTypeFactory.getDataType(identifierDataTypeId);
        if (dataType == null) {
            throw new DOMStructureException(elementAttributeAssignment,
                                            "Unknown dataType \"" + identifierDataTypeId.toString()
                                                + "\" in \""
                                                + DOMUtil.getNodeLabel(elementAttributeAssignment));
        }

        AttributeValue<?> attributeValue = null;
        try {
            attributeValue = dataType.createAttributeValue(elementAttributeAssignment);
        } catch (DataTypeException ex) {
            if (!bLenient) {
                throw new DOMStructureException("DataTypeException creating AttributeValue from \""
                                                + DOMUtil.getNodeLabel(elementAttributeAssignment)
                                                + "\" contents", ex);
            }
        }
        if (attributeValue == null && !bLenient) {
            throw new DOMStructureException(elementAttributeAssignment,
                                            "Failed to create AttributeValue from \""
                                                + DOMUtil.getNodeLabel(elementAttributeAssignment)
                                                + "\" contents");
        }
        mutableAttributeAssignment.setAttributeValue(attributeValue);

        mutableAttributeAssignment.setCategory(DOMUtil.getIdentifierAttribute(elementAttributeAssignment,
                                                                              XACML3.ATTRIBUTE_CATEGORY));
        mutableAttributeAssignment.setIssuer(DOMUtil.getStringAttribute(elementAttributeAssignment,
                                                                        XACML3.ATTRIBUTE_ISSUER));

        return new StdAttributeAssignment(mutableAttributeAssignment);
    }

    public static boolean repair(Node nodeAttributeAssignment) throws DOMStructureException {
        Element elementAttributeAssignment = DOMUtil.getElement(nodeAttributeAssignment);
        boolean result = false;

        result = DOMUtil.repairIdentifierAttribute(elementAttributeAssignment, XACML3.ATTRIBUTE_ATTRIBUTEID,
                                                   logger) || result;
        result = DOMUtil.repairIdentifierAttribute(elementAttributeAssignment, XACML3.ATTRIBUTE_DATATYPE,
                                                   logger) || result;

        DataTypeFactory dataTypeFactory = null;
        try {
            dataTypeFactory = DataTypeFactory.newInstance();
            if (dataTypeFactory == null) {
                throw new DOMStructureException("Failed to get DataTypeFactory");
            }
        } catch (FactoryException ex) {
            throw new DOMStructureException("FactoryException loading DataTypeFactory: " + ex.getMessage(),
                                            ex);
        }
        Identifier identifierDataType = DOMUtil.getIdentifierAttribute(elementAttributeAssignment,
                                                                       XACML3.ATTRIBUTE_DATATYPE);
        DataType<?> dataType = dataTypeFactory.getDataType(identifierDataType);
        if (dataType == null) {
            logger.warn("Changing unknown DataType " + identifierDataType.stringValue() + " to "
                        + XACML3.ID_DATATYPE_STRING.stringValue());
            elementAttributeAssignment.setAttribute(XACML3.ATTRIBUTE_DATATYPE,
                                                    XACML3.ID_DATATYPE_STRING.stringValue());
            result = true;
        }

        return result;
    }

}
