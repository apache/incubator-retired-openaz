package org.openliberty.openaz.pepapi.std.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openliberty.openaz.pepapi.*;
import org.openliberty.openaz.pepapi.std.StdPepAgentFactory;

import java.util.ArrayList;
import java.util.List;


public class TestAPI {

	private PepAgentFactory pepAgentFactory;

	@Before
	public void setup() {
		pepAgentFactory = new StdPepAgentFactory("/properties/testapi.xacml.properties");
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
	public void testPermit(){
		PepResponse response = getPepAgent().simpleDecide("Julius Hibbert",
				"read", "http://medico.com/record/patient/BartSimpson");
		Assert.assertNotNull(response);
		Assert.assertEquals(true, response.allowed());
	}

	
	/**
	 * 
	 */
	@Test
	public void testNotApplicable(){
		PepResponse response = getPepAgent().simpleDecide("Julius Hibbert",
				"read","http://medico.com/record/patient/JohnSmith");
		Assert.assertNotNull(response);
		Assert.assertEquals(false, response.allowed());
	}

	
	/**
	 * 
	 */
	@Test
	public void testMultiRequest() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(Action.newInstance("read"));
		actions.add(Action.newInstance("write"));
		actions.add(Action.newInstance("update"));
		actions.add(Action.newInstance("delete"));

		List<PepResponse> responses = getPepAgent().bulkDecide(actions,
				Subject.newInstance("Julius Hibbert"),
				Resource.newInstance("http://medico.com/record/patient/BartSimpson"));
		Assert.assertNotNull(responses);
		Assert.assertEquals(true, responses.get(0).allowed());
		Assert.assertEquals(true, responses.get(1).allowed());
		Assert.assertEquals(false, responses.get(2).allowed());
		Assert.assertEquals(false, responses.get(3).allowed());

	}

	public PepAgent getPepAgent() {
		return pepAgentFactory.getPepAgent();
	}
}
