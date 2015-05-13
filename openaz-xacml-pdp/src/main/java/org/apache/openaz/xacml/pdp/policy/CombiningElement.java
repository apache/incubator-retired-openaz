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

import org.apache.openaz.xacml.pdp.eval.Evaluatable;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.EvaluationResult;

/**
 * CombiningElement wraps an {@link org.apache.openaz.xacml.pdp.evl.Evaluatable} with a set of
 * {@link org.apache.openaz.xacml.pdp.policy.TargetedCombinerParameter}s for use with a
 * {@link org.apache.openaz.xacml.pdp.policy.CombiningAlgorithm} to get a combined
 * {@link org.apache.openaz.xacml.pdp.eval.EvaluationResult}
 *
 * @param <T> the java class extending <code>Evaluatable</code> of the objects to be combined
 */
public class CombiningElement<T extends Evaluatable> {
    private T evaluatable;
    private List<CombinerParameter> targetedCombinerParameters;

    /**
     * Creates a new <code>CombiningElement</code> with the given <code>Evaluatable</code> and
     * <code>List</code> of <code>TargetedCombinerParameter</code>.
     *
     * @param evaluatableIn the <code>Evaluatable</code>
     * @param targetedCombinerParametersIn the <code>List</code> of <code>TargetedCombinerParameter</code>s.
     */
    public CombiningElement(T evaluatableIn, Collection<CombinerParameter> targetedCombinerParametersIn) {
        this.evaluatable = evaluatableIn;
        if (targetedCombinerParametersIn != null) {
            this.targetedCombinerParameters = new ArrayList<CombinerParameter>();
            this.targetedCombinerParameters.addAll(targetedCombinerParametersIn);
        }
    }

    /**
     * Gets the <code>Evaluatable</code> for this <code>CombiningElement</code>.
     *
     * @return the <code>Evaluatable</code> for this <code>CombiningElement</code>
     */
    public T getEvaluatable() {
        return this.evaluatable;
    }

    /**
     * Gets an <code>Iterator</code> over the <code>TargetedCombinerParameters</code> for this
     * <code>CombiningElement</code>.
     *
     * @return an <code>Iterator</code> over the <code>TargetedCombinerParameters</code> for this
     *         <code>CombiningElement</code>
     */
    public Iterator<CombinerParameter> getTargetedCombinerParameters() {
        return (this.targetedCombinerParameters == null ? null : this.targetedCombinerParameters.iterator());
    }

    /**
     * Evaluates this <code>CombiningElement</code> in the given
     * {@link org.apache.openaz.xacml.pdp.eval.EvaluationContext}.
     *
     * @param evaluationContext the <code>EvaluationContext</code>
     * @return the {@link org.apache.openaz.xacml.pdp.eval.EvaluationResult} from the
     *         <code>Evaluatable</code>
     * @throws org.apache.openaz.xacml.pdp.eval.EvaluationException if there is an error in the
     *             <code>evaluate</code> method of the <code>Evaluatable</code>
     */
    public EvaluationResult evaluate(EvaluationContext evaluationContext) throws EvaluationException {
        return this.getEvaluatable().evaluate(evaluationContext);
    }

}
