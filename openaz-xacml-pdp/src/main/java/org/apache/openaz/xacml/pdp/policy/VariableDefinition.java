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

import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * VariableDefinition extends {@link PolicyComponent} to represent a XACML VariableDefinition element.
 */
public class VariableDefinition extends PolicyComponent {
    private String id;
    private Expression expression;

    public VariableDefinition(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public VariableDefinition(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public VariableDefinition() {
        super();
    }

    /**
     * Gets the id of the variable for this <code>VariableDefinition</code>.
     *
     * @return the <code>String</code> id for the variable for this <code>VariableDefinition</code>.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the id of the variable for this <code>VariableDefinition</code>.
     *
     * @param idIn the <code>String</code> id for the variable for this <code>VariableDefinition</code>.
     */
    public void setId(String idIn) {
        this.id = idIn;
    }

    /**
     * Gets the {@link Expression} for this <code>VariableDefinition</code>.
     *
     * @return the <code>Expression</code> for this <code>VariableDefinition</code>.
     */
    public Expression getExpression() {
        return this.expression;
    }

    /**
     * Sets the <code>Expression</code> for this <code>VariableDefinition</code>.
     *
     * @param expressionIn the <code>Expression</code> for this <code>VariableDefinition</code>
     */
    public void setExpression(Expression expressionIn) {
        this.expression = expressionIn;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        Object objectToDump;
        if ((objectToDump = this.getId()) != null) {
            stringBuilder.append(",id=");
            stringBuilder.append((String)objectToDump);
        }
        if ((objectToDump = this.getExpression()) != null) {
            stringBuilder.append(",expression=");
            stringBuilder.append(objectToDump.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    protected boolean validateComponent() {
        if (this.getId() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing variable id");
            return false;
        } else if (this.getExpression() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing variable expression");
            return false;
        } else {
            this.setStatus(StdStatusCode.STATUS_CODE_OK, null);
            return true;
        }
    }
}
