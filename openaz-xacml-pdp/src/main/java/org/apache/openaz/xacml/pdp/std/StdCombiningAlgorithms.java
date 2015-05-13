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
package org.apache.openaz.xacml.pdp.std;

import org.apache.openaz.xacml.api.XACML1;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.CombiningAlgorithm;
import org.apache.openaz.xacml.pdp.policy.PolicySetChild;
import org.apache.openaz.xacml.pdp.policy.Rule;
import org.apache.openaz.xacml.pdp.std.combiners.CombinedPermitOverrides;
import org.apache.openaz.xacml.pdp.std.combiners.DenyOverrides;
import org.apache.openaz.xacml.pdp.std.combiners.DenyUnlessPermit;
import org.apache.openaz.xacml.pdp.std.combiners.FirstApplicable;
import org.apache.openaz.xacml.pdp.std.combiners.LegacyDenyOverridesPolicy;
import org.apache.openaz.xacml.pdp.std.combiners.LegacyDenyOverridesRule;
import org.apache.openaz.xacml.pdp.std.combiners.LegacyPermitOverridesPolicy;
import org.apache.openaz.xacml.pdp.std.combiners.LegacyPermitOverridesRule;
import org.apache.openaz.xacml.pdp.std.combiners.OnlyOneApplicable;
import org.apache.openaz.xacml.pdp.std.combiners.PermitOverrides;
import org.apache.openaz.xacml.pdp.std.combiners.PermitUnlessDeny;
import org.apache.openaz.xacml.pdp.util.OpenAZPDPProperties;

/**
 * StdCombiningAlgorithms contains single instances of each of the
 * {@link org.apache.openaz.xacml.pdp.policy.CombiningAlgorithm} implementations in the
 * {@link org.apache.openaz.xacml.pdp.std.combiners} package.
 */
public class StdCombiningAlgorithms {

    protected StdCombiningAlgorithms() {
    }

    public static final String PREFIX_CA = "CA_";
    public static final String PREFIX_RULE = PREFIX_CA + "RULE_";
    public static final String PREFIX_POLICY = PREFIX_CA + "POLICY_";

    // C.2 Deny-overrides
    public static final CombiningAlgorithm<Rule> CA_RULE_DENY_OVERRIDES = new DenyOverrides<Rule>(
                                                                                                  XACML3.ID_RULE_DENY_OVERRIDES);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_DENY_OVERRIDES = new DenyOverrides<PolicySetChild>(
                                                                                                                        XACML3.ID_POLICY_DENY_OVERRIDES);

    // C.3 Ordered-deny-overrides
    public static final CombiningAlgorithm<Rule> CA_RULE_ORDERED_DENY_OVERRIDES = new DenyOverrides<Rule>(
                                                                                                          XACML3.ID_RULE_ORDERED_DENY_OVERRIDES);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_ORDERED_DENY_OVERRIDES = new DenyOverrides<PolicySetChild>(
                                                                                                                                XACML3.ID_POLICY_ORDERED_DENY_OVERRIDES);

    // C.4 Permit-overrides
    public static final CombiningAlgorithm<Rule> CA_RULE_PERMIT_OVERRIDES = new PermitOverrides<Rule>(
                                                                                                      XACML3.ID_RULE_PERMIT_OVERRIDES);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_PERMIT_OVERRIDES = new PermitOverrides<PolicySetChild>(
                                                                                                                            XACML3.ID_POLICY_PERMIT_OVERRIDES);

    // C.5 Ordered-permit-overrides
    public static final CombiningAlgorithm<Rule> CA_RULE_ORDERED_PERMIT_OVERRIDES = new PermitOverrides<Rule>(
                                                                                                              XACML3.ID_RULE_ORDERED_PERMIT_OVERRIDES);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_ORDERED_PERMIT_OVERRIDES = new PermitOverrides<PolicySetChild>(
                                                                                                                                    XACML3.ID_POLICY_ORDERED_PERMIT_OVERRIDES);

