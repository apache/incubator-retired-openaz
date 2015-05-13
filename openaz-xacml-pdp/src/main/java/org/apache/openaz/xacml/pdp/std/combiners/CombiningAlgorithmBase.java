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

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.pdp.policy.CombiningAlgorithm;

public abstract class CombiningAlgorithmBase<T extends org.apache.openaz.xacml.pdp.eval.Evaluatable> implements 
    CombiningAlgorithm<T> {
    
    private Identifier id;

    public CombiningAlgorithmBase(Identifier identifierIn) {
        this.id = identifierIn;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        Object objectToDump;
        if ((objectToDump = this.getId()) != null) {
            stringBuilder.append("id=");
            stringBuilder.append(objectToDump.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
