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
package org.apache.openaz.xacml.std.json;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.x500.X500Principal;
import javax.xml.XMLConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Advice;
import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeAssignment;
import org.apache.openaz.xacml.api.AttributeCategory;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.DataTypeFactory;
import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.IdReference;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.MissingAttributeDetail;
import org.apache.openaz.xacml.api.Obligation;
import org.apache.openaz.xacml.api.Response;
import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.api.SemanticString;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdAdvice;
import org.apache.openaz.xacml.std.StdDataTypeFactory;
import org.apache.openaz.xacml.std.StdIdReference;
import org.apache.openaz.xacml.std.StdMutableAttribute;
import org.apache.openaz.xacml.std.StdMutableAttributeAssignment;
import org.apache.openaz.xacml.std.StdMutableAttributeCategory;
import org.apache.openaz.xacml.std.StdMutableMissingAttributeDetail;
import org.apache.openaz.xacml.std.StdMutableResponse;
import org.apache.openaz.xacml.std.StdMutableResult;
import org.apache.openaz.xacml.std.StdMutableStatus;
import org.apache.openaz.xacml.std.StdMutableStatusDetail;
import org.apache.openaz.xacml.std.StdObligation;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.StdVersion;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.apache.openaz.xacml.std.datatypes.ExtendedNamespaceContext;
import org.apache.openaz.xacml.std.datatypes.StringNamespaceContext;
import org.apache.openaz.xacml.std.datatypes.XPathExpressionWrapper;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.apache.openaz.xacml.util.FactoryException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSONResponse is used to convert JSON into {@link org.apache.openaz.xacml.api.Response} objects and
 * {@link org.apache.openaz.xacml.api.Response} objects into JSON strings. Instances of this class are never
 * created. The {@link org.apache.openaz.xacml.api.Response} objects returned by this class are instances of
 * {@link org.apache.openaz.xacml.std.StdMutableResponse}. {@link org.apache.openaz.xacml.api.Response} objects
 * are generated by loading a file or JSON string representing the Request. In normal product operation this
 * is not used to generate new instances because the PDP generates
 * {@link org.apache.openaz.xacml.std.StdResponse} objects internally. Those objects are converted to JSON
 * strings for transmission through the RESTful Web Service using the <code>convert</code> method in this
 * class.
 */
public class JSONResponse {
    private static final Log logger = LogFactory.getLog(JSONResponse.class);

    /*
     * Map of Data Type Identifiers used to map the Identifier into the shorthand name of that DataType. This
     * is loaded the first time a Request is processed. Loading is done using Reflection. key = full name of
     * the Identifier as a String value = shorthand version of that name (Note difference in structure and
     * usage from JSON Request.)
     */
    private static Map<String, String> outputShorthandMap = null;

    /*
     * USED ONLY IN CONVERTING File/String/InputStream JSON TEXT INTO INTERNAL RESPONSE OBJECT Map of Data
     * Type Identifiers used to map shorthand notation for DataTypes into the full Identifer. This is loaded
     * the first time a Request is processed. Loading is done using Reflection. The map contains keys for both
     * the short form and the long form of each DataType. For example both of the following are in the table:
     * http://www.w3.org/2001/XMLSchema#base64Binary = http://www.w3.org/2001/XMLSchema#base64Binary
     * base64Binary = http://www.w3.org/2001/XMLSchema#base64Binary (Note difference in structure and usage
     * from JSONResponse.)
     */
    private static Map<String, Identifier> shorthandMap = null;

    /*
     * To check the individual data attributes for being the correct type, we need an instance of the
     * DataTypeFactory
     */
    private static DataTypeFactory dataTypeFactory = null;

    protected JSONResponse() {
    }

    //
    // HELPER METHODS
    //

