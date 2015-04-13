package org.openliberty.openaz.pepapi.std.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openliberty.openaz.pepapi.*;
import org.openliberty.openaz.pepapi.std.StdPepAgentFactory;

import java.util.ArrayList;
import java.util.List;


public class TestAPIWithPIP {
	
	private static final Log log = LogFactory.getLog(TestAPIWithPIP.class);

	private PepAgentFactory pepAgentFactory;

	@Before
	public void setup() {
		pepAgentFactory = new StdPepAgentFactory("xacml.properties");
	}
	

	/**
	 * 
	 */
	@Ignore
	@Test
	public void testPepAgent() {
		Assert.assertNotNull(getPepAgent());
	}

	
	/**
	 * 
	 */
	@Ignore
	@Test
	public void testPermit() {
		PepResponse response = getPepAgent()
				.simpleDecide("John Doe",
						"read",
						"http://medico.com/record/patient/BartSimpson");
		Assert.assertNotNull(response);
		Assert.assertEquals(true, response.allowed());
	}

	
	/**
	 * 
	 */
	@Ignore
	@Test
	public void testNotApplicable() {
		PepResponse response = getPepAgent()
				.simpleDecide("John Smith",
						"read",
						"http://medico.com/record/patient/BartSimpson");
		Assert.assertNotNull(response);
		Assert.assertEquals(false, response.allowed());
	}

	
	/**
	 * 
	 */
	@Ignore
	@Test
	public void testMultiRequest() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(Action.newInstance("read"));
		actions.add(Action.newInstance("update"));
		actions.add(Action.newInstance("write"));
		actions.add(Action.newInstance("modify"));

		Resource resource = Resource.newInstance("http://medico.com/record/patient/BartSimpson");
		Subject subject = Subject.newInstance("John Doe");

		List<PepResponse> responses = getPepAgent().bulkDecide(actions, resource, subject);
		Assert.assertNotNull(responses);
		Assert.assertEquals(actions.size(), responses.size());
		Assert.assertEquals(true, responses.get(0).allowed());
		Assert.assertEquals(false, responses.get(1).allowed());
		Assert.assertEquals(true, responses.get(2).allowed());
		Assert.assertEquals(false, responses.get(3).allowed());
		for(PepResponse response: responses) {
			log.debug(response.getAssociation());
		}
	}
	
	public PepAgent getPepAgent() {
		return pepAgentFactory.getPepAgent();
	}
}
