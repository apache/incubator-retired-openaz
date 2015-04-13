package org.openliberty.openaz.pepapi.std;

import com.att.research.xacml.api.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.ObligationRouter;
import org.openliberty.openaz.pepapi.PepConfig;
import org.openliberty.openaz.pepapi.PepResponse;
import org.openliberty.openaz.pepapi.PepResponseFactory;


final class StdPepResponseFactory implements PepResponseFactory {
    
	private static final Log logger = LogFactory.getLog(StdPepResponseFactory.class);

	private PepConfig pepConfig;
	
	private ObligationRouter obligationRouter;

	StdPepResponseFactory(PepConfig pepConfig, ObligationRouter obligationRouter) {
		this.pepConfig = pepConfig;
		this.obligationRouter = obligationRouter;
	}
	
	@Override
	public PepResponse newPepResponse(Result result) {
		return StdPepResponse.newInstance(pepConfig, obligationRouter, result);
	}
}
