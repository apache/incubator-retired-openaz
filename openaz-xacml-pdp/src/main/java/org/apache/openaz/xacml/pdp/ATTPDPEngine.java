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
package com.att.research.xacmlatt.pdp;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.att.research.xacml.api.AttributeCategory;
import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.Response;
import com.att.research.xacml.api.Result;
import com.att.research.xacml.api.Status;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.api.pdp.ScopeResolver;
import com.att.research.xacml.api.trace.TraceEngine;
import com.att.research.xacml.api.trace.TraceEngineFactory;
import com.att.research.xacml.api.trace.Traceable;
import com.att.research.xacml.std.StdIndividualDecisionRequestGenerator;
import com.att.research.xacml.std.StdMutableResponse;
import com.att.research.xacml.std.StdMutableResult;
import com.att.research.xacml.std.StdResult;
import com.att.research.xacml.std.StdStatus;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.trace.StdTraceEvent;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.eval.EvaluationContextFactory;
import com.att.research.xacmlatt.pdp.eval.EvaluationException;
import com.att.research.xacmlatt.pdp.policy.PolicyDef;
import com.att.research.xacmlatt.pdp.policy.PolicyFinderResult;

/**
 * ATTPDPEngine implements the {@link com.att.research.xacml.api.pdp.PDPEngine} interface using the XACML 3.0 specification.
 *
 */
