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

package org.apache.openaz.xacml.admin.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.api.pap.PAPEngine;
import org.apache.openaz.xacml.api.pap.PAPException;
import org.apache.openaz.xacml.api.pap.PDP;
import org.apache.openaz.xacml.api.pap.PDPGroup;
import org.apache.openaz.xacml.api.pap.PDPPolicy;
import org.apache.openaz.xacml.api.pap.PDPStatus;
import org.apache.openaz.xacml.rest.XACMLRestProperties;
import org.apache.openaz.xacml.std.pap.StdPDP;
import org.apache.openaz.xacml.std.pap.StdPDPGroup;
import org.apache.openaz.xacml.std.pap.StdPDPItemSetChangeNotifier;
import org.apache.openaz.xacml.std.pap.StdPDPPolicy;
import org.apache.openaz.xacml.std.pap.StdPDPStatus;
import org.apache.openaz.xacml.util.XACMLProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * Implementation of the PAPEngine interface that communicates with a PAP engine in a remote servlet
 * through a RESTful interface
 * 
 * @author glenngriffin
 *
 */
public class RESTfulPAPEngine extends StdPDPItemSetChangeNotifier implements PAPEngine {
	private static final Log logger	= LogFactory.getLog(RESTfulPAPEngine.class);

	//
	// URL of the PAP Servlet that this Admin Console talks to
	//
	private String papServletURLString;
	
	/**
	 * Set up link with PAP Servlet and get our initial set of Groups
	 * @throws Exception 
	 */
	public RESTfulPAPEngine (String myURLString) throws PAPException, IOException  {
		//
		// Get our URL to the PAP servlet
		//
		this.papServletURLString = XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL);
		if (this.papServletURLString == null || this.papServletURLString.length() == 0) {
			String message = "The property 'POLICYENGINE_ADMIN_ACTIVE' was not set during installation.  Admin Console cannot call PAP.";
			logger.error(message);
			throw new PAPException(message);
		}

