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
package org.apache.openaz.xacml.pdp.std.functions;

import java.util.List;

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.apache.openaz.xacml.std.datatypes.RFC822Name;

/**
 * FunctionDefinitionRFC822NameMatch extends {@link org.apache.openaz.xacml.pdp.std.functions.FunctionDefinitionHomogeneousSimple} to
 * implement the XACML RFC822Name match predicate as functions taking one <code>String</code> and one <code>RFC822Name</code> arguments
 * and returning a single <code>Boolean</code> value.
 *
 * In the first implementation of XACML we had separate files for each XACML Function.
 * This release combines multiple Functions in fewer files to minimize code duplication.
 * This file supports the following XACML codes:
 * 		rfc822Name-match
 *
 *
 */
public class FunctionDefinitionRFC822NameMatch extends FunctionDefinitionBase<Boolean, RFC822Name> {


    /**
     * Constructor
     *
     * @param idIn
     * @param dataTypeArgsIn
     * @param op
     */
    public FunctionDefinitionRFC822NameMatch(Identifier idIn) {
        super(idIn, DataTypes.DT_BOOLEAN, DataTypes.DT_RFC822NAME, false);
    }


    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {

        if (arguments == null || arguments.size() != 2) {
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, getShortFunctionId() + " Expected 2 arguments, got " +
                                             ((arguments == null) ? "null" : arguments.size()) ));
        }

        // get the string to search for
        ConvertedArgument<String> stringArgument = new ConvertedArgument<String>(arguments.get(0), DataTypes.DT_STRING, false);
        if ( ! stringArgument.isOk()) {
            Status decoratedStatus = new StdStatus(stringArgument.getStatus().getStatusCode(), stringArgument.getStatus().getStatusMessage() + " at arg index 0"  );
            return ExpressionResult.newError(getFunctionStatus(decoratedStatus));
        }
        String searchTermString = stringArgument.getValue();

        // get the RFC822Name to match with
        ConvertedArgument<RFC822Name> rfc822Argument = new ConvertedArgument<RFC822Name>(arguments.get(1), DataTypes.DT_RFC822NAME, false);
        if ( ! rfc822Argument.isOk()) {
            Status decoratedStatus = new StdStatus(rfc822Argument.getStatus().getStatusCode(), rfc822Argument.getStatus().getStatusMessage() + " at arg index 1"  );
            return ExpressionResult.newError(getFunctionStatus(decoratedStatus));
        }

        RFC822Name rfc822Name = rfc822Argument.getValue();


        /*
         * Now perform the match.
         */

        /*
         * According to the spec the string must be one of the following 3 things:
         * 	- a name with an '@' in it = a full name that must exactly match the whole RFC name (domain part is ignore case)
         * 	- a domain name (without an '@' and not starting with a '.') = must match whole RFC domain name (ignore case)
         * 	- a partial domain name (without an '@') starting with a '.' = the last part of the RFC domain name (ignore case)
         */

        String[] searchTerms = searchTermString.split("@");

        if (searchTerms.length > 2) {
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, getShortFunctionId() +
                                             " String contained more than 1 '@' in '" + searchTermString + "'" ));
        }

        if (searchTerms.length == 2 || searchTermString.endsWith("@")) {
            // this is an exact match
            if (searchTerms[0] == null || searchTerms[0].length() == 0) {
                return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, getShortFunctionId() +
                                                 " String missing local part in '" + searchTermString + "'" ));
            }
            if (searchTerms.length < 2 || searchTerms[1] == null || searchTerms[1].length() == 0) {
                return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, getShortFunctionId() +
                                                 " String missing domain part in '" + searchTermString + "'" ));
            }

            // args are ok, so check both against RFC name
            if (searchTerms[0].equals(rfc822Name.getLocalName()) &&
                    searchTerms[1].toLowerCase().equals(rfc822Name.getCanonicalDomainName())) {
                return ER_TRUE;
            } else {
                return ER_FALSE;
            }
        }

        // we have only a domain name, which may be whole or partial

        // make it match the canonical version
        searchTerms[0] = searchTerms[0].toLowerCase();

        if (searchTerms[0].charAt(0) == '.') {
            // name is partial - must match the end
            if (rfc822Name.getCanonicalDomainName().endsWith(searchTerms[0])) {
                return ER_TRUE;
            } else {
                return ER_FALSE;
            }
        } else {
            // name is whole domain - must match exactly
            if (rfc822Name.getCanonicalDomainName().equals(searchTerms[0])) {
                return ER_TRUE;
            } else {
                return ER_FALSE;
            }
        }

    }

}
