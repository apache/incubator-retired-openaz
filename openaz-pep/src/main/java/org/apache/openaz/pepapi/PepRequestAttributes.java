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

package org.apache.openaz.pepapi;

import java.net.URI;
import java.util.Date;

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.RequestAttributes;

/**
 * Convenient wrapper around a RequestAttributes{@link org.apache.openaz.xacml.api.RequestAttributes} instance,
 * representing a collection of request attributes that belong to a particular category.
 */
public interface PepRequestAttributes {

    /**
     * Returns an Indentifier representing the attribute category that the PepRequestAttributes encapsulates
     *
     * @return Identifier
     */
    Identifier getCategory();

    /**
     * Returns an id representing the xml:id
     *
     * @return Identifier
     */
    String getId();

    /**
     * Creates and adds an attribute with the name as the AttributeId, Date array elements as
     * AttributeValue(s) into the underlying attribute collection. The attribute will NOT be returned by the
     * PDP in the response after request evaluation.
     *
     * @param name a string with a name to be used as AttributeId
     * @param values a Date array to be used as AttributeValue(s)
     * @throws IllegalArgumentException if the array is null
     */
    void addAttribute(String name, Date... values);

    /**
     * Creates and adds an attribute with the name as the AttributeId, String array elements as
     * AttributeValue(s) into the underlying attribute collection. The attribute will NOT be returned by the
     * PDP in the response after request evaluation.
     *
     * @param name a string with a name to be used as AttributeId
     * @param values a String array to be used as AttributeValue(s)
     * @throws IllegalArgumentException if the array is null
     */
    void addAttribute(String name, String... values);

    /**
     * Creates and adds an attribute with the name as the AttributeId, Integer array elements as
     * AttributeValue(s) into the underlying attribute collection. The attribute will NOT be returned by the
     * PDP in the response after request evaluation.
     *
     * @param name a string with a name to be used as AttributeId
     * @param values an Integer array to be used as AttributeValue(s)
     * @throws IllegalArgumentException if the array is null
     */
    void addAttribute(String name, Integer... values);

    /**
     * Creates and adds an attribute with the name as the AttributeId, Boolean array elements as
     * AttributeValue(s) into the underlying attribute collection. The attribute will NOT be returned by the
     * PDP in the response after request evaluation.
     *
     * @param name a string with a name to be used as AttributeId
     * @param values a Boolean array to be used as AttributeValue(s)
     * @throws IllegalArgumentException if the array is null
     */
    void addAttribute(String name, Boolean... values);

    /**
     * Creates and adds an attribute with the name as the AttributeId, Long array elements as
     * AttributeValue(s) into the underlying attribute collection. The attribute will NOT be returned by the
     * PDP in the response after request evaluation.
     *
     * @param name a string with a name to be used as AttributeId
     * @param values a Long array to be used as AttributeValue(s)
     * @throws IllegalArgumentException if the array is null
     */
    void addAttribute(String name, Long... values);

    /**
     * Creates and adds an attribute with the name as the AttributeId, Double array elements as
     * AttributeValue(s) into the underlying attribute collection. The attribute will NOT be returned by the
     * PDP in the response after request evaluation.
     *
     * @param name a string with a name to be used as AttributeId
     * @param values a Double array to be used as AttributeValue(s)
     * @throws IllegalArgumentException if the array is null
     */
    void addAttribute(String name, Double... values);

    /**
     * Creates and adds an attribute with the name as the AttributeId, URI array elements as AttributeValue(s)
     * into the underlying attribute collection. The attribute will NOT be returned by the PDP in the response
     * after request evaluation.
     *
     * @param name a string AttributeId of the attribute being set
     * @param values a URI array to be used as AttributeValue(s
     * @throws IllegalArgumentException if the array is null
     */
    void addAttribute(String name, URI... values);

    /**
     * @return
     */
    RequestAttributes getWrappedRequestAttributes();

}
