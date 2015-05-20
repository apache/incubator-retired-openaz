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

import org.apache.openaz.xacml.api.Response;
import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.util.ListUtil;

/**
 * Mutable implementation of the {@link org.apache.openaz.xacml.api.Response} interface.
 */
public class StdMutableResponse implements Response {
    private static final List<Result> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<Result>());

    private List<Result> results;

    /**
     * Creates a new <code>StdMutableResponse</code> with no {@link org.apache.openaz.xacml.api.Result}s.
     */
    public StdMutableResponse() {
        this.results = EMPTY_LIST;
    }

    /**
     * Creates a new <code>StdMutableResponse</code> with a single {@link org.apache.openaz.xacml.api.Result}.
     *
     * @param resultIn the <code>Result</code> for the new <code>StdMutableResponse</code>.
     */
    public StdMutableResponse(Result resultIn) {
        if (resultIn != null) {
            this.results = new ArrayList<Result>();
            this.results.add(resultIn);
        } else {
            this.results = EMPTY_LIST;
        }
    }

    /**
     * Creates a new <code>StdMutableResponse</code> with a copy of the {@link org.apache.openaz.xacml.api.Result}s in
     * the given <code>Collection</code>>
     *
     * @param listResults the <code>Collection</code> of <code>Result</code>s for the new
     *            <code>StdMutableResponse</code>
     */
    public StdMutableResponse(Collection<Result> listResults) {
        if (listResults != null && listResults.size() > 0) {
            this.results = new ArrayList<Result>();
            this.results.addAll(listResults);
        } else {
            this.results = EMPTY_LIST;
        }
    }

    /**
     * Creates a new <code>StdMutableResponse</code> that is a copy of the given
     * {@link org.apache.openaz.xacml.api.Response}.
     *
     * @param copy the <code>Response</code> to copy
     */
    public StdMutableResponse(Response copy) {
        this(copy.getResults());
    }

    /**
     * Creates a new <code>StdMutableResponse</code> with a single {@link org.apache.openaz.xacml.api.Result}
     * defined by the given {@link org.apache.openaz.xacml.api.Status}.
     *
     * @param status the <code>Status</code> of the <code>Result</code> for the new
     *            <code>StdMutableResponse</code>.
     */
    public StdMutableResponse(Status status) {
        this(new StdMutableResult(status));
    }

    @Override
    public Collection<Result> getResults() {
        return (this.results == EMPTY_LIST ? this.results : Collections.unmodifiableCollection(this.results));
    }

    /**
     * Adds a {@link org.apache.openaz.xacml.api.Result} to this <code>StdMutableResponse</code>>
     *
     * @param result the <code>Result</code> to add
     */
    public void add(Result result) {
        if (this.results == EMPTY_LIST) {
            this.results = new ArrayList<Result>();
        }
        this.results.add(result);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof Response)) {
            return false;
        } else {
            Response objResponse = (Response)obj;
            return ListUtil.equalsAllowNulls(this.getResults(), objResponse.getResults());
        }
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (getResults() != null) {
            result = 31 * result + getResults().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        Collection<Result> listResults = this.getResults();
        if (listResults.size() > 0) {
            stringBuilder.append("results=");
            stringBuilder.append(ListUtil.toString(listResults));
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
