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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Obligation;
import org.apache.openaz.xacml.util.ListUtil;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * Mutable implementation of the {@link org.apache.openaz.xacml.api.Obligation} interface for XACML Obligation
 * elements.
 */
public class StdMutableObligation implements Obligation {
    private static final List<AttributeAssignment> EMPTY_LIST = Collections
        .unmodifiableList(new ArrayList<AttributeAssignment>());

    private Identifier id;
    private List<AttributeAssignment> attributeAssignments;

    /**
     * Creates a new empty <code>StdMutableObligation</code>.
     */
    public StdMutableObligation() {
        this.attributeAssignments = EMPTY_LIST;
    }

    /**
     * Creates a new <code>StdMutableObligation</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier} representing the XACML ObligationId and the given
     * <code>Collection</code> of {@link org.apache.openaz.xacml.api.AttributeAssignment}s representing the
     * XACML AttributeAssignments for the Obligation.
     *
     * @param idIn the <code>Identifier</code> representing the XACML ObligationId
     * @param attributeAssignmentsIn the <code>Collection</code> of <code>AttributeAssignment</code>s
     *            representing the XACML AttributeAssignments
     */
    public StdMutableObligation(Identifier idIn, Collection<AttributeAssignment> attributeAssignmentsIn) {
        this.id = idIn;
        if (attributeAssignmentsIn != null) {
            this.attributeAssignments = new ArrayList<AttributeAssignment>();
            this.attributeAssignments.addAll(attributeAssignmentsIn);
        } else {
            this.attributeAssignments = EMPTY_LIST;
        }
    }

    /**
     * Creates a new <code>StdMutableObligation</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier} representing the XACML ObligationId.
     *
     * @param idIn the <code>Identifier</code> representing the XACML ObligationId
     */
    public StdMutableObligation(Identifier idIn) {
        this(idIn, null);
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Identifier} representing the XACML ObligationId for the
     * Obligation represented by this <code>StdMutableObligation</code>.
     *
     * @param identifier the <code>Identifier</code> representing the XACML ObligationId for the Obligation
     *            represented by this <code>StdMutableObligation</code>.
     */
    public void setId(Identifier identifier) {
        this.id = identifier;
    }

    @Override
    public Collection<AttributeAssignment> getAttributeAssignments() {
        return Collections.unmodifiableCollection(this.attributeAssignments);
    }

    /**
     * Adds an {@link org.apache.openaz.xacmo.api.AttributeAssignment} to this
     * <code>StdMutableObligation</code>.
     *
     * @param attributeAssignment the <code>AttributeAssignment</code> to add to this
     *            <code>StdMutableObligation</code>.
     */
    public void addAttributeAssignment(AttributeAssignment attributeAssignment) {
        if (this.attributeAssignments == EMPTY_LIST) {
            this.attributeAssignments = new ArrayList<AttributeAssignment>();
        }
        this.attributeAssignments.add(attributeAssignment);
    }

    /**
     * Adds a copy of the given <code>Collection</code> of
     * {@link org.apache.openaz.xacml.api.AttributeAssignment}s to this <code>StdMutableObligation</code>.
     *
     * @param attributeAssignmentsIn the <code>Collection</code> of <code>AttributeAssignment</code>s to add
     *            to this <code>StdMutableObligation</code>.
     */
    public void addAttributeAssignments(Collection<AttributeAssignment> attributeAssignmentsIn) {
        this.attributeAssignments = EMPTY_LIST;
        this.addAttributeAssignments(attributeAssignmentsIn);
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.AttributeAssignment}s in this
     * <code>StdMutableObligation</code> to a copy of the given <code>Collection</code>.
     *
     * @param attributeAssignmentsIn the <code>Collection</code> of <code>AttributeAssignment</code>s to set
     *            in this <code>StdMutableObligation</code>.
     */
    public void setAttributeAssignments(Collection<AttributeAssignment> attributeAssignmentsIn) {
        this.attributeAssignments = EMPTY_LIST;
        this.addAttributeAssignments(attributeAssignmentsIn);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof Obligation)) {
            return false;
        } else {
            Obligation objObligation = (Obligation)obj;
            return ObjUtil.equalsAllowNull(this.getId(), objObligation.getId())
                   && ListUtil.equalsAllowNulls(this.getAttributeAssignments(),
                                                objObligation.getAttributeAssignments());
        }
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (getId() != null) {
            result = 31 * result + getId().hashCode();
        }
        if (getAttributeAssignments() != null) {
            result = 31 * result + getAttributeAssignments().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        boolean needsComma = false;
        Identifier identifier = this.getId();
        if (identifier != null) {
            stringBuilder.append("id=");
            stringBuilder.append(identifier.toString());
            needsComma = true;
        }
        Collection<AttributeAssignment> listAttributeAssignments = this.getAttributeAssignments();
        if (listAttributeAssignments.size() > 0) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("attributeAssignments=");
            stringBuilder.append(ListUtil.toString(listAttributeAssignments));
            needsComma = true;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
