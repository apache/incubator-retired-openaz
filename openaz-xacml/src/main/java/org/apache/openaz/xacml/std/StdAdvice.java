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

import org.apache.openaz.xacml.api.Advice;
import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.Advice} interface.
 */
public class StdAdvice extends Wrapper<Advice> implements Advice {
    /**
     * Creates an immutable <code>StdAdvice</code> that wraps the given
     * {@link org.apache.openaz.xacml.api.Advice}.
     *
     * @param advice the <code>Advice</code> object to wrap in the new <code>StdAdvice</code>
     */
    public StdAdvice(Advice advice) {
        super(advice);
    }

    /**
     * Creates an immutable <code>StdAdvice</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier} as the XACML AdviceId and the given
     * <code>Collection</code> of {@link org.apache.openaz.xacml.api.AttributeAssignment}s representing the
     * XACML AttributeAssignment elements.
     *
     * @param adviceId the <code>Identifier</code> representing the XACML AdviceId
     * @param attributeAssignmentsIn the <code>Collection</code> of <code>AttributeAssignment</code>s
     *            representing the XACML AttributeAssignment elements.
     */
    public StdAdvice(Identifier adviceId, Collection<AttributeAssignment> attributeAssignmentsIn) {
        this(new StdMutableAdvice(adviceId, attributeAssignmentsIn));
    }

    /**
     * Creates an immutable <code>StdAdvice</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier} as the XACML AdviceId and an empty list of
     * <code>AttributeAssignment</code>s.
     *
     * @param adviceId the <code>Identifier</code> representing the XACML AdviceId
     */
    public StdAdvice(Identifier adviceId) {
        this(new StdMutableAdvice(adviceId));
    }

    /**
     * Creates a copy of the given {@link org.apache.openaz.xacml.api.Advice}.
     *
     * @param advice the <code>Advice</code> to copy
     * @return a new <code>StdAdvice</code> that is a copy of the given <code>Advice</code>.
     */
    public static StdAdvice copy(Advice advice) {
        return new StdAdvice(advice.getId(), advice.getAttributeAssignments());
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
