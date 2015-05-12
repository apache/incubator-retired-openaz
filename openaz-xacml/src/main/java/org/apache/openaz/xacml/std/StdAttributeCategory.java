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
package com.att.research.xacml.std;

import java.util.Collection;
import java.util.Iterator;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.AttributeCategory;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link com.att.research.xacml.api.AttributeCategory} interface.
 *
 */
public class StdAttributeCategory extends Wrapper<AttributeCategory> implements AttributeCategory {
    /**
     * Creates an immutable <code>StdAttributeCategory</code> that wraps the given {@link com.att.research.xacml.api.AttributeCategory}.
     * The caller agrees to no longer modify the given <code>AttributeCategory</code> while it is wrapped by the <code>StdAttributeCategory</code>>
     *
     * @param attributeCategory the <code>AttributeCategory</code> to wrap.
     */
    public StdAttributeCategory(AttributeCategory attributeCategory) {
        super(attributeCategory);
    }

    /**
     * Creates a new <code>StdAttributeCategory</code> with the given {@link com.att.research.xacml.api.Identifier} representing its
     * XACML Category, and the given <code>Collection</code> of {@link com.att.research.xacml.api.Attribute}s as its XACML Attributes.
     * The <code>Collection</code> is copied; changes made to the <code>Collection</code> after creating the new <code>StdAttributeCategory</code>
     * are not reflected in the <code>StdAttributeCategory</code>.
     *
     * @param identifierCategory the <code>Identifier</code> for the XACML Category for the new <code>StdAttributeCategory</code>.
     * @param listAttributes a <code>Collection</code> of <code>Attribute</code>s for the new <code>StdAttributeCategory</code>.
     */
    public StdAttributeCategory(Identifier identifierCategory, Collection<Attribute> listAttributes) {
        this(new StdMutableAttributeCategory(identifierCategory, listAttributes));
    }

    @Override
    public Identifier getCategory() {
        return this.getWrappedObject().getCategory();
    }

    @Override
    public Collection<Attribute> getAttributes() {
        return this.getWrappedObject().getAttributes();
    }

    @Override
    public Iterator<Attribute> getAttributes(Identifier attributeId) {
        return this.getWrappedObject().getAttributes(attributeId);
    }

    @Override
    public boolean hasAttributes(Identifier attributeId) {
        return this.getWrappedObject().hasAttributes(attributeId);
    }
}
