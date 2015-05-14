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
package org.apache.openaz.xacml.std.pip.finders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.api.pip.PIPEngine;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.api.pip.PIPResponse;
import org.apache.openaz.xacml.std.pip.StdPIPResponse;
import org.apache.openaz.xacml.std.pip.engines.EnvironmentEngine;
import org.apache.openaz.xacml.std.pip.engines.RequestEngine;

/**
 * RequestFinder implements the {@link org.apache.openaz.xacml.api.pip.PIPFinder} interface by wrapping another
 * <code>PIPFinder</code> a {@link org.apache.openaz.xacml.std.pip.engines.RequestEngine} and a
 * {@link org.apache.openaz.xacml.std.pip.engines.EnvironmentEngine}. When attributes are requested, the
 * <code>RequestEngine</code> is searched first, followed by the <code>EnvironmentEngine</code> and if no
 * results are found, the wrapped <code>PIPFinder</code> is searched.
 */
public class RequestFinder extends WrappingFinder {
    private RequestEngine requestEngine;
    private EnvironmentEngine environmentEngine;
    private Map<PIPRequest, PIPResponse> mapCache = new HashMap<PIPRequest, PIPResponse>();

    protected RequestEngine getRequestEngine() {
        return this.requestEngine;
    }

    protected EnvironmentEngine getEnvironmentEngine() {
        return this.environmentEngine;
    }

    public RequestFinder(PIPFinder pipFinder, RequestEngine requestEngineIn) {
        super(pipFinder);
        this.requestEngine = requestEngineIn;
        this.environmentEngine = new EnvironmentEngine(new Date());
    }

    @Override
    protected PIPResponse getAttributesInternal(PIPRequest pipRequest, PIPEngine exclude,
                                                PIPFinder pipFinderRoot) throws PIPException {
        // long tStart = 0, tEnd = 0;
        /*
         * First try the RequestEngine
         */
        PIPResponse pipResponse = null;
        RequestEngine thisRequestEngine = this.getRequestEngine();
        Status status = null;
        if (thisRequestEngine != null && thisRequestEngine != exclude) {
            // tStart = System.nanoTime();
            pipResponse = thisRequestEngine.getAttributes(pipRequest, (pipFinderRoot == null
                ? this : pipFinderRoot));
            // tEnd = System.nanoTime();
            if (pipResponse.getStatus() == null || pipResponse.getStatus().isOk()) {
                /*
                 * We know how the RequestEngine works. It does not return multiple results and all of the
                 * results should match the request.
                 */
                if (pipResponse.getAttributes().size() > 0) {
                    return pipResponse;
                }
            } else {
                status = pipResponse.getStatus();
            }
        }

        /*
         * Next try the EnvironmentEngine if no issuer has been specified
         */
        if (XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT.equals(pipRequest.getCategory())
            && (pipRequest.getIssuer() == null || pipRequest.getIssuer().length() == 0)) {
            EnvironmentEngine thisEnvironmentEngine = this.getEnvironmentEngine();
            pipResponse = thisEnvironmentEngine.getAttributes(pipRequest, this);
            if (pipResponse.getStatus() == null || pipResponse.getStatus().isOk()) {
                /*
                 * We know how the EnvironmentEngine works. It does not return multiple results and all of
                 * the results should match the request.
                 */
                if (pipResponse.getAttributes().size() > 0) {
                    return pipResponse;
                }
            } else {
                if (status == null) {
                    status = pipResponse.getStatus();
                }
            }
        }

        /*
         * Try the cache
         */
        if (this.mapCache.containsKey(pipRequest)) {
            return this.mapCache.get(pipRequest);
        }

        /*
         * Delegate to the wrapped Finder
         */
        PIPFinder thisWrappedFinder = this.getWrappedFinder();
        if (thisWrappedFinder != null) {
            pipResponse = thisWrappedFinder.getAttributes(pipRequest, exclude, (pipFinderRoot == null
                ? this : pipFinderRoot));
            if (pipResponse != null) {
                if (pipResponse.getStatus() == null || pipResponse.getStatus().isOk()) {
                    if (pipResponse.getAttributes().size() > 0) {
                        /*
                         * Cache all of the returned attributes
                         */
                        Map<PIPRequest, PIPResponse> mapResponses = StdPIPResponse
                            .splitResponse(pipResponse);
                        if (mapResponses != null && mapResponses.size() > 0) {
                            for (PIPRequest pipRequestSplit : mapResponses.keySet()) {
                                this.mapCache.put(pipRequestSplit, mapResponses.get(pipRequestSplit));
                            }
                        }
                        return pipResponse;
                    }
                } else if (status == null || status.isOk()) {
                    status = pipResponse.getStatus();
                }
            }
        }

        /*
         * We did not get a valid, non-empty response back from either the Request or the wrapped
         * PIPFinder. If there was an error using the RequestEngine, use that as the status of the
         * response, otherwise return an empty response.
         */
        if (status != null && !status.isOk()) {
            return new StdPIPResponse(status);
        } else {
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        }
        // System.out.println("RequestFinder.getAttributesInternal() = " + (tEnd - tStart));
    }

    @Override
    public Collection<PIPEngine> getPIPEngines() {
        List<PIPEngine> engines = new ArrayList<PIPEngine>();
        if (this.requestEngine != null) {
            engines.add(this.requestEngine);
        }
        if (this.environmentEngine != null) {
            engines.add(this.environmentEngine);
        }
        PIPFinder wrappedFinder = this.getWrappedFinder();
        if (wrappedFinder != null) {
            engines.addAll(wrappedFinder.getPIPEngines());
        }
        return Collections.unmodifiableList(engines);
    }
}
