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
package org.apache.openaz.xacml.std.trace;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.trace.TraceEngine;
import org.apache.openaz.xacml.api.trace.TraceEvent;
import org.apache.openaz.xacml.api.trace.Traceable;

/**
 * Implements the {@link org.apache.openaz.xacml.api.trace.TraceEngine} interface to log
 * {@link org.apache.openaz.xacml.api.trace.TraceEvent}s using the Apache Commons logging system with debug
 * messages.
 */
public class LoggingTraceEngine implements TraceEngine {
    private static final LoggingTraceEngine loggingTraceEngine = new LoggingTraceEngine();

    private Log logger = LogFactory.getLog(this.getClass());

    protected LoggingTraceEngine() {
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
        String message = traceEvent.getMessage();
        Traceable cause = traceEvent.getCause();
        this.logger.debug(traceEvent.getTimestamp().toString() + ": " + "\""
                          + (message == null ? "" : message) + "\""
                          + (cause == null ? "" : " from \"" + cause.getTraceId() + "\""));
        Object traceObject = traceEvent.getValue();
        if (traceObject != null) {
            this.logger.debug(traceObject);
        }
    }

    @Override
    public boolean isTracing() {
        return this.logger.isDebugEnabled();
    }

}
