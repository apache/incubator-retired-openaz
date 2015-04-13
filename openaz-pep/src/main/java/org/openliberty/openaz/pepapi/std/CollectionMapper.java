package org.openliberty.openaz.pepapi.std;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.*;

import java.util.Collection;


public final class CollectionMapper implements ObjectMapper {
	
	private static final Log logger = LogFactory.getLog(CollectionMapper.class);

	private MapperRegistry mapperRegistry;

	private PepConfig pepConfig;
	
	@Override
	public Class<?> getMappedClass() {
		return Collection.class;
	}

	@Override
	public void map(Object o, PepRequest pepRequest) {
		Collection<?> collection = (Collection<?>)o;
		for(Object item: collection) {
			ObjectMapper mapper = mapperRegistry.getMapper(item.getClass());
			if(mapper != null) {
				mapper.map(item, pepRequest);
			}else {
				logger.error("Can't map an Object of class: " + item.getClass().getName());
				throw new PepException("Can't map an Object of class: " + item.getClass().getName());
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
