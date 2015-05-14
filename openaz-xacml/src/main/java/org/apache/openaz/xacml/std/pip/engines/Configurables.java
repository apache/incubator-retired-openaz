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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.pip.StdPIPRequest;

/**
 */
public class Configurables {

    public static final String PROP_ID = "id";
    public static final String PROP_DATATYPE = "datatype";
    public static final String PROP_CATEGORY = "category";
    public static final String PROP_ISSUER = "issuer";

    private static Log logger = LogFactory.getLog(Configurables.class);

    public static List<PIPRequest> getPIPRequestList(String prefix, String name, Properties properties,
                                                     String defaultIssuer) throws PIPException {
        String idxProp = properties.getProperty(prefix + "." + name);
        if (null == idxProp) {
            throw new PIPException("PIPRequest list definition not found (entry '" + (prefix + "." + name)
                                   + "')");
        }
        ArrayList<PIPRequest> list = new ArrayList<PIPRequest>();
        if (idxProp.length() == 0) {
            return list;
        }
        try (Scanner s = new Scanner(idxProp)) {
            s.useDelimiter("[,]");
            while (s.hasNextInt()) {
                int idx = s.nextInt();
                PIPRequest request = getPIPRequest(prefix + "." + name + "." + idx, properties, defaultIssuer);
                if (null == request)
                    throw new PIPException("PIPRequest list defines element " + idx
                                           + " but element specification is missing");
                else {
                    // list.ensureCapacity(idx);
                    // list.set(idx-1, request);
                    list.add(request);
                }
            }
        } catch (InputMismatchException imx) {
            throw new PIPException(
                                   "PIPRequest list specification contains non-readable position specification");
        }

        return list;
    }

    public static void setPIPRequestList(List<PIPRequest> list, String prefix, String name,
                                         Properties properties) throws PIPException {
        StringBuilder spec = null;
        int i = 1;
        try {
            for (PIPRequest req : list) {
                if (req != null) {
                    setPIPRequest(req, prefix + "." + name + "." + i, properties);
                    if (null == spec) {
                        spec = new StringBuilder().append(String.valueOf(i));
                    } else {
                        spec.append(",").append(String.valueOf(i));
                    }
                }
                i++;
            }
        } catch (PIPException pipx) {
            throw new PIPException("Failed to set request " + list.get(i), pipx);
        } finally {
            if (spec != null) {
                properties.setProperty(prefix + "." + name, spec.toString());
            }
        }
    }

    public static Map<String, PIPRequest> getPIPRequestMap(String prefix, String name, Properties properties,
                                                           String defaultIssuer) throws PIPException {

        String idxProp = properties.getProperty(prefix + "." + name);
        if (null == idxProp) {
            throw new PIPException("PIPRequest map definition not found (entry '" + (prefix + "." + name)
                                   + "')");
        }
        HashMap<String, PIPRequest> map = new HashMap<String, PIPRequest>();
        if (idxProp.length() == 0) {
            return map;
        }
        try (Scanner s = new Scanner(idxProp)) {
            s.useDelimiter("[,]");
            while (s.hasNext()) {
                String key = s.next();
                PIPRequest request = getPIPRequest(prefix + "." + name + "." + key, properties, defaultIssuer);
                if (null == request)
                    throw new PIPException("PIPRequest list defines element " + key
                                           + " but element specification is missing");
                else {
                    map.put(key, request);
                }
            }
        } catch (InputMismatchException imx) {
            throw new PIPException("PIPRequest list specification contains non-readable key specification");
        }

        return map;
    }

    public static void setPIPRequestMap(Map<String, PIPRequest> map, String prefix, String name,
                                        Properties properties) throws PIPException {
        StringBuilder spec = null;
        PIPRequest req = null;
        try {
            for (Map.Entry<String, PIPRequest> entry : map.entrySet()) {
                setPIPRequest(entry.getValue(), prefix + "." + name + "." + entry.getKey(), properties);
                if (null == spec) {
                    spec = new StringBuilder().append(entry.getKey());
                } else {
                    spec.append(",").append(entry.getKey());
                }
            }
        } catch (PIPException pipx) {
            throw new PIPException("Failed to set request " + req, pipx);
        } finally {
            // keep the properties consistent even if we failed half-way ..
            if (null != spec) {
                properties.setProperty(prefix + "." + name, spec.toString());
            }
        }
    }

    public static PIPRequest getPIPRequest(String idPrefix, Properties properties, String defaultIssuer)
        throws PIPException {
        String stringProp = idPrefix + "." + PROP_ID;
        String attributeId = properties.getProperty(stringProp);
        stringProp = idPrefix + "." + PROP_DATATYPE;
        String dataTypeId = properties.getProperty(stringProp);
        stringProp = idPrefix + "." + PROP_CATEGORY;
        String categoryId = properties.getProperty(stringProp);
        stringProp = idPrefix + "." + PROP_ISSUER;
        String issuer = properties.getProperty(stringProp);

        if (issuer == null) {
            issuer = defaultIssuer;
        }
        //
        // if none of these properties are present there was no intent to define the
        // given attribute.
        //
        if (dataTypeId == null && attributeId == null && categoryId == null) {
            return null;
        }

        // no that we know there was intent, check that mandatory information is
        // present
        if (attributeId == null || attributeId.length() == 0
            || dataTypeId == null || dataTypeId.length() == 0
            || categoryId == null || categoryId.length() == 0) {
            StringBuilder errMsg = new StringBuilder();
            errMsg.append("Incomplete PIPRequest specification, missing property (").append("attributeId=")
                .append(attributeId).append(",").append("dataTypeId=").append(dataTypeId).append(",")
                .append("categoryId=").append(categoryId).append(")");
            logger.error(errMsg.toString());
            throw new PIPException(errMsg.toString());
        }

        return new StdPIPRequest(new IdentifierImpl(categoryId), new IdentifierImpl(attributeId),
                                 new IdentifierImpl(dataTypeId), issuer);
    }

    public static void setPIPRequest(PIPRequest pipRequest, String prefix, Properties properties)
        throws PIPException {
        properties.setProperty(prefix + ".id", pipRequest.getAttributeId().stringValue());
        properties.setProperty(prefix + ".datatype", pipRequest.getDataTypeId().stringValue());
        properties.setProperty(prefix + ".category", pipRequest.getCategory().stringValue());
        if (pipRequest.getIssuer() != null) {
            properties.setProperty(prefix + ".issuer", pipRequest.getIssuer());
        }
    }

}
