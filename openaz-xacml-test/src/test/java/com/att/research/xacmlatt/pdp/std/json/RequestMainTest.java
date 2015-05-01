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

import org.junit.Test;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.std.json.JSONStructureException;
/**
 * Test JSON Request convert to object - High-level Request-as-a-whole tests including test that fills in all fields with multiple values (where appropriate)
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
 * 	- toString might not include some sub-component, and
 * 	- the initial verification of the resulting string is done by hand and may have been incorrect.
 *
 * @author glenngriffin
 *
 */
public class RequestMainTest {

    // The request object output from each test conversion from JSON string
    Request request;


    /*
     * Request that uses all fields with both single and multiple  entries
     */
    String allFieldsRequest =
        "{\"Request\": {" +
        "\"ReturnPolicyIdList\" : true ," +
        "\"CombinedDecision\" : true ," +
        "\"XPathVersion\" : \"http://www.w3.org/TR/1999/REC-xpath-19991116\"," +
        "\"MultiRequests\" : {" +
        "\"RequestReference\": [" +
        "{ " +
        "\"ReferenceId\" : [\"foo1\",\"bar1\"]" +
        "}," +
        "{" +
        "\"ReferenceId\" : [\"foo2\",\"bar1\"]" +
        "}]" +
        "}," +

        "\"Category\": [" +
        "{ " +
        "\"CategoryId\": \"custom-category\", " +
        "\"Id\" : \"customId\", " +
        "\"Attribute\" : [" +
        "{" +
        "\"AttributeId\"		: \"document-id\", " +
        "\"DataType\"	: \"integer\", " +
        "\"Value\"	: 123 " +
        "}, " +
        "{" +
        "\"AttributeId\"		: \"document-url\", " +
        "\"DataType\"	: \"anyURI\", " +
        "\"Value\"	: \"http://somewhere.over.the.com/rainbow\" " +
        "}, " +
        "{" +
        "\"AttributeId\"		: \"page-list\", " +
        "\"Value\"	: [1, 2, 3, 4.5, 3, 2, 1] " +
        "} " +
        "]" +
        "}, " +
        "{ " +
        "\"CategoryId\": \"another-custom-cat\", " +
        "\"Id\" : \"anotherXmlId\", " +
        "\"Attribute\" : []" +
        "} " +
        "], " +

        "\"AccessSubject\":{ " +
        "\"Content\" : \"<?xml version=\\\"1.0\\\"?><catalog>" +
        "<book id=\\\"bk101\\\"><author>Gambardella, Matthew</author><title>XML Developer's Guide</title><genre>Computer</genre>" +
        "<price>44.95</price><publish_date>2000-10-01</publish_date><description>An in-depth look at creating applications with XML.</description>"+
        "</book></catalog>\"," +
        "\"Attribute\" : []" +
        "}, " +

        "\"Resource\" : {" +
        "\"Content\" : \"PD94bWwgdmVyc2lvbj0iMS4wIj8+PGNhdGFsb2c+PGJvb2sgaWQ9ImJrMTAxIj48YXV0aG9yPkdhbWJhcmRlbGxhLCBNYXR0aGV3PC9hdXRob3I+PHRpdGxlPlhNT" +
        "CBEZXZlbG9wZXIncyBHdWlkZTwvdGl0bGU+PGdlbnJlPkNvbXB1dGVyPC9nZW5yZT48cHJpY2U+NDQuOTU8L3ByaWNlPjxwdWJsaXNoX2RhdGU+MjAwMC0xMC0wMTwvcHVibGlzaF"+
        "9kYXRlPjxkZXNjcmlwdGlvbj5BbiBpbi1kZXB0aCBsb29rIGF0IGNyZWF0aW5nIGFwcGxpY2F0aW9ucyB3aXRoIFhNTC48L2Rlc2NyaXB0aW9uPjwvYm9vaz48L2NhdGFsb2c+\"" +


        "} " +


        "}}";

