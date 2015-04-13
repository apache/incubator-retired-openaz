package org.openliberty.openaz.pepapi.std;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.*;


public final class ArrayMapper implements ObjectMapper {
	
	private static final Log logger = LogFactory.getLog(ArrayMapper.class);

	private PepConfig pepConfig;

	private MapperRegistry mapperRegistry;

	@Override
	public Class<Object[]> getMappedClass() {
		return Object[].class;
	}

	@Override
	public void map(Object o, PepRequest pepRequest) {
		Object[] array = (Object[])o;
		if(array != null && array.length > 0) {
			ObjectMapper mapper = mapperRegistry.getMapper(array[0].getClass());
			if(mapper != null) {
				for(Object item: array) {
					mapper.map(item, pepRequest);
				}
			}else {
				logger.error("Can't map an Object of class: " + array[0].getClass().getName());
				throw new PepException("Can't map an Object of class: " + array[0].getClass().getName());
			}
		}
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
