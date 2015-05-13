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
 * VersionMatch is the interface that objects implementing references to {@link Version} objects must
 * implement.
 */
public interface VersionMatch {
    /**
     * Gets the <code>String</code> representation of the <code>Version</code> matching pattern.
     *
     * @return the <code>String</code> representation of the <code>Version</code> matching pattern.
     */
    String getVersionMatch();

    /**
     * Determines if the pattern in this <code>VersionMatch</code> matches the given <code>Version</code>
     * based on the given comparison code. Comparison code values are: 0 - match only if version numbers match
     * the pattern -1 - match if the version numbers <= the pattern 1 - match if the version numbers >= the
     * pattern Wildcard values are considered to match any comparison code
     *
     * @param version the <code>Version</code> to match against
     * @param cmp integer comparision code
     * @return true if this pattern matches the given <code>Version</code> else false
     */
    boolean match(Version version, int cmp);
}
