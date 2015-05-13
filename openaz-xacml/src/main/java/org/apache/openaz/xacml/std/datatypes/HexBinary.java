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
import org.apache.commons.codec.binary.Hex;
import org.apache.openaz.xacml.api.SemanticString;
import org.apache.openaz.xacml.util.StringUtils;

/**
 * HexBinary provides utilities for converting the XACML hexBinary data type to and from <code>String</code>
 * values.
 */
public class HexBinary implements SemanticString {
    private byte[] data;

    /**
     * Creates a <code>HexBinary</code> object from an array of <code>byte</code>s.
     *
     * @param dataIn the array of <code>byte</code>s
     */
    public HexBinary(byte[] dataIn) {
        this.data = dataIn;
    }

    /**
     * Creates a new <code>HexBinary</code> by parsing the given <code>String</code> as hex binary data.
     *
     * @param stringHexBinary the <code>String</code> to convert
     * @return a new <code>HexBinary</code> from the converted <code>String</code>.
     */
    public static HexBinary newInstance(String stringHexBinary) throws DecoderException {
        if (stringHexBinary == null) {
            return null;
        }
        byte[] hexBytes = (byte[])new Hex().decode(stringHexBinary);
        return new HexBinary(hexBytes);
    }

    /**
     * Gets the array of <code>byte</code>s for this <code>HexBinary</code>.
     *
     * @return the array of <code>byte</code>s for this <code>HexBinary</code>.
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
        if (obj == null || !(obj instanceof HexBinary)) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            HexBinary hexBinaryObj = (HexBinary)obj;
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        byte[] thisData = this.getData();
        if (thisData != null) {
            stringBuilder.append("data=");
            stringBuilder.append(StringUtils.toString(thisData));
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    public String stringValue() {
        byte[] thisData = this.getData();
        return (thisData == null ? null : Hex.encodeHexString(thisData));
    }
}
