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
 * Test JSON Request convert to object - Default Category object tests
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
public class RequestDefaultCategoryTest {
	
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

	
	// test Shorthand Category notation for elements not tested in their own section below.
	// Categories that are more commonly used are fully tested. 
	// Given that the functions within the categories are the same irrespective of the name of the category, 
	// we assume that the contents of the category will work ok once the Shorthand notation is recognized, so all we need to test is the shorthand
	// The ones that are tested in their own sections are:
	//		AccessSubject
	//		Action
	//		Resource
	//		Environment 
	// test Subject
	@Test
	public void testCategoryShorthand() {
	
		// RecipientSubject present both as element within Category and as separate RecipientSubject element at same level as Category
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
						"}] }, "
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
					"\"RecipientSubject\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// IntermediarySubject present both as element within Category and as separate IntermediarySubject element at same level as Category
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
						"}] }, "
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
					"\"IntermediarySubject\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// Codebase present both as element within Category and as separate Codebase element at same level as Category
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:codebase\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
						"}] }, "
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:codebase\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
					"\"Codebase\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:codebase,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:codebase,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:codebase,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:codebase,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:codebase,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:codebase,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:codebase,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		
		// RequestingMachine present both as element within Category and as separate RequestingMachine element at same level as Category
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
						"}] }, "
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
					"\"RequestingMachine\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
	}





	


	


	
	
	
	
	
	// test AccessSubject
	// Include test for backward compatibility with "Subject"
	@Test
	public void testAccessSubjectRequest() {
		
		// AccessSubject absent
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// AccessSubject as normal element under Category (with CategoryId==subject category id)
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"aValue\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// multiple AccessSubjects under Category
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": ["
					+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\", \"Attribute\" : [{" +
	    				"\"Id\" : \"document-id\", " +
	    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] }, "
					+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\", \"Attribute\" : [{" +
	    				"\"Id\" : \"document-id\", " +
	    				"\"Value\"	: \"aValue\"" +
					"}] } "
					+ "] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// AccessSubject present both as element within Category and as separate AccessSubject element at same level as Category
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
						"}] }, "
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// AccessSubject present, no other Category element
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// Subject present, no other Category element (Backward Compatibility
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Subject\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// AccessSubject present, 1/multiple other Category element also present
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// 2 AccessSubjects - duplicates fail
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
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
					"} " 
					+ 
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
					"} " 
				+ " }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// AccessSubject with correct Category value
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"AccessSubject\" : { " +
						"\"CategoryId\" : \"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject\" ," +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// AccessSubject with wrong Category value
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"AccessSubject\" : { " +
						"\"CategoryId\" : \"notthesubject\" ," +
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
					"} " 
				+ " }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// AccessSubject with array of sub-object AccessSubjects (Multi Decision)
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"AccessSubject\" : ["
					+ "{ " +
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
					"}, "
					+ "{ " +
					"\"Attribute\": [ " +
						"{ " +
							"\"Id\" : \"subject-id\", " +
							"\"Value\" : \"Arless\" " +
						"}, " +
						"{ " +
							"\"Id\" : \"location\", " +
							"\"Value\" : \"Somewhere\" " +
						"} " +
					"] " +
					"}, "
					+ "{ " +
					"\"Attribute\": [ " +
						"{ " +
							"\"Id\" : \"subject-id\", " +
							"\"Value\" : \"Barry\" " +
						"}, " +
						"{ " +
							"\"Id\" : \"location\", " +
							"\"Value\" : \"Elsewhere\" " +
						"} " +
					"] " +
					"} "
				+ "]"
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Arless}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Somewhere}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Barry}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:1.0:subject-category:access-subject,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Elsewhere}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}

	}
	
	
	
	
	
	
	
	
	
	// Action ... duplicate all AccessSubject tests...
	// test Action
	@Test
	public void testActionRequest() {
		
		// Action absent
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Action as normal element under Category (with CategoryId==subject category id)
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"aValue\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// multiple Actions under Category
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": ["
					+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
	    				"\"Id\" : \"document-id\", " +
	    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] }, "
					+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
	    				"\"Id\" : \"document-id\", " +
	    				"\"Value\"	: \"aValue\"" +
					"}] } "
					+ "] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Action present both as element within Category and as separate Action element at same level as Category
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
						"}] }, "
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
					"\"Action\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Action present, no other Category element
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Action\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Action present, 1/multiple other Category element also present
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
					"\"Action\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// 2 Actions - duplicates fail
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Action\" : { " +
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
					"} " 
					+ 
					"\"Action\" : { " +
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
					"} " 
				+ " }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Action with correct Category value
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Action\" : { " +
						"\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:action\" ," +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Action with wrong Category value
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Action\" : { " +
						"\"CategoryId\" : \"notthesubject\" ," +
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
					"} " 
				+ " }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Action with array of sub-object Actions (Multi Decision)
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Action\" : ["
					+ "{ " +
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
					"}, "
					+ "{ " +
					"\"Attribute\": [ " +
						"{ " +
							"\"Id\" : \"subject-id\", " +
							"\"Value\" : \"Arless\" " +
						"}, " +
						"{ " +
							"\"Id\" : \"location\", " +
							"\"Value\" : \"Somewhere\" " +
						"} " +
					"] " +
					"}, "
					+ "{ " +
					"\"Attribute\": [ " +
						"{ " +
							"\"Id\" : \"subject-id\", " +
							"\"Value\" : \"Barry\" " +
						"}, " +
						"{ " +
							"\"Id\" : \"location\", " +
							"\"Value\" : \"Elsewhere\" " +
						"} " +
					"] " +
					"} "
				+ "]"
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Arless}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Somewhere}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Barry}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:action,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Elsewhere}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}

	}
	
	
	
	
	// Resource ... duplicate all AccessSubject tests...
	// test Resource
	@Test
	public void testResourceRequest() {
		
		// Resource absent
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Resource as normal element under Category (with CategoryId==subject category id)
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"aValue\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// multiple Resources under Category
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": ["
					+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
	    				"\"Id\" : \"document-id\", " +
	    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] }, "
					+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
	    				"\"Id\" : \"document-id\", " +
	    				"\"Value\"	: \"aValue\"" +
					"}] } "
					+ "] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Resource present both as element within Category and as separate Resource element at same level as Category
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
						"}] }, "
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
					"\"Resource\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Resource present, no other Category element
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Resource\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Resource present, 1/multiple other Category element also present
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
					"\"Resource\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// 2 Resources - duplicates fail
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Resource\" : { " +
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
					"} " 
					+ 
					"\"Resource\" : { " +
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
					"} " 
				+ " }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Resource with correct Category value
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Resource\" : { " +
						"\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" ," +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Resource with wrong Category value
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Resource\" : { " +
						"\"CategoryId\" : \"notthesubject\" ," +
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
					"} " 
				+ " }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Resource with array of sub-object Resources (Multi Decision)
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Resource\" : ["
					+ "{ " +
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
					"}, "
					+ "{ " +
					"\"Attribute\": [ " +
						"{ " +
							"\"Id\" : \"subject-id\", " +
							"\"Value\" : \"Arless\" " +
						"}, " +
						"{ " +
							"\"Id\" : \"location\", " +
							"\"Value\" : \"Somewhere\" " +
						"} " +
					"] " +
					"}, "
					+ "{ " +
					"\"Attribute\": [ " +
						"{ " +
							"\"Id\" : \"subject-id\", " +
							"\"Value\" : \"Barry\" " +
						"}, " +
						"{ " +
							"\"Id\" : \"location\", " +
							"\"Value\" : \"Elsewhere\" " +
						"} " +
					"] " +
					"} "
				+ "]"
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Arless}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Somewhere}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Barry}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:resource,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Elsewhere}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}

	}
	
	
	
	
	
	
	// Environment ... duplicate all AccessSubject tests ...
	// test Environment
	@Test
	public void testEnvironmentRequest() {
		
		// Environment absent
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Environment as normal element under Category (with CategoryId==subject category id)
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": [{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
	    			"\"Id\" : \"document-id\", " +
	    			"\"Value\"	: \"aValue\" " +
					"}] } ] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// multiple Environments under Category
		try {
			request = JSONRequest.load("{\"Request\" : {\"Category\": ["
					+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
	    				"\"Id\" : \"document-id\", " +
	    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
					"}] }, "
					+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
	    				"\"Id\" : \"document-id\", " +
	    				"\"Value\"	: \"aValue\"" +
					"}] } "
					+ "] }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Environment present both as element within Category and as separate Environment element at same level as Category
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: [ \"aValue\", \"aValue\", \"aValue\" ] " +
						"}] }, "
						+ "{\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
					"\"Environment\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=document-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Environment present, no other Category element
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Environment\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Environment present, 1/multiple other Category element also present
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ "\"Category\": ["
						+ "{\"CategoryId\" : \"custom-category\", \"Attribute\" : [{" +
		    				"\"Id\" : \"document-id\", " +
		    				"\"Value\"	: \"aValue\"" +
						"}] } "
						+ "]," +
					"\"Environment\" : { " +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=custom-category,attributes=[{attributeId=document-id,category=custom-category,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=aValue}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// 2 Environments - duplicates fail
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Environment\" : { " +
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
					"} " 
					+ 
					"\"Environment\" : { " +
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
					"} " 
				+ " }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Environment with correct Category value
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Environment\" : { " +
						"\"CategoryId\" : \"urn:oasis:names:tc:xacml:3.0:attribute-category:environment\" ," +
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
					"} " 
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Environment with wrong Category value
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Environment\" : { " +
						"\"CategoryId\" : \"notthesubject\" ," +
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
					"} " 
				+ " }}");
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Environment with array of sub-object Environments (Multi Decision)
		try {
			request = JSONRequest.load("{\"Request\" : {"
					+ 
					"\"Environment\" : ["
					+ "{ " +
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
					"}, "
					+ "{ " +
					"\"Attribute\": [ " +
						"{ " +
							"\"Id\" : \"subject-id\", " +
							"\"Value\" : \"Arless\" " +
						"}, " +
						"{ " +
							"\"Id\" : \"location\", " +
							"\"Value\" : \"Somewhere\" " +
						"} " +
					"] " +
					"}, "
					+ "{ " +
					"\"Attribute\": [ " +
						"{ " +
							"\"Id\" : \"subject-id\", " +
							"\"Value\" : \"Barry\" " +
						"}, " +
						"{ " +
							"\"Id\" : \"location\", " +
							"\"Value\" : \"Elsewhere\" " +
						"} " +
					"] " +
					"} "
				+ "]"
				+ " }}");
			assertEquals("{returnPolicyIdList=false,combinedDecision=false,requestAttributes=[{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Andreas}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Gamla Stan}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Arless}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Somewhere}],includeInResults=false}]}}{super={category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,attributes=[{attributeId=subject-id,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Barry}],includeInResults=false}{attributeId=location,category=urn:oasis:names:tc:xacml:3.0:attribute-category:environment,values=[{dataTypeId=http://www.w3.org/2001/XMLSchema#string,value=Elsewhere}],includeInResults=false}]}}]}", request.toString());
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}

	}

	
}
