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
import org.apache.openaz.xacml.api.VersionMatch;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdIdReferenceMatch;
import org.apache.openaz.xacml.std.StdVersionMatch;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.IdReferenceType;

/**
 * JaxpIdReferenceMatch extends {@link org.apache.openaz.xacml.std.StdIdReferenceMatch} with methods for
 * creation from JAXP elements.
 */
public class JaxpIdReferenceMatch extends StdIdReferenceMatch {

    protected JaxpIdReferenceMatch(Identifier idIn, VersionMatch versionIn, VersionMatch earliestVersionIn,
                                   VersionMatch latestVersionIn) {
        super(idIn, versionIn, earliestVersionIn, latestVersionIn);
    }

    public static JaxpIdReferenceMatch newInstance(IdReferenceType idReferenceType) {
        if (idReferenceType == null) {
            throw new NullPointerException("Null IdReferenceType");
        } else if (idReferenceType.getValue() == null) {
            throw new IllegalArgumentException("Null value for IdReferenceType");
        }

        VersionMatch version = null;
        VersionMatch earliestVersion = null;
        VersionMatch latestVersion = null;

        if (idReferenceType.getVersion() != null) {
            try {
                version = StdVersionMatch.newInstance(idReferenceType.getVersion());
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Invalid version");
            }
        }
        if (idReferenceType.getEarliestVersion() != null) {
            try {
                earliestVersion = StdVersionMatch.newInstance(idReferenceType.getEarliestVersion());
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Invalid earliest version");
            }
        }

        if (idReferenceType.getLatestVersion() != null) {
            try {
                latestVersion = StdVersionMatch.newInstance(idReferenceType.getLatestVersion());
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Invalid latest version");
            }
        }

        return new JaxpIdReferenceMatch(new IdentifierImpl(idReferenceType.getValue()), version,
                                        earliestVersion, latestVersion);
    }
}
