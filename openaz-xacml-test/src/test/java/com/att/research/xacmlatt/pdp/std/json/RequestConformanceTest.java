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
package com.att.research.xacmlatt.pdp.std.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.RequestAttributes;
import com.att.research.xacml.api.RequestReference;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.std.json.JSONStructureException;
/**
 * Test JSON Request convert to object - Conformance tests
 *
 * TO RUN - use jUnit
 * In Eclipse select this file or the enclosing directory, right-click and select Run As/JUnit Test
 *
 * NOTE:
 * The "correct" way to verify that each JSON string gets translated into our internal Objects correctly is to look explicitly at each of the child objects
 * and verify that they are correct.  This would involve a lot of coding to get child of child of child and individually verify each property of each element.
 * To simplify testing we assume that request.toString() correctly includes a complete text representation of every sub-component of the Request object
 * and we compare the resulting String to our expected String.
 * This has two possible sources of error:
 *      - toString might not include some sub-component, and
 *      - the initial verification of the resulting string is done by hand and may have been incorrect.
 *
 *
 */
public class RequestConformanceTest {

    // where to find the conformance test XML files
    private final String CONFORMANCE_DIRECTORY_PATH = "testsets/conformance/xacml3.0-ct-v.0.4";

    // The request object output from each test conversion from JSON string
    Request request;





