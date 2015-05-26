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
 * Defines the API for objects that represent XACML Result elements. Results communicate the Decision, Status,
 * Attributes, Obligations, and Advice for an individual decision request.
 */
public interface Result {
    /**
     * Gets the {@link org.apache.openaz.xacml.api.Decision} associated with this <code>Result</code>.
     *
     * @return the <code>Decision</code> associated with this <code>Result</code>.
     */
    Decision getDecision();

    /**
     * Gets the {@link org.apache.openaz.xacml.api.Status} associated with this <code>Result</code>.
     *
     * @return the <code>Status</code> associated with this <code>Result</code>
     */
    Status getStatus();

    /**
     * Gets the <code>Collection</code> of {@link org.apache.openaz.xacml.api.Obligation}s int this
     * <code>Result</code>. If there are no <code>Obligation</code>s this method must return an empty
     * <code>Collection</code>.
     *
     * @return the <code>Collection</code> of {@link org.apache.openaz.xacml.api.Obligation}s
     *         <code>Result</code>.
     */
    Collection<Obligation> getObligations();

    /**
     * Gets the <code>Collection</code> of {@link Advice} objects in this <code>Result</code>. If there are no
     * <code>Advice</code> codes this method must return an empty <code>Collection</code>.
     *
     * @return the <code>Collection</code> of <code>Advice</code> objects in this <code>Result</code>.
     */
    Collection<Advice> getAssociatedAdvice();

    /**
     * Gets the <code>Collection</code> of {@link org.apache.openaz.xacml.api.AttributeCategory} objects in this <code>Result</code>.  If there
     * are no <code>AttributeCategory</code> objects this method must return an empty <code>Collection</code>.
     *
     * @return the <code>Collection</code> of <code>AttributeCategory</code> objects in this
     *         <code>Result</code>.
     */
    Collection<AttributeCategory> getAttributes();

    /**
     * Gets the <code>Collection</code> of {@link org.apache.openaz.xacml.api.IdReference} objects referring to XACML 3.0 Policies
     * that are in this <code>Result</code>.
     *
     * @return the <code>Collection</code> of Policy <code>IdReference</code>s in this <code>Result</code>.
     */
    Collection<IdReference> getPolicyIdentifiers();

    /**
     * Gets the <code>Collection</code> of {@link org.apache.openaz.xacml.api.IdReference} objects referring to XACML 3.0 PolicySets
     * that are in this <code>Result</code>.
     *
     * @return the <code>Collection</code> of PolicySet <code>IdReference</code>s in this <code>Result</code>.
     */
    Collection<IdReference> getPolicySetIdentifiers();

    /**
     * {@inheritDoc} Implementations of this interface must override the <code>equals</code> method with the
     * following semantics: Two <code>Result</code>s (<code>r1</code> and <code>r2</code>) are equal if:
     * {@code r1.getDecision() == r2.getDecision()} AND {@code r1.getStatus().equals(r2.getStatus()} AND
     * {@code r1.getObligations()} is pair-wise equal to {@code r2.getObligations()}
     * {@code r1.getAssociatedAdvice()} is pair-wise equal to {@code r2.getAssociatedAdvice()}
     * {@code r1.getAttributes()} is pair-wise equal to {@code r2.getAttributes()}
     * {@code r1.getPolicyIdentifiers()} is pair-wise equal to {@code r2.getPolicyIdentifiers()}
     * {@code r1.getPolicySetIdentifiers()} is pair-wise equal to {@code r2.getPolicySetIdentifiers()}
     */
    @Override
    boolean equals(Object obj);
}
