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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.math.BigInteger;

import org.junit.Test;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdAttribute;
import com.att.research.xacml.std.StdAttributeCategory;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.StdIdReference;
import com.att.research.xacml.std.StdMutableAdvice;
import com.att.research.xacml.std.StdMutableAttribute;
import com.att.research.xacml.std.StdMutableAttributeAssignment;
import com.att.research.xacml.std.StdMutableMissingAttributeDetail;
import com.att.research.xacml.std.StdMutableObligation;
import com.att.research.xacml.std.StdMutableResponse;
import com.att.research.xacml.std.StdMutableResult;
import com.att.research.xacml.std.StdMutableStatus;
import com.att.research.xacml.std.StdMutableStatusDetail;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.StdVersion;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacml.std.datatypes.StringNamespaceContext;
import com.att.research.xacml.std.datatypes.XPathExpressionWrapper;
import com.att.research.xacml.std.json.JSONResponse;
import com.att.research.xacml.std.json.JSONStructureException;

/**
 * Test JSON Responses
 * 
 * TO RUN - use jUnit
 * In Eclipse select this file or the enclosing directory, right-click and select Run As/JUnit Test
 * 
 * @author glenngriffin
 *
 */
public class ResponseTest {

	String jsonResponse;
	
	StdMutableResponse response;
	
	StdMutableResult result;
	
	StdMutableStatus status;
	
	
	// Note: Initially test responses without Obligations, Associated Advice, Attributes, or PolicyIdentifier
	
	
	@Test
	public void testEmptyAndDecisions() {
		// null response
		try {
			jsonResponse = JSONResponse.toString(null, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// empty response (no Result object)
		response = new StdMutableResponse();
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// just decision, no status
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// just status (empty), no decision
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();		
		result.setStatus(status);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// just status (non-empty), no decision
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_OK);
		result.setStatus(status);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// test other decisions without Status
		
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.DENY);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Deny\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.NOTAPPLICABLE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"NotApplicable\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.INDETERMINATE_DENY);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Indeterminate{D}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.INDETERMINATE_DENYPERMIT);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Indeterminate{DP}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.INDETERMINATE_PERMIT);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Indeterminate{P}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}

		
		// test Multiple Decisions - success
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		response.add(result);
		StdMutableResult result2 = new StdMutableResult();
		result2.setDecision(Decision.DENY);
		response.add(result2);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\"},{\"Decision\":\"Deny\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// test Multiple Decisions - one success and one error
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		response.add(result);
		result2 = new StdMutableResult();
		result2.setDecision(Decision.INDETERMINATE);
		response.add(result2);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\"},{\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
	}
		

	
	
