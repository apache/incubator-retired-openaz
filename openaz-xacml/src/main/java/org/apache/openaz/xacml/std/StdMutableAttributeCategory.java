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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeCategory;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.util.ListUtil;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * Mutable implementation of the {@link org.apache.openaz.xacml.api.AttributeCategory} interface.
 *
 */
public class StdMutableAttributeCategory implements AttributeCategory {
    private static final List<Attribute> EMPTY_LIST = Collections
        .unmodifiableList(new ArrayList<Attribute>());

    protected Log logger = LogFactory.getLog(this.getClass());
    private Identifier category;
    private List<Attribute> attributes = new ArrayList<Attribute>();
    private Map<Identifier, List<Attribute>> attributesById = new HashMap<Identifier, List<Attribute>>();

    /**
     * Builds the <code>Map</code> from {@link org.apache.openaz.xacml.api.Identifier}s for XACML AttributeIds
     * to {@link org.apache.openaz.xacml.api.Attribute}s.
     */
    private void buildMap() {
        for (Attribute attribute : this.attributes) {
            List<Attribute> listAttributes = this.attributesById.get(attribute.getAttributeId());
            if (listAttributes == null) {
                listAttributes = new ArrayList<Attribute>();
                this.attributesById.put(attribute.getAttributeId(), listAttributes);
            }
            listAttributes.add(attribute);
        }
    }

    /**
     * Creates a new <code>StdMutableAttributeCategory</code> with all default values.
     */
    public StdMutableAttributeCategory() {

    }

    /**
     * Creates a new <code>StdMutableAttributeCategory</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier} representing its XACML Category, and the given
     * <code>Collection</code> of {@link org.apache.openaz.xacml.api.Attribute}s as its XACML Attributes. The
     * <code>Collection</code> is copied; changes made to the <code>Collection</code> after creating the new
     * <code>StdMutableAttributeCategory</code> are not reflected in the
     * <code>StdMutableAttributeCategory</code>.
     *
     * @param identifierCategory the <code>Identifier</code> for the XACML Category for the new
     *            <code>StdMutableAttributeCategory</code>.
     * @param listAttributes a <code>Collection</code> of <code>Attribute</code>s for the new
     *            <code>StdMutableAttributeCategory</code>.
     */
    public StdMutableAttributeCategory(Identifier identifierCategory, Collection<Attribute> listAttributes) {
        this.category = identifierCategory;
        if (listAttributes != null) {
            this.attributes.addAll(listAttributes);
            this.buildMap();
        }
    }

    /**
     * Creates a new <code>StdMutableAttributeCategory</code> that is a copy of the given
     * {@link org.apache.openaz.xacml.api.AttributeCategory}.
     *
     * @param attributeCategory the <code>AttributeCategory</code> to copy
     */
    public StdMutableAttributeCategory(AttributeCategory attributeCategory) {
        this.category = attributeCategory.getCategory();
        this.attributes.addAll(attributeCategory.getAttributes());
        this.buildMap();
    }

    @Override
    public Identifier getCategory() {
        return this.category;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Identifier} representing the XACML Category of this
     * <code>StdMutableAttributeCategory</code>.
     *
     * @param identifierCategory the <code>Identifier</code> representing the XACML Category of this
     *            <code>StdMutableAttributeCategory</code>.
     */
    public void setCategory(Identifier identifierCategory) {
        this.category = identifierCategory;
    }

    @Override
    public Collection<Attribute> getAttributes() {
        return Collections.unmodifiableCollection(this.attributes);
    }

    /**
     * Adds a {@link org.apache.openaz.xacml.api.Attribute} to this <code>StdMutableAttributeCategory</code>>
     *
     * @param attribute the <code>Attribute</code> to add
     */
    public void add(Attribute attribute) {
        this.attributes.add(attribute);
        List<Attribute> listAttributes = this.attributesById.get(attribute.getAttributeId());
        if (listAttributes == null) {
            listAttributes = new ArrayList<Attribute>();
            this.attributesById.put(attribute.getAttributeId(), listAttributes);
        }
        listAttributes.add(attribute);
    }

    @Override
    public Iterator<Attribute> getAttributes(Identifier attributeId) {
        List<Attribute> listAttributes = this.attributesById.get(attributeId);
        return (listAttributes == null ? EMPTY_LIST.iterator() : listAttributes.iterator());
    }

    @Override
    public boolean hasAttributes(Identifier attributeId) {
        List<Attribute> listAttributes = this.attributesById.get(attributeId);
        return listAttributes != null && listAttributes.size() > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof AttributeCategory)) {
            return false;
        } else {
            AttributeCategory objAttributeCategory = (AttributeCategory)obj;
            return ObjUtil.equalsAllowNull(this.getCategory(), objAttributeCategory.getCategory())
                   && ListUtil.equalsAllowNulls(this.getAttributes(), objAttributeCategory.getAttributes());
        }
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (getCategory() != null) {
            result = 31 * result + getCategory().hashCode();
        }
        if (getAttributes() != null) {
            result = 31 * result + getAttributes().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        boolean needsComma = false;
        Object objectToDump;
        if ((objectToDump = this.getCategory()) != null) {
            stringBuilder.append("category=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }
        if (this.attributes.size() > 0) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("attributes=");
            stringBuilder.append(ListUtil.toString(this.attributes));
            needsComma = true;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
