/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.std.pep;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPEngineFactory;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.api.pep.PEPEngine;
import com.att.research.xacml.api.pep.PEPException;
import com.att.research.xacml.util.FactoryException;

/**
 * StdEngine implements the {@link com.att.research.xacml.api.pep.PEPEngine} interface by creating
 * an instance of the {@link com.att.research.xacml.api.pdp.PDPEngine} interface using the {@link com.att.research.xacml.api.pdp.PDPEngineFactory} and
 * passing requests through to that engine, forwarding the {@link com.att.research.xacml.api.Response} object back to the caller.
 * 
 * @author car
 * @version $Revision: 1.2 $
 */
public class StdEngine implements PEPEngine {
	private Log	logger	= LogFactory.getLog(this.getClass());
	
	protected Properties properties = null;
	
	public StdEngine() {
	}

	public StdEngine(Properties properties) {
		this.properties = properties;
	}

	@Override
	public Response decide(Request pepRequest) throws PEPException {
		/*
		 * Get the PDP engine factory
		 */
		PDPEngineFactory pdpEngineFactory	= null;
		try {
			pdpEngineFactory	= PDPEngineFactory.newInstance();
		} catch (FactoryException ex) {
			this.logger.error("FactoryException creating the PDPEngineFactory", ex);
			throw new PEPException("FactoryException creating the PDPEngineFactory", ex);
		}
		assert(pdpEngineFactory != null);
		
		PDPEngine pdpEngine	= null;
		try {
			pdpEngine	= pdpEngineFactory.newEngine();
		} catch (FactoryException ex) {
			this.logger.error("PDPException creating the PDPEngine", ex);
			throw new PEPException("PDPException creating the PDPEngine", ex);
		}
		assert(pdpEngine != null);
		
		Response response	= null;
		try {
			response	= pdpEngine.decide(pepRequest);
		} catch (PDPException ex) {
			this.logger.error("PDPException deciding on Request", ex);
			throw new PEPException("PDPException deciding on Request", ex);
		}
		return response;
	}

}
