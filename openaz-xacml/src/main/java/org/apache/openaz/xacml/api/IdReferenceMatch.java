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
 * Defines the API for objects that represent XACML PolicyIdReference or PolicySetIdReference elements with
 * exact, earliest, and latest version matching.
 */
public interface IdReferenceMatch {
    /**
     * Returns the {@link Identifier} representing the XACML PolicyId or PolicySetId that is referenced by
     * this <code>IdReference</code>.
     *
     * @return the <code>Identifier</code> representing the XACML PolicyId or PolicySetId that is referenced
     *         by this <code>IdReference</code>.
     */
    Identifier getId();

    /**
     * Gets the {@link VersionMatch} representing a full or partial match against a XACML Version string.
     *
     * @return the <code>VersionMatch</code> representing a full or partial match against a XACML Version
     *         string.
     */
    VersionMatch getVersion();

    /**
     * Gets the {@link VersionMatch} representing a full or partial match against the earliest XACML Version
     * string.
     *
     * @return the <code>VersionMatch</code> representing a full or partial match against the earliest XACML
     *         Version string.
     */
    VersionMatch getEarliestVersion();

    /**
     * Gets the {@link VersionMatch} representing a full or partial match against the latest XACML Version
     * string.
     *
     * @return the <code>VersionMatch</code> representing a full or partial match against the latest XACML
     *         Version string.
     */
    VersionMatch getLatestVersion();

    /**
     * {@inheritDoc} Implementations of the <code>IdReferenceMatch</code> interface must override the
     * <code>equals</code> method with the following semantics: Two <code>IdReferenceMatch</code> objects (
     * <code>i1</code> and <code>i2</code>) are equal if: {@code i1.getId().equals(i2.getId())} AND
     * {@code i1.getVersion() == null && i2.getVersion() == null} OR
     * {@code i1.getVersion().equals(i2.getVersion())}
     * {@code i1.getEarliestVersion() == null && i2.getEarliestVersion() == null} OR
     * {@code i1.getEarliestVersion().equals(i2.getEarliestVersion())} AND
     * {@code i1.getLatestVersion() == null && i2.getLatestVersion() == null} OR
     * {@code i1.getLatestVersion().equals(i2.getLatestVersion())}
     */
    @Override
    boolean equals(Object obj);
}
