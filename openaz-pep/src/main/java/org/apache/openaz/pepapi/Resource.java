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

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;

import java.net.URI;

/**
 * Container class that maps attributes to predefined XACML Resource category.
 */
public final class Resource extends CategoryContainer {

    public static final Identifier DEFAULT_IDENTIFIER_ID = XACML3.ID_RESOURCE_RESOURCE_ID;
    public static final Identifier DEFAULT_IDENTIFIER_LOCATION = XACML3.ID_RESOURCE_RESOURCE_LOCATION;

    private Object idValue; // only java.lang.String or java.net.URI
    private URI locationValue;

    private Resource() {
        super(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE);
    }

    /**
     * Creates a new Resource instance
     *
     * @return
     */
    public static Resource newInstance() {
        return new Resource();
    }

    /**
     * Creates a new Resource instance containing a single default attribute with the given String value.
     *
     * @param idValue
     * @return
     */
    public static Resource newInstance(String idValue) {
        return newInstance().withId(idValue);
    }

    /**
     * Creates a new Resource instance containing a single default attribute with the given URI value.
     *
     * @param idValue
     * @return
     */
    public static Resource newInstance(URI idValue) {
        return newInstance().withId(idValue);
    }

    /**
     * Sets resource id value
     *
     * @param idValue
     * @return this
     */
    public Resource withId(URI idValue) {
        this.idValue = idValue;
        addAttribute(DEFAULT_IDENTIFIER_ID.stringValue(), idValue);
        return this;
    }

    /**
     * Sets resource id value
     *
     * @param id
     * @param idValue
     * @return this
     */
    public Resource withId(Identifier id, URI idValue) {
        this.idValue = idValue;
        addAttribute(id.stringValue(), idValue);
        return this;
    }

    /**
     * Sets resource id value
     *
     * @param idValue
     * @return this
     */
    public Resource withId(String idValue) {
        this.idValue = idValue;
        addAttribute(DEFAULT_IDENTIFIER_ID.stringValue(), idValue);
        return this;
    }

    /**
     * Sets resource id value
     *
     * @param id
     * @param idValue
     * @return this
     */
    public Resource withId(Identifier id, String idValue) {
        this.idValue = idValue;
        addAttribute(id.stringValue(), idValue);
        return this;
    }

    /**
     * Sets resource location
     *
     * @param locationValue
     * @return this
     */
    public Resource withLocation(URI locationValue) {
        this.locationValue = locationValue;
        addAttribute(DEFAULT_IDENTIFIER_LOCATION.stringValue(), locationValue);
        return this;
    }

    /**
     * Sets resource location
     *
     * @param id
     * @param locationValue
     * @return this
     */
    public Resource withLocation(Identifier id, URI locationValue) {
        this.locationValue = locationValue;
        addAttribute(id.stringValue(), locationValue);
        return this;
    }

    /**
     * Returns the value of the id attribute
     *
     * @return
     */
    public Object getId() {
        return this.idValue;
    }

    /**
     * Returns the value of the location attribute
     *
     * @return
     */
    public URI getLocation() {
        return locationValue;
    }

}
