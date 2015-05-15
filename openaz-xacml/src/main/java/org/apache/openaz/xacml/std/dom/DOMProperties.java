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
package org.apache.openaz.xacml.std.dom;

import java.util.HashMap;
import java.util.Map;

import org.apache.openaz.xacml.util.XACMLProperties;

/**
 * DOMProperties contains utilities for determining the properties for parsing XACML documents with a DOM
 * parser.
 */
public class DOMProperties {
    private static final String PROP_LENIENT = "xacml.dom.lenient";
    private static final String PROP_EXCEPTIONS = "xacml.dom.exceptions";

    private static Map<String, Object> cachedProperties = new HashMap<String, Object>();

    protected static String getSourceProperty(String propertyName) {
        String result = System.getProperty(propertyName);
        if (result == null) {
            try {
                result = XACMLProperties.getProperties().getProperty(propertyName);
            } catch (Exception ex) { //NOPMD

            }
        }
        return result;
    }

    protected static String getStringProperty(String propertyName) {
        Object cachedProperty = cachedProperties.get(propertyName);
        if (cachedProperty == null) {
            cachedProperty = getSourceProperty(propertyName);
            cachedProperties.put(propertyName, cachedProperty);
        }
        return cachedProperty instanceof String ? (String)cachedProperty : cachedProperty.toString();
    }

    protected static Boolean getBooleanProperty(String propertyName) {
        Object cachedProperty = cachedProperties.get(propertyName);
        if (cachedProperty == null) {
            String stringProperty = getSourceProperty(propertyName);
            if (stringProperty != null) {
                cachedProperty = Boolean.parseBoolean(stringProperty);
                cachedProperties.put(propertyName, cachedProperty);
            }
        }
        if (cachedProperty == null || cachedProperty instanceof Boolean) {
            return (Boolean)cachedProperty;
        } else {
            return null;
        }
    }

    protected DOMProperties() {
    }

    public static boolean isLenient() {
        Boolean booleanIsLenient = getBooleanProperty(PROP_LENIENT);
        return (booleanIsLenient == null ? false : booleanIsLenient.booleanValue());
    }

    public static void setLenient(boolean b) {
        cachedProperties.put(PROP_LENIENT, (b ? Boolean.TRUE : Boolean.FALSE));
    }

    public static boolean throwsExceptions() {
        Boolean booleanThrowsExceptions = getBooleanProperty(PROP_EXCEPTIONS);
        return booleanThrowsExceptions == null ? true : booleanThrowsExceptions.booleanValue();
    }

    public static void setThrowsExceptions(boolean b) {
        cachedProperties.put(PROP_EXCEPTIONS, (b ? Boolean.TRUE : Boolean.FALSE));
    }

}
