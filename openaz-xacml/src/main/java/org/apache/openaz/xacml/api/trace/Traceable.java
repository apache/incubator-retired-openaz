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

/**
 * Defines the API for objects that can be set as the "cause" in a
 * {@link org.apache.openaz.xacml.api.trace.TraceEvent}. Objects cause a <code>TraceEvent</code> through
 * methods called during the evaluation of a XACML Policy or PolicySet.
 */
public interface Traceable {
    /**
     * Gets the <code>String</code> identifier for the object that caused the
     * {@link org.apache.openaz.xacml.api.trace.TraceEvent} as a result of a policy evaluation method.
     * Implementations must not return <code>null</code>.
     *
     * @return the <code>String</code> identifier for the object that caused the <code>TraceEvent</code>.
     */
    String getTraceId();

    /**
     * Gets the <code>Traceable</code> that caused the evaluation method on this <code>Traceable</code> to be
     * called. If there is no known causing object, this method should return <code>null</code>.
     *
     * @return the <code>Traceable</code> that caused the evaluation method on this <code>Traceable</code> to
     *         be called.
     */
    Traceable getCause();
}
