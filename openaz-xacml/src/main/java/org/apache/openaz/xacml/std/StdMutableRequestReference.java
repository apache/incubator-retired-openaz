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

import org.apache.openaz.xacml.api.RequestAttributesReference;
import org.apache.openaz.xacml.api.RequestReference;
import org.apache.openaz.xacml.util.ListUtil;

/**
 * Mutable implementation of the {@link org.apache.openaz.xacml.api.RequestReference} interface.
 */
public class StdMutableRequestReference implements RequestReference {
    private static final List<RequestAttributesReference> EMPTY_LIST = Collections
        .unmodifiableList(new ArrayList<RequestAttributesReference>());
    private List<RequestAttributesReference> requestAttributesReferences = EMPTY_LIST;

    /**
     * Creates a new <code>StdMutableRequestReference</code> with no
     * {@link org.apache.openaz.xacml.api.RequestAttributesReference}s.
     */
    public StdMutableRequestReference() {
    }

    public StdMutableRequestReference(Collection<RequestAttributesReference> listRequestAttributesReferencesIn) {
        if (listRequestAttributesReferencesIn != null) {
            this.requestAttributesReferences = new ArrayList<RequestAttributesReference>();
            this.requestAttributesReferences.addAll(listRequestAttributesReferencesIn);
        }
    }

    @Override
    public Collection<RequestAttributesReference> getAttributesReferences() {
        return (this.requestAttributesReferences == EMPTY_LIST
            ? this.requestAttributesReferences : Collections
                .unmodifiableCollection(this.requestAttributesReferences));
    }

    /**
     * Adds a {@link org.apache.openaz.xacml.api.RequestAttributesReference} to this
     * <code>StdMutableRequestReference</code>>
     *
     * @param requestAttributesReference the <code>RequestAttributesReference</code> to add
     */
    public void add(RequestAttributesReference requestAttributesReference) {
        if (this.requestAttributesReferences == EMPTY_LIST) {
            this.requestAttributesReferences = new ArrayList<RequestAttributesReference>();
        }
        this.requestAttributesReferences.add(requestAttributesReference);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof RequestReference)) {
            return false;
        } else {
            RequestReference objRequestReference = (RequestReference)obj;
            return ListUtil.equalsAllowNulls(this.getAttributesReferences(),
                                             objRequestReference.getAttributesReferences());
        }
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (getAttributesReferences() != null) {
            result = 31 * result + getAttributesReferences().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        if (this.requestAttributesReferences.size() > 0) {
            stringBuilder.append("requestAttributesReferences=");
            stringBuilder.append(ListUtil.toString(this.requestAttributesReferences));
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
