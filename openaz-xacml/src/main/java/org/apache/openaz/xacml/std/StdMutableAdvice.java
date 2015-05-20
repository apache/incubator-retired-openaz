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

import org.apache.openaz.xacml.api.Advice;
import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.util.ListUtil;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * Mutable implementation of the {@link org.apache.openaz.xacml.api.Advice} interface.
 */
public class StdMutableAdvice implements Advice {
    private static final List<AttributeAssignment> EMPTY_ATTRIBUTE_ASSIGNMENTS = Collections
        .unmodifiableList(new ArrayList<AttributeAssignment>());

    private Identifier id;
    private List<AttributeAssignment> attributeAssignments;

    /**
     * Creates a new empty <code>StdMutableAdvice</code>.
     */
    public StdMutableAdvice() {
        this.attributeAssignments = EMPTY_ATTRIBUTE_ASSIGNMENTS;
    }

    /**
     * Creates a new <code>StdMutableAdvice</code> with the given {@link org.apache.openaz.xacml.Identifier} as
     * its unique identifier, and the given <code>Collection</code> of
     * {@link org.apache.openaz.xacml.AttributeAssignment}s. A copy of the <code>AttributeAssignment</code>s is
     * made.
     *
     * @param idIn the <code>Identifier</code> that uniquely identifies this <code>StdMutableAdvice</code>.
     *            Should not be null.
     * @param attributeAssignmentsIn the <code>Collection</code> of <code>AttributeAssignment</code>s for this
     *            <code>StdMutableAdvice</code>. May be null.
     */
    public StdMutableAdvice(Identifier idIn, Collection<AttributeAssignment> attributeAssignmentsIn) {
        this();
        this.id = idIn;
        this.attributeAssignments = new ArrayList<AttributeAssignment>();
        if (attributeAssignmentsIn != null) {
            this.addAttributeAssignments(attributeAssignmentsIn);
        }
    }

    /**
     * Creates a new <code>StdMutableAdvice</code> with the given {@link org.apache.openaz.xacml.Identifier} as
     * its unique identifier.
     *
     * @param idIn the <code>Identifier</code> that uniquely identifies this <code>StdMutableAdvice</code>.
     *            May be null.
     */
    public StdMutableAdvice(Identifier idIn) {
        this(idIn, null);
        this.attributeAssignments = new ArrayList<AttributeAssignment>();
    }

    /**
     * Creates a copy of the given {@link org.apache.openaz.xacml.api.Advice} as a new <code>StdMutableAdvice</code>.
     *
     * @param advice the <code>Advice</code> to copy
     * @return a new <code>StdMutableAdvice</code> that is a copy of the given <code>Advice</code>
     */
    public static StdMutableAdvice copy(Advice advice) {
        return new StdMutableAdvice(advice.getId(), advice.getAttributeAssignments());
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Identifier} representing the XACML AdviceId of the Advice represented by this <code>StdMutableAdvice</code>.
     *
     * @param identifier the <code>Identifier</code> representing the XACML AdviceId of the Advice represented
     *            by this <code>StdMutableAdvice</code>.
     */
    public void setId(Identifier identifier) {
        this.id = identifier;
    }

    @Override
    public Collection<AttributeAssignment> getAttributeAssignments() {
        return (this.attributeAssignments == EMPTY_ATTRIBUTE_ASSIGNMENTS
            ? this.attributeAssignments : Collections.unmodifiableList(this.attributeAssignments));
    }

    /**
     * Adds an {@link org.apache.openaz.xacml.api.AttributeAssignment} to this <code>StdMutableAdvice</code>>
     *
     * @param attributeAssignment the <code>AttributeAssignment</code> to add to this
     *            <code>StdMutableAdvice</code>>
     */
    public void addAttributeAssignment(AttributeAssignment attributeAssignment) {
        if (this.attributeAssignments == EMPTY_ATTRIBUTE_ASSIGNMENTS) {
            this.attributeAssignments = new ArrayList<AttributeAssignment>();
        }
        this.attributeAssignments.add(attributeAssignment);
    }

    /**
     * Adds a copy of the given <code>Collection</code> of
     * {@link org.apache.openaz.xacml.api.AttributeAssignment}s to this <code>StdMutableAdvice</code>>
     *
     * @param listAttributeAssignments the <code>Collection</code> of <code>AttributeAssignment</code>s to add
     *            to this <code>StdMutableAdvice</code>.
     */
    public void addAttributeAssignments(Collection<AttributeAssignment> listAttributeAssignments) {
        if (listAttributeAssignments != null && listAttributeAssignments.size() > 0) {
            if (this.attributeAssignments == EMPTY_ATTRIBUTE_ASSIGNMENTS) {
                this.attributeAssignments = new ArrayList<AttributeAssignment>();
            }
            this.attributeAssignments.addAll(listAttributeAssignments);
        }
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.AttributeAssignment}s in this <code>StdMutableAdvice</code>
     * to a copy of the given <code>Collection</code>.
     *
     * @param listAttributeAssignments the <code>Collection</code> of <code>AttributeAssignment</code>s to set
     *            in this <code>StdMutableAdvice</code>.
     */
    public void setAttributeAssignments(Collection<AttributeAssignment> listAttributeAssignments) {
        this.attributeAssignments = EMPTY_ATTRIBUTE_ASSIGNMENTS;
        this.addAttributeAssignments(listAttributeAssignments);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof Advice)) {
            return false;
        } else {
            Advice adviceObj = (Advice)obj;
            return ObjUtil.equalsAllowNull(this.getId(), adviceObj.getId()) && ListUtil
                .equalsAllowNulls(this.getAttributeAssignments(), adviceObj.getAttributeAssignments());
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
        if (this.attributeAssignments.size() > 0) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("attributeAssignments=");
            stringBuilder.append(ListUtil.toString(this.attributeAssignments));
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
