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

import org.apache.openaz.xacml.api.trace.TraceEngine;
import org.apache.openaz.xacml.api.trace.TraceEvent;

/**
 * Implements the {@link org.apache.openaz.xacml.api.trace.TraceEngine} interface to just ignore
 * {@link org.apache.openaz.xacml.api.trace.TraceEvent}s. This is the default implementation, returned by the
 * default {@link org.apache.openaz.xacml.api.trace.TraceEngineFactory}.
 */
public class NullTraceEngine implements TraceEngine {
    private static final NullTraceEngine nullTraceEngine = new NullTraceEngine();

    protected NullTraceEngine() {
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
