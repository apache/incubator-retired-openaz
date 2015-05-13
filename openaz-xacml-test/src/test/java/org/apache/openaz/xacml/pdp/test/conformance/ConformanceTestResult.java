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
package org.apache.openaz.xacml.pdp.test.conformance;

import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.Response;

/**
 * ConformanceTestResult holds all of the objects for a single conformance test run.
 */
public class ConformanceTestResult {
    private ConformanceTest conformanceTest;
    private Request request;
    private Response expectedResponse;
    private Response actualResponse;
    private ResponseMatchResult responseMatchResult;
    private Exception error;

    // performance timings
    private long firstCallTime;
    private long averageTotalLoopTime;

    // how many non-first-call times the decide() was called
    private int iterations;

    public ConformanceTestResult(ConformanceTest conformanceTestIn, int iterations) {
        this.conformanceTest = conformanceTestIn;
        this.iterations = iterations;
    }

    public ConformanceTestResult(ConformanceTest conformanceTestIn, Exception errorIn) {
        this.conformanceTest = conformanceTestIn;
        this.error = errorIn;
    }

    public int getIterations() {
        return this.iterations;
    }

    public ConformanceTest getConformanceTest() {
        return this.conformanceTest;
    }

    public void setConformanceTest(ConformanceTest conformanceTestIn) {
        this.conformanceTest = conformanceTestIn;
    }

    public Request getRequest() {
        return this.request;
    }

    public void setRequest(Request requestIn) {
        this.request = requestIn;
    }

    public Response getExpectedResponse() {
        return this.expectedResponse;
    }

    public void setExpectedResponse(Response response) {
        this.expectedResponse = response;
        this.responseMatchResult = null;
    }

    public Response getActualResponse() {
        return this.actualResponse;
    }

    public void setActualResponse(Response response) {
        this.actualResponse = response;
        this.responseMatchResult = null;
    }

    public ResponseMatchResult getResponseMatchResult() {
        if (this.responseMatchResult == null
            && (this.actualResponse != null && this.expectedResponse != null)) {
            this.computeResponseMatchResult();
        }
        return this.responseMatchResult;
    }

    public void computeResponseMatchResult() {
        if (this.expectedResponse != null && this.actualResponse != null) {
            this.responseMatchResult = ResponseMatchResult.newInstance(this.expectedResponse,
                                                                       this.actualResponse);
        }
    }

    public Exception getError() {
        return this.error;
    }

    public void setError(Exception ex) {
        this.error = ex;
    }

    public long getFirstCallTime() {
        return firstCallTime;
    }

    public void setFirstCallTime(long t) {
        firstCallTime = t;
    }

    public long getAverageTotalLoopTime() {
        return averageTotalLoopTime;
    }

    public void setAverageTotalLoopTime(long t) {
        averageTotalLoopTime = t;
    }

}
