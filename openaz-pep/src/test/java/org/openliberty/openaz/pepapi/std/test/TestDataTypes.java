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
