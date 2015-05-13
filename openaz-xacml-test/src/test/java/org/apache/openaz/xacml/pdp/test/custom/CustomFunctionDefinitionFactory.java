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
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.pdp.test.custom;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.pdp.policy.FunctionDefinition;
import org.apache.openaz.xacml.pdp.policy.FunctionDefinitionFactory;
import org.apache.openaz.xacml.pdp.std.StdFunctions;
import org.apache.openaz.xacml.pdp.std.functions.FunctionDefinitionBagOneAndOnly;
import org.apache.openaz.xacml.std.IdentifierImpl;

public class CustomFunctionDefinitionFactory extends FunctionDefinitionFactory {
    private static Map<Identifier, FunctionDefinition> mapFunctionDefinitions = new HashMap<Identifier, FunctionDefinition>();
    private static boolean needMapInit = true;

    public static final Identifier ID_FUNCTION_PRIVATEKEY_ONE_AND_ONLY = new IdentifierImpl(
                                                                                            "urn:com:att:research:xacml:custom:function:3.0:rsa:privatekey-one-and-only");
    public static final Identifier ID_FUNCTION_PUBLICKEY_ONE_AND_ONLY = new IdentifierImpl(
                                                                                           "urn:com:att:research:xacml:custom:function:3.0:rsa:publickey-one-and-only");

    public static final FunctionDefinition FD_PRIVATEKEY_ONE_AND_ONLY = new FunctionDefinitionBagOneAndOnly<PrivateKey>(
                                                                                                                        ID_FUNCTION_PRIVATEKEY_ONE_AND_ONLY,
                                                                                                                        DataTypePrivateKey
                                                                                                                            .newInstance());
    public static final FunctionDefinition FD_PUBLICKEY_ONE_AND_ONLY = new FunctionDefinitionBagOneAndOnly<PublicKey>(
                                                                                                                      ID_FUNCTION_PUBLICKEY_ONE_AND_ONLY,
                                                                                                                      DataTypePublicKey
                                                                                                                          .newInstance());

    private static void register(FunctionDefinition functionDefinition) {
        mapFunctionDefinitions.put(functionDefinition.getId(), functionDefinition);
    }

    private static void initMap() {
        if (needMapInit) {
            synchronized (mapFunctionDefinitions) {
                if (needMapInit) {
                    needMapInit = false;
                    Field[] declaredFields = StdFunctions.class.getDeclaredFields();
                    for (Field field : declaredFields) {
                        if (Modifier.isStatic(field.getModifiers())
                            && field.getName().startsWith(StdFunctions.FD_PREFIX)
                            && FunctionDefinition.class.isAssignableFrom(field.getType())
                            && Modifier.isPublic(field.getModifiers())) {
                            try {
                                register((FunctionDefinition)(field.get(null)));
                            } catch (IllegalAccessException ex) {

                            }
                        }
                    }
                    //
                    // Our custom function
                    //
                    register(FunctionDefinitionDecrypt.newInstance());
                    register(FD_PRIVATEKEY_ONE_AND_ONLY);
                    register(FD_PUBLICKEY_ONE_AND_ONLY);
                }
            }
        }
    }

    public CustomFunctionDefinitionFactory() {
        initMap();
    }

    @Override
    public FunctionDefinition getFunctionDefinition(Identifier functionId) {
        return mapFunctionDefinitions.get(functionId);
    }

}
