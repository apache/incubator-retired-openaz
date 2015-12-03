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


package org.apache.openaz.xacml.pdp.policy;

import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.IdReference;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.XACML1;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.EvaluationResult;
import org.apache.openaz.xacml.pdp.eval.MatchResult;
import org.apache.openaz.xacml.pdp.std.combiners.CombiningAlgorithmBase;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PolicySetTest extends PolicyTest {

    @Override
    protected PolicyDef createPolicy() {
        PolicySet policySet = new PolicySet(StdStatusCode.STATUS_CODE_OK);
        policySet.addChild(super.createPolicy());
        policySet.setTarget(createTarget());
        CombiningAlgorithmBase<PolicySetChild> ruleCombiningAlgorithm = new CombiningAlgorithmBase<PolicySetChild>(XACML1.ID_RULE_COMBINING_ALGORITHM) {
            @Override
            public EvaluationResult combine(EvaluationContext evaluationContext, List<CombiningElement<PolicySetChild>> combiningElements, List<CombinerParameter> combinerParameters) throws EvaluationException {
                return new EvaluationResult(Decision.INDETERMINATE);
            }
        };
        policySet.setPolicyCombiningAlgorithm(ruleCombiningAlgorithm);
        return policySet;
    }

    @Override
    protected Collection<IdReference> getPolicyIdentifiers(EvaluationResult evaluationResult) {
        return evaluationResult.getPolicySetIdentifiers();
    }
}
