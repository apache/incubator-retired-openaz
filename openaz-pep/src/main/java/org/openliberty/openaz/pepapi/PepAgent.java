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

import java.util.List;

/**
 *
 * Serves as the main entry point into the PepAPI framework. It coordinates authorization request creation, execution and
 * response assemblage. Applications typically work with a single instance of PepAgent which is thread-safe.
 *
 * The <code>decide()</code> method, which provides the most general mechanism for authorization, accepts a collection of application Domain Objects,
 * each with it's own <code>ObjectMapper</code> defined.  The client application thus passes these Domain Objects directly,  <code>decide()</code> uses
 * reflection to determine their type, and then finds a type-specific mapper.
 *
 * This mechanism relies on application defined library of Object Mappers, one for each Domain Object that the client
 * program expects to use in an authorization call.
 *
 * It is important to note that Java Primitives/Wrappers and other standard types(except Collections) are not supported out of the box.
 * This is primarily because there is no sensible default mapping between a Java Standard Type and a XACML category and hence
 * it's impossible for the framework to make a mapping decision at runtime. However, client applications may enforce their own rules as
 * they see fit by providing Custom ObjectMapper(s) for these types.
 *
 * <code>simpleDecide()</code> method addresses the simplest of use cases where attributes involved are simple userId, actionId and resourceId Strings.
 *
 * <code>bulkDecide()</code> provides an abstraction for a MultiRequest, where in client applications may provide collection of Domain Object
 * bindings/associations each of which map to individual requests. The method separates out Domain Object associations with multiple cardinality
 * from the ones shared across requests.
 *
 * Thus, in a <code>bulkDecide()</code> call applications provide two sets of arguments:
 *              - a List of Domain Object bindings, each of which map to an individual request.
 *              - a collection of common Domain Objects shared across all requests.
 *
 * Specific AzService implementations(PDP Providers) may implement bulkDecide() as a XACML MultiRequest (Note: XACML Multi Decision Profile is optional)
 * or as individual requests executed iteratively.
 *
 *
 */
public interface PepAgent {


    /**
     * Returns a authorization decision for the given subjectId, actionId,
     * resourceId Strings.
     *
     * @param subjectId
     * @param actionId
     * @param resourceId
     * @return
     * @throws PepException
     *                  - if an appropriate ObjectMapper cannot be found.
     *                  - if the underlying AzService instance/PDP throws an exception
     *                  - if the PepAgent is configured to throw PepExceptions for "Indeterminate" or "Not Applicable" decisions.
     * @throws IllegalArgumentException if any of the arguments are null
     */
    public PepResponse simpleDecide(String subjectId, String actionId, String resourceId);

    /**
     * Returns an authorization decision for the given collection of Domain Objects each with it's own
     * ObjectMapper instance. Java Primitives/Wrappers or other Standard types (except Collections) are not supported
     * out of the box. However, client applications may enforce their own rules as they see fit by providing Custom ObjectMapper(s)
     * for these types.
     *
     * @param objects
     * @return
     * @throws PepException
     *                  - if an appropriate ObjectMapper cannot be found.
     *                  - if the underlying AzService instance/PDP throws an exception
     *                  - if the PepAgent is configured to throw PepException for "Indeterminate" or "Not Applicable" decisions.
     * @throws IllegalArgumentException if any of the arguments are null
     */
    public PepResponse decide(Object... objects);

    /**
     * Returns a PepResponse instance representing a collection of decisions, each of which corresponds to
     * an association. Each association represents a specific instance of Domain Object binding. A typical example for an association
     * would be an Action-Resource pair.
     *
     * @param associations a list of Domain Object bindings, each of which maps to a individual Request.
     * @param objects a collection of common Domain Objects shared across all Requests.
     * @return
     * @throws PepException
     *                  - if an appropriate ObjectMapper cannot be found.
     *                  - if the underlying AzService instance/PDP throws an exception
     *                  - if the PepAgent is configured to throw PepExceptions for "Indeterminate" or "Not Applicable" decisions.
     * @throws IllegalArgumentException if any of the arguments are null
     */
    public List<PepResponse> bulkDecide(List<?> associations, Object... objects);

}
