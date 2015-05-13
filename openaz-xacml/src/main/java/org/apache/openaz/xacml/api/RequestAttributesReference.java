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
 * Defines the API for objects that implement XACML AttributesReference elements.
 */
public interface RequestAttributesReference {
    /**
     * Gets the <String> representing the xml:Id of the XACML AttributesReference element represented by this
     * <code>RequestAttributesReference</code>
     *
     * @return the <code>String</code> representing the xml:Id of the XACML AttributesReference element
     *         represented by this <code>RequestAttributesReference</code>
     */
    String getReferenceId();

    /**
     * {@inheritDoc} Implementations of this interface must override the <code>equals</code> method with the
     * following semantics: Two <code>RequestAttributesReference</code>s (<code>r1</code> and <code>r2</code>)
     * are equal if: {@code r1.getReferenceId() == null && r2.getReferenceId() == null} OR
     * {@code r1.getReferenceId().equals(r2.getReferenceId())}
     */
    @Override
    boolean equals(Object obj);
}
