package org.openliberty.openaz.pepapi.std;


import org.openliberty.openaz.pepapi.*;

public final class ActionResourcePairMapper implements ObjectMapper {

	private MapperRegistry mapperRegistry;

	private PepConfig pepConfig;
	
	@Override
	public Class<?> getMappedClass() {
		return ActionResourcePair.class;
	}

	@Override
	public void map(Object o, PepRequest pepRequest) {
		ActionResourcePair actionResource = (ActionResourcePair)o;
		Object action = actionResource.getAction();
		Object resource = actionResource.getResource();
		mapperRegistry.getMapper(action.getClass()).map(action, pepRequest);
		mapperRegistry.getMapper(resource.getClass()).map(resource, pepRequest);
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
