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
package com.att.research.xacml.std.pip;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPFinder;
import com.att.research.xacml.api.pip.PIPFinderFactory;
import com.att.research.xacml.std.pip.finders.ConfigurableEngineFinder;
import com.att.research.xacml.util.XACMLProperties;

public class StdPIPFinderFactory extends PIPFinderFactory {
	private PIPFinder pipFinder;
	
	private Log logger	= LogFactory.getLog(this.getClass());
	
	public StdPIPFinderFactory() {
	}

	public StdPIPFinderFactory(Properties properties) {
	}

	@Override
	public PIPFinder getFinder() throws PIPException {
		if (pipFinder == null) {
			synchronized(this) {
				if (pipFinder == null) {
					pipFinder					= new ConfigurableEngineFinder();
					Properties xacmlProperties	= null;
					try {
						xacmlProperties	= XACMLProperties.getProperties();
					} catch (Exception ex) {
						this.logger.error("Exception getting XACML properties: " + ex.getMessage(), ex);
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
					pipFinder					= new ConfigurableEngineFinder();
					((ConfigurableEngineFinder)pipFinder).configure(properties);
				}
			}
		}
		return this.pipFinder;
	}
}
