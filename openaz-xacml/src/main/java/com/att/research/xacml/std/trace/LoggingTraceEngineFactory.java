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
package com.att.research.xacml.std.trace;

import java.util.Properties;

import com.att.research.xacml.api.trace.TraceEngine;
import com.att.research.xacml.api.trace.TraceEngineFactory;

/**
 * Extends the {@link com.att.research.xacml.api.trace.TraceEngineFactory} class to implement the <code>getTraceEngine</code> method to return
 * an instance of the {@link LoggingTraceEngine} class.
 * 
 * @author Christopher A. Rath
 * @version $Revision$
 */
public class LoggingTraceEngineFactory extends TraceEngineFactory {
	/**
	 * Creates a new <code>LoggingTraceEngineFactory</code>
	 */
	public LoggingTraceEngineFactory() {
	}

	/**
	 * Creates a new <code>LoggingTraceEngineFactory</code>
	 */
	public LoggingTraceEngineFactory(Properties properties) {
	}

	@Override
	public TraceEngine getTraceEngine() {
		return LoggingTraceEngine.newInstance();
	}

	@Override
	public TraceEngine getTraceEngine(Properties properties) {
		return LoggingTraceEngine.newInstance(properties);
	}

}
