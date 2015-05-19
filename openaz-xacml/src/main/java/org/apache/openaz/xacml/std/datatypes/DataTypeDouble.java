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
 * DataTypeDouble implements conversion to the XACML Double data type.
 */
public class DataTypeDouble extends DataTypeBase<Double> {
    private static final DataTypeDouble singleInstance = new DataTypeDouble();

    private DataTypeDouble() {
        super(XACML.ID_DATATYPE_DOUBLE, Double.class);
    }

    public static DataTypeDouble newInstance() {
        return singleInstance;
    }

    @Override
    public Double convert(Object source) throws DataTypeException {
        if (source == null || source instanceof Double) {
            return (Double)source;
        } else {
            String stringValue = this.convertToString(source);
            Double intValue = null;
            try {
                // the XML representation of Infinity is "INF" and "-INF",
                // but the Java Double represenation is "Infinity" and "-Infinity"
                if (stringValue.equals("INF")) {
                    stringValue = "Infinity";
                } else if (stringValue.equals("-INF")) {
                    stringValue = "-Infinity";
                }
                intValue = Double.parseDouble(stringValue);
            } catch (NumberFormatException ex) {
                throw new DataTypeException(this, "Failed to convert from \""
                                                  + source.getClass().getCanonicalName() + "\" with value \""
                                                  + stringValue + "\" to double", ex);
            }
            return intValue;
        }
    }
}
