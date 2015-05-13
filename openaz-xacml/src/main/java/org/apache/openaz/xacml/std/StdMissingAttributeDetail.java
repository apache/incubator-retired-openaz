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

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.MissingAttributeDetail;
import org.apache.openaz.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.MissingAttributeDetail} interface.
 */
public class StdMissingAttributeDetail extends Wrapper<MissingAttributeDetail> implements
    MissingAttributeDetail {
    /**
     * Creates a new immutable <code>StdMissingAttributeDetail</code> that wraps the given
     * {@link org.apache.openaz.xacml.api.MissingAttributeDetail}. The caller agrees not to modify the
     * <code>MissingAttributeDetail</code> as long as the new <code>StdMissingAttributeDetail</code> refers to
     * it.
     *
     * @param missingAttributeDetail the <code>MissingAttributeDetail</code> to wrap in the new
     *            <code>StdMissingAttributeDetail</code>.
     */
    public StdMissingAttributeDetail(MissingAttributeDetail missingAttributeDetail) {
        super(missingAttributeDetail);
    }

    /**
     * Creates a new immutable <code>StdMissingAttributeDetail</code> with the given
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
    public StdMissingAttributeDetail(Identifier categoryIn, Identifier attributeIdIn,
                                     Identifier dataTypeIdIn, String issuerIn,
                                     Collection<AttributeValue<?>> attributeValuesIn) {
        this(new StdMutableMissingAttributeDetail(categoryIn, attributeIdIn, dataTypeIdIn, issuerIn,
                                                  attributeValuesIn));
    }

    /**
     * Creates a new immutable <code>StdMissingAttributeDetail</code> with the given
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
    public StdMissingAttributeDetail(Identifier categoryIn, Identifier attributeIdIn,
                                     Identifier dataTypeIdIn, String issuerIn) {
        this(new StdMutableMissingAttributeDetail(categoryIn, attributeIdIn, dataTypeIdIn, issuerIn, null));
    }

    /**
     * Creates a new immutable <code>StdMissingAttributeDetail</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier}s for the XACML Category, AttributeId, and DataType of the
     * missing Attribute.
     *
     * @param categoryIn the <code>Identifier</code> representing the XACML Category of the missing Attribute
     * @param attributeIdIn the <code>Identifier</code> representing the XACML AttributeId of the missing
     *            Attribute
     * @param dataTypeIdIn the <code>Identifier</code> representing the XACML DataType of the missing
     *            AttributeValue
     */
    public StdMissingAttributeDetail(Identifier categoryIn, Identifier attributeIdIn, Identifier dataTypeIdIn) {
        this(new StdMutableMissingAttributeDetail(categoryIn, attributeIdIn, dataTypeIdIn, null));
    }

    /**
     * Creates a new <code>StdMissingAttributeDetail</code> that is a copy of the given {@link org.apache.openaz.xacml.api.MissingAttributeDetail</code>.
     *
     * @param missingAttributeDetail the <code>MissingAttributeDetail</code> to copy
     * @return a new <code>StdMissingAttributeDetail</code> that is a copy of the given <code>MissingAttributeDetail</code>.
     */
    public static StdMissingAttributeDetail copy(MissingAttributeDetail missingAttributeDetail) {
        return new StdMissingAttributeDetail(missingAttributeDetail.getCategory(),
                                             missingAttributeDetail.getAttributeId(),
                                             missingAttributeDetail.getDataTypeId(),
                                             missingAttributeDetail.getIssuer(),
                                             missingAttributeDetail.getAttributeValues());
    }

    @Override
    public Identifier getCategory() {
        return this.getWrappedObject().getCategory();
    }

    @Override
    public Identifier getAttributeId() {
        return this.getWrappedObject().getAttributeId();
    }

    @Override
    public Identifier getDataTypeId() {
        return this.getWrappedObject().getDataTypeId();
    }

    @Override
    public String getIssuer() {
        return this.getWrappedObject().getIssuer();
    }

    @Override
    public Collection<AttributeValue<?>> getAttributeValues() {
        return this.getWrappedObject().getAttributeValues();
    }

}
