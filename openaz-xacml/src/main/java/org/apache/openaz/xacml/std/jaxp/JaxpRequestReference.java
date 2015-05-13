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

import java.util.Iterator;

import org.apache.openaz.xacml.std.StdMutableRequestReference;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesReferenceType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestReferenceType;

/**
 * JaxpRequestReference extends {@link org.apache.openaz.xacml.std.StdMutableRequestReference} with methods for
 * creation form JAXP elements.
 */
public class JaxpRequestReference extends StdMutableRequestReference {

    protected JaxpRequestReference() {
    }

    public static JaxpRequestReference newInstance(RequestReferenceType requestReferenceType) {
        if (requestReferenceType == null) {
            throw new NullPointerException("Null RequestReferenceType");
        } else if (requestReferenceType.getAttributesReference() == null
                   || requestReferenceType.getAttributesReference().size() == 0) {
            throw new IllegalArgumentException("No AttributesReferenceTypes in RequestReferenceType");
        }
        JaxpRequestReference jaxpRequestReference = new JaxpRequestReference();
        Iterator<AttributesReferenceType> iterAttributesReferenceTypes = requestReferenceType
            .getAttributesReference().iterator();
        while (iterAttributesReferenceTypes.hasNext()) {
            jaxpRequestReference.add(JaxpRequestAttributesReference.newInstances(iterAttributesReferenceTypes
                .next()));
        }
        return jaxpRequestReference;
    }

}
