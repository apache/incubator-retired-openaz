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
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.DataTypeFactory;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdAttributeValue;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.apache.openaz.xacml.util.FactoryException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOMAttributeValue extends {@link org.apache.openaz.xacml.std.StdAttributeValue} with methods for creation
 * from DOM elements.
 *
 * @param <T> the Java type implementing the value for the Attribute
 */
public class DOMAttributeValue<T> extends StdAttributeValue<T> {
    private static Log logger = LogFactory.getLog(DOMAttributeValue.class);

    protected DOMAttributeValue(Identifier dataTypeIdIn, T valueIn) {
        super(dataTypeIdIn, valueIn);
    }

    public static AttributeValue<?> newInstance(Node nodeAttributeValue, Identifier category)
        throws DOMStructureException {
        repair(nodeAttributeValue);

        Element elementAttributeValue = DOMUtil.getElement(nodeAttributeValue);
        boolean bLenient = DOMProperties.isLenient();

        Identifier identifierDataTypeId = DOMUtil
            .getIdentifierAttribute(elementAttributeValue, XACML3.ATTRIBUTE_DATATYPE, !bLenient);

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
        DataType<?> dataTypeExtended = dataTypeFactory.getDataType(identifierDataTypeId);
        if (dataTypeExtended == null) {
            throw new DOMStructureException(elementAttributeValue, "Unknown dataTypeId \""
                                                                   + identifierDataTypeId.toString()
                                                                   + "\" in \""
                                                                   + DOMUtil.getNodeLabel(nodeAttributeValue));
        }
        AttributeValue<?> attributeValue = null;
        try {
            attributeValue = dataTypeExtended.createAttributeValue(elementAttributeValue);
            if (!bLenient && attributeValue != null && attributeValue.getXPathCategory() != null
                && category != null && !category.equals(attributeValue.getXPathCategory())) {
                throw new DOMStructureException(elementAttributeValue,
                                                "AttributeValue XPathCategory does not match "
                                                    + category.stringValue());
            }
        } catch (DataTypeException ex) {
            throw new DOMStructureException("Unable to convert \"" + DOMUtil.getNodeLabel(nodeAttributeValue)
                                            + "\" to \"" + identifierDataTypeId.toString() + "\"");
        }
        return attributeValue;
    }

    public static boolean repair(Node nodeAttributeValue) throws DOMStructureException {
        Element elementAttributeValue = DOMUtil.getElement(nodeAttributeValue);
        boolean result = false;

        result = DOMUtil.repairIdentifierAttribute(elementAttributeValue, XACML3.ATTRIBUTE_DATATYPE, logger)
                 || result;
        Identifier identifierDataTypeId = DOMUtil.getIdentifierAttribute(elementAttributeValue,
                                                                         XACML3.ATTRIBUTE_DATATYPE);
        try {
            DataTypeFactory dataTypeFactory = DataTypeFactory.newInstance();
            DataType<?> dataTypeExtended = dataTypeFactory.getDataType(identifierDataTypeId);

            if (dataTypeExtended == null) {
                if (identifierDataTypeId.equals(XACML.ID_DATATYPE_WD_DAYTIMEDURATION)) {
                    dataTypeExtended = DataTypes.DT_DAYTIMEDURATION;
                } else if (identifierDataTypeId.equals(XACML.ID_DATATYPE_WD_YEARMONTHDURATION)) {
                    dataTypeExtended = DataTypes.DT_YEARMONTHDURATION;
                } else {
                    dataTypeExtended = DataTypes.DT_STRING;
                }
                logger.warn("Changing unknown DataType " + identifierDataTypeId.stringValue() + " to "
                            + dataTypeExtended.getId().stringValue());
                elementAttributeValue.setAttribute(XACML3.ATTRIBUTE_DATATYPE, dataTypeExtended.getId()
                    .stringValue());
                result = true;
            }
            dataTypeExtended.createAttributeValue(nodeAttributeValue);

            if (result) {
                // reset the DataType attribute of the node
                nodeAttributeValue.getAttributes().getNamedItem("DataType")
                    .setNodeValue(dataTypeExtended.getId().stringValue());
            }

        } catch (Exception ex) {
            throw new DOMStructureException("Unable to convert \"" + DOMUtil.getNodeLabel(nodeAttributeValue)
                                            + "\" to \"" + identifierDataTypeId.toString() + "\"");
        }
        return result;
    }

}
