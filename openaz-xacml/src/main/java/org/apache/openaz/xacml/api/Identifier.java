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

import java.net.URI;

/**
 * Identifier is the interface for objects that represent a XACML 3.0 identifier. In most cases the
 * {@link org.apache.openaz.xacml.std.IdentifierImpl} in this package will suffice as an implementation of this
 * interface, but all use of identifiers will use the <code>Identifier</code> interface to allow for
 * extensions. Classes that implement the <code>Identifier</code> interface should override the
 * <code>equals</code> method to meet the following semantics: Two <code>Identifier</code>s are
 * <code>equal</code> if the values returned by the <code>getUri</code> method are <code>equal</code>.
 */
public interface Identifier extends SemanticString {
    /**
     * Gets this <code>Identifier</code> as a <code>URI</code>.
     *
     * @return the <code>URI</code> representation of this <code>Identifier</code>.
     */
    URI getUri();

    /**
     * {@inheritDoc} The implementation of the <code>Identifier</code> interface must override the
     * <code>hashCode</code> method .
     */
    @Override
    int hashCode();

    /**
     * {@inheritDoc} The implementation of the <code>Identifier</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>Identifier</code> objects (
     * <code>i1</code> and <code>i2</code>) are equal if: {@code a1.getUri().equals(a2.getUri())}
     */
    @Override
    boolean equals(Object obj);
}
