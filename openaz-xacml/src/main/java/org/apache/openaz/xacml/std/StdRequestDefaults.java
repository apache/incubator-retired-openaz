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
package org.apache.openaz.xacml.std;

import java.net.URI;

import org.apache.openaz.xacml.api.RequestDefaults;
import org.apache.openaz.xacml.api.XACML;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.RequestDefaults} interface for the XACML
 * RequestDefaults element.
 */
public class StdRequestDefaults implements RequestDefaults {
    private URI xpathVersion;

    public StdRequestDefaults(URI xpathVersionIn) {
        this();
        if (xpathVersionIn != null) {
            this.xpathVersion = xpathVersionIn;
        }
    }

    public StdRequestDefaults() {
        try {
            this.xpathVersion = new URI(XACML.XPATHVERSION_2_0);
        } catch (Exception ex) { //NOPMD

        }
    }

    @Override
    public URI getXPathVersion() {
        return this.xpathVersion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof RequestDefaults)) {
            return false;
        } else {
            RequestDefaults objRequestDefaults = (RequestDefaults)obj;
            return ObjUtil.equalsAllowNull(this.getXPathVersion(), objRequestDefaults.getXPathVersion());
        }
    }
    
    public int hashCode() {
        int result = 17;
        if (getXPathVersion() != null) {
            result = 31 * result + getXPathVersion().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        Object objectToDump;
        if ((objectToDump = this.getXPathVersion()) != null) {
            stringBuilder.append("xpatherVersion=");
            stringBuilder.append(objectToDump.toString());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
