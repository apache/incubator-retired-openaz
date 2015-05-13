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

package org.apache.openaz.xacml.api.trace;

import java.util.Date;

/**
 * Defines the API for objects that represent traceable events during evaluation of a XACML Policy or
 * PolicySet.
 *
 * @param T the java <code>Class</code> of the traced object wrapped by the <code>TraceEvent</code>
 */
public interface TraceEvent<T> {
    /**
     * Gets the timestamp as a <code>Date</code> when this <code>TraceEvent</code> occurred.
     *
     * @return the timestamp as a <code>Date</code> when this <code>TraceEvent</code> occurred.
     */
    Date getTimestamp();

    /**
     * Gets the <code>String</code> message associated with this <code>TraceEvent</code>. If there is no
     * message, the implementation may return <code>null</code>.
     *
     * @return the <code>String</code> message associated with this <code>TraceEvent</code>.
     */
    String getMessage();

    /**
     * Gets the {@link Traceable} that created this <code>TraceEvent</code>
     *
     * @return the <code>Traceable</code> that created this <code>TraceEvent</code>.
     */
    Traceable getCause();

    /**
     * Gets the <code>T</code> object representing the value of this <code>TraceEvent</code>.
     *
     * @return the <code>T</code> object representing the value of this <code>TraceEvent</code>.
     */
    T getValue();
}
