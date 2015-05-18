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
package org.apache.openaz.xacml.std;

import java.text.ParseException;

import org.apache.openaz.xacml.api.Version;
import org.apache.openaz.xacml.api.VersionMatch;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * StdVersionMatch implements {@link org.apache.openaz.xacml.api.VersionMatch} as an array of integers.
 */
public class StdVersionMatch implements VersionMatch {
    private int[] matchComponents;
    private String cachedStringRep;

    private static void addComponent(StringBuilder stringBuilder, int component) {
        if (component == -2) {
            stringBuilder.append('+');
        } else if (component == -1) {
            stringBuilder.append('*');
        } else {
            stringBuilder.append(component);
        }
    }

    public StdVersionMatch(int[] matchComponentsIn) {
        this.matchComponents = matchComponentsIn;
    }

    public static StdVersionMatch newInstance(String versionMatch) throws ParseException {
        if (versionMatch == null) {
            throw new NullPointerException("Null version string");
        }
        String[] versionMatchParts = versionMatch.split("[.]", -1);
        if (versionMatchParts == null) {
            throw new ParseException("Invalid version string \"" + versionMatch + "\"", 0);
        }
        int[] versionMatchNumberParts = new int[versionMatchParts.length];
        for (int i = 0; i < versionMatchParts.length; i++) {
            if (versionMatchParts[i].equals("*")) {
                versionMatchNumberParts[i] = -1;
            } else if (versionMatchParts[i].equals("+")) {
                versionMatchNumberParts[i] = -2;
            } else {
                try {
                    versionMatchNumberParts[i] = Integer.parseInt(versionMatchParts[i]);
                } catch (NumberFormatException ex) {
                    throw new ParseException("Invalid version number \"" + versionMatchParts[i] + "\"", i);
                }
            }
        }
        return new StdVersionMatch(versionMatchNumberParts);

    }

    @Override
    public String getVersionMatch() {
        if (this.cachedStringRep == null) {
            StringBuilder stringBuilder = new StringBuilder();
            int[] matchComponentsHere = this.getMatchComponents();
            if (matchComponentsHere != null && matchComponentsHere.length > 0) {
                addComponent(stringBuilder, matchComponents[0]);
                for (int i = 1; i < matchComponents.length; i++) {
                    stringBuilder.append('.');
                    addComponent(stringBuilder, matchComponents[i]);
                }
            }
            this.cachedStringRep = stringBuilder.toString();
        }
        return this.cachedStringRep;
    }

    public int[] getMatchComponents() {
        return this.matchComponents;
    }

    @Override
    public boolean match(Version version, int cmp) {
        int[] matchComponentsHere = this.getMatchComponents();
        if (matchComponentsHere == null || matchComponentsHere.length == 0) {
            return false;
        }
        int[] versionComponents = version.getVersionDigits();
        if (versionComponents == null || versionComponents.length == 0) {
            return false;
        }
        int iMatch = 0, iVersion = 0;
        int matchValue;
        while (iMatch < matchComponents.length && iVersion < versionComponents.length) {
            if ((matchValue = matchComponentsHere[iMatch]) == -2) {
                iVersion = versionComponents.length;
            } else if (matchValue == -1) {
                iVersion++;
            } else {
                int versionValue = versionComponents[iVersion];
                if (cmp == 0 && versionValue == matchValue
                    || cmp < 0 && versionValue <= matchValue
                    || cmp > 0 && versionValue >= matchValue) {
                    iVersion++;
                } else {
                    return false;
                }
            }
            iMatch++;
        }
        return iVersion == versionComponents.length && iMatch == matchComponents.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof VersionMatch)) {
            return false;
        } else {
            VersionMatch objVersionMatch = (VersionMatch)obj;
            return ObjUtil.equalsAllowNull(this.getVersionMatch(), objVersionMatch.getVersionMatch());
        }
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (getVersionMatch() != null) {
            result = 31 * result + getVersionMatch().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return this.getVersionMatch();
    }

}
