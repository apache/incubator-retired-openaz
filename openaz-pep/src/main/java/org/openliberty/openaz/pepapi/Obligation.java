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

import java.util.Map;

/**
 * The Obligation interface provides access to an Obligation
 * object implementation that contains a set of zero or more
 * Attributes.
 * <p>
 * The Obligation has an id: {@link #getId()}
 * <p>
 * Each attribute has an id, as well, which are used as the key Strings
 * of the Maps returned by method:
 * <ul>
 * <li>{@link #getAttributeMap()}</li>
 * </ul>
 * Each key String has an associated value, which can be an
 * an array of Objects.
 * <p>
 *
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public interface Obligation {

    /**
     * Return the Id for this Obligation.
     *
     * @return a string containing the Id of this Obligation
     */
    public String getId();

    /**
     * Returns a Map of Obligation Attribute name,object-value-array pairs,
     * indexed by name, where name is the AttributeId and the value
     * is an array of one or more Object values of the "attribute"
     * (where an array with length > 1 indicates a multi-valued attribute).
     * <p>
     * @return a Map of String (AttributeId name), Object array
     * (Attribute values) pairs
     */
    public Map<String, Object[]> getAttributeMap();

}
