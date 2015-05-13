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
package org.apache.openaz.xacml.std.jaxp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdMutableObligation;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationType;

/**
 * JaxpObligation extends {@link org.apache.openaz.xacml.std.StdMutableObligation} with methods for creation
 * from JAXP elements.
 */
public class JaxpObligation extends StdMutableObligation {

    protected JaxpObligation(Identifier idIn, Collection<AttributeAssignment> attributeAssignmentsIn) {
        super(idIn, attributeAssignmentsIn);
    }

    public static JaxpObligation newInstance(ObligationType obligationType) {
        if (obligationType == null) {
            throw new NullPointerException("Null ObligationType");
        } else if (obligationType.getObligationId() == null) {
            throw new IllegalArgumentException("Null obligationId for ObligationType");
        }
        Identifier obligationId = new IdentifierImpl(obligationType.getObligationId());
        List<AttributeAssignment> attributeAssignments = null;
        if (obligationType.getAttributeAssignment() != null
            && obligationType.getAttributeAssignment().size() > 0) {
            attributeAssignments = new ArrayList<AttributeAssignment>();
            Iterator<AttributeAssignmentType> iterAttributeAssignmentTypes = obligationType
                .getAttributeAssignment().iterator();
            while (iterAttributeAssignmentTypes.hasNext()) {
                attributeAssignments.add(JaxpAttributeAssignment.newInstance(iterAttributeAssignmentTypes
                    .next()));
            }
        }
        return new JaxpObligation(obligationId, attributeAssignments);
    }
}
