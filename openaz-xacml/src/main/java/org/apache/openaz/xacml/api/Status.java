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
 * Defines the API for objects that represent XACML Status elements.
 */
public interface Status {
    /**
     * Gets the {@link org.apache.openaz.xacml.api.StatusCode} for this <code>Status</code>.
     *
     * @return the <code>StatusCode</code> for this <code>Status</code>.
     */
    StatusCode getStatusCode();

    /**
     * Gets the <code>String</code> status message for this <code>Status</code>.
     *
     * @return the <code>String</code> status message for this <code>Status</code>
     */
    String getStatusMessage();

    /**
     * Gets the {@link org.apache.openaz.xacml.api.StatusDetail} associated with this <code>Status</code> if
     * there is any.
     *
     * @return the <code>StatusDetail</code> for this <code>Status</code> if there is any.
     */
    StatusDetail getStatusDetail();

    /**
     * Returns <code>true</code> if the <code>StatusCode</code> for this <code>Status</code> is the XACML OK
     * value.
     *
     * @return true if the <code>StatusCode</code> for this <code>Status</code> is the XACML OK value, else
     *         false
     */
    boolean isOk();

    /**
     * Returns a <code>Status</code> with the same <code>StatusCode</code> and status message as this
     * <code>Status</code> but whose <code>StatusDetail</code> is the merging of the <code>StatusDetail</code>
     * in this <code>Status</code> and the <code>StatusDetail</code> in the given <code>Status</code>.
     *
     * @param status the <code>Status</code> whose <code>StatusDetail</code> is to be merged in
     * @return a <code>Status</code> with merged <code>StatusDetail</code>
     */
    Status merge(Status status);

    /**
     * {@inheritDoc} Implementations of the <code>Status</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>Status</code> objects (
     * <code>s1</code> and <code>s2</code>) are equal if:
     * {@code s1.getStatusCode().equals(s2.getStatusCode())} AND
     * {@code s1.getStatusMessage() == null && s2.getStatusMessage() == null} OR
     * {@code s1.getStatusMessage().equals(s2.getStatusMessage())}
     * {@code s1.getStatusDetail() == null && s2.getStatusDetail() == null} OR
     * {@code s1.getStatusDetail().equals(s2.getStatusDetail())}
     */
    @Override
    boolean equals(Object obj);
}
