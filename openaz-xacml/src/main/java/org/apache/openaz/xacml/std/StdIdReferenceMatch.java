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

import org.apache.openaz.xacml.api.IdReferenceMatch;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.VersionMatch;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.IdReferenceMatch} interface.
 */
public class StdIdReferenceMatch implements IdReferenceMatch {
    private Identifier id;
    private VersionMatch version;
    private VersionMatch earliestVersion;
    private VersionMatch latestVersion;

    /**
     * Creates a new <code>StdIdReferenceMatch</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier} representing the PolicyId or PolicySetId to match, and
     * the given set of {@link org.apache.openaz.xacml.api.VersionMatch} objects specifying which XACML
     * Versions are acceptable.
     *
     * @param idIn the <code>Identifier</code> representing the PolicyId or PolicySetId.
     * @param versionIn the <code>VersionMatch</code> for an exact match against the current Version of a
     *            Policy or PolicySet (may be null)
     * @param earliestVersionIn the <code>VersionMatch</code> for a lower-bound match against the current
     *            Version of a Policy or PolicySet (may be null)
     * @param latestVersionIn the <code>VersionMatch</code> for an upper-bound match against the current
     *            Version of a Policy or PolicySet (may be null)
     */
    public StdIdReferenceMatch(Identifier idIn, VersionMatch versionIn, VersionMatch earliestVersionIn,
                               VersionMatch latestVersionIn) {
        this.id = idIn;
        this.version = versionIn;
        this.earliestVersion = earliestVersionIn;
        this.latestVersion = latestVersionIn;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public VersionMatch getVersion() {
        return this.version;
    }

    @Override
    public VersionMatch getEarliestVersion() {
        return this.earliestVersion;
    }

    @Override
    public VersionMatch getLatestVersion() {
        return this.latestVersion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof IdReferenceMatch)) {
            return false;
        } else {
            IdReferenceMatch objIdReferenceMatch = (IdReferenceMatch)id;
            return ObjUtil.equalsAllowNull(this.getId(), objIdReferenceMatch.getId())
                   && ObjUtil.equalsAllowNull(this.getVersion(), objIdReferenceMatch.getVersion())
                   && ObjUtil.equalsAllowNull(this.getEarliestVersion(),
                                              objIdReferenceMatch.getEarliestVersion())
                   && ObjUtil
                       .equalsAllowNull(this.getLatestVersion(), objIdReferenceMatch.getLatestVersion());
        }
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (getId() != null) {
            result = 31 * result + getId().hashCode();
        }
        if (getVersion() != null) {
            result = 31 * result + getVersion().hashCode();
        }
        if (getEarliestVersion() != null) {
            result = 31 * result + getEarliestVersion().hashCode();
        }
        if (getLatestVersion() != null) {
            result = 31 * result + getLatestVersion().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        boolean needsComma = false;
        Object objectToDump;
        if ((objectToDump = this.getId()) != null) {
            stringBuilder.append("id=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }
        if ((objectToDump = this.getVersion()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("version=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getEarliestVersion()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append(",earliestVersion=");
            stringBuilder.append(objectToDump);
        }
        if ((objectToDump = this.getLatestVersion()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append(",latestVersion=");
            stringBuilder.append(objectToDump);
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
