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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.Response;
import org.apache.openaz.xacml.api.Result;

/**
 * ResponseMatchResult provides information about how a {@link org.apache.openaz.xacml.api.Response} object
 * matches another <code>Response</code> object.
 */
public class ResponseMatchResult {
    private List<ResultMatchResult> resultMatchResults = new ArrayList<ResultMatchResult>();

    private boolean bAssociatedAdviceMatches = true;
    private boolean bAttributesMatch = true;
    private boolean bDecisionsMatch = true;
    private boolean bStatusCodesMatch = true;
    private boolean bObligationsMatch = true;
    private boolean bPolicyIdentifiersMatch = true;
    private boolean bPolicySetIdentifiersMatch = true;
    private boolean bNumResultsMatch = true;
    private boolean bUnknownFunction;

    protected void addResultMatchResult(ResultMatchResult resultMatchResult) {
        this.resultMatchResults.add(resultMatchResult);
        this.bAssociatedAdviceMatches = resultMatchResult.associatedAdviceMatches()
                                        && this.bAssociatedAdviceMatches;
        this.bAttributesMatch = resultMatchResult.attributesMatch() && this.bAttributesMatch;
        this.bDecisionsMatch = resultMatchResult.decisionsMatch() && this.bDecisionsMatch;
        this.bStatusCodesMatch = resultMatchResult.statusCodesMatch() && this.bStatusCodesMatch;
        this.bObligationsMatch = resultMatchResult.obligationsMatch() && this.bObligationsMatch;
        this.bPolicyIdentifiersMatch = resultMatchResult.policyIdentifiersMatch()
                                       && this.bPolicyIdentifiersMatch;
        this.bPolicySetIdentifiersMatch = resultMatchResult.policySetIdentifiersMatch()
                                          && this.bPolicySetIdentifiersMatch;
        this.bUnknownFunction = resultMatchResult.unknownFunction() || this.bUnknownFunction;
    }

    protected void setNumResultsMatch(boolean b) {
        this.bNumResultsMatch = b;
    }

    public ResponseMatchResult() {
    }

    public static ResponseMatchResult newInstance(Response response1, Response response2) {
        ResponseMatchResult responseMatchResult = new ResponseMatchResult();

        Collection<Result> listResultsResponse1 = response1.getResults();
        Collection<Result> listResultsResponse2 = response2.getResults();
        if (listResultsResponse1.size() == 1 && listResultsResponse2.size() == 1) {
            /*
             * Just add a single ResultMatchResult comparing the results in the two responses
             */
            responseMatchResult.addResultMatchResult(ResultMatchResult.newInstance(listResultsResponse1
                .iterator().next(), listResultsResponse2.iterator().next()));
        } else {
            /*
             * Iterate over all of the results in the two responses and match them
             */
            Iterator<Result> iterResponse1Results = listResultsResponse1.iterator();
            Iterator<Result> iterResponse2Results = listResultsResponse2.iterator();
            while ((iterResponse1Results != null && iterResponse1Results.hasNext())
                   || (iterResponse2Results != null && iterResponse2Results.hasNext())) {
                Result result1 = (iterResponse1Results != null && iterResponse1Results.hasNext()
                    ? iterResponse1Results.next() : null);
                Result result2 = (iterResponse2Results != null && iterResponse2Results.hasNext()
                    ? iterResponse2Results.next() : null);
                if ((result1 == null || result2 == null) && responseMatchResult.numResultsMatch()) {
                    responseMatchResult.setNumResultsMatch(false);
                }
                responseMatchResult.addResultMatchResult(ResultMatchResult.newInstance(result1, result2));
            }
        }
        return responseMatchResult;
    }

    public Iterator<ResultMatchResult> getResultMatchResults() {
        return this.resultMatchResults.iterator();
    }

    public boolean numResultsMatch() {
        return this.bNumResultsMatch;
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