    // C.6 Deny-unless-permit
    public static final CombiningAlgorithm<Rule> CA_RULE_DENY_UNLESS_PERMIT = new DenyUnlessPermit<Rule>(
                                                                                                         XACML3.ID_RULE_DENY_UNLESS_PERMIT);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_DENY_UNLESS_PERMIT = new DenyUnlessPermit<PolicySetChild>(
                                                                                                                               XACML3.ID_POLICY_DENY_UNLESS_PERMIT);

    // C.7 Permit-unles-deny
    public static final CombiningAlgorithm<Rule> CA_RULE_PERMIT_UNLESS_DENY = new PermitUnlessDeny<Rule>(
                                                                                                         XACML3.ID_RULE_PERMIT_UNLESS_DENY);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_PERMIT_UNLESS_DENY = new PermitUnlessDeny<PolicySetChild>(
                                                                                                                               XACML3.ID_POLICY_PERMIT_UNLESS_DENY);

    // C.8 First-applicable
    public static final CombiningAlgorithm<Rule> CA_RULE_FIRST_APPLICABLE = new FirstApplicable<Rule>(
                                                                                                      XACML1.ID_RULE_FIRST_APPLICABLE);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_FIRST_APPLICABLE = new FirstApplicable<PolicySetChild>(
                                                                                                                            XACML1.ID_POLICY_FIRST_APPLICABLE);

    // C.9 Only-one-applicable
    // public static final CombiningAlgorithm<Rule> CA_RULE_ONLY_ONE_APPLICABLE
    // = new OnlyOneApplicable<Rule>(XACML1.ID_RULE_ONLY_ONE_APPLICABLE);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_ONLY_ONE_APPLICABLE = new OnlyOneApplicable(
                                                                                                                 XACML1.ID_POLICY_ONLY_ONE_APPLICABLE);

    // C.10 Legacy Deny-overrides
    public static final CombiningAlgorithm<Rule> CA_RULE_LEGACY_DENY_OVERRIDES = new LegacyDenyOverridesRule(
                                                                                                             XACML1.ID_RULE_DENY_OVERRIDES);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_LEGACY_DENY_OVERRIDES = new LegacyDenyOverridesPolicy(
                                                                                                                           XACML1.ID_POLICY_DENY_OVERRIDES);

    // C.11 Legacy Ordered-deny-overrides
    public static final CombiningAlgorithm<Rule> CA_RULE_LEGACY_ORDERED_DENY_OVERRIDES = new LegacyDenyOverridesRule(
                                                                                                                     XACML1.ID_RULE_ORDERED_DENY_OVERRIDES);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_LEGACY_ORDERED_DENY_OVERRIDES = new LegacyDenyOverridesPolicy(
                                                                                                                                   XACML1.ID_POLICY_ORDERED_DENY_OVERRIDES);

    // C.12 Legacy Permit-overrides
    public static final CombiningAlgorithm<Rule> CA_RULE_LEGACY_PERMIT_OVERRIDES = new LegacyPermitOverridesRule(
                                                                                                                 XACML1.ID_RULE_PERMIT_OVERRIDES);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_LEGACY_PERMIT_OVERRIDES = new LegacyPermitOverridesPolicy(
                                                                                                                               XACML1.ID_POLICY_PERMIT_OVERRIDES);

    // C.13 Legacy Ordered-permit-overrides
    public static final CombiningAlgorithm<Rule> CA_RULE_LEGACY_ORDERED_PERMIT_OVERRIDES = new LegacyPermitOverridesRule(
                                                                                                                         XACML1.ID_RULE_ORDERED_PERMIT_OVERRIDES);
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_LEGACY_ORDERED_PERMIT_OVERRIDES = new LegacyPermitOverridesPolicy(
                                                                                                                                       XACML1.ID_POLICY_ORDERED_PERMIT_OVERRIDES);

    //
    // Custom AT&T Policy Combing Algorithms
    //
    public static final CombiningAlgorithm<PolicySetChild> CA_POLICY_COMBINED_PERMIT_OVERRIDES
        = new CombinedPermitOverrides<PolicySetChild>(OpenAZPDPProperties.ID_POLICY_COMBINEDPERMITOVERRIDES);

}
