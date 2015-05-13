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
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * CombinerParameter extends {@link PolicyComponent} to represent a XACML CombinerParameter element.
 */
public class CombinerParameter extends PolicyComponent {
    private String name;
    private AttributeValue<?> attributeValue;

    @Override
    protected boolean validateComponent() {
        if (this.getName() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing parameter name");
            return false;
        } else if (this.getAttributeValue() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing attribute value");
            return false;
        } else {
            this.setStatus(StdStatusCode.STATUS_CODE_OK, null);
            return true;
        }
    }

    /**
     * Creates a new <code>CombinerParameter</code> with the given <code>String</code> name,
     * <code>AttributeValue</code>, {@link org.apache.openaz.xacml.api.StatusCode} and <code>String</code>
     * status message.
     *
     * @param nameIn the <code>String</code> name of the <code>CombinerParameter</code>
     * @param attributeValueIn the <code>AttributeValue</code> of the <code>CombinerParameter</code>
     * @param statusCodeIn the <code>StatusCode</code> of the <code>CombinerParameter</code>
     * @param statusMessageIn the <code>String</code> status message of the <code>CombinerParameter</code>
     */
    public CombinerParameter(String nameIn, AttributeValue<?> attributeValueIn, StatusCode statusCodeIn,
                             String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
        this.name = nameIn;
        this.attributeValue = attributeValueIn;
    }

    /**
     * Creates a new <code>CombinerParameter</code> for an error condition with the given
     * <code>StatusCode</code> and <code>String</code> status message.
     *
     * @param statusCodeIn the <code>StatusCode</code> of the <code>CombinerParameter</code>
     * @param statusMessageIn the <code>String</code> status message of the <code>CombinerParameter</code>
     */
    public CombinerParameter(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    /**
     * Creates a new <code>CombinerParameter</code> for an error condition with the given
     * <code>StatusCode</code> and null status message.
     *
     * @param statusCodeIn the <code>StatusCode</code> of the <code>CombinerParameter</code>
     */
    public CombinerParameter(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    /**
     * Creates a new <code>CombinerParameter</code> with a default <code>StatusCode</code>, null status
     * message, and the given <code>String</code> name and <code>AttributeValue</code>>
     *
     * @param nameIn the <code>String</code> name of the <code>CombinerParameter</code>
     * @param attributeValueIn the <code>AttributeValue</code> of the <code>CombinerParameter</code>
     */
    public CombinerParameter(String nameIn, AttributeValue<?> attributeValueIn) {
        super();
        this.name = nameIn;
        this.attributeValue = attributeValueIn;
    }

    public CombinerParameter() {

    }

    /**
     * Gets the <code>String</code> name of this <code>CombinerParameter</code>.
     *
     * @return the <code>String</code> name of this <code>CombinerParameter</code>
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of this <code>CombinerParameter</code> to the given <code>String</code>.
     *
     * @param nameIn the <code>String</code> name for this <code>CombinerParameter</code>.
     */
    public void setName(String nameIn) {
        this.name = nameIn;
    }

    /**
     * Gets the <code>AttributeValue</code> of this <code>CombinerParameter</code>.
     *
     * @return the <code>AttributeValue</code> of this <code>CombinerParameter</code>
     */
    public AttributeValue<?> getAttributeValue() {
        return this.attributeValue;
    }

    /**
     * Sets the <code>AttributeValue</code> for this <code>CombinerParameter</code>>
     *
     * @param attributeValueIn the <code>AttributeValue</code> for this <code>CombinerParameter</code>>
     */
    public void setAttributeValue(AttributeValue<?> attributeValueIn) {
        this.attributeValue = attributeValueIn;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        Object objectToDump;
        if ((objectToDump = this.getName()) != null) {
            stringBuilder.append(",name=");
            stringBuilder.append((String)objectToDump);
        }
        if ((objectToDump = this.getAttributeValue()) != null) {
            stringBuilder.append(",attributeValue=");
            stringBuilder.append(objectToDump.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
