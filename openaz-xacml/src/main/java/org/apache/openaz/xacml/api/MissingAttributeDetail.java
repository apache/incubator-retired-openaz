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
package org.apache.openaz.xacml.api;

import java.util.Collection;

/**
 * Defines the API for objects that represent XACML MissingAtributeDetail elements as part of a Status
 * element. MissingAttributeDetails relay information back about the reason a policy decision may have failed
 * due to the absence of required AttributeValues.
 */
public interface MissingAttributeDetail {
    /**
     * Gets the {@link Identifier} representing the XACML Category of the Attribute that was missing.
     *
     * @return the <code>Identifier</code> representing the XACML Category of the Attribute that was missing.
     */
    Identifier getCategory();

    /**
     * Gets the {@link Identifier} representing the XACML AttributeId of the Attribute that was missing.
     *
     * @return the <code>Identifier</code> representing the XACML AttributeId of the Attribute that was
     *         missing.
     */
    Identifier getAttributeId();

    /**
     * Gets the {@link Identifier} representing the XACML DataType of the AttributeValue that was missing.
     *
     * @return the <code>Identifier</code> representing the XACML DataType of the Attribute that was missing.
     */
    Identifier getDataTypeId();

    /**
     * Gets the <code>String</code> representing the XACML Issuer for the Attribute that was missing if
     * required.
     *
     * @return the <code>String</code> representing the XACML Issuer for the Attribute that was missing if
     *         required.
     */
    String getIssuer();

    /**
     * Gets the <code>Collection</code> of {@link AttributeValue}s that were expected for the Attribute that
     * was missing. If there are no expected <code>AttributeValue</code>s this method should return an empty
     * list. The <code>Collection</code> returned should not be modified. Implementations are free to use
     * unmodifiable <code>Collection</code>s to enforce this.
     *
     * @return a <code>Collection</code> of <code>AttributeValue</code>s that were expected for the Attribute
     *         that was missing.
     */
    Collection<AttributeValue<?>> getAttributeValues();

    /**
     * {@inheritDoc} Implementations of the <code>MissingAttributeDetail</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>MissingAttributeDetail</code>s (
     * <code>m1</code> and <code>m2</code>) are equal if: {@code m1.getCategory().equals(m2.getCategory())}
     * AND {@code m1.getAttributeId().equals(m2.getAttributeId())} AND
     * {@code m1.getDataTypeId().equals(m2.getDataTypeId())} AND
     * {@code m1.getIssuer() == null && m2.getIssuer() == null} OR
     * {@code m1.getIssuer().equals(m2.getIssuer())} AND {@code m1.getAttributeValues()} is pairwise equal to
     * {@code m2.getAttributeValues()}
     * 
     * @param obj
     * @return
     */
    @Override
    boolean equals(Object obj);
}
