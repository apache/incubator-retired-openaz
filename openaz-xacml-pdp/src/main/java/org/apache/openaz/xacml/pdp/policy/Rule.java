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

import org.apache.openaz.xacml.api.Advice;
import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Obligation;
import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.api.trace.Traceable;
import org.apache.openaz.xacml.pdp.eval.Evaluatable;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.EvaluationResult;
import org.apache.openaz.xacml.pdp.eval.MatchResult;
import org.apache.openaz.xacml.pdp.eval.Matchable;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.trace.StdTraceEvent;
import org.apache.openaz.xacml.util.StringUtils;

/**
 * Rule extends {@link org.apache.openaz.xacml.pdp.policy.PolicyComponent} to represent a XACML Rule within
 * a Policy. It implements {@link org.apache.openaz.xacml.pdp.eval.Matchable} and
 * {@link org.apache.openaz.xacml.pdp.eval.Evaluatable} for matching and evaluation a request.
 */
public class Rule extends PolicyComponent implements Matchable, Evaluatable, Traceable {
    private Policy policy;
    private String ruleId;
    private RuleEffect ruleEffect;
    private String description;
    private Target target;
    private Condition condition;
    private List<ObligationExpression> obligationExpressions = new ArrayList<ObligationExpression>();
    private List<AdviceExpression> adviceExpressions = new ArrayList<AdviceExpression>();

    protected List<ObligationExpression> getObligationExpressionList() {
        return this.obligationExpressions;
    }

    protected void clearObligationExpressions() {
        this.getObligationExpressionList().clear();
    }

    protected List<AdviceExpression> getAdviceExpressionList() {
        return this.adviceExpressions;
    }

    protected void clearAdviceExpressions() {
        this.getAdviceExpressionList().clear();
    }

