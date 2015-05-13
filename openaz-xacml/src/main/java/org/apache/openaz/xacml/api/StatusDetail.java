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

import java.util.Collection;

/**
 * Defines the API for objects that represent XACML StatusDetail elements.
 */
public interface StatusDetail {
    /**
     * Gets the <code>Collection</code> of {@link org.apache.openaz.xacml.api.MissingAttributeDetail} objects
     * for this <code>StatusDetail</code>. If there are no <code>MissingAttributeDetail</code>s an empty
     * <code>Collection</code> must be returned.
     *
     * @return the <code>MissingAttributeDetail</code> objects for this <code>StatusDetail</code> or null if
     *         none.
     */
    Collection<MissingAttributeDetail> getMissingAttributeDetails();

    /**
     * Gets a <code>StatusDetail</code> object that is the result of merging this
     * <code>StatusDetail<code> with the
     * given <code>StatusDetail</code>.
     *
     * @param statusDetail the <code>StatusDetail</code> to merge in
     * @return a <code>StatusDetail</code> merging this <code>StatusDetail</code> with the given
     *         <code>StatusDetail</code>.
     */
    StatusDetail merge(StatusDetail statusDetail);

    /**
     * {@inheritDoc} Implementations of the <code>StatusDetail</code> interface must override the
     * <code>equals</code> method as follows: Two <code>StatusDetail</code>s (<code>s1</code> and
     * <code>s2</code>) are equal if: {@code s1.getMissingAttributeDetails()} is pair-wise equal to
     * {@code s2.getMissingAttributeDetails()}
     */
    @Override
    boolean equals(Object obj);
}
