package org.openliberty.openaz.pepapi.std;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.MapperRegistry;
import org.openliberty.openaz.pepapi.PepConfig;
import org.openliberty.openaz.pepapi.PepRequest;
import org.openliberty.openaz.pepapi.PepRequestFactory;

import java.util.List;


final class StdPepRequestFactory implements PepRequestFactory {
	
	private static final Log logger = LogFactory.getLog(StdPepRequestFactory.class);

	private final PepConfig pepConfig;

	private final MapperRegistry mapperRegistry;

	/**
	 *
	 * @param pepConfig
	 */
	StdPepRequestFactory(PepConfig pepConfig, MapperRegistry mapperRegistry) {
		this.pepConfig = pepConfig;
		this.mapperRegistry = mapperRegistry;
	}

	@Override
	public PepRequest newPepRequest(Object[] objects) {
		return StdPepRequest.newInstance(pepConfig, mapperRegistry, objects);
	}

	@Override
	public PepRequest newBulkPepRequest(List<?> associations, Object[] objects) {
		return MultiRequest.newInstance(pepConfig, mapperRegistry, associations, objects);
	}

}
