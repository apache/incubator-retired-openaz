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
package org.apache.openaz.xacml.pdp.std.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.openaz.xacml.api.Advice;
import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeCategory;
import org.apache.openaz.xacml.api.IdReference;
import org.apache.openaz.xacml.api.Obligation;
import org.apache.openaz.xacml.api.Response;
import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.std.dom.DOMResponse;
import org.apache.openaz.xacml.std.json.JSONResponse;
import org.apache.openaz.xacml.util.ListUtil;
import org.junit.Test;

/**
 * Test JSON Response convert to object - Conformance tests TO RUN - use jUnit In Eclipse select this file or
 * the enclosing directory, right-click and select Run As/JUnit Test Note: some of the validation tests
 * comparing the XML-derived Results to the JSON-derived Results are high-level comparisons of Collections.
 * When this class was first created that was sufficient to pass all Conformance tests. However if this sees a
 * failure in a Conformance test, those validations may need to be upgraded to look at the individual data
 * elements to see what is wrong.
 */
public class ResponseConformanceTest {

    // where to find the conformance test XML files
    private final String CONFORMANCE_DIRECTORY_PATH = "testsets/conformance/xacml3.0-ct-v.0.4";

    // The request object output from each test conversion from JSON string
    Response response;

    // test just one of each top-level element.
    // For simple elements also test for incorrect type
    @Test
    public void testConformanceResponses() {

        List<File> filesInDirectory = null;

        File conformanceDirectory = null;

        File currentFile = null;

        try {
            conformanceDirectory = new File(CONFORMANCE_DIRECTORY_PATH);
            filesInDirectory = getRequestsInDirectory(conformanceDirectory);
        } catch (Exception e) {
            fail("Unable to set up Conformance tests for dir '" + conformanceDirectory.getAbsolutePath()
                 + "' e=" + e);
        }

        // run through each XML file
        // - load the file from XML into an internal Response object
        // - generate the JSON representation of that Response object
        // - load that JSON representation into a new Response object
        // - compare the 2 Request objects
        Response xmlResponse = null;
        Response jsonResponse = null;
        try {
            for (File f : filesInDirectory) {
                currentFile = f;

                // // This is a simple way to select just one file for debugging - comment out when not being
                // used
                // if ( ! f.getName().equals("IIIA030Response.xml") && !
                // f.getName().equals("IIIA330Response.xml")) { continue; }

                // during debugging it is helpful to know what file it is starting to work on
                // System.out.println("starting file="+currentFile.getName());

                try {
                    // load XML into a Response object
                    xmlResponse = DOMResponse.load(f);
                } catch (Exception e) {
                    // if XML does not load, just note it and continue with next file
                    System.out.println("XML file did not load: '" + f.getName() + "  e=" + e);
                    continue;
                }

                // some tests have JSON response files to load, most do not
                String jsonFileName = f.getName().replace(".xml", ".json");
                File jsonFile = new File(conformanceDirectory, jsonFileName);

                if (jsonFile.exists()) {
                    // System.out.println("found file "+jsonFile.getName());
                    // json version exists in file, so load it
                    jsonResponse = JSONResponse.load(jsonFile);
                } else {
                    // json does not exist in file, so create it from the XML response using a String
                    // intermediate version
                    String jsonResponseString = JSONResponse.toString(xmlResponse, false);
                    // System.out.println(jsonResponseString);
                    // System.out.println(JSONResponse.toString(xmlResponse, true));

                    jsonResponse = JSONResponse.load(jsonResponseString);
                }

                // System.out.println(JSONResponse.toString(xmlResponse, true));

                // compare the two Response objects

                // compare results
                assertEquals(xmlResponse.getResults().size(), jsonResponse.getResults().size());

                if (xmlResponse.getResults().size() == 0) {
                    fail("neither XML nor JSON response have any Results");
                }

                // Results are an un-ordered Collection.
                // There is no identifying information that is unique to a specific Result.
                // If there are more than one we cannot be sure which one corresponds with which.
                // The best we can do is say that one or more in the first list do not match any in the second
                // list
                if (xmlResponse.getResults().size() > 1) {
                    for (Result xmlResult : xmlResponse.getResults()) {
                        boolean found = false;
                        for (Result jsonResult : jsonResponse.getResults()) {
                            if (xmlResult.equals(jsonResult)) {
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            continue;
                        }
                        // no match found
                        System.out.println("No match for XML in " + f.getName());
                        System.out.println("XML =" + xmlResult.toString());
                        for (Result jsonResult : jsonResponse.getResults()) {
                            System.out.println("JSON=" + jsonResult.toString());
                        }
                        fail("JSON Response has no match for XML Result: " + xmlResult.toString());
                    }
                    // we've done the best we can for multiple decisions, so go to next file
                    continue;
                }

                // single Result in each
                Result xmlResult = xmlResponse.getResults().iterator().next();
                Result jsonResult = jsonResponse.getResults().iterator().next();

                // The following sections have not given us trouble, so checking is very high-level.
                // If we see a problem in one of these elements, the single line will need to be replaced with
                // detailed examination of the objects.
                assertEquals(f.getName() + " Decision", xmlResult.getDecision(), jsonResult.getDecision());
                assertEquals(f.getName() + " Status", xmlResult.getStatus(), jsonResult.getStatus());

                // Obligations
                if (xmlResult.getObligations() != jsonResult.getObligations()) {
                    Collection<Obligation> xmlObligations = xmlResult.getObligations();
                    Collection<Obligation> jsonObligations = jsonResult.getObligations();
                    // if both are null we do not get here
                    if (xmlObligations == null || jsonObligations == null) {
                        fail(f.getName() + " Obligations has null \nXML=" + xmlObligations + "\nJSON="
                             + jsonObligations);
                    }
                    if (ListUtil.equalsAllowNulls(xmlObligations, jsonObligations) == false) {
                        // collections are not equal, so need to examine further
                        fail(f.getName() + " Obligation collections not equal\nXML=" + xmlObligations
                             + "\nJSON=" + jsonObligations);
                    }
                }

                // AssociatedAdvice
                if (xmlResult.getAssociatedAdvice() != jsonResult.getAssociatedAdvice()) {
                    Collection<Advice> xmlAdvice = xmlResult.getAssociatedAdvice();
                    Collection<Advice> jsonAdvice = jsonResult.getAssociatedAdvice();
                    // if both are null we do not get here
                    if (xmlAdvice == null || jsonAdvice == null) {
                        fail(f.getName() + " Advice has null \nXML=" + xmlAdvice + "\nJSON=" + jsonAdvice);
                    }
                    if (ListUtil.equalsAllowNulls(xmlAdvice, jsonAdvice) == false) {
                        // collections are not equal, so need to examine further
                        fail(f.getName() + " Advice collections not equal\nXML=" + xmlAdvice + "\nJSON="
                             + jsonAdvice);
                    }
                }

                // check Attributes in more detail
                Collection<AttributeCategory> xmlAttributes = xmlResult.getAttributes();
                Collection<AttributeCategory> jsonAttributes = jsonResult.getAttributes();
                if (xmlAttributes == null && jsonAttributes != null || xmlAttributes != null
                    && jsonAttributes == null) {
                    fail(f.getName() + " XML Attributes=" + xmlAttributes + "  but JSON Attributes="
                         + jsonAttributes);
                }
                if (xmlAttributes != null) {
                    // both are non-null
                    if (xmlAttributes.size() != jsonAttributes.size()) {
                        String xmlAttributesString = "XML categorys=";
                        for (AttributeCategory ac : xmlAttributes) {
                            xmlAttributesString += " " + ac.getCategory().stringValue();
                        }
                        String jsonAttributesString = "JSON categorys=";
                        for (AttributeCategory ac : jsonAttributes) {
                            jsonAttributesString += " " + ac.getCategory().stringValue();
                        }
                        fail(f.getName() + " XML and JSON have different number of Category elements: "
                             + xmlAttributesString + ", " + jsonAttributesString);
                    }

                    // Attribute collections are the same size but may be in different orders.
                    // for each XML category try to find the corresponding JSON category.
                    // ASSUME that each category only shows up once!!!!
                    for (AttributeCategory xmlAttributeCategory : xmlAttributes) {
                        boolean attributeCategoryFound = false;
                        for (AttributeCategory jsonAttributeCategory : jsonAttributes) {
                            if (xmlAttributeCategory.equals(jsonAttributeCategory)) {
                                attributeCategoryFound = true;
                                break;
                            }
                            // not an exact match, but if same CategoryId then need to check individual
                            // Attribute objects
                            if (xmlAttributeCategory.getCategory()
                                .equals(jsonAttributeCategory.getCategory())) {
                                // same category
                                if (xmlAttributeCategory.getAttributes().size() != jsonAttributeCategory
                                    .getAttributes().size()) {
                                    System.out.println("XML =" + xmlAttributeCategory.getAttributes());
                                    System.out.println("JSON=" + jsonAttributeCategory.getAttributes());
                                    fail(f.getName() + " Attributes Category '"
                                         + xmlAttributeCategory.getCategory().stringValue()
                                         + "' size mismatch; XML="
                                         + xmlAttributeCategory.getAttributes().size() + ", JSON="
                                         + jsonAttributeCategory.getAttributes().size());
                                }
                                for (Attribute xmlAttr : xmlAttributeCategory.getAttributes()) {
                                    boolean attributeFound = false;
                                    for (Attribute jsonAttr : jsonAttributeCategory.getAttributes()) {
                                        if (xmlAttr.equals(jsonAttr)) {
                                            attributeFound = true;
                                            break;
                                        }
                                    }

                                    if (attributeFound) {
                                        // check next XML attribute
                                        continue;
                                    }
                                    System.out.println("Attribute not found in JSON, Category="
                                                       + xmlAttributeCategory.getCategory());
                                    System.out.println("XML Attribute =" + xmlAttr);
                                    System.out.println("JSON Attributes=" + jsonAttributeCategory.toString());
                                    fail(f.getName() + " Attribute not found in JSON, Category="
                                         + xmlAttributeCategory.getCategory() + "/nXML Attribute=" + xmlAttr
                                         + "\nJSON Category Attributes=" + jsonAttributeCategory.toString());
                                }

                            }
                        }
                        if (attributeCategoryFound) {
                            continue;
                        }
                        fail("XML Category not found in JSON; xml=" + xmlAttributeCategory.toString());
                    }

                }

                // PolicyIdentifiers
                if (xmlResult.getPolicyIdentifiers() != jsonResult.getPolicyIdentifiers()) {
                    Collection<IdReference> xmlIdReferences = xmlResult.getPolicyIdentifiers();
                    Collection<IdReference> jsonIdReferences = jsonResult.getPolicyIdentifiers();
                    // if both are null we do not get here
                    if (xmlIdReferences == null || jsonIdReferences == null) {
                        fail(f.getName() + " PolicyIdentifiers has null \nXML=" + xmlIdReferences + "\nJSON="
                             + jsonIdReferences);
                    }
                    if (ListUtil.equalsAllowNulls(xmlIdReferences, jsonIdReferences) == false) {
                        // collections are not equal, so need to examine further
                        fail(f.getName() + " PolicyIdentifiers collections not equal\nXML=" + xmlIdReferences
                             + "\nJSON=" + jsonIdReferences);
                    }
                }

                // PolicySetIdentifiers
                if (xmlResult.getPolicySetIdentifiers() != jsonResult.getPolicySetIdentifiers()) {
                    Collection<IdReference> xmlIdReferences = xmlResult.getPolicySetIdentifiers();
                    Collection<IdReference> jsonIdReferences = jsonResult.getPolicySetIdentifiers();
                    // if both are null we do not get here
                    if (xmlIdReferences == null || jsonIdReferences == null) {
                        fail(f.getName() + " PolicySetIdentifiers has null \nXML=" + xmlIdReferences
                             + "\nJSON=" + jsonIdReferences);
                    }
                    if (ListUtil.equalsAllowNulls(xmlIdReferences, jsonIdReferences) == false) {
                        // collections are not equal, so need to examine further
                        fail(f.getName() + " PolicySetIdentifiers collections not equal\nXML="
                             + xmlIdReferences + "\nJSON=" + jsonIdReferences);
                    }
                }

            }

        } catch (Exception e) {
            fail("Failed test with '" + currentFile.getName() + "', e=" + e);
        }

    }

    //
    // HELPER to get list of all Request files in the given directory
    //

    private List<File> getRequestsInDirectory(File directory) {
        List<File> fileList = new ArrayList<File>();

        File[] fileArray = directory.listFiles();
        for (File f : fileArray) {
            if (f.isDirectory()) {
                List<File> subDirList = getRequestsInDirectory(f);
                fileList.addAll(subDirList);
            }
            if (f.getName().endsWith("Response.xml")) {
                fileList.add(f);
            }
        }
        return fileList;

    }

}

/*
 * This is a place to copy the really long output from test rigs that need to be manually edited for
 * readability....
 * {"Response":[{"Status":{"StatusCode":{"Value":"urn:oasis:names:tc:xacml:1.0:status:ok"}},"Obligations"
 * :[{"Id":"urn:oasis:names:tc:xacml:2.0:conformance-test:IIIA030:obligation-1","AttributeAssignment":[
 * {"Value":"assignment1","DataType":"string","AttributeId":
 * "urn:oasis:names:tc:xacml:2.0:conformance-test:IIIA030:assignment1"},
 * {"Value":{"Namespaces":[{"Namespace":"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
 * },{"Namespace":"http://www.w3.org/2001/XMLSchema-instance","Prefix":"xsi"}],
 * "XPathCategory":"urn:oasis:names:tc:xacml:3.0:attribute-category:resource",
 * "XPath":"//md:records/md:record"}, "DataType":"xpathExpression",
 * "AttributeId":"urn:oasis:names:tc:xacml:2.0:conformance-test:IIIA030:assignment2"
 * }]}],"Decision":"Permit"}]}
 */

