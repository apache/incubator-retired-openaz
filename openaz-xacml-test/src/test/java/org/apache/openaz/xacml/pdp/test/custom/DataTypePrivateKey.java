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

import java.security.PrivateKey;

import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.datatypes.DataTypeBase;

public class DataTypePrivateKey extends DataTypeBase<PrivateKey> {
    public static final Identifier DT_PRIVATEKEY = new IdentifierImpl(
                                                                      "urn:com:att:research:xacml:custom:3.0:rsa:private");
    private static final DataTypePrivateKey singleInstance = new DataTypePrivateKey();

    private DataTypePrivateKey() {
        super(DT_PRIVATEKEY, PrivateKey.class);
    }

    public static DataTypePrivateKey newInstance() {
        return singleInstance;
    }

    @Override
    public PrivateKey convert(Object source) throws DataTypeException {
        if (source == null || (source instanceof PrivateKey)) {
            return (PrivateKey)source;
        } else if (source instanceof byte[]) {
            return (PrivateKey)source;
        } else if (source instanceof String) {
            return (PrivateKey)(Object)((String)source).getBytes();
        }
        throw new DataTypeException(this, "Failed to convert \"" + source.getClass().getCanonicalName());
    }

}
