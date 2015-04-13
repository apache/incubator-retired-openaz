package org.openliberty.openaz.pepapi.std.test.mapper;

import com.att.research.xacml.api.XACML3;
import org.openliberty.openaz.pepapi.*;

public class BusinessRequestContextMapper implements ObjectMapper {

	private MapperRegistry mapperRegistry;

	private PepConfig pepConfig;

	@Override
	public Class<?> getMappedClass() {
		return BusinessRequestContext.class;
	}

	@Override
	public void map(Object o, PepRequest pepRequest) {
		BusinessRequestContext bc = (BusinessRequestContext)o;
		PepRequestAttributes envAttributes = pepRequest.getPepRequestAttributes(XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT);
		envAttributes.addAttribute("jpmc:request-context:country", bc.getRequestCountry());
		envAttributes.addAttribute("jpmc:request-context:time", bc.getRequestTime());
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
