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

import java.util.Collection;
import java.util.Iterator;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.Attribute} interface.
 */
public class StdAttribute extends Wrapper<Attribute> implements Attribute {
    /**
     * Creates an immutable <code>StdAttribute</code> that wraps the given
     * {@link org.apache.openaz.xacml.api.Attribute}.
     *
     * @param attribute the <code>Attribute</code> wrapped by this <code>StdAttribute</code>.
     */
    public StdAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * Creates an immutable <code>StdAttribute</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier}s representing the XACML Category and AttributeId, a copy
     * of the given <code>Collection</code> of {@link org.apache.openaz.xacml.api.AttributeValue}s representing
     * the XACML AttributeValue elements, the given <code>String</code> Issuer, and the given
     * <code>boolean</code> indicating whether the new <code>StdAttribute</code> should be returned as part of
     * a decision Result.
     *
     * @param categoryIdIn the <code>Identifier</code> representing the XACML Category
     * @param attributeIdIn the <code>Identifier</code> representing the XACML AttributeId
     * @param valuesIn the <code>Collection</code> of <code>AttributeValue</code>s
     * @param issuerIn the <code>String</code> Issuer
     * @param includeInResultsIn the <code>boolean</code> IncludeInResults
     */
    public StdAttribute(Identifier categoryIdIn, Identifier attributeIdIn,
                        Collection<AttributeValue<?>> valuesIn, String issuerIn, boolean includeInResultsIn) {
        this(new StdMutableAttribute(categoryIdIn, attributeIdIn, valuesIn, issuerIn, includeInResultsIn));
    }

    /**
     * Creates an immutable <code>StdAttribute</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier}s representing the XACML Category and AttributeId, the
     * {@link org.apache.openaz.xacml.api.AttributeValue}s representing a single XACML AttributeValue element,
     * the given <code>String</code> Issuer, and the given <code>boolean</code> indicating whether the new
     * <code>StdAttribute</code> should be returned as part of a decision Result.
     *
     * @param categoryIdIn the <code>Identifier</code> representing the XACML Category
     * @param attributeIdIn the <code>Identifier</code> representing the XACML AttributeId
     * @param valueIn the <code>AttributeValue</code>
     * @param issuerIn the <code>String</code> Issuer
     * @param includeInResultsIn the <code>boolean</code> IncludeInResults
     */
    public StdAttribute(Identifier categoryIdIn, Identifier attributeIdIn, AttributeValue<?> valueIn,
                        String issuerIn, boolean includeInResultsIn) {
        this(new StdMutableAttribute(categoryIdIn, attributeIdIn, valueIn, issuerIn, includeInResultsIn));
    }

    /**
     * Creates an immutable <code>StdAttribute</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier}s representing the XACML Category and AttributeId, and the
     * {@link org.apache.openaz.xacml.api.AttributeValue}s representing a single XACML AttributeValue element.
     *
     * @param categoryIdIn the <code>Identifier</code> representing the XACML Category
     * @param attributeIdIn the <code>Identifier</code> representing the XACML AttributeId
     * @param valueIn the <code>AttributeValue</code>
     */
    public StdAttribute(Identifier categoryIdIn, Identifier attributeIdIn, AttributeValue<?> valueIn) {
        this(new StdMutableAttribute(categoryIdIn, attributeIdIn, valueIn));
    }

    /**
     * Gets a new <code>StdAttribute</code> that is a copy of the given
     * {@link org.apache.openaz.xacml.api.Attribute}.
     *
     * @param attribute the <code>Attribute</code> to copy
     * @return a new <code>StdAttribute</code> that is a copy of the given <code>Attribute</code>
     */
    public static StdAttribute copy(Attribute attribute) {
        return new StdAttribute(attribute.getCategory(), attribute.getAttributeId(), attribute.getValues(),
                                attribute.getIssuer(), attribute.getIncludeInResults());
    }

    @Override
    public Identifier getAttributeId() {
        return this.getWrappedObject().getAttributeId();
    }

    @Override
    public Identifier getCategory() {
        return this.getWrappedObject().getCategory();
    }

    @Override
    public Collection<AttributeValue<?>> getValues() {
        return this.getWrappedObject().getValues();
    }

    @Override
    public <T> Iterator<AttributeValue<T>> findValues(DataType<T> dataType) {
        return this.getWrappedObject().findValues(dataType);
    }

    @Override
    public String getIssuer() {
        return this.getWrappedObject().getIssuer();
    }

    @Override
    public boolean getIncludeInResults() {
        return this.getWrappedObject().getIncludeInResults();
    }
}
