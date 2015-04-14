/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.openliberty.openaz.pepapi.std;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.Result;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPEngineFactory;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.std.json.JSONRequest;
import com.att.research.xacml.std.json.JSONResponse;
import com.att.research.xacml.std.json.JSONStructureException;
import com.att.research.xacml.util.FactoryException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


final class StdPepAgent implements PepAgent {
	
	@SuppressWarnings("unused")
	private static final Log logger = LogFactory.getLog(StdPepAgent.class);

	private Properties xacmlProperties;

	private PepConfig pepConfig;

	private PDPEngine pdpEngine;

	private PDPEngineFactory pdpEngineFactory;

	private List<ObligationStoreAware> obligationHandlers;
	
	private PepRequestFactory pepRequestFactory;
	
	private PepResponseFactory pepResponseFactory;
	
	StdPepAgent() {
		obligationHandlers = new ArrayList<ObligationStoreAware>();
	}
	
	final void initialize() {
		assert(pdpEngineFactory != null);

		//Instantiate PDPEngine
		if(pdpEngine == null) {
			try {
				pdpEngine = pdpEngineFactory.newEngine(xacmlProperties);
			} catch (FactoryException e) {
				throw new PepException(e);
			}
		}

		List<ObjectMapper> objectMappers = new ArrayList<ObjectMapper>();
		for(String mapperClassName: pepConfig.getMapperClassNames()) {
			Class<? extends ObjectMapper> clazz = (Class<? extends ObjectMapper>)PepUtils.loadClass(mapperClassName);
			objectMappers.add(PepUtils.instantiateClass(clazz));
		}
		MapperRegistry mapperRegistry = StdMapperRegistry.newInstance(pepConfig, objectMappers);

		ObligationRouter oRouter = null;
		if(!obligationHandlers.isEmpty()) {
			ObligationHandlerRegistry oHandlerRegistry = StdObligationHandlerRegistry.newInstance(obligationHandlers);
			ThreadLocalObligationStore oStore = ThreadLocalObligationStore.newInstance();
			for(ObligationStoreAware oHandler: obligationHandlers) {
				oHandler.setObligationStore(oStore);
			}
            oRouter = StdObligationRouter.newInstance(oHandlerRegistry, oStore);
		}
		
		//Instantiate PepRequestFactory
		pepRequestFactory = new StdPepRequestFactory(pepConfig, mapperRegistry);
		//Instantiate PepResponseFactory
		pepResponseFactory = new StdPepResponseFactory(pepConfig, oRouter);
	}
	
	@Override
	public PepResponse decide(Object... objects) {
		return decide(pepRequestFactory.newPepRequest(objects)).get(0);
	}
	
	@Override
	public PepResponse simpleDecide(String subjectId, String actionId,
			String resourceId) {
		return decide(Subject.newInstance(subjectId), Action.newInstance(actionId), Resource.newInstance(resourceId));
	}
	
	@Override
	public List<PepResponse> bulkDecide(List<?> actionResourcePairs, Object... objects) {
		return decide(pepRequestFactory.newBulkPepRequest(actionResourcePairs, objects));
	}
		
	private List<PepResponse> decide(PepRequest pepRequest) {
		List<PepResponse> pepResponses = new ArrayList<PepResponse>();
		Request request = pepRequest.getWrappedRequest();

		//Log request
		if(logger.isDebugEnabled()) {
			logRequest(request);
		}

		Response response;
		try {
			response = pdpEngine.decide(request);
		} catch (PDPException e) {
			logger.error(e);
			throw new PepException(e);
		}

		//Log the response
		if(logger.isDebugEnabled()) {
			logResponse(response);
		}

		for(Result result: response.getResults()) {
			pepResponses.add(pepResponseFactory.newPepResponse(result));
		}
		return pepResponses;
    }

	private void logRequest(Request request) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			JSONRequest.convert(request, out);
			logger.debug(out.toString("UTF-8"));
		} catch (IOException e) {
			logger.debug("Error printing XACML request in JSON", e);
		} catch (JSONStructureException e) {
			logger.debug("Error printing XACML request in JSON", e);
		}finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.debug("Error closing stream");
				}
			}
		}
	}

	private void logResponse(Response response) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			JSONResponse.convert(response, out);
			logger.debug(out.toString("UTF-8"));
		} catch (IOException e) {
			logger.debug("Error printing XACML response in JSON", e);
		} catch (JSONStructureException e) {
			logger.debug("Error printing XACML response in JSON", e);
		}finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.debug("Error closing stream");
				}
			}
		}
	}

	public PDPEngine getPdpEngine() {
		return pdpEngine;
	}

	public PepConfig getPepConfig() {
		return pepConfig;
	}

	void setPdpEngineFactory(PDPEngineFactory pdpEngineFactory) {
		this.pdpEngineFactory = pdpEngineFactory;
	}

	void setPepConfig(PepConfig pepConfig) {
		this.pepConfig = pepConfig;
	}

	void setXacmlProperties(Properties properties) {
		this.xacmlProperties = properties;
	}

	void setObligationHandlers(List<ObligationStoreAware> obligationHandlers) {
		if(obligationHandlers != null) {
			this.obligationHandlers = new ArrayList<ObligationStoreAware>();
			this.obligationHandlers.addAll(obligationHandlers);
		}
	}
}
