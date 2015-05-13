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

import java.util.List;

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.pdp.eval.Evaluatable;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.EvaluationResult;

/**
 * CombiningAlgorithm is the interface for objects that implement XACML combining algorithms for rules,
 * policies, and policy sets.
 *
 * @param <T> the type of object to be combined
 * @param <U> the type of the identifier for <code>T</code>
 */
public interface CombiningAlgorithm<T extends Evaluatable> {
    /**
     * Gets the {@link org.apache.openaz.xacml.api.Identifier} for this <code>CombiningAlgorithm</code>.
     *
     * @return the <code>Identifier</code> for this <code>CombiningAlgorithm</code>
     */
    Identifier getId();

    /**
     * Evaluates as many of the <code>CombiningElement</code>s supplied with the given
     * <code>CombinerParameter</code>s based on the particular combining algorithm and combines their
     * <code>EvaluationResult</code>s into a single <code>EvaluationResult</code>.
     *
     * @param evaluationContext the <code>EvaluationContext</code> in which to evaluate each of the
     *            <code>CombiningElement</code>s
     * @param elements the <code>List</code> of <code>CombiningElement</code>s to evaluate
     * @param combinerParameters the <code>List</code> of <code>CombinerParameter</code>s to apply to the
     *            combining algorithm
     * @return the combined <code>EvaluationResult</code>
     * @throws org.apache.openaz.xacml.pdp.eval.EvaluationException if there is an error in the
     *             <code>evaluate</code> method of any of the <code>CombiningElement</code>s
     */
    EvaluationResult combine(EvaluationContext evaluationContext, List<CombiningElement<T>> elements,
                                    List<CombinerParameter> combinerParameters) throws EvaluationException;
}
