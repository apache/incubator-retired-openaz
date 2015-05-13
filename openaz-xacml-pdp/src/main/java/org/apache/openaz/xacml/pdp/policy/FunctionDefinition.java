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
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;

/**
 * FunctionDefinition is the interface that objects representing XACML functions found in Match and Apply
 * elements in Policies, PolicySets and Rules.
 */
public interface FunctionDefinition {
    /**
     * Gets the {@link org.apache.openaz.xacml.api.Identifier} for this <code>FunctionDefinition</code>.
     *
     * @return the <code>Identifier</code> for this <code>FunctionDefinition</code>.
     */
    Identifier getId();

    /**
     * Returns the <code>Identifier</code> for the data type returned by this function if
     * <code>returnsBag()</code> is false or if this <code>FunctionDefinition</code> returns a bag containing
     * a single data type. Otherwise it returns null.
     *
     * @return the <code>Identifier</code> for the XACML data type this <code>FunctionDefinition</code>
     *         returns
     */
    Identifier getDataTypeId();

    /**
     * Determines if this <code>FunctionDefinition</code> returns a bag of values or a single value.
     *
     * @return true if this <code>FunctionDefinition</code> returns a bag, else false
     */
    boolean returnsBag();

    /**
     * Evaluates this <code>FunctionDefinition</code> on the given <code>List</code> of
     * {@link FunctionArgument}s.
     *
     * @param evaluationContext the {@link org.apache.openaz.xacml.pdp.eval.EvaluationContext} to use in the
     *            evaluation
     * @param arguments the <code>List</code> of <code>FunctionArgument</code>s for the evaluation
     * @return an {@link ExpressionResult} with the results of the call
     */
    ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments);
}
