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

package org.apache.openaz.xacml.pdp.eval;

import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.std.StdStatus;

/**
 * MatchResult is the value returned by the {@link Matchable} interface.
 */
public class MatchResult {
    public static enum MatchCode {
        INDETERMINATE,
        MATCH,
        NOMATCH
    }

    public static MatchResult MM_MATCH = new MatchResult(MatchCode.MATCH);
    public static MatchResult MM_NOMATCH = new MatchResult(MatchCode.NOMATCH);

    private MatchCode matchCode;
    private Status status;

    public MatchResult(MatchCode matchCodeIn, Status statusIn) {
        this.matchCode = matchCodeIn;
        this.status = statusIn;
    }

    public MatchResult(MatchCode matchCodeIn) {
        this(matchCodeIn, StdStatus.STATUS_OK);
    }

    public MatchResult(Status statusIn) {
        this(MatchCode.INDETERMINATE, statusIn);
    }

    public MatchCode getMatchCode() {
        return this.matchCode;
    }

    public Status getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        stringBuilder.append("matchCode=");
        stringBuilder.append(this.getMatchCode());
        Status thisStatus = this.getStatus();
        if (thisStatus != null) {
            stringBuilder.append(", status=");
            stringBuilder.append(thisStatus.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
