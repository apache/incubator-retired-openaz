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
import java.util.Iterator;

/**
 * Defines the API for objects that represent XACML 3.0 Attribute elements.
 */
public interface Attribute {
    /**
     * Gets the {@link org.apache.openaz.xacml.api.Identifier} for this <code>Attribute</code> object.
     * <code>Identifier</code> uniquely identifies a XACML 3.0 Attribute element in a Request, Policy, or
     * Response document.
     *
     * @return the <code>Identifier</code> for this <code>Attribute</code>
     */
    Identifier getAttributeId();

    /**
     * Gets the {@link org.apache.openaz.xacml.api.Identifier} for the XACML 3.0 Category of this <code>Attribute</code>.
     * <code>Attribute</code>.
     *
     * @return the <code>Identifier</code> for the XACML 3.0 Category of this <code>Attribute</code>.
     */
    Identifier getCategory();

    /**
     * Gets the <code>Collection</code> of {@link org.apache.openaz.xacml.api.AttributeValue} objects for this <code>Attribute</code>.
     * If there are no <code>AttributeValue</code>s in this <code>Attribute</code>, an empty <code>Collection</code> must be returned.
     * The returned <code>Collection</code> should not be modified.  Implementations are free to return an immutable view to enforce this.
     *
     * @return a <code>Collection</code> of the <code>AttributeValue</code>s for this <code>Attribute</code>
     */
    Collection<AttributeValue<?>> getValues();

    /**
     * Finds all of the {@link org.apache.openaz.xacml.api.AttributeValue} objects with the given {@link DataType} in
     * the <code>AttributeValue</code>s for this <code>Attribute</code>.  If there are no matching <code>AttributeValue</code>s, an empty
     * <code>Iterator</code> must be returned.
     *
     * @param dataType the <code>DataType</code> to filter on
     * @return an <code>Iterator</code> over all of the <code>AttributeValue</code>s of the given
     *         <code>DataType</code>.
     * @throws NullPointerException if the supplied <code>DataType</code> is null
     */
    <T> Iterator<AttributeValue<T>> findValues(DataType<T> dataType);

    /**
     * Gets the <code>String</code> issuer of this <code>Attribute</code>. If the <code>Attribute</code> does
     * not have an issuer, null is returned.
     *
     * @return the <code>String</code> issuer of this <code>Attribute</code>.
     */
    String getIssuer();

    /**
     * Gets the <code>boolean</code> value indicating whether this <code>Attribute</code> should be included
     * in the {@link org.apache.openaz.xacml.Response} to a XACML 3.0 Request.
     *
     * @return true if this <code>Attribute</code> should be included in the <code>Response</code> else false.
     */
    boolean getIncludeInResults();

    /**
     * {@inheritDoc} The implementation of the <code>Attribute</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>Attribute</code>s (<code>a1</code>
     * and <code>a2</code>) are equal if: {@code a1.getAttributeId().equals(a2.getAttributeId())} AND
     * {@code a1.getCategory().equals(a2.getCategory())} AND {@code a1.getIssuer().equals(a2.getIssuer())} or
     * both issuers are null AND {@code a1.getIncludeInResults() == a2.getIncludeInResults} AND
     * {@code a1.getValues()} is pair-wise equal to {@code a2.getValues()}
     */
    @Override
    boolean equals(Object obj);
}
