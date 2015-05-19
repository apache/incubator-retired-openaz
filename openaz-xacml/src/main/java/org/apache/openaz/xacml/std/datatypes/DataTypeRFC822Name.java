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

import java.text.ParseException;

import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.XACML1;

/**
 * DataTypeRFC822Name extends {@link org.apache.openaz.xacml.common.datatypes.DataTypeBase> for the XACML
 * rfc822Name data type. *
 */
public class DataTypeRFC822Name extends DataTypeSemanticStringBase<RFC822Name> {
    private static final DataTypeRFC822Name singleInstance = new DataTypeRFC822Name();

    private DataTypeRFC822Name() {
        super(XACML1.ID_DATATYPE_RFC822NAME, RFC822Name.class);
    }

    public static DataTypeRFC822Name newInstance() {
        return singleInstance;
    }

    @Override
    public RFC822Name convert(Object source) throws DataTypeException {
        if (source == null || source instanceof RFC822Name) {
            return (RFC822Name)source;
        } else {
            String stringValue = this.convertToString(source);
            if (stringValue == null) {
                return null;
            }
            RFC822Name rfc822Name = null;
            try {
                rfc822Name = RFC822Name.newInstance(stringValue);
            } catch (ParseException ex) {
                throw new DataTypeException(this, "Failed to convert \""
                                                  + source.getClass().getCanonicalName() + "\" with value \""
                                                  + stringValue + "\" to RFC822Name", ex);
            }
            return rfc822Name;
        }
    }

}
