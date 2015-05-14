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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;
import org.apache.openaz.xacml.pdp.policy.FunctionDefinition;
import org.apache.openaz.xacml.pdp.std.functions.ConvertedArgument;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.datatypes.DataTypeHexBinary;
import org.apache.openaz.xacml.std.datatypes.DataTypeString;
import org.apache.openaz.xacml.std.datatypes.HexBinary;

public class FunctionDefinitionDecrypt implements FunctionDefinition {
    public static final Identifier FD_RSA_DECRYPT = new IdentifierImpl(
                                                                       "urn:com:att:research:xacml:custom:function:3.0:rsa:decrypt");
    private static final FunctionDefinitionDecrypt singleInstance = new FunctionDefinitionDecrypt();

    public static FunctionDefinitionDecrypt newInstance() {
        return singleInstance;
    }

    @Override
    public Identifier getId() {
        return FD_RSA_DECRYPT;
    }

    @Override
    public Identifier getDataTypeId() {
        return XACML3.ID_DATATYPE_STRING;
    }

    @Override
    public boolean returnsBag() {
        return false;
    }

    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, List<FunctionArgument> arguments) {
        if (arguments == null || arguments.size() < 2) {
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                           "Decrypt failed, expecting 2 arguments."));
        }
        //
        // What is the first argument?
        //
        FunctionArgument arg0 = arguments.get(0);
        if (arg0.isBag()) {
            //
            // We don't support bags right now
            //
            return ExpressionResult
                .newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                        "Decrypt failed, not expecting a bag for argument 0."));
        }
        if (!arg0.getValue().getDataTypeId().equals(XACML3.ID_DATATYPE_HEXBINARY)) {
            //
            // Should be a String
            //
            return ExpressionResult
                .newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                        "Decrypt failed, expected a Hex Binary for argument 0."));
        }
        //
        // Convert the argument
        //
        ConvertedArgument<HexBinary> data = new ConvertedArgument<HexBinary>(arg0,
                                                                             DataTypeHexBinary.newInstance(),
                                                                             false);
        if (!data.isOk()) {
            return ExpressionResult
                .newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                        "Decrypt failed, argument 0 failed to convert to Hex Binary."));
        }
        //
        // Ok - check the 2nd argument
        //
        FunctionArgument arg1 = arguments.get(1);
        if (arg1.isBag()) {
            //
            // We don't support bags right now
            //
            return ExpressionResult
                .newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                        "Decrypt failed, not expecting a bag for argument 1."));
        }
        if (arg1.getValue().getDataTypeId().equals(DataTypePrivateKey.DT_PRIVATEKEY)
            || arg1.getValue().getDataTypeId().equals(DataTypePublicKey.DT_PUBLICKEY)) {
            //
            // Ok - let's try to decrypt
            //
            Cipher cipher;
            try {
                cipher = Cipher.getInstance("RSA");
                if (arg1.getValue().getDataTypeId().equals(DataTypePrivateKey.DT_PRIVATEKEY)) {
                    //
                    // Using the private key
                    //
                    DataType<PrivateKey> pkDatatype = DataTypePrivateKey.newInstance();
                    ConvertedArgument<PrivateKey> privateKey = new ConvertedArgument<PrivateKey>(arg1,
                                                                                                 pkDatatype,
                                                                                                 false);
                    if (!privateKey.isOk()) {
                        return ExpressionResult
                            .newError(new StdStatus(privateKey.getStatus().getStatusCode(),
                                                    "Decrypt: " + privateKey.getStatus().getStatusMessage()));
                    }
                    //
                    // Setup decryption
                    //
                    cipher.init(Cipher.DECRYPT_MODE, privateKey.getValue());
                } else if (arg1.getValue().getDataTypeId().equals(DataTypePublicKey.DT_PUBLICKEY)) {
                    //
                    // Using the private key
                    //
                    DataType<PublicKey> pkDatatype = DataTypePublicKey.newInstance();
                    ConvertedArgument<PublicKey> publicKey = new ConvertedArgument<PublicKey>(arg1,
                                                                                              pkDatatype,
                                                                                              false);
                    if (!publicKey.isOk()) {
                        return ExpressionResult.newError(new StdStatus(publicKey.getStatus().getStatusCode(),
                                                                       "Decrypt: "
                                                                           + publicKey.getStatus()
                                                                               .getStatusMessage()));
                    }
                    //
                    // Setup decryption
                    //
                    cipher.init(Cipher.DECRYPT_MODE, publicKey.getValue());
                }
                //
                // Do the decryption
                //
                byte[] decryptedData = cipher.doFinal(data.getValue().getData());
                String decryptedString = new String(decryptedData);
                //
                // All good, return the decrypted string
                //
                return ExpressionResult.newSingle(DataTypeString.newInstance()
                    .createAttributeValue(decryptedString));
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException | DataTypeException e) {
                return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                               "Decrypt failed: " + e.getLocalizedMessage()));
            }
        }
        return ExpressionResult
            .newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                    "Decrypt failed, expecting public/private key datatype for argument 1."));
    }

}
