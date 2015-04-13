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
package com.att.research.xacml.std.pap;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.att.research.xacml.api.pap.PAPEngine;
import com.att.research.xacml.api.pap.PAPEngineFactory;
import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.util.FactoryException;

public class StdEngineFactory extends PAPEngineFactory {
	private static Log	logger	= LogFactory.getLog(StdEngineFactory.class);
	
	@Override
	public PAPEngine newEngine() throws FactoryException, PAPException {
		try {
			return new StdEngine();
		} catch (IOException e) {
			logger.error("Failed to create engine: ", e);
			return null;
		}
	}

	@Override
	public PAPEngine newEngine(Properties properties) throws FactoryException,
			PAPException {
		try {
			return new StdEngine(properties);
		} catch (IOException e) {
			logger.error("Failed to create engine: ", e);
			return null;
		}
	}

}
