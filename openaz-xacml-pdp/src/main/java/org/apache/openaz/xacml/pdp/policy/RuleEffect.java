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

import org.apache.openaz.xacml.api.Decision;

/**
 * RuleEffect is an enumeration of the XACML decision effects that a {@link Rule} may apply to.
 */
public enum RuleEffect {
    DENY("Deny", Decision.DENY),
    PERMIT("Permit", Decision.PERMIT);

    private String name;
    private Decision decision;

    private RuleEffect(String nameIn, Decision decisionIn) {
        this.name = nameIn;
        this.decision = decisionIn;
    }

    public String getName() {
        return this.name;
    }

    public Decision getDecision() {
        return this.decision;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * Maps a XACML rule effect <code>String</code> name to the matching <code>RuleEffect</code>.
     *
     * @param effectName the <code>String</code> effect name to match
     * @return the matching <code>RuleEffect</code> or null if there is no match
     */
    public static RuleEffect getRuleEffect(String effectName) {
        for (RuleEffect ruleEffect : RuleEffect.values()) {
            if (ruleEffect.getName().equalsIgnoreCase(effectName)) {
                return ruleEffect;
            }
        }
        return null;
    }
}
