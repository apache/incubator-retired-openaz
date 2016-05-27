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

import org.apache.openaz.xacml.api.XACML3;

import java.net.URI;

/**
 * Container class that maps attributes to predefined XACML Resource category.
 */
public final class Resource extends CategoryContainer {

    // only java.lang.String or java.net.URI
    private Object id;
    private URI location;

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
     * @param id
     * @return
     */
    public static Resource newInstance(String id) {
        Resource r = newInstance().withId(id);
        r.addAttribute(XACML3.ID_RESOURCE_RESOURCE_ID.stringValue(), id);
        return r;
    }

    /**
     * Creates a new Resource instance containing a single default attribute with the given URI value.
     *
     * @param id
     * @return
     */
    public static Resource newInstance(URI id) {
        Resource r = newInstance().withId(id);
        r.addAttribute(XACML3.ID_RESOURCE_RESOURCE_ID.stringValue(), id);
        return r;
    }

    /**
     * Sets resource id value
     *
     * @return this
     */
    public Resource withId(URI id) {
        this.id = id;
        return this;
    }

    /**
     * Sets resource id value
     *
     * @return this
     */
    public Resource withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Sets resource location
     *
     * @return this
     */
    public Resource withLocation(URI location) {
        addAttribute(XACML3.ID_RESOURCE_RESOURCE_LOCATION.stringValue(), location);
        return this;
    }

    /**
     * Returns the value of the default id attribute
     *
     * @return
     */
    public Object getId() {
        return this.id;
    }

    /**
     * Returns the value of the location attribute
     *
     * @return
     */
    public URI getLocation() {
        return location;
    }

}
