package org.openliberty.openaz.pepapi.std.test.mapper;

public class Client {
	
	private final String name;
	
	private final String countryOfDomicile;
	
	public Client(String name, String countryOfDomicile){
		this.name = name;
		this.countryOfDomicile = countryOfDomicile;
	}

	public String getName() {
		return name;
	}
	public String getCountryOfDomicile() {
		return countryOfDomicile;
	}
}
