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

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.std.datatypes.DataTypes;

/**
 * FunctionDefinitionStringEqualIgnoreCase extends {@link FunctionDefinitionEquality} for
 * <code>String</code> arguments by testing for equality without regard to case.
 *
 * The specification actually says that the strings are first converted to lower case using the string-normalize-to-lower-case function.
 * This code ASSUMES that
 * <UL>
 * <LI> the normalize function just calls the Java toLowerCase() function, and
 * <LI> the Java VM is consistent in that equalsIgnoreCase provides the same result as calling toLowerCase on each string and doing a compare.
 * </UL>
 *
 * In the first implementation of XACML we had separate files for each XACML Function.
 * This release combines multiple Functions in fewer files to minimize code duplication.
 * This file supports the following XACML codes:
 *              string-equal-ignore-case
 *
 *
 */
public class FunctionDefinitionStringEqualIgnoreCase extends FunctionDefinitionEquality<String> {

    /**
     * ASSUMES that equalsIgnoreCase provides the same result as calling string-normalize-to-lower-case on both strings and then comparing.
     */
    @Override
    protected boolean isEqual(String s1, String s2) {
        return s1.equalsIgnoreCase(s2);
    }

    /**
     * Creates a new <code>FunctionDefinitionStringEqualIgnoreCase</code> with the given <code>Identifier</code>.
     *
     * @param idIn the <code>Identifier</code> for the new <code>FunctionDefinitionStringEqualIgnoreCase</code>
     */
    public FunctionDefinitionStringEqualIgnoreCase(Identifier idIn) {
        super(idIn, DataTypes.DT_STRING);
    }

}
