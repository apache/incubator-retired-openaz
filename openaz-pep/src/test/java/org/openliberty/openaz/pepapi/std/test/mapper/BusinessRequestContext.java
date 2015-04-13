package org.openliberty.openaz.pepapi.std.test.mapper;

public class BusinessRequestContext {
	
	private final String requestCountry;
	
	private final String requestTime;

	public BusinessRequestContext(String country, String time){
		this.requestCountry = country;
		this.requestTime = time;
	}
	
	public String getRequestCountry() {
		return requestCountry;
	}

	public String getRequestTime() {
		return requestTime;
	}
}
