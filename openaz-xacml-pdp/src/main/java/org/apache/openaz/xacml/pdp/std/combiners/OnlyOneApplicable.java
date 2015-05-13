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
import org.apache.openaz.xacml.pdp.eval.MatchResult;
import org.apache.openaz.xacml.pdp.policy.CombinerParameter;
import org.apache.openaz.xacml.pdp.policy.CombiningElement;
import org.apache.openaz.xacml.pdp.policy.PolicySetChild;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * OnlyOneApplicable extends {@link org.apache.openaz.xacml.pdp.std.combiners.CombiningAlgorithmBase} to
 * implement the XACML 1.0 "only-one-applicable" combining algorithm for policies and rules.
 *
 * @param <T> the java class of the object to be combined
 */
public class OnlyOneApplicable extends CombiningAlgorithmBase<PolicySetChild> {

    public OnlyOneApplicable(Identifier identifierIn) {
        super(identifierIn);
    }

    @Override
    public EvaluationResult combine(EvaluationContext evaluationContext,
                                    List<CombiningElement<PolicySetChild>> elements,
                                    List<CombinerParameter> combinerParameters) throws EvaluationException {
        Iterator<CombiningElement<PolicySetChild>> iterElements = elements.iterator();
        PolicySetChild policySetChildApplicable = null;
        while (iterElements.hasNext()) {
            CombiningElement<PolicySetChild> combiningElement = iterElements.next();
            MatchResult matchResultElement = combiningElement.getEvaluatable().match(evaluationContext);

            switch (matchResultElement.getMatchCode()) {
            case INDETERMINATE:
                return new EvaluationResult(Decision.INDETERMINATE, matchResultElement.getStatus());
            case MATCH:
                if (policySetChildApplicable == null) {
                    policySetChildApplicable = combiningElement.getEvaluatable();
                } else {
                    return new EvaluationResult(Decision.INDETERMINATE,
                                                new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                              "More than one applicable policy"));
                }
                break;
            case NOMATCH:
                break;
            default:
                throw new EvaluationException("Illegal Decision: \""
                                              + matchResultElement.getMatchCode().toString());
            }
        }

        if (policySetChildApplicable != null) {
            return policySetChildApplicable.evaluate(evaluationContext);
        } else {
            return new EvaluationResult(Decision.NOTAPPLICABLE);
        }
    }

}
