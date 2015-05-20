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
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * Mutable implementation of the {@link org.apache.openaz.xacml.api.Status} interface to represent a XACML
 * Status element.
 */
public class StdMutableStatus implements Status {
    private StatusCode statusCode;
    private String statusMessage;
    private StatusDetail statusDetail;

    /**
     * Creates a new <code>StdMutableStatus</code> with the given
     * {@link org.apache.openaz.xacml.api.StatusCode}, <code>String</code> status message, and
     * {@link org.apache.openaz.xacml.api.StatusDetail}.
     *
     * @param statusCodeIn the <code>StatusCode</code> representing the XACML StatusCode.
     * @param statusMessageIn the <code>String</code> representing the XACML StatusMessage
     * @param statusDetailIn the <code>StatusDetail</code> representing the XACML StatusDetail
     */
    public StdMutableStatus(StatusCode statusCodeIn, String statusMessageIn, StatusDetail statusDetailIn) {
        this.statusCode = statusCodeIn;
        this.statusMessage = statusMessageIn;
        this.statusDetail = statusDetailIn;
    }

    /**
     * Creates a new <code>StdMutableStatus</code> with the given
     * {@link org.apache.openaz.xacml.api.StatusCode}, <code>String</code> status message and no
     * {@link org.apache.openaz.xacml.api.StatusDetail}.
     *
     * @param statusCodeIn the <code>StatusCode</code> representing the XACML StatusCode.
     * @param statusMessageIn the <code>String</code> representing the XACML StatusMessage
     */
    public StdMutableStatus(StatusCode statusCodeIn, String statusMessageIn) {
        this(statusCodeIn, statusMessageIn, null);
    }

    /**
     * Creates a new <code>StdMutableStatus</code> with the given
     * {@link org.apache.openaz.xacml.api.StatusCode}, a null status message and no
     * {@link org.apache.openaz.xacml.api.StatusDetail}.
     *
     * @param statusCodeIn the <code>StatusCode</code> representing the XACML StatusCode.
     */
    public StdMutableStatus(StatusCode statusCodeIn) {
        this(statusCodeIn, null, null);
    }

    /**
     * Creates an empty <code>StdMutableStatus</code>.
     */
    public StdMutableStatus() {
    }

    /**
     * Creates a new <code>StdMutableStatus</code> that is a copy of the given
     * {@link org.apache.openaz.xacml.api.Status}.
     *
     * @param status the <code>Status</code> to copy
     * @return a new <code>StdMutableStatus</code> that is a copy of the given <code>Status</code>.
     */
    public static StdMutableStatus copy(Status status) {
        return new StdMutableStatus(status.getStatusCode(), status.getStatusMessage(),
                                    status.getStatusDetail());
    }

    @Override
    public StatusCode getStatusCode() {
        return this.statusCode;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.StatusCode} representing the XACML StatusCode for the Status
     * represented by this <code>StdMutableStatus</code>.
     *
     * @param statusCodeIn the <code>StatusCode</code> representing the XACML StatusCode for the Status
     */
    public void setStatusCode(StatusCode statusCodeIn) {
        this.statusCode = statusCodeIn;
    }

    @Override
    public String getStatusMessage() {
        return this.statusMessage;
    }

    /**
     * Sets the <code>String</code> representing the XACML StatusMessage for the Status represented by this
     * <code>StdMutableStatus</code>.
     *
     * @param message the <code>String</code> representing the XACML StatusMessage for the Status
     */
    public void setStatusMessage(String message) {
        this.statusMessage = message;
    }

    @Override
    public StatusDetail getStatusDetail() {
        return this.statusDetail;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.StatusDetail} representing the XACML StatusDetail for the
     * Status represented by this <code>StdMutableStatus</code>.
     *
     * @param statusDetailIn the <code>StatusDetail</code> representing the XACML StatusDetail for the Status
     */
    public void setStatusDetail(StatusDetail statusDetailIn) {
        this.statusDetail = statusDetailIn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof Status)) {
            return false;
        } else {
            Status objStatus = (Status)obj;
            return ObjUtil.equalsAllowNull(this.getStatusCode(), objStatus.getStatusCode())
                   && ObjUtil.equalsAllowNull(this.getStatusMessage(), objStatus.getStatusMessage())
                   && ObjUtil.equalsAllowNull(this.getStatusDetail(), objStatus.getStatusDetail());
        }
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (getStatusCode() != null) {
            result = 31 * result + getStatusCode().hashCode();
        }
        if (getStatusMessage() != null) {
            result = 31 * result + getStatusMessage().hashCode();
        }
        if (getStatusDetail() != null) {
            result = 31 * result + getStatusDetail().hashCode();
        }
        return result;
    }

    @Override
    public boolean isOk() {
        StatusCode thisStatusCode = this.getStatusCode();
        return thisStatusCode == null || thisStatusCode.equals(StdStatusCode.STATUS_CODE_OK);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        boolean needsComma = false;
        Object objectToDump;

        if ((objectToDump = this.getStatusCode()) != null) {
            stringBuilder.append("statusCode=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }
        if ((objectToDump = this.getStatusMessage()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("statusMessage=");
            stringBuilder.append((String)objectToDump);
            needsComma = true;
        }
        if ((objectToDump = this.getStatusDetail()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("statusDetail=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    public Status merge(Status status) {
        if (status == null || !this.getStatusCode().equals(status.getStatusCode())
            || status.getStatusDetail() == null) {
            return this;
        } else if (this.getStatusDetail() == null) {
            return new StdMutableStatus(this.getStatusCode(), this.getStatusMessage(),
                                        status.getStatusDetail());
        } else {
            return new StdMutableStatus(this.getStatusCode(), this.getStatusMessage(), this.getStatusDetail()
                .merge(status.getStatusDetail()));
        }
    }

}
