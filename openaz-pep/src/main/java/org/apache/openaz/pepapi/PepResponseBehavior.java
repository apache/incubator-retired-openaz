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

package org.openliberty.openaz.pepapi;

/**
 * This enum provides the options that can be set using the
 * {@link org.openliberty.openaz.pepapi.PepResponseFactory} to determine the behavior when
 * {@link org.openliberty.openaz.pepapi.PepResponse#allowed()} is called AND the
 * decision is either Indeterminate or NotApplicable.
 *
 *
 */
public enum PepResponseBehavior {

    /** The behavior is to allow (Permit) access by returning true when the condition for which this behavior is assigned occurs  */
    RETURN_YES,

    /** The behavior is to disallow (Deny) access by returning false when the condition for which this behavior is assigned occurs  */
    RETURN_NO,

    /** The behavior is to disallow (Deny) access by throwing a PepException when the condition for which this behavior is assigned occurs  */
    THROW_EXCEPTION
}
