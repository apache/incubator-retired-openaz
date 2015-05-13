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

import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.api.StatusDetail;
import org.apache.openaz.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.Status} interface.
 */
public class StdStatus extends Wrapper<Status> implements Status {
    public static final Status STATUS_OK = new StdStatus(StdStatusCode.STATUS_CODE_OK);

    /**
     * Creates a new immutable <code>StdStatus</code> that wraps the given
     * {@link org.apache.openaz.xacml.api.Status}. The caller agrees not to modify the given
     * <code>Status</code> as long as the new <code>StdStatus</code> refers to it.
     *
     * @param status the <code>Status</code> to wrap
     */
    public StdStatus(Status status) {
        super(status);
    }

    /**
     * Creates a new <code>StdStatus</code> with the given {@link org.apache.openaz.xacml.api.StatusCode},
     * <code>String</code> status message, and {@link org.apache.openaz.xacml.api.StatusDetail}.
     *
     * @param statusCodeIn the <code>StatusCode</code> representing the XACML StatusCode.
     * @param statusMessageIn the <code>String</code> representing the XACML StatusMessage
     * @param statusDetailIn the <code>StatusDetail</code> representing the XACML StatusDetail
     */
    public StdStatus(StatusCode statusCodeIn, String statusMessageIn, StatusDetail statusDetailIn) {
        this(new StdMutableStatus(statusCodeIn, statusMessageIn, statusDetailIn));
    }

    /**
     * Creates a new <code>StdStatus</code> with the given {@link org.apache.openaz.xacml.api.StatusCode},
     * <code>String</code> status message and no {@link org.apache.openaz.xacml.api.StatusDetail}.
     *
     * @param statusCodeIn the <code>StatusCode</code> representing the XACML StatusCode.
     * @param statusMessageIn the <code>String</code> representing the XACML StatusMessage
     */
    public StdStatus(StatusCode statusCodeIn, String statusMessageIn) {
        this(new StdMutableStatus(statusCodeIn, statusMessageIn));
    }

    /**
     * Creates a new <code>StdStatus</code> with the given {@link org.apache.openaz.xacml.api.StatusCode}, a
     * null status message and no {@link org.apache.openaz.xacml.api.StatusDetail}.
     *
     * @param statusCodeIn the <code>StatusCode</code> representing the XACML StatusCode.
     */
    public StdStatus(StatusCode statusCodeIn) {
        this(new StdMutableStatus(statusCodeIn));
    }

    /**
     * Creates a new <code>StdStatus</code> that is a copy of the given
     * {@link org.apache.openaz.xacml.api.Status}.
     *
     * @param status the <code>Status</code> to copy
     * @return a new <code>StatusStatus</code> that is a copy of the given <code>Status</code>.
     */
    public static StdStatus copy(Status status) {
        return new StdStatus(status.getStatusCode(), status.getStatusMessage(), status.getStatusDetail());
    }

    @Override
    public StatusCode getStatusCode() {
        return this.getWrappedObject().getStatusCode();
    }

    @Override
    public String getStatusMessage() {
        return this.getWrappedObject().getStatusMessage();
    }

    @Override
    public StatusDetail getStatusDetail() {
        return this.getWrappedObject().getStatusDetail();
    }

    @Override
    public boolean isOk() {
        return this.getWrappedObject().isOk();
    }

    @Override
    public Status merge(Status status) {
        return new StdStatus(this.getWrappedObject().merge(status));
    }

}
