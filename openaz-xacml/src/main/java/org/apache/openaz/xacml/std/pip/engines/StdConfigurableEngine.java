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

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.std.pip.engines;

import java.util.Properties;

import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPResponse;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * StdConfigurableEngine implements the {@link ConfigurableEngine} interface to automatically process the
 * standard name, description, and issuer properties.
 */
public abstract class StdConfigurableEngine implements ConfigurableEngine {
    public static final String PROP_NAME = "name";
    public static final String PROP_DESCRIPTION = "description";
    public static final String PROP_ISSUER = "issuer";
    public static final String PROP_CACHESPEC = "cacheSpec";

    private String name;
    private String description;
    private String issuer;
    private Cache<String, PIPResponse> cache;

    public StdConfigurableEngine() {
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String nameIn) {
        this.name = nameIn;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String descriptionIn) {
        this.description = descriptionIn;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public void setIssuer(String issuerIn) {
        this.issuer = issuerIn;
    }

    public Cache<String, PIPResponse> getCache() {
        return cache;
    }

    public void setCache(Cache<String, PIPResponse> cache) {
        this.cache = cache;
    }

    @Override
    public void configure(String id, Properties properties) throws PIPException {
        this.setName(properties.getProperty(id + "." + PROP_NAME, id));
        this.setDescription(properties.getProperty(id + "." + PROP_DESCRIPTION));
        this.setIssuer(properties.getProperty(id + "." + PROP_ISSUER));
        /*
         * Configure the cache IF it is defined
         */
        if (properties.getProperty(id + "." + PROP_CACHESPEC) != null) {
            this.cache = CacheBuilder.from(properties.getProperty(id + "." + PROP_CACHESPEC)).build();
        }
    }

}
