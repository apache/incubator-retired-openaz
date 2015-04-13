package org.openliberty.openaz.pepapi.std.test.mapper;

import com.att.research.xacml.api.XACML3;
import org.openliberty.openaz.pepapi.*;

public class ClientMapper implements ObjectMapper {

	private MapperRegistry mapperRegistry;

	private PepConfig pepConfig;
	
	@Override
	public Class<?> getMappedClass() {
		return Client.class;
	}

	@Override
	public void map(Object o, PepRequest pepRequest) {
		Client c = (Client)o;
		PepRequestAttributes resAttributes = pepRequest.getPepRequestAttributes(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE);
		resAttributes.addAttribute("jpmc:client:name", c.getName());
		resAttributes.addAttribute("jpmc:client:country-of-domicile", c.getCountryOfDomicile());
	}

	@Override
	public void setMapperRegistry(MapperRegistry mapperRegistry) {
		this.mapperRegistry = mapperRegistry;
	}

	@Override
	public void setPepConfig(PepConfig pepConfig) {
		this.pepConfig = pepConfig;
	}
}