    public Rule(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public Rule(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public Rule() {
    }

    public Policy getPolicy() {
        return this.policy;
    }

    public void setPolicy(Policy policyIn) {
        this.policy = policyIn;
    }

    public String getRuleId() {
        return this.ruleId;
    }

    public void setRuleId(String ruleIdIn) {
        this.ruleId = ruleIdIn;
    }

    public RuleEffect getRuleEffect() {
        return this.ruleEffect;
    }

    public void setRuleEffect(RuleEffect ruleEffectIn) {
        this.ruleEffect = ruleEffectIn;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String descriptionIn) {
        this.description = descriptionIn;
    }

    public Target getTarget() {
        return this.target;
    }

    public void setTarget(Target targetIn) {
        this.target = targetIn;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public void setCondition(Condition conditionIn) {
        this.condition = conditionIn;
    }

    public Iterator<ObligationExpression> getObligationExpressions() {
        return (this.obligationExpressions == null ? null : this.obligationExpressions.iterator());
    }

    public void setObligationExpressions(Collection<ObligationExpression> obligationExpressionsIn) {
        this.clearObligationExpressions();
        if (obligationExpressionsIn != null) {
            this.addObligationExpressions(obligationExpressionsIn);
        }
    }

    public void addObligationExpression(ObligationExpression obligationExpression) {
        this.getObligationExpressionList().add(obligationExpression);
    }

    public void addObligationExpressions(Collection<ObligationExpression> obligationExpressionsIn) {
        this.getObligationExpressionList().addAll(obligationExpressionsIn);
    }

    public Iterator<AdviceExpression> getAdviceExpressions() {
        return (this.adviceExpressions == null ? null : this.adviceExpressions.iterator());
    }

    public void setAdviceExpressions(Collection<AdviceExpression> adviceExpressionsIn) {
        this.clearAdviceExpressions();
        if (adviceExpressionsIn != null) {
            this.addAdviceExpressions(adviceExpressionsIn);
        }
    }

    public void addAdviceExpression(AdviceExpression adviceExpression) {
        this.getAdviceExpressionList().add(adviceExpression);
    }

    public void addAdviceExpressions(Collection<AdviceExpression> adviceExpressionsIn) {
        this.getAdviceExpressionList().addAll(adviceExpressionsIn);
    }

    @Override
    public EvaluationResult evaluate(EvaluationContext evaluationContext) throws EvaluationException {
        if (!this.validate()) {
            return new EvaluationResult(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }

        /*
         * See if our target matches
         */
        MatchResult matchResult = this.match(evaluationContext);
        if (evaluationContext.isTracing()) {
            evaluationContext.trace(new StdTraceEvent<MatchResult>("Match", this, matchResult));
        }
        switch (matchResult.getMatchCode()) {
        case INDETERMINATE:
            return new EvaluationResult(Decision.INDETERMINATE, matchResult.getStatus());
        case MATCH:
            break;
        case NOMATCH:
            return new EvaluationResult(Decision.NOTAPPLICABLE);
        }

        /*
         * See if our condition matches
         */
        Condition thisCondition = this.getCondition();
        if (thisCondition != null) {
            ExpressionResultBoolean expressionResultCondition = thisCondition
                .evaluate(evaluationContext, this.getPolicy().getPolicyDefaults());
            assert expressionResultCondition != null;

            if (evaluationContext.isTracing()) {
                evaluationContext
                    .trace(new StdTraceEvent<ExpressionResultBoolean>("Condition", this,
                                                                      expressionResultCondition));
            }

            if (!expressionResultCondition.isOk()) {
                return new EvaluationResult(Decision.INDETERMINATE, expressionResultCondition.getStatus());
            } else if (!expressionResultCondition.isTrue()) {
                return new EvaluationResult(Decision.NOTAPPLICABLE);
            }
        }

        /*
         * The target and condition match, so we can start creating the EvaluationResult
         */
        List<Obligation> listObligations = ObligationExpression.evaluate(evaluationContext, this.getPolicy()
            .getPolicyDefaults(), this.getRuleEffect().getDecision(), this.getObligationExpressionList());
        List<Advice> listAdvices = AdviceExpression.evaluate(evaluationContext, this.getPolicy()
            .getPolicyDefaults(), this.getRuleEffect().getDecision(), this.getAdviceExpressionList());

        EvaluationResult evaluationResult = new EvaluationResult(this.getRuleEffect().getDecision(),
                                                                 listObligations, listAdvices);
        if (evaluationContext.isTracing()) {
            evaluationContext.trace(new StdTraceEvent<Result>("Result", this, evaluationResult));
        }
        return evaluationResult;
    }

    @Override
    public MatchResult match(EvaluationContext evaluationContext) throws EvaluationException {
        if (!this.validate()) {
            return new MatchResult(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }
        Target thisTarget = this.getTarget();
        if (thisTarget != null) {
            return thisTarget.match(evaluationContext);
        } else {
            return MatchResult.MM_MATCH;
        }
    }

    @Override
    protected boolean validateComponent() {
        if (this.getRuleId() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing rule id");
            return false;
        } else if (this.getPolicy() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, "Rule not in a Policy");
            return false;
        } else if (this.getRuleEffect() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing effect");
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        Object objectToDump;
        if ((objectToDump = this.getRuleId()) != null) {
            stringBuilder.append(",ruleId=");
            stringBuilder.append((String)objectToDump);
        }
        if ((objectToDump = this.getRuleEffect()) != null) {
            stringBuilder.append(",ruleEffect=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getDescription()) != null) {
            stringBuilder.append(",description=");
            stringBuilder.append((String)objectToDump);
        }
        if ((objectToDump = this.getTarget()) != null) {
            stringBuilder.append(",target=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getCondition()) != null) {
            stringBuilder.append(",condition=");
            stringBuilder.append(objectToDump.toString());
        }

        String iterToDump;
        if ((iterToDump = StringUtils.toString(this.getObligationExpressions())) != null) {
            stringBuilder.append(",obligationExpressions=");
            stringBuilder.append(iterToDump);
        }
        if ((iterToDump = StringUtils.toString(this.getAdviceExpressions())) != null) {
            stringBuilder.append(",adviceExpressions=");
            stringBuilder.append(iterToDump);
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    public String getTraceId() {
        return this.getRuleId();
    }

    @Override
    public Traceable getCause() {
        return this.policy;
    }

}
