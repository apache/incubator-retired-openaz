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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML;

/**
 * DataTypeAnyURI extends {@link DataTypeBase} for the XACML anyURI data type.
 */
public class DataTypeAnyURI extends DataTypeBase<URI> {
    private static final DataTypeAnyURI singleInstance = new DataTypeAnyURI();

    /**
     * Creates a <code>DataTypeAnyURI</code> with the XACML anyURI id and the java <code>URI</code> class.
     */
    private DataTypeAnyURI() {
        super(XACML.ID_DATATYPE_ANYURI, URI.class);
    }

    public static DataTypeAnyURI newInstance() {
        return singleInstance;
    }

    @Override
    public URI convert(Object source) throws DataTypeException {
        if (source == null || source instanceof URI) {
            return (URI)source;
        } else if (source instanceof Identifier) {
            return ((Identifier)source).getUri();
        } else {
            String stringValue = this.convertToString(source);
            if (stringValue == null) {
                return null;
            }
            URI uriValue = null;
            try {
                // uriValue = URI.create(stringValue);
                uriValue = new URI(stringValue);
            } catch (URISyntaxException ex) {
                throw new DataTypeException(this, "Failed to convert \""
                                                  + source.getClass().getCanonicalName() + "\" with value \""
                                                  + stringValue + "\" to anyURI", ex);
            }
            return uriValue;
        }
    }

    @Override
    public String toStringValue(URI source) throws DataTypeException {
        return (source == null ? null : source.toString());
    }
}
