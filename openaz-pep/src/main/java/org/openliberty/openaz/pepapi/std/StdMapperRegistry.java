package org.openliberty.openaz.pepapi.std;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class StdMapperRegistry implements MapperRegistry {

	private static final Log logger = LogFactory.getLog(StdMapperRegistry.class);
	
	private final Map<Class<?>, ObjectMapper> map;

	private PepConfig pepConfig;

	private StdMapperRegistry(PepConfig pepConfig) {
		//Register defaults.
		this.pepConfig = pepConfig;
		map = new HashMap<Class<?>, ObjectMapper>();
		registerMapper(new CollectionMapper());
		registerMapper(new ArrayMapper());
		registerMapper(new SubjectMapper());
		registerMapper(new ActionMapper());
		registerMapper(new ResourceMapper());
		registerMapper(new CategoryContainerMapper(Environment.class));
		registerMapper(new CategoryContainerMapper(CategoryContainer.class));
		registerMapper(new ActionResourcePairMapper());
	}

	public static MapperRegistry newInstance(PepConfig pepConfig) {
		return new StdMapperRegistry(pepConfig);
	}
	
	public static MapperRegistry newInstance(PepConfig pepConfig, List<ObjectMapper> mappers) {
		MapperRegistry mapperRegistry = newInstance(pepConfig);
		if(mappers != null) {
			mapperRegistry.registerMappers(mappers);
		}
		return mapperRegistry;
	}
	
	@Override
	public void registerMapper(ObjectMapper mapper) {
		mapper.setPepConfig(pepConfig);
		mapper.setMapperRegistry(this);
		map.put(mapper.getMappedClass(), mapper);
	}

	@Override
	public void registerMappers(Iterable<? extends ObjectMapper> mappers) {
		for(ObjectMapper mapper: mappers) {
			registerMapper(mapper);
		}
	}

	@Override
	public ObjectMapper getMapper(Class<?> clazz) {
		ObjectMapper mapper = null;
		Class<?> c = clazz;
		while(mapper == null && !c.equals(Object.class)) {
			mapper = getClassMapper(c);
			c = c.getSuperclass();
		}
		
		//Handle Arrays.
    	if(mapper == null) {
    		if(clazz.isArray()) {
    			mapper = getMapper(Object[].class);
    		}
    	}
 	
    	if(mapper != null) {
			logger.debug("Mapper :" + mapper.getClass().getName() + " found for class: " + clazz);
    		return mapper;
    	}else {
    		throw new PepException("No ObjectMapper found for Object of Class: " + clazz);
    	}
	}
	
	private ObjectMapper getClassMapper(Class<?> clazz) {
		ObjectMapper mapper = map.get(clazz);
		if(mapper == null) {
			Class<?>[] interfaces = clazz.getInterfaces();
			if(interfaces != null && interfaces.length > 0) {
				for(Class<?> inf: interfaces) {
					mapper = map.get(inf);
					if(mapper != null) {
						break;
					}
				}
			}
		}
		return mapper;
	}
}
