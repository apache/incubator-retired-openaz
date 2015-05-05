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
 * Converts a Java Class (typically an application Domain Object) into request attributes of some Category.
 * Applications are expected to provide only a single ObjectMapper instance per Domain Type. Typically, there
 * is a one-to-one relationship between the Domain Type and Attribute Category. The interface, however, takes
 * a general approach allowing a Domain Type to be mapped to multiple categories. The conversion for the most
 * part involves obtaining a <code>CategoryAttributes</code> instance for a specific category from the request
 * context and then mapping Object properties as name-value pairs using one of the overloaded
 * <code>setAttribute</code> methods.
 */
public interface ObjectMapper {

    /**
     * Returns a Class that represents the mapped domain type.
     *
     * @return a Class object
     */
    public Class<?> getMappedClass();

    /**
     * Maps Object properties to attributes
     *
     * @param o - an instance of the domain object to be mapped
     * @param pepRequest - the current Request Context
     */
    public void map(Object o, PepRequest pepRequest);

    /**
     * @param mapperRegistry
     */
    public void setMapperRegistry(MapperRegistry mapperRegistry);

    /**
     * @param pepConfig
     */
    public void setPepConfig(PepConfig pepConfig);
}