	// Test with every field filled in with multiple values where appropriate
	@Test
	public void testAllFieldsResponse() {	
		
		// fully-loaded multiple response
		
		StdMutableResponse response = new StdMutableResponse();
		// create a Status object
		StdMutableStatus status = new StdMutableStatus(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
		status.setStatusMessage("some status message");
		StdMutableStatusDetail statusDetailIn = new StdMutableStatusDetail();
		StdMutableMissingAttributeDetail mad = new StdMutableMissingAttributeDetail();
		mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "doh"));
		mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_INTEGER.getId(), "5432"));
		mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "meh"));
		mad.setAttributeId(XACML3.ID_ACTION_PURPOSE);
		mad.setCategory(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION);
		mad.setDataTypeId(XACML3.ID_DATATYPE_STRING);
		mad.setIssuer("an Issuer");
		statusDetailIn.addMissingAttributeDetail(mad);
		status.setStatusDetail(statusDetailIn);
		// create a single result object
		StdMutableResult result = new StdMutableResult(status);
		// set the decision
		result.setDecision(Decision.INDETERMINATE);
		// put the Result into the Response
		response.add(result);

		
		// create a new Result with a different Decision
		status = new StdMutableStatus(StdStatusCode.STATUS_CODE_OK);
		result = new StdMutableResult(status);
		result.setDecision(Decision.DENY);
		
		StdMutableObligation obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart")));
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer2", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Ned")));
		result.addObligation(obligation);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_SUBJECT_CATEGORY_INTERMEDIARY_SUBJECT);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer3", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Maggie")));
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer4", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Homer")));
		result.addObligation(obligation);
		
		
		StdMutableAdvice advice = new StdMutableAdvice();
		advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"advice-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu")));
		advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				null, 
				XACML3.ID_SUBJECT, 
				"advice-issuerNoCategory", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Crusty")));
		result.addAdvice(advice);
		
		
		response.add(result);
		
		
		// create a new Result with a different Decision
		// add Child/minor status codes within the main status
		StdStatusCode childChildChildStatusCode = new StdStatusCode(new IdentifierImpl("childChildChildStatusCode"));
		StdStatusCode childChildStatusCode = new StdStatusCode(new IdentifierImpl("childChildStatusCode"), childChildChildStatusCode);
		StdStatusCode child1StatusCode = new StdStatusCode(new IdentifierImpl("child1StatusCode"), childChildStatusCode);
		StdStatusCode statusCode = new StdStatusCode(XACML3.ID_STATUS_OK, child1StatusCode);
		
		status = new StdMutableStatus(statusCode);
		
		
		result = new StdMutableResult(status);
		result.setDecision(Decision.PERMIT);
		
		
		
		
		// add attribute list in result
		Identifier categoryIdentifier = new IdentifierImpl("firstCategory");
		Attribute[] attrList = {
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), "AIssue", true),
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent2"), new StdAttributeValue<String>(DataTypes.DT_YEARMONTHDURATION.getId(), "P10Y4M"), "BIssue", false),
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent3"), new StdAttributeValue<Double>(DataTypes.DT_DOUBLE.getId(), 765.432), "CIssue", true),
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent4"), new StdAttributeValue<Boolean>(DataTypes.DT_BOOLEAN.getId(), true), "DIssue", true),
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent5"), new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)), "EIssue", true),
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrNoIssuer"), new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)), null, true) };
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, Arrays.asList(attrList)));
		categoryIdentifier = new IdentifierImpl("secondCategory");
		Attribute[] secondAttrList = {
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent12"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu2"), "AIssue2", true),
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent22"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Abc2"), "BIssue2", false),
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent32"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Der2"), "CIssue2", true) };
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, Arrays.asList(secondAttrList)));
		
		
		// add PolicyIdentifierList to result
		StdIdReference policyIdentifier1 = null;
		StdIdReference policyIdentifier2 = null;
		StdIdReference policySetIdentifier1 = null;
		StdIdReference policySetIdentifier2 = null;
		try {
			policyIdentifier1 = new StdIdReference(new IdentifierImpl("idRef1"), StdVersion.newInstance("1.2.3"));
			policyIdentifier2 = new StdIdReference(new IdentifierImpl("idRef2_NoVersion"));
			policySetIdentifier1 = new StdIdReference(new IdentifierImpl("idSetRef1"), StdVersion.newInstance("4.5.6.7.8.9.0"));
			policySetIdentifier2 = new StdIdReference(new IdentifierImpl("idSetRef2_NoVersion"));
		} catch (ParseException e1) {
			fail("creating policyIds, e="+e1);
		}
		
		result.addPolicyIdentifier(policyIdentifier1);
		result.addPolicyIdentifier(policyIdentifier2);
	
		result.addPolicySetIdentifier(policySetIdentifier1);
		result.addPolicySetIdentifier(policySetIdentifier2);
		
		response.add(result);
	
		// convert Response to JSON
		try {
			jsonResponse = JSONResponse.toString(response, false);
System.out.println(jsonResponse);
//System.out.println(JSONResponse.toString(response, true));
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"},\"StatusMessage\":\"some status message\",\"StatusDetail\":\"<MissingAttributeDetail Category=\\\\\\\"urn:oasis:names:tc:xacml:3.0:attribute-category:action\\\\\\\" AttributeId=\\\\\\\"urn:oasis:names:tc:xacml:2.0:action:purpose\\\\\\\" DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#string\\\\\\\" Issuer=\\\\\\\"an Issuer\\\\\\\"><AttributeValue DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#string\\\\\\\">doh</AttributeValue><AttributeValue DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#integer\\\\\\\">5432</AttributeValue><AttributeValue DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#string\\\\\\\">meh</AttributeValue></MissingAttributeDetail>\"},\"Decision\":\"Indeterminate\"},{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:ok\"}},\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"obligation-issuer1\",\"Value\":\"Bart\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Issuer\":\"obligation-issuer2\",\"Value\":\"Ned\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]},{\"Id\":\"urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject\",\"AttributeAssignment\":[{\"Issuer\":\"obligation-issuer3\",\"Value\":\"Maggie\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Issuer\":\"obligation-issuer4\",\"Value\":\"Homer\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}],\"Decision\":\"Deny\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"advice-issuer1\",\"Value\":\"Apu\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Issuer\":\"advice-issuerNoCategory\",\"Value\":\"Crusty\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}]},{\"Status\":{\"StatusCode\":{\"StatusCode\":{\"StatusCode\":{\"StatusCode\":{\"Value\":\"childChildChildStatusCode\"},\"Value\":\"childChildStatusCode\"},\"Value\":\"child1StatusCode\"},\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:ok\"}},\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":\"Apu\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"CIssue\",\"Value\":765.432,\"DataType\":\"http://www.w3.org/2001/XMLSchema#double\",\"AttributeId\":\"attrIdent3\"},{\"Issuer\":\"DIssue\",\"Value\":true,\"DataType\":\"http://www.w3.org/2001/XMLSchema#boolean\",\"AttributeId\":\"attrIdent4\"},{\"Issuer\":\"EIssue\",\"Value\":4567,\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"attrIdent5\"},{\"Value\":4567,\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"attrNoIssuer\"}]},{\"CategoryId\":\"secondCategory\",\"Attribute\":[{\"Issuer\":\"AIssue2\",\"Value\":\"Apu2\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent12\"},{\"Issuer\":\"CIssue2\",\"Value\":\"Der2\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent32\"}]}],\"Decision\":\"Permit\",\"PolicyIdentifier\":{\"PolicyIdReference\":[{\"Id\":\"idRef1\",\"Version\":\"1.2.3\"},{\"Id\":\"idRef2_NoVersion\"}],\"PolicySetIdReference\":[{\"Id\":\"idSetRef1\",\"Version\":\"4.5.6.7.8.9.0\"},{\"Id\":\"idSetRef2_NoVersion\"}]}}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
	}
	
	
	
	
	// combinations of Status values with Decision values
	@Test
	public void testDecisionStatusMatch() {
		// the tests in this method use different values and do not change structures, so we can re-use the objects
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		result.setStatus(status);
		response.add(result);
		
		// StatusCode = OK
		status.setStatusCode(StdStatusCode.STATUS_CODE_OK);
		result.setDecision(Decision.PERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:ok\"}},\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.DENY);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:ok\"}},\"Decision\":\"Deny\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.NOTAPPLICABLE);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:ok\"}},\"Decision\":\"NotApplicable\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.INDETERMINATE);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.INDETERMINATE_DENY);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.INDETERMINATE_DENYPERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.INDETERMINATE_PERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		
		
		
		
		// StatusCode = SyntaxError
		status.setStatusCode(StdStatusCode.STATUS_CODE_SYNTAX_ERROR);
		result.setDecision(Decision.PERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.DENY);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.NOTAPPLICABLE);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.INDETERMINATE);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:syntax-error\"}},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.INDETERMINATE_DENY);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:syntax-error\"}},\"Decision\":\"Indeterminate{D}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.INDETERMINATE_DENYPERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:syntax-error\"}},\"Decision\":\"Indeterminate{DP}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.INDETERMINATE_PERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:syntax-error\"}},\"Decision\":\"Indeterminate{P}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// StatusCode = ProcessingError
		status.setStatusCode(StdStatusCode.STATUS_CODE_PROCESSING_ERROR);
		result.setDecision(Decision.PERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.DENY);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.NOTAPPLICABLE);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.INDETERMINATE);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:processing-error\"}},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.INDETERMINATE_DENY);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:processing-error\"}},\"Decision\":\"Indeterminate{D}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.INDETERMINATE_DENYPERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:processing-error\"}},\"Decision\":\"Indeterminate{DP}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.INDETERMINATE_PERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:processing-error\"}},\"Decision\":\"Indeterminate{P}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		
		// StatusCode = MissingAttribute
		status.setStatusCode(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
		result.setDecision(Decision.PERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.DENY);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.NOTAPPLICABLE);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		result.setDecision(Decision.INDETERMINATE);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"}},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.INDETERMINATE_DENY);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"}},\"Decision\":\"Indeterminate{D}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.INDETERMINATE_DENYPERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"}},\"Decision\":\"Indeterminate{DP}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		result.setDecision(Decision.INDETERMINATE_PERMIT);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"}},\"Decision\":\"Indeterminate{P}\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
	}

	
	

	// tests related to Status and its components
	@Test
	public void testStatus() {
		// Status with no StatusCode - error
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		result.setStatus(status);
		result.setDecision(Decision.PERMIT);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Status with StatusMessage when OK
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_OK);
		status.setStatusMessage("I'm ok, you're ok");
		result.setStatus(status);
		result.setDecision(Decision.PERMIT);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:ok\"},\"StatusMessage\":\"I'm ok, you're ok\"},\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// Status with StatusDetail when OK
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_OK);
		StdMutableStatusDetail statusDetail = new StdMutableStatusDetail();
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.PERMIT);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Status with StatusMessage when SyntaxError
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_SYNTAX_ERROR);
		status.setStatusMessage("I'm ok, you're ok");
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:syntax-error\"},\"StatusMessage\":\"I'm ok, you're ok\"},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// Status with empty StatusDetail when SyntaxError
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_SYNTAX_ERROR);
		statusDetail = new StdMutableStatusDetail();
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		// Status with StatusMessage when ProcessingError
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_PROCESSING_ERROR);
		status.setStatusMessage("I'm ok, you're ok");
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:processing-error\"},\"StatusMessage\":\"I'm ok, you're ok\"},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// Status with empty StatusDetail when ProcessingError
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_PROCESSING_ERROR);
		statusDetail = new StdMutableStatusDetail();
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}

		
		// Status with StatusMessage when MissingAttribute
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
		status.setStatusMessage("I'm ok, you're ok");
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"},\"StatusMessage\":\"I'm ok, you're ok\"},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// Status with empty StatusDetail when MissingAttribute
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
		statusDetail = new StdMutableStatusDetail();
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"}},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		
		// Status with StatusDetail with empty detail when MissingAttribute
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
		statusDetail = new StdMutableStatusDetail();
		StdMutableMissingAttributeDetail mad = new StdMutableMissingAttributeDetail();
		statusDetail.addMissingAttributeDetail(mad);
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Status with StatusDetail with valid detail with no value when MissingAttribute
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
		statusDetail = new StdMutableStatusDetail();
		mad = new StdMutableMissingAttributeDetail();
		mad.setAttributeId(new IdentifierImpl("mad"));
		mad.setCategory(XACML3.ID_ACTION);
		mad.setDataTypeId(DataTypes.DT_STRING.getId());
		statusDetail.addMissingAttributeDetail(mad);
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"},\"StatusDetail\":\"<MissingAttributeDetail Category=\\\\\\\"urn:oasis:names:tc:xacml:1.0:action\\\\\\\" AttributeId=\\\\\\\"mad\\\\\\\" DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#string\\\\\\\"></MissingAttributeDetail>\"},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// Status with StatusDetail with valid detail with value when MissingAttribute
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
		statusDetail = new StdMutableStatusDetail();
		mad = new StdMutableMissingAttributeDetail();
		mad.setAttributeId(new IdentifierImpl("mad"));
		mad.setCategory(XACML3.ID_ACTION);
		mad.setDataTypeId(DataTypes.DT_STRING.getId());	
		mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "meh"));
		statusDetail.addMissingAttributeDetail(mad);
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"},\"StatusDetail\":\"<MissingAttributeDetail Category=\\\\\\\"urn:oasis:names:tc:xacml:1.0:action\\\\\\\" AttributeId=\\\\\\\"mad\\\\\\\" DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#string\\\\\\\"><AttributeValue DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#string\\\\\\\">meh</AttributeValue></MissingAttributeDetail>\"},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// Status with StatusDetail with array valid detail with value when MissingAttribute
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
		statusDetail = new StdMutableStatusDetail();
		mad = new StdMutableMissingAttributeDetail();
		mad.setAttributeId(new IdentifierImpl("mad"));
		mad.setCategory(XACML3.ID_ACTION);
		mad.setDataTypeId(DataTypes.DT_STRING.getId());	
		mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "meh"));
		mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "nu?"));
		statusDetail.addMissingAttributeDetail(mad);
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"},\"StatusDetail\":\"<MissingAttributeDetail Category=\\\\\\\"urn:oasis:names:tc:xacml:1.0:action\\\\\\\" AttributeId=\\\\\\\"mad\\\\\\\" DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#string\\\\\\\"><AttributeValue DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#string\\\\\\\">meh</AttributeValue><AttributeValue DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#string\\\\\\\">nu?</AttributeValue></MissingAttributeDetail>\"},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// Status with StatusDetail with valid detail with Integer value when MissingAttribute
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
		statusDetail = new StdMutableStatusDetail();
		mad = new StdMutableMissingAttributeDetail();
		mad.setAttributeId(new IdentifierImpl("mad"));
		mad.setCategory(XACML3.ID_ACTION);
		mad.setDataTypeId(DataTypes.DT_INTEGER.getId());	
		mad.addAttributeValue(new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(1111)));
		statusDetail.addMissingAttributeDetail(mad);
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
//			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"},\"StatusDetail\":\"<MissingAttributeDetail Category=\\\\\\\"urn:oasis:names:tc:xacml:1.0:action\\\\\\\" AttributeId=\\\\\\\"mad\\\\\\\" DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#string\\\\\\\"><AttributeValue DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#integer\\\\\\\">1111</AttributeValue></MissingAttributeDetail>\"},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);


			fail("operation failed, e="+e + sw.toString());
		}
		
		// Status with StatusDetail with array valid detail with Integer value when MissingAttribute
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
		statusDetail = new StdMutableStatusDetail();
		mad = new StdMutableMissingAttributeDetail();
		mad.setAttributeId(new IdentifierImpl("mad"));
		mad.setCategory(XACML3.ID_ACTION);
		mad.setDataTypeId(DataTypes.DT_STRING.getId());	
		mad.addAttributeValue(new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(1111)));
		mad.addAttributeValue(new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(2222)));
		statusDetail.addMissingAttributeDetail(mad);
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"},\"StatusDetail\":\"<MissingAttributeDetail Category=\\\\\\\"urn:oasis:names:tc:xacml:1.0:action\\\\\\\" AttributeId=\\\\\\\"mad\\\\\\\" DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#string\\\\\\\"><AttributeValue DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#integer\\\\\\\">1111</AttributeValue><AttributeValue DataType=\\\\\\\"http://www.w3.org/2001/XMLSchema#integer\\\\\\\">2222</AttributeValue></MissingAttributeDetail>\"},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
//		StringNamespaceContext snc = new StringNamespaceContext();
//		try {
//			snc.add("defaultURI");
//			snc.add("md", "referenceForMD");
//		} catch (Exception e) {
//			fail("unable to create NamespaceContext e="+e);
//		}
//		XPathExpressionWrapper xpathExpressionWrapper = new XPathExpressionWrapper(snc, "//md:record");
//
//TODO - assume that we will never try to pass back an XPathExpression in a MissingAttributeDetail - it doesn't make sense and is unclear how to put into XML
//		// Status with StatusDetail with valid detail with XPathExpression value when MissingAttribute
//		response = new StdMutableResponse();
//		result = new StdMutableResult();
//		status = new StdMutableStatus();
//		status.setStatusCode(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
//		statusDetail = new StdMutableStatusDetail();
//		mad = new StdMutableMissingAttributeDetail();
//		mad.setAttributeId(new IdentifierImpl("mad"));
//		mad.setCategory(XACML3.ID_ACTION);
//		mad.setDataTypeId(DataTypes.DT_STRING.getId());	
//		mad.addAttributeValue(new StdAttributeValue<XPathExpressionWrapper>(DataTypes.DT_XPATHEXPRESSION.getId(), xpathExpressionWrapper, new IdentifierImpl("xpathCategoryId")));
//		statusDetail.addMissingAttributeDetail(mad);
//		status.setStatusDetail(statusDetail);
//		result.setStatus(status);
//		result.setDecision(Decision.INDETERMINATE);
//		response.add(result);
//		try {
//			jsonResponse = JSONResponse.toString(response, false);
//			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"},\"StatusDetail\":\"<AttributeValue>1111</AttributeValue><Category>urn:oasis:names:tc:xacml:1.0:action</Category><AttributeId>mad</AttributeId><DataType>http://www.w3.org/2001/XMLSchema#string</DataType></MissingAttributeDetail>\"},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
//		} catch (Exception e) {
//			fail("operation failed, e="+e);
//		}
//		
//		// Status with StatusDetail with array valid detail with XPathExpression value when MissingAttribute
//		response = new StdMutableResponse();
//		result = new StdMutableResult();
//		status = new StdMutableStatus();
//		status.setStatusCode(StdStatusCode.STATUS_CODE_MISSING_ATTRIBUTE);
//		statusDetail = new StdMutableStatusDetail();
//		mad = new StdMutableMissingAttributeDetail();
//		mad.setAttributeId(new IdentifierImpl("mad"));
//		mad.setCategory(XACML3.ID_ACTION);
//		mad.setDataTypeId(DataTypes.DT_STRING.getId());	
//		mad.addAttributeValue(new StdAttributeValue<XPathExpressionWrapper>(DataTypes.DT_XPATHEXPRESSION.getId(), xpathExpressionWrapper, new IdentifierImpl("xpathCategoryId1")));
//		mad.addAttributeValue(new StdAttributeValue<XPathExpressionWrapper>(DataTypes.DT_XPATHEXPRESSION.getId(), xpathExpressionWrapper, new IdentifierImpl("xpathCategoryId2")));
//		statusDetail.addMissingAttributeDetail(mad);
//		status.setStatusDetail(statusDetail);
//		result.setStatus(status);
//		result.setDecision(Decision.INDETERMINATE);
//		response.add(result);
//		try {
//			jsonResponse = JSONResponse.toString(response, false);
//			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:missing-attribute\"},\"StatusDetail\":\"<AttributeValue>1111</AttributeValue><AttributeValue>2222</AttributeValue><Category>urn:oasis:names:tc:xacml:1.0:action</Category><AttributeId>mad</AttributeId><DataType>http://www.w3.org/2001/XMLSchema#string</DataType></MissingAttributeDetail>\"},\"Decision\":\"Indeterminate\"}]}", jsonResponse);
//		} catch (Exception e) {
//			fail("operation failed, e="+e);
//		}
		
