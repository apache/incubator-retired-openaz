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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.att.research.xacml.api.trace.TraceEngine;
import com.att.research.xacml.api.trace.TraceEvent;
import com.att.research.xacml.api.trace.Traceable;

/**
 * Implements the {@link com.att.research.xacml.api.trace.TraceEngine} interface to log {@link com.att.research.xacml.api.trace.TraceEvent}s
 * using the Apache Commons logging system with debug messages.
 * 
 * @author Christopher A. Rath
 * @version $Revision$
 */
public class LoggingTraceEngine implements TraceEngine {
	private static final LoggingTraceEngine loggingTraceEngine	= new LoggingTraceEngine();
	
	private Log logger	= LogFactory.getLog(this.getClass());
	
	protected LoggingTraceEngine() {
	}
	
	protected LoggingTraceEngine(Properties properties) {
	}
	
	/**
	 * Gets the single instance of the <code>LoggingTraceEngine</code>.
	 * 
	 * @return the single instance of the <code>LoggingTraceEngine</code>.
	 */
	public static LoggingTraceEngine newInstance() {
		return loggingTraceEngine;
	}

	/**
	 * Gets the single instance of the <code>LoggingTraceEngine</code>.
	 * 
	 * @return the single instance of the <code>LoggingTraceEngine</code>.
	 */
	public static LoggingTraceEngine newInstance(Properties properties) {
		return loggingTraceEngine;
	}

	@Override
	public void trace(TraceEvent<?> traceEvent) {
		String message	= traceEvent.getMessage();
		Traceable cause	= traceEvent.getCause();
		this.logger.debug(
				traceEvent.getTimestamp().toString() + ": " +
				"\"" + (message == null ? "" : message) + "\"" +
				(cause == null ? "" : " from \"" + cause.getTraceId() + "\"")
				);
		Object traceObject	= traceEvent.getValue();
		if (traceObject != null) {
			this.logger.debug(traceObject);
		}
	}

	@Override
	public boolean isTracing() {
		return this.logger.isDebugEnabled();
	}

}
