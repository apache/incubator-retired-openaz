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
 * Defines the API for objects that represent XACML RequestDefaults elements.
 */
public interface RequestDefaults {
    /**
     * Returns the <code>URI</code> of the XPath version to use when applying XPath expressions to XML
     * content.
     *
     * @return the <code>URI</code> of the XPath version.
     */
    URI getXPathVersion();

    /**
     * {@inheritDoc} Implementations of this interface must override the <code>equals</code> method with the
     * following semantics: Two <code>RequestDefaults</code> (<code>r1</code> and <code>r2</code> are equal
     * if: {@code r1.getXPathVersion() == null && r2.getXPathVersion() == null} OR
     * {@code r1.getXPathVersion().equals(r2.getXPathVersion())}
     */
    @Override
    boolean equals(Object obj);
}
