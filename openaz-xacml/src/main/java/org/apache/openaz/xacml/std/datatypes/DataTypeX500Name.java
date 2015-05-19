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
package org.apache.openaz.xacml.std.datatypes;

import javax.security.auth.x500.X500Principal;

import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.XACML1;

/**
 * DataTypeX500Name extends {@link DataTypeBase} to implement the XACML x500Name data type.
 */
public class DataTypeX500Name extends DataTypeBase<X500Principal> {
    private static final DataTypeX500Name singleInstance = new DataTypeX500Name();

    /**
     * Creates a new <code>DataTypeX500Name</code>>
     */
    private DataTypeX500Name() {
        super(XACML1.ID_DATATYPE_X500NAME, X500Principal.class);
    }

    public static DataTypeX500Name newInstance() {
        return singleInstance;
    }

    @Override
    public X500Principal convert(Object source) throws DataTypeException {
        if (source == null || source instanceof X500Principal) {
            return (X500Principal)source;
        } else {
            String stringValue = this.convertToString(source);
            X500Principal x500Principal = null;
            try {
                x500Principal = new X500Principal(stringValue);
            } catch (IllegalArgumentException ex) {
                throw new DataTypeException(this, "Failed to convert \""
                                                  + source.getClass().getCanonicalName() + "\" with value \""
                                                  + stringValue + "\" to X500Name", ex);
            }
            return x500Principal;
        }
    }
}
