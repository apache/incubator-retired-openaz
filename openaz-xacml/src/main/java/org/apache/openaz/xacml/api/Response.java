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

package org.apache.openaz.xacml.api;

import java.util.Collection;

/**
 * Defines the API for objects that represent XACML Response documents. Response documents wrap the Result
 * elements for individual XACML decision requests.
 */
public interface Response {
    /**
     * Gets the <code>Collection</code> of {@link Result}s objects in this <code>Response</code>. If there are
     * no <code>Result</code>s, this method must return an empty <code>Collection</code>.
     *
     * @return the <code>Collection</code> of {@link Result}s objects in this <code>Response</code>.
     */
    Collection<Result> getResults();

    /**
     * {@inheritDoc} Implementations of this interface must override the <code>equals</code> method with the
     * following semantics: Two <code>Response</code>s (<code>r1</code> and <code>r2</code>) are equal if:
     * {@code r1.getResults()} is pairwise equal to {@code r2.getResults()}
     */
    @Override
    boolean equals(Object obj);
}
