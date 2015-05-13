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

import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Obligation;
import org.apache.openaz.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.Obligation} interface.
 */
public class StdObligation extends Wrapper<Obligation> implements Obligation {
    /**
     * Creates a new immutable <code>StdObligation</code> that wraps the given
     * {@link org.apache.openaz.xacml.api.Obligation}. The caller agrees not o modify the given
     * <code>Obligation</code> as long as the new <code>StdObligation,</code> refers to it.
     *
     * @param obligation the <code>Obligation</code> to wrap in the new <code>StdObligation</code>.
     */
    public StdObligation(Obligation obligation) {
        super(obligation);
    }

    /**
     * Creates a new immutable <code>StdObligation</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier} representing the XACML ObligationId of the Obligation
     * represented by the new <code>StdObligation</code>.
     *
     * @param identifier the <code>Identifier</code> representing the XACML ObligationId of the Obligation
     */
    public StdObligation(Identifier identifier) {
        this(new StdMutableObligation(identifier));
    }

    /**
     * Creates a new immutable <code>StdObligation</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier} representing the XACML ObligationId and a copy of the
     * given <code>Collection</code> of {@link org.apache.openaz.xacml.api.AttributeAssignment}s representing
     * the AttributeAssignment elements of the Obligation represented by the new <code>StdObligation</code>.
     *
     * @param identifier the <code>Identifier</code> representing the XACML ObligationId of the Obligation
     * @param attributeAssignments <code>Collection</code> of <code>AttributeAssignment</code>s representing
     *            the XACML AttributeAssignments of the Obligation.
     */
    public StdObligation(Identifier identifier, Collection<AttributeAssignment> attributeAssignments) {
        this(new StdMutableObligation(identifier, attributeAssignments));
    }

    /**
     * Creates a new <code>StdObligation</code> as a copy of the given
     * {@link org.apache.openaz.xacml.api.Obligation}.
     *
     * @param obligation the <code>Obligation</code> to copy
     * @return a new <code>StdObligation</code> copied from the given <code>Obligation</code>.
     */
    public static StdObligation copy(Obligation obligation) {
        return new StdObligation(obligation.getId(), obligation.getAttributeAssignments());
    }

    @Override
    public Identifier getId() {
        return this.getWrappedObject().getId();
    }

    @Override
    public Collection<AttributeAssignment> getAttributeAssignments() {
        return this.getWrappedObject().getAttributeAssignments();
    }

}
