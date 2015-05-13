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

import java.util.Collection;

import org.apache.openaz.xacml.api.RequestAttributesReference;
import org.apache.openaz.xacml.api.RequestReference;
import org.apache.openaz.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.RequestReference} interface.
 */
public class StdRequestReference extends Wrapper<RequestReference> implements RequestReference {
    /**
     * Creates a new immutable <code>StdRequestReference</code> that wraps the given
     * {@link org.apache.openaz.xacml.api.RequestReference}.
     *
     * @param requestReference the <code>RequestReference</code> to wrap.
     */
    public StdRequestReference(RequestReference requestReference) {
        super(requestReference);
    }

    /**
     * Creates a new <code>StdRequestReference</code> with a copy of the given <code>Collection</code> of
     * {@link org.apache.openaz.xacml.api.RequestAttributesReference}s.
     *
     * @param requestAttributesReferences the <code>Collection</code> of
     *            <code>RequestAttributesReference</code>s to copy into the new
     *            <code>StdRequestReference</code>>
     */
    public StdRequestReference(Collection<RequestAttributesReference> requestAttributesReferences) {
        this(new StdMutableRequestReference(requestAttributesReferences));
    }

    @Override
    public Collection<RequestAttributesReference> getAttributesReferences() {
        return this.getWrappedObject().getAttributesReferences();
    }

}
