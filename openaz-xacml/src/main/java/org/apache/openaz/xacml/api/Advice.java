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
 * Defines the API for objects that implement XACML 3.0 AssociatedAdvice elements.
 */
public interface Advice {
    /**
     * Gets the {@link org.apache.openaz.xacml.api.Identifier} for this <code>Advice</code> object.  The <code>Identifier</code>
     * uniquely identifies a XACML 3.0 AssociatedAdvice element in a Rule or Policy.
     *
     * @return the <code>Identifier</code> for this <code>Advice</code>.
     */
    Identifier getId();

    /**
     * Gets the {@link org.apache.openaz.xacml.api.AttributeAssignment}s in this <code>Advice</code> object.  If there
     * are no <code>AttributeAssignment</code>s in this <code>Advice</code>, an empty <code>Collection</code> must be returned.
     * The returned <code>Collection</code> should not be modified.  Implementations are free to return an immutable view to enforce this.
     *
     * @return the <code>Collection</code> of <code>AttributeAssignment</code>s in this <code>Advice</code>
     *         object.
     */
    Collection<AttributeAssignment> getAttributeAssignments();

    /**
     * {@inheritDoc} The implementation of the <code>Advice</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>Advice</code> objects (
     * <code>a1</code> and <code>a2</code>) are equal if: {@code a1.getId().equals(a2.getId())} AND
     * {@code a1.getAttributeAssignments()} is pair-wise equal to {@code a2.getAttributeAssignments()}
     */
    @Override
    boolean equals(Object obj);
}
