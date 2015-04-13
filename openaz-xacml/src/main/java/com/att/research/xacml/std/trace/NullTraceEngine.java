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
import com.att.research.xacml.api.trace.TraceEvent;

/**
 * Implements the {@link com.att.research.xacml.api.trace.TraceEngine} interface to just ignore {@link com.att.research.xacml.api.trace.TraceEvent}s.
 * This is the default implementation, returned by the default {@link com.att.research.xacml.api.trace.TraceEngineFactory}.
 * 
 * @author Christopher A. Rath
 * @version $Revision$
 */
public class NullTraceEngine implements TraceEngine {
	private static final NullTraceEngine nullTraceEngine	= new NullTraceEngine();
	
	protected NullTraceEngine() {
	}
	
	protected NullTraceEngine(Properties properties) {
	}
	
	/**
	 * Gets the single instance of the <code>NullTraceEngine</code> class.
	 * 
	 * @return the single instance of the <code>NullTraceEngine</code> class.
	 */
	public static NullTraceEngine newInstance() {
		return nullTraceEngine;
	}

	/**
	 * Gets the single instance of the <code>NullTraceEngine</code> class.
	 * 
	 * @return the single instance of the <code>NullTraceEngine</code> class.
	 */
	public static NullTraceEngine newInstance(Properties properties) {
		return nullTraceEngine;
	}

	@Override
	public void trace(TraceEvent<?> traceEvent) {
	}

	@Override
	public boolean isTracing() {
		return false;
	}

}
