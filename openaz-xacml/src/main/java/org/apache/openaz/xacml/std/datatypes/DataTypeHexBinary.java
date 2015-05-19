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

import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.XACML;

/**
 * DataTypeHexBinary extends {@link DataTypeBase} to implement the XACML hexBinary data type.
 */
public class DataTypeHexBinary extends DataTypeSemanticStringBase<HexBinary> {
    private static final DataTypeHexBinary singleInstance = new DataTypeHexBinary();

    private DataTypeHexBinary() {
        super(XACML.ID_DATATYPE_HEXBINARY, HexBinary.class);
    }

    public static DataTypeHexBinary newInstance() {
        return singleInstance;
    }

    @Override
    public HexBinary convert(Object source) throws DataTypeException {
        if (source == null || source instanceof HexBinary) {
            return (HexBinary)source;
        } else if (source instanceof byte[]) {
            return new HexBinary((byte[])source);
        } else {
            String stringValue = this.convertToString(source);
            if (stringValue == null) {
                return null;
            }
            HexBinary hexBinary = null;
            try {
                hexBinary = HexBinary.newInstance(stringValue);
            } catch (Exception ex) {
                throw new DataTypeException(this, "Failed to convert \""
                                                  + source.getClass().getCanonicalName() + "\" with value \""
                                                  + stringValue + "\" to HexBinary", ex);
            }
            return hexBinary;
        }
    }

}
