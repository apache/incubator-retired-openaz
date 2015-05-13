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
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.std.pap;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.pap.PDPPIPConfig;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StdPDPPIPConfig implements PDPPIPConfig, Serializable {
    private static final long serialVersionUID = 1L;
    private static Log logger = LogFactory.getLog(StdPDPPIPConfig.class);

    private String id;

    private String name;

    private String description;

    private String classname;

    private Map<String, String> config = new HashMap<String, String>();

    public StdPDPPIPConfig() {

    }

    public StdPDPPIPConfig(String id) {
        this.id = id;
    }

    public StdPDPPIPConfig(String id, String name, String description) {
        this(id);
        this.name = name;
        this.description = description;
    }

    public StdPDPPIPConfig(String id, Properties properties) {
        this(id);
        if (!this.initialize(properties)) {
            throw new IllegalArgumentException("PIP Engine '" + id + "' has no classname property in config");
        }
    }

    public boolean initialize(Properties properties) {
        boolean classnameSeen = false;
        for (Object key : properties.keySet()) {
            if (key.toString().startsWith(this.id + ".")) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Found: " + key);
                }
                if (key.toString().equals(this.id + ".name")) {
                    this.name = properties.getProperty(key.toString());
                } else if (key.toString().equals(this.id + ".description")) {
                    this.description = properties.getProperty(key.toString());
                } else if (key.toString().equals(this.id + ".classname")) {
                    this.classname = properties.getProperty(key.toString());
                    classnameSeen = true;
                }
                // all properties, including the special ones located above, are included in the properties
                // list
                this.config.put(key.toString(), properties.getProperty(key.toString()));
            }
        }
        return classnameSeen;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    @Override
    @JsonIgnore
    public Map<String, String> getConfiguration() {
        return Collections.unmodifiableMap(this.config);
    }

    public void setValues(Map<String, String> config) {
        this.config = config;
    }

    @Override
    @JsonIgnore
    public boolean isConfigured() {
        //
        // TODO
        // Also include this in the JSON I/O if it is a data field rather than calculated
        //
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((classname == null) ? 0 : classname.hashCode());
        result = prime * result + ((config == null) ? 0 : config.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StdPDPPIPConfig other = (StdPDPPIPConfig)obj;
        if (classname == null) {
            if (other.classname != null)
                return false;
        } else if (!classname.equals(other.classname))
            return false;
        if (config == null) {
            if (other.config != null)
                return false;
        } else if (!config.equals(other.config))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "StdPDPPIPConfig [id=" + id + ", name=" + name + ", description=" + description
               + ", classname=" + classname + ", config=" + config + "]";
    }

    //
    // Methods needed for JSON serialization/deserialization
    //

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

}
