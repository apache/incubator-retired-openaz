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

import com.att.research.xacml.api.XACML3;

import java.net.URI;
import java.util.Date;

/**
 * Container class that maps attributes to predefined XACML Resource category.
 *
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
public final class Resource extends CategoryContainer {

    public static final String RESOURCE_ID_KEY = "RESOURCE_ID_KEY";

    private Object resourceIdValue;

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
     * @param resourceIdValue
     * @return
     */
    public static Resource newInstance(String resourceIdValue) {
        Resource r = new Resource();
        r.resourceIdValue = resourceIdValue;
        r.addAttribute(RESOURCE_ID_KEY, resourceIdValue);
        return r;
    }

    /**
     * Creates a new Resource instance containing a single default attribute with the given URI value.
     *
     * @param resourceIdValue
     * @return
     */
    public static Resource newInstance(URI resourceIdValue) {
        Resource r = new Resource();
        r.resourceIdValue = resourceIdValue;
        r.addAttribute(RESOURCE_ID_KEY, resourceIdValue);
        return r;
    }

    /**
     * Creates a new Resource instance containing a single default attribute with the given Long value.
     *
     * @param resourceIdValue
     * @return
     */
    public static Resource newInstance(Long resourceIdValue) {
        Resource r = new Resource();
        r.resourceIdValue = resourceIdValue;
        r.addAttribute(RESOURCE_ID_KEY, resourceIdValue);
        return r;
    }

    /**
     * Creates a new Resource instance containing a single default attribute with the given Double value.
     *
     * @param resourceIdValue
     * @return
     */
    public static Resource newInstance(Double resourceIdValue) {
        Resource r = new Resource();
        r.resourceIdValue = resourceIdValue;
        r.addAttribute(RESOURCE_ID_KEY, resourceIdValue);
        return r;
    }

    /**
     * Creates a new Resource instance containing a single default attribute with the given Boolean value.
     *
     * @param resourceIdValue
     * @return
     */
    public static Resource newInstance(Boolean resourceIdValue) {
        Resource r = new Resource();
        r.resourceIdValue = resourceIdValue;
        r.addAttribute(RESOURCE_ID_KEY, resourceIdValue);
        return r;
    }

    /**
     * Creates a new Resource instance containing a single default attribute with the given <code>java.util.Date</code> value.
     *
     * @param resourceIdValue
     * @return
     */
    public static Resource newInstance(Date resourceIdValue) {
        Resource r = new Resource();
        r.resourceIdValue = resourceIdValue;
        r.addAttribute(RESOURCE_ID_KEY, resourceIdValue);
        return r;
    }

    /**
     * Returns the value of the default resourceIdValue attribute
     *
     * @return
     */
    public Object getResourceIdValue() {
        return resourceIdValue;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("resource-id value : " + resourceIdValue);
        builder.append("\n");
        builder.append(super.toString());
        return builder.toString();
    }
}
