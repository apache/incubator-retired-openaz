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
package org.apache.openaz.xacml.std;

import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * Mutable implementation of the {@link org.apache.openaz.xacml.api.AttributeAssignment} interface.
 */
public class StdMutableAttributeAssignment implements AttributeAssignment {
    private Identifier attributeId;
    private Identifier category;
    private String issuer;
    private AttributeValue<?> attributeValue;

    /**
     * Creates an empty <code>StdMutableAttributeAssignment</code>.
     */
    public StdMutableAttributeAssignment() {

    }

    /**
     * Creates a new <code>StdMutableAttributeAssignment</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier}s for the XACML Category and AttributeId, the given
     * <code>String</code> issuer, and the given {@link org.apache.openaz.xacml.api.AttributeValue}.
     *
     * @param categoryIn the <code>Identifier</code> for the XACML Category of the new
     *            <code>StdMutableAttributeAssignment</code>
     * @param attributeIdIn the <code>Identifier</code> for the XACML AttributeId of the new
     *            <code>StdMutableAttributeAssignment</code>
     * @param issuerIn the <code>String</code> issuer for the XACML Attribute
     * @param attributeValueIn the <code>AttributeValue</code> for the new StdMutableAttributeAssignment
     */
    public StdMutableAttributeAssignment(Identifier categoryIn, Identifier attributeIdIn, String issuerIn,
                                         AttributeValue<?> attributeValueIn) {
        this.attributeId = attributeIdIn;
        this.category = categoryIn;
        this.issuer = issuerIn;
        this.attributeValue = attributeValueIn;
    }

    @Override
    public Identifier getDataTypeId() {
        return (this.attributeValue == null ? null : this.attributeValue.getDataTypeId());
    }

    @Override
    public AttributeValue<?> getAttributeValue() {
        return this.attributeValue;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.AttributeValue} representing the value to be assigned to the
     * XACML AttributeId associated with this <code>StdMutableAttributeAssignment</code>.
     *
     * @param attributeValueIn the <code>AttributeValue</code> representing the value to be assigned to the
     *            XACML AttributeId associated with this <code>StdMutableAttributeAssignment</code>
     */
    public void setAttributeValue(AttributeValue<?> attributeValueIn) {
        this.attributeValue = attributeValueIn;
    }

    @Override
    public Identifier getAttributeId() {
        return this.attributeId;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Identifier} representing the XACML AttributeId to be
     * assigned in this <code>StdMutableAttributeAssignment</code>.
     *
     * @param identifier the <code>Identifier</code> representing the XACML AttributeId to be assigned in this
     *            <code>StdMutableAttributeAssignment</code>
     */
    public void setAttributeId(Identifier identifier) {
        this.attributeId = identifier;
    }

    @Override
    public Identifier getCategory() {
        return this.category;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Identifier} representing the XACML Category of the
     * AttributeId to be assigned in this <code>StdMutableAttributeAssignment</code>.
     *
     * @param identifier the <code>Identifier</code> representing the XACML Category of the AttributeId to be
     *            assigned in this <code>StdMutableAttributeAssignment</code>.
     */
    public void setCategory(Identifier identifier) {
        this.category = identifier;
    }

    @Override
    public String getIssuer() {
        return this.issuer;
    }

    /**
     * Sets the <code>String</code> representing the Issuer of the XACML Attribute to be assigned in this
     * <code>StdMutableAttributeAssignment</code>.
     *
     * @param issuerIn the <code>String</code> representing the Issuer of the XACML Attribute to be assigned
     *            in this <code>StdMutableAttributeAssignment</code>.
     */
    public void setIssuer(String issuerIn) {
        this.issuer = issuerIn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof AttributeAssignment)) {
            return false;
        } else {
            AttributeAssignment objAttributeAssignment = (AttributeAssignment)obj;
            return ObjUtil.equalsAllowNull(this.getCategory(), objAttributeAssignment.getCategory())
                   && ObjUtil.equalsAllowNull(this.getAttributeId(), objAttributeAssignment.getAttributeId())
                   && ObjUtil.equalsAllowNull(this.getAttributeValue(),
                                              objAttributeAssignment.getAttributeValue())
                   && ObjUtil.equalsAllowNull(this.getIssuer(), objAttributeAssignment.getIssuer());
        }
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (getCategory() != null) {
            result = 31 * result + getCategory().hashCode();
        }
        if (getAttributeId() != null) {
            result = 31 * result + getAttributeId().hashCode();
        }
        if (getAttributeValue() != null) {
            result = 31 * result + getAttributeValue().hashCode();
        }
        if (getIssuer() != null) {
            result = 31 * result + getIssuer().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        boolean needsComma = false;
        Object objectToDump;
        if ((objectToDump = this.getAttributeId()) != null) {
            stringBuilder.append("attributeId=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }
        if ((objectToDump = this.getCategory()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("category=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }
        if ((objectToDump = this.getIssuer()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("issuer=");
            stringBuilder.append((String)objectToDump);
            needsComma = true;
        }
        if (this.attributeValue != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("attributeValue=");
            stringBuilder.append(this.attributeValue.toString());
            needsComma = true;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
