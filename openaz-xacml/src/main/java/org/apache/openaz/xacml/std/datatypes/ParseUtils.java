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

/**
 * ParseUtils provides a number of static methods that are useful in parsing <code>String</code> objects into
 * other java data types.
 */
public class ParseUtils {
    protected ParseUtils() {
    }

    public static class ParseValue<T> {
        private T value;
        private int nextPos;

        public ParseValue(T v, int n) {
            this.value = v;
            this.nextPos = n;
        }

        public T getValue() {
            return this.value;
        }

        public int getNextPos() {
            return this.nextPos;
        }
    }

    static int getTwoDigitValue(String fromString, int startPos) {
        if (fromString.length() <= (startPos + 1)) {
            return -1;
        } else if (!Character.isDigit(fromString.charAt(startPos))
                   || !Character.isDigit(fromString.charAt(startPos + 1))) {
            return -1;
        } else {
            return 10 * Character.digit(fromString.charAt(startPos), 10)
                   + Character.digit(fromString.charAt(startPos + 1), 10);
        }
    }

    static int getThreeDigitValue(String fromString, int startPos) {
        if (fromString.length() <= (startPos + 2)) {
            return -1;
        } else if (!Character.isDigit(fromString.charAt(startPos))
                   || !Character.isDigit(fromString.charAt(startPos + 1))
                   || !Character.isDigit(fromString.charAt(startPos + 2))) {
            return -1;
        } else {
            return 100 * Character.digit(fromString.charAt(startPos), 10) + 10
                   * Character.digit(fromString.charAt(startPos + 1), 10)
                   + Character.digit(fromString.charAt(startPos + 2), 10);
        }
    }

    static int getFourDigitValue(String fromString, int startPos) {
        if (fromString.length() <= (startPos + 3)) {
            return -1;
        } else if (!Character.isDigit(fromString.charAt(startPos))
                   || !Character.isDigit(fromString.charAt(startPos + 1))
                   || !Character.isDigit(fromString.charAt(startPos + 2))
                   || !Character.isDigit(fromString.charAt(startPos + 3))) {
            return -1;
        } else {
            return 1000 * Character.digit(fromString.charAt(startPos), 10) + 100
                   * Character.digit(fromString.charAt(startPos + 1), 10) + 10
                   * Character.digit(fromString.charAt(startPos + 2), 10)
                   + Character.digit(fromString.charAt(startPos + 3), 10);
        }
    }

    static ParseValue<Integer> getSignedValue(String fromString, int startPos) {
        int sign = 1;
        int value = 0;
        int i = startPos;
        if (i >= fromString.length()) {
            return null;
        }
        if (fromString.charAt(i) == '-') {
            sign = -1;
            i++;
        }
        if (i >= fromString.length() || !Character.isDigit(fromString.charAt(i))) {
            return null;
        }
        char charAt;
        while (i < fromString.length() && Character.isDigit((charAt = fromString.charAt(i)))) {
            value = value * 10 + Character.digit(charAt, 10);
            i++;
        }
        return new ParseValue<Integer>(sign * value, i);
    }

    static int nextNonWhite(String fromString, int startPos) {
        while (startPos < fromString.length()) {
            if (!Character.isWhitespace(fromString.charAt(startPos))) {
                return startPos;
            } else {
                startPos++;
            }
        }
        return -1;
    }

}
