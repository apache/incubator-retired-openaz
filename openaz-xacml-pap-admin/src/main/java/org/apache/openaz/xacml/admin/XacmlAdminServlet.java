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

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

import org.apache.openaz.xacml.rest.XACMLRest;


//
// The Servlet underlying the Vaadin Servlet
//
@Push
@WebServlet(
		value = "/*",
		description = "XACML Admin Console",
		asyncSupported = true, 
		loadOnStartup=1,
		initParams = { 
		@WebInitParam(name = "XACML_PROPERTIES_NAME", value = "xacml.admin.properties", description = "The location of the properties file holding configuration information.")
})
@VaadinServletConfiguration(productionMode = false, ui = XacmlAdminUI.class)
public class XacmlAdminServlet extends VaadinServlet {
	//
	// All static declarations
	//
	private static Log logger	= LogFactory.getLog(XacmlAdminServlet.class); //NOPMD

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		//
		// Common initialization
		//
		XACMLRest.xacmlInit(servletConfig);

		// Initialization
		XacmlAdminUI.servletInit();
	}
	
	@Override
	public void destroy() {
		XacmlAdminUI.servletDestroy();
		super.destroy();
	}
	
	/**
	 * 
	 * Called by:
	 * 	- PAP to notify Vaadin GUIs that something has changed
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// watch for notifications from the PAP
		if (request.getMethod().equals("PUT") && request.getParameter("PAPNotification") != null) {
			XacmlAdminUI.doPAPNotification(request, response);
			return;
		}

		// not a PAP notification, so let Vaadin handle normally
		super.service(request,response);
	}
}
