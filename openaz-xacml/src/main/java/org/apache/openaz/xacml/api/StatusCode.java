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
 * Defines the API for objects that represent XACML StatusCode values.
 */
public interface StatusCode {
    /**
     * Retrieves the major status {@link org.apache.openaz.xacmo.common.Identifier} for this
     * <code>StatusCode</code>.
     *
     * @return the major status <code>Identifier</code> for this <code>StatusCode</code>
     */
    Identifier getStatusCodeValue();

    /**
     * Gets a child <code>StatusCode</code> of this <code>StatusCode</code> if there is one.
     *
     * @return the child <code>StatusCode</code> of this <code>StatusCode</code> or null if there is none
     */
    StatusCode getChild();

    /**
     * {@inheritDoc} Implementations of the <code>StatusCode</code> interface must override the
     * <code>equals</code> method as follows: Two <code>StatusCode</code>s (<code>s1</code> and
     * <code>s2</code>) are equal if: {@code s1.getIdentifer().equals(s2.getIdentifier()} AND
     * {@code s1.getChild() == null && s2.getChild() == null} OR {@code s1.getChild().equals(s2.getChild())}
     */
    @Override
    boolean equals(Object obj);
}