    /**
     * Use reflection to load the map with all the names of all DataTypes allowing us to output the shorthand
     * version rather than the full Identifier name. (to shorten the JSON output). The shorthand map is used
     * differently in JSONRequest than in JSONResponse, so there are similarities and differences in the
     * implementation. This is done once the first time a Request is processed.
     */
    private static void initOutputShorthandMap() throws JSONStructureException {
        Field[] declaredFields = XACML3.class.getDeclaredFields();
        outputShorthandMap = new HashMap<String, String>();
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getName().startsWith("ID_DATATYPE")
                && Modifier.isPublic(field.getModifiers())) {
                try {
                    Identifier id = (Identifier)field.get(null);
                    String longName = id.stringValue();
                    // most names start with 'http://www.w3.org/2001/XMLSchema#'
                    int sharpIndex = longName.lastIndexOf("#");
                    if (sharpIndex <= 0) {
                        // some names start with 'urn:oasis:names:tc:xacml:1.0:data-type:'
                        // or urn:oasis:names:tc:xacml:2.0:data-type:
                        if (longName.contains(":data-type:")) {
                            sharpIndex = longName.lastIndexOf(":");
                        } else {
                            continue;
                        }
                    }
                    String shortName = longName.substring(sharpIndex + 1);
                    // put both the full name and the short name in the table
                    outputShorthandMap.put(id.stringValue(), shortName);
                } catch (Exception e) {
                    throw new JSONStructureException("Error loading ID Table, e=" + e);
                }
            }
        }
    }

    /**
     * Use reflection to load the map with all the names of all DataTypes, both the long name and the
     * shorthand, and point each name to the appropriate Identifier. The shorthand map is used differently in
     * JSONRequest than in JSONResponse, so there are similarities and differences in the implementation. This
     * is done once the first time a Request is processed.
     */
    private static void initShorthandMap() throws JSONStructureException {
        Field[] declaredFields = XACML3.class.getDeclaredFields();
        shorthandMap = new HashMap<String, Identifier>();
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getName().startsWith("ID_DATATYPE")
                && Modifier.isPublic(field.getModifiers())) {
                try {
                    Identifier id = (Identifier)field.get(null);
                    String longName = id.stringValue();
                    // most names start with 'http://www.w3.org/2001/XMLSchema#'
                    int sharpIndex = longName.lastIndexOf("#");
                    if (sharpIndex <= 0) {
                        // some names start with 'urn:oasis:names:tc:xacml:1.0:data-type:'
                        // or urn:oasis:names:tc:xacml:2.0:data-type:
                        if (longName.contains(":data-type:")) {
                            sharpIndex = longName.lastIndexOf(":");
                        } else {
                            continue;
                        }
                    }
                    String shortName = longName.substring(sharpIndex + 1);
                    // put both the full name and the short name in the table
                    shorthandMap.put(longName, id);
                    shorthandMap.put(shortName, id);
                } catch (Exception e) {
                    throw new JSONStructureException("Error loading ID Table, e=" + e);
                }
            }
        }
    }

    /**
     * Check the given map for all components having been removed (i.e. everything in the map was known and
     * used). If anything remains, throw an exception based on the component and the keys left in the map
     */
    private static void checkUnknown(String component, Map<?, ?> map) throws JSONStructureException {
        if (map.size() == 0) {
            return;
        }

        String keys = null;
        Iterator<?> it = map.keySet().iterator();
        while (it.hasNext()) {
            if (keys == null) {
                keys = "'" + it.next().toString() + "'";
            } else {
                keys += ", '" + it.next().toString() + "'";
            }
        }

        String message = component + " contains unknown element" + ((map.size() == 1) ? " " : "s ") + keys;
        throw new JSONStructureException(message);
    }

    /**
     * When parsing a JSON string into an object, recursively parse the StatusCode and any child StatusCodes
     *
     * @param statusCodeMap
     * @return
     * @throws JSONStructureException
     */
    private static StatusCode parseStatusCode(Map<?, ?> statusCodeMap) throws JSONStructureException {

        // get optional value
        Object valueObject = statusCodeMap.remove("Value");
        Identifier identifier = null;
        if (valueObject != null) {
            identifier = new IdentifierImpl(valueObject.toString());
        }

        // get optional child status code
        Object childStatusCodeMap = statusCodeMap.remove("StatusCode");
        StatusCode childStatusCode = null;
        if (childStatusCodeMap != null) {
            if (!(childStatusCodeMap instanceof Map)) {
                throw new JSONStructureException("Child StatusCode must be object");
            }
            childStatusCode = parseStatusCode((Map<?, ?>)childStatusCodeMap);
        }

        checkUnknown("StatusCode", statusCodeMap);

        StdStatusCode statusCode = new StdStatusCode(identifier, childStatusCode);

        return statusCode;
    }

    /**
     * When reading a JSON string and converting to internal objects, this converts the parsed Map into an
     * XPathExpression AttributeValue.
     *
     * @param mapObject
     * @param categoryId
     * @return
     * @throws JSONStructureException
     */
    private static AttributeValue<?> convertMapToXPathExpression(Object mapObject)
        throws JSONStructureException {
        if (!(mapObject instanceof Map)) {
            throw new JSONStructureException(
                                             "XPathExpression value must be complex object containing XPath, XPathCategory and optional Namespaces");

        }
        Map<?, ?> xpathExpressionMap = (Map<?, ?>)mapObject;

        // get mandatory XPath
        Object xpathObject = xpathExpressionMap.remove("XPath");
        if (xpathObject == null || !(xpathObject instanceof String)) {
            throw new JSONStructureException("XPathExpression must contain string XPath");
        }

        // mandatory XPathCategory
        Object xpathCategoryObject = xpathExpressionMap.remove("XPathCategory");
        if (xpathCategoryObject == null || !(xpathCategoryObject instanceof String)) {
            throw new JSONStructureException("XPathExpression must contain URI (string) XPathCategory");
        }

        Identifier xpathCategoryIdentifier = new IdentifierImpl(xpathCategoryObject.toString());

        // optional Namespaces
        Object namespacesObject = xpathExpressionMap.remove("Namespaces");
        StringNamespaceContext namespaceContext = null;
        if (namespacesObject != null) {
            if (!(namespacesObject instanceof List)) {
                throw new JSONStructureException("Namespaces must be list");
            }
            List<?> namespacesList = (List<?>)namespacesObject;

            namespaceContext = new StringNamespaceContext();
            // get all Namespace elements and add to context
            for (Object namespaceObject : namespacesList) {
                if (!(namespaceObject instanceof Map)) {
                    throw new JSONStructureException("Namespaces array items must be object");
                }
                Map<?, ?> namespaceMap = (Map<?, ?>)namespaceObject;

                // mandatory Namespace
                Object namespaceURI = namespaceMap.remove("Namespace");
                if (namespaceURI == null) {
                    throw new JSONStructureException("Namespace array item must contain Namespace member");
                }

                // optional Prefix
                Object prefixObject = namespaceMap.remove("Prefix");
                String prefix = null;
                if (prefixObject != null) {
                    prefix = prefixObject.toString();
                }

                checkUnknown("Namespace", namespaceMap);

                try {
                    if (prefix == null) {
                        namespaceContext.add(namespaceURI.toString());
                    } else {
                        namespaceContext.add(prefix, namespaceURI.toString());
                    }
                } catch (Exception e) {
                    throw new JSONStructureException("Namespace array item error: " + e.getMessage());
                }
            }
        }

        checkUnknown("XPathExpression", xpathExpressionMap);

        // create XPathExpression
        XPathExpressionWrapper wrapper = new XPathExpressionWrapper(namespaceContext, xpathObject.toString());

        // create and return AttributeValue
        AttributeValue<XPathExpressionWrapper> attributeValue;
        try {
            attributeValue = DataTypes.DT_XPATHEXPRESSION.createAttributeValue(wrapper,
                                                                               xpathCategoryIdentifier);
        } catch (DataTypeException e) {
            throw new JSONStructureException("Namespaces unable to create AttributeValue; reason: "
                                             + e.getMessage());
        }

        return attributeValue;

    }

    /**
     * Parse Obligations or AssociatedAdvice and put them into the Result. This code combines Obligations and
     * AssociatedAdvice because the operations are identical except for the final steps.
     *
     * @param listObject
     * @param stdMutableResult
     * @param isObligation
     * @throws JSONStructureException
     */
    private static void parseObligationsOrAdvice(Object listObject, StdMutableResult stdMutableResult,
                                                 boolean isObligation) throws JSONStructureException {
        String oaTypeName = isObligation ? "Obligations" : "AssociatedAdvice";

        if (!(listObject instanceof List)) {
            throw new JSONStructureException(oaTypeName + " must be list");
        }
        List<?> oaList = (List<?>)listObject;

        // for each element in list
        for (Object oa : oaList) {

            if (!(oa instanceof Map)) {
                throw new JSONStructureException(oaTypeName + " array items must all be objects");
            }
            Map<?, ?> oaMap = (Map<?, ?>)oa;

            // get mandatory id
            Object idObject = oaMap.remove("Id");
            if (idObject == null) {
                throw new JSONStructureException(oaTypeName + " array item must have Id");
            }
            Identifier oaId = new IdentifierImpl(idObject.toString());

            // get optional list of AttributeAssignment
            Object aaListObject = oaMap.remove("AttributeAssignment");
            List<AttributeAssignment> attributeAssignmentList = new ArrayList<AttributeAssignment>();
            if (aaListObject != null) {
                if (!(aaListObject instanceof List)) {
                    throw new JSONStructureException("AttributeAssignment must be list in " + oaTypeName);
                }
                List<?> attributeAssignmentMapList = (List<?>)aaListObject;

                // list should contain instances of Maps which translate into AttributeAssignments
                for (Object aaMapObject : attributeAssignmentMapList) {
                    if (aaMapObject == null || !(aaMapObject instanceof Map)) {
                        throw new JSONStructureException(
                                                         "AttributeAssignment list item must be non-null object in "
                                                             + oaTypeName);
                    }
                    Map<?, ?> aaMap = (Map<?, ?>)aaMapObject;
                    StdMutableAttributeAssignment stdMutableAttributeAssignment = new StdMutableAttributeAssignment();

                    // mandatory Id
                    Object aaIdObject = aaMap.remove("AttributeId");
                    if (aaIdObject == null) {
                        throw new JSONStructureException(
                                                         "AttributeAssignment list item missing AttributeId in "
                                                             + oaTypeName);
                    }
                    stdMutableAttributeAssignment.setAttributeId(new IdentifierImpl(aaIdObject.toString()));

                    // optional Category
                    Object categoryObject = aaMap.remove("Category");
                    if (categoryObject != null) {
                        stdMutableAttributeAssignment.setCategory(new IdentifierImpl(categoryObject
                            .toString()));
                    }

                    // get the optional DataType so we know what to do with the mandatory value
                    Object dataTypeObject = aaMap.remove("DataType");
                    Identifier dataTypeId = null;
                    if (dataTypeObject != null) {
                        dataTypeId = shorthandMap.get(dataTypeObject.toString());
                        // if there was a DataType given it must be a real one
                        if (dataTypeId == null) {
                            throw new JSONStructureException(
                                                             "AttributeAssignment list item has unknown DataType='"
                                                                 + dataTypeObject.toString() + "' in "
                                                                 + oaTypeName);
                        }
                    } else {
                        // if DataType not given, use String
                        dataTypeId = DataTypes.DT_STRING.getId();
                    }

                    // mandatory Value
                    Object valueObject = aaMap.remove("Value");
                    if (valueObject == null) {
                        throw new JSONStructureException("AttributeAssignment list item missing Value in "
                                                         + oaTypeName);
                    }
                    AttributeValue<?> attributeValue = null;
                    try {
                        DataType<?> dataType = new StdDataTypeFactory().getDataType(dataTypeId);
                        if (dataType == DataTypes.DT_XPATHEXPRESSION) {
                            attributeValue = convertMapToXPathExpression(valueObject);

                        } else {
                            // everything other than XPathExpressions are simple values that the DataTypes
                            // know how to handle
                            attributeValue = dataType.createAttributeValue(valueObject);
                        }

                    } catch (DataTypeException e) {
                        throw new JSONStructureException("AttributeAssignment list item Value='"
                                                         + valueObject.toString() + "' not of type '"
                                                         + dataTypeId + "' in " + oaTypeName);
                    }
                    stdMutableAttributeAssignment.setAttributeValue(attributeValue);

                    // optional Issuer
                    Object issuerObject = aaMap.remove("Issuer");
                    if (issuerObject != null) {
                        stdMutableAttributeAssignment.setIssuer(issuerObject.toString());
                    }

                    checkUnknown("AttributeAssignment in " + oaTypeName, aaMap);

                    // add to attributeAssignmentList
                    attributeAssignmentList.add(stdMutableAttributeAssignment);
                }

            }

            checkUnknown(oaTypeName + " array item", oaMap);

            if (isObligation) {
                Obligation obligation = new StdObligation(oaId, attributeAssignmentList);
                stdMutableResult.addObligation(obligation);
            } else {
                Advice advice = new StdAdvice(oaId, attributeAssignmentList);
                stdMutableResult.addAdvice(advice);
            }

        }

    }

    /**
     * When reading a JSON string to create a Result object, parse the PolicyIdReference and
     * PolicySetIdReference texts.
     *
     * @param policyIdReferenceObject
     * @param stdMutableResult
     * @param isSet
     */
    private static void parseIdReferences(Object policyIdReferenceObject, StdMutableResult stdMutableResult,
                                          boolean isSet) throws JSONStructureException {
        String idTypeName = isSet ? "PolicySetIdReference" : "PolicyIdReference";

        if (!(policyIdReferenceObject instanceof List)) {
            throw new JSONStructureException(idTypeName + " must be array");
        }
        List<?> policyIdReferenceList = (List<?>)policyIdReferenceObject;
        for (Object idReferenceObject : policyIdReferenceList) {
            if (idReferenceObject == null || !(idReferenceObject instanceof Map)) {
                throw new JSONStructureException(idTypeName + " array item must be non-null object");
            }
            Map<?, ?> idReferenceMap = (Map<?, ?>)idReferenceObject;

            // mandatory Id
            Object idReferenceIdObject = idReferenceMap.remove("Id");
            if (idReferenceIdObject == null) {
                throw new JSONStructureException(idTypeName + " array item must contain Id");
            }
            Identifier idReferenceId = new IdentifierImpl(idReferenceIdObject.toString());

            // optional Version
            StdVersion version = null;
            Object idReferenceVersionObject = idReferenceMap.remove("Version");
            if (idReferenceVersionObject != null) {
                try {
                    version = StdVersion.newInstance(idReferenceVersionObject.toString());
                } catch (ParseException e) {
                    throw new JSONStructureException(idTypeName + " array item Version: " + e.getMessage());
                }
            }

            checkUnknown("IdReference in " + idTypeName, idReferenceMap);

            StdIdReference policyIdentifier = new StdIdReference(idReferenceId, version);

            // add to the appropriate list in the Result
            if (isSet) {
                stdMutableResult.addPolicySetIdentifier(policyIdentifier);

            } else {
                stdMutableResult.addPolicyIdentifier(policyIdentifier);
            }
        }

    }

    //
    // PRIMARY INTERFACE METHODS
    //

    /**
     * Parse and JSON string into a {@link org.apache.openaz.xacml.api.Response} object.
     *
     * @param jsonString
     * @return
     * @throws JSONStructureException
     */
    public static Response load(String jsonString) throws JSONStructureException {
        Response response = null;
        try (InputStream is = new ByteArrayInputStream(jsonString.getBytes("UTF-8"))) {
            response = JSONResponse.load(is);
        } catch (Exception ex) {
            throw new JSONStructureException("Exception loading String Response: " + ex.getMessage(), ex);
        }
        return response;
    }

    /**
     * Read a file containing an JSON representation of a Response and parse it into a
     * {@link org.apache.openaz.xacml.api.Response} Object. This is used only for testing since Responses in
     * the normal environment are generated by the PDP code.
     *
     * @param fileResponse
     * @return
     * @throws JSONStructureException
     */
    public static Response load(File fileResponse) throws JSONStructureException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileResponse))) {
            String responseString = "";
            String line;
            while ((line = br.readLine()) != null) {
                responseString += line;
            }
            br.close();
            return load(responseString);
        } catch (Exception e) {
            throw new JSONStructureException(e);
        }
    }

    /**
     * Loads from Java 7 nio Path object.
     *
     * @param pathResponse
     * @return
     * @throws JSONStructureException
     */
    public static Response load(Path pathResponse) throws JSONStructureException {
        try {
            return JSONResponse.load(Files.newInputStream(pathResponse));
        } catch (Exception e) {
            throw new JSONStructureException(e);
        }
    }

    /**
     * Read characters from the given <code>InputStream</code> and parse them into an XACML
     * {@link org.apache.openaz.xacml.api.Request} object.
     *
     * @param is
     * @return
     * @throws JSONStructureException
     */
    public static Response load(InputStream is) throws JSONStructureException {

        // TODO - ASSUME that order of members within an object does not matter (Different from XML, in JSON
        // everything is handled as Maps so order does not matter)

        // ensure shorthand map is set up
        if (shorthandMap == null) {
            initShorthandMap();
        }

        // ensure that we have an instance of the DataTypeFactory for generating AttributeValues by DataType
        if (dataTypeFactory == null) {
            try {
                dataTypeFactory = DataTypeFactory.newInstance();
                if (dataTypeFactory == null) {
                    throw new NullPointerException("No DataTypeFactory found");
                }
            } catch (FactoryException e) {
                throw new JSONStructureException("Unable to find DataTypeFactory, e=" + e);
            }
        }

        // create a new Response object to be filled in
        StdMutableResponse stdMutableResponse = null;

        String json = null;
        ObjectMapper mapper = null;
        try {

            // read the inputStream into a buffer (trick found online scans entire input looking for
            // end-of-file)
            java.util.Scanner scanner = new java.util.Scanner(is);
            scanner.useDelimiter("\\A");
            json = scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

            // TODO - ASSUME that any duplicated component is a bad thing (probably indicating an error in the
            // incoming JSON)
            mapper.configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true);

            Map<?, ?> root = mapper.readValue(json, Map.class);

            //
            // Does the Response exist?
            //
            List<?> resultList = (List<?>)root.remove("Response");
            if (resultList == null) {
                throw new JSONStructureException("No \"Response\" property found.");
            }

            checkUnknown("Top-level message", root);

            stdMutableResponse = new StdMutableResponse();

            // handle each Result object
            for (int resultIndex = 0; resultIndex < resultList.size(); resultIndex++) {
                // each item should be a Map<?,?> containing a Result, otherwise it is an error
                Object resultObj = resultList.get(resultIndex);
                if (resultObj == null || !(resultObj instanceof Map)) {
                    throw new JSONStructureException(
                                                     "Response contains null Result or list instead of Result object");
                }

                StdMutableResult stdMutableResult = new StdMutableResult();

                Map<?, ?> resultMap = (Map<?, ?>)resultObj;

                // Must have a Decision
                Object decisionObject = resultMap.remove("Decision");
                if (decisionObject == null) {
                    throw new JSONStructureException("Result must have Decision");
                }
                Decision decision = Decision.get(decisionObject.toString());
                if (decision == null) {
                    throw new JSONStructureException("Unknown value for Decision: '"
                                                     + decisionObject.toString() + "'");
                }
                stdMutableResult.setDecision(decision);

                // may have Status
                Object statusObject = resultMap.remove("Status");
                if (statusObject != null) {
                    if (!(statusObject instanceof Map)) {
                        throw new JSONStructureException("Status must be an object, not type '"
                                                         + statusObject.getClass().getName() + "'");
                    }
                    StdMutableStatus stdMutableStatus = new StdMutableStatus();
                    Map<?, ?> statusMap = (Map<?, ?>)statusObject;

                    // optional message
                    Object messageObject = statusMap.remove("StatusMessage");
                    if (messageObject != null) {
                        stdMutableStatus.setStatusMessage(messageObject.toString());
                    }

                    // optional detail
                    Object detailObject = statusMap.remove("StatusDetail");
                    if (detailObject != null) {
                        StdMutableStatusDetail statusDetail = new StdMutableStatusDetail();
                        // TODO - PROBLEM: The JSON spec says only that the status Detail is raw XML rather
                        // than a JSON object. Therefore we cannot discriminate what is inside the string we
                        // just got.
                        // TODO Fortunately there is only one thing it can be: a MissingAttributeDetail.
                        // TODO Unfortunately the MissingAttributeDetail contains multiple optional elements
                        // including 0 or more values, which makes it non-trivial to parse the XML
                        // representation.
                        // TODO Unfortunately the JSON spec does not say how the XML is formatted
                        // (with/without whitespace, etc).

                        //
                        // First of all, the String is possible escaped.
                        //
                        // The meaning of "escaped" is defined in section 4.2.3.1 in the JSON spec
                        //
                        String unescapedContent = detailObject.toString().replace("\\\"", "\"");
                        unescapedContent = unescapedContent.replace("\\\\", "\\");

                        // need to add a root element so that the MissingAttributeDetail elements are findable
                        unescapedContent = "<ROOT>" + unescapedContent + "</ROOT>";

                        // logger.info("Escaped content: \n" + unescapedContent);
                        Document doc = null;
                        try (InputStream bis = new ByteArrayInputStream(unescapedContent.getBytes("UTF-8"))) {
                            doc = DOMUtil.loadDocument(bis);
                        } catch (Exception ex) {
                            throw new JSONStructureException("Unable to parse Content '"
                                                             + detailObject.toString() + "'");
                        }

                        // ASSUME that this can only be an array of MissingAttributeDetail. Example:
                        // <MissingAttributeDetail
                        // Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource"
                        // AttributeId="urn:att:xacml:resource:application:motsid"
                        // DataType="http://www.w3.org/2001/XMLSchema#integer">
                        // <AttributeValue
                        // DataType="http://www.w3.org/2001/XMLSchema#integer">56</AttributeValue>
                        // </MissingAttributeDetail>"
                        Element docElement = doc.getDocumentElement();
                        NodeList missingAttributeDetailList = docElement
                            .getElementsByTagName("MissingAttributeDetail");
                        for (int madNodeIndex = 0; madNodeIndex < missingAttributeDetailList.getLength(); madNodeIndex++) {
                            Node madNode = missingAttributeDetailList.item(madNodeIndex);
                            StdMutableMissingAttributeDetail mutableMAD = new StdMutableMissingAttributeDetail();

                            NamedNodeMap attributeMap = madNode.getAttributes();
                            Node attributeNode = attributeMap.getNamedItem("AttributeId");
                            if (attributeNode == null) {
                                throw new JSONStructureException("MissingAttributeDetail missing AttributeId");
                            }
                            mutableMAD.setAttributeId(new IdentifierImpl(attributeNode.getNodeValue()));
                            Node categoryNode = attributeMap.getNamedItem("Category");
                            if (categoryNode == null) {
                                throw new JSONStructureException("MissingAttributeDetail missing Category");
                            }
                            mutableMAD.setCategory(new IdentifierImpl(categoryNode.getNodeValue()));
                            Node dataTypeNode = attributeMap.getNamedItem("DataType");
                            if (dataTypeNode == null) {
                                throw new JSONStructureException("MissingAttributeDetail missing DataType");
                            }
                            mutableMAD.setDataTypeId(new IdentifierImpl(dataTypeNode.getNodeValue()));
                            Node issuerNode = attributeMap.getNamedItem("Issuer");
                            if (issuerNode != null) {
                                mutableMAD.setIssuer(issuerNode.getNodeValue());
                            }

                            // get any value elements
                            NodeList childNodeList = madNode.getChildNodes();
                            for (int childIndex = 0; childIndex < childNodeList.getLength(); childIndex++) {
                                Node childNode = childNodeList.item(childIndex);
                                if (!childNode.getNodeName().equals("AttributeValue")) {
                                    continue;
                                }
                                Node childDataTypeNode = childNode.getAttributes().getNamedItem("DataType");
                                if (childDataTypeNode == null) {
                                    throw new JSONStructureException(
                                                                     "MissingAttributeDetail contains AttributeValue '"
                                                                         + childNode.getNodeValue()
                                                                         + "' with no DataType");
                                }
                                String dataType = childDataTypeNode.getNodeValue();
                                // this probably is not a shorthand, but look it up anyway. The full Ids are
                                // in the table too.
                                Identifier valueDataTypeId = shorthandMap.get(dataType);
                                if (valueDataTypeId == null) {
                                    throw new JSONStructureException(
                                                                     "MissingAttibuteDetail contains AttributeValue with unknown DataType="
                                                                         + dataType);
                                }
                                // if Id is known then it is reasonable to do the following without checking
                                DataType<?> valueDataType = dataTypeFactory.getDataType(valueDataTypeId);
                                AttributeValue<?> attributeValue;
                                try {
                                    // for some reason the value may be the value of a child of this node
                                    // rather than the value of this node itself.
                                    Node valueNode = childNode;
                                    if (valueNode.hasChildNodes()) {
                                        valueNode = valueNode.getFirstChild();
                                    }
                                    attributeValue = valueDataType.createAttributeValue(valueNode
                                        .getNodeValue());
                                } catch (Exception ex) {
                                    throw new JSONStructureException(
                                                                     "Unable to create AttributeValue from MissingAttributeDetail AttributeValue '"
                                                                         + childNode.getNodeValue()
                                                                         + "', error was: " + ex.getMessage());
                                }
                                mutableMAD.addAttributeValue(attributeValue);
                            }

                            statusDetail.addMissingAttributeDetail(mutableMAD);
                        }

                        stdMutableStatus.setStatusDetail(statusDetail);
                    }

                    // optional StatusCode which may contain recursive child StatusCode
                    Object statusCodeObject = statusMap.remove("StatusCode");
                    if (statusCodeObject != null) {
                        if (!(statusCodeObject instanceof Map)) {
                            throw new JSONStructureException("StatusCode must be object");
                        }
                        StatusCode statusCode = parseStatusCode((Map<?, ?>)statusCodeObject);
                        stdMutableStatus.setStatusCode(statusCode);
                    }

                    checkUnknown("Status", statusMap);

                    stdMutableResult.setStatus(stdMutableStatus);
                }

                // may have Obligations
                Object obligationsObject = resultMap.remove("Obligations");
                if (obligationsObject != null) {
                    parseObligationsOrAdvice(obligationsObject, stdMutableResult, true);
                }

                // may have Advice
                Object adviceObject = resultMap.remove("AssociatedAdvice");
                if (adviceObject != null) {
                    parseObligationsOrAdvice(adviceObject, stdMutableResult, false);
                }

                // may have Category (a.k.a Attributes)
                // TODO - POSSIBLE NAME CHANGE - XML core calls this "Attributes", but name in JSON standard
                // is questionable.
                // TODO The variables here are named "Attributes" because that is the internal name in our
                // objects (based on the Core spec).
                Object attributesObject = resultMap.remove("Category");
                if (attributesObject != null) {
                    if (!(attributesObject instanceof List)) {
                        throw new JSONStructureException("Category must be list");
                    }
                    List<?> attributesList = (List<?>)attributesObject;

                    for (Object categoryObject : attributesList) {
                        if (categoryObject == null || !(categoryObject instanceof Map)) {
                            throw new JSONStructureException("Category array item must be object");
                        }
                        Map<?, ?> categoryMap = (Map<?, ?>)categoryObject;
                        StdMutableAttributeCategory stdMutableAttributeCategory = new StdMutableAttributeCategory();

                        // mandatory CategoryId
                        Object categoryIdObject = categoryMap.remove("CategoryId");
                        if (categoryIdObject == null) {
                            throw new JSONStructureException("Category array item must contain CategoryId");
                        }
                        Identifier categoryId = new IdentifierImpl(categoryIdObject.toString());

                        stdMutableAttributeCategory.setCategory(categoryId);

                        // optional Attributes
                        Object attributeListObject = categoryMap.remove("Attribute");
                        if (attributeListObject != null) {
                            if (!(attributeListObject instanceof List)) {
                                throw new JSONStructureException("Category memeber Attribute must be list");
                            }
                            List<?> attributeList = (List<?>)attributeListObject;
                            // get each attribute and add to category
                            for (Object attributeMapObject : attributeList) {
                                if (attributeMapObject == null || !(attributeMapObject instanceof Map)) {
                                    throw new JSONStructureException(
                                                                     "Category member Attribute list item must be object");
                                }
                                Map<?, ?> attributeMap = (Map<?, ?>)attributeMapObject;

                                StdMutableAttribute stdMutableAttribute = new StdMutableAttribute();

                                // optional IncludeInResult
                                // TODO - Odd situation!!: We are reading a string representing a Result which
                                // includes Attributes.
                                // TODO In this case, what does it mean if "IncludeInResult=false"?
                                // TODO The Attribute is obviously included in this Result because it is in
                                // the file/string we are reading.
                                // TODO Our choice: Always include the Attribute. If the IncludeInResult is
                                // included in the input, set it's value in the object as directed.
                                // TODO This may cause mismatches between a Result read in and a new text
                                // generated from the internal Result object.
                                Object includeInResultObject = attributeMap.remove("IncludeInResult");
                                // the fact that the attribute is in the input means this should be true
                                stdMutableAttribute.setIncludeInResults(true);
                                if (includeInResultObject != null) {
                                    // need to check the value in the input
                                    try {
                                        boolean include = DataTypes.DT_BOOLEAN.convert(includeInResultObject)
                                            .booleanValue();
                                        // set the value in the object exactly as directed, whether it makes
                                        // sense or not
                                        stdMutableAttribute.setIncludeInResults(include);
                                    } catch (DataTypeException e) {
                                        throw new JSONStructureException(
                                                                         "Category member Attribute list item has IncludeInResult value '"
                                                                             + includeInResultObject
                                                                                 .toString()
                                                                             + "' which is not boolean");
                                    }
                                }

                                // category is not part of Attribute in spec - it is used internally to link
                                // attribute to Category
                                stdMutableAttribute.setCategory(categoryId);

                                // mandatory Id
                                Object aaIdObject = attributeMap.remove("AttributeId");
                                if (aaIdObject == null) {
                                    throw new JSONStructureException(
                                                                     "Category member Attribute list item missing AttributeId");
                                }
                                stdMutableAttribute.setAttributeId(new IdentifierImpl(aaIdObject.toString()));

                                // get the optional DataType so we know what to do with the mandatory value
                                Object dataTypeObject = attributeMap.remove("DataType");
                                Identifier dataTypeId = null;
                                if (dataTypeObject != null) {
                                    dataTypeId = shorthandMap.get(dataTypeObject.toString());
                                    // if there was a DataType given it must be a real one
                                    if (dataTypeId == null) {
                                        throw new JSONStructureException(
                                                                         "Category member Attribute list item has unknown DataType='"
                                                                             + dataTypeObject.toString()
                                                                             + "'");
                                    }
                                } else {
                                    // if DataType not given, use String
                                    dataTypeId = DataTypes.DT_STRING.getId();
                                }

                                // mandatory Value
                                Object valueObject = attributeMap.remove("Value");
                                if (valueObject == null) {
                                    throw new JSONStructureException(
                                                                     "Category member Attribute list item missing Value");
                                }
                                AttributeValue<?> attributeValue = null;
                                try {
                                    DataType<?> dataType = new StdDataTypeFactory().getDataType(dataTypeId);
                                    if (dataType == DataTypes.DT_XPATHEXPRESSION) {
                                        // XPAthExpressions are complex data types that need special
                                        // translation from the JSON form to the internal form
                                        attributeValue = convertMapToXPathExpression(valueObject);

                                    } else {
                                        // everything other than XPathExpressions are simple values that the
                                        // DataTypes know how to handle
                                        attributeValue = dataType.createAttributeValue(valueObject);
                                    }
                                } catch (DataTypeException e) {
                                    throw new JSONStructureException(
                                                                     "Category member Attribute list item Value='"
                                                                         + valueObject.toString()
                                                                         + "' not of type '" + dataTypeId
                                                                         + "'");
                                }
                                stdMutableAttribute.addValue(attributeValue);

                                // optional Issuer
                                Object issuerObject = attributeMap.remove("Issuer");
                                if (issuerObject != null) {
                                    stdMutableAttribute.setIssuer(issuerObject.toString());
                                }

                                checkUnknown("Category Attribute list item", attributeMap);
                                stdMutableAttributeCategory.add(stdMutableAttribute);
                            }
                        }

                        checkUnknown("Category", categoryMap);

                        // if none of the attributes are returned, do not return the category either
                        if (stdMutableAttributeCategory.getAttributes().size() > 0) {
                            stdMutableResult.addAttributeCategory(stdMutableAttributeCategory);
                        }
                    }
                }

                // may have PolicyIdentifierList
                Object policyIdObject = resultMap.remove("PolicyIdentifier");
                if (policyIdObject != null) {
                    if (!(policyIdObject instanceof Map)) {
                        throw new JSONStructureException("PolicyIdentifier must be object");
                    }
                    Map<?, ?> policyIdMap = (Map<?, ?>)policyIdObject;

                    // optional PolicyIdReference list
                    Object policyIdReferenceObject = policyIdMap.remove("PolicyIdReference");
                    if (policyIdReferenceObject != null) {
                        parseIdReferences(policyIdReferenceObject, stdMutableResult, false);
                    }

                    // optional PolicySetIdReferenceList
                    Object policySetIdReferenceObject = policyIdMap.remove("PolicySetIdReference");
                    if (policySetIdReferenceObject != null) {
                        parseIdReferences(policySetIdReferenceObject, stdMutableResult, true);
                    }

                    checkUnknown("PolicyIdentifier", policyIdMap);

                }

                checkUnknown("Result", resultMap);

                // add this result to the Response
                stdMutableResponse.add(stdMutableResult);

            }

            return stdMutableResponse;

        } catch (JsonParseException e) {
            throw new JSONStructureException("Unable to parse JSON '" + json + "', exception: " + e, e);
        } catch (JsonMappingException e) {
            throw new JSONStructureException("Unable to map JSON '" + json + "', exception: " + e, e);
        } catch (IOException e) {
            throw new JSONStructureException("Unable to read JSON input, exception: " + e, e);
        }

        // all done
        // return new StdRequest(stdMutableRequest);

        // throw new JSONStructureException("JSONResponse load string and load from file not implemented");
    }

    /**
     * Convert the {@link org.apache.openaz.xacml.api.Response} into an JSON string with pretty-printing. This
     * is used only for debugging.
     *
     * @param response
     * @return
     * @throws Exception
     */
    public static String toString(Response response) throws Exception {
        return toString(response, true);
    }

    /**
     * Convert the {@link org.apache.openaz.xacml.api.Response} into an JSON string, pretty-printing is
     * optional. This is used only for debugging.
     *
     * @param response
     * @param prettyPrint
     * @return
     * @throws Exception
     */
    public static String toString(Response response, boolean prettyPrint) throws Exception {
        String outputString = null;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            JSONResponse.convert(response, os, prettyPrint);
            outputString = new String(os.toByteArray(), "UTF-8");
        } catch (Exception ex) {
            throw ex;
        }
        return outputString;
    }

    /**
     * Convert the {@link org.apache.openaz.xacml.api.Response} object into a string suitable for output in an
     * HTTPResponse. This method generates the output without any pretty-printing. This is the method normally
     * called by the Web Service for generating the output to the PEP through the RESTful interface.
     * IMPORTANT: This method does NOT close the outputStream. It is the responsibility of the caller to (who
     * opened the stream) to close it.
     *
     * @param response
     * @param outputStream
     * @throws java.io.IOException
     * @throws JSONStructureException
     */
    public static void convert(Response response, OutputStream outputStream) throws IOException,
        JSONStructureException {
        convert(response, outputStream, false);
    }

    /**
     * Do the work of converting the {@link org.apache.openaz.xacml.api.Response} object to a string, allowing
     * for pretty-printing if desired. IMPORTANT: This method does NOT close the outputStream. It is the
     * responsibility of the caller to (who opened the stream) to close it.
     *
     * @param response
     * @param outputStream
     * @param prettyPrint
     * @throws java.io.IOException #throws JSONStructureException
     */
    public static void convert(Response response, OutputStream outputStream, boolean prettyPrint)
        throws IOException, JSONStructureException {

        // TODO - ASSUME that order of members within an object does not matter (Different from XML, in JSON
        // everything is handled as Maps so order does not matter)

        // TODO - ASSUME that the spec will fix inconsistency between AttributeId and Id (both are mentioned);
        // for now use "AttributeId" as it is clearer.

        // ensure shorthand map is set up
        if (outputShorthandMap == null) {
            initOutputShorthandMap();
        }

        if (response == null) {
            throw new JSONStructureException("No Request in convert");
        }

        if (response.getResults() == null || response.getResults().size() == 0) {
            // must be at least one result
            throw new JSONStructureException("No Result in Response");
        }

        String json = null;
        ArrayList<Map<String, Object>> responses = new ArrayList<Map<String, Object>>();

        //
        // Process each Result object
        //

        Iterator<Result> iter = response.getResults().iterator();
        while (iter.hasNext()) {
            Result result = iter.next();
            Map<String, Object> responseTree = new HashMap<String, Object>();
            if (result.getDecision() == null) {
                throw new JSONStructureException("No Decision in Result");
            }
            responseTree.put("Decision", result.getDecision().toString());

            if (result.getStatus() != null) {
                // if the StatusCode object as a whole is missing it defaults to OK, but if it exists it must
                // have an actual code value
                if (result.getStatus().getStatusCode() == null) {
                    throw new JSONStructureException("No Identifier given in StatusCode");
                }
                Identifier statusCodeId = result.getStatus().getStatusCode().getStatusCodeValue();

                // if there is a status code, it must agree with the decision
                // Permit/Deny/NotAllowed must all be OK
                // Indeterminate must not be OK
                if (statusCodeId.equals(StdStatusCode.STATUS_CODE_OK.getStatusCodeValue()) 
                    && !(result.getDecision() == Decision.DENY || result.getDecision() == Decision.PERMIT 
                        || result.getDecision() == Decision.NOTAPPLICABLE)
                    || !statusCodeId.equals(StdStatusCode.STATUS_CODE_OK.getStatusCodeValue()) 
                        && !(result.getDecision() == Decision.INDETERMINATE
                            || result.getDecision() == Decision.INDETERMINATE_DENY
                            || result.getDecision() == Decision.INDETERMINATE_DENYPERMIT 
                            || result.getDecision() == Decision.INDETERMINATE_PERMIT)) {
                    throw new JSONStructureException("StatusCode '" + statusCodeId.stringValue()
                                                     + "' does not match Decision '"
                                                     + result.getDecision().toString());
                }

                //
                // Create the status
                //
                Map<String, Object> statusTree = new HashMap<String, Object>();
                Map<String, Object> statusValue = new HashMap<String, Object>();
                statusValue.put("Value", statusCodeId.stringValue());
                addChildStatusCodes(result.getStatus().getStatusCode(), statusValue);
                statusTree.put("StatusCode", statusValue);
                String message = result.getStatus().getStatusMessage();
                if (message != null) {
                    statusTree.put("StatusMessage", message);
                }

                /*
                 * StatusDetail - special information The XACML 3.0 core spec says that the StatusDetail field
                 * depends on the StatusCode: StatusCode == missing-attribute => may have StatusDetail which
                 * is a list of MissingAttributeDetail structures StatusCode == anything else => no
                 * StatusDetail allowed This greatly simplifies handling the StatusDetail because the
                 * MissingAttributeDetail structure is well-defined. Thus the statement in the specs (both
                 * core and RESTful/JSON) that this can contain arbitrary XML is not correct.
                 */
                if (result.getStatus().getStatusDetail() != null) {

                    String statusDetailXMLString = "";

                    // cross-check that rules defined in XACML Core spec section 5.5.7 re: when StatusDetail
                    // may/may-not be included have been followed
                    if (result.getStatus().isOk()) {
                        throw new JSONStructureException("Status '"
                                                         + result.getStatus().getStatusCode().toString()
                                                         + "' must not return StatusDetail");
                    } else if (result.getStatus().getStatusCode().equals(XACML3.ID_STATUS_MISSING_ATTRIBUTE)
                               && result.getStatus().getStatusDetail().getMissingAttributeDetails() == null) {
                        throw new JSONStructureException(
                                                         "Status '"
                                                             + result.getStatus().getStatusCode().toString()
                                                             + "' has StatusDetail without MissingAttributeDetail");
                    } else if (result.getStatus().getStatusCode().equals(XACML3.ID_STATUS_SYNTAX_ERROR)) {
                        throw new JSONStructureException("Status '"
                                                         + result.getStatus().getStatusCode().toString()
                                                         + "' must not return StatusDetail");
                    } else if (result.getStatus().getStatusCode().equals(XACML3.ID_STATUS_PROCESSING_ERROR)) {
                        throw new JSONStructureException("Status '"
                                                         + result.getStatus().getStatusCode().toString()
                                                         + "' must not return StatusDetail");
                    }

                    // if included, StatusDetail is handled differently for each type of detail message and
                    // the contents are formatted into escaped XML rather than objects

                    if (result.getStatus().getStatusDetail().getMissingAttributeDetails() != null) {
                        if (!statusCodeId.equals(XACML3.ID_STATUS_MISSING_ATTRIBUTE)) {
                            throw new JSONStructureException(
                                                             "MissingAttributeDetails can only be included when StatusCode is MISSING_ATTRIBUTES, not '"
                                                                 + statusCodeId.stringValue());
                        }
                        // ASSUME that a list of length 0 should be treated as having no
                        // MissingAttributeDetails and ignored
                        if (result.getStatus().getStatusDetail().getMissingAttributeDetails().size() > 0) {
                            // TODO - ASSUME no newlines or indentation in XML - NOTE that white-space IS
                            // significant in XML
                            statusDetailXMLString = "";

                            for (MissingAttributeDetail mad : result.getStatus().getStatusDetail()
                                .getMissingAttributeDetails()) {
                                statusDetailXMLString += "<MissingAttributeDetail";

                                if (mad.getCategory() == null || mad.getAttributeId() == null
                                    || mad.getDataTypeId() == null) {
                                    throw new JSONStructureException(
                                                                     "MissingAttributeDetail must have Category, AttributeId and DataType");
                                }
                                statusDetailXMLString += " Category=\"" + mad.getCategory().stringValue()
                                                         + "\"";
                                statusDetailXMLString += " AttributeId=\""
                                                         + mad.getAttributeId().stringValue() + "\"";
                                // TODO - In this case we do NOT use the shorthand notation for the DataType
                                // because we are generating XML and it is not clear who should will be using
                                // it on client
                                statusDetailXMLString += " DataType=\"" + mad.getDataTypeId().stringValue()
                                                         + "\"";
                                if (mad.getIssuer() != null) {
                                    statusDetailXMLString += " Issuer=\"" + mad.getIssuer() + "\"";
                                }

                                // done with attibutes
                                statusDetailXMLString += ">";

                                // Now get Values and add as child element nodes
                                if (mad.getAttributeValues() != null && mad.getAttributeValues().size() > 0) {
                                    for (AttributeValue<?> av : mad.getAttributeValues()) {
                                        statusDetailXMLString += "<AttributeValue";
                                        statusDetailXMLString += " DataType=\"" + av.getDataTypeId() + "\">";
                                        statusDetailXMLString += jsonOutputObject(av.getValue(), av)
                                            .toString() + "</AttributeValue>";
                                    }
                                }

                            }
                            statusDetailXMLString += "</MissingAttributeDetail>";
                        }
                    } else {
                        throw new JSONStructureException(
                                                         "Unhandled StatusDetail contents (statusDetail exists but is not MissingAttributeDetail)");
                    }

                    if (statusDetailXMLString.length() > 0) {
                        // make sure all backslashes and double-quotes are escaped
                        // (will only exist in string values)
                        statusDetailXMLString = statusDetailXMLString.replace("\\", "\\\\");
                        statusDetailXMLString = statusDetailXMLString.replace("\"", "\\\"");
                        statusTree.put("StatusDetail", statusDetailXMLString);
                    }

                }

                responseTree.put("Status", statusTree);
            }

            //
            // Obligations
            //
            if (result.getObligations() != null && result.getObligations().size() > 0) {
                Iterator<Obligation> iterObs = result.getObligations().iterator();
                List<Object> obligationCollectionList = new ArrayList<Object>();
                while (iterObs.hasNext()) {
                    Obligation ob = iterObs.next();
                    Map<String, Object> obligationTree = new HashMap<String, Object>();
                    if (ob.getId() == null) {
                        throw new JSONStructureException("Obligation must have Id");
                    }
                    obligationTree.put("Id", ob.getId().stringValue());
                    if (ob.getAttributeAssignments() != null && ob.getAttributeAssignments().size() > 0) {
                        Iterator<AttributeAssignment> iterSetObs = ob.getAttributeAssignments().iterator();
                        ArrayList<HashMap<String, Object>> attributes = new ArrayList<HashMap<String, Object>>();
                        while (iterSetObs.hasNext()) {
                            AttributeAssignment entity = iterSetObs.next();
                            HashMap<String, Object> entityTree = new HashMap<String, Object>();
                            if (entity.getAttributeId() == null) {
                                throw new JSONStructureException("Obligation Attribute must have AttributeId");
                            }
                            entityTree.put("AttributeId", entity.getAttributeId().stringValue());
                            if (entity.getCategory() != null) {
                                entityTree.put("Category", entity.getCategory().stringValue());
                            }
                            if (entity.getIssuer() != null) {
                                entityTree.put("Issuer", entity.getIssuer());
                            }
                            AttributeValue<?> value = entity.getAttributeValue();
                            if (value == null || value.getValue() == null) {
                                // Yes it can
                                // throw new JSONStructureException("Obligation Attribute must have Value");
                                entityTree.put("Value", new String(""));
                            } else {
                                // we are "encouraged" to us Shorthand notation for DataType, but it is not
                                // required
                                if (value.getDataTypeId() != null) {
                                    //
                                    // Don't use shorthand by default, for backwards compatibility
                                    // to our pep's.
                                    //
                                    entityTree.put("DataType", value.getDataTypeId().stringValue());
                                }

                                // Internally the XPathCategory is in the AttributeValue object, but in the
                                // JSON format it is part of the Value (handled by jsonOutputObject() )
                                // so do not handle it here

                                entityTree.put("Value", jsonOutputObject(value.getValue(), value));
                            }
                            attributes.add(entityTree);
                        }
                        obligationTree.put("AttributeAssignment", attributes);
                    }
                    obligationCollectionList.add(obligationTree);
                }
                responseTree.put("Obligations", obligationCollectionList);
            }

            //
            // Advice
            //
            if (result.getAssociatedAdvice() != null && result.getAssociatedAdvice().size() > 0) {
                Iterator<Advice> iterAAs = result.getAssociatedAdvice().iterator();
                List<Object> adviceCollectionList = new ArrayList<Object>();
                while (iterAAs.hasNext()) {
                    Advice advice = iterAAs.next();
                    Map<String, Object> adviceTree = new HashMap<String, Object>();
                    if (advice.getId() == null) {
                        throw new JSONStructureException("Advice must have Id");
                    }
                    adviceTree.put("Id", advice.getId().stringValue());
                    if (advice.getAttributeAssignments() != null
                        && advice.getAttributeAssignments().size() > 0) {
                        Iterator<AttributeAssignment> iterSetObs = advice.getAttributeAssignments()
                            .iterator();
                        ArrayList<HashMap<String, Object>> attributes = new ArrayList<HashMap<String, Object>>();
                        while (iterSetObs.hasNext()) {
                            AttributeAssignment entity = iterSetObs.next();
                            HashMap<String, Object> entityTree = new HashMap<String, Object>();
                            if (entity.getAttributeId() == null) {
                                throw new JSONStructureException("Advice Attribute must have AttributeId");
                            }
                            entityTree.put("AttributeId", entity.getAttributeId().stringValue());
                            if (entity.getCategory() != null) {
                                entityTree.put("Category", entity.getCategory().stringValue());
                            }
                            if (entity.getIssuer() != null) {
                                entityTree.put("Issuer", entity.getIssuer());
                            }
                            AttributeValue<?> value = entity.getAttributeValue();
                            if (value == null || value.getValue() == null) {
                                // NO - it can have a null or empty string etc.
                                // throw new JSONStructureException("Advice Attribute must have Value");
                                entityTree.put("Value", new String(""));
                            } else {
                                // we are "encouraged" to us Shorthand notation for DataType, but it is not
                                // required
                                if (value.getDataTypeId() != null) {
                                    //
                                    // Don't use shorthand by default, for backwards compatibility
                                    // to our pep's.
                                    //
                                    entityTree.put("DataType", value.getDataTypeId().stringValue());
                                }

                                // Internally the XPathCategory is in the AttributeValue object, but in the
                                // JSON format it is part of the Value (handled by jsonOutputObject() )
                                // so do not handle it here

                                entityTree.put("Value", jsonOutputObject(value.getValue(), value));
                            }
                            attributes.add(entityTree);
                        }
                        adviceTree.put("AttributeAssignment", attributes);
                    }
                    adviceCollectionList.add(adviceTree);
                }
                responseTree.put("AssociatedAdvice", adviceCollectionList);
            }

            //
            // Attributes
            //
            // (note change in name from XML to JSON spec; this is called Category in the XML)
            //

            if (result.getAttributes() != null && result.getAttributes().size() > 0) {
                Iterator<AttributeCategory> iterAttributes = result.getAttributes().iterator();
                ArrayList<HashMap<String, Object>> categoryArray = new ArrayList<HashMap<String, Object>>();
                while (iterAttributes.hasNext()) {
                    AttributeCategory entity = iterAttributes.next();
                    HashMap<String, Object> categoryTree = new HashMap<String, Object>();
                    categoryTree.put("CategoryId", entity.getCategory().stringValue());

                    // The JSON and XML spec both imply that we can return Content here, but they do not say
                    // so explicitly and give no indication of when to include/not-include it
                    // Also we should be able to return the xml:Id associated with this attribute, but that
                    // does not seem to be available in the AttributeCategory object
                    // Note: Our choice is to not include these.
                    // There is a question of when they would be included (since IncludeInResult is only on
                    // the individual Attribute (singular) objects, not the Attributes),
                    // and the Content can be quite lengthy and should not be included by default.
                    // We could potentially return these only when at least one of the Attribute components
                    // has IncludeInResult=true.
                    // However the focus seems to be on returning the individual Attribute objects so the
                    // caller can see what the response is referring to, and the Attributes (plural)
                    // container is just re-used from the Request object without understanding that the Result
                    // should be different or explicitly stating in the Spec what to do with those fields.

                    Collection<Attribute> attrs = entity.getAttributes();
                    if (attrs != null) {
                        Iterator<Attribute> iterAttrs = attrs.iterator();
                        ArrayList<HashMap<String, Object>> arrayAttributes = new ArrayList<HashMap<String, Object>>();
                        while (iterAttrs.hasNext()) {
                            Attribute attribute = iterAttrs.next();
                            if (!attribute.getIncludeInResults()) {
                                // Would this be an error? This is an internal matter and we arbitrarily
                                // decided to just ignore it.
                                // The attribute will not be included in the output, so the receiver won't
                                // know that this happened.
                                continue;
                            }

                            HashMap<String, Object> theAttribute = new HashMap<String, Object>();
                            // TODO - no need to put this in Result because, by definition, if it is in the
                            // result then this must be true? Since it is optional we do not want to add to
                            // length of JSON output
                            // theAttribute.put("IncludeInResult", true);

                            if (attribute.getAttributeId() == null) {
                                throw new JSONStructureException("Attribute must have AttributeId");
                            }
                            theAttribute.put("AttributeId", attribute.getAttributeId().stringValue());
                            if (attribute.getValues() == null || attribute.getValues().size() == 0) {
                                throw new JSONStructureException("Attribute missing required Value");
                            }
                            Iterator<AttributeValue<?>> valueIterator = attribute.getValues().iterator();

                            // The spec talks about inferring the data type from the value and what to do if
                            // it is a list.
                            // However this is output from the PDP, and the attributes would have been
                            // screened while processing the Request,
                            // so we can assume at this point that we always have a DataType associated with
                            // the values and that the values are
                            // consistent with that DataType (because otherwise the Request would have been
                            // rejected and we would never get here).
                            // However we do need to extract the DataType from one of the Values and that is
                            // done slightly differently
                            // when there is one vs a list.
                            if (attribute.getValues().size() == 1) {
                                // exactly one value, so no need for list of values AND we know exactly what
                                // the DataType is
                                AttributeValue<?> attributeValue = valueIterator.next();
                                if (attributeValue == null || attributeValue.getValue() == null) {
                                    throw new JSONStructureException("Attribute must have value");
                                }
                                theAttribute.put("Value",
                                                 jsonOutputObject(attributeValue.getValue(), attributeValue));
                                if (attributeValue.getDataTypeId() != null) {
                                    // we are "encouraged" to us Shorthand notation for DataType, but it is
                                    // not required
                                    //
                                    // Don't use shorthand by default, for backwards compatibility
                                    // to our pep's.
                                    //
                                    theAttribute
                                        .put("DataType", attributeValue.getDataTypeId().stringValue());
                                }
                            } else {
                                // there are multiple values so we have to make a list of the Values
                                List<Object> attrValueList = new ArrayList<Object>();
                                boolean mixedTypes = false;
                                Identifier inferredDataTypeId = null;
                                while (valueIterator.hasNext()) {
                                    AttributeValue<?> attrValue = valueIterator.next();
                                    if (attrValue == null || attrValue.getValue() == null) {
                                        throw new JSONStructureException("Attribute in array must have value");
                                    }
                                    attrValueList.add(jsonOutputObject(attrValue.getValue(), attrValue));

                                    // try to infer the data type
                                    if (attrValue.getDataTypeId() != null) {
                                        if (inferredDataTypeId == null) {
                                            inferredDataTypeId = attrValue.getDataTypeId();
                                        } else {
                                            if (inferredDataTypeId.equals(DataTypes.DT_INTEGER.getId())
                                                && attrValue.getDataTypeId().equals(DataTypes.DT_DOUBLE
                                                                                        .getId())) {
                                                // seeing a double anywhere in a list of integers means the
                                                // type is double
                                                inferredDataTypeId = attrValue.getDataTypeId();
                                            } else if (inferredDataTypeId.equals(DataTypes.DT_DOUBLE.getId())
                                                       && attrValue.getDataTypeId()
                                                           .equals(DataTypes.DT_INTEGER.getId())) {
                                                // integers are ok in a list of doubles
                                                continue;
                                            } else if (!inferredDataTypeId.equals(attrValue.getDataTypeId())) {
                                                // all other combinations of types are illegal.
                                                // Note: these attribute values were read from the client's
                                                // Request and were assigned the appropriate DataType at that
                                                // time.
                                                // That DataType would have been the same for each one (e.g.
                                                // String) so there should never be a case where
                                                // there are multiple different types here.
                                                // NOTE THAT IF THIS CHANGES and we want to allow mixed types,
                                                // just replace this throws with
                                                // mixedTypes = true;
                                                throw new JSONStructureException(
                                                                                 "Mixed DataTypes in Attribute values, '"
                                                                                     + attrValue
                                                                                         .getDataTypeId()
                                                                                         .stringValue()
                                                                                     + "' in list of '"
                                                                                     + inferredDataTypeId
                                                                                         .stringValue() + "'");
                                            }
                                        }
                                    }
                                }
                                theAttribute.put("Value", attrValueList);

                                if (inferredDataTypeId != null && !mixedTypes) {
                                    // list is uniform and we know the type
                                    //
                                    // Don't use shorthand by default, for backwards compatibility
                                    // to our pep's.
                                    //
                                    theAttribute.put("DataType", inferredDataTypeId.stringValue());
                                }

                            }

                            if (attribute.getIssuer() != null) {
                                theAttribute.put("Issuer", attribute.getIssuer());
                            }

                            arrayAttributes.add(theAttribute);
                        }
                        categoryTree.put("Attribute", arrayAttributes);
                    }

                    if (categoryTree.size() > 0) {
                        categoryArray.add(categoryTree);
                    }
                }
                if (categoryArray.size() > 0) {
                    // TODO - Spec changing from Attributes to Category - change is for no good reason other
                    // than they didn't like the XML name.
                    responseTree.put("Category", categoryArray);
                }
            }

            //
            // PolicyIdentifier
            //
            // (These seem to be handled differently from the XML version where multiple PolicyIdRef and
            // PolicySetIdRef items can be jumbled together in any order.
            // In the XACML JSON spec (5.2.10) it says that the PolicyIdReference and PolicySetIdReference are
            // separate groups
            // where each group is a list of IdReferences.)
            //
            //

            if (result.getPolicyIdentifiers() != null && result.getPolicyIdentifiers().size() > 0
                || result.getPolicySetIdentifiers() != null && result.getPolicySetIdentifiers().size() > 0) {

                Map<String, Object> policyIdentifierCollectionList = new HashMap<String, Object>();
                // handle PolicyIds separately from PolicySetIds
                if (result.getPolicyIdentifiers() != null && result.getPolicyIdentifiers().size() > 0) {
                    List<Object> policyIdentifierList = new ArrayList<Object>();
                    for (IdReference idRef : result.getPolicyIdentifiers()) {
                        if (idRef == null) {
                            throw new JSONStructureException("PolicyIdReference with null reference");
                        }
                        HashMap<String, Object> entityTree = new HashMap<String, Object>();
                        entityTree.put("Id", idRef.getId().stringValue());
                        if (idRef.getVersion() != null) {
                            entityTree.put("Version", idRef.getVersion().stringValue());
                        }

                        policyIdentifierList.add(entityTree);
                    }

                    policyIdentifierCollectionList.put("PolicyIdReference", policyIdentifierList);
                }
                // handle PolicySetIds
                if (result.getPolicySetIdentifiers() != null && result.getPolicySetIdentifiers().size() > 0) {
                    List<Object> policyIdentifierList = new ArrayList<Object>();
                    for (IdReference idRef : result.getPolicySetIdentifiers()) {
                        if (idRef == null) {
                            throw new JSONStructureException("PolicySetIdReference with null reference");
                        }
                        HashMap<String, Object> entityTree = new HashMap<String, Object>();
                        entityTree.put("Id", idRef.getId().stringValue());
                        if (idRef.getVersion() != null) {
                            entityTree.put("Version", idRef.getVersion().stringValue());
                        }

                        policyIdentifierList.add(entityTree);
                    }

                    policyIdentifierCollectionList.put("PolicySetIdReference", policyIdentifierList);
                }

                responseTree.put("PolicyIdentifier", policyIdentifierCollectionList);
            }

            //
            // Finished
            //
            responses.add(responseTree);
        }

        //
        // Create the overall response
        //
        Map<String, Object> theWholeResponse = new HashMap<String, Object>();
        theWholeResponse.put("Response", responses);
        //
        // Create a string buffer
        //
        ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
        try (OutputStreamWriter osw = new OutputStreamWriter(outputStream)) {
            json = mapper.writeValueAsString(theWholeResponse);

            osw.write(json);

            // force output
            osw.flush();

        } catch (Exception e) {
            logger.error("Failed to write to json string: " + e.getLocalizedMessage(), e);
        }

    }

    /*
     * Recursively add sub-status codes. Note there is inconsistency in the Core and JSON specs where the Core
     * XML says that each statusCode may contain at most 1 sub-statusCode but the text in both specs says that
     * the statusCode may contain a sequence of statusCodes. We interpret the spec to say there is one
     * optional sub-status code.
     */
    private static void addChildStatusCodes(StatusCode statusCode, Map<String, Object> map) {
        if (statusCode.getChild() != null) {
            StatusCode child = statusCode.getChild();
            Map<String, Object> childMap = new HashMap<String, Object>();
            childMap.put("Value", child.getStatusCodeValue().stringValue());
            addChildStatusCodes(child, childMap);

            // the spec is not clear on whether the sequence of child StatusCodes has a name or not,
            // but since JSON components are either sequences or objects (whose components have name:value)
            // and the parent StatusCode (represented by the map passed in) is an object, not a sequence, the
            // component must be named.
            // The only name mentioned in the the specs for this list of children is "StatusCode".
            map.put("StatusCode", childMap);
        }
    }

    /**
     * Create the appropriate object for JSON output. This needs to be a Boolean, Integer or Double for those
     * data types so that the ObjectMapper knows how to format the JSON text. For objects implementing
     * stringValue we use that string. for XPathExpressions use the Path. Otherwise default to using toString.
     *
     * @param obj
     * @return
     */
    private static Object jsonOutputObject(Object obj, AttributeValue<?> attrValue)
        throws JSONStructureException {
        if (obj instanceof String || obj instanceof Boolean || obj instanceof BigInteger) {
            return obj;
        } else if (obj instanceof Double) {
            Double d = (Double)obj;
            if (d == Double.NaN) {
                return "NaN";
            } else if (d == Double.POSITIVE_INFINITY) {
                return "INF";
            } else if (d == Double.NEGATIVE_INFINITY) {
                return "-INF";
            }
            return obj;
        } else if (obj instanceof SemanticString) {
            return ((SemanticString)obj).stringValue();
        } else if (obj instanceof X500Principal || obj instanceof URI) {
            // something is very weird with X500Principal data type. If left on its own the output is a map
            // that includes encoding.
            return obj.toString();
        } else if (obj instanceof XPathExpressionWrapper) {
            // create a map containing the complex value for the XPathExpression
            Map<String, Object> xpathExpressionMap = new HashMap<String, Object>();
            Identifier xpathCategoryId = attrValue.getXPathCategory();
            if (xpathCategoryId == null) {
                throw new JSONStructureException("XPathExpression is missing XPathCategory");
            }
            xpathExpressionMap.put("XPathCategory", attrValue.getXPathCategory().stringValue());

            XPathExpressionWrapper xw = (XPathExpressionWrapper)obj;
            xpathExpressionMap.put("XPath", xw.getPath());

            ExtendedNamespaceContext namespaceContext = xw.getNamespaceContext();
            if (namespaceContext != null) {
                List<Object> namespaceList = new ArrayList<Object>();

                // get the list of all namespace prefixes
                Iterator<String> prefixIt = namespaceContext.getAllPrefixes();
                while (prefixIt.hasNext()) {
                    String prefix = prefixIt.next();
                    String namespaceURI = namespaceContext.getNamespaceURI(prefix);
                    Map<String, Object> namespaceMap = new HashMap<String, Object>();
                    if (prefix != null && !prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                        namespaceMap.put("Prefix", prefix);
                    }
                    namespaceMap.put("Namespace", namespaceURI);
                    namespaceList.add(namespaceMap);
                }

                xpathExpressionMap.put("Namespaces", namespaceList);
            }
            return xpathExpressionMap;
        } else {
            throw new JSONStructureException("Unhandled data type='" + obj.getClass().getName() + "'");
        }
    }

}
