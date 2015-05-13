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
package org.apache.openaz.xacml.pdp.std;

import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.pdp.policy.PolicyDef;
import org.apache.openaz.xacml.pdp.policy.PolicyFinderResult;
import org.apache.openaz.xacml.std.StdStatus;

/**
 * StdPolicyFinderResult implements the {@link org.apache.openaz.xacml.pdp.policy.PolicyFinderResult}
 * interface.
 *
 * @param <T> the java class extending {@link org.apache.openaz.xacml.pdp.policy.PolicyDef} held by the
 *            <code>StdPolicyFinderResult</code>
 */
public class StdPolicyFinderResult<T extends PolicyDef> implements PolicyFinderResult<T> {
    private Status status;
    private T policyDef;

    public StdPolicyFinderResult(Status statusIn, T policyDefIn) {
        this.status = (statusIn == null ? StdStatus.STATUS_OK : statusIn);
        this.policyDef = policyDefIn;
    }

    public StdPolicyFinderResult(Status statusIn) {
        this(statusIn, null);
    }

    public StdPolicyFinderResult(T policyDefIn) {
        this(null, policyDefIn);
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public T getPolicyDef() {
        return this.policyDef;
    }

}
