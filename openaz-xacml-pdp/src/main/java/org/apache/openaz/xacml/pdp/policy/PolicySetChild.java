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

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.api.trace.Traceable;
import org.apache.openaz.xacml.pdp.eval.Evaluatable;
import org.apache.openaz.xacml.pdp.eval.Matchable;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * PolicySetChild extends {@link org.apache.openaz.xacml.pdp.PolicyComponent} to represent XACML 3.0
 * Policies, PolicySets, PolicyReferences, and PolicySetReferences.
 */
public abstract class PolicySetChild extends PolicyComponent implements Evaluatable, Matchable, Traceable {
    private Identifier identifier;
    private PolicyDefaults policyDefaults;
    private PolicySet parent;

    /**
     * Creates a new <code>PolicySetChild</code> with the given given
     * {@link org.apache.openaz.xacml.api.StatusCode} and <code>String</code> status message.
     *
     * @param statusCodeIn the <code>StatusCode</code> for the new <code>PolicySetChild</code>
     * @param statusMessageIn the <code>String</code> status message for the new <code>PolicySetChild</code>
     */
    protected PolicySetChild(PolicySet parentIn, StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
        this.parent = parentIn;
    }

    protected PolicySetChild(StatusCode statusCodeIn, String statusMessageIn) {
        this(null, statusCodeIn, statusMessageIn);
    }

    /**
     * Creates a new <code>PolicySetChild</code> with the default OK <code>StatusCode</code>.
     *
     * @param statusCodeIn the <code>StatusCode</code> for this <code>PolicySetChild</code>
     */
    protected PolicySetChild(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    protected PolicySetChild(PolicySet parentIn) {
        this.parent = parentIn;
    }

    /**
     * Creates a new <code>PolicySetChild</code> with the default OK status.
     */
    protected PolicySetChild() {
        super();
    }

    /**
     * Gets the <code>Identifier</code> for this <code>PolicySetChild</code>.
     *
     * @return the <code>Identifier</code> for this <code>PolicySetChild</code>
     */
    public Identifier getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(Identifier identifierIn) {
        this.identifier = identifierIn;
    }

    /**
     * Gets the <code>PolicyDefaults</code> for this <code>PolicySetChild</code>.
     *
     * @return the <code>PolicyDefaults</code> for this <code>PolicySetChild</code>
     */
    public PolicyDefaults getPolicyDefaults() {
        return this.policyDefaults;
    }

    /**
     * Sets the <code>PolicyDefaults</code> for this <code>PolicySetChild</code>.
     *
     * @param policyDefaultsIn the <code>PolicyDefaults</code> for this <code>PolicySetChild</code>
     */
    public void setPolicyDefaults(PolicyDefaults policyDefaultsIn) {
        this.policyDefaults = policyDefaultsIn;
    }

    /**
     * Gets the parent {@link PolicySet} containing this <code>PolicySetChild</code> or null if this is the
     * root.
     *
     * @return the parent <code>PolicySet</code> of this <code>PolicySetChild</code>
     */
    public PolicySet getParent() {
        return this.parent;
    }

    @Override
    protected boolean validateComponent() {
        if (this.getIdentifier() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing identifier");
            return false;
        } else {
            this.setStatus(StdStatusCode.STATUS_CODE_OK, null);
            return true;
        }
    }

    @Override
    public String getTraceId() {
        return this.getIdentifier().stringValue();
    }

    @Override
    public Traceable getCause() {
        return this.parent;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        Object objectToDump;
        if ((objectToDump = this.getIdentifier()) != null) {
            stringBuilder.append(",identifier=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getPolicyDefaults()) != null) {
            stringBuilder.append(",policyDefaults=");
            stringBuilder.append(objectToDump.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
