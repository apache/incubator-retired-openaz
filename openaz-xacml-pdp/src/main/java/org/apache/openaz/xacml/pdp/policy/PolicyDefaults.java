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
package org.apache.openaz.xacml.pdp.policy;

import java.net.URI;

import org.apache.openaz.xacml.api.XACML;

/**
 * PolicyDefaults represents the default values associated with a XACML 3.0 Policy or PolicySet that may be
 * overridden or inherited by child Policies or PolicySets.
 */
public class PolicyDefaults {
    private static URI xpathVersionDefault;

    static {
        try {
            xpathVersionDefault = new URI(XACML.XPATHVERSION_2_0);
        } catch (Exception ex) { //NOPMD

        }
    }

    private URI xpathVersion;
    private PolicyDefaults policyDefaultsParent;

    /**
     * Creates a new <code>PolicyDefaults</code> with the given <code>URI</code> for the XPath version and the
     * given <code>PolicyDefaults</code> pointing to the parent.
     *
     * @param xpathVersionIn the <code>URI</code> representing the XPath version for the new
     *            <code>PolicyDefaults</code>
     * @param policyDefaultsParentIn the <code>PolicyDefaults</code> object that is the parent of the new
     *            <code>PolicyDefaults</code>
     */
    public PolicyDefaults(URI xpathVersionIn, PolicyDefaults policyDefaultsParentIn) {
        this.xpathVersion = xpathVersionIn;
        this.policyDefaultsParent = policyDefaultsParentIn;
    }

    /**
     * Gets the parent <code>PolicyDefaults</code> for this <code>PolicyDefaults</code>.
     *
     * @return the parent <code>PolicyDefaults</code> for this <code>PolicyDefaults</code> or null if none
     */
    public PolicyDefaults getPolicyDefaultsParent() {
        return this.policyDefaultsParent;
    }

    /**
     * Gets the XPath version <code>URI</code> for this <code>PolicyDefaults</code>. If there is no explicit
     * version in this <code>PolicyDefaults</code>, walk up the parent <code>PolicyDefaults</code> hierarchy
     * until one is found, or return the default value.
     *
     * @return the <code>URI</code> for the XPath version
     */
    public URI getXPathVersion() {
        /*
         * See if the XPath version was explicitly set here
         */
        if (this.xpathVersion != null) {
            return this.xpathVersion;
        }

        /*
         * Try the parent hierarchy if there is one
         */
        PolicyDefaults policyDefaultsParentThis = this.getPolicyDefaultsParent();
        if (policyDefaultsParentThis != null) {
            return policyDefaultsParentThis.getXPathVersion();
        }

        /*
         * Use the default
         */
        return xpathVersionDefault;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        boolean needsComma = false;
        Object objectToDump;
        if ((objectToDump = this.xpathVersion) != null) {
            stringBuilder.append("xpathVersion=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }
        if ((objectToDump = this.getPolicyDefaultsParent()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("policyDefaultsParent=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