//TODO - try with other data types, esp XPathExpression		
		
		// Status with StatusDetail with array valid detail with value when SyntaxError
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_SYNTAX_ERROR);
		statusDetail = new StdMutableStatusDetail();
		mad = new StdMutableMissingAttributeDetail();
		mad.setAttributeId(new IdentifierImpl("mad"));
		mad.setCategory(XACML3.ID_ACTION);
		mad.setDataTypeId(DataTypes.DT_STRING.getId());	
		mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "meh"));
		mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "nu?"));
		statusDetail.addMissingAttributeDetail(mad);
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Status with StatusDetail with array valid detail with value when ProcessingError
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		status.setStatusCode(StdStatusCode.STATUS_CODE_PROCESSING_ERROR);
		statusDetail = new StdMutableStatusDetail();
		mad = new StdMutableMissingAttributeDetail();
		mad.setAttributeId(new IdentifierImpl("mad"));
		mad.setCategory(XACML3.ID_ACTION);
		mad.setDataTypeId(DataTypes.DT_STRING.getId());	
		mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "meh"));
		mad.addAttributeValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "nu?"));
		statusDetail.addMissingAttributeDetail(mad);
		status.setStatusDetail(statusDetail);
		result.setStatus(status);
		result.setDecision(Decision.INDETERMINATE);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		
		// Status with nested child StatusCodes (child status containing child status containing...)
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		StdStatusCode child1StatusCode = new StdStatusCode(new IdentifierImpl("child1StatusCode"));
		StdStatusCode statusCode = new StdStatusCode(XACML3.ID_STATUS_OK, child1StatusCode);
		status = new StdMutableStatus(statusCode);
		status.setStatusMessage("I'm ok, you're ok");
		result.setStatus(status);
		result.setDecision(Decision.PERMIT);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"StatusCode\":{\"Value\":\"child1StatusCode\"},\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:ok\"},\"StatusMessage\":\"I'm ok, you're ok\"},\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		response = new StdMutableResponse();
		result = new StdMutableResult();
		status = new StdMutableStatus();
		StdStatusCode childChildChildStatusCode = new StdStatusCode(new IdentifierImpl("childChildChildStatusCode"));
		StdStatusCode childChildStatusCode = new StdStatusCode(new IdentifierImpl("childChildStatusCode"), childChildChildStatusCode);
		child1StatusCode = new StdStatusCode(new IdentifierImpl("child1StatusCode"), childChildStatusCode);
		statusCode = new StdStatusCode(XACML3.ID_STATUS_OK, child1StatusCode);
		status = new StdMutableStatus(statusCode);
		status.setStatusMessage("I'm ok, you're ok");
		result.setStatus(status);
		result.setDecision(Decision.PERMIT);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Status\":{\"StatusCode\":{\"StatusCode\":{\"StatusCode\":{\"StatusCode\":{\"Value\":\"childChildChildStatusCode\"},\"Value\":\"childChildStatusCode\"},\"Value\":\"child1StatusCode\"},\"Value\":\"urn:oasis:names:tc:xacml:1.0:status:ok\"},\"StatusMessage\":\"I'm ok, you're ok\"},\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}

	}


	
	@Test
	public void testObligations() {
		
		// create an XPathExpression for use later
		StringNamespaceContext snc = new StringNamespaceContext();
		try {
			snc.add("defaultURI");
			snc.add("md", "referenceForMD");
		} catch (Exception e) {
			fail("unable to create NamespaceContext e="+e);
		}
		XPathExpressionWrapper xpathExpressionWrapper = new XPathExpressionWrapper(snc, "//md:record");
		XPathExpressionWrapper xpathExpressionWrapper2 = new XPathExpressionWrapper(snc, "//md:hospital");
		
		StdMutableObligation obligation;

		// test Obligation single decision no attributes
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\"}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// obligation missing Id
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		
		//	AttributeAssignment	- with AttributeId, Value,  Category, DataType, Issuer
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart")));
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"obligation-issuer1\",\"Value\":\"Bart\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		//	AttributeAssignment	- with AttributeId, Value, no Category, DataType, Issuer
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				null, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart")));
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"obligation-issuer1\",\"Value\":\"Bart\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		//	AttributeAssignment	- Missing AttributeId
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				null, 
				"obligation-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart")));
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		//	AttributeAssignment	- Missing Value
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer1", 
				null));
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"obligation-issuer1\",\"Value\":\"\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// AttributeAssignment - missing DataType
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer1", 
				new StdAttributeValue<String>(null, "Bart")));
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"obligation-issuer1\",\"Value\":\"Bart\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// AttributeAssignment - missing issuer
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				null, 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart")));
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Value\":\"Bart\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// AttributeAssignment - Integer type
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				null, 
				new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(1111))));
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Value\":1111,\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// AttributeAssignment - XPathExpression type
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				null, 
				new StdAttributeValue<XPathExpressionWrapper>(DataTypes.DT_XPATHEXPRESSION.getId(), xpathExpressionWrapper, new IdentifierImpl("SimpleXPathCategory"))));
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Value\":{\"Namespaces\":[{\"Namespace\":\"referenceForMD\",\"Prefix\":\"md\"},{\"Namespace\":\"defaultURI\"}],\"XPathCategory\":\"SimpleXPathCategory\",\"XPath\":\"//md:record\"},\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		

		//
		// Technically arrays cannot occur in Obligations and Advice elements.  The XML spec boils down to the following definition:
		//		<Obligation (attributes of the obligation) >
		//			<AttributeAssignment (attributes of this assignment) >value</AttributeAssignment>
		//			<AttributeAssignment (attributes of this assignment) >value</AttributeAssignment>
		//			:
		//		</Obligation
		//	which means that there may be multiple AttributeAssignments but each one has only one value.
		//	This differs from the Attributes section in which each <Attribute> may have multiple <AttributeValue> elements.
		// For Obligations and Advice we can simulate an array by having multiple AttributeAssignment elements with the same Category, Id and Issuer.
		//

		
		//	AttributeAssignment	- Multiple values with same Category and Id (one way of doing array)
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart")));
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Lisa")));
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Maggie")));
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"obligation-issuer1\",\"Value\":\"Bart\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Issuer\":\"obligation-issuer1\",\"Value\":\"Lisa\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Issuer\":\"obligation-issuer1\",\"Value\":\"Maggie\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		//	AttributeAssignment	- Multiple Integer values with same Category and Id (one way of doing array)
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer1", 
				new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(1111))));
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer1", 
				new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(2222))));
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"obligation-issuer1", 
				new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(3333))));
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"obligation-issuer1\",\"Value\":1111,\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Issuer\":\"obligation-issuer1\",\"Value\":2222,\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Issuer\":\"obligation-issuer1\",\"Value\":3333,\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// Multiple XPathExpression values with same Category and Id (one way of doing array)
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		obligation = new StdMutableObligation();
		obligation.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				null, 
				new StdAttributeValue<XPathExpressionWrapper>(DataTypes.DT_XPATHEXPRESSION.getId(), xpathExpressionWrapper, new IdentifierImpl("SimpleXPathCategory"))));
		obligation.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				null, 
				new StdAttributeValue<XPathExpressionWrapper>(DataTypes.DT_XPATHEXPRESSION.getId(), xpathExpressionWrapper2, new IdentifierImpl("SimpleXPathCategory"))));
		result.addObligation(obligation);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Obligations\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Value\":{\"Namespaces\":[{\"Namespace\":\"referenceForMD\",\"Prefix\":\"md\"},{\"Namespace\":\"defaultURI\"}],\"XPathCategory\":\"SimpleXPathCategory\",\"XPath\":\"//md:record\"},\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Value\":{\"Namespaces\":[{\"Namespace\":\"referenceForMD\",\"Prefix\":\"md\"},{\"Namespace\":\"defaultURI\"}],\"XPathCategory\":\"SimpleXPathCategory\",\"XPath\":\"//md:hospital\"},\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}	
		
	}
	
	
	
	
	@Test
	public void testAdvice() {
		
		// create an XPathExpression for use later
		StringNamespaceContext snc = new StringNamespaceContext();
		try {
			snc.add("defaultURI");
			snc.add("md", "referenceForMD");
		} catch (Exception e) {
			fail("unable to create NamespaceContext e="+e);
		}
		XPathExpressionWrapper xpathExpressionWrapper = new XPathExpressionWrapper(snc, "//md:record");
		XPathExpressionWrapper xpathExpressionWrapper2 = new XPathExpressionWrapper(snc, "//md:hospital");
		
		StdMutableAdvice Advice;

		// test Advice single decision no attributes
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\"}]}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// Advice missing Id
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		
		
		//	AttributeAssignment	- with AttributeId, Value,  Category, DataType, Issuer
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"Advice-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart")));
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"Advice-issuer1\",\"Value\":\"Bart\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}]}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		//	AttributeAssignment	- with AttributeId, Value, no Category, DataType, Issuer
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				null, 
				XACML3.ID_SUBJECT, 
				"Advice-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart")));
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"Advice-issuer1\",\"Value\":\"Bart\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}]}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		//	AttributeAssignment	- Missing AttributeId
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				null, 
				"Advice-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart")));
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		//	AttributeAssignment	- Missing Value
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"Advice-issuer1", 
				null));
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"Advice-issuer1\",\"Value\":\"\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}]}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// AttributeAssignment - missing DataType
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"Advice-issuer1", 
				new StdAttributeValue<String>(null, "Bart")));
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"Advice-issuer1\",\"Value\":\"Bart\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}]}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// AttributeAssignment - missing issuer
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				null, 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart")));
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Value\":\"Bart\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}]}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// AttributeAssignment - Integer type
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				null, 
				new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(1111))));
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Value\":1111,\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}]}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// AttributeAssignment - XPathExpression type
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				null, 
				new StdAttributeValue<XPathExpressionWrapper>(DataTypes.DT_XPATHEXPRESSION.getId(), xpathExpressionWrapper, new IdentifierImpl("SimpleXPathCategory"))));
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Value\":{\"Namespaces\":[{\"Namespace\":\"referenceForMD\",\"Prefix\":\"md\"},{\"Namespace\":\"defaultURI\"}],\"XPathCategory\":\"SimpleXPathCategory\",\"XPath\":\"//md:record\"},\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}]}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		

		//
		// Technically arrays cannot occur in Obligations and Advice elements.  The XML spec boils down to the following definition:
		//		<Obligation (attributes of the obligation) >
		//			<AttributeAssignment (attributes of this assignment) >value</AttributeAssignment>
		//			<AttributeAssignment (attributes of this assignment) >value</AttributeAssignment>
		//			:
		//		</Obligation
		//	which means that there may be multiple AttributeAssignments but each one has only one value.
		//	This differs from the Attributes section in which each <Attribute> may have multiple <AttributeValue> elements.
		// For Obligations and Advice we can simulate an array by having multiple AttributeAssignment elements with the same Category, Id and Issuer.
		//
		
		//	AttributeAssignment	- Multiple values with same Category and Id (one way of doing array)
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"Advice-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart")));
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"Advice-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Lisa")));
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"Advice-issuer1", 
				new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Maggie")));
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"Advice-issuer1\",\"Value\":\"Bart\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Issuer\":\"Advice-issuer1\",\"Value\":\"Lisa\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Issuer\":\"Advice-issuer1\",\"Value\":\"Maggie\",\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}]}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		//	AttributeAssignment	- Multiple Integer values with same Category and Id (one way of doing array)
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"Advice-issuer1", 
				new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(1111))));
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"Advice-issuer1", 
				new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(2222))));
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				"Advice-issuer1", 
				new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(3333))));
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Issuer\":\"Advice-issuer1\",\"Value\":1111,\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Issuer\":\"Advice-issuer1\",\"Value\":2222,\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Issuer\":\"Advice-issuer1\",\"Value\":3333,\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}]}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// Multiple XPathExpression values with same Category and Id (one way of doing array)
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		Advice = new StdMutableAdvice();
		Advice.setId(XACML3.ID_ACTION_IMPLIED_ACTION);
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				null, 
				new StdAttributeValue<XPathExpressionWrapper>(DataTypes.DT_XPATHEXPRESSION.getId(), xpathExpressionWrapper, new IdentifierImpl("SimpleXPathCategory"))));
		Advice.addAttributeAssignment(new StdMutableAttributeAssignment(
				XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE, 
				XACML3.ID_SUBJECT, 
				null, 
				new StdAttributeValue<XPathExpressionWrapper>(DataTypes.DT_XPATHEXPRESSION.getId(), xpathExpressionWrapper2, new IdentifierImpl("SimpleXPathCategory"))));
		result.addAdvice(Advice);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"AssociatedAdvice\":[{\"Id\":\"urn:oasis:names:tc:xacml:1.0:action:implied-action\",\"AttributeAssignment\":[{\"Value\":{\"Namespaces\":[{\"Namespace\":\"referenceForMD\",\"Prefix\":\"md\"},{\"Namespace\":\"defaultURI\"}],\"XPathCategory\":\"SimpleXPathCategory\",\"XPath\":\"//md:record\"},\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"},{\"Value\":{\"Namespaces\":[{\"Namespace\":\"referenceForMD\",\"Prefix\":\"md\"},{\"Namespace\":\"defaultURI\"}],\"XPathCategory\":\"SimpleXPathCategory\",\"XPath\":\"//md:hospital\"},\"Category\":\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\",\"DataType\":\"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\",\"AttributeId\":\"urn:oasis:names:tc:xacml:1.0:subject\"}]}]}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
	}
	
	
	
	
	

	
	

	
	// Attributes tests
	@Test
	public void testAttributes() {
		
		// create an XPathExpression for use later
		StringNamespaceContext snc = new StringNamespaceContext();
		try {
			snc.add("defaultURI");
			snc.add("md", "referenceForMD");
		} catch (Exception e) {
			fail("unable to create NamespaceContext e="+e);
		}
		XPathExpressionWrapper xpathExpressionWrapper = new XPathExpressionWrapper(snc, "//md:record");
		
		
		Identifier categoryIdentifier;
		List<Attribute> attrList = new ArrayList<Attribute>();
		StdMutableAttribute mutableAttribute;
		
		// Attr list with no entries
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// one Attribute
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
				attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), "AIssue", true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":\"Apu\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// multiple attributes
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent2"), new StdAttributeValue<String>(DataTypes.DT_YEARMONTHDURATION.getId(), "P10Y4M"), "BIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent3"), new StdAttributeValue<Double>(DataTypes.DT_DOUBLE.getId(), 765.432), "CIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent4"), new StdAttributeValue<Boolean>(DataTypes.DT_BOOLEAN.getId(), true), "DIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent5"), new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)), "EIssue", true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":\"Apu\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"BIssue\",\"Value\":\"P10Y4M\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#yearMonthDuration\",\"AttributeId\":\"attrIdent2\"},{\"Issuer\":\"CIssue\",\"Value\":765.432,\"DataType\":\"http://www.w3.org/2001/XMLSchema#double\",\"AttributeId\":\"attrIdent3\"},{\"Issuer\":\"DIssue\",\"Value\":true,\"DataType\":\"http://www.w3.org/2001/XMLSchema#boolean\",\"AttributeId\":\"attrIdent4\"},{\"Issuer\":\"EIssue\",\"Value\":4567,\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"attrIdent5\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// IncludeInResult=false/true
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
				attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), "AIssue", false));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// Missing AttributeId (mandatory)
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
				attrList.add(new StdAttribute(categoryIdentifier, null, new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), "AIssue", true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Missing mandatory Value
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
				attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), null), "AIssue", true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// Missing optional Issuer
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
				attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), null, true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Value\":\"Apu\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// missing optional DataType
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
				attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(null, "Apu"), "AIssue", true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":\"Apu\",\"AttributeId\":\"attrIdent1\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// same id, same type different issuer
		// (This is not an array of values because issuer is different)
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart"), "BIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Simpson"), "CIssue", true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":\"Apu\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"BIssue\",\"Value\":\"Bart\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"CIssue\",\"Value\":\"Simpson\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// same id, same type different issuer
		// (This is effectively an array of values, but we return them as separate values to the client)
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart"), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Simpson"), "AIssue", true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":\"Apu\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"AIssue\",\"Value\":\"Bart\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"AIssue\",\"Value\":\"Simpson\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// same Id, different types, same issuer
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_YEARMONTHDURATION.getId(), "P10Y4M"), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<Double>(DataTypes.DT_DOUBLE.getId(), 765.432), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<Boolean>(DataTypes.DT_BOOLEAN.getId(), true), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)), "AIssue", true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":\"Apu\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"AIssue\",\"Value\":\"P10Y4M\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#yearMonthDuration\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"AIssue\",\"Value\":765.432,\"DataType\":\"http://www.w3.org/2001/XMLSchema#double\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"AIssue\",\"Value\":true,\"DataType\":\"http://www.w3.org/2001/XMLSchema#boolean\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"AIssue\",\"Value\":4567,\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"AIssue\",\"Value\":4567,\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"attrIdent1\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		// same Id, different types, different issuer
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_YEARMONTHDURATION.getId(), "P10Y4M"), "BIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<Double>(DataTypes.DT_DOUBLE.getId(), 765.432), "CIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<Boolean>(DataTypes.DT_BOOLEAN.getId(), true), "DIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)), "EIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)), null, true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":\"Apu\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"BIssue\",\"Value\":\"P10Y4M\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#yearMonthDuration\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"CIssue\",\"Value\":765.432,\"DataType\":\"http://www.w3.org/2001/XMLSchema#double\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"DIssue\",\"Value\":true,\"DataType\":\"http://www.w3.org/2001/XMLSchema#boolean\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"EIssue\",\"Value\":4567,\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"attrIdent1\"},{\"Value\":4567,\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"attrIdent1\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}

		// different Id, different types, same issuer
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent2"), new StdAttributeValue<String>(DataTypes.DT_YEARMONTHDURATION.getId(), "AIssue"), "BIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent3"), new StdAttributeValue<Double>(DataTypes.DT_DOUBLE.getId(), 765.432), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent4"), new StdAttributeValue<Boolean>(DataTypes.DT_BOOLEAN.getId(), true), "AIssue", true));
			attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent5"), new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)), "AIssue", true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":\"Apu\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"BIssue\",\"Value\":\"AIssue\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#yearMonthDuration\",\"AttributeId\":\"attrIdent2\"},{\"Issuer\":\"AIssue\",\"Value\":765.432,\"DataType\":\"http://www.w3.org/2001/XMLSchema#double\",\"AttributeId\":\"attrIdent3\"},{\"Issuer\":\"AIssue\",\"Value\":true,\"DataType\":\"http://www.w3.org/2001/XMLSchema#boolean\",\"AttributeId\":\"attrIdent4\"},{\"Issuer\":\"AIssue\",\"Value\":4567,\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"attrIdent5\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// one Attribute of type XPathExpression (the only complex data type)
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
				attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<XPathExpressionWrapper>(DataTypes.DT_XPATHEXPRESSION.getId(), xpathExpressionWrapper, new IdentifierImpl("xpathCategory")), "AIssue", true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":{\"Namespaces\":[{\"Namespace\":\"referenceForMD\",\"Prefix\":\"md\"},{\"Namespace\":\"defaultURI\"}],\"XPathCategory\":\"xpathCategory\",\"XPath\":\"//md:record\"},\"DataType\":\"urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression\",\"AttributeId\":\"attrIdent1\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// multiple sets of values
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		categoryIdentifier = new IdentifierImpl("firstCategory");
		attrList.clear();
		attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"), "AIssue", true));
		attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent2"), new StdAttributeValue<String>(DataTypes.DT_YEARMONTHDURATION.getId(), "P10Y4M"), "BIssue", false));
		attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent3"), new StdAttributeValue<Double>(DataTypes.DT_DOUBLE.getId(), 765.432), "CIssue", true));
		attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent4"), new StdAttributeValue<Boolean>(DataTypes.DT_BOOLEAN.getId(), true), "DIssue", true));
		attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent5"), new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)), "EIssue", true));
		attrList.add(new StdAttribute(categoryIdentifier, new IdentifierImpl("attrNoIssuer"), new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)), null, true));
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		categoryIdentifier = new IdentifierImpl("secondCategory");
		Attribute[] secondAttrList = {
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent12"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu2"), "AIssue2", true),
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent22"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Abc2"), "BIssue2", false),
				new StdAttribute(categoryIdentifier, new IdentifierImpl("attrIdent32"), new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Der2"), "CIssue2", true) };
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, Arrays.asList(secondAttrList)));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":\"Apu\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"},{\"Issuer\":\"CIssue\",\"Value\":765.432,\"DataType\":\"http://www.w3.org/2001/XMLSchema#double\",\"AttributeId\":\"attrIdent3\"},{\"Issuer\":\"DIssue\",\"Value\":true,\"DataType\":\"http://www.w3.org/2001/XMLSchema#boolean\",\"AttributeId\":\"attrIdent4\"},{\"Issuer\":\"EIssue\",\"Value\":4567,\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"attrIdent5\"},{\"Value\":4567,\"DataType\":\"http://www.w3.org/2001/XMLSchema#integer\",\"AttributeId\":\"attrNoIssuer\"}]},{\"CategoryId\":\"secondCategory\",\"Attribute\":[{\"Issuer\":\"AIssue2\",\"Value\":\"Apu2\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent12\"},{\"Issuer\":\"CIssue2\",\"Value\":\"Der2\",\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent32\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// array of values - same type
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		attrList.clear();
		categoryIdentifier = new IdentifierImpl("firstCategory");
		mutableAttribute = new StdMutableAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), (Collection<AttributeValue<?>>)null, "AIssue", true);

			mutableAttribute.addValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"));
			mutableAttribute.addValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Bart"));
			mutableAttribute.addValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Homer"));
			mutableAttribute.addValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Ned"));
			
		attrList.add(mutableAttribute);
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":[\"Apu\",\"Bart\",\"Homer\",\"Ned\"],\"DataType\":\"http://www.w3.org/2001/XMLSchema#string\",\"AttributeId\":\"attrIdent1\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// array of values - compatible different types
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		attrList.clear();
		categoryIdentifier = new IdentifierImpl("firstCategory");
		mutableAttribute = new StdMutableAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), (Collection<AttributeValue<?>>)null, "AIssue", true);

			mutableAttribute.addValue(new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)));
			mutableAttribute.addValue(new StdAttributeValue<Double>(DataTypes.DT_DOUBLE.getId(), 765.432));
			mutableAttribute.addValue(new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)));
		attrList.add(mutableAttribute);
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Category\":[{\"CategoryId\":\"firstCategory\",\"Attribute\":[{\"Issuer\":\"AIssue\",\"Value\":[4567,765.432,4567],\"DataType\":\"http://www.w3.org/2001/XMLSchema#double\",\"AttributeId\":\"attrIdent1\"}]}],\"Decision\":\"Permit\"}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// array of values - incompatible different types
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		attrList.clear();
		categoryIdentifier = new IdentifierImpl("firstCategory");
		mutableAttribute = new StdMutableAttribute(categoryIdentifier, new IdentifierImpl("attrIdent1"), (Collection<AttributeValue<?>>)null, "AIssue", true);

			mutableAttribute.addValue(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), "Apu"));
			mutableAttribute.addValue(new StdAttributeValue<String>(DataTypes.DT_YEARMONTHDURATION.getId(), "P10Y4M"));
			mutableAttribute.addValue(new StdAttributeValue<Double>(DataTypes.DT_DOUBLE.getId(), 765.432));
			mutableAttribute.addValue(new StdAttributeValue<Boolean>(DataTypes.DT_BOOLEAN.getId(), true));
			mutableAttribute.addValue(new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)));
			mutableAttribute.addValue(new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), BigInteger.valueOf(4567)));
		attrList.add(mutableAttribute);
		result.addAttributeCategory(new StdAttributeCategory(categoryIdentifier, attrList));
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
	}
	
	
	
	
	
	// PolicyIdentifier tests
	@Test
	public void testPolicyIdentifier() {
		
		StdIdReference policyIdentifier1 = null;
		StdIdReference policyIdentifier2 = null;
		StdIdReference policySetIdentifier1 = null;
		StdIdReference policySetIdentifier2 = null;
		
		// multiple PolicyIdentifiers of both types
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		try {
			policyIdentifier1 = new StdIdReference(new IdentifierImpl("idRef1"), StdVersion.newInstance("1.2.3"));
			policyIdentifier2 = new StdIdReference(new IdentifierImpl("idRef2_NoVersion"));
			policySetIdentifier1 = new StdIdReference(new IdentifierImpl("idSetRef1"), StdVersion.newInstance("4.5.6.7.8.9.0"));
			policySetIdentifier2 = new StdIdReference(new IdentifierImpl("idSetRef2_NoVersion"));
		} catch (ParseException e1) {
			fail("creating policyIds, e="+e1);
		}
		result.addPolicyIdentifier(policyIdentifier1);
		result.addPolicyIdentifier(policyIdentifier2);
		result.addPolicySetIdentifier(policySetIdentifier1);
		result.addPolicySetIdentifier(policySetIdentifier2);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"PolicyIdentifier\":{\"PolicyIdReference\":[{\"Id\":\"idRef1\",\"Version\":\"1.2.3\"},{\"Id\":\"idRef2_NoVersion\"}],\"PolicySetIdReference\":[{\"Id\":\"idSetRef1\",\"Version\":\"4.5.6.7.8.9.0\"},{\"Id\":\"idSetRef2_NoVersion\"}]}}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// PolicyIdentifier exists but has no IdReferences
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		policyIdentifier1 = null;
		result.addPolicyIdentifier(policyIdentifier1);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// PolicySetIdentifier exists but has not IdReferences
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		policySetIdentifier1 = null;
		result.addPolicyIdentifier(policySetIdentifier1);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			fail("Operation should throw exception");
		} catch (JSONStructureException e) {
			// correct response
		} catch (Exception e) {
			fail ("Failed convert from JSON to object: " + e);
		}
		
		// PolicyIdentifier with PolicyIdReference and no PolicySetIdReference
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		try {
			policyIdentifier1 = new StdIdReference(new IdentifierImpl("idRef1"), StdVersion.newInstance("1.2.3"));
		} catch (ParseException e1) {
			fail("creating policyIds, e="+e1);
		}
		result.addPolicyIdentifier(policyIdentifier1);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"PolicyIdentifier\":{\"PolicyIdReference\":[{\"Id\":\"idRef1\",\"Version\":\"1.2.3\"}]}}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		
		// PolicyIdentifier with no PolicyIdReference and with PolicySetIdReference
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);
		try {
			policySetIdentifier1 = new StdIdReference(new IdentifierImpl("idSetRef1"), StdVersion.newInstance("4.5.6.7.8.9.0"));
		} catch (ParseException e1) {
			fail("creating policyIds, e="+e1);
		}
		result.addPolicySetIdentifier(policySetIdentifier1);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"PolicyIdentifier\":{\"PolicySetIdReference\":[{\"Id\":\"idSetRef1\",\"Version\":\"4.5.6.7.8.9.0\"}]}}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
		
		
		// IdReferences without version
		response = new StdMutableResponse();
		result = new StdMutableResult();
		result.setDecision(Decision.PERMIT);

			policyIdentifier1 = new StdIdReference(new IdentifierImpl("idRef1"), null);
			policyIdentifier2 = new StdIdReference(new IdentifierImpl("idRef2_NoVersion"));
			policySetIdentifier1 = new StdIdReference(new IdentifierImpl("idSetRef1"));
			policySetIdentifier2 = new StdIdReference(new IdentifierImpl("idSetRef2_NoVersion"));

		result.addPolicyIdentifier(policyIdentifier1);
		result.addPolicyIdentifier(policyIdentifier2);
		result.addPolicySetIdentifier(policySetIdentifier1);
		result.addPolicySetIdentifier(policySetIdentifier2);
		response.add(result);
		try {
			jsonResponse = JSONResponse.toString(response, false);
			assertEquals("{\"Response\":[{\"Decision\":\"Permit\",\"PolicyIdentifier\":{\"PolicyIdReference\":[{\"Id\":\"idRef1\"},{\"Id\":\"idRef2_NoVersion\"}],\"PolicySetIdReference\":[{\"Id\":\"idSetRef1\"},{\"Id\":\"idSetRef2_NoVersion\"}]}}]}", jsonResponse);
		} catch (Exception e) {
			fail("operation failed, e="+e);
		}
	}


//TODO - the JSON and XML spec imply that the Result Attributes may include the Content (It is part of the UML)
	
	
	// test indentation???
	
	// order does not matter??
	
}
















