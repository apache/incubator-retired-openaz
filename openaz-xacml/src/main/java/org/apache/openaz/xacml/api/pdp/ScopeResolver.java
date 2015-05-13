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
package org.apache.openaz.xacml.api.pdp;

import org.apache.openaz.xacml.api.Attribute;

/**
 * ScopeResolver is the interface that objects implement that can be used to resolve attribute scopes to
 * individual identifiers.
 */
public interface ScopeResolver {
    /**
    * Examines the given {@link org.apache.openaz.xacml.api.Attribute} representing a resource identifier for
     * a hierarchical resource and returns an <code>Iterator</code> over a set of <code>Attribute</code>s
     * representing individual resource identifiers that are part of the requested hierarchy as specified by
     * the {@link org.apache.openaz.xacml.api.pdp.ScopeQualifier}.
     *
     * @param attributeResourceId the <code>Attribute</code> for the resource identifier
     * @param scopeQualifier the <code>ScopeQualifier</code> determining which nodes are returned
     * @return a {@link org.apache.openaz.xacml.api.pdp.ScopeResolverResult} with the results of the request
     * @throws ScopeResolverException if there is an error resolving the resource identifier to a scope.
     */
    ScopeResolverResult resolveScope(Attribute attributeResourceId, ScopeQualifier scopeQualifier)
        throws ScopeResolverException;
}
