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
 * Test JSON Request convert to object - Category sub-component.  Does not include "Default" Categories (Subject, Action, Resource, Environment).
 * Basic existance/absence of Category is tested in RequestMainTest.
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
public class RequestCategoryTest {
	
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


	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Top-level of Category
	@Test
	public void testCategoryTopLevel() {	
		
		// empty Category
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Missing value
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\" }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\" : }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category without CategoryId
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{}] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category with CategoryId value missing or =""
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\"] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"\" ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// CategoryId wrong type
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : true } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : 123 } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category with Id
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Id\" : \"customId\" } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category},xmlId=customId}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category with Id - wrong type
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Id\" : true } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Id\" : 123 } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// Category without Id	
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\" } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// Category with standard CategoryId
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\" } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category with extra unknown field
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", unknown } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"unknown\" : 123 } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category with multiple sub-Category objects using same CategoryId
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"category1\" }, {\"CategoryId\" : \"category1\" } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=category1}}{super={category=category1}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
	}

	
	
	// Tests related to Attributes
	@Test
	public void testCategoryAttributes() {	
	
		// Category with Attribute but none given ("Attribute" : [] )		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}

		// Category Attribute with empty attribute (missing both AttributeId and Id)
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with AttributeId and no Value
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"AttributeId\" : \"document-id\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute missing AttributeId but with Id		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"AttributeId\" : \"document-id\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute missing AttributeId but with Id
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute including both AttributeId and Id with same value
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"AttributeId\" : \"document-id\", " +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute missing both AttributeId and Id
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute AttributeId not string
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"AttributeId\" : true, " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"AttributeId\" : 123, " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute Id not string
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : true, " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : 123, " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// Category Attribute with DataType
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"integer\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with DataType not string (e.g. "DataType" : 55.5 )
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: true, " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with unknown DataType
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"no such data type\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: 321, " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with multiple value array
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"dayTimeDuration\", " +
	    			"\"Value\"	: [\"P3D\", \"P2DT12H34M\", \"PT15M\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#dayTimeDuration,value={super={durationSign=1years=0months=0days=3hours=0minutes=0seconds=0millis=0},factionalSeconds=0.0}}{dataTypeId=http://www.w3.org/2001/XMLSchema#dayTimeDuration,value={super={durationSign=1years=0months=0days=2hours=12minutes=34seconds=0millis=0},factionalSeconds=0.0}}{dataTypeId=http://www.w3.org/2001/XMLSchema#dayTimeDuration,value={super={durationSign=1years=0months=0days=0hours=0minutes=15seconds=0millis=0},factionalSeconds=0.0}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute multiple value with null in array
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"dayTimeDuration\", " +
	    			"\"Value\"	: [\"P3D\", , \"P15M\"] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with array value with no values ("Attribute": [ {"AttributeId" :"a", Value:[] } ] }  )
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"dayTimeDuration\", " +
	    			"\"Value\"	: [ ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with no DataType and array with no values
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with Issuer
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
					"\"Issuer\" : \"University Press\", " +
	    			"\"DataType\"	: \"integer\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}],issuer=University Press,includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with Issuer not string
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
					"\"Issuer\" : true, " +
	    			"\"DataType\"	: \"integer\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
					"\"Issuer\" : 4.56, " +
	    			"\"DataType\"	: \"integer\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with includeInResult=true
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: 123, " +
	    			"\"IncludeInResult\" : true " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}],includeInResults=true}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with includeInResult = false
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: 123, " +
	    			"\"IncludeInResult\" : false " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with includeInResult not boolean
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: 123, " +
	    			"\"IncludeInResult\" : \"abc\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: 123, " +
	    			"\"IncludeInResult\" : 123.45 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
	}

	
	
	// Tests related to DataTypes within Attributes
	@Test
	public void testCategoryAttributesDataTypesSimple() {	
		
		// Category Attribute using full Identifier for each data type
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: \"abc\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#boolean\", " +
	    			"\"Value\"	: true " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#double\", " +
	    			"\"Value\"	: 123.34 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=123.34}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#time\", " +
	    			"\"Value\"	: \"12:00:00Z\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#time,value=12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#date\", " +
	    			"\"Value\"	: \"2002-10-10\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#date,value=2002-10-10}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dateTime\", " +
	    			"\"Value\"	: \"2002-10-10T12:00:00Z\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#dateTime,value=2002-10-10T12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dayTimeDuration\", " +
	    			"\"Value\"	: \"P23DT7H12M54S\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#dayTimeDuration,value={super={durationSign=1years=0months=0days=23hours=7minutes=12seconds=54millis=0},factionalSeconds=54.0}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#yearMonthDuration\", " +
	    			"\"Value\"	: \"P165Y8M\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#yearMonthDuration,value={super={durationSign=1years=165months=8days=0hours=0minutes=0seconds=0millis=0},monthsDuration=1988}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#anyURI\", " +
	    			"\"Value\"	: \"aValue\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#hexBinary\", " +
	    			"\"Value\"	: \"FA027B7D12CC34DDD20012AEEF\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#hexBinary,value={data=[-6,2,123,125,18,-52,52,-35,-46,0,18,-82,-17]}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#base64Binary\", " +
	    			"\"Value\"	: \"lvbj0iMS4wIj8+PGNhdGFsb2c+PGJvb2sgaWQ9ImJrMTAxIj48YXV0aG9y\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-106,-10,-29,-46,35,18,-29,2,35,-13,-29,-58,54,23,70,22,-58,-10,115,-29,-58,38,-10,-10,-78,6,-106,67,-46,38,38,-77,19,3,18,35,-29,-58,23,87,70,-122,-9]}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name\", " +
	    			"\"Value\"	: \"someone.else@A.COMPANY.com\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name,value={localName=someone.else,domainName=A.COMPANY.com}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:x500Name\", " +
	    			"\"Value\"	: \"cn=Julius Hibbert, o=Medi Corporation, c=US\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:x500Name,value=CN=Julius Hibbert, O=Medi Corporation, C=US}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:ipAddress\", " +
	    			"\"Value\"	: \"121.221.43.58:12345\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:ipAddress,value=121.221.43.58:12345-12345}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:dnsName\", " +
	    			"\"Value\"	: \"aValue\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:dnsName,value={domainName=aValue}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : ["
	    					+ "{ "+
    							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
    							"}," +
    							"{" +
    								"\"Prefix\" : \"md\", " +
    								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
    							"}], "+
    					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression,value={path=md:record/md:patient/md:patientDoB,Namespace={[{md,urn:example:med:schemas:record}{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}]},status=null,xpathExpressionWrapped=null},xpathCategory=urn:oasis:names:tc:xacml:3.0:attribute-category:resource}],includeInResults=false}]}}]}", request.toString());

		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}

		
		// Category Attribute shorthand notation for each data type
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"string\", " +
	    			"\"Value\"	: \"abc\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"boolean\", " +
	    			"\"Value\"	: true " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"integer\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"double\", " +
	    			"\"Value\"	: 123.34 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=123.34}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"time\", " +
	    			"\"Value\"	: \"12:00:00Z\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#time,value=12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"date\", " +
	    			"\"Value\"	: \"2002-10-10\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#date,value=2002-10-10}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"dateTime\", " +
	    			"\"Value\"	: \"2002-10-10T12:00:00Z\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#dateTime,value=2002-10-10T12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"dayTimeDuration\", " +
	    			"\"Value\"	: \"P23DT7H12M54S\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#dayTimeDuration,value={super={durationSign=1years=0months=0days=23hours=7minutes=12seconds=54millis=0},factionalSeconds=54.0}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"yearMonthDuration\", " +
	    			"\"Value\"	: \"P165Y8M\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#yearMonthDuration,value={super={durationSign=1years=165months=8days=0hours=0minutes=0seconds=0millis=0},monthsDuration=1988}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"anyURI\", " +
	    			"\"Value\"	: \"aValue\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"hexBinary\", " +
	    			"\"Value\"	: \"FA027B7D12CC34DDD20012AEEF\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#hexBinary,value={data=[-6,2,123,125,18,-52,52,-35,-46,0,18,-82,-17]}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"base64Binary\", " +
	    			"\"Value\"	: \"lvbj0iMS4wIj8+PGNhdGFsb2c+PGJvb2sgaWQ9ImJrMTAxIj48YXV0aG9y\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-106,-10,-29,-46,35,18,-29,2,35,-13,-29,-58,54,23,70,22,-58,-10,115,-29,-58,38,-10,-10,-78,6,-106,67,-46,38,38,-77,19,3,18,35,-29,-58,23,87,70,-122,-9]}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"rfc822Name\", " +
	    			"\"Value\"	: \"someone.else@A.COMPANY.com\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name,value={localName=someone.else,domainName=A.COMPANY.com}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"x500Name\", " +
	    			"\"Value\"	: \"cn=Julius Hibbert, o=Medi Corporation, c=US\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:x500Name,value=CN=Julius Hibbert, O=Medi Corporation, C=US}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"ipAddress\", " +
	    			"\"Value\"	: \"121.221.43.58:12345\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:ipAddress,value=121.221.43.58:12345-12345}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"dnsName\", " +
	    			"\"Value\"	: \"aValue\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:dnsName,value={domainName=aValue}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : ["
	    					+ "{ "+
    							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
    							"}," +
    							"{" +
    								"\"Prefix\" : \"md\", " +
    								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
    							"}], "+
    					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression,value={path=md:record/md:patient/md:patientDoB,Namespace={[{md,urn:example:med:schemas:record}{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}]},status=null,xpathExpressionWrapped=null},xpathCategory=urn:oasis:names:tc:xacml:3.0:attribute-category:resource}],includeInResults=false}]}}]}", request.toString());

		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}

		
		
		// infer data type - only integer, boolean and double are distinguishable from strings; everything else is treated as a string
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"abc\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: true " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: 123.34 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=123.34}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"12:00:00Z\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"2002-10-10\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"2002-10-10T12:00:00Z\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10T12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"P23DT7H12M54S\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P23DT7H12M54S}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"P165Y8M\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P165Y8M}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"aValue\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"FA027B7D12CC34DDD20012AEEF\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=FA027B7D12CC34DDD20012AEEF}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"lvbj0iMS4wIj8+PGNhdGFsb2c+PGJvb2sgaWQ9ImJrMTAxIj48YXV0aG9y\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lvbj0iMS4wIj8+PGNhdGFsb2c+PGJvb2sgaWQ9ImJrMTAxIj48YXV0aG9y}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"someone.else@A.COMPANY.com\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=someone.else@A.COMPANY.com}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:x500Name\", " +
	    			"\"Value\"	: \"cn=Julius Hibbert, o=Medi Corporation, c=US\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:x500Name,value=CN=Julius Hibbert, O=Medi Corporation, C=US}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"121.221.43.58:12345\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=121.221.43.58:12345}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"aValue\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"aValue\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : ["
	    					+ "{ "+
    							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
    							"}," +
    							"{" +
    								"\"Prefix\" : \"md\", " +
    								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
    							"}], "+
    					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			// gets inferred to a String containing the whole structure under Value as a String
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value={XPathCategory=urn:oasis:names:tc:xacml:3.0:attribute-category:resource, Namespaces=[{Namespace=urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}, {Prefix=md, Namespace=urn:example:med:schemas:record}], XPath=md:record/md:patient/md:patientDoB}}],includeInResults=false}]}}]}", request.toString());

		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
	}
	
	
	
	@Test
	public void testCategoryAttributesDataTypesNotMatchValue() {	

		// Category Attribute with DataType not matching value type (JSON type derived from syntax)
		// AUTO-CONVERSION from Boolean to String!
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: true " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: 123.34 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123.34}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		

		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#boolean\", " +
	    			"\"Value\"	: \"abc\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#boolean\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#boolean\", " +
	    			"\"Value\"	: 123.45 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: \"123\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: true " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: 123.45 " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		

		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#double\", " +
	    			"\"Value\"	: \"123.34\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=123.34}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#double\", " +
	    			"\"Value\"	: true " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// allow integer to auto-convert to double when DataType is given
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#double\", " +
	    			"\"Value\"	: 123 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=123.0}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// special JavaScript values not allowed except for -0 (inappropriate requirement in spec - check it anyway)
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: \"NaN\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: \"INF\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: \"-INF\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// JavaScript 0 and -0 are ok
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: 0 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=0}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: -0 " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=0}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// All other data types are checked when we convert internally, so value must be syntactically correct
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#time\", " +
	    			"\"Value\"	: \"syntactically incorrect value\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#date\", " +
	    			"\"Value\"	: \"syntactically incorrect value\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dateTime\", " +
	    			"\"Value\"	: \"syntactically incorrect value\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dayTimeDuration\", " +
	    			"\"Value\"	: \"syntactically incorrect value\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#yearMonthDuration\", " +
	    			"\"Value\"	: \"syntactically incorrect value\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#anyURI\", " +
	    			"\"Value\"	: \"syntactically incorrect value\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#hexBinary\", " +
	    			"\"Value\"	: \"syntactically incorrect value\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Base64 convert does not throw an exception if the contents are not Base64, so cannot test for this.
		// Any problem with the data will have to be discovered later when the data is used.
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name\", " +
	    			"\"Value\"	: \"syntactically incorrect value\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:x500Name\", " +
	    			"\"Value\"	: \"syntactically incorrect value\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:ipAddress\", " +
	    			"\"Value\"	: \"syntactically incorrect value\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:dnsName\", " +
	    			"\"Value\"	: \"syntactically incorrect value\" " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		

		// Cannot test XPathExpressions here.  The XPathExpression gets converted into a simple String value within the XPathExpression object,
		// but it is not evaluated or compiled at that time.  Therefore we do not know whether or not the value is valid until it is used in a computation.

	}
	
	
	@Test
	public void testArrayDataTypes() {
		
		// array of size 0
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: [] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// Category Attribute value array DataType given (repeat for all data types)
		// Category Attribute using full Identifier for each data type
		// Category Attribute shorthand notation for each data type
		// Also tests for mixes of different JSON types (trying incorrect strings for XACML data types whenever possible)
		// string
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: [\"abc\", \"def\", \"hig\", \"lmn\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=def}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=hig}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lmn}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"string\", " +
	    			"\"Value\"	: [\"abc\", \"def\", \"hig\", \"lmn\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=def}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=hig}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lmn}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT to DataType
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: [\"abc\", true, \"hig\", \"lmn\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=hig}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lmn}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: [\"abc\",123, \"hig\", \"lmn\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=hig}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lmn}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: [\"abc\", 34.34, \"hig\", \"lmn\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=34.34}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=hig}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lmn}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// boolean
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#boolean\", " +
	    			"\"Value\"	: [true, true, false, true, false ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=false}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=false}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"boolean\", " +
	    			"\"Value\"	: [true, true, false, true, false ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=false}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=false}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#boolean\", " +
	    			"\"Value\"	: [true, \"abc\", false, true, false ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#boolean\", " +
	    			"\"Value\"	: [true, 123, false, true, false ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#boolean\", " +
	    			"\"Value\"	: [true, 12.34, false, true, false ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		
		// integer
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: [123, 456, 765, 234] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=456}{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=765}{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=234}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"integer\", " +
	    			"\"Value\"	: [123, 456, 765, 234] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=456}{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=765}{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=234}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: [123, \"abc\", 765, 234] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: [123, true, 765, 234] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#integer\", " +
	    			"\"Value\"	: [123, 34.56, 765, 234] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// double
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#double\", " +
	    			"\"Value\"	: [ 123.34, 543.54, 3445.455, 4543,543 ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=123.34}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=543.54}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=3445.455}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=4543.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=543.0}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"double\", " +
	    			"\"Value\"	: [ 123.34, 543.54, 3445.455, 4543,543 ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=123.34}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=543.54}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=3445.455}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=4543.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=543.0}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// special case - auto-convert integer to boolean
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#double\", " +
	    			"\"Value\"	: [ 123.34, 111122, 3445.455, 4543,543 ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=123.34}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=111122.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=3445.455}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=4543.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=543.0}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#double\", " +
	    			"\"Value\"	: [ 123.34, true, 3445.455, 4543,543 ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#double\", " +
	    			"\"Value\"	: [ 123.34, \"abb\", 3445.455, 4543,543 ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// time
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#time\", " +
	    			"\"Value\"	: [ \"12:00:00Z\", \"12:00:00Z\", \"12:00:00Z\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#time,value=12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#time,value=12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#time,value=12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"time\", " +
	    			"\"Value\"	: [ \"12:00:00Z\", \"12:00:00Z\", \"12:00:00Z\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#time,value=12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#time,value=12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#time,value=12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#time\", " +
	    			"\"Value\"	: [ \"12:00:00Z\", \"not a time\", \"12:00:00Z\"] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#time\", " +
	    			"\"Value\"	: [ \"12:00:00Z\", true, \"12:00:00Z\"] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#time\", " +
	    			"\"Value\"	: [ \"12:00:00Z\", 123, \"12:00:00Z\"] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#time\", " +
	    			"\"Value\"	: [ \"12:00:00Z\", 12.34, \"12:00:00Z\"] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// date
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#date\", " +
	    			"\"Value\"	: [\"2002-10-10\",\"2002-10-10\",\"2002-10-10\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#date,value=2002-10-10}{dataTypeId=http://www.w3.org/2001/XMLSchema#date,value=2002-10-10}{dataTypeId=http://www.w3.org/2001/XMLSchema#date,value=2002-10-10}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"date\", " +
	    			"\"Value\"	: [\"2002-10-10\",\"2002-10-10\",\"2002-10-10\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#date,value=2002-10-10}{dataTypeId=http://www.w3.org/2001/XMLSchema#date,value=2002-10-10}{dataTypeId=http://www.w3.org/2001/XMLSchema#date,value=2002-10-10}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#date\", " +
	    			"\"Value\"	: [\"2002-10-10\",\"not a date\",\"2002-10-10\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#date\", " +
	    			"\"Value\"	: [\"2002-10-10\",true,\"2002-10-10\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#date\", " +
	    			"\"Value\"	: [\"2002-10-10\",123,\"2002-10-10\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#date\", " +
	    			"\"Value\"	: [\"2002-10-10\",123.45,\"2002-10-10\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// dateTime
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dateTime\", " +
	    			"\"Value\"	: [ \"2002-10-10T12:00:00Z\",\"2002-10-10T12:00:00Z\",\"2002-10-10T12:00:00Z\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#dateTime,value=2002-10-10T12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#dateTime,value=2002-10-10T12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#dateTime,value=2002-10-10T12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"dateTime\", " +
	    			"\"Value\"	: [ \"2002-10-10T12:00:00Z\",\"2002-10-10T12:00:00Z\",\"2002-10-10T12:00:00Z\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#dateTime,value=2002-10-10T12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#dateTime,value=2002-10-10T12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#dateTime,value=2002-10-10T12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dateTime\", " +
	    			"\"Value\"	: [ \"2002-10-10T12:00:00Z\",\"not a dateTime\",\"2002-10-10T12:00:00Z\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dateTime\", " +
	    			"\"Value\"	: [ \"2002-10-10T12:00:00Z\",true,\"2002-10-10T12:00:00Z\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dateTime\", " +
	    			"\"Value\"	: [ \"2002-10-10T12:00:00Z\",123,\"2002-10-10T12:00:00Z\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dateTime\", " +
	    			"\"Value\"	: [ \"2002-10-10T12:00:00Z\",12.34,\"2002-10-10T12:00:00Z\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// dayTimeDuration
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dayTimeDuration\", " +
	    			"\"Value\"	: [ \"P23DT7H12M54S\",\"P23DT7H12M54S\",\"P23DT7H12M54S\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#dayTimeDuration,value={super={durationSign=1years=0months=0days=23hours=7minutes=12seconds=54millis=0},factionalSeconds=54.0}}{dataTypeId=http://www.w3.org/2001/XMLSchema#dayTimeDuration,value={super={durationSign=1years=0months=0days=23hours=7minutes=12seconds=54millis=0},factionalSeconds=54.0}}{dataTypeId=http://www.w3.org/2001/XMLSchema#dayTimeDuration,value={super={durationSign=1years=0months=0days=23hours=7minutes=12seconds=54millis=0},factionalSeconds=54.0}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"dayTimeDuration\", " +
	    			"\"Value\"	: [ \"P23DT7H12M54S\",\"P23DT7H12M54S\",\"P23DT7H12M54S\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#dayTimeDuration,value={super={durationSign=1years=0months=0days=23hours=7minutes=12seconds=54millis=0},factionalSeconds=54.0}}{dataTypeId=http://www.w3.org/2001/XMLSchema#dayTimeDuration,value={super={durationSign=1years=0months=0days=23hours=7minutes=12seconds=54millis=0},factionalSeconds=54.0}}{dataTypeId=http://www.w3.org/2001/XMLSchema#dayTimeDuration,value={super={durationSign=1years=0months=0days=23hours=7minutes=12seconds=54millis=0},factionalSeconds=54.0}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dayTimeDuration\", " +
	    			"\"Value\"	: [ \"P23DT7H12M54S\",\"not a duration\",\"P23DT7H12M54S\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dayTimeDuration\", " +
	    			"\"Value\"	: [ \"P23DT7H12M54S\",true,\"P23DT7H12M54S\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dayTimeDuration\", " +
	    			"\"Value\"	: [ \"P23DT7H12M54S\",123,\"P23DT7H12M54S\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#dayTimeDuration\", " +
	    			"\"Value\"	: [ \"P23DT7H12M54S\",11.22,\"P23DT7H12M54S\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// yearMonth duration
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#yearMonthDuration\", " +
	    			"\"Value\"	: [ \"P165Y8M\",\"P165Y8M\",\"P165Y8M\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#yearMonthDuration,value={super={durationSign=1years=165months=8days=0hours=0minutes=0seconds=0millis=0},monthsDuration=1988}}{dataTypeId=http://www.w3.org/2001/XMLSchema#yearMonthDuration,value={super={durationSign=1years=165months=8days=0hours=0minutes=0seconds=0millis=0},monthsDuration=1988}}{dataTypeId=http://www.w3.org/2001/XMLSchema#yearMonthDuration,value={super={durationSign=1years=165months=8days=0hours=0minutes=0seconds=0millis=0},monthsDuration=1988}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"yearMonthDuration\", " +
	    			"\"Value\"	: [ \"P165Y8M\",\"P165Y8M\",\"P165Y8M\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#yearMonthDuration,value={super={durationSign=1years=165months=8days=0hours=0minutes=0seconds=0millis=0},monthsDuration=1988}}{dataTypeId=http://www.w3.org/2001/XMLSchema#yearMonthDuration,value={super={durationSign=1years=165months=8days=0hours=0minutes=0seconds=0millis=0},monthsDuration=1988}}{dataTypeId=http://www.w3.org/2001/XMLSchema#yearMonthDuration,value={super={durationSign=1years=165months=8days=0hours=0minutes=0seconds=0millis=0},monthsDuration=1988}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#yearMonthDuration\", " +
	    			"\"Value\"	: [ \"P165Y8M\",\"not a duration\",\"P165Y8M\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#yearMonthDuration\", " +
	    			"\"Value\"	: [ \"P165Y8M\",true,\"P165Y8M\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#yearMonthDuration\", " +
	    			"\"Value\"	: [ \"P165Y8M\",123,\"P165Y8M\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#yearMonthDuration\", " +
	    			"\"Value\"	: [ \"P165Y8M\",11.22,\"P165Y8M\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// anyURI
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#anyURI\", " +
	    			"\"Value\"	: [ \"aValue\",\"aValue\",\"aValue\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"anyURI\", " +
	    			"\"Value\"	: [ \"aValue\",\"aValue\",\"aValue\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#anyURI\", " +
	    			"\"Value\"	: [ \"aValue\",true,\"aValue\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#anyURI\", " +
	    			"\"Value\"	: [ \"aValue\",123,\"aValue\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#anyURI\", " +
	    			"\"Value\"	: [ \"aValue\",11.111,\"aValue\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=11.111}{dataTypeId=http://www.w3.org/2001/XMLSchema#anyURI,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// hexBinary
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#hexBinary\", " +
	    			"\"Value\"	: [ \"FA027B7D12CC34DDD20012AEEF\",\"FA027B7D12CC34DDD20012AEEF\",\"FA027B7D12CC34DDD20012AEEF\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#hexBinary,value={data=[-6,2,123,125,18,-52,52,-35,-46,0,18,-82,-17]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#hexBinary,value={data=[-6,2,123,125,18,-52,52,-35,-46,0,18,-82,-17]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#hexBinary,value={data=[-6,2,123,125,18,-52,52,-35,-46,0,18,-82,-17]}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"hexBinary\", " +
	    			"\"Value\"	: [ \"FA027B7D12CC34DDD20012AEEF\",\"FA027B7D12CC34DDD20012AEEF\",\"FA027B7D12CC34DDD20012AEEF\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#hexBinary,value={data=[-6,2,123,125,18,-52,52,-35,-46,0,18,-82,-17]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#hexBinary,value={data=[-6,2,123,125,18,-52,52,-35,-46,0,18,-82,-17]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#hexBinary,value={data=[-6,2,123,125,18,-52,52,-35,-46,0,18,-82,-17]}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#hexBinary\", " +
	    			"\"Value\"	: [ \"FA027B7D12CC34DDD20012AEEF\",true,\"012AEEF\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#hexBinary\", " +
	    			"\"Value\"	: [ \"FA027B7D12CC34DDD20012AEEF\",123,\"012AEEF\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#hexBinary\", " +
	    			"\"Value\"	: [ \"FA027B7D12CC34DDD20012AEEF\",11.44,\"012AEEF\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// base64Binary
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#base64Binary\", " +
	    			"\"Value\"	: [ \"aG9y\",\"lvbj0iMS4xIj48YXV0aG9y\",\"lvbjIj48YXV0aG9y\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[104,111,114]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-106,-10,-29,-46,35,18,-29,18,35,-29,-58,23,87,70,-122,-9]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-106,-10,-29,34,62,60,97,117,116,104,111,114]}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"base64Binary\", " +
	    			"\"Value\"	: [ \"aG9y\",\"lvbj0iMS4xIj48YXV0aG9y\",\"lvbjIj48YXV0aG9y\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[104,111,114]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-106,-10,-29,-46,35,18,-29,18,35,-29,-58,23,87,70,-122,-9]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-106,-10,-29,34,62,60,97,117,116,104,111,114]}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#base64Binary\", " +
	    			"\"Value\"	: [ \"aG9y\",true,\"lvbjIj48YXV0aG9y\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[104,111,114]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-74,-69,-98]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-106,-10,-29,34,62,60,97,117,116,104,111,114]}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#base64Binary\", " +
	    			"\"Value\"	: [ \"aG9y\",1123,\"lvbjIj48YXV0aG9y\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[104,111,114]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-41,93,-73]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-106,-10,-29,34,62,60,97,117,116,104,111,114]}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#base64Binary\", " +
	    			"\"Value\"	: [ \"aG9y\",11.22,\"lvbjIj48YXV0aG9y\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[104,111,114]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-41,93,-74]}}{dataTypeId=http://www.w3.org/2001/XMLSchema#base64Binary,value={data=[-106,-10,-29,34,62,60,97,117,116,104,111,114]}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// RFC822 name
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name\", " +
	    			"\"Value\"	: [ \"sne.else@A.COMPANY.com\",\"one.else@A.COMPANY.com\",\"someone.else@A.CONY.com\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name,value={localName=sne.else,domainName=A.COMPANY.com}}{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name,value={localName=one.else,domainName=A.COMPANY.com}}{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name,value={localName=someone.else,domainName=A.CONY.com}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"rfc822Name\", " +
	    			"\"Value\"	: [ \"sne.else@A.COMPANY.com\",\"one.else@A.COMPANY.com\",\"someone.else@A.CONY.com\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name,value={localName=sne.else,domainName=A.COMPANY.com}}{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name,value={localName=one.else,domainName=A.COMPANY.com}}{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name,value={localName=someone.else,domainName=A.CONY.com}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name\", " +
	    			"\"Value\"	: [ \"sne.else@A.COMPANY.com\",\"not a dns\",\"someone.else@A.CONY.com\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name\", " +
	    			"\"Value\"	: [ \"sne.else@A.COMPANY.com\",true,\"someone.else@A.CONY.com\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name\", " +
	    			"\"Value\"	: [ \"sne.else@A.COMPANY.com\",111,\"someone.else@A.CONY.com\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name\", " +
	    			"\"Value\"	: [ \"sne.else@A.COMPANY.com\",11.22,\"someone.else@A.CONY.com\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// x500
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:x500Name\", " +
	    			"\"Value\"	: [ \"cn=Julius Hibbert, o=Medi Corporation, c=US\", \"cn=Julius Hibbert, o=Medi Corporation, c=US\", \"cn=Julius Hibbert, o=Medi Corporation, c=US\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:x500Name,value=CN=Julius Hibbert, O=Medi Corporation, C=US}{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:x500Name,value=CN=Julius Hibbert, O=Medi Corporation, C=US}{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:x500Name,value=CN=Julius Hibbert, O=Medi Corporation, C=US}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"x500Name\", " +
	    			"\"Value\"	: [ \"cn=Julius Hibbert, o=Medi Corporation, c=US\", \"cn=Julius Hibbert, o=Medi Corporation, c=US\", \"cn=Julius Hibbert, o=Medi Corporation, c=US\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:x500Name,value=CN=Julius Hibbert, O=Medi Corporation, C=US}{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:x500Name,value=CN=Julius Hibbert, O=Medi Corporation, C=US}{dataTypeId=urn:oasis:names:tc:xacml:1.0:data-type:x500Name,value=CN=Julius Hibbert, O=Medi Corporation, C=US}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:x500Name\", " +
	    			"\"Value\"	: [ \"cn=Julius Hibbert, o=Medi Corporation, c=US\", \"non-x500 string\", \"cn=Julius Hibbert, o=Medi Corporation, c=US\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:x500Name\", " +
	    			"\"Value\"	: [ \"cn=Julius Hibbert, o=Medi Corporation, c=US\", true, \"cn=Julius Hibbert, o=Medi Corporation, c=US\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:x500Name\", " +
	    			"\"Value\"	: [ \"cn=Julius Hibbert, o=Medi Corporation, c=US\", 1111, \"cn=Julius Hibbert, o=Medi Corporation, c=US\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:1.0:data-type:x500Name\", " +
	    			"\"Value\"	: [ \"cn=Julius Hibbert, o=Medi Corporation, c=US\", 11.22, \"cn=Julius Hibbert, o=Medi Corporation, c=US\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// ipAddress
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:ipAddress\", " +
	    			"\"Value\"	: [ \"121.221.43.58:12345\",\"121.221.43.58:12345\",\"121.221.43.58:12345\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:ipAddress,value=121.221.43.58:12345-12345}{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:ipAddress,value=121.221.43.58:12345-12345}{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:ipAddress,value=121.221.43.58:12345-12345}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"ipAddress\", " +
	    			"\"Value\"	: [ \"121.221.43.58:12345\",\"121.221.43.58:12345\",\"121.221.43.58:12345\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:ipAddress,value=121.221.43.58:12345-12345}{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:ipAddress,value=121.221.43.58:12345-12345}{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:ipAddress,value=121.221.43.58:12345-12345}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:ipAddress\", " +
	    			"\"Value\"	: [ \"121.221.43.58:12345\",\"not an ip address\",\"121.221.43.58:12345\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:ipAddress\", " +
	    			"\"Value\"	: [ \"121.221.43.58:12345\",true,\"121.221.43.58:12345\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:ipAddress\", " +
	    			"\"Value\"	: [ \"121.221.43.58:12345\",1111,\"121.221.43.58:12345\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:ipAddress\", " +
	    			"\"Value\"	: [ \"121.221.43.58:12345\",11.22,\"121.221.43.58:12345\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// dnsName
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:dnsName\", " +
	    			"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:dnsName,value={domainName=aValue}}{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:dnsName,value={domainName=aValue}}{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:dnsName,value={domainName=aValue}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"dnsName\", " +
	    			"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:dnsName,value={domainName=aValue}}{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:dnsName,value={domainName=aValue}}{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:dnsName,value={domainName=aValue}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:dnsName\", " +
	    			"\"Value\"	: [ \"aValue\", true, \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:dnsName,value={domainName=aValue}}{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:dnsName,value={domainName=true}}{dataTypeId=urn:oasis:names:tc:xacml:2.0:data-type:dnsName,value={domainName=aValue}}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:dnsName\", " +
	    			"\"Value\"	: [ \"aValue\", 1111, \"aValue\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:2.0:data-type:dnsName\", " +
	    			"\"Value\"	: [ \"aValue\", 11.22, \"aValue\" ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// xPathExpression
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\", " +
	    			"\"Value\"	: [ "
	    			+ "{" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : ["
	    					+ "{ "+
    							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
    							"}," +
    							"{" +
    								"\"Prefix\" : \"md\", " +
    								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
    							"}], "+
    					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}," +
            		"{" +
    	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
    	    				"\"Namespaces\" : ["
    	    					+ "{ "+
        							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
        							"}," +
        							"{" +
        								"\"Prefix\" : \"md\", " +
        								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
        							"}], "+
        					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
                		"}," +
                		"{" +
        	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
        	    				"\"Namespaces\" : ["
        	    					+ "{ "+
            							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
            							"}," +
            							"{" +
            								"\"Prefix\" : \"md\", " +
            								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
            							"}], "+
            					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
                    		"}"
	    			+ "] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression,value={path=md:record/md:patient/md:patientDoB,Namespace={[{md,urn:example:med:schemas:record}{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}]},status=null,xpathExpressionWrapped=null},xpathCategory=urn:oasis:names:tc:xacml:3.0:attribute-category:resource}{dataTypeId=urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression,value={path=md:record/md:patient/md:patientDoB,Namespace={[{md,urn:example:med:schemas:record}{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}]},status=null,xpathExpressionWrapped=null},xpathCategory=urn:oasis:names:tc:xacml:3.0:attribute-category:resource}{dataTypeId=urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression,value={path=md:record/md:patient/md:patientDoB,Namespace={[{md,urn:example:med:schemas:record}{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}]},status=null,xpathExpressionWrapped=null},xpathCategory=urn:oasis:names:tc:xacml:3.0:attribute-category:resource}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}

		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\", " +
	    			"\"Value\"	: [ "
	    			+ "{" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : ["
	    					+ "{ "+
    							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
    							"}," +
    							"{" +
    								"\"Prefix\" : \"md\", " +
    								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
    							"}], "+
    					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}," +
            		"\"simpleString\"," +
                		"{" +
        	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
        	    				"\"Namespaces\" : ["
        	    					+ "{ "+
            							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
            							"}," +
            							"{" +
            								"\"Prefix\" : \"md\", " +
            								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
            							"}], "+
            					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
                    		"}"
	    			+ "] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\", " +
	    			"\"Value\"	: [ "
	    			+ "{" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : ["
	    					+ "{ "+
    							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
    							"}," +
    							"{" +
    								"\"Prefix\" : \"md\", " +
    								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
    							"}], "+
    					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}," +
            		"true," +
                		"{" +
        	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
        	    				"\"Namespaces\" : ["
        	    					+ "{ "+
            							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
            							"}," +
            							"{" +
            								"\"Prefix\" : \"md\", " +
            								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
            							"}], "+
            					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
                    		"}"
	    			+ "] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\", " +
	    			"\"Value\"	: [ "
	    			+ "{" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : ["
	    					+ "{ "+
    							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
    							"}," +
    							"{" +
    								"\"Prefix\" : \"md\", " +
    								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
    							"}], "+
    					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}," +
            		"123," +
                		"{" +
        	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
        	    				"\"Namespaces\" : ["
        	    					+ "{ "+
            							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
            							"}," +
            							"{" +
            								"\"Prefix\" : \"md\", " +
            								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
            							"}], "+
            					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
                    		"}"
	    			+ "] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\", " +
	    			"\"Value\"	: [ "
	    			+ "{" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : ["
	    					+ "{ "+
    							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
    							"}," +
    							"{" +
    								"\"Prefix\" : \"md\", " +
    								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
    							"}], "+
    					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}," +
            		"12.34," +
                		"{" +
        	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
        	    				"\"Namespaces\" : ["
        	    					+ "{ "+
            							"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
            							"}," +
            							"{" +
            								"\"Prefix\" : \"md\", " +
            								"\"Namespace\" : \"urn:example:med:schemas:record\" " +
            							"}], "+
            					"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
                    		"}"
	    			+ "] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		

	}
	

	
	
	
	
	
	@Test
	public void testArrayNoDataTypes() {
		
		// array of size 0
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// Category Attribute value array DataType Not given (repeat for all data types)
		// Also tests for mixes of different JSON types (trying incorrect strings for XACML data types whenever possible)
		// string
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [\"abc\", \"def\", \"hig\", \"lmn\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=def}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=hig}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lmn}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT to DataType
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [\"abc\", true, \"hig\", \"lmn\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=hig}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lmn}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [\"abc\",123, \"hig\", \"lmn\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=hig}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lmn}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [\"abc\", 34.34, \"hig\", \"lmn\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=34.34}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=hig}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lmn}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// boolean
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [true, true, false, true, false ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=false}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#boolean,value=false}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [true, \"abc\", false, true, false ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [true, 123, false, true, false ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [true, 12.34, false, true, false ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		
		// integer
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [123, 456, 765, 234] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=456}{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=765}{dataTypeId=http://www.w3.org/2001/XMLSchema#integer,value=234}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [123, \"abc\", 765, 234] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [123, true, 765, 234] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}

		
		
		// double
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ 123.34, 543.54, 3445.455, 4543,543 ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=123.34}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=543.54}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=3445.455}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=4543.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=543.0}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// special case - auto-convert integer to boolean
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ 123.34, 111122, 3445.455, 4543,543 ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=123.34}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=111122.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=3445.455}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=4543.0}{dataTypeId=http://www.w3.org/2001/XMLSchema#double,value=543.0}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ 123.34, true, 3445.455, 4543,543 ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ 123.34, \"abb\", 3445.455, 4543,543 ] " +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// time - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"12:00:00Z\", \"12:00:00Z\", \"12:00:00Z\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"12:00:00Z\", true, \"12:00:00Z\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// SUCCESSFUL AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"12:00:00Z\", 123, \"12:00:00Z\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"12:00:00Z\", 12.34, \"12:00:00Z\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12.34}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// date - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [\"2002-10-10\",\"2002-10-10\",\"2002-10-10\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [\"2002-10-10\",true,\"2002-10-10\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [\"2002-10-10\",123,\"2002-10-10\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [\"2002-10-10\",123.45,\"2002-10-10\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123.45}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// dateTime - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"2002-10-10T12:00:00Z\",\"2002-10-10T12:00:00Z\",\"2002-10-10T12:00:00Z\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10T12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10T12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10T12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"2002-10-10T12:00:00Z\",true,\"2002-10-10T12:00:00Z\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10T12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10T12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"2002-10-10T12:00:00Z\",123,\"2002-10-10T12:00:00Z\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10T12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10T12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"2002-10-10T12:00:00Z\",12.34,\"2002-10-10T12:00:00Z\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10T12:00:00Z}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=12.34}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=2002-10-10T12:00:00Z}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// dayTimeDuration - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"P23DT7H12M54S\",\"P23DT7H12M54S\",\"P23DT7H12M54S\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P23DT7H12M54S}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P23DT7H12M54S}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P23DT7H12M54S}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		//AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"P23DT7H12M54S\",true,\"P23DT7H12M54S\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P23DT7H12M54S}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P23DT7H12M54S}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"P23DT7H12M54S\",123,\"P23DT7H12M54S\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P23DT7H12M54S}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P23DT7H12M54S}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"P23DT7H12M54S\",11.22,\"P23DT7H12M54S\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P23DT7H12M54S}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=11.22}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P23DT7H12M54S}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// yearMonth duration - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"P165Y8M\",\"P165Y8M\",\"P165Y8M\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P165Y8M}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P165Y8M}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P165Y8M}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"P165Y8M\",true,\"P165Y8M\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P165Y8M}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P165Y8M}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"P165Y8M\",123,\"P165Y8M\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P165Y8M}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P165Y8M}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"P165Y8M\",11.22,\"P165Y8M\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P165Y8M}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=11.22}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=P165Y8M}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// anyURI - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\",\"aValue\",\"aValue\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\",true,\"aValue\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\",123,\"aValue\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\",11.111,\"aValue\"] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=11.111}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// hexBinary - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"FA027B7D12CC34DDD20012AEEF\",\"FA027B7D12CC34DDD20012AEEF\",\"FA027B7D12CC34DDD20012AEEF\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=FA027B7D12CC34DDD20012AEEF}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=FA027B7D12CC34DDD20012AEEF}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=FA027B7D12CC34DDD20012AEEF}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"FA027B7D12CC34DDD20012AEEF\",true,\"012AEEF\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=FA027B7D12CC34DDD20012AEEF}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=012AEEF}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"FA027B7D12CC34DDD20012AEEF\",123,\"012AEEF\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=FA027B7D12CC34DDD20012AEEF}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=123}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=012AEEF}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"FA027B7D12CC34DDD20012AEEF\",11.44,\"012AEEF\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=FA027B7D12CC34DDD20012AEEF}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=11.44}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=012AEEF}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// base64Binary - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aG9y\",\"lvbj0iMS4xIj48YXV0aG9y\",\"lvbjIj48YXV0aG9y\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aG9y}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lvbj0iMS4xIj48YXV0aG9y}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lvbjIj48YXV0aG9y}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aG9y\",true,\"lvbjIj48YXV0aG9y\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aG9y}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lvbjIj48YXV0aG9y}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aG9y\",1123,\"lvbjIj48YXV0aG9y\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aG9y}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=1123}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lvbjIj48YXV0aG9y}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aG9y\",11.22,\"lvbjIj48YXV0aG9y\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aG9y}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=11.22}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=lvbjIj48YXV0aG9y}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// RFC822 name - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"sne.else@A.COMPANY.com\",\"one.else@A.COMPANY.com\",\"someone.else@A.CONY.com\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=sne.else@A.COMPANY.com}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=one.else@A.COMPANY.com}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=someone.else@A.CONY.com}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"sne.else@A.COMPANY.com\",true,\"someone.else@A.CONY.com\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=sne.else@A.COMPANY.com}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=someone.else@A.CONY.com}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"sne.else@A.COMPANY.com\",111,\"someone.else@A.CONY.com\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=sne.else@A.COMPANY.com}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=111}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=someone.else@A.CONY.com}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"sne.else@A.COMPANY.com\",11.22,\"someone.else@A.CONY.com\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=sne.else@A.COMPANY.com}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=11.22}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=someone.else@A.CONY.com}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// x500 - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"cn=Julius Hibbert, o=Medi Corporation, c=US\", \"cn=Julius Hibbert, o=Medi Corporation, c=US\", \"cn=Julius Hibbert, o=Medi Corporation, c=US\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=cn=Julius Hibbert, o=Medi Corporation, c=US}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=cn=Julius Hibbert, o=Medi Corporation, c=US}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=cn=Julius Hibbert, o=Medi Corporation, c=US}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"cn=Julius Hibbert, o=Medi Corporation, c=US\", true, \"cn=Julius Hibbert, o=Medi Corporation, c=US\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=cn=Julius Hibbert, o=Medi Corporation, c=US}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=cn=Julius Hibbert, o=Medi Corporation, c=US}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"cn=Julius Hibbert, o=Medi Corporation, c=US\", 1111, \"cn=Julius Hibbert, o=Medi Corporation, c=US\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=cn=Julius Hibbert, o=Medi Corporation, c=US}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=1111}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=cn=Julius Hibbert, o=Medi Corporation, c=US}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"cn=Julius Hibbert, o=Medi Corporation, c=US\", 11.22, \"cn=Julius Hibbert, o=Medi Corporation, c=US\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=cn=Julius Hibbert, o=Medi Corporation, c=US}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=11.22}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=cn=Julius Hibbert, o=Medi Corporation, c=US}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// ipAddress - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"121.221.43.58:12345\",\"121.221.43.58:12345\",\"121.221.43.58:12345\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=121.221.43.58:12345}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=121.221.43.58:12345}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=121.221.43.58:12345}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"121.221.43.58:12345\",true,\"121.221.43.58:12345\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=121.221.43.58:12345}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=121.221.43.58:12345}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"121.221.43.58:12345\",1111,\"121.221.43.58:12345\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=121.221.43.58:12345}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=1111}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=121.221.43.58:12345}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"121.221.43.58:12345\",11.22,\"121.221.43.58:12345\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=121.221.43.58:12345}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=11.22}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=121.221.43.58:12345}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// dnsName - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", true, \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", 1111, \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=1111}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", 11.22, \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=11.22}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// xPathExpression - defaults to String
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", true, \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=true}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", 1111, \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=1111}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		// AUTO-CONVERT
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", 11.22, \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=11.22}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
	}
	
	

	
	@Test
	public void testXPathExpression() {
		// Category Attribute with XPathExpression including XPathCategory and XPath
		// Category Attribute with XPathExpression with Namespaces with/without Prefix
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : ["
	    				  + "{ "+
	    						"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
	    					"}," +
	    					"{ "+
    							"\"Prefix\" : \"lab\", " +
    							"\"Namespace\" : \"http://somewhere/uri.html\" " +
    						"}," +
	    					"{" +
	    						"\"Prefix\" : \"md\", " +
	    						"\"Namespace\" : \"urn:example:med:schemas:record\" " +
	    					"}], "+
	    				"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression,value={path=md:record/md:patient/md:patientDoB,Namespace={[{md,urn:example:med:schemas:record}{lab,http://somewhere/uri.html}{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}]},status=null,xpathExpressionWrapped=null},xpathCategory=urn:oasis:names:tc:xacml:3.0:attribute-category:resource}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with XPathExpression missing XPathCategory
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"Namespaces\" : [{ "+
	    						"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
	    					"}," +
	    					"{" +
	    						"\"Prefix\" : \"md\", " +
	    						"\"Namespace\" : \"urn:example:med:schemas:record\" " +
	    					"}], "+
	    				"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with XPathExpression missing XPath
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : [{ "+
	    						"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
	    					"}," +
	    					"{" +
	    						"\"Prefix\" : \"md\", " +
	    						"\"Namespace\" : \"urn:example:med:schemas:record\" " +
	    					"}] "+
            		"}" +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with XPathExpression without Namespaces
		// (path does not contain namespace references)
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"XPath\" : \"record/patient/patientDoB\" "+
            		"}" +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression,value={path=record/patient/patientDoB,Namespace=null,status=null,xpathExpressionWrapped=null},xpathCategory=urn:oasis:names:tc:xacml:3.0:attribute-category:resource}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with XPathExpression with 0 Namespaces
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : [], "+
	    				"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression,value={path=md:record/md:patient/md:patientDoB,Namespace=null,status=null,xpathExpressionWrapped=null},xpathCategory=urn:oasis:names:tc:xacml:3.0:attribute-category:resource}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// Category Attribute with XPathExpression with Namespaces without mandatory Namespace
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : [{ "+
	    						"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
	    					"}," +
	    					"{" +
	    						"\"Prefix\" : \"md\", " +
	    					"}], "+
	    				"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : [{ "+
	    					"}," +
	    					"{" +
	    						"\"Prefix\" : \"md\", " +
	    						"\"Namespace\" : \"urn:example:med:schemas:record\" " +
	    					"}], "+
	    				"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
				
		// Category Attribute with XPathExpression with Namespaces with 2 namespaces using same prefix
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : [{ "+
							"\"Prefix\" : \"md\", " +
	    						"\"Namespace\" : \"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" " +
	    					"}," +
	    					"{" +
	    						"\"Prefix\" : \"md\", " +
	    						"\"Namespace\" : \"urn:example:med:schemas:record\" " +
	    					"}], "+
	    				"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with XPathExpression without Namespaces which are used within the XPathExpression (NOTE: Error is not syntactic and is not found by converter)
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression,value={path=md:record/md:patient/md:patientDoB,Namespace=null,status=null,xpathExpressionWrapped=null},xpathCategory=urn:oasis:names:tc:xacml:3.0:attribute-category:resource}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with XPathExpression containing simple value (must be object)
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: \"simple Value\"" +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with Namespaces containing simple value (must be object)
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : [ \"simpleValue\"," +
	    					"{" +
	    						"\"Prefix\" : \"md\", " +
	    						"\"Namespace\" : \"urn:example:med:schemas:record\" " +
	    					"}], "+
	    				"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// Category Attribute with Namespaces non-string Namespace
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : [ {" +
	    						"\"Prefix\" : \"md\", " +
	    						"\"Namespace\" : 123 " +
	    					"}], "+
	    				"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with Namespaces non-string prefix
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : [ {" +
	    						"\"Prefix\" : 123, " +
	    						"\"Namespace\" : \"urn:example:med:schemas:record\" " +
	    					"}], "+
	    				"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with Namespaces non-string XPathCategory
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : 123," +
	    				"\"Namespaces\" : [ {" +
	    						"\"Prefix\" : \"md\", " +
	    						"\"Namespace\" : \"urn:example:med:schemas:record\" " +
	    					"}], "+
	    				"\"XPath\" : \"md:record/md:patient/md:patientDoB\" "+
            		"}" +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category Attribute with Namespaces non-string XPath
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"xpathExpression\", " +
	    			"\"Value\"	: {" +
	    				"\"XPathCategory\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\"," +
	    				"\"Namespaces\" : [ {" +
	    						"\"Prefix\" : \"md\", " +
	    						"\"Namespace\" : \"urn:example:med:schemas:record\" " +
	    					"}], "+
	    				"\"XPath\" : 123 "+
            		"}" +
					"}] } ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
	}


	
	@Test
	public void testContent() {

		// Category with Content in XML, escaped properly
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\",\"aValue\",\"aValue\"] " +
	    			"}]," +
					"\"Content\" : \"<?xml version=\\\"1.0\\\"?><catalog>" + 
						"<book id=\\\"bk101\\\"><author>Gambardella, Matthew</author><title>XML Developer's Guide</title><genre>Computer</genre>" +
						"<price>44.95</price><publish_date>2000-10-01</publish_date><description>An in-depth look at creating applications with XML.</description>"+
						"</book></catalog>\"" +
					"} ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]},contentRoot=[catalog: null]}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category with Content in XML, double quotes and back-slashes NOT escaped properly?
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\",\"aValue\",\"aValue\"] " +
	    			"}]," +
					"\"Content\" : \"<?xml version=\\\"1.0\\\"?><catalog>" + 
						"<book id=\"bk101\\\"><author>Gambardella, Matthew</author><title>XML Developer's Guide</title><genre>Computer</genre>" +
						"<price>44.95</price><publish_date>2000-10-01</publish_date><description>An in-depth look at creating applications with XML.</description>"+
						"</book></catalog>\"" +
					"} ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category with Content in Base64
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\",\"aValue\",\"aValue\"] " +
	    			"}]," +
					"\"Content\" :  \"PD94bWwgdmVyc2lvbj0iMS4wIj8+PGNhdGFsb2c+PGJvb2sgaWQ9ImJrMTAxIj48YXV0aG9yPkdhbWJhcmRlbGxhLCBNYXR0aGV3PC9hdXRob3I+PHRpdGxlPlhNT" +
							"CBEZXZlbG9wZXIncyBHdWlkZTwvdGl0bGU+PGdlbnJlPkNvbXB1dGVyPC9nZW5yZT48cHJpY2U+NDQuOTU8L3ByaWNlPjxwdWJsaXNoX2RhdGU+MjAwMC0xMC0wMTwvcHVibGlzaF"+
							"9kYXRlPjxkZXNjcmlwdGlvbj5BbiBpbi1kZXB0aCBsb29rIGF0IGNyZWF0aW5nIGFwcGxpY2F0aW9ucyB3aXRoIFhNTC48L2Rlc2NyaXB0aW9uPjwvYm9vaz48L2NhdGFsb2c+\"" +
					"} ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]},contentRoot=[catalog: null]}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Category with Bad Content in Base64
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\",\"aValue\",\"aValue\"] " +
	    			"}]," +
					"\"Content\" :  \"PD94bWwgdmV\"" +
					"} ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
	}
	

	
	
	@Test
	public void testDuplicates() {
		// duplicate of same element within Category array is ok
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: \"abc\" " +
					"}] }, " +
					"{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
			    			"\"Id\" : \"document-id\", " +
			    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
			    			"\"Value\"	: \"abc\" " +
							"}] } "
					+ "] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}],includeInResults=false}]}}{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=abc}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// duplicate Attribute
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [" +
					"{\"CategoryId\" : \"custom-category\","
					+ " \"Attribute\" : [{" +
			    			"\"Id\" : \"document-id\", " +
			    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
			    			"\"Value\"	: \"abc\" " +
							"}], "
					+ " \"Attribute\" : [{" +
			    			"\"Id\" : \"document-id\", " +
			    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
			    			"\"Value\"	: \"abc\" " +
							"}] "
							+ "} "
					+ " }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// dup id
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: \"abc\" " +
					"}] } " 
					+ "] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// dup DataType
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: \"abc\" " +
					"}] } " 
					+ "] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// dup Value
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"DataType\"	: \"http://www.w3.org/2001/XMLSchema#string\", " +
	    			"\"Value\"	: \"abc\" " +
	    			"\"Value\"	: \"abc\" " +
					"}] } " 
					+ "] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// duplicate Content
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{"
					+ "\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    				"\"Id\" : \"document-id\", " +
	    				"\"Value\"	: [ \"aValue\",\"aValue\",\"aValue\"] " +
	    				"}]," +
	    				"\"Content\" : \"<?xml version=\\\"1.0\\\"?><catalog>" + 
							"<book id=\\\"bk101\\\"><author>Gambardella, Matthew</author><title>XML Developer's Guide</title><genre>Computer</genre>" +
							"<price>44.95</price><publish_date>2000-10-01</publish_date><description>An in-depth look at creating applications with XML.</description>"+
							"</book></catalog>\" , " +
						"\"Content\" : \"<?xml version=\\\"1.0\\\"?><catalog>" + 
							"<book id=\\\"bk101\\\"><author>Gambardella, Matthew</author><title>XML Developer's Guide</title><genre>Computer</genre>" +
							"<price>44.95</price><publish_date>2000-10-01</publish_date><description>An in-depth look at creating applications with XML.</description>"+
							"</book></catalog>\"" +
					"} ] }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
	}
	

	
//TODO - Shorthand for CategoryId ????
	
	
}
