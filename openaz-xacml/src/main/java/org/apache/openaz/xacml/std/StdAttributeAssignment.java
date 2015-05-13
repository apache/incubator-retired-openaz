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
import org.apache.openaz.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.AttributeAssignment} interface.
 */
public class StdAttributeAssignment extends Wrapper<AttributeAssignment> implements AttributeAssignment {
    /**
     * Creates a new immutable <code>StdAttributeAssignment</code> that wraps the given
     * {@link org.apache.openaz.xacml.api.AttributeAssignment}. The caller agrees not to modify the given
     * <code>AttributeAssignment</code> as long as the new <code>StdAttributeAssignment</code> refers to it.
     *
     * @param attributeAssignment the <code>AttributeAssignment</code> to be wrapped by the new
     *            <code>StdAttributeAssignment</code>.
     */
    public StdAttributeAssignment(AttributeAssignment attributeAssignment) {
        super(attributeAssignment);
    }

    /**
     * Creates a new immutable <code>StdAttributeAssignment</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier}s representing the XACML Category and AttributeId of the
     * Attribute to be assigned, the given <code>String</code> issuer, and the given
     * {@link org.apache.openaz.xacml.api.AttributeValue} representing the value to assign to the Attribute.
     *
     * @param categoryIn the <code>Identifier</code> representing the Category of the Attribute to be assigned
     * @param attributeIdIn the <code>Identifier</code> representing the AttributeId of the Attribute to be
     *            assigned
     * @param issuerIn the <code>String</code> representing the Issuer of the Attribute to be assigned
     * @param attributeValueIn the <code>AttributeValue</code> representing the AttributeValue to be assigned
     *            to the Attribute
     */
    public StdAttributeAssignment(Identifier categoryIn, Identifier attributeIdIn, String issuerIn,
                                  AttributeValue<?> attributeValueIn) {
        this(new StdMutableAttributeAssignment(categoryIn, attributeIdIn, issuerIn, attributeValueIn));
    }

    /**
     * Creates a new immutable <code>StdAttributeAssignment</code> by copying the given
     * {@link org.apache.openaz.xacml.api.AttributeAssignment}.
     *
     * @param attributeAssignment the <code>AttributeAssignment</code> to copy
     * @return a new <code>StdAttributeAssignment</code> copied from the given
     *         <code>AttributeAssignment</code>
     */
    public static StdAttributeAssignment copy(AttributeAssignment attributeAssignment) {
        return new StdAttributeAssignment(attributeAssignment.getCategory(),
                                          attributeAssignment.getAttributeId(),
                                          attributeAssignment.getIssuer(),
                                          attributeAssignment.getAttributeValue());
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
    public String getIssuer() {
        return this.getWrappedObject().getIssuer();
    }

    @Override
    public Identifier getDataTypeId() {
        return this.getWrappedObject().getDataTypeId();
    }

    @Override
    public AttributeValue<?> getAttributeValue() {
        return this.getWrappedObject().getAttributeValue();
    }
}
