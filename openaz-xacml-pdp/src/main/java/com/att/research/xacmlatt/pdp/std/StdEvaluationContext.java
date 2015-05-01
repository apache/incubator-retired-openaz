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
package com.att.research.xacmlatt.pdp.std;

import com.att.research.xacml.api.IdReferenceMatch;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.pip.*;
import com.att.research.xacml.api.trace.TraceEngine;
import com.att.research.xacml.api.trace.TraceEngineFactory;
import com.att.research.xacml.api.trace.TraceEvent;
import com.att.research.xacml.std.pip.engines.RequestEngine;
import com.att.research.xacml.std.pip.finders.RequestFinder;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.policy.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Properties;

/**
 * StdEvaluationContext implements the {@link com.att.research.xacmlatt.pdp.eval.EvaluationContext} interface using
 * default factories to load the XACML policies, and get the PIP engines.
 *
 * @author car
 * @version $Revision: 1.1 $
 */
public class StdEvaluationContext implements EvaluationContext {
    private Log logger	= LogFactory.getLog(this.getClass());
    private Properties properties;
    private Request request;
    private RequestFinder requestFinder;
    private PolicyFinder policyFinder;
    private TraceEngine traceEngine;

    /**
     * Creates a new <code>StdEvaluationContext</code> with the given {@link com.att.research.xacml.api.Request} and
     * {@link com.att.research.xacmlatt.pdp.policy.PolicyDef}.
     *
     * @param requestIn the <code>Request</code>
     * @param policyDef the <code>PolicyDef</code>
     */
    public StdEvaluationContext(Request requestIn, PolicyFinder policyFinderIn, PIPFinder pipFinder, TraceEngine traceEngineIn, Properties properties) {
        this.properties		= properties;
        this.request		= requestIn;
        this.policyFinder	= policyFinderIn;
        if (traceEngineIn != null) {
            this.traceEngine	= traceEngineIn;
        } else {
            try {
                if (this.properties == null) {
                    this.traceEngine	= TraceEngineFactory.newInstance().getTraceEngine();
                } else {
                    this.traceEngine	= TraceEngineFactory.newInstance(this.properties).getTraceEngine(this.properties);
                }
            } catch (FactoryException ex) {
                this.logger.error("FactoryException creating TraceEngine: " + ex.toString(), ex);
            }
        }

        if (pipFinder == null) {
            this.requestFinder		= new RequestFinder(null, new RequestEngine(requestIn));
        } else {
            if (pipFinder instanceof RequestFinder) {
                this.requestFinder	= (RequestFinder)pipFinder;
            } else {
                this.requestFinder	= new RequestFinder(pipFinder, new RequestEngine(requestIn));
            }
        }
    }

    public StdEvaluationContext(Request requestIn, PolicyFinder policyFinderIn, PIPFinder pipFinder, TraceEngine traceEngineIn) {
        this(requestIn, policyFinderIn, pipFinder, traceEngineIn, null);
    }

    public StdEvaluationContext(Request requestIn, PolicyFinder policyFinderIn, PIPFinder pipFinder) {
        this(requestIn, policyFinderIn, pipFinder, null);
    }

    @Override
    public Request getRequest() {
        return this.request;
    }

    @Override
    public PIPResponse getAttributes(PIPRequest pipRequest) throws PIPException {
        return this.requestFinder.getAttributes(pipRequest, null);
    }

    @Override
    public PIPResponse getAttributes(PIPRequest pipRequest, PIPEngine exclude) throws PIPException {
        return this.requestFinder.getAttributes(pipRequest, exclude);
    }

    @Override
    public PIPResponse getAttributes(PIPRequest pipRequest, PIPEngine exclude, PIPFinder pipFinderRoot) throws PIPException {
        return this.requestFinder.getAttributes(pipRequest, exclude, pipFinderRoot);
    }

    @Override
    public PolicyFinderResult<PolicyDef> getRootPolicyDef() {
        return this.policyFinder.getRootPolicyDef(this);
    }

    @Override
    public PolicyFinderResult<Policy> getPolicy(IdReferenceMatch idReferenceMatch) {
        return this.policyFinder.getPolicy(idReferenceMatch);
    }

    @Override
    public PolicyFinderResult<PolicySet> getPolicySet(IdReferenceMatch idReferenceMatch) {
        return this.policyFinder.getPolicySet(idReferenceMatch);
    }

    @Override
    public void trace(TraceEvent<?> traceEvent) {
        if (this.traceEngine != null) {
            this.traceEngine.trace(traceEvent);
        }
    }

    @Override
    public boolean isTracing() {
        return (this.traceEngine == null ? false : this.traceEngine.isTracing());
    }

    @Override
    public PIPResponse getMatchingAttributes(PIPRequest pipRequest, PIPEngine exclude) throws PIPException {
        return this.requestFinder.getMatchingAttributes(pipRequest, exclude);
    }

    @Override
    public PIPResponse getMatchingAttributes(PIPRequest pipRequest, PIPEngine exclude, PIPFinder pipFinderParent) throws PIPException {
        return this.requestFinder.getMatchingAttributes(pipRequest, exclude, pipFinderParent);
    }

    @Override
    public Collection<PIPEngine> getPIPEngines() {
        return this.requestFinder.getPIPEngines();
    }
}
