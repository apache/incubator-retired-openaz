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
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.util.ListUtil;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * Mutable implementation of the {@link org.apache.openaz.xacml.Attribute} interface.
 */
public class StdMutableAttribute implements Attribute {
    private static final List<AttributeValue<?>> EMPTY_ATTRIBUTE_VALUE_LIST = Collections
        .unmodifiableList(new ArrayList<AttributeValue<?>>());

    private Identifier attributeId;
    private Identifier category;
    private List<AttributeValue<?>> values;
    private String issuer;
    private boolean includeInResults;

    /**
     * Creates a new <code>StdMutableAttribute</code> with an empty list of
     * {@link org.apache.openaz.xacom.api.AttributeValue}s.
     */
    public StdMutableAttribute() {
        this.values = EMPTY_ATTRIBUTE_VALUE_LIST;
    }

    /**
     * Creates a new <code>StdMutableAttribute</code> with the given {@link org.apache.openaz.xacml.Identifier}
     * s for the XACML 3.0 Category and AttributeId properties, and the given <code>Collection</code> of
     * {@link org.apache.openaz.xacml.AttributeValue}s. The <code>String</code> issuer may be null.
     *
     * @param categoryIdIn the <code>Identifier</code> for the XACML 3.0 Category of the new
     *            <code>StdMutableAttribute</code>
     * @param attributeIdIn the <code>Identifier</code> for the XACML 3.0 AttributeId of the new
     *            <code>StdMutableAttribute</code>
     * @param valuesIn the <code>Collection</code> of <code>AttributeValue</code>s for the new
     *            <code>StdMutableAttribute</code>
     * @param issuerIn the <code>String</code> issuer of the new <code>StdMutableAttribute</code>
     * @param includeInResultsIn the <code>boolean</code> indicating whether this
     *            <code>StdMutableAttribute</code> should be in included in the XACML Response to a Request.
     */
    public StdMutableAttribute(Identifier categoryIdIn, Identifier attributeIdIn,
                               Collection<AttributeValue<?>> valuesIn, String issuerIn,
                               boolean includeInResultsIn) {
        this();
        this.attributeId = attributeIdIn;
        this.category = categoryIdIn;
        this.setValues(valuesIn);
        this.issuer = issuerIn;
        this.includeInResults = includeInResultsIn;
    }

    public StdMutableAttribute(Attribute attributeCopy) {
        this(attributeCopy.getCategory(), attributeCopy.getAttributeId(), attributeCopy.getValues(),
             attributeCopy.getIssuer(), attributeCopy.getIncludeInResults());
    }

    /**
     * Creates a new <code>StdMutableAttribute</code> with the given {@link org.apache.openaz.xacml.Identifier}
     * s for the XACML 3.0 Category and AttributeId properties, and the given
     * {@link org.apache.openaz.xacml.AttributeValue} as its only value. The <code>String</code> issuer may be
     * null.
     *
     * @param categoryIdIn the <code>Identifier</code> for the XACML 3.0 Category of the new
     *            <code>StdMutableAttribute</code>
     * @param attributeIdIn the <code>Identifier</code> for the XACML 3.0 AttributeId of the new
     *            <code>StdMutableAttribute</code>
     * @param valueIn the <code>AttributeValue</code> for the new <code>StdMutableAttribute</code>
     * @param issuerIn the <code>String</code> issuer of the new <code>StdMutableAttribute</code>
     * @param includeInResultsIn the <code>boolean</code> indicating whether this
     *            <code>StdMutableAttribute</code> should be in included in the XACML Response to a Request.
     */
    public StdMutableAttribute(Identifier categoryIdIn, Identifier attributeIdIn, AttributeValue<?> valueIn,
                               String issuerIn, boolean includeInResultsIn) {
        this();
        this.attributeId = attributeIdIn;
        this.category = categoryIdIn;
        if (valueIn != null) {
            this.addValue(valueIn);
        }
        this.issuer = issuerIn;
        this.includeInResults = includeInResultsIn;
    }

