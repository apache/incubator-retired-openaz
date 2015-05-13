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

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.StatusCode;

/**
 * TargetedCombinerParameter extends {@link CombinerParameter} to include a lazy reference to a particular
 * sub-element within the evaluatable children that should be used when combining evaluation results from that
 * sub-element.
 *
 * @param <T> the type of the identifier used to reference the targeted object
 * @param <U> the type of the targeted object
 */
public class TargetedCombinerParameter<T, U> extends CombinerParameter {
    private T targetId;
    private U target;

    public TargetedCombinerParameter(T targetIdIn, String nameIn, AttributeValue<?> attributeValueIn,
                                     StatusCode statusCodeIn, String statusMessageIn) {
        super(nameIn, attributeValueIn, statusCodeIn, statusMessageIn);
        this.targetId = targetIdIn;
    }

    public TargetedCombinerParameter(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public TargetedCombinerParameter(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public TargetedCombinerParameter(T targetIdIn, String nameIn, AttributeValue<?> attributeValueIn) {
        super(nameIn, attributeValueIn);
        this.targetId = targetIdIn;
    }

    public TargetedCombinerParameter() {

    }

    /**
     * Gets the target id of this <code>TargetedCombinerParameter</code>.
     *
     * @return the <code>T</code> id of this <code>TargetedCombinerParameter</code>
     */
    public T getTargetId() {
        return this.targetId;
    }

    /**
     * Sets the target id to the given <code>T</code> value.
     *
     * @param targetIdIn the <code>T</code> to set as the target id
     */
    public void setTargetId(T targetIdIn) {
        this.targetId = targetIdIn;
    }

    /**
     * Gets the target for this <code>TargetedCombinerParameter</code>.
     *
     * @return the <code>U</code> target for this <code>TargetedCombinerParameter</code>
     */
    public U getTarget() {
        return this.target;
    }

    /**
     * Sets the target for this <code>TargetedCombinerParameter</code> to the given <code>U</code>.
     *
     * @param targetIn the <code>U</code> target for this <code>TargetedCombinerParameter</code>
     */
    public void setTarget(U targetIn) {
        this.target = targetIn;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        Object objectToDump;
        if ((objectToDump = this.getTargetId()) != null) {
            stringBuilder.append("targetId=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getTarget()) != null) {
            stringBuilder.append("target=");
            stringBuilder.append(objectToDump.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
