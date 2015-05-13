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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.MatchResult;
import org.apache.openaz.xacml.pdp.eval.Matchable;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.util.StringUtils;

/**
 * AnyOf extends {@link org.apache.openaz.xacml.pdp.policy.PolicyComponent} and implements the
 * {@link org.apache.openaz.xacml.pdp.policy.Matchable} interface to represent XACML AllOf elements in a
 * XACML Target.
 */
public class AllOf extends PolicyComponent implements Matchable {
    private List<Match> matches;

    protected List<Match> getMatchList(boolean bNoNulls) {
        if (this.matches == null && bNoNulls) {
            this.matches = new ArrayList<Match>();
        }
        return this.matches;
    }

    protected void clearMatchList() {
        if (this.matches != null) {
            this.matches.clear();
        }
    }

    public AllOf(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public AllOf(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public AllOf() {
    }

    public Iterator<Match> getMatches() {
        return (this.matches == null ? null : this.matches.iterator());
    }

    public void setMatches(Collection<Match> matchesIn) {
        this.clearMatchList();
        if (matchesIn != null) {
            this.addMatches(matchesIn);
        }
    }

    public void addMatch(Match match) {
        List<Match> matchList = this.getMatchList(true);
        matchList.add(match);
    }

    public void addMatches(Collection<Match> matchesIn) {
        List<Match> matchList = this.getMatchList(true);
        matchList.addAll(matchesIn);
    }

    @Override
    public MatchResult match(EvaluationContext evaluationContext) throws EvaluationException {
        if (!this.validate()) {
            return new MatchResult(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }
        Iterator<Match> iterMatches = this.getMatches();
        assert iterMatches != null && iterMatches.hasNext();

        MatchResult matchResultFallThrough = MatchResult.MM_MATCH;
        while (iterMatches.hasNext()) {
            MatchResult matchResultMatch = iterMatches.next().match(evaluationContext);
            assert matchResultMatch != null;
            switch (matchResultMatch.getMatchCode()) {
            case INDETERMINATE:
                if (matchResultFallThrough.getMatchCode() != MatchResult.MatchCode.INDETERMINATE) {
                    matchResultFallThrough = matchResultMatch;
                }
                break;
            case MATCH:
                break;
            case NOMATCH:
                return matchResultMatch;
            }
        }
        return matchResultFallThrough;
    }

    @Override
    protected boolean validateComponent() {
        Iterator<Match> iterMatches = this.getMatches();
        if (iterMatches == null || !iterMatches.hasNext()) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing matches");
            return false;
        } else {
            this.setStatus(StdStatusCode.STATUS_CODE_OK, null);
            return true;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        String stringMatches = StringUtils.toString(this.getMatches());
        if (stringMatches != null) {
            stringBuilder.append(",matches=");
            stringBuilder.append(stringMatches);
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
