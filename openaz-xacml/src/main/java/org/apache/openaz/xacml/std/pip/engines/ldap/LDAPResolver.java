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
package org.apache.openaz.xacml.std.pip.engines.ldap;

import java.util.List;

import javax.naming.directory.SearchResult;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.pip.PIPEngine;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.std.pip.engines.ConfigurableResolver;

/**
 * LDAPResolver is the interface used by the {@link LDAPEngine} to convert a request for a XACML attribute
 * into an LDAP query string, including retrieving any required attributes needed to construct the query
 * string, and convert the response into a collection of {@link org.apache.openaz.xacml.api.Attribute}s.
 */
public interface LDAPResolver extends ConfigurableResolver {
    /**
     * Gets the base <code>String</code> to be used in the <code>search</code> method of a
     * {@link javax.naming.directory.DirectoryContext}.
     *
     * @param pipEngine the {@link org.apache.openaz.xacml.api.pip.PIPEngine} making the request
     * @param pipRequest the <code>PIPRequest</code> to convert
     * @param pipFinder the {@link org.apache.openaz.xacml.api.pip.PIPFinder} to use when resolving required
     *            attributes
     * @return the base <code>String</code> or null if the <code>PIPRequest</code> cannot be satisfied by this
     *         <code>LDAPResolver</code>
     */
    String getBase(PIPEngine pipEngine, PIPRequest pipRequest, PIPFinder pipFinder)
        throws PIPException;

    /**
     * Converts the given <code>PIPRequest</code> into an LDAP filter string to use in the <code>search</code>
     * method of a {@link javax.naming.directory.DirectoryContext}.
     *
     * @param pipEngine the <code>PIPEngine</code> making the request
     * @param pipRequest the <code>PIPRequest</code> to convert
     * @param pipFinder the <code>PIPFinder</code> to use when resolving required attributes
     * @return the filter string to use or null if the given <code>PIPRequest</code> cannot be satisfied by
     *         this <code>LDAPResolver</code>
     * @throws org.apache.openaz.xacml.api.pip.PIPException if there is an error retrieving any required
     *             attributes
     */
    String getFilterString(PIPEngine pipEngine, PIPRequest pipRequest, PIPFinder pipFinder)
        throws PIPException;

    /**
     * Converts a {@link javax.naming.directory.SearchResult} into a <code>List</code> of
     * {@link org.apache.openaz.xacml.api.Attribute}s.
     *
     * @param searchResult the <code>SearchResult</code> to convert
     * @return a <code>List</code> of <code>Attribute</code>s or null if the <code>SearchResult</code> connot
     *         be converted.
     * @throws org.apache.openaz.xacml.api.pip.PIPException if there is an error decoding the
     *             <code>SearchResult</code>
     */
    List<Attribute> decodeResult(SearchResult searchResult) throws PIPException;

}