    // test just one of each top-level element.
    // For simple elements also test for incorrect type
    @Test
    public void testConformanceRequests() {

        List<File> filesInDirectory = null;

        File conformanceDirectory = null;

        File currentFile = null;

        try {
            conformanceDirectory = new File(CONFORMANCE_DIRECTORY_PATH);
            filesInDirectory = getRequestsInDirectory(conformanceDirectory);
        } catch (Exception e) {
            fail("Unable to set up Conformance tests for dir '" + conformanceDirectory.getAbsolutePath()+"' e="+ e);
        }

        // run through each XML file
        //      - load the file from XML into an internal Request object
        //      - generate the JSON representation of that Request object
        //      - load that JSON representation into a new Request object
        //      - compare the 2 Request objects
        Request xmlRequest = null;
        Request jsonRequest = null;
        try {
            for (File f : filesInDirectory) {
                currentFile = f;

//// This is a simple way to select just one file for debugging - comment out when not being used
//if ( ! f.getName().equals("IIA023Request.xml")) {   continue;  }

// during debugging it is helpful to know what file it is starting to work on
//                              System.out.println("starting file="+currentFile.getName());

                try {
                    // load XML into a Request object
                    xmlRequest = DOMRequest.load(f);
                    xmlRequest.getStatus();
                } catch (Exception e) {
                    // if XML does not load, just note it and continue with next file
                    System.out.println("XML file did not load: '" + f.getName() + "  e=" + e);
                    continue;
                }

//System.out.println(JSONRequest.toString(xmlRequest, false));

                // generate JSON from the Request
                String jsonString = JSONRequest.toString(xmlRequest, false);


// single-value elements
//jsonString = "{\"Request\":{\"Category\":[{\"CategoryId\":\"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\",\"Attribute\":["
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"Julius Hibbert\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"IncludeInResult\":\"true\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":123,\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-string\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"true\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#boolean\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-boolean\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"56\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-integer\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"27.5\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#double\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-double\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"2002-03-22\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#date\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-date\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"2002-03-22T08:23:47-05:00\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#dateTime\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-dateTime\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"P50DT5H4M3S\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#dayTimeDuration\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-dayTimeDuration\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"-P5Y3M\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#yearMonthDuration\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-yearMonthDuration\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"0bf7a9876cde\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#hexBinary\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-hexBinary\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"c3VyZS4=\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#base64Binary\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-rfc822Name\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"j_hibbert@medico.com\",\"DataType\":\"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-rfc822Name\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"CN=Julius Hibbert, O=Medi Corporation, C=US\",\"DataType\":\"urn:oasis:names:tc:xacml:1.0:data-type:x500Name\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-x500Name\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"122.45.38.245/255.255.255.64:8080-8080\",\"DataType\":\"urn:oasis:names:tc:xacml:2.0:data-type:ipAddress\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-ipAddress\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"some.host.name:147-874\",\"DataType\":\"urn:oasis:names:tc:xacml:2.0:data-type:dnsName\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-dnsName\"}"
//              + "]},"
//              + "{\"CategoryId\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"Attribute\":["
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":{\"Namespaces\":[{\"Namespace\":\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\"},{\"Namespace\":\"http://www.medico.com/schemas/record\",\"Prefix\":\"md\"},{\"Namespace\":\"http://www.w3.org/2001/XMLSchema-instance\",\"Prefix\":\"xsi\"}],\"XPathCategory\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"XPath\":\"//md:records/md:record\"},\"DataType\":\"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:resource:xpathExpression\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"http://medico.com/record/patient/BartSimpson\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#anyURI\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\"}"
//              + "]},"
//              + "{\"CategoryId\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\",\"Attribute\":["
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"read\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:action:action-id\"}"
//              + "]},"
//              + "{\"CategoryId\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\",\"Attribute\":["
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"08:23:47-05:00\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#time\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:environment:current-time\"}"
//              + "]}],\"ReturnPolicyIdList\":false,\"CombinedDecision\":false}}";






// array attributes WITH explicit data types




// String for testing Arrays of Attribute values
//jsonString = "{\"Request\":{\"Category\":[{\"CategoryId\":\"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\",\"Attribute\":["
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":[\"test string\",\"Julius Hibbert\",\"Julius Hibbert as string\"],\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":[true, false],\"DataType\":\"http://www.w3.org/2001/XMLSchema#boolean\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-boolean\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":[56, 12],\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-integer\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":[27.12, 12112.344],\"DataType\":\"http://www.w3.org/2001/XMLSchema#double\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-double\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":[\"2002-03-22\",\"1256-11-11\"],\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-date\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":[\"2002-03-22T08:23:47-05:00\",\"1056-11-05T19:08:12-14:30\"],\"DataType\":\"http://www.w3.org/2001/XMLSchema#dateTime\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-dateTime\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":[\"P50DT5H4M3S\",\"P12DT148H18M21S\"],\"DataType\":\"http://www.w3.org/2001/XMLSchema#dayTimeDuration\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-dayTimeDuration\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"-P5Y3M\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#yearMonthDuration\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-yearMonthDuration\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"-P28Y7M\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#yearMonthDuration\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-yearMonthDuration\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"0bf7a9876cde\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#hexBinary\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-hexBinary\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"0fb8\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#hexBinary\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-hexBinary\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"c3VyZS4=\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#base64Binary\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-rfc822Name\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"YXN1cmUu\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#base64Binary\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-rfc822Name\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"j_hibbert@medico.com\",\"DataType\":\"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-rfc822Name\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"c_clown@nose_medico.com\",\"DataType\":\"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-rfc822Name\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"CN=Julius Hibbert, O=Medi Corporation, C=US\",\"DataType\":\"urn:oasis:names:tc:xacml:1.0:data-type:x500Name\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-x500Name\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"CN=Crusty Clown, O=Red Nose Corporation, C=US\",\"DataType\":\"urn:oasis:names:tc:xacml:1.0:data-type:x500Name\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-x500Name\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"122.45.38.245/255.255.255.64:8080-8080\",\"DataType\":\"urn:oasis:names:tc:xacml:2.0:data-type:ipAddress\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-ipAddress\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"35.123.111.56/255.64.32.255:9999-9999\",\"DataType\":\"urn:oasis:names:tc:xacml:2.0:data-type:ipAddress\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-ipAddress\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"some.host.name:147-874\",\"DataType\":\"urn:oasis:names:tc:xacml:2.0:data-type:dnsName\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-dnsName\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"a.different.host:-45\",\"DataType\":\"urn:oasis:names:tc:xacml:2.0:data-type:dnsName\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject:subject-dnsName\"}"
//              + "]},"
//              + "{\"CategoryId\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"Attribute\":["
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":{\"Namespaces\":[{\"Namespace\":\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\"},{\"Namespace\":\"http://www.medico.com/schemas/record\",\"Prefix\":\"md\"},{\"Namespace\":\"http://www.w3.org/2001/XMLSchema-instance\",\"Prefix\":\"xsi\"}],\"XPathCategory\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"XPath\":\"//md:records/md:record\"},\"DataType\":\"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:resource:xpathExpression\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":{\"Namespaces\":[{\"Namespace\":\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\"},{\"Namespace\":\"http://www.medico.com/schemas/record\",\"Prefix\":\"md\"},{\"Namespace\":\"http://www.w3.org/2001/XMLSchema-instance\",\"Prefix\":\"xsi\"}],\"XPathCategory\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"XPath\":\"//md:records/md:diagnosis_info\"},\"DataType\":\"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:resource:xpathExpression\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"http://medico.com/record/patient/BartSimpson\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#anyURI\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"http://medico.com/record/patient/HomerSimpson\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#anyURI\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\"}"
//              + "],"
//              + "\"Content\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><md:records xmlns:md=\\\"http://www.medico.com/schemas/record\\\">\\r\\n            \\t<md:record>\\r\\n                \\t<md:patient_info>\\r\\n                    \\t<md:name>Bart Simpson</md:name>\\r\\n                    \\t<md:age>60</md:age>\\r\\n                    \\t<md:sex>male</md:sex>\\r\\n                    \\t<md:health_insurance>123456</md:health_insurance>\\r\\n                \\t</md:patient_info>\\r\\n                \\t<md:diagnosis_info>\\r\\n                    \\t<md:diagnosis>\\r\\n                        \\t<md:item type=\\\"primary\\\">Gastric Cancer</md:item>\\r\\n                        \\t<md:item type=\\\"secondary\\\">Hyper tension</md:item>\\r\\n                    \\t</md:diagnosis>\\r\\n                    \\t<md:pathological_diagnosis>\\r\\n                        \\t<md:diagnosis>\\r\\n                            \\t<md:item type=\\\"primary\\\">Well differentiated adeno carcinoma</md:item>\\r\\n                        \\t</md:diagnosis>\\r\\n                        \\t<md:date>2000-10-05</md:date>\\r\\n                        \\t<md:malignancy type=\\\"yes\\\"/>\\r\\n                    \\t</md:pathological_diagnosis>\\r\\n                \\t</md:diagnosis_info>                \\r\\n            \\t</md:record>\\r\\n            \\t<md:record>\\r\\n                \\t<md:patient_info>\\r\\n                    \\t<md:name>Homer Simpson</md:name>\\r\\n                    \\t<md:age>80</md:age>\\r\\n                    \\t<md:sex>male</md:sex>\\r\\n                    \\t<md:health_insurance>123456</md:health_insurance>\\r\\n                \\t</md:patient_info>\\r\\n                \\t<md:diagnosis_info>\\r\\n                    \\t<md:diagnosis>\\r\\n                        \\t<md:item type=\\\"primary\\\">Gastric Cancer</md:item>\\r\\n                        \\t<md:item type=\\\"secondary\\\">Hyper tension</md:item>\\r\\n                    \\t</md:diagnosis>\\r\\n                    \\t<md:pathological_diagnosis>\\r\\n                        \\t<md:diagnosis>\\r\\n                            \\t<md:item type=\\\"primary\\\">Well differentiated adeno carcinoma</md:item>\\r\\n                        \\t</md:diagnosis>\\r\\n                        \\t<md:date>2000-10-05</md:date>\\r\\n                        \\t<md:malignancy type=\\\"yes\\\"/>\\r\\n                    \\t</md:pathological_diagnosis>\\r\\n                \\t</md:diagnosis_info>                \\r\\n            \\t</md:record>\\r\\n\\t    </md:records>\"},"
//              + "{\"CategoryId\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\",\"Attribute\":["
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"read\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:action:action-id\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"write\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:action:action-id\"}"
//              + "]},"
//              + "{\"CategoryId\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\",\"Attribute\":["
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"08:23:47-05:00\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#time\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:environment:current-time\"},"
//              + "{\"Issuer\":\"ConformanceTester\",\"Value\":\"22:12:10Z\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#time\",\"IncludeInResult\":true,\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:environment:current-time\"}]}]"
//              + ",\"ReturnPolicyIdList\":false,\"CombinedDecision\":false}}";




                // load JSON into a Request
                jsonRequest = JSONRequest.load(jsonString);

                // compare the two Request objects

                // check simple things first
                assertEquals("File '" + currentFile.getName() + "' CombinedDecision", xmlRequest.getCombinedDecision(), jsonRequest.getCombinedDecision());
                assertEquals("File '" + currentFile.getName() + "' getReturnPolicyIdList", xmlRequest.getReturnPolicyIdList(), jsonRequest.getReturnPolicyIdList());
                assertEquals("File '" + currentFile.getName() + "' requestDefaults", xmlRequest.getRequestDefaults(), jsonRequest.getRequestDefaults());

                // multiRequests (guaranteed to not be null)
                // We do NOT care about ordering, so compare the two collections inefficiently
                Collection<RequestReference> xmlCollection = xmlRequest.getMultiRequests();
                Collection<RequestReference> jsonCollection = jsonRequest.getMultiRequests();
                String errorMessage = null;
                if (jsonCollection.size() != xmlCollection.size()) {
                    errorMessage = "File '" + currentFile.getName() + "' MultiRequests not same size.  ";
                } else if (! jsonCollection.containsAll(xmlCollection)) {
                    errorMessage = "File '" + currentFile.getName() + "' MultiRequests have different contents.  ";
                }
                if (errorMessage != null) {
                    String xmlContents = "";
                    String jsonContents = "";
                    Iterator<RequestReference> rrIt = xmlCollection.iterator();
                    while (rrIt.hasNext()) {
                        xmlContents += "\n   " + rrIt.next().toString();
                    }
                    rrIt = jsonCollection.iterator();
                    while (rrIt.hasNext()) {
                        jsonContents += "\n  " + rrIt.next().toString();
                    }
                    fail(errorMessage + "\nXML(" + xmlCollection.size() + ")='" + xmlContents +
                         "'  \nJSON(" + jsonCollection.size() + ")='" + jsonContents +
                         "'" +
                         "\njson='" + jsonString + "'");
                }

                // attributes (guaranteed to not be null)
                // We do NOT care about ordering, so compare the two collections inefficiently
                Collection<RequestAttributes> xmlAttrCollection = xmlRequest.getRequestAttributes();
                Collection<RequestAttributes> jsonAttrCollection = jsonRequest.getRequestAttributes();
                errorMessage = null;
                if (jsonAttrCollection.size() != xmlAttrCollection.size()) {
                    errorMessage = "File '" + currentFile.getName() + "' RequestAttributes not same size.  ";
                } else if (! jsonAttrCollection.containsAll(xmlAttrCollection)) {
                    String attrName = "";
                    Iterator<RequestAttributes> rait = xmlAttrCollection.iterator();
                    while (rait.hasNext()) {
                        RequestAttributes ra = rait.next();
                        if (jsonAttrCollection.contains(ra) == false) {
                            attrName = ra.toString();
                        }
                    }
                    errorMessage = "File '" + currentFile.getName() + "' RequestAttributes have different contents.  JSON is missing attr=" + attrName;
                }
                if (errorMessage != null) {
                    String xmlContents = "";
                    String jsonContents = "";
                    Iterator<RequestAttributes> rrIt = xmlAttrCollection.iterator();
                    while (rrIt.hasNext()) {
                        RequestAttributes ras = rrIt.next();
                        xmlContents += "\n   " + ras.toString();
                        if (ras.getContentRoot() != null) {
                            StringWriter writer = new StringWriter();
                            Transformer transformer = null;
                            try {
                                transformer = TransformerFactory.newInstance().newTransformer();
                                transformer.transform(new DOMSource(ras.getContentRoot()), new StreamResult(writer));
                            } catch (Exception e) {
                                throw new JSONStructureException("Unable to Content node to string; e="+e);
                            }

                            xmlContents += "\n        Content: " + writer.toString();
                        }
                    }
                    rrIt = jsonAttrCollection.iterator();
                    while (rrIt.hasNext()) {
                        RequestAttributes ras = rrIt.next();
                        jsonContents += "\n   " + ras.toString();
                        if (ras.getContentRoot() != null) {
                            StringWriter writer = new StringWriter();
                            Transformer transformer = null;
                            try {
                                transformer = TransformerFactory.newInstance().newTransformer();
                                transformer.transform(new DOMSource(ras.getContentRoot()), new StreamResult(writer));
                            } catch (Exception e) {
                                throw new JSONStructureException("Unable to Content node to string; e="+e);
                            }

                            jsonContents += "\n        Content: " + writer.toString();
                        }
                    }
                    fail(errorMessage + "\nXML(" + xmlAttrCollection.size() + ")='" + xmlContents +
                         "'  \nJSON(" + jsonAttrCollection.size() + ")='" + jsonContents +
                         "\njson='" + jsonString + "'");
                }


            }

        } catch (Exception e) {
            fail ("Failed test with '" + currentFile.getName() + "', e=" + e);
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
            if (f.getName().endsWith("Request.xml")) {
                fileList.add(f);
            }
        }
        return fileList;

    }

}




/*
Place to dump very long trace/exception strings that need manual editing to understand






 */









