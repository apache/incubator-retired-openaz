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

import org.apache.openaz.xacml.api.RequestAttributesReference;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.RequestAttributesReference} interface.
 */
public class StdRequestAttributesReference implements RequestAttributesReference {
    private String referenceId;

    /**
     * Creates a new <code>StdRequestAttributesReference</code> with the given <code>String</code>
     * representing the xml:Id.
     *
     * @param referenceIdIn the <code>String</code> representing the xml:Id of the XACML AttributesReference
     *            represented by the new <code>StdRequestAttributesReference</code>.
     */
    public StdRequestAttributesReference(String referenceIdIn) {
        this.referenceId = referenceIdIn;
    }

    @Override
    public String getReferenceId() {
        return this.referenceId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof RequestAttributesReference)) {
            return false;
        } else {
            RequestAttributesReference objRequestAttributesReference = (RequestAttributesReference)obj;
            return ObjUtil.equalsAllowNull(this.getReferenceId(),
                                           objRequestAttributesReference.getReferenceId());
        }
    }
    
    public int hashCode() {
        int result = 17;
        if (getReferenceId() != null) {
            result = 31 * result + getReferenceId().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        Object objectToDump = this.getReferenceId();
        if (objectToDump != null) {
            stringBuilder.append("referenceId=");
            stringBuilder.append((String)objectToDump);
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
