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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.MissingAttributeDetail;
import org.apache.openaz.xacml.util.ListUtil;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * Mutable implementation of the {@link org.apache.openaz.xacml.api.MissingAttributeDetail} interface.
 *
 */
public class StdMutableMissingAttributeDetail implements MissingAttributeDetail {
    private static final List<AttributeValue<?>> EMPTY_LIST = Collections
        .unmodifiableList(new ArrayList<AttributeValue<?>>());

    private Identifier category;
    private Identifier attributeId;
    private Identifier dataTypeId;
    private String issuer;
    private List<AttributeValue<?>> attributeValues;

    /**
     * Creates a new empty <code>StdMutableMissingAttributeDetail</code>.
     */
    public StdMutableMissingAttributeDetail() {
        this.attributeValues = EMPTY_LIST;
    }

    /**
     * Creates a new <code>StdMutableMissingAttributeDetail</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier}s for the XACML Category, AttributeId, and DataType of the
     * missing Attribute, and the given <code>String</code> Issuer. If not null, the <code>Collection</code>
     * of {@link org.apache.openaz.xacml.api.AttributeValue}s is copied into the new
     * <code>StdMutableMissingAttributeDetail</code>.
     *
     * @param categoryIn the <code>Identifier</code> representing the XACML Category of the missing Attribute
     * @param attributeIdIn the <code>Identifier</code> representing the XACML AttributeId of the missing
     *            Attribute
     * @param dataTypeIdIn the <code>Identifier</code> representing the XACML DataType of the missing
     *            AttributeValue
     * @param issuerIn the <code>String</code> representing the XACML Issuer (may be null)
     * @param attributeValuesIn the <code>Collection</code> of <code>AttributeValue</code>s representing the
     *            expected AttributeValues for the missing Attribute
     */
    public StdMutableMissingAttributeDetail(Identifier categoryIn, Identifier attributeIdIn,
                                            Identifier dataTypeIdIn, String issuerIn,
                                            Collection<AttributeValue<?>> attributeValuesIn) {
        this.category = categoryIn;
        this.attributeId = attributeIdIn;
        this.dataTypeId = dataTypeIdIn;
        this.issuer = issuerIn;
        if (attributeValuesIn != null && attributeValuesIn.size() > 0) {
            this.attributeValues = new ArrayList<AttributeValue<?>>();
            this.attributeValues.addAll(attributeValuesIn);
        } else {
            this.attributeValues = EMPTY_LIST;
        }
    }

    /**
     * Creates a new <code>StdMutableMissingAttributeDetail</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier}s for the XACML Category, AttributeId, and DataType of the
     * missing Attribute, and the given <code>String</code> Issuer.
     *
     * @param categoryIn the <code>Identifier</code> representing the XACML Category of the missing Attribute
     * @param attributeIdIn the <code>Identifier</code> representing the XACML AttributeId of the missing
     *            Attribute
     * @param dataTypeIdIn the <code>Identifier</code> representing the XACML DataType of the missing
     *            AttributeValue
     * @param issuerIn the <code>String</code> representing the XACML Issuer (may be null)
     */
    public StdMutableMissingAttributeDetail(Identifier categoryIn, Identifier attributeIdIn,
                                            Identifier dataTypeIdIn, String issuerIn) {
        this(categoryIn, attributeIdIn, dataTypeIdIn, issuerIn, null);
    }

    /**
     * Creates a new <code>StdMutableMissingAttributeDetail</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier}s for the XACML Category, AttributeId, and DataType of the
     * missing Attribute.
     *
     * @param categoryIn the <code>Identifier</code> representing the XACML Category of the missing Attribute
     * @param attributeIdIn the <code>Identifier</code> representing the XACML AttributeId of the missing
     *            Attribute
     * @param dataTypeIdIn the <code>Identifier</code> representing the XACML DataType of the missing
     *            AttributeValue
     */
    public StdMutableMissingAttributeDetail(Identifier categoryIn, Identifier attributeIdIn,
                                            Identifier dataTypeIdIn) {
        this(categoryIn, attributeIdIn, dataTypeIdIn, null);
    }

    /**
     * Creates a new <code>StdMutableMissingAttributeDetail</code> that is a copy of the given
     * {@link org.apache.openaz.xacml.api.MissingAttributeDetail}.
     *
     * @param missingAttributeDetail the <code>MissingAttributeDetail</code> to copy
     * @return a new <code>StdMutableMissingAttributeDetail</code> that is a copy of the given
     *         <code>MissingAttributeDetail</code>.
     */
    public static StdMutableMissingAttributeDetail copy(MissingAttributeDetail missingAttributeDetail) {
        return new StdMutableMissingAttributeDetail(missingAttributeDetail.getCategory(),
                                                    missingAttributeDetail.getAttributeId(),
                                                    missingAttributeDetail.getDataTypeId(),
                                                    missingAttributeDetail.getIssuer(),
                                                    missingAttributeDetail.getAttributeValues());
    }

