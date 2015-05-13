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

import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;

/**
 * Expression extends {@link org.apache.openaz.xacml.pdp.policy.PolicyComponent} to represent a XACML
 * ExpressionType element.
 */
public abstract class Expression extends PolicyComponent {

    public Expression(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public Expression(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public Expression() {
    }

    /**
     * Evaluates this <code>Expression</code> in the given
     * {@link org.apache.openaz.xacml.pdp.eval.EvaluationContext}.
     *
     * @param evaluationContext the <code>EvaluationContext</code> in which to evaluate this
     *            <code>Expression</code>
     * @param policyDefaults the {@link org.apache.openaz.xacml.pdp.policy.PolicyDefaults} to use in evaluating
     *            this <code>Expression</code>
     * @return a {@link org.apache.openaz.xacml.pdp.policy.ExpressionResult}
     */
    public abstract ExpressionResult evaluate(EvaluationContext evaluationContext,
                                              PolicyDefaults policyDefaults) throws EvaluationException;
}
