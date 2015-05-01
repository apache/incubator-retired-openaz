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
 * Container that holds <code>ObjectMapper</code> instances registered with the framework.
 *
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
public interface MapperRegistry {

    /**
     * Registers the provided ObjectMapper instance
     *
     * @param mapper
     */
    public void registerMapper(ObjectMapper mapper);

    /**
     * Registers the provided ObjectMapper instances
     *
     * @param mappers
     */
    public void registerMappers(Iterable<? extends ObjectMapper> mappers);

    /**
     * Returns the ObjectMapper instance registered for the given Class.
     *
     * @param clazz
     * @return an ObjectMapper instance
     * @throws org.openliberty.openaz.pepapi.PepException if no ObjectMapper could be found for class clazz;
     */
    public ObjectMapper getMapper(Class<?> clazz);

}
