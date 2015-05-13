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
package org.apache.openaz.xacml.pdp.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.util.StringUtils;

/**
 * AttributeAssignmentResult is the object returned by the <code>evaluate</code> method of an
 * {@link org.apache.openaz.xacml.pdp.policy.AttributeAssignmentExpression}. It contains a
 * {@link org.apache.openaz.xacml.api.Status} and an optional collection of
 * {@link org.apache.openaz.xacml.api.AttributeAssignment} elements.
 */
public class AttributeAssignmentResult {
    private Status status;
    private List<AttributeAssignment> listAttributeAssignments;

    protected List<AttributeAssignment> getListAttributeAssignments() {
        return this.listAttributeAssignments;
    }

    public AttributeAssignmentResult(Status statusIn,
                                     Collection<AttributeAssignment> listAttributeAssignmentsIn) {
        this.status = statusIn;
        if (listAttributeAssignmentsIn != null && listAttributeAssignmentsIn.size() > 0) {
            this.listAttributeAssignments = new ArrayList<AttributeAssignment>();
            this.listAttributeAssignments.addAll(listAttributeAssignmentsIn);
        }
    }

    public AttributeAssignmentResult(Status statusIn) {
        this(statusIn, null);
    }

    public AttributeAssignmentResult(Collection<AttributeAssignment> listAttributeAssignmentsIn) {
        this(StdStatus.STATUS_OK, listAttributeAssignmentsIn);
    }

    public Status getStatus() {
        return this.status;
    }

    public boolean isOk() {
        return this.getStatus() == null || this.getStatus().isOk();
    }

    public Iterator<AttributeAssignment> getAttributeAssignments() {
        List<AttributeAssignment> thisListAttributeAssignments = this.getListAttributeAssignments();
        return (thisListAttributeAssignments == null ? null : thisListAttributeAssignments.iterator());
    }

    public int getNumAttributeAssignments() {
        List<AttributeAssignment> thisListAttributeAssignments = this.getListAttributeAssignments();
        return (thisListAttributeAssignments == null ? 0 : thisListAttributeAssignments.size());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        boolean needsComma = false;

        Object objectToDump;
        if ((objectToDump = this.getStatus()) != null) {
            stringBuilder.append("status=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }

        Iterator<?> iterToDump;
        if ((iterToDump = this.getAttributeAssignments()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append(StringUtils.toString(iterToDump));
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
