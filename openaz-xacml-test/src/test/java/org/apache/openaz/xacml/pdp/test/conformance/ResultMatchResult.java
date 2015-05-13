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
package org.apache.openaz.xacml.pdp.test.conformance;

import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.util.ListUtil;

/**
 * ResultMatchResult provides information about how well a {@link org.apache.openaz.xacml.api.Result} object
 * matches another <code>Result</code> object.
 */
public class ResultMatchResult {
    private boolean bAssociatedAdviceMatches = true;
    private boolean bAttributesMatch = true;
    private boolean bDecisionsMatch = true;
    private boolean bObligationsMatch = true;
    private boolean bPolicyIdentifiersMatch = true;
    private boolean bPolicySetIdentifiersMatch = true;
    private boolean bStatusCodesMatch = true;
    private boolean bUnknownFunction = false;

    protected void setAssociatedAdviceMatches(boolean b) {
        this.bAssociatedAdviceMatches = b;
    }

    protected void setAttributesMatch(boolean b) {
        this.bAttributesMatch = b;
    }

    protected void setDecisionsMatch(boolean b) {
        this.bDecisionsMatch = b;
    }

    protected void setObligationsMatch(boolean b) {
        this.bObligationsMatch = b;
    }

    protected void setPolicyIdentifiersMatch(boolean b) {
        this.bPolicyIdentifiersMatch = b;
    }

    protected void setPolicySetIdentifiersMatch(boolean b) {
        this.bPolicySetIdentifiersMatch = b;
    }

    protected void setStatusCodesMatch(boolean b) {
        this.bStatusCodesMatch = b;
    }

    protected void setUnknownFunction(boolean b) {
        this.bUnknownFunction = b;
    }

    public ResultMatchResult() {
    }

    public static ResultMatchResult newInstance(Result result1, Result result2) {
        ResultMatchResult resultMatchResult = new ResultMatchResult();
        if (result2 != null && result2.getStatus() != null
            && result2.getStatus().getStatusCode().equals(StdStatusCode.STATUS_CODE_PROCESSING_ERROR)
            && result2.getStatus().getStatusMessage() != null
            && result2.getStatus().getStatusMessage().contains("Unknown Function")) {
            resultMatchResult.setUnknownFunction(true);
        }
        if (result1 == null || result2 == null) {
            resultMatchResult.setAssociatedAdviceMatches(false);
            resultMatchResult.setAttributesMatch(false);
            resultMatchResult.setDecisionsMatch(false);
            resultMatchResult.setObligationsMatch(false);
            resultMatchResult.setPolicyIdentifiersMatch(false);
            resultMatchResult.setPolicySetIdentifiersMatch(false);
            resultMatchResult.setStatusCodesMatch(false);
        } else {
            resultMatchResult.setAssociatedAdviceMatches(ListUtil.equalsAllowNulls(result1
                .getAssociatedAdvice(), result2.getAssociatedAdvice()));
            resultMatchResult.setAttributesMatch(ListUtil.equalsAllowNulls(result1.getAttributes(),
                                                                           result2.getAttributes()));
            resultMatchResult.setDecisionsMatch(result1.getDecision() == result2.getDecision());
            resultMatchResult.setObligationsMatch(ListUtil.equalsAllowNulls(result1.getObligations(),
                                                                            result2.getObligations()));
            resultMatchResult.setPolicyIdentifiersMatch(ListUtil.equalsAllowNulls(result1
                .getPolicyIdentifiers(), result2.getPolicyIdentifiers()));
            resultMatchResult.setPolicySetIdentifiersMatch(ListUtil.equalsAllowNulls(result1
                .getPolicySetIdentifiers(), result2.getPolicySetIdentifiers()));
            if (result1.getStatus() == null || result1.getStatus().getStatusCode() == null
                || result2.getStatus() == null || result2.getStatus().getStatusCode() == null) {
                resultMatchResult.setStatusCodesMatch(false);
            } else {
                resultMatchResult.setStatusCodesMatch(result1.getStatus().getStatusCode()
                    .equals(result2.getStatus().getStatusCode()));
            }
        }
        return resultMatchResult;
    }

    public boolean associatedAdviceMatches() {
        return this.bAssociatedAdviceMatches;
    }

    public boolean attributesMatch() {
        return this.bAttributesMatch;
    }

    public boolean decisionsMatch() {
        return this.bDecisionsMatch;
    }

    public boolean obligationsMatch() {
        return this.bObligationsMatch;
    }

    public boolean policyIdentifiersMatch() {
        return this.bPolicyIdentifiersMatch;
    }

    public boolean policySetIdentifiersMatch() {
        return this.bPolicySetIdentifiersMatch;
    }

    public boolean statusCodesMatch() {
        return this.bStatusCodesMatch;
    }

    public boolean unknownFunction() {
        return this.bUnknownFunction;
    }

}