    @Override
    public Identifier getCategory() {
        return this.category;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Identifier} representing the XACML Category of the
     * MissingAttributeDetail.
     *
     * @param identifier the <code>Identifier</code> representing the XACML Category of the
     *            MissingAttributeDetail.
     */
    public void setCategory(Identifier identifier) {
        this.category = identifier;
    }

    @Override
    public Identifier getAttributeId() {
        return this.attributeId;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Identifier} representing the XACML AttributeId of the
     * MissingAttributeDetail.
     *
     * @param identifier the <code>Identifier</code> representing the XACML AttributeId of the
     *            MissingAttributeDetail.
     */
    public void setAttributeId(Identifier identifier) {
        this.attributeId = identifier;
    }

    @Override
    public Identifier getDataTypeId() {
        return this.dataTypeId;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Identifier} representing the XACML DataTypeId of the
     * MissingAttributeDetail.
     *
     * @param identifier the <code>Identifier</code> representing the XACML AttributeId of the
     *            MissingAttributeDetail.
     */
    public void setDataTypeId(Identifier identifier) {
        this.dataTypeId = identifier;
    }

    @Override
    public String getIssuer() {
        return this.issuer;
    }

    public void setIssuer(String issuerIn) {
        this.issuer = issuerIn;
    }

    @Override
    public Collection<AttributeValue<?>> getAttributeValues() {
        return (this.attributeValues == EMPTY_LIST ? this.attributeValues : Collections
            .unmodifiableCollection(this.attributeValues));
    }

    /**
     * Adds an {@link org.apache.openaz.xacml.api.AttributeValue} to this
     * <code>StdMutableMissingAttributeDetail</code>.
     *
     * @param attributeValue the <code>AttributeValue</code> to add to this
     *            <code>StdMutableMissingAttributeDetail</code>.
     */
    public void addAttributeValue(AttributeValue<?> attributeValue) {
        if (this.attributeValues == EMPTY_LIST) {
            this.attributeValues = new ArrayList<AttributeValue<?>>();
        }
        this.attributeValues.add(attributeValue);
    }

    /**
     * Adds a copy of the given <code>Collection</code> of {@link org.apache.openaz.xacml.api.AttributeValue}s
     * to this <code>StdMutableMissingAttributeDetail</code>.
     *
     * @param attributeValuesIn the <code>Collection</code> of <code>AttributeValue</code>s to add to this
     *            <code>StdMutableMissingAttributeDetail</code>.
     */
    public void addAttributeValues(Collection<AttributeValue<?>> attributeValuesIn) {
        if (attributeValuesIn != null && attributeValuesIn.size() > 0) {
            if (this.attributeValues == EMPTY_LIST) {
                this.attributeValues = new ArrayList<AttributeValue<?>>();
            }
            this.attributeValues.addAll(attributeValuesIn);
        }
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.AtributeValue}s in this
     * <code>StdMutableMissingAttributeDetail</code> to a copy of the given <code>Collection</code>. If the
     * <code>Collection</code> is null or empty, the list of <code>AttributeValue</code>s in is set to the
     * empty list.
     *
     * @param attributeValuesIn the <code>Collection</code> of <code>AttributeValue</code>s to set in this
     *            <code>StdMutableMissingAttributeDetail</code>.
     */
    public void setAttributeValues(Collection<AttributeValue<?>> attributeValuesIn) {
        this.attributeValues = EMPTY_LIST;
        this.addAttributeValues(attributeValuesIn);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof MissingAttributeDetail)) {
            return false;
        } else {
            MissingAttributeDetail objMissingAttributeDetail = (MissingAttributeDetail)obj;
            return ObjUtil.equalsAllowNull(this.getCategory(), objMissingAttributeDetail.getCategory())
                   && ObjUtil.equalsAllowNull(this.getAttributeId(),
                                              objMissingAttributeDetail.getAttributeId())
                   && ObjUtil
                       .equalsAllowNull(this.getDataTypeId(), objMissingAttributeDetail.getDataTypeId())
                   && ObjUtil.equalsAllowNull(this.getIssuer(), objMissingAttributeDetail.getIssuer())
                   && ListUtil.equalsAllowNulls(this.getAttributeValues(),
                                                objMissingAttributeDetail.getAttributeValues());
        }
    }
    
    public int hashCode() {
        int result = 17;
        if (getCategory() != null) {
            result = 31 * result + getCategory().hashCode();
        }
        if (getAttributeId() != null) {
            result = 31 * result + getAttributeId().hashCode();
        }
        if (getDataTypeId() != null) {
            result = 31 * result + getDataTypeId().hashCode();
        }
        if (getIssuer() != null) {
            result = 31 * result + getIssuer().hashCode();
        }
        if (getAttributeValues() != null) {
            result = 31 * result + getAttributeValues().hashCode();
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
        if ((objectToDump = this.getDataTypeId()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("dataTypeId=");
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
        Collection<AttributeValue<?>> listAttributeValues = this.getAttributeValues();
        if (listAttributeValues.size() > 0) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("attributeValues=");
            stringBuilder.append(ListUtil.toString(listAttributeValues));
            needsComma = true;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
