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
import org.apache.openaz.xacml.util.StringUtils;

/**
 * Target extends {@link org.apache.openaz.xacml.pdp.policy.PolicyComponent} to implement XACML 3.0 Target
 * elements for Policies, PolicySets, and Rules.
 */
public class Target extends PolicyComponent implements Matchable {
    private List<AnyOf> anyOfs;

    protected List<AnyOf> getAnyOfList(boolean bNoNull) {
        if (this.anyOfs == null && bNoNull) {
            this.anyOfs = new ArrayList<AnyOf>();
        }
        return this.anyOfs;
    }

    protected void clearAnyOfList() {
        if (this.anyOfs != null) {
            this.anyOfs.clear();
        }
    }

    public Target(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public Target(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public Target() {
    }

    public Target(Collection<AnyOf> anyOfsIn) {
        if (anyOfsIn != null) {
            this.addAnyOfs(anyOfsIn);
        }
    }

    public Target(AnyOf anyOfIn) {
        if (anyOfIn != null) {
            this.addAnyOf(anyOfIn);
        }
    }

    /**
     * Gets an <code>Iterator</code> over all of the {@link AnyOf}s in this <code>Target</code>.
     *
     * @return an <code>Iterator</code> over all of the <code>AnyOf</code>s in this <code>Target</code> or
     *         null if there are none
     */
    public Iterator<AnyOf> getAnyOfs() {
        return (this.anyOfs == null ? null : this.anyOfs.iterator());
    }

    public void setAnyOfs(Collection<AnyOf> anyOfsIn) {
        this.clearAnyOfList();
        if (anyOfsIn != null) {
            this.addAnyOfs(anyOfsIn);
        }
    }

    public void addAnyOf(AnyOf anyOfIn) {
        List<AnyOf> listAnyOfs = this.getAnyOfList(true);
        listAnyOfs.add(anyOfIn);
    }

    public void addAnyOfs(Collection<AnyOf> anyOfsIn) {
        List<AnyOf> listAnyOfs = this.getAnyOfList(true);
        listAnyOfs.addAll(anyOfsIn);
    }

    @Override
    public MatchResult match(EvaluationContext evaluationContext) throws EvaluationException {
        if (!this.validate()) {
            return new MatchResult(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }
        Iterator<AnyOf> iterAnyOfs = this.getAnyOfs();
        if (iterAnyOfs == null || !iterAnyOfs.hasNext()) {
            return MatchResult.MM_MATCH;
        } else {
            MatchResult matchResult = MatchResult.MM_MATCH;
            while (iterAnyOfs.hasNext()) {
                matchResult = iterAnyOfs.next().match(evaluationContext);
                if (matchResult.getMatchCode() != MatchResult.MatchCode.MATCH) {
                    return matchResult;
                }
            }
            return matchResult;
        }
    }

    @Override
    protected boolean validateComponent() {
        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        String iterToDump = StringUtils.toString(this.getAnyOfs());
        if (iterToDump != null) {
            stringBuilder.append(",anyOfs=");
            stringBuilder.append(iterToDump);
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
