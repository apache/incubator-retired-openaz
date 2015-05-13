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
package org.apache.openaz.xacml.pdp.policy.expressions;

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.api.StatusDetail;
import org.apache.openaz.xacml.api.XACML;
import org.apache.openaz.xacml.pdp.policy.Expression;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.datatypes.DataTypes;

/**
 * AttributeRetrievalBase extends {@link org.apache.openaz.xacml.pdp.policy.PolicyComponent} and implements
 * {@link org.apache.openaz.xacml.pdp.eval.Evaluatable} to serve as an abstract base class for the
 * {@link org.apache.openaz.xacml.pdp.policy.AttributeSelector} and
 * {@link org.apache.openaz.xacml.pdp.policy.AttributeDesignator} classes.
 */
public abstract class AttributeRetrievalBase extends Expression {
    private Identifier category;
    private Identifier dataTypeId;
    private Boolean mustBePresent;

    protected AttributeRetrievalBase(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    protected AttributeRetrievalBase(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    protected AttributeRetrievalBase() {
    }

    protected AttributeRetrievalBase(Identifier categoryIn, Identifier dataTypeIdIn, Boolean mustBePresentIn) {
        this.category = categoryIn;
        this.dataTypeId = dataTypeIdIn;
        this.mustBePresent = mustBePresentIn;
    }

    /**
     * Gets the {@link org.apache.openaz.xacml.api.Identifier} for the category associated with this
     * <code>AttributeRetrievalBase</code>.
     *
     * @return the <code>Identifier</code> for the category of this <code>AttributeRetrievalBase</code>.
     */
    public Identifier getCategory() {
        return this.category;
    }

    /**
     * Sets the <code>Identifier</code> for the category associated with this
     * <code>AttributeRetrievalBase</code>.
     *
     * @param categoryIn the <code>Identifier</code> for the category associated with this
     *            <code>AttributeRetrievalBase</code>
     */
    public void setCategory(Identifier categoryIn) {
        this.category = categoryIn;
    }

    /**
     * Gets the <code>Identifier</code> for the data type associated with this
     * <code>AttributeRetrievalBase</code>.
     *
     * @return the <code>Identifier</code> for the data type associated with this
     *         <code>AttributeRetrievalBase</code>
     */
    public Identifier getDataTypeId() {
        return this.dataTypeId;
    }

    /**
     * Sets the <code>Identifier</code> for the data type associated with this
     * <code>AttributeRetrievalBase</code>.
     *
     * @param dataTypeIn the <code>Identifier</code> for the data type associated with this
     *            <code>AttributeRetrievalBase</code>
     */
    public void setDataTypeId(Identifier dataTypeIn) {
        // allow old-style Ids for Durations since there is no structural or semantic changes, just a
        // different Id.
        if (dataTypeIn.equals(XACML.ID_DATATYPE_WD_DAYTIMEDURATION)) {
            dataTypeIn = DataTypes.DT_DAYTIMEDURATION.getId();
        } else if (dataTypeIn.equals(XACML.ID_DATATYPE_WD_YEARMONTHDURATION)) {
            dataTypeIn = DataTypes.DT_YEARMONTHDURATION.getId();
        }
        this.dataTypeId = dataTypeIn;
    }

    /**
     * Determines if a value must be found for this <code>AttributeRetrievalBase</code> when it is evaluated.
     * If true, and no value is found, an indeterminate result is returned, otherwise an empty bag is
     * returned.
     *
     * @return true if the value of this <code>AttributeRetrievalBase</code> must be found, else false
     */
    public Boolean getMustBePresent() {
        return this.mustBePresent;
    }

    /**
     * Sets the flag indicating whether a value must be found for this <code>AttributeRetrievalBase</code>.
     *
     * @param b the boolean value for the flag
     */
    public void setMustBePresent(boolean b) {
        this.mustBePresent = b;
    }

    @Override
    protected boolean validateComponent() {
        if (this.getCategory() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing Category");
            return false;
        } else if (this.getDataTypeId() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing DataType");
            return false;
        } else if (this.getMustBePresent() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing MustBePresent");
            return false;
        } else {
            this.setStatus(StdStatusCode.STATUS_CODE_OK, null);
            return true;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        Object objectToDump;
        if ((objectToDump = this.getCategory()) != null) {
            stringBuilder.append(",category=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getDataTypeId()) != null) {
            stringBuilder.append(",dataType=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getMustBePresent()) != null) {
            stringBuilder.append(",mustBePresent=");
            stringBuilder.append(objectToDump.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    /**
     * Creates the appropriate {@link org.apache.openaz.xacml.pdp.policy.ExpressionResult} for an empty list
     * based on the <code>getMustBePresent</code> value.
     *
     * @return an appropriate <code>ExpressionResult</code>
     */
    protected ExpressionResult getEmptyResult(String statusMessage, StatusDetail statusDetail) {
        if (this.getMustBePresent() != null && this.getMustBePresent().booleanValue()) {
            return ExpressionResult.newError(new StdStatus(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE,
                                                           statusMessage, statusDetail));
        } else {
            return ExpressionResult.newEmpty();
        }
    }

}
