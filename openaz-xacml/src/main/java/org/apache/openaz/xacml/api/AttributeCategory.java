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
 * Defines the API for objects representing XACML Attributes elements. Attributes elements represent
 * collections of XACML Attribute elements with the same Category.
 */
public interface AttributeCategory {
    /**
     * Gets the {@link Identifier} for the XACML Category of this <code>AttributeCategory</code>.
     *
     * @return the <code>Identifier</code> for the category of this <code>AttributeCategory</code>.
     */
    Identifier getCategory();

    /**
     * Gets the <code>Collection</code> of {@link Attribute}s in this <code>AttributeCategory</code>. If there
     * are no <code>Attribute</code>s in this <code>AttributeCategory</code> then an empty
     * <code>Collection</code> must be returned. The returned <code>Collection</code> should not be modified.
     * Implementations are free to return an immutable view to enforce this.
     *
     * @return the <code>Collection</code> of {@link Attribute}s in this <code>AttributeCategory</code>.
     */
    Collection<Attribute> getAttributes();

    /**
     * Gets an <code>Iterator</code> over all of the {@link Attribute}s in this <code>AttributeCategory</code>
     * with the given {@link Identifier} matching their XACML AttributeId.
     *
     * @param attributeId the <code>Identifier</code> to match against the XACML AttributeId
     * @return an <code>Iterator</code> over all of the <code>Attribute</code>s in this
     *         <code>AttributeCategory</code> with the given <code>Identifier</code> matching their XACML
     *         AttributeId.
     */
    Iterator<Attribute> getAttributes(Identifier attributeId);

    /**
     * Determines if there is at least one {@link Attribute} in this <code>AttributeCategory</code> whose
     * XACML AttributeId matches the given {@link Identifier}.
     *
     * @param attributeId the <code>Identifier</code> of the AttributeId to look for
     * @return true if there is at least one <code>Attribute</code> whose XACML AttributeId matches the given
     *         <code>Identifier</code>, else false
     */
    boolean hasAttributes(Identifier attributeId);

    /**
     * {@inheritDoc} Implementations of the <code>AttributeCategory</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>AttributeCategory</code>s (
     * <code>a1</code> and <code>a2</code>) are equal if: {@code a1.getCategory().equals(a2.getCategory())}
     * AND The {@link Attribute}s in <code>a1</code> and <code>a2</code> are pairwise equal.
     */
    @Override
    boolean equals(Object obj);
}