    /**
     * Creates a new <code>StdMutableAttribute</code> with the given {@link org.apache.openaz.xacml.Identifier}
     * s for the XACML 3.0 Category and AttributeId properties and the given
     * {@link org.apache.openaz.xacml.AttributeValue} as its only value. The issuer is null and the
     * <code>StdMutableAttribute</code> will not be included in XACML 3.0 responses.
     *
     * @param categoryIdIn the <code>Identifier</code> for the XACML 3.0 Category of the new
     *            <code>StdMutableAttribute</code>
     * @param attributeIdIn the <code>Identifier</code> for the XACML 3.0 AttributeId of the new
     *            <code>StdMutableAttribute</code>
     * @param valueIn the <code>AttributeValue</code> for the new <code>StdMutableAttribute</code>
     */
    public StdMutableAttribute(Identifier categoryIdIn, Identifier attributeIdIn, AttributeValue<?> valueIn) {
        this(categoryIdIn, attributeIdIn, valueIn, null, false);
    }

    /**
     * Creates a new <code>StdMutableAttribute</code> that is a copy of the given
     * {@link org.apache.openaz.xacml.api.Attribute}.
     *
     * @param attribute the <code>Attribute</code> to copy
     * @return a new <code>StdMutableAttribute</code> that is a copy of the given <code>Attribute</code>.
     */
    public static StdMutableAttribute copy(Attribute attribute) {
        return new StdMutableAttribute(attribute.getCategory(), attribute.getAttributeId(),
                                       attribute.getValues(), attribute.getIssuer(),
                                       attribute.getIncludeInResults());
    }

    @Override
    public Identifier getAttributeId() {
        return this.attributeId;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Identifier} representing the XACML AttributeId of the
     * Attribute represented by this <code>StdMutableAttribute</code>>
     *
     * @param identifier the <code>Identifier</code> representing the XACML AttributeId of the Attribute
     *            represented by this <code>StdMutableAttribute</code>.
     */
    public void setAttributeId(Identifier identifier) {
        this.attributeId = identifier;
    }

    @Override
    public Identifier getCategory() {
        return this.category;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Identifier} representing the XACML Category of the Attribute
     * represented by this <code>StdMutableAttribute</code>.
     *
     * @param identifier the <code>Identifier</code> representing the XACML Category of the Attribute
     *            represented by this <code>StdMutableAttribute</code>.
     */
    public void setCategory(Identifier identifier) {
        this.category = identifier;
    }

    @Override
    public Collection<AttributeValue<?>> getValues() {
        return this.values == EMPTY_ATTRIBUTE_VALUE_LIST ? this.values : Collections
            .unmodifiableCollection(this.values);
    }

    /**
     * Adds an {@link org.apache.openaz.xacml.api.AttributeValue} to this <code>StdMutableAttribute</code>.
     *
     * @param attributeValue the <code>AttributeValue</code> to add to this <code>StdMutableAttribute</code>.
     */
    public void addValue(AttributeValue<?> attributeValue) {
        if (this.values == EMPTY_ATTRIBUTE_VALUE_LIST) {
            this.values = new ArrayList<AttributeValue<?>>();
        }
        this.values.add(attributeValue);
    }

    /**
     * Adds a copy of the given <code>Collection</code> of {@link org.apache.openaz.xacml.api.AttributeValue}s
     * to this <code>StdMutableAttribute</code>.
     *
     * @param listAttributeValues the <code>Collection</code> of <code>AttributeValue</code>s to add to this
     *            <code>StdMutableAttribute</code>.
     */
    public void addValues(Collection<AttributeValue<?>> listAttributeValues) {
        if (listAttributeValues != null && listAttributeValues.size() > 0) {
            if (this.values == EMPTY_ATTRIBUTE_VALUE_LIST) {
                this.values = new ArrayList<AttributeValue<?>>();
            }
            this.values.addAll(listAttributeValues);
        }
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.AttributeValue}s in this <code>StdMutableAttribute</code> to
     * a copy of the given <code>Collection</code>.
     *
     * @param listAttributeValues the <code>Collection</code> of <code>AttributValue</code>s to set in this
     *            <code>StdMutableAttribute</code>.
     */
    public void setValues(Collection<AttributeValue<?>> listAttributeValues) {
        this.values = EMPTY_ATTRIBUTE_VALUE_LIST;
        this.addValues(listAttributeValues);
    }