    /*
     * The following example comes directly from the JSON Profile Spec
     */
    String exampleFromSpec = "{ " +
                             "\"Request\" : { " +
                             "\"AccessSubject\" : { " +
                             "\"Attribute\": [ " +
                             "{ " +
                             "\"Id\" : \"subject-id\", " +
                             "\"Value\" : \"Andreas\" " +
                             "}, " +
                             "{ " +
                             "\"Id\" : \"location\", " +
                             "\"Value\" : \"Gamla Stan\" " +
                             "} " +
                             "] " +
                             "}, " +
                             "\"Action\" : { " +
                             "\"Attribute\":  " +
                             "{ " +
                             "\"Id\" : \"action-id\", " +
                             "\"Value\" : \"http://www.xacml.eu/buy\", " +
                             "\"DataType\" : \"anyURI\" " +
                             "} " +
                             "}, " +
                             "\"Resource\" : { " +
                             "\"Attribute\": [ " +
                             "{ " +
                             "\"Id\" : \"book-title\", " +
                             "\"Value\" : \"Learn German in 90 days\" " +
                             "}, " +
                             "{ " +
                             "\"Id\" : \"currency\", " +
                             "\"Value\" : \"SEK\" " +
                             "}, " +
                             "{ " +
                             "\"Id\" : \"price\", " +
                             "\"Value\" : 123.34 " +
                             "} " +
                             "] " +
                             "} " +
                             "} " +
                             "} ";


    /*
     * The following example comes directly from the JSON Profile Spec (modified to include a "</Catalog>" missing from both examples).
     * It shows the two ways of handling XPath content, as escaped XML and as Base64 encoding.
     */
    String xPathExampleFromSpec = "{ " +
                                  "\"Request\" : { " +
                                  "\"Resource\" : { " +
                                  "\"Attribute\": [ " +
                                  "{ " +
                                  "\"Id\" : \"urn:oasis:names:tc:xacml:3.0:content-selector\", " +
                                  "\"DataType\" : \"xpathExpression\", " +
                                  "\"Value\" : { " +
                                  "\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\", " +
                                  "\"Namespaces\" : [{ " +
                                  "\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
                                  "}, " +
                                  "{ " +
                                  "\"Prefix\" : \"md\", " +
                                  "\"Namespace\" : \"urn:example:med:schemas:record\" " +
                                  "} " +
                                  "], " +
                                  "\"XPath\" : \"md:record/md:patient/md:patientDoB\" " +
                                  "} " +
                                  "} " +
                                  "] " +
                                  "} " +
                                  "} " +
                                  "} ";