public class ATTPDPEngine implements PDPEngine, Traceable {
    private static final Status		STATUS_ADVICE_NA		= new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, "Advice not allowed in combined decision");
    private static final Status		STATUS_OBLIGATIONS_NA	= new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, "Obligations not allowed in combined decision");
    private static final Status		STATUS_COMBINE_FAILED	= new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, "Individual decisions do not match");
    private static final Result		RESULT_ECTX_NULL		= new StdMutableResult(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, "Null EvaluationContext"));

    /*
     * These are the profiles that this reference implementation of the PDP engine supports
     */
    private static final Set<URI> PROFILES			= new HashSet<URI>();
    static {
        PROFILES.add(XACML3.ID_PROFILE_MULTIPLE_COMBINED_DECISION.getUri());
        PROFILES.add(XACML3.ID_PROFILE_MULTIPLE_REFERENCE.getUri());
        PROFILES.add(XACML3.ID_PROFILE_MULTIPLE_REPEATED_ATTRIBUTE_CATEGORIES.getUri());
        PROFILES.add(XACML3.ID_PROFILE_MULTIPLE_SCOPE.getUri());
        PROFILES.add(XACML3.ID_PROFILE_MULTIPLE_XPATH_EXPRESSION.getUri());
    }

    private EvaluationContextFactory evaluationContextFactory;
    private Decision defaultDecision				= Decision.INDETERMINATE;
    private ScopeResolver scopeResolver;
    private TraceEngine traceEngine;
    private Log logger								= LogFactory.getLog(this.getClass());

    protected TraceEngine getTraceEngine() {
        if (this.traceEngine == null) {
            synchronized(this) {
                if (this.traceEngine == null) {
                    try {
                        this.traceEngine	= TraceEngineFactory.newInstance().getTraceEngine();
                    } catch (FactoryException ex) {
                        this.logger.error("FactoryException creating TraceEngine instance: " + ex.toString(), ex);
                        throw new IllegalStateException("FactoryException creating TraceEngine instance", ex);
                    }
                }
            }
        }
        return this.traceEngine;
    }

    public ATTPDPEngine(EvaluationContextFactory evaluationContextFactoryIn, ScopeResolver scopeResolverIn) {
        this.evaluationContextFactory	= evaluationContextFactoryIn;
        this.scopeResolver				= scopeResolverIn;
    }

    public ATTPDPEngine(EvaluationContextFactory evaluationContextFactoryIn, Decision defaultDecisionIn, ScopeResolver scopeResolverIn) {
        this(evaluationContextFactoryIn, scopeResolverIn);
        this.defaultDecision	= defaultDecisionIn;
    }

    public ATTPDPEngine(EvaluationContextFactory evaluationContextFactoryIn, Decision defaultDecisionIn, ScopeResolver scopeResolverIn, Properties properties) {
        this(evaluationContextFactoryIn, defaultDecisionIn, scopeResolverIn);
    }

    protected Result processRequest(EvaluationContext evaluationContext) {
        try {
            PolicyFinderResult<PolicyDef> policyFinderResult	= evaluationContext.getRootPolicyDef();
            if (policyFinderResult.getStatus() != null && !policyFinderResult.getStatus().isOk()) {
                return new StdMutableResult(policyFinderResult.getStatus());
            }
            PolicyDef policyDefRoot					= policyFinderResult.getPolicyDef();
            if (policyDefRoot == null) {
                switch(this.defaultDecision) {
                case DENY:
                case NOTAPPLICABLE:
                case PERMIT:
                    return new StdMutableResult(this.defaultDecision, new StdStatus(StdStatusCode.STATUS_CODE_OK, "No applicable policy"));
                case INDETERMINATE:
                case INDETERMINATE_DENY:
                case INDETERMINATE_DENYPERMIT:
                case INDETERMINATE_PERMIT:
                    return new StdMutableResult(this.defaultDecision, new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, "No applicable policy"));
                }
            }
            Result result	= policyDefRoot.evaluate(evaluationContext);
            if (result.getStatus().isOk()) {
                Collection<AttributeCategory> listRequestAttributesIncludeInResult	= evaluationContext.getRequest().getRequestAttributesIncludedInResult();
                if (listRequestAttributesIncludeInResult != null && listRequestAttributesIncludeInResult.size() > 0) {
                    StdMutableResult stdMutableResult	= new StdMutableResult(result);
                    stdMutableResult.addAttributeCategories(listRequestAttributesIncludeInResult);
                    result	= new StdResult(stdMutableResult);
                }
            }
            return result;
        } catch (EvaluationException ex) {
            return new StdMutableResult(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, ex.getMessage()));
        }
    }

    @Override
    public Response decide(Request pepRequest) throws PDPException {
        /*
         * Validate the request
         */
        TraceEngine traceEngineThis	= this.getTraceEngine();
        if (traceEngineThis.isTracing()) {
            traceEngineThis.trace(new StdTraceEvent<Request>("Input Request", this, pepRequest));
        }
        Status statusRequest	= pepRequest.getStatus();
        if (statusRequest != null && !statusRequest.isOk()) {
            return new StdMutableResponse(statusRequest);
        }

        /*
         * Split the original request up into individual decision requests
         */
        StdIndividualDecisionRequestGenerator stdIndividualDecisionRequestGenerator	= new StdIndividualDecisionRequestGenerator(this.scopeResolver, pepRequest);
        /*
         * Determine if we are combining multiple results into a single result
         */
        boolean bCombineResults	= pepRequest.getCombinedDecision();
        StdMutableResult stdResultCombined	= null;

        /*
         * Iterate over all of the individual decision requests and process them, combining them into the final response
         */
        StdMutableResponse stdResponse	= new StdMutableResponse();
        Iterator<Request> iterRequestsIndividualDecision	= stdIndividualDecisionRequestGenerator.getIndividualDecisionRequests();
        if (iterRequestsIndividualDecision == null || !iterRequestsIndividualDecision.hasNext()) {
            return new StdMutableResponse(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR, "No individual decision requests"));
        }

        while (iterRequestsIndividualDecision.hasNext()) {
            Request requestIndividualDecision	= iterRequestsIndividualDecision.next();
            if (traceEngineThis.isTracing()) {
                traceEngineThis.trace(new StdTraceEvent<Request>("Individual Request", this, requestIndividualDecision));
            }
            Result resultIndividualDecision		= null;
            if (requestIndividualDecision.getStatus() != null && !requestIndividualDecision.getStatus().isOk()) {
                resultIndividualDecision	= new StdMutableResult(requestIndividualDecision.getStatus());
            } else {
                EvaluationContext evaluationContext	= this.evaluationContextFactory.getEvaluationContext(requestIndividualDecision);
                if (evaluationContext == null) {
                    resultIndividualDecision	= RESULT_ECTX_NULL;
                } else {
                    resultIndividualDecision	= this.processRequest(evaluationContext);
                }
            }

            assert(resultIndividualDecision != null);
            if (traceEngineThis.isTracing()) {
                traceEngineThis.trace(new StdTraceEvent<Result>("Individual Result", this, resultIndividualDecision));
            }
            if (bCombineResults) {
                Decision decision	= resultIndividualDecision.getDecision();
                Status status		= resultIndividualDecision.getStatus();
                if (resultIndividualDecision.getAssociatedAdvice().size() > 0) {
                    decision	= Decision.INDETERMINATE;
                    status		= STATUS_ADVICE_NA;
                } else if (resultIndividualDecision.getObligations().size() > 0) {
                    decision	= Decision.INDETERMINATE;
                    status		= STATUS_OBLIGATIONS_NA;
                }

                if (stdResultCombined == null) {
                    stdResultCombined	= new StdMutableResult(decision, status);
                } else {
                    if (stdResultCombined.getDecision() != resultIndividualDecision.getDecision()) {
                        stdResultCombined.setDecision(Decision.INDETERMINATE);
                        stdResultCombined.setStatus(STATUS_COMBINE_FAILED);
                    }
                }
                stdResultCombined.addPolicyIdentifiers(resultIndividualDecision.getPolicyIdentifiers());
                stdResultCombined.addPolicySetIdentifiers(resultIndividualDecision.getPolicySetIdentifiers());
                stdResultCombined.addAttributeCategories(resultIndividualDecision.getAttributes());
                if (traceEngineThis.isTracing()) {
                    traceEngineThis.trace(new StdTraceEvent<Result>("Combined result", this, stdResultCombined));
                }
            } else {
                stdResponse.add(resultIndividualDecision);
            }
        }

        if (bCombineResults) {
            stdResponse.add(stdResultCombined);
        }
        return stdResponse;
    }

    @Override
    public Collection<URI> getProfiles() {
        return Collections.unmodifiableCollection(PROFILES);
    }

    @Override
    public boolean hasProfile(URI uriProfile) {
        return PROFILES.contains(uriProfile);
    }

    @Override
    public String getTraceId() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public Traceable getCause() {
        return null;
    }
}
