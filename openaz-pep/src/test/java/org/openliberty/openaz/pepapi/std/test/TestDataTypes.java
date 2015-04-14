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

package org.openliberty.openaz.pepapi.std.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openliberty.openaz.pepapi.*;
import org.openliberty.openaz.pepapi.std.StdPepAgentFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class TestDataTypes {

	private PepAgentFactory pepAgentFactory;

	@Before
	public void setup() {
		/*System.setProperty("xacml.properties" ,
				getClass().getClassLoader().getResource("properties/testdatatypes.xacml.properties").getPath());*/
		pepAgentFactory = new StdPepAgentFactory("/properties/testdatatypes.xacml.properties");
	}
	
	
	/**
	 * 
	 */
	@Test
	public void testPepAgent() {
		Assert.assertNotNull(getPepAgent());
	}

	
	/**
	 * 
	 */
	@Test
	public void testPermitWithURIResource() {
		Subject subject = Subject.newInstance("John Smith");
		Action action = Action.newInstance("view");
		Resource resource = Resource.newInstance(URI.create("file://repository/classified/abc"));
		PepResponse response = getPepAgent().decide(subject, action, resource);
		Assert.assertNotNull(response);
		Assert.assertEquals(true, response.allowed());
	}
	

	/**
	 * 
	 */
	@Test
	public void testPermitWithIntegerResource() {
		Subject subject = Subject.newInstance("John Smith");
		Action action = Action.newInstance("view");
		Resource resource = Resource.newInstance(101L);
		PepResponse response = getPepAgent().decide(subject, action, resource);
		Assert.assertNotNull(response);
		Assert.assertEquals(true, response.allowed());
	}

	
	/**
	 * 
	 */
	@Test
	public void testMultiRequestWithURI() {
		List<Resource> resources = new ArrayList<Resource>();
		resources.add(Resource.newInstance(URI.create("file://repository/classified/abc")));
		resources.add(Resource.newInstance(URI.create("file://repository/classified/xyz")));

		Subject subject = Subject.newInstance("John Smith");
		Action action = Action.newInstance("view");

		List<PepResponse> responses = getPepAgent().bulkDecide(resources, action, subject);
		Assert.assertNotNull(responses);
		for(PepResponse response: responses) {
			Assert.assertEquals(true, response.allowed());
		}
	}

	public PepAgent getPepAgent() {
		return pepAgentFactory.getPepAgent();
	}
}
