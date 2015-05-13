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
package org.apache.openaz.xacml.pdp.std.functions;

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.pdp.policy.Bag;
import org.apache.openaz.xacml.pdp.policy.FunctionArgument;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * A ConvertedArgument is the result of processing an
 * {@link org.apache.openaz.xacml.pdp.policy.FunctionArgument} to validate its correctness and to convert it
 * into an object of the required type. It is returned by the <code>validateArguments</code> method in
 * {@link org.apache.openaz.xacml.pdp.std.functions.FunctionDefinitionHomogeneousSimple} and should only be
 * used by other Functions in that same package. This is a data holder with no processing. It contains two
 * elements:
 * <UL>
 * <LI>A {@link org.apache.openaz.xacml.api.Status} object, and
 * <LI>An object containing the value of the FunctionArgument processed by validateArguments. This object will
 * only exist if status.isOk() (or the isOk() method in this class that calls status.isOk()) is true.
 * </UL>
 */
public class ConvertedArgument<I> {

    // When status != Status.OK, the value is null
    private Status status;

    // This is non-null when status == Status.OK
    private I value = null;

    /**
     * Constructor ensures we have a non-null status, though value will be null if status is not ok.
     *
     * @param s
     * @param v
     */
    public ConvertedArgument(Status s, I v) {
        status = s;
        if (s == null) {
            throw new IllegalArgumentException("Status of argument cannot be null");
        }
        if (s.isOk()) {
            // only set value if status is ok
            value = v;
        }
    }

    /**
     * Get the Status object
     *
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Convenience method that directly returns the isOk() state from the status object.
     *
     * @return
     */
    public boolean isOk() {
        return status.isOk();
    }

    /**
     * Get the value object. This may be a Bag.
     *
     * @return
     */
    public I getValue() {
        return value;
    }

    /**
     * Get the value as a Bag. (convenience method)
     *
     * @return
     */
    public Bag getBag() {
        return (Bag)value;
    }

    /**
     * Returns a shortened version of the given DataType Id, primarily for use with error messages to prevent
     * them from becoming too long. This is a simple convenience method to reduce code bloat.
     *
     * @param identifier expected to have '#' in it, and if no '#' should have ":data-type:"
     * @return
     */
    public String getShortDataTypeId(Identifier identifier) {
        String idString = identifier.stringValue();
        int index = idString.indexOf("#");
        if (index < 0) {
            index = idString.indexOf(":data-type:");
            if (index < 0) {
                return idString;
            } else {
                return idString.substring(index + 11);
            }
        } else {
            return idString.substring(index + 1);
        }
    }

    /**
     * Evaluates the given <code>FunctionArgument</code> and validates that it has the correct
     * <code>DataType</code>. The returned object will be either:
     * <UL>
     * <LI>A Status Object indicating an error condition, or
     * <LI>An Object of the appropriate type containing the value of the function. In this case the caller
     * should assume that the Status is Status.OK. Note that the object may be a bag if that is what the
     * caller expected.
     * </UL>
     *
     * @param listFunctionArguments the <code>List</code> of <code>FunctionArgument</code>s to validate
     * @param convertedValues the <code>List</code> of <code>U</code> that the converted value is added to.
     * @return a {@link org.apache.openaz.xacml.api.Status} indication with an error if the arguments are not
     *         valid, or an object of the correct DataType containing the value.
     */
    @SuppressWarnings("unchecked")
    // to suppress warning on bag conversion
    public ConvertedArgument(FunctionArgument functionArgument, DataType<I> expectedDataType,
                             boolean expectBag) {

        if (functionArgument == null) {
            status = new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, "Got null argument");
            return;
        }
        if (!functionArgument.isOk()) {
            status = functionArgument.getStatus();
            return;
        }

        // bags are valid arguments for some functions
        if (expectBag) {
            if (!functionArgument.isBag()) {
                status = new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                       "Expected a bag, saw a simple value");
                return;
            }

            Bag bag = functionArgument.getBag();
            value = (I)bag;
            status = StdStatus.STATUS_OK;
            return;
        }

        // argument should not be a bag
        if (functionArgument.isBag()) {
            status = new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                   "Expected a simple value, saw a bag");
            return;
        }
        AttributeValue<?> attributeValue = functionArgument.getValue();
        if (attributeValue == null || attributeValue.getValue() == null) {
            status = new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, "Got null attribute");
            return;
        }
        if (!attributeValue.getDataTypeId().equals(expectedDataType.getId())) {
            status = new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                   "Expected data type '" + getShortDataTypeId(expectedDataType.getId())
                                       + "' saw '" + getShortDataTypeId(attributeValue.getDataTypeId()) + "'");
            return;
        }

        try {
            value = expectedDataType.convert(attributeValue.getValue());
            status = StdStatus.STATUS_OK;
        } catch (Exception e) {
            String message = e.getMessage();
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            status = new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, message);
        }
    }

}