    // test various ways that request might be empty
    @Test
    public void testEmptyRequest() {
        // null request
        try {
            request = JSONRequest.load((String)null);
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // empty request
        try {
            request = JSONRequest.load((String)"");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)" ");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // empty JSON request
        try {
            request = JSONRequest.load((String)"{}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{{}}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // garbage input
        try {
            request = JSONRequest.load((String)"Some non-JSON string");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{something non-JSON}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // bad syntax (Request with no content)
        try {
            request = JSONRequest.load((String)"{\"Request\"}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // bad syntax (no :field after Request)
        try {
            request = JSONRequest.load((String)"{\"Request\" : }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // bad syntax (no " around Request)
        try {
            request = JSONRequest.load((String)"{Request}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // empty content in Request
        try {
            request = JSONRequest.load((String)"{\"Request\" : \"\"}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // content is not an object
        try {
            request = JSONRequest.load((String)"{\"Request\" : \"CombinedDecision\" : true }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // too many } at end
        // Jackson parser does not treat this as an error
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"XPathVersion\" : \"http://www.w3.org/TR/1999/REC-xpath-19991116\"}}}}}");
            assertEquals("{requestDefaults={xpatherVersion=http://www.w3.org/TR/1999/REC-xpath-19991116},returnPolicyIdList=false,combinedDecision=false}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // too few } at end
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"XPathVersion\" : \"http://www.w3.org/TR/1999/REC-xpath-19991116\" }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }


        // misplaced } in middle
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"XPathVersion\" : } \"http://www.w3.org/TR/1999/REC-xpath-19991116\"}}}}}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }


    }



    // Test double braces around request
    @Test
    public void testDoubleBraces() {

        try {
            request = JSONRequest.load((String)"{{\"Request\" }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{{\"Request\" : }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{{\"Request\" : {\"CombinedDecision\" : true }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }
    }



    // test elements missing from top-level Request and arrays where single elements should be
    @Test
    public void testMissingFields() {

        // Request containing empty array
        try {
            request = JSONRequest.load((String)"{\"Request\" : []}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // array of one element
        try {
            request = JSONRequest.load((String)"{\"Request\" : [{\"CombinedDecision\" : true }]}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // correctly formatted empty request gives request with defaults set
        try {
            request = JSONRequest.load((String)"{\"Request\" : { }}");
            assertEquals("{returnPolicyIdList=false,combinedDecision=false}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }


        // space in front of name (inside quotes)
        try {
            request = JSONRequest.load((String)"{\" Request\" : {\"XPathVersion\" : \"http://some/other/default/uri\" }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // space at end of name (inside quotes)
        try {
            request = JSONRequest.load((String)"{\"Request \" : {\"XPathVersion\" : \"http://some/other/default/uri\" }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // space in front of value (inside quotes) - valid String but not valid URI
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"XPathVersion\" : \" http://some/other/default/uri\" }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // space at end of value (inside quotes) - valid String but not valid URI
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"XPathVersion\" : \"http://some/other/default/uri \" }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

    }



    // test just one of each top-level element.
    // For simple elements also test for incorrect type
    @Test
    public void testTopLevelElements() {

        // empty request
        try {
            request = JSONRequest.load((String)"{\"Request\" : {}}");
            assertEquals("{returnPolicyIdList=false,combinedDecision=false}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }



        // ReturnPolicyIdList
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"ReturnPolicyIdList\" : true  }}");
            assertEquals("{returnPolicyIdList=true,combinedDecision=false}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"ReturnPolicyIdList\" : \"abc\"  }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"ReturnPolicyIdList\" : 123  }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // CombinedDecision
        try {
            request = JSONRequest.load((String)"{\"Request\" : { \"CombinedDecision\" : true }}");
            assertEquals("{returnPolicyIdList=false,combinedDecision=true}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"CombinedDecision\" : \"abc\"  }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"CombinedDecision\" : 123  }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // XPathVersion
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"XPathVersion\" : \"http://some/other/default/uri\" }}");
            assertEquals("{requestDefaults={xpatherVersion=http://some/other/default/uri},returnPolicyIdList=false,combinedDecision=false}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"XPathVersion\" : true  }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"XPathVersion\" : 123  }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"XPathVersion\" : \"not a uri\" }}");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }


        // Category
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"Category\": [{ " +
                                       "\"CategoryId\": \"another-custom-cat\", " +
                                       "\"Id\" : \"anotherXmlId\", " +
                                       "\"Attribute\" : []" +
                                       "} " +
                                       "] }}");
            assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=another-custom-cat},xmlId=anotherXmlId}]}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // AccessSubject
        try {
            request = JSONRequest.load((String)"{\"Request\" : { \"AccessSubject\":{ }}}");
            assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject}}]}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // Action
        try {
            request = JSONRequest.load((String)"{\"Request\" : { \"Action\":{ }}}");
            assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:action}}]}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // Resource
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"Resource\":{ }}}");
            assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource}}]}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // Environment
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"Environment\":{ } }}");
            assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment}}]}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // MultiRequests
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{ " +
                                       "\"ReferenceId\" : [\"foo1\",\"bar1\"]" +
                                       "}," +
                                       "{" +
                                       "\"ReferenceId\" : [\"foo2\",\"bar2\"]" +
                                       "}]" +
                                       "} } }");
            assertEquals("{returnPolicyIdList=false,combinedDecision=false,multiRequests=[{requestAttributesReferences=[{referenceId=foo1}{referenceId=bar1}]}{requestAttributesReferences=[{referenceId=foo2}{referenceId=bar2}]}]}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // MultiRequest with 1
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{" +
                                       "\"ReferenceId\" : [\"bar2\"]" +
                                       "}]" +
                                       "} } }");
            assertEquals("{returnPolicyIdList=false,combinedDecision=false,multiRequests=[{requestAttributesReferences=[{referenceId=bar2}]}]}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // MultiRequest with RequestReferences with no ReferenceId
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{" +
                                       "\"ReferenceId\" : []" +
                                       "}]" +
                                       "} } }");
            assertEquals("{returnPolicyIdList=false,combinedDecision=false}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }


        // MultiRequests with no RequestReference
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": []" +
                                       "} } }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // MultiRequests with something other than RequestReference
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"SomeOtherAttribute\": 123" +
                                       "} } }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // MultiRequests with single RequestReference rather than array
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": " +
                                       "{" +
                                       "\"ReferenceId\" : []" +
                                       "}" +
                                       "} } }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // MultiRequests with RequestReference containing single element instead of array
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{" +
                                       "\"ReferenceId\" : \"foo1\"" +
                                       "}]" +
                                       "} } }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{" +
                                       "\"ReferenceId\" : {\"foo1\"}" +
                                       "}]" +
                                       "} } }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // MultiRequests with component that is not a RequestReference
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{ " +
                                       "\"SomeOtherAttribute\" : [\"foo1\",\"bar1\"]" +
                                       "}," +
                                       "{" +
                                       "\"ReferenceId\" : [\"foo2\",\"bar2\"]" +
                                       "}]" +
                                       "} } }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // MultiRequests with component that is not a RequestReference
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{ " +
                                       "\"ReferenceId\" : [\"foo2\",\"bar2\"]," +
                                       "\"SomeOtherAttribute\" : [\"foo1\",\"bar1\"]" +
                                       "}," +
                                       "{" +
                                       "\"ReferenceId\" : [\"foo2\",\"bar2\"]" +
                                       "}]" +
                                       "} } }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // MultiRequest with unknown elements (in addition to RequestReference)
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"SomeOtherAttribute\": 123," +
                                       "\"RequestReference\": [" +
                                       "{ " +
                                       "\"ReferenceId\" : [\"foo1\",\"bar1\"]" +
                                       "}," +
                                       "{" +
                                       "\"ReferenceId\" : [\"foo2\",\"bar2\"]" +
                                       "}]" +
                                       "} } }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // MultiRequest with RequestReferences with  ReferenceId NOT a string
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{" +
                                       "\"ReferenceId\" : [ true ]" +
                                       "}]" +
                                       "} } }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{" +
                                       "\"ReferenceId\" : [ 123 ]" +
                                       "}]" +
                                       "} } }");
            fail("Request should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }


        // Cannot test with ReferenceId that is NOT referring to a Category object Id property because we may not have read the Category objects yet.
        // Need to leave this up to the PDP.


        // extra elements in top-level
        try {
            request = JSONRequest.load((String)"{\"Request\" : {}, \"unknownElement\" : false, \"unk2\" : \"abc\", \"unk3\" : 123 }}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // extra elements in Request
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"XPathVersion\" : \"http://www.w3.org/TR/1999/REC-xpath-19991116\", \"unknownElement\" : false }}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }
    }


    // Test with every field filled in with multiple values where appropriate
    @Test
    public void testAllFieldsRequest() {

        // convert Response to JSON
        try {
            request = JSONRequest.load(allFieldsRequest);
            assertEquals("{requestDefaults={xpatherVersion=http://www.w3.org/TR/1999/REC-xpath-19991116},returnPolicyIdList=true,combinedDecision=true,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}],includeInResults=false}{attributeId=document-url,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=http://somewhere.over.the.com/rainbow}],includeInResults=false}{attributeId=page-list,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=1.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=2.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=3.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=4.5}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=3.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=2.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=1.0}],includeInResults=false}]},xmlId=customId}{super={category=another-custom-cat},xmlId=anotherXmlId}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject},contentRoot=[catalog: null]}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource},contentRoot=[catalog: null]}],multiRequests=[{requestAttributesReferences=[{referenceId=foo1}{referenceId=bar1}]}{requestAttributesReferences=[{referenceId=foo2}{referenceId=bar1}]}]}"
                         , request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }


        // convert example request from spec
        try {
            request = JSONRequest.load(exampleFromSpec);
            assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,attributes=[{attributeId=action-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=http://www.xacml.eu/buy}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,attributes=[{attributeId=book-title,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Learn German in 90 days}],includeInResults=false}{attributeId=currency,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=SEK}],includeInResults=false}{attributeId=price,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=123.34}],includeInResults=false}]}}]}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // convert example request from spec containing XPAthExpression
        try {
            request = JSONRequest.load(xPathExampleFromSpec);
            assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,attributes=[{attributeId=urn:oasis:names:tc:xacml:3.0:content-selector,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression,value={path=md:record/md:patient/md:patientDoB,Namespace={[{md,urn:example:med:schemas:record}{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}]},status=null,xpathExpressionWrapped=null},xpathCategory=urn:oasis:names:tc:xacml:3.0:attribute-category:resource}],includeInResults=false}]}}]}", request.toString());
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }


    }



    // Duplicates - Each element duplicated
    @Test
    public void testDuplicates() {
        // duplicate Request
        try {
            request = JSONRequest.load((String)"{\"Request\" : {}, \"Request\" : {}}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }


        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"ReturnPolicyIdList\" : true, \"ReturnPolicyIdList\" : true   }}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{\"Request\" : { \"CombinedDecision\" : true, \"CombinedDecision\" : true }}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }


        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"Category\": [{ " +
                                       "\"CategoryId\": \"another-custom-cat\", " +
                                       "\"Id\" : \"anotherXmlId\", " +
                                       "\"Attribute\" : []" +
                                       "} " +
                                       "],"
                                       + "\"Category\": [{ " +
                                       "\"CategoryId\": \"another-custom-cat\", " +
                                       "\"Id\" : \"anotherXmlId\", " +
                                       "\"Attribute\" : []" +
                                       "} " +
                                       "] }}");
            assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=another-custom-cat},xmlId=anotherXmlId}]}", request.toString());
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"Category\": [{ " +
                                       "\"CategoryId\": \"another-custom-cat\", " +
                                       "\"CategoryId\": \"another-custom-cat\", " +
                                       "\"Id\" : \"anotherXmlId\", " +
                                       "\"Attribute\" : []" +
                                       "} " +
                                       "] }}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"Category\": [{ " +
                                       "\"CategoryId\": \"another-custom-cat\", " +
                                       "\"Id\" : \"anotherXmlId\", " +
                                       "\"Id\" : \"anotherXmlId\", " +
                                       "\"Attribute\" : []" +
                                       "} " +
                                       "] }}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"Category\": [{ " +
                                       "\"CategoryId\": \"another-custom-cat\", " +
                                       "\"Id\" : \"anotherXmlId\", " +
                                       "\"Attribute\" : []" +
                                       "\"Attribute\" : []" +
                                       "} " +
                                       "] }}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }


        // AccessSubject
        try {
            request = JSONRequest.load((String)"{\"Request\" : { \"AccessSubject\":{ }, \"AccessSubject\":{ }}}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // Action
        try {
            request = JSONRequest.load((String)"{\"Request\" : { \"Action\":{ }, \"Action\":{ }}}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // Resource
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"Resource\":{ }, \"Resource\":{ }}}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // Environment
        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"Environment\":{ }, \"Environment\":{ } }}");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        // MultiRequests


        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{ " +
                                       "\"ReferenceId\" : [\"foo1\",\"bar1\"]" +
                                       "}," +
                                       "{" +
                                       "\"ReferenceId\" : [\"foo2\",\"bar2\"]" +
                                       "}]" +
                                       "},"
                                       + "\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{ " +
                                       "\"ReferenceId\" : [\"foo1\",\"bar1\"]" +
                                       "}," +
                                       "{" +
                                       "\"ReferenceId\" : [\"foo2\",\"bar2\"]" +
                                       "}]" +
                                       "}  } }");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{ " +
                                       "\"ReferenceId\" : [\"foo1\",\"bar1\"]" +
                                       "}," +
                                       "{" +
                                       "\"ReferenceId\" : [\"foo2\",\"bar2\"]" +
                                       "}]," +
                                       "\"RequestReference\": [" +
                                       "{ " +
                                       "\"ReferenceId\" : [\"foo1\",\"bar1\"]" +
                                       "}," +
                                       "{" +
                                       "\"ReferenceId\" : [\"foo2\",\"bar2\"]" +
                                       "}]" +
                                       "} } }");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }

        try {
            request = JSONRequest.load((String)"{\"Request\" : {\"MultiRequests\" : {" +
                                       "\"RequestReference\": [" +
                                       "{ " +
                                       "\"ReferenceId\" : [\"foo1\",\"bar1\"]" +
                                       "\"ReferenceId\" : [\"foo1\",\"bar1\"]" +
                                       "}," +
                                       "{" +
                                       "\"ReferenceId\" : [\"foo2\",\"bar2\"]" +
                                       "}]" +
                                       "} } }");
            fail("Unknown element should throw exception");
        } catch (JSONStructureException e) {
            // correct response
        } catch (Exception e) {
            fail ("Failed convert from JSON to object: " + e);
        }
    }


}
