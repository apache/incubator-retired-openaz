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
package org.apache.openaz.xacml.std.trace;

import java.util.Date;

import org.apache.openaz.xacml.api.trace.TraceEvent;
import org.apache.openaz.xacml.api.trace.Traceable;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.trace.TraceEvent} interface.
 *
 * @param <T>
 */
public class StdTraceEvent<T> implements TraceEvent<T> {
    private Date timestamp;
    private String message;
    private Traceable cause;
    private T value;

    public StdTraceEvent(Date timestampIn, String messageIn, Traceable causeIn, T valueIn) {
        this.timestamp = timestampIn;
        this.message = messageIn;
        this.cause = causeIn;
        this.value = valueIn;
    }

    public StdTraceEvent(String messageIn, Traceable causeIn, T valueIn) {
        this(new Date(), messageIn, causeIn, valueIn);
    }

    public StdTraceEvent(Date timestampIn, String messageIn, T valueIn) {
        this(timestampIn, messageIn, null, valueIn);
    }

    public StdTraceEvent(String messageIn, T valueIn) {
        this(new Date(), messageIn, null, valueIn);
    }

    public StdTraceEvent() {
        this(new Date(), null, null, null);
    }

    @Override
    public Date getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Traceable getCause() {
        return this.cause;
    }

    @Override
    public T getValue() {
        return this.value;
    }

}
