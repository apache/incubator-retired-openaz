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
 * Defines the API for objects that represent XACML 3.0 AttributeAssignment elements. AttributeAssignments are
 * used in XACML 3.0 ObligationExpressions and AdviceExpressions.
 */
public interface AttributeAssignment {
    /**
     * Gets the {@link Identifier} for the XACML Attribute that is assigned by this
     * <code>AttributeAssignment</code>.
     *
     * @return the <code>Identifier</code> for the XACML Attribute that is assigned by this
     *         <code>AttributeAssignment</code>.
     */
    Identifier getAttributeId();

    /**
     * Gets the {@link Identifier} for the XACML Category of the Attribute that is assigned by this
     * <code>AttributeAssignment</code>.
     *
     * @return the <code>Identifier</code> for the XACML Category of the Attribute that is assigned by this
     *         <code>AttributeAssignment</code>.
     */
    Identifier getCategory();

    /**
     * Gets the <code>String</code> issuer of the XACML Attribute that is assigned by this
     * <code>AttributeAssignment</code>.
     *
     * @return the <code>String</code> issuer of the XACML Attribute that is assigned by this
     *         <code>AttributeAssignment</code>.
     */
    String getIssuer();

    /**
     * Gets the {@link Identifier} for the XACML data type of the AttributeValue that is assigned to the
     * Attribute by this <code>AttributeAssignment</code>.
     *
     * @return the <code>Identifier</code> for the XACML data type of the AttributeValue that is assigned to
     *         the Attribute by this <code>AttributeAssignment</code>.
     */
    Identifier getDataTypeId();

    /**
     * Gets the {@link AttributeValue} representing the XACML AttributeValue that is assigned to the Attribute
     * by this <code>AttributeAssignment</code>.
     *
     * @return the {@link AttributeValue} representing the XACML AttributeValue that is assigned to the
     *         Attribute by this <code>AttributeAssignment</code>.
     */
    AttributeValue<?> getAttributeValue();

    /**
     * {@inheritDoc} The implementation of the <code>AttributeAssignment</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>AttributeAssignment</code>s (
     * <code>a1</code> and <code>a2</code>) are equal if:
     * {@code a1.getAttributeId().equals(a2.getAttributeId())} AND
     * {@code a1.getCategory()>equals(a2.getCategory())} AND
     * {@code a1.getIssuer() == null && @a2.getIssuer() == null} OR
     * {@code a1.getIssuer().equals(a2.getIssuer())} AND {@code a1.getDataTypeId().equals(a2.getDataTypeId())}
     * AND {@code a1.getAttributeValue().equals(a2.getAttributeValue())}
     */
    @Override
    boolean equals(Object obj);
}
