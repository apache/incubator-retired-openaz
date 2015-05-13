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

import org.apache.openaz.xacml.api.Response;
import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.Response} interface.
 */
public class StdResponse extends Wrapper<Response> implements Response {
    /**
     * Creates an immutable <code>StdResponse</code> wrapping the given
     * {@link org.apache.openaz.xacml.api.Response}.
     *
     * @param response the <code>Response</code> to wrap in the new <code>StdResponse</code>
     */
    public StdResponse(Response response) {
        super(response);
    }

    /**
     * Creates a new <code>StdResponse</code> with the single given {@link org.apache.openaz.xacml.api.Result}.
     *
     * @param result the <code>Result</code> for the new <code>StdResponse</code>.
     */
    public StdResponse(Result result) {
        this(new StdMutableResponse(result));
    }

    /**
     * Creates a new <code>StdResponse</code> with a copy of the {@link org.apache.openaz.xacml.api.Result}s in
     * the given <code>Collection</code>.
     *
     * @param listResults the <code>Collection</code> of <code>Result</code>s to copy
     */
    public StdResponse(Collection<Result> listResults) {
        this(new StdMutableResponse(listResults));
    }

    @Override
    public Collection<Result> getResults() {
        return this.getWrappedObject().getResults();
    }

}
