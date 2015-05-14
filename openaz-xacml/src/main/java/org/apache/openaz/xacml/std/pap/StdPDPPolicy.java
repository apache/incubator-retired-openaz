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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.pap.PAPException;
import org.apache.openaz.xacml.api.pap.PDPPolicy;
import org.apache.openaz.xacml.util.XACMLPolicyScanner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class StdPDPPolicy implements PDPPolicy, Serializable {
    private static final long serialVersionUID = 1L;
    private static Log logger = LogFactory.getLog(StdPDPPolicy.class);

    private String id = null;

    private String name = null;

    private String policyId = null;

    private String description = null;

    private int[] version = null;

    private boolean isRoot = false;

    private boolean isValid = false;

    private URI location = null;

    public StdPDPPolicy(String id, boolean isRoot) {
        this.id = id;
        this.isRoot = isRoot;
    }

    public StdPDPPolicy(String id, boolean isRoot, String name) {
        this(id, isRoot);
        this.name = name;
    }

    public StdPDPPolicy(String id, boolean isRoot, String name, URI location) throws IOException {
        this(id, isRoot);
        this.name = name;
        this.location = location;
        //
        // Read the policy data
        //
        String theID = this.readPolicyData();
        if (this.id == null) {
            this.id = theID;
        }
    }

    public StdPDPPolicy(String id, boolean isRoot, URI location, Properties properties) throws IOException {
        this(id, isRoot);
        this.location = location;
        //
        // Read the policy data
        //
        this.readPolicyData();
        //
        // See if there's a name
        //
        for (Object key : properties.keySet()) {
            if (key.toString().equals(id + ".name")) {
                this.name = properties.getProperty(key.toString());
                break;
            }
        }
    }

    private String readPolicyData() throws IOException {
        //
        // Extract XACML policy information
        //
        URL url = this.location.toURL();
        Object rootElement = XACMLPolicyScanner.readPolicy(url.openStream());
        if (rootElement == null
            || !(rootElement instanceof PolicySetType) && !(rootElement instanceof PolicyType)) {
            logger.warn("No root policy element in URI: " + this.location.toString() + " : " + rootElement);
            this.isValid = false;
        } else {
            this.version = versionStringToArray(XACMLPolicyScanner.getVersion(rootElement));
            if (rootElement instanceof PolicySetType) {
                this.policyId = ((PolicySetType)rootElement).getPolicySetId();
                this.description = ((PolicySetType)rootElement).getDescription();
                this.isValid = true;
                this.version = versionStringToArray(((PolicySetType)rootElement).getVersion());
            } else if (rootElement instanceof PolicyType) {
                this.policyId = ((PolicyType)rootElement).getPolicyId();
                this.description = ((PolicyType)rootElement).getDescription();
                this.version = versionStringToArray(((PolicyType)rootElement).getVersion());
                this.isValid = true;
            } else {
                logger.error("Unknown root element: " + rootElement.getClass().getCanonicalName());
            }
        }
        if (this.policyId != null) {
            ArrayList<String> foo = Lists.newArrayList(Splitter.on(':').split(this.policyId));
            if (!foo.isEmpty()) {
                return foo.get(foo.size() - 1);
            }
        }
        return null;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPolicyId() {
        return this.policyId;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getVersion() {
        return versionArrayToString(this.version);
    }

    @Override
    @JsonIgnore
    public int[] getVersionInts() {
        return version;
    }

    @Override
    public boolean isRoot() {
        return this.isRoot;
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    @JsonIgnore
    public InputStream getStream() throws PAPException, IOException {
        try {
            if (this.location != null) {
                URL url = this.location.toURL();
                return url.openStream();
            }
            return null;
        } catch (FileNotFoundException e) {
            throw new PAPException(e);
        }
    }

    @Override
    public URI getLocation() throws PAPException {
        return this.location;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((policyId == null) ? 0 : policyId.hashCode());
        result = prime * result;
        if (version != null) {
            for (int i = 0; i < version.length; i++) {
                result += version[i];
            }
        }
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
        StdPDPPolicy other = (StdPDPPolicy)obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (policyId == null) {
            if (other.policyId != null)
                return false;
        } else if (!policyId.equals(other.policyId))
            return false;
        if (version != other.version)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "StdPDPPolicy [id=" + id + ", name=" + name + ", policyId=" + policyId + ", description="
               + description + ", version=" + this.getVersion() + ", isRoot=" + isRoot + ", isValid="
               + isValid + ", location=" + location + "]";
    }

    /**
     * Given a version string consisting of integers with dots between them, convert it into an array of ints.
     *
     * @param version
     * @return
     * @throws NumberFormatException
     */
    public static int[] versionStringToArray(String version) throws NumberFormatException {
        if (version == null || version.length() == 0) {
            return new int[0];
        }
        String[] stringArray = version.split("\\.");
        int[] resultArray = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            resultArray[i] = Integer.parseInt(stringArray[i]);
        }
        return resultArray;
    }

    /**
     * Given an array representing a version, create the corresponding dot-separated string.
     *
     * @param array
     * @return
     */
    public static String versionArrayToString(int[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        String versionString = "";
        if (array.length > 0) {
            versionString = "" + array[0];
            for (int i = 1; i < array.length; i++) {
                versionString += "." + array[i];
            }
        }
        return versionString;
    }

    //
    // Methods needed for JSON Deserialization
    //
    public StdPDPPolicy() {
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(String version) {
        this.version = versionStringToArray(version);
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public void setLocation(URI location) {
        this.location = location;
    }

}
