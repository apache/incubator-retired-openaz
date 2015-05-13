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
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.datatypes.DataTypeBoolean;

/**
 * ExpressionResultBoolean extends {@link ExpressionResult} to represent predicates.
 */
public class ExpressionResultBoolean extends ExpressionResult {
    private AttributeValue<Boolean> value;
    public static final ExpressionResultBoolean ERB_FALSE = new ExpressionResultBoolean(false);
    public static final ExpressionResultBoolean ERB_TRUE = new ExpressionResultBoolean(true);

    public ExpressionResultBoolean(Status statusIn) {
        super(statusIn);
    }

    public ExpressionResultBoolean(boolean bvalue) {
        super(StdStatus.STATUS_OK);
        this.value = (bvalue ? DataTypeBoolean.AV_TRUE : DataTypeBoolean.AV_FALSE);
    }

    /**
     * Gets the <code>boolean</code> value of this <code>ExpressionResultBoolean</code>
     *
     * @return the <code>boolean</code> value of this <code>ExpressionResultBoolean</code>
     */
    public boolean isTrue() {
        if (this.value == null) {
            return false;
        } else {
            return this.value.getValue().booleanValue();
        }
    }

    @Override
    public AttributeValue<?> getValue() {
        return this.value;
    }

    @Override
    public boolean isBag() {
        return false;
    }

    @Override
    public Bag getBag() {
        return null;
    }
}
