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

import org.apache.openaz.xacml.api.IdReference;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Version;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.IdReference} interface.
 */
public class StdIdReference implements IdReference {
    private Identifier id;
    private Version version;

    /**
     * Creates a new <code>StdIdReference</code> with the given {@link org.apache.openaz.xacml.api.Identifier}
     * representing the XACML PolicyId or PolicySetId, and the given
     * {@link org.apache.openaz.xacml.api.Version} representing the PolicyVersion or PolicySetVersion.
     *
     * @param idIn the <code>Identifier</code> representing the PolicyId or PolicySetId.
     * @param versionIn the <code>Version</code> representing the PolicyVersion or PolicySetVersion.
     */
    public StdIdReference(Identifier idIn, Version versionIn) {
        this.id = idIn;
        this.version = versionIn;
    }

    /**
     * Creates a new <code>StdIdReference</code> with the given {@link org.apache.openaz.xacml.api.Identifier}
     * representing the XACML PolicyId or PolicySetId.
     *
     * @param idIn the <code>Identifier</code> representing the PolicyId or PolicySetId.
     */
    public StdIdReference(Identifier idIn) {
        this(idIn, null);
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public Version getVersion() {
        return this.version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof IdReference)) {
            return false;
        } else {
            IdReference objIdReference = (IdReference)obj;
            return ObjUtil.equalsAllowNull(this.getId(), objIdReference.getId())
                   && ObjUtil.equalsAllowNull(this.getVersion(), objIdReference.getVersion());
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
            needsComma = true;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
