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

/**
 * Defines the API for objects representing XACML AttributeValue elements.
 *
 * @param <T> the java type of the object representing the value of the XACML AttributeValue element.
 */
public interface AttributeValue<T> {
    /**
     * Returns the {@link Identifier} representing the XACML data type of this <code>AttributeValue</code>.
     *
     * @return the <code>Identifier</code> representing the XACML data type of this
     *         <code>AttributeValue</code>
     */
    Identifier getDataTypeId();

    /**
     * Returns the object representing the value of the XACML AttributeValue element represented by this
     * <code>AttributeValue</code> of type <code>T</code>.
     *
     * @return the object representing the value of the XACML AttributeValue element represented by this
     *         <code>AttributeValue</code>
     */
    T getValue();

    /**
     * Returns the {@link Identifier} representing the XACML Category id of this <code>AttributeValue</code>
     * for <code>AttributeValue</code>s of the data type <code>XPathExpression</code>.
     *
     * @return the <code>Identifier</code> representing the XACML Category id of this
     *         <code>AttributeValue</code>.
     */
    Identifier getXPathCategory();

    /**
     * {@inheritDoc} Implementations of the <code>AttributeValue</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>AttributeValue</code>s (
     * <code>a1</code> and <code>a2</code>) are equal if:
     * {@code a1.getDataTypeId().equals(a2.getDataTypeId())} AND
     * {@code a1.getValue() == null && a2.getValue() == null} OR {@code a1.getValue().equals(a2.getValue())}
     */
    @Override
    boolean equals(Object obj);

}
