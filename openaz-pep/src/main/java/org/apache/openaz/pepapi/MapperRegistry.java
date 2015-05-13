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

/**
 * Container that holds <code>ObjectMapper</code> instances registered with the framework.
 */
public interface MapperRegistry {

    /**
     * Registers the provided ObjectMapper instance
     *
     * @param mapper
     */
    void registerMapper(ObjectMapper mapper);

    /**
     * Registers the provided ObjectMapper instances
     *
     * @param mappers
     */
    void registerMappers(Iterable<? extends ObjectMapper> mappers);

    /**
     * Returns the ObjectMapper instance registered for the given Class.
     *
     * @param clazz
     * @return an ObjectMapper instance
     * @throws org.apache.openaz.pepapi.PepException if no ObjectMapper could be found for class clazz;
     */
    ObjectMapper getMapper(Class<?> clazz);

}
