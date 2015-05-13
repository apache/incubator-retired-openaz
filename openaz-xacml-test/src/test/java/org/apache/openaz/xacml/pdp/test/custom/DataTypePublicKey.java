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

import java.security.PublicKey;

import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.datatypes.DataTypeBase;

public class DataTypePublicKey extends DataTypeBase<PublicKey> {
    public static final Identifier DT_PUBLICKEY = new IdentifierImpl(
                                                                     "urn:com:att:research:xacml:custom:3.0:rsa:public");
    private static final DataTypePublicKey singleInstance = new DataTypePublicKey();

    public DataTypePublicKey() {
        super(DT_PUBLICKEY, PublicKey.class);
    }

    public static DataTypePublicKey newInstance() {
        return singleInstance;
    }

    @Override
    public PublicKey convert(Object source) throws DataTypeException {
        if (source == null || (source instanceof PublicKey)) {
            return (PublicKey)source;
        } else if (source instanceof byte[]) {
            return (PublicKey)source;
        } else if (source instanceof String) {
            return (PublicKey)(Object)((String)source).getBytes();
        }
        throw new DataTypeException(this, "Failed to convert \"" + source.getClass().getCanonicalName());
    }

}
