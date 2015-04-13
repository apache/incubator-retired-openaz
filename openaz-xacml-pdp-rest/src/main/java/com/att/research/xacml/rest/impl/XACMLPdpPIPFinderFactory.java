/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.rest.impl;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPFinder;
import com.att.research.xacml.api.pip.PIPFinderFactory;
import com.att.research.xacml.std.pip.finders.ConfigurableEngineFinder;
import com.att.research.xacml.util.XACMLProperties;

public class XACMLPdpPIPFinderFactory extends PIPFinderFactory {
	private ConfigurableEngineFinder pipFinder;
	
	private static Log logger	= LogFactory.getLog(XACMLPdpPIPFinderFactory.class);
	
	public XACMLPdpPIPFinderFactory() {
	}

	public XACMLPdpPIPFinderFactory(Properties properties) {
	}

	@Override
	public PIPFinder getFinder() throws PIPException {
		if (pipFinder == null) {
			synchronized(this) {
				if (pipFinder == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Creating default configurable engine finder");
					}
					pipFinder					= new ConfigurableEngineFinder();
					Properties xacmlProperties	= null;
					try {
						xacmlProperties	= XACMLProperties.getProperties();
					} catch (Exception ex) {
						logger.error("Exception getting XACML properties: " + ex.getMessage(), ex);
						return null;
					}
					if (xacmlProperties != null) {
						((ConfigurableEngineFinder)pipFinder).configure(xacmlProperties);
					}
				}
			}
		}
		return pipFinder;
	}

	@Override
	public PIPFinder getFinder(Properties properties) throws PIPException {
		if (pipFinder == null) {
			synchronized(this) {
				if (pipFinder == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Creating configurable engine finder using: " + properties);
					}
					pipFinder					= new ConfigurableEngineFinder();
					((ConfigurableEngineFinder)pipFinder).configure(properties);
				}
			}
		}
		return this.pipFinder;
	}
}
