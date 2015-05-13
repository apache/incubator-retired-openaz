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
package org.apache.openaz.xacml.pdp.std.combiners;

import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.EvaluationResult;
import org.apache.openaz.xacml.pdp.policy.CombinerParameter;
import org.apache.openaz.xacml.pdp.policy.CombiningElement;
import org.apache.openaz.xacml.pdp.policy.Rule;
import org.apache.openaz.xacml.pdp.policy.RuleEffect;

/**
 * DenyOverrides implements the XACML 1.0 "deny-overrides" combining algorithm for rules.
 *
 * @param <T> the java class for the {@link org.apache.openaz.xacml.pdp.eval.Evaluatable}
 * @param <U> the java class for the identifier
 */
public class LegacyDenyOverridesRule extends CombiningAlgorithmBase<Rule> {

    public LegacyDenyOverridesRule(Identifier identifierIn) {
        super(identifierIn);
    }

    @Override
    public EvaluationResult combine(EvaluationContext evaluationContext,
                                    List<CombiningElement<Rule>> elements,
                                    List<CombinerParameter> combinerParameters) throws EvaluationException {
        boolean atLeastOnePermit = false;
        boolean potentialDeny = false;

        EvaluationResult combinedResult = new EvaluationResult(Decision.PERMIT);
        EvaluationResult evaluationResultIndeterminate = null;

        Iterator<CombiningElement<Rule>> iterElements = elements.iterator();
        while (iterElements.hasNext()) {
            CombiningElement<Rule> combiningElement = iterElements.next();
            EvaluationResult evaluationResultElement = combiningElement.evaluate(evaluationContext);

            assert evaluationResultElement != null;
            switch (evaluationResultElement.getDecision()) {
            case DENY:
                return evaluationResultElement;
            case INDETERMINATE:
            case INDETERMINATE_DENYPERMIT:
            case INDETERMINATE_DENY:
            case INDETERMINATE_PERMIT:
                if (evaluationResultIndeterminate == null) {
                    evaluationResultIndeterminate = evaluationResultElement;
                } else {
                    evaluationResultIndeterminate.merge(evaluationResultElement);
                }
                if (combiningElement.getEvaluatable().getRuleEffect() == RuleEffect.DENY) {
                    potentialDeny = true;
                }
            case NOTAPPLICABLE:
                break;
            case PERMIT:
                atLeastOnePermit = true;
                combinedResult.merge(evaluationResultElement);
                break;
            default:
                throw new EvaluationException("Illegal Decision: \""
                                              + evaluationResultElement.getDecision().toString());
            }
        }

        if (potentialDeny) {
            return evaluationResultIndeterminate;
        } else if (atLeastOnePermit) {
            return combinedResult;
        } else if (evaluationResultIndeterminate != null) {
            return evaluationResultIndeterminate;
        } else {
            return new EvaluationResult(Decision.NOTAPPLICABLE);
        }
    }

}
