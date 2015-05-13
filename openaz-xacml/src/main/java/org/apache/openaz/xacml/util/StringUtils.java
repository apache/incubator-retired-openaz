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
package org.apache.openaz.xacml.util;

import java.util.Iterator;

/**
 * StringUtils provides utilities for converting various general classes of objects to strings.
 */
public class StringUtils {

    protected StringUtils() {
    }

    /**
     * Generates a <code>String</code> value from the given <code>Iterator</code> of the form: '['
     * object.toString() ',' object.toString() ',' ... ']'
     *
     * @param iterObjects the <code>Iterator</code> to dump into a <code>String</code>
     * @param allowEmptyLists if true, show empty lists as "[]" otherwise return null
     * @return the <code>String</code> representation of the <code>Iterator</code> or null if the
     *         <code>Iterator</code> is null (or empty if allowEmptyLists is false)
     */
    public static String toString(Iterator<?> iterObjects, boolean allowEmptyLists) {
        if (iterObjects == null || !allowEmptyLists && !iterObjects.hasNext()) {
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder("[");
            if (iterObjects.hasNext()) {
                stringBuilder.append(iterObjects.next().toString());
                while (iterObjects.hasNext()) {
                    stringBuilder.append(',');
                    stringBuilder.append(iterObjects.next().toString());
                }
            }
            stringBuilder.append(']');
            return stringBuilder.toString();
        }
    }

    public static String toString(short[] a) {
        StringBuilder stringBuilder = new StringBuilder("[");
        if (a != null && a.length > 0) {
            stringBuilder.append(a[0]);
            for (int i = 1; i < a.length; i++) {
                stringBuilder.append(',');
                stringBuilder.append(a[i]);
            }
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    public static String toString(byte[] a) {
        StringBuilder stringBuilder = new StringBuilder("[");
        if (a != null && a.length > 0) {
            stringBuilder.append(a[0]);
            for (int i = 1; i < a.length; i++) {
                stringBuilder.append(',');
                stringBuilder.append(a[i]);
            }
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    public static String toString(Iterator<?> iterObjects) {
        return toString(iterObjects, false);
    }

    protected static void indent(StringBuilder stringBuilder, int spaces) {
        for (int i = 0; i < spaces; i++) {
            stringBuilder.append(' ');
        }
    }

    public static String prettyPrint(String string, int indentSize) {
        if (string == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int stringLen = string.length();
        int curIndent = 0;
        boolean bstart = false;
        for (int i = 0; i < stringLen;) {
            char charAt = string.charAt(i);
            switch (charAt) {
            case '{':
                if (i + 1 < stringLen) {
                    if (string.charAt(i + 1) == '}') {
                        stringBuilder.append("{}");
                        i += 2;
                    } else {
                        if (i > 0) {
                            stringBuilder.append('\n');
                            curIndent++;
                            indent(stringBuilder, curIndent * indentSize);
                            stringBuilder.append(charAt);
                        } else {
                            stringBuilder.append(charAt);
                        }
                        i++;
                    }
                } else {
                    stringBuilder.append(charAt);
                    i++;
                }
                bstart = true;
                break;
            case '}':
                stringBuilder.append('\n');
                indent(stringBuilder, curIndent * indentSize);
                stringBuilder.append('}');
                if (curIndent > 0) {
                    curIndent--;
                }
                i++;
                bstart = true;
                break;
            case '[':
                if (i + 1 < stringLen) {
                    if (string.charAt(i + 1) == ']') {
                        stringBuilder.append("[]");
                        i += 2;
                    } else {
                        if (i > 0) {
                            stringBuilder.append('\n');
                            curIndent++;
                            indent(stringBuilder, curIndent * indentSize);
                            stringBuilder.append(charAt);
                        } else {
                            stringBuilder.append(charAt);
                        }
                        i++;
                    }
                } else {
                    stringBuilder.append(charAt);
                    i++;
                }
                bstart = true;
                break;
            case ']':
                stringBuilder.append('\n');
                indent(stringBuilder, curIndent * indentSize);
                stringBuilder.append(']');
                if (curIndent > 0) {
                    curIndent--;
                }
                i++;
                bstart = true;
                break;
            case ',':
                stringBuilder.append(charAt);
                i++;
                break;
            case '\n':
                stringBuilder.append(charAt);
                indent(stringBuilder, curIndent * indentSize);
                i++;
                break;
            default:
                if (bstart) {
                    stringBuilder.append('\n');
                    indent(stringBuilder, (curIndent + 1) * indentSize);
                    bstart = false;
                }
                stringBuilder.append(charAt);
                i++;
                break;
            }
        }
        return stringBuilder.toString();
    }

    public static String prettyPrint(String string) {
        return prettyPrint(string, 3);
    }
}
