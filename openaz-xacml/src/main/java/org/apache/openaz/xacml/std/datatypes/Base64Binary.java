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

import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.openaz.xacml.api.SemanticString;
import org.apache.openaz.xacml.util.StringUtils;

/**
 * Base64Binary provides utilities for converting the XACML base64Binary data type to and from
 * <code>String</code> values.
 */
public class Base64Binary implements SemanticString {
    private byte[] data;

    /**
     * Creates a <code>Base64Binary</code> object from an array of <code>byte</code>s.
     *
     * @param dataIn the array of <code>byte</code>s
     */
    public Base64Binary(byte[] dataIn) {
        this.data = dataIn;
    }

    /**
     * Creates a new <code>Base64Binary</code> by parsing the given <code>String</code> as hex binary data.
     *
     * @param stringBase64Binary the <code>String</code> to convert
     * @return a new <code>Base64Binary</code> from the converted <code>String</code>.
     */
    public static Base64Binary newInstance(String stringBase64Binary) throws DecoderException {
        if (stringBase64Binary == null) {
            return null;
        }
        byte[] base64Bytes = new Base64().decode(stringBase64Binary);
        return new Base64Binary(base64Bytes);
    }

    /**
     * Gets the array of <code>byte</code>s for this <code>Base64Binary</code>.
     *
     * @return the array of <code>byte</code>s for this <code>Base64Binary</code>.
     */
    public byte[] getData() {
        return this.data;
    }

    @Override
    public int hashCode() {
        return (this.getData() == null ? 0 : this.getData().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Base64Binary)) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            Base64Binary hexBinaryObj = (Base64Binary)obj;
            if (this.getData() == null) {
                if (hexBinaryObj.getData() == null) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (hexBinaryObj.getData() == null) {
                    return false;
                } else {
                    return Arrays.equals(this.getData(), hexBinaryObj.getData());
                }
            }
        }
    }

    /**
     * Gets the <code>String</code> Base 64 binary representation of this <code>Base64Binary</code> object.
     *
     * @return the <code>String</code> Base 64 binary representation of this <code>Base64Binary</code> object.
     */
    @Override
    public String stringValue() {
        if (this.getData() == null) {
            return null;
        } else {
            return Base64.encodeBase64String(this.getData());
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        byte[] thisData = this.getData();
        if (thisData != null) {
            stringBuilder.append("data=");
            stringBuilder.append(StringUtils.toString(thisData));
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
