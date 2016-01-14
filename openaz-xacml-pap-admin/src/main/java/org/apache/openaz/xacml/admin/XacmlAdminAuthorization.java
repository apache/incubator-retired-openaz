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

package org.apache.openaz.xacml.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.std.annotations.XACMLRequest;
import org.apache.openaz.xacml.std.annotations.RequestParser;
import org.apache.openaz.xacml.std.annotations.XACMLSubject;
import org.apache.openaz.xacml.std.annotations.XACMLAction;
import org.apache.openaz.xacml.std.annotations.XACMLResource;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.Response;
import org.apache.openaz.xacml.api.Result;
import org.apache.openaz.xacml.api.pdp.PDPEngine;
import org.apache.openaz.xacml.api.pdp.PDPEngineFactory;
import org.apache.openaz.xacml.api.pdp.PDPException;
import org.apache.openaz.xacml.util.FactoryException;

public class XacmlAdminAuthorization {
	private static Log logger	= LogFactory.getLog(XacmlAdminAuthorization.class);
	
	public enum AdminAction {
		ACTION_ACCESS("access"),
		ACTION_READ("read"),
		ACTION_WRITE("write"),
		ACTION_ADMIN("admin");
		
		String action;
		AdminAction(String a) {
			this.action = a;
		}
		public String toString() {
			return this.action;
		}
	}
	
	public enum AdminResource {
		RESOURCE_APPLICATION("application"),
		RESOURCE_POLICY_WORKSPACE("workspace"),
		RESOURCE_POLICY_EDITOR("editor"),
		RESOURCE_DICTIONARIES("dictionaries"),
		RESOURCE_PDP_ADMIN("pdp_admin"),
		RESOURCE_PIP_ADMIN("pip_admin");
		
		String resource;
		AdminResource(String r) {
			this.resource = r;
		}
		public String toString() {
			return this.resource;
		}
	}
	
	@XACMLRequest(ReturnPolicyIdList=true)
	public class AuthorizationRequest {
		
		@XACMLSubject(includeInResults=true)
		String	userID;
		
		@XACMLAction()
		String	action;
		
		@XACMLResource()
		String	resource;
		
		public AuthorizationRequest(String userId, String action, String resource) {
			this.userID = userId;
			this.action = action;
			this.resource = resource;
		}

		public String getUserID() {
			return userID;
		}

		public void setUserID(String userID) {
			this.userID = userID;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getResource() {
			return resource;
		}

		public void setResource(String resource) {
			this.resource = resource;
		}
	}
	
	//
	// The PDP Engine
	//
	protected PDPEngine pdpEngine;

	public XacmlAdminAuthorization() {
		PDPEngineFactory pdpEngineFactory	= null;
		try {
			pdpEngineFactory	= PDPEngineFactory.newInstance();
			if (pdpEngineFactory == null) {
				logger.error("Failed to create PDP Engine Factory");
			}
			this.pdpEngine = pdpEngineFactory.newEngine();
		} catch (FactoryException e) {
			logger.error("Exception create PDP Engine: " + e.getLocalizedMessage());
		}
	}
	
	public boolean	isAuthorized(String userid, AdminAction action, AdminResource resource) {
		logger.info("authorize: " + userid + " to " + action + " with " + resource);
		if (this.pdpEngine == null) {
			logger.warn("no pdp engine available to authorize");
			return false;
		}
		Request request;
		try {
			request = RequestParser.parseRequest(new AuthorizationRequest(userid, action.toString(), resource.toString()));
		} catch (IllegalArgumentException | IllegalAccessException | DataTypeException e) {
			logger.error("Failed to create request: " + e.getLocalizedMessage());
			return false;
		}
		if (request == null) {
			logger.error("Failed to parse request.");
			return false;
		}
		logger.info("Request: " + request);
		//
		// Ask the engine
		//
		try {
			Response response = this.pdpEngine.decide(request);
			if (response == null) {
				logger.error("Null response from PDP decide");
			}
			//
			// Should only be one result
			//
			for (Result result : response.getResults()) {
				Decision decision = result.getDecision();
				logger.info("Decision: " + decision);
				if (decision.equals(Decision.PERMIT)) {
					return true;
				}
			}
		} catch (PDPException e) {
			logger.error("PDP Decide failed: " + e.getLocalizedMessage());
		}
		return false;
	}
}