		//
		// register this Admin Console with the PAP Servlet to get updates
		//
		Object newURL = sendToPAP("PUT", null, null, null, "adminConsoleURL=" + myURLString);
		if (newURL != null) {
			// assume this was a re-direct and try again
			logger.warn("Redirecting to '" + newURL + "'");
			this.papServletURLString = (String)newURL;
			newURL = sendToPAP("PUT", null, null, null, "adminConsoleURL=" + myURLString);
			if (newURL != null) {
				logger.error("Failed to redirect to " + this.papServletURLString);
				throw new PAPException("Failed to register with PAP");
			}
		}
	}
	

	//
	// High-level commands used by the Admin Console code through the PAPEngine Interface
	//
	
	@Override
	public PDPGroup getDefaultGroup() throws PAPException {
		PDPGroup newGroup = (PDPGroup)sendToPAP("GET", null, null, StdPDPGroup.class, "groupId=", "default=");
		return newGroup;
	}

	@Override
	public void SetDefaultGroup(PDPGroup group) throws PAPException {
		sendToPAP("POST", null, null, null, "groupId=" + group.getId(), "default=true");
	}

	@Override
	public Set<PDPGroup> getPDPGroups() throws PAPException {
		Set<PDPGroup> newGroupSet;
		newGroupSet = (Set<PDPGroup>) this.sendToPAP("GET", null, Set.class, StdPDPGroup.class, "groupId=");
		return Collections.unmodifiableSet(newGroupSet);
	}


	@Override
	public PDPGroup getGroup(String id) throws PAPException {
		PDPGroup newGroup = (PDPGroup)sendToPAP("GET", null, null, StdPDPGroup.class, "groupId=" + id);
		return newGroup;
	}

	@Override
	public void newGroup(String name, String description)
			throws PAPException, NullPointerException {
		String escapedName = null;
		String escapedDescription = null;
		try {
			escapedName = URLEncoder.encode(name, "UTF-8");
			escapedDescription = URLEncoder.encode(description, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new PAPException("Unable to send name or description to PAP: " + e.getMessage());
		}
		
		this.sendToPAP("POST", null, null, null, "groupId=", "groupName="+escapedName, "groupDescription=" + escapedDescription);
	}
	
	
	/**
	 * Update the configuration on the PAP for a single Group.
	 * 
	 * @param group
	 * @return
	 * @throws PAPException
	 */
	public void updateGroup(PDPGroup group) throws PAPException {

		try {
			
			//
			// ASSUME that all of the policies mentioned in this group are already located in the correct directory on the PAP!
			//
			// Whenever a Policy is added to the group, that file must be automatically copied to the PAP from the Workspace.
			// 
			
			
//			// Copy all policies from the local machine's workspace to the PAP's PDPGroup directory.
//			// This is not efficient since most of the policies will already exist there.
//			// However, the policy files are (probably!) not too huge, and this is a good way to ensure that any corrupted files on the PAP get refreshed.
//			
//TODO WRONG!!!!	The policy.getStream() is based on the location in the PAP directory, not the Workspace.
//TODO 
//			for (PDPPolicy policy : group.getPolicies()) {
//				try (InputStream is = policy.getStream()) {
//				copyFile(policy.getId(), group, is);
//				}
//			}
			
			// now update the group object on the PAP
			
			sendToPAP("PUT", group, null, null, "groupId=" + group.getId());
		} catch (Exception e) {
			String message = "Unable to PUT policy '" + group.getId() + "', e:" + e;
			logger.error(message, e);
			throw new PAPException(message);
		}
	}
	
	
	@Override
	public void removeGroup(PDPGroup group, PDPGroup newGroup)
			throws PAPException, NullPointerException {
		String moveToGroupString = null;
		if (newGroup != null) {
			moveToGroupString = "movePDPsToGroupId=" + newGroup.getId();
		}
		sendToPAP("DELETE", null, null, null, "groupId=" + group.getId(), moveToGroupString);
	}
	
	@Override
	public PDPGroup getPDPGroup(PDP pdp) throws PAPException {
		return getPDPGroup(pdp.getId());
	}

	@Override
	public PDPGroup getPDPGroup(String pdpId) throws PAPException {
		PDPGroup newGroup = (PDPGroup)sendToPAP("GET", null, null, StdPDPGroup.class, "groupId=", "pdpId=" + pdpId, "getPDPGroup=");
		return newGroup;
	}

	@Override
	public PDP getPDP(String pdpId) throws PAPException {
		PDP newPDP = (PDP)sendToPAP("GET", null, null, StdPDP.class, "groupId=", "pdpId=" + pdpId);
		return newPDP;
	}

	@Override
	public void newPDP(String id, PDPGroup group, String name, String description) throws PAPException,
			NullPointerException {
		StdPDP newPDP = new StdPDP(id, name, description);
		sendToPAP("PUT", newPDP, null, null, "groupId=" + group.getId(), "pdpId=" + id);
	}

	
	@Override
	public void movePDP(PDP pdp, PDPGroup newGroup) throws PAPException {
		sendToPAP("POST", null, null, null, "groupId=" + newGroup.getId(), "pdpId=" + pdp.getId());
	}

	@Override
	public void updatePDP(PDP pdp) throws PAPException {
		PDPGroup group = getPDPGroup(pdp);
		sendToPAP("PUT", pdp, null, null, "groupId=" + group.getId(), "pdpId=" + pdp.getId());
	}
	
	@Override
	public void removePDP(PDP pdp) throws PAPException {
		PDPGroup group = getPDPGroup(pdp);
		sendToPAP("DELETE", null, null, null, "groupId=" + group.getId(), "pdpId=" + pdp.getId());
	}

	

	@Override
	public void publishPolicy(String id, String name, boolean isRoot,
			InputStream policy, PDPGroup group) throws PAPException {
		
//TODO - this method should take as input a Policy object, add it to the group, then call updateGroup
//TODO - ?? Where does the Policy object (with the version info) get created?

		// copy the (one) file into the target directory on the PAP servlet
		copyFile(id, group, policy);
		
		// adjust the local copy of the group to include the new policy
		PDPPolicy pdpPolicy = new StdPDPPolicy(id, isRoot, name);
		group.getPolicies().add(pdpPolicy);
		
		// tell the PAP servlet to include the policy in the configuration
		updateGroup(group);
	}
	
	
	
	/**
	 * Copy a single Policy file from the input stream to the PAP Servlet.
	 * Either this works (silently) or it throws an exception.
	 * 
	 * @param policyId
	 * @param group
	 * @param policy
	 * @return
	 * @throws PAPException
	 */
	public void copyFile(String policyId, PDPGroup group, InputStream policy) throws PAPException {
		// send the policy file to the PAP Servlet
		try {
			sendToPAP("POST", policy, null, null, "groupId=" + group.getId(), "policyId="+policyId);
		} catch (Exception e) {
			String message = "Unable to PUT policy '" + policyId + "', e:" + e;
			logger.error(message, e);
			throw new PAPException(message);
		}
	}
	

	@Override
	public void	copyPolicy(PDPPolicy policy, PDPGroup group) throws PAPException {
		if (policy == null || group == null) {
			throw new PAPException("Null input policy="+policy+"  group="+group);
		}
		try (InputStream is = new FileInputStream(new File(policy.getLocation())) ) {
			copyFile(policy.getId(), group, is );
		} catch (Exception e) {
			String message = "Unable to PUT policy '" + policy.getId() + "', e:" + e;
			logger.error(message, e);
			throw new PAPException(message);
		}
	}


	
	
	@Override
	public void	removePolicy(PDPPolicy policy, PDPGroup group) throws PAPException {
		throw new PAPException("NOT IMPLEMENTED");

	}

	
	
	/**
	 * Special operation - Similar to the normal PAP operations but this one contacts the PDP directly
	 * to get detailed status info.
	 * 
	 * @param pdp
	 * @return
	 * @throws PAPException 
	 */
	@Override
	public PDPStatus getStatus(PDP pdp) throws PAPException {
		StdPDPStatus status = (StdPDPStatus)sendToPAP("GET", pdp, null, StdPDPStatus.class);
		return status;
	}
	
	
	
	
	//
	// Internal Operations called by the PAPEngine Interface methods
	//
	
	/**
	 * Send a request to the PAP Servlet and get the response.
	 * 
	 * The content is either an InputStream to be copied to the Request OutputStream
	 * 	OR it is an object that is to be encoded into JSON and pushed into the Request OutputStream.
	 * 
	 * The Request parameters may be encoded in multiple "name=value" sets, or parameters may be combined by the caller.
	 * 
	 * @param method
	 * @param content	- EITHER an InputStream OR an Object to be encoded in JSON
	 * @param collectionTypeClass
	 * @param responseContentClass
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	private Object sendToPAP(String method, Object content, Class collectionTypeClass, Class responseContentClass, String... parameters ) throws PAPException {
		HttpURLConnection connection = null;
		try {
			String fullURL = papServletURLString;
			if (parameters != null && parameters.length > 0) {
				String queryString = "";
				for (String p : parameters) {
					queryString += "&" + p;
				}
				fullURL += "?" + queryString.substring(1);
			}
			
			// special case - Status (actually the detailed status) comes from the PDP directly, not the PAP
			if (method.equals("GET") &&
					content instanceof PDP &&
					responseContentClass == StdPDPStatus.class) {
				// Adjust the url and properties appropriately
				fullURL = ((PDP)content).getId() + "?type=Status";
				content = null;
			}
			
			
			URL url = new URL(fullURL);

			//
			// Open up the connection
			//
			connection = (HttpURLConnection)url.openConnection();
			//
			// Setup our method and headers
			//
            connection.setRequestMethod(method);
//				connection.setRequestProperty("Accept", "text/x-java-properties");
//	            connection.setRequestProperty("Content-Type", "text/x-java-properties");
            connection.setUseCaches(false);
            //
            // Adding this in. It seems the HttpUrlConnection class does NOT
            // properly forward our headers for POST re-direction. It does so
            // for a GET re-direction.
            //
            // So we need to handle this ourselves.
            //
            connection.setInstanceFollowRedirects(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			if (content != null) {
				if (content instanceof InputStream) {
		    		try {
		    			//
		    			// Send our current policy configuration
		    			//
		    			try (OutputStream os = connection.getOutputStream()) {
		    				int count = IOUtils.copy((InputStream)content, os);
		    				if (logger.isDebugEnabled()) {
		    					logger.debug("copied to output, bytes="+count);
		    				}
		    			}
		    		} catch (Exception e) {
		    			logger.error("Failed to write content in '" + method + "'", e);
		    			throw e;
		    		}
				} else {
					// The content is an object to be encoded in JSON
		            ObjectMapper mapper = new ObjectMapper();
		            mapper.writeValue(connection.getOutputStream(),  content);
				}
			}
            //
            // Do the connect
            //
            connection.connect();
            if (connection.getResponseCode() == 204) {
            	logger.info("Success - no content.");
            	return null;
            } else if (connection.getResponseCode() == 200) {
            	logger.info("Success. We have a return object.");
            	
            	// get the response content into a String
            	String json = null;
    			// read the inputStream into a buffer (trick found online scans entire input looking for end-of-file)
    		    java.util.Scanner scanner = new java.util.Scanner(connection.getInputStream());
    		    scanner.useDelimiter("\\A");
    		    json =  scanner.hasNext() ? scanner.next() : "";
    		    scanner.close();
    		    logger.info("JSON response from PAP: " + json);
            	
            	// convert Object sent as JSON into local object
	            ObjectMapper mapper = new ObjectMapper();
	            
	            if (collectionTypeClass != null) {
	            	// collection of objects expected
	            	final CollectionType javaType = 
	            	      mapper.getTypeFactory().constructCollectionType(collectionTypeClass, responseContentClass);

	            	Object objectFromJSON = mapper.readValue(json, javaType);
					return objectFromJSON;
	            } else {
	            	// single value object expected
		            Object objectFromJSON = mapper.readValue(json, responseContentClass);
					return objectFromJSON;
	            }

            } else if (connection.getResponseCode() >= 300 && connection.getResponseCode()  <= 399) {
            	// redirection
            	String newURL = connection.getHeaderField("Location");
            	if (newURL == null) {
            		logger.error("No Location header to redirect to when response code="+connection.getResponseCode());
            		throw new IOException("No redirect Location header when response code="+connection.getResponseCode());
            	}
            	int qIndex = newURL.indexOf("?");
            	if (qIndex > 0) {
            		newURL = newURL.substring(0, qIndex);
            	}
            	logger.info("Redirect seen.  Redirecting " + fullURL + " to " + newURL);
            	return newURL;
            } else {
            	logger.warn("Unexpected response code: " + connection.getResponseCode() + "  message: " + connection.getResponseMessage());
            	throw new IOException("Server Response: " + connection.getResponseCode() + ": " + connection.getResponseMessage());
            }

		} catch (Exception e) {
			logger.error("HTTP Request/Response to PAP: " + e,e);
			throw new PAPException("Request/Response threw :" + e);
		} finally {
			// cleanup the connection
				if (connection != null) {
				try {
					// For some reason trying to get the inputStream from the connection
					// throws an exception rather than returning null when the InputStream does not exist.
					InputStream is = null;
					try {
						is = connection.getInputStream();
					} catch (Exception e1) { //NOPMD
						// ignore this
					}
					if (is != null) {
						is.close();
					}

				} catch (IOException ex) {
					logger.error("Failed to close connection: " + ex, ex);
				}
				connection.disconnect();
			}
		}
	}
}


