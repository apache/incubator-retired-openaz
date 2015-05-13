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
 * {@link org.apache.openaz.xacml.pdp.policy.Matchable} interface to represent XACML AnyOf elements in a
 * XACML Target.
 */
public class AnyOf extends PolicyComponent implements Matchable {
    private List<AllOf> allOfs;

    protected List<AllOf> getAllOfList(boolean bNoNull) {
        if (this.allOfs == null && bNoNull) {
            this.allOfs = new ArrayList<AllOf>();
        }
        return this.allOfs;
    }

    protected void clearAllOfList() {
        if (this.allOfs != null) {
            this.allOfs.clear();
        }
    }

    public AnyOf(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public AnyOf(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public AnyOf() {
    }

    public AnyOf(Collection<AllOf> allOfsIn) {
        if (allOfsIn != null) {
            this.addAllOfs(allOfsIn);
        }
    }

    public Iterator<AllOf> getAllOfs() {
        return (this.allOfs == null ? null : this.allOfs.iterator());
    }

    public void setAllOfs(Collection<AllOf> allOfsIn) {
        this.clearAllOfList();
        if (allOfsIn != null) {
            this.addAllOfs(allOfsIn);
        }
    }

    public void addAllOf(AllOf allOf) {
        List<AllOf> listAllOfs = this.getAllOfList(true);
        listAllOfs.add(allOf);
    }

    public void addAllOfs(Collection<AllOf> allOfs) {
        List<AllOf> listAllOfs = this.getAllOfList(true);
        listAllOfs.addAll(allOfs);
    }

    @Override
    public MatchResult match(EvaluationContext evaluationContext) throws EvaluationException {
        if (!this.validate()) {
            return new MatchResult(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }
        Iterator<AllOf> iterAllOfs = this.getAllOfs();
        if (iterAllOfs == null || !iterAllOfs.hasNext()) {
            return MatchResult.MM_NOMATCH;
        }

        /*
         * Assume "No Match" until we find a match or an indeterminate result
         */
        MatchResult matchResultFallThrough = MatchResult.MM_NOMATCH;
        while (iterAllOfs.hasNext()) {
            MatchResult matchResultAllOf = iterAllOfs.next().match(evaluationContext);
            assert matchResultAllOf != null;
            switch (matchResultAllOf.getMatchCode()) {
            case INDETERMINATE:
                /*
                 * Keep the first indeterminate value to return if no other match is found
                 */
                if (matchResultFallThrough.getMatchCode() != MatchResult.MatchCode.INDETERMINATE) {
                    matchResultFallThrough = matchResultAllOf;
                }
                break;
            case MATCH:
                return matchResultAllOf;
            case NOMATCH:
                break;
            }
        }
        return matchResultFallThrough;
    }

    @Override
    protected boolean validateComponent() {
        Iterator<AllOf> iterAllOfs = this.getAllOfs();
        if (iterAllOfs == null || !iterAllOfs.hasNext()) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing AllOf elements in AnyOf");
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

        String iterToDump = StringUtils.toString(this.getAllOfs());
        if (iterToDump != null) {
            stringBuilder.append(",allOfs=");
            stringBuilder.append(iterToDump);
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
