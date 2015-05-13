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
 * Defines the API for objects that represent XACML Obligation elements. Obligations are returned in Result
 * elements to indicate actions a PEP must enforce as part of a decision.
 */
public interface Obligation {
    /**
     * Gets the {@link Identifier} representing the XACML ObligationId for this <code>Obligation</code>.
     *
     * @return the <code>Identifier</code> representing the XACML ObligationId for this
     *         <code>Obligation</code>.
     */
    Identifier getId();

    /**
     * Gets the <code>Collection</code> of {@link org.apache.openaz.xacml.api.AttributeAssignment}s
     * representing the XACML AttributeAssignment elements for this <code>Obligation</code>.
     *
     * @return a <code>Collection</code> of the <code>AttributeAssignment</code>s representing the XACML
     *         AttributeAssignment elements for this <code>Obligation</code>.
     */
    Collection<AttributeAssignment> getAttributeAssignments();

    /**
     * {@inheritDoc} Implementations of the <code>Obligation</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>Obligation</code>s (<code>o1</code>
     * and <code>o2</code>) are equal if: {@code o1.getId().equals(o2.getId())} AND
     * {@code o1.getAttributeAssignments()} is pairwise equal to {@code o2.getAttributeAssignments()}
     */
    @Override
    boolean equals(Object obj);
}