    @Override
    public <T> Iterator<AttributeValue<T>> findValues(final DataType<T> dataType) {
        final Iterator<AttributeValue<?>> iterAttributeValues = this.values.iterator();
        return new Iterator<AttributeValue<T>>() {
            private AttributeValue<T> findNextMatch() {
                AttributeValue<T> attributeValue = null;
                while (attributeValue == null && iterAttributeValues.hasNext()) {
                    AttributeValue<?> attributeValueTest = iterAttributeValues.next();
                    if (attributeValueTest.getDataTypeId().equals(dataType.getId())) {
                        try {
                            attributeValue = dataType.convertAttributeValue(attributeValueTest);
                        } catch (DataTypeException ex) { //NOPMD
                            // TODO: Should log this somewhere. It should never happen unless
                            // the implementation of the provided dataType is broken.
                        }
                    }
                }
                return attributeValue;
            }

            private AttributeValue<T> attributeValueNextMatch = this.findNextMatch();

            @Override
            public boolean hasNext() {
                return this.attributeValueNextMatch != null;
            }

            @Override
            public AttributeValue<T> next() {
                AttributeValue<T> attributeValueNext = this.attributeValueNextMatch;
                this.attributeValueNextMatch = this.findNextMatch();
                return attributeValueNext;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove is not supported");
            }

        };
    }

    @Override
    public String getIssuer() {
        return this.issuer;
    }

    /**
     * Sets the <code>String</code> representing the XACML Issuer for this <code>StdMutableAttribute</code>.
     *
     * @param issuerIn the <code>String</code> representing the XACML Issuer for this
     *            <code>StdMutableAttribute</code>.
     */
    public void setIssuer(String issuerIn) {
        this.issuer = issuerIn;
    }

    @Override
    public boolean getIncludeInResults() {
        return this.includeInResults;
    }

    /**
     * Sets the <code>boolean</code> flag indicating whether the XACML Attribute represented by this
     * <code>StdMutableAttribute</code> should be included in the XACML Result from a decision including this
     * <code>StdMutableAttribute</code>.
     *
     * @param b if true, this <code>StdMutableAttribute</code> should be included, otherwise not.
     */
    public void setIncludeInResults(boolean b) {
        this.includeInResults = b;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || !(obj instanceof Attribute)) {
            return false;
        } else {
            Attribute objAttribute = (Attribute)obj;
            return ObjUtil.equalsAllowNull(this.getCategory(), objAttribute.getCategory())
                   && ObjUtil.equalsAllowNull(this.getAttributeId(), objAttribute.getAttributeId())
                   && ObjUtil.equalsAllowNull(this.getIssuer(), objAttribute.getIssuer())
                   && this.getIncludeInResults() == objAttribute.getIncludeInResults()
                   && ListUtil.equalsAllowNulls(this.getValues(), objAttribute.getValues());
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
        result = 31 * result + Boolean.valueOf(getIncludeInResults()).hashCode();
        if (getIssuer() != null) {
            result = 31 * result + getIssuer().hashCode();
        }
        if (getValues() != null) {
            result = 31 * result + getValues().hashCode();
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
        Collection<AttributeValue<?>> listValues = this.getValues();
        if (listValues.size() > 0) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("values=");
            stringBuilder.append(ListUtil.toString(listValues));
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
        if (needsComma) {
            stringBuilder.append(',');
            stringBuilder.append("includeInResults=");
            stringBuilder.append(this.getIncludeInResults());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
