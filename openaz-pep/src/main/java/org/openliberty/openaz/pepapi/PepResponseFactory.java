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

import com.att.research.xacml.api.Result;

/**
 * Factory for creating and configuring <code>PepResponse</code>.
 * <br>
 * This class creates {@link org.openliberty.openaz.pepapi.PepResponse} objects and configures
 * the behavior of how the <code>PepResponse</code> interprets the
 * results from the AzService or any other PDP that is supported
 * by an implementation of PepApi (org.openliberty.openaz.azapi.pep.*).
 * <br>
 * The {@link PepResponseBehavior} that is invoked when
 * {@link org.openliberty.openaz.pepapi.PepResponse#allowed()} is called and the associated status code
 * has been returned by the PDP and is being handled by the PepResponse
 * provider impl, can be configured to be one of:
 * <ul>
 * <li>
 * {@link org.openliberty.openaz.pepapi.PepResponse#allowed()} returns true (PERMIT: {@link PepResponseBehavior#RETURN_YES}),
 * <li>
 * {@link org.openliberty.openaz.pepapi.PepResponse#allowed()} returns false (DENY: {@link PepResponseBehavior#RETURN_NO}),
 * <li>
 * or{@link org.openliberty.openaz.pepapi.PepResponse#allowed()} throws an exception (DENY: {@link PepResponseBehavior#THROW_EXCEPTION}).
 * </ul>
 * <p>
 * In general, a Permit returns true, and a Deny returns false,
 * but there are also other types of returns, including
 * NotApplicable and Indeterminate. The configuration is to
 * specify for each of the 4 xacml-defined conditions, what
 * the behavior will be. i.e. for each of the "special"
 * conditions there is a choice to return either true (Permit),
 * false (Deny), or throw an Exception.
 * <p>
 * In addition, PDP-specific status codes can be specified, such
 * that when the impl detects one of the configured status codes
 * has been returned, then the {@link PepResponseBehavior} configured
 * for that status code will be returned.
 * <p>
 * Finally, a default {@link PepResponseBehavior} may be configured
 * for any status code that has not been explicitly configured
 * or does not have its own default provided by the impl. The
 * default if the statusCode default has not been configured is
 * {@link PepResponseBehavior#THROW_EXCEPTION}.
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public interface PepResponseFactory {

    public PepResponse newPepResponse(Result result);

}
