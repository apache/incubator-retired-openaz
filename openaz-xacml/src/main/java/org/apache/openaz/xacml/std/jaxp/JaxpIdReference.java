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

import java.text.ParseException;

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Version;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdIdReference;
import org.apache.openaz.xacml.std.StdVersion;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.IdReferenceType;

/**
 * JaxpIdReference extends {@link org.apache.openaz.xacml.std.StdIdReference} with methods for creation from
 * JAXP elements.
 */
public class JaxpIdReference extends StdIdReference {

    protected JaxpIdReference(Identifier idIn, Version versionIn) {
        super(idIn, versionIn);
    }

    public static JaxpIdReference newInstance(IdReferenceType idReferenceType) {
        if (idReferenceType == null) {
            throw new NullPointerException("Null IdReferenceType");
        } else if (idReferenceType.getValue() == null) {
            throw new IllegalArgumentException("Null value in IdReferenceType");
        }

        Version version = null;
        if (idReferenceType.getVersion() != null) {
            try {
                version = StdVersion.newInstance(idReferenceType.getVersion());
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Invalid version");
            }
        }
        return new JaxpIdReference(new IdentifierImpl(idReferenceType.getValue()), version);
    }

}
