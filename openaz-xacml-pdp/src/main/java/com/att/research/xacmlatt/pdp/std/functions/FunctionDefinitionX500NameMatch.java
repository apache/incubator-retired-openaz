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
package com.att.research.xacmlatt.pdp.std.functions;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.Status;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;

/**
 * FunctionDefinitionX500NameMatch extends {@link com.att.research.xacmlatt.pdp.std.functions.FunctionDefinitionHomogeneousSimple} to
 * implement the XACML X500Name match predicate as functions taking two <code>X500Name</code> arguments and returning a single <code>Boolean</code> value.
 *
 * In the first implementation of XACML we had separate files for each XACML Function.
 * This release combines multiple Functions in fewer files to minimize code duplication.
 * This file supports the following XACML codes:
 * 		x500Name-match
 *
 *
 */
public class FunctionDefinitionX500NameMatch extends FunctionDefinitionHomogeneousSimple<Boolean, X500Principal> {


    /**
     * Constructor
     *
     * @param idIn
     * @param dataTypeArgsIn
     * @param op
     */
    public FunctionDefinitionX500NameMatch(Identifier idIn) {
        super(idIn, DataTypes.DT_BOOLEAN, DataTypes.DT_X500NAME, 2);
    }


    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {
        List<X500Principal> convertedArguments	= new ArrayList<X500Principal>();
        Status status				= this.validateArguments(arguments, convertedArguments);

        /*
         * If the function arguments are not correct, just return an error status immediately
         */
        if (!status.getStatusCode().equals(StdStatusCode.STATUS_CODE_OK)) {
            return ExpressionResult.newError(getFunctionStatus(status));
        }

        /*
         * Now perform the match.
         */

        /*
         * The spec writer's comments at:
         * https://lists.oasis-open.org/archives/xacml/200906/msg00019.html
         * say that the first sequence must exactly match the END of the second sequence.
         */

        String[] searchFor = convertedArguments.get(0).getName().split(",");
        String[] searchIn = convertedArguments.get(1).getName().split(",");

        // if first is bigger than 2nd there is no way we can match
        if (searchFor.length > searchIn.length) {
            return ER_FALSE;
        }

        // start from back-end of both lists - everything should match up to the length of the input
        for (int i = 0; i < searchFor.length; i++) {
            String searchForTerm = searchFor[searchFor.length - i - 1];
            String searchInTerm = searchIn[searchIn.length - i - 1];
            if (searchForTerm == null || searchInTerm == null ||
                    ! searchForTerm.trim().equals(searchInTerm.trim())) {
                return ER_FALSE;
            }
        }


        return ER_TRUE;
    }

}
