package org.openliberty.openaz.pepapi.std.test.util;

import org.openliberty.openaz.pepapi.PepAgent;
import org.openliberty.openaz.pepapi.PepResponse;

import java.util.concurrent.Callable;


public class AzInvoker implements Callable<String> {
	
	private final PepAgent pepAgent;
	
	private final Object subject;
	
	private final Object action;
	
	private final Object resource;
	
	private final long sleepDuration;
	
	private final HasResult handler;
	
	public AzInvoker(PepAgent pepAgent, Object subject, Object action,
			Object resource, HasResult handler, long sleepDuration) {
		this.pepAgent = pepAgent;
		this.subject = subject;
		this.action = action;
		this.resource = resource;
		this.handler = handler;
		this.sleepDuration = sleepDuration;
	}
	
	private String invoke()throws InterruptedException{
		PepResponse response = pepAgent.decide(subject, action, resource);
		if(response != null){
			response.allowed();
		}
		Thread.sleep(this.sleepDuration);
		return handler.getResult();
	}
	
	public String call() throws Exception {
		return invoke();
	}

	public long getSleepDuration() {
		return sleepDuration;
	}

	public HasResult getPep() {
		return handler;
	}
}
