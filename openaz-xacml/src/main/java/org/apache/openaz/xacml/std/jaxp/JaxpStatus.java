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
package org.apache.openaz.xacml.std.jaxp;

import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.api.StatusDetail;
import org.apache.openaz.xacml.std.StdMutableStatus;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.StatusType;

/**
 * JaxpStatus extends {@link org.apache.openaz.StdMutableStatus.common.std.StdStatus} with methods for creation
 * from JAXP elements.
 */
public class JaxpStatus extends StdMutableStatus {

    protected JaxpStatus(StatusCode statusCodeIn, String statusMessageIn, StatusDetail statusDetailIn) {
        super(statusCodeIn, statusMessageIn, statusDetailIn);
    }

    public static JaxpStatus newInstance(StatusType statusType) {
        if (statusType == null) {
            throw new NullPointerException("Null StatusType");
        } else if (statusType.getStatusCode() == null) {
            throw new IllegalArgumentException("Null StatusCode in StatusType");
        }
        StatusCode statusCode = JaxpStatusCode.newInstance(statusType.getStatusCode());
        StatusDetail statusDetail = null;
        if (statusType.getStatusDetail() != null) {
            statusDetail = JaxpStatusDetail.newInstance(statusType.getStatusDetail());
        }

        return new JaxpStatus(statusCode, statusType.getStatusMessage(), statusDetail);

    }
}
