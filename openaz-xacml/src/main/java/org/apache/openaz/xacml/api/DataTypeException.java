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
package org.apache.openaz.xacml.api;

/**
 * Extends <code>Exception</code> to represent errors thrown by methods in the {@link DataType} interface.
 */
public class DataTypeException extends Exception {
    private static final long serialVersionUID = -6308818179904447096L;

    private DataType<?> dataType;

    /**
     * Creates a new <code>DataTypeException</code> for an error thrown by the given {@link DataType}.
     *
     * @param dataTypeIn the <code>DataType</code> throwing the error.
     */
    public DataTypeException(DataType<?> dataTypeIn) {
        this.dataType = dataTypeIn;
    }

    /**
     * Creates a new <code>DataTypeException</code> for an error thrown by the given {@link DataType} with a
     * <code>String</code> message.
     *
     * @param dataTypeIn the <code>DataType</code> throwing the error
     * @param message the <code>String</code> error message
     */
    public DataTypeException(DataType<?> dataTypeIn, String message) {
        super(message);
        this.dataType = dataTypeIn;
    }

    /**
     * Creates a new <code>DataTypeException</code> for an error thrown by the given {@link DataType} with a
     * <code>Throwable</code> cause.
     *
     * @param dataTypeIn the <code>DataType</code> throwing the error
     * @param cause the <code>Throwable</code> cause of the error
     */
    public DataTypeException(DataType<?> dataTypeIn, Throwable cause) {
        super(cause);
        this.dataType = dataTypeIn;
    }

    /**
     * Creates a new <code>DataTypeException</code> for an error thrown by the given {@link DataType} with the
     * given <code>String</code> message and <code>Throwable</code> cause.
     *
     * @param dataTypeIn the <code>DataType</code> throwing the error
     * @param message the <code>String</code> error message
     * @param cause the <code>Throwable</code> cause of the error
     */
    public DataTypeException(DataType<?> dataTypeIn, String message, Throwable cause) {
        super(message, cause);
        this.dataType = dataTypeIn;
    }

    /**
     * Returns the {@link DataType} that caused the <code>Exception</code>.
     *
     * @return the <code>DataType</code> that caused the <code>Exception</code>
     */
    public DataType<?> getDataType() {
        return this.dataType;
    }
}
