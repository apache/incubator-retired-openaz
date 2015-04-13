package org.openliberty.openaz.pepapi.std;

import com.att.research.xacml.api.pdp.PDPEngineFactory;
import com.att.research.xacml.util.FactoryException;
import org.openliberty.openaz.pepapi.*;

import java.util.List;
import java.util.Properties;


public class StdPepAgentFactory implements PepAgentFactory {
	
	private volatile PepAgent pepAgent;

	private PDPEngineFactory pdpEngineFactory;

	private Properties xacmlProperties;

	private PepConfig pepConfig;

	private List<ObligationStoreAware> obligationHandlers;

	public StdPepAgentFactory(String propertyFile) {
		this(PepUtils.loadProperties(propertyFile));
	}

	public StdPepAgentFactory(Properties properties) {
		this.xacmlProperties = properties;
		this.pepConfig = new StdPepConfig(properties);
		try {
			//FIXME: Error when invoking newInstance() with properties.
			pdpEngineFactory = PDPEngineFactory.newInstance();
		} catch (FactoryException e) {
			throw new PepException(e);
		}
	}

	@Override
	public PepAgent getPepAgent() {
		if(pepAgent == null) {
			synchronized(this) {
				if(this.pepAgent == null) {
					StdPepAgent pa = new StdPepAgent();
					pa.setPepConfig(pepConfig);
					pa.setXacmlProperties(xacmlProperties);
					pa.setPdpEngineFactory(pdpEngineFactory);
					pa.setObligationHandlers(obligationHandlers);
					pa.initialize();
					pepAgent = pa;
				}
			}
		}
		return pepAgent;
	}

	public void setObligationHandlers(List<ObligationStoreAware> obligationHandlers) {
		this.obligationHandlers = obligationHandlers;
	}
}
