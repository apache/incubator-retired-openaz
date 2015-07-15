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
package org.apache.openaz.xacml.pdp.std;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPFinderFactory;
import org.apache.openaz.xacml.api.trace.TraceEngine;
import org.apache.openaz.xacml.api.trace.TraceEngineFactory;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationContextFactory;
import org.apache.openaz.xacml.pdp.policy.PolicyFinder;
import org.apache.openaz.xacml.pdp.policy.PolicyFinderFactory;

/**
 * StdEvaluationContextFactory extends {@link org.apache.openaz.xacml.pdp.eval.EvaluationContextFactory} to
 * implement the <code>getEvaluationContext</code> method with a standard
 * {@link org.apache.openaz.xacml.pdp.eval.EvaluationContext}.
 */
public class StdEvaluationContextFactory extends EvaluationContextFactory {
    private Log logger = LogFactory.getLog(this.getClass());
    private PolicyFinder policyFinder;
    private PIPFinder pipFinder;
    private TraceEngine traceEngine;

    /**
     * Should this properties file be passed onward when instantiating the PolicyFinder and the PIPFinder? If
     * yes, then we are assuming that the given properties were not just meant to configure the evaluation
     * context, but all the other engines that get created. If no, then we are assuming the given properties
     * were only meant for the evaluation context. But this implementation as of 7/14 does not even need the
     * properties for configuring itself. The problem is, the caller does not have the ability to instantiate
     * the PIPFinder and PolicyFinder engines. This is done internally by the evaluation context. So how can
     * they have the ability to customize PIP/Policy factories with their own properties object if the
     * properties file isn't passed on? Thus, this class will pass on the properties file if given in the
     * constructor.
     */
    protected Properties properties = null;

    protected PolicyFinder getPolicyFinder() {
        if (this.policyFinder == null) {
            synchronized (this) {
                if (this.policyFinder == null) {
                    try {
                        if (this.properties == null) {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("getting Policy finder using default properties");
                            }
                            PolicyFinderFactory policyFinderFactory = PolicyFinderFactory.newInstance();
                            this.policyFinder = policyFinderFactory.getPolicyFinder();
                        } else {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("getting Policy finder using properties: "
                                                  + this.properties);
                            }
                            PolicyFinderFactory policyFinderFactory = PolicyFinderFactory
                                .newInstance(this.properties);
                            this.policyFinder = policyFinderFactory.getPolicyFinder(this.properties);
                        }
                    } catch (Exception ex) {
                        this.logger.error("Exception getting PolicyFinder: " + ex.getMessage(), ex);
                    }
                }
            }
        }
        return this.policyFinder;
    }

    protected PIPFinder getPIPFinder() {
        if (this.pipFinder == null) {
            synchronized (this) {
                if (this.pipFinder == null) {
                    try {
                        if (this.properties == null) {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("getting PIP finder using default properties");
                            }
                            PIPFinderFactory pipFinderFactory = PIPFinderFactory.newInstance();
                            this.pipFinder = pipFinderFactory.getFinder();
                        } else {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("getting PIP finder using properties: " + this.properties);
                            }
                            PIPFinderFactory pipFinderFactory = PIPFinderFactory.newInstance(this.properties);
                            this.pipFinder = pipFinderFactory.getFinder(this.properties);
                        }
                    } catch (Exception ex) {
                        this.logger.error("Exception getting PIPFinder: " + ex.toString(), ex);
                    }
                }
            }
        }
        return this.pipFinder;
    }

    protected TraceEngine getTraceEngine() {
        if (this.traceEngine == null) {
            synchronized (this) {
                if (this.traceEngine == null) {
                    try {
                        TraceEngineFactory traceEngineFactory = TraceEngineFactory.newInstance();
                        this.traceEngine = traceEngineFactory.getTraceEngine();
                    } catch (Exception ex) {
                        this.logger.error("Exception getting TraceEngine: " + ex.toString(), ex);
                    }
                }
            }
        }
        return this.traceEngine;
    }

    public StdEvaluationContextFactory() {
    }

    public StdEvaluationContextFactory(Properties properties) {
        this.properties = properties;
    }

    @Override
    public EvaluationContext getEvaluationContext(Request request) {
        return new StdEvaluationContext(request, this.getPolicyFinder(), this.getPIPFinder(),
                                        this.getTraceEngine());
    }

    @Override
    public void setPolicyFinder(PolicyFinder policyFinderIn) {
        this.policyFinder = policyFinderIn;
    }

    @Override
    public void setPIPFinder(PIPFinder pipFinderIn) {
        this.pipFinder = pipFinderIn;
    }

}
