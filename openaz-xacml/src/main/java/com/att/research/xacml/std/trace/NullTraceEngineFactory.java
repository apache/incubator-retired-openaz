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
 * an instance of the {@link NullTraceEngine} class.
 * 
 * @author Christopher A. Rath
 * @version $Revision$
 */
public class NullTraceEngineFactory extends TraceEngineFactory {
	/**
	 * Creates a new <code>NullTraceEngineFactory</code>
	 */
	public NullTraceEngineFactory() {
	}

	/**
	 * Creates a new <code>NullTraceEngineFactory</code>
	 */
	public NullTraceEngineFactory(Properties properties) {
	}

	@Override
	public TraceEngine getTraceEngine() {
		return NullTraceEngine.newInstance();
	}

	@Override
	public TraceEngine getTraceEngine(Properties properties) {
		return NullTraceEngine.newInstance(properties);
	}
}
