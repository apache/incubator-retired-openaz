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
package org.apache.openaz.xacml.std.annotations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.*;
import org.apache.openaz.xacml.std.*;
import org.apache.openaz.xacml.std.datatypes.*;
import org.apache.openaz.xacml.util.AttributeUtils;
import org.apache.openaz.xacml.util.FactoryException;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathExpression;

import javax.security.auth.x500.X500Principal;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

public class RequestParser {
    private static Log logger = LogFactory.getLog(RequestParser.class);

    //
    // Create our data type factory object. We could make this static
    //
    protected static DataTypeFactory dataTypeFactory = null;

    protected static synchronized DataTypeFactory getDataTypeFactory() {
        try {
            if (dataTypeFactory != null) {
                return dataTypeFactory;
            }
            dataTypeFactory = DataTypeFactory.newInstance();
            if (dataTypeFactory == null) {
                logger.error("Could not create data type factory");
            }
        } catch (FactoryException e) {
            logger.error("Can't get Data type Factory: " + e.getLocalizedMessage());
        }
        return dataTypeFactory;
    }

    public static Request parseRequest(Object obj) throws IllegalArgumentException, IllegalAccessException,
        DataTypeException {
        //
        // Our returned object
        //
        StdMutableRequest stdMutableRequest = new StdMutableRequest();
        //
        // Our collection of attribute values
        //
        List<StdMutableRequestAttributes> attributes = new ArrayList<StdMutableRequestAttributes>();
        //
        // Get overall XACML request annotation fields
        //
        XACMLRequest requestAnnotation = obj.getClass().getAnnotation(XACMLRequest.class);
        //
        // Add these annotations into the request
        //
        stdMutableRequest.setReturnPolicyIdList(requestAnnotation.ReturnPolicyIdList());
        stdMutableRequest.setCombinedDecision(requestAnnotation.CombinedDecision());
        if (!requestAnnotation.Defaults().equals(XACMLRequest.nullString)) {
            stdMutableRequest.setRequestDefaults(new StdRequestDefaults(URI.create(requestAnnotation
                .Defaults())));
        }
        //
        // Check for multi-request
        //
        XACMLMultiRequest multi = requestAnnotation.multiRequest();
        if (multi != null) {
            for (XACMLRequestReference reference : multi.values()) {
                Collection<RequestAttributesReference> refs = new ArrayList<RequestAttributesReference>();
                for (String id : reference.values()) {
                    refs.add(new StdRequestAttributesReference(id));
                }
                if (!refs.isEmpty()) {
                    stdMutableRequest.add(new StdRequestReference(refs));
                }
            }
        }
        //
        // Iterate all the fields in the object
        //
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Field: " + field);
            }
            XACMLSubject subject = field.getAnnotation(XACMLSubject.class);
            if (subject != null) {
                RequestParser.addAttribute(attributes,
                                           new IdentifierImpl(subject.category()),
                                           new IdentifierImpl(subject.attributeId()),
                                           subject.includeInResults(),
                                           (subject.datatype().equals(XACMLRequest.nullString)
                                               ? null : subject.datatype()),
                                           (subject.issuer().equals(XACMLRequest.nullString) ? null : subject
                                               .issuer()), (subject.id().equals(XACMLRequest.nullString)
                                               ? null : subject.id()), field, obj);
            }
            XACMLAction action = field.getAnnotation(XACMLAction.class);
            if (action != null) {
                RequestParser
                    .addAttribute(attributes,
                                  new IdentifierImpl(action.category()),
                                  new IdentifierImpl(action.attributeId()),
                                  action.includeInResults(),
                                  (action.datatype().equals(XACMLRequest.nullString) ? null : action
                                      .datatype()), (action.issuer().equals(XACMLRequest.nullString)
                                      ? null : action.issuer()), (action.id().equals(XACMLRequest.nullString)
                                      ? null : action.id()), field, obj);
            }
            XACMLResource resource = field.getAnnotation(XACMLResource.class);
            if (resource != null) {
                RequestParser.addAttribute(attributes,
                                           new IdentifierImpl(resource.category()),
                                           new IdentifierImpl(resource.attributeId()),
                                           resource.includeInResults(),
                                           (resource.datatype().equals(XACMLRequest.nullString)
                                               ? null : resource.datatype()),
                                           (resource.issuer().equals(XACMLRequest.nullString)
                                               ? null : resource.issuer()),
                                           (resource.id().equals(XACMLRequest.nullString) ? null : resource
                                               .id()), field, obj);
            }
            XACMLEnvironment environment = field.getAnnotation(XACMLEnvironment.class);
            if (environment != null) {
                RequestParser.addAttribute(attributes, new IdentifierImpl(environment.category()),
                                           new IdentifierImpl(environment.attributeId()), environment
                                               .includeInResults(),
                                           (environment.datatype().equals(XACMLRequest.nullString)
                                               ? null : environment.datatype()),
                                           (environment.issuer().equals(XACMLRequest.nullString)
                                               ? null : environment.issuer()),
                                           (environment.id().equals(XACMLRequest.nullString)
                                               ? null : environment.id()), field, obj);
            }
            XACMLAttribute attribute = field.getAnnotation(XACMLAttribute.class);
            if (attribute != null) {
                RequestParser.addAttribute(attributes,
                                           new IdentifierImpl(attribute.category()),
                                           new IdentifierImpl(attribute.attributeId()),
                                           attribute.includeInResults(),
                                           (attribute.datatype().equals(XACMLRequest.nullString)
                                               ? null : attribute.datatype()),
                                           (attribute.issuer().equals(XACMLRequest.nullString)
                                               ? null : attribute.issuer()),
                                           (attribute.id().equals(XACMLRequest.nullString) ? null : attribute
                                               .id()), field, obj);
            }
        }
        //
        // Add in all the attributes
        //
        for (StdMutableRequestAttributes a : attributes) {
            stdMutableRequest.add(a);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(AttributeUtils.prettyPrint(stdMutableRequest));
        }
        return stdMutableRequest;
    }

    public static void addAttribute(List<StdMutableRequestAttributes> attributes, Identifier category,
                                    Identifier attributeId, boolean includeInResults, String datatype,
                                    String issuer, String id, Field field, Object object)
        throws IllegalArgumentException, IllegalAccessException, DataTypeException {
        //
        // Create our attribute
        //
        StdMutableAttribute mutableAttribute = new StdMutableAttribute();
        mutableAttribute.setCategory(category);
        mutableAttribute.setAttributeId(attributeId);
        mutableAttribute.setIncludeInResults(includeInResults);
        if (issuer != null && !issuer.isEmpty()) {
            mutableAttribute.setIssuer(issuer);
        }
        //
        // Pull the values from the field
        //
        field.setAccessible(true);
        Collection<AttributeValue<?>> value = RequestParser.extractValues(datatype, field, object);
        if (value != null) {
            mutableAttribute.addValues(value);
        } else {
            throw new IllegalArgumentException("Unable to extract attribute value from object");
        }
        //
        // Does the category exist?
        //
        boolean added = false;
        for (StdMutableRequestAttributes a : attributes) {
            if (a.getCategory().equals(mutableAttribute.getCategory())
                && id != null ? a.getXmlId().equals(id) : a.getXmlId() == null ? true : false) {
                //
                // Category exists, add in the attribute values
                //
                a.add(mutableAttribute);
                added = true;
                break;
            }
        }
        //
        // Was it added?
        //
        if (!added) {
            //
            // No the category does not exist yet or this has a different xml:id
            //
            StdMutableRequestAttributes newAttributes = new StdMutableRequestAttributes();
            newAttributes.setCategory(mutableAttribute.getCategory());
            newAttributes.setXmlId(id);
            newAttributes.add(mutableAttribute);
            attributes.add(newAttributes);
        }
    }

    public static Collection<AttributeValue<?>> extractValues(String datatype, Field field, Object object)
        throws IllegalArgumentException, IllegalAccessException, DataTypeException {
        //
        // Synchronize?
        //
        DataTypeFactory dtFactory = getDataTypeFactory();
        if (dtFactory == null) {
            logger.error("Could not create data type factory");
            return null;
        }
        //
        // This is what we will return
        //
        Collection<AttributeValue<?>> values = new ArrayList<AttributeValue<?>>();
        //
        // Sanity check the object
        //
        Object fieldObject = field.get(object);
        if (logger.isDebugEnabled()) {
            logger.debug(fieldObject);
        }
        if (fieldObject == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("field's object is null.");
            }
            return values;
        }
        //
        // Are we working with a collection or an array?
        //
        if (field.get(object) instanceof Collection || field.get(object) instanceof Map) {
            Collection<?> objects = (Collection<?>)field.get(object);
            if (objects == null || objects.isEmpty()) {
                if (logger.isTraceEnabled()) {
                    logger.trace("empty collection");
                }
                return values;
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Object is a collection");
            }
            for (Object obj : objects) {
                values.add(extractValue(datatype, obj));
            }
        } else if (fieldObject.getClass().isArray()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Object is an array");
            }
            for (int i = 0; i < Array.getLength(fieldObject); i++) {
                values.add(extractValue(datatype, Array.get(fieldObject, i)));
            }
        } else {
            values.add(extractValue(datatype, field.get(object)));
        }
        return values;
    }

    protected static AttributeValue<?> extractValue(String datatype, Object object) throws DataTypeException {
        //
        // Is there a data type?
        //
        Identifier datatypeId;
        if (datatype == null) {
            //
            // The data type is not specified, we will decipher it based
            // on its Java class.
            //
            if (object instanceof String) {
                datatypeId = XACML3.ID_DATATYPE_STRING;
            } else if (object instanceof Integer || object instanceof Long) {
                datatypeId = XACML3.ID_DATATYPE_INTEGER;
            } else if (object instanceof Boolean) {
                datatypeId = XACML3.ID_DATATYPE_BOOLEAN;
            } else if (object instanceof Double || object instanceof Float) {
                datatypeId = XACML3.ID_DATATYPE_DOUBLE;
            } else if (object instanceof Date || object instanceof Calendar
                       || object instanceof ISO8601DateTime) {
                datatypeId = XACML3.ID_DATATYPE_DATETIME;
            } else if (object instanceof URI) {
                datatypeId = XACML3.ID_DATATYPE_ANYURI;
            } else if (object instanceof ISO8601Date) {
                datatypeId = XACML3.ID_DATATYPE_DATE;
            } else if (object instanceof ISO8601Time) {
                datatypeId = XACML3.ID_DATATYPE_TIME;
            } else if (object instanceof RFC2396DomainName) {
                datatypeId = XACML3.ID_DATATYPE_DNSNAME;
            } else if (object instanceof byte[] || object instanceof HexBinary) {
                datatypeId = XACML3.ID_DATATYPE_HEXBINARY;
            } else if (object instanceof Base64Binary) {
                datatypeId = XACML3.ID_DATATYPE_BASE64BINARY;
            } else if (object instanceof XPathDayTimeDuration) {
                datatypeId = XACML3.ID_DATATYPE_DAYTIMEDURATION;
            } else if (object instanceof IPAddress) {
                datatypeId = XACML3.ID_DATATYPE_IPADDRESS;
            } else if (object instanceof RFC822Name) {
                datatypeId = XACML3.ID_DATATYPE_RFC822NAME;
            } else if (object instanceof X500Principal) {
                datatypeId = XACML3.ID_DATATYPE_X500NAME;
            } else if (object instanceof XPathExpression || object instanceof Node) {
                datatypeId = XACML3.ID_DATATYPE_XPATHEXPRESSION;
            } else if (object instanceof XPathYearMonthDuration) {
                datatypeId = XACML3.ID_DATATYPE_YEARMONTHDURATION;
            } else {
                logger
                    .warn("Cannot decipher java object, defaulting to String datatype. If this is not correct, you must specify the datatype in the annotation.");
                //
                // Default to a string
                //
                datatypeId = XACML3.ID_DATATYPE_STRING;
            }
        } else {
            datatypeId = new IdentifierImpl(datatype);
        }
        DataType<?> dataTypeExtended = getDataTypeFactory().getDataType(datatypeId);
        if (dataTypeExtended == null) {
            logger.error("DataType factory does not know datatype: " + datatype);
            return null;
        }
        return dataTypeExtended.createAttributeValue(object);
    }

}
