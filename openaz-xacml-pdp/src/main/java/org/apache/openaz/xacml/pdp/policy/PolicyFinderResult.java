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
package org.apache.openaz.xacml.pdp.policy;

import org.apache.openaz.xacml.api.Status;

/**
 * PolicyFinderResult is the interface for return values of the methods in the
 * {@link org.apache.openaz.xacml.pdp.policy.PolicyFinderFactory} interface.
 *
 * @param <T> the class extending {@link PolicyDef} contained as a result in this
 *            <code>PolicyFinderResult</code>
 */
public interface PolicyFinderResult<T extends PolicyDef> {
    /**
     * Gets the {@link org.apache.openaz.xacml.api.Status} of the method call.
     *
     * @return the <code>Status</code> of the method call
     */
    Status getStatus();

    /**
     * Gets the {@link PolicyDef} returned by the method if the status is OK.
     *
     * @return the <code>T</code> returned by the method if the status is OK.
     */
    T getPolicyDef();
}
