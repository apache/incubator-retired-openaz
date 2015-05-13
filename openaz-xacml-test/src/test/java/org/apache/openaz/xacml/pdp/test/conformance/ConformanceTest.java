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

import java.io.File;

/**
 * ConformanceTest represents a collection of XACML files with a root Policy document, optional referenced
 * Policy documents, a Request, and a Response.
 */
public class ConformanceTest {
    private String testName;
    private File request;
    private File response;
    private ConformanceRepository repository;

    public ConformanceTest(String name, ConformanceRepository conformanceRepository, File fileRequest,
                           File fileResponse) {
        this.testName = name;
        this.request = fileRequest;
        this.response = fileResponse;
        this.repository = conformanceRepository;
    }

    public ConformanceTest(String name) {
        this.testName = name;
    }

    public String getTestName() {
        return this.testName;
    }

    public void setTestName(String s) {
        this.testName = s;
    }

    public ConformanceRepository getRepository() {
        if (this.repository == null) {
            this.repository = new ConformanceRepository();
        }
        return this.repository;
    }

    public File getRequest() {
        return this.request;
    }

    public void setRequest(File f) {
        this.request = f;
    }

    public File getResponse() {
        return this.response;
    }

    public void setResponse(File f) {
        this.response = f;
    }

    public boolean isComplete() {
        return this.getTestName() != null && this.getRepository() != null
               && this.getRepository().hasRootPolicy() && this.getRequest() != null
               && this.getResponse() != null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean needColon = false;
        if (this.getTestName() != null) {
            stringBuilder.append(this.getTestName());
            needColon = true;
        }
        if (this.getRepository() != null) {

        }
        if (this.getRequest() != null) {
            if (needColon) {
                stringBuilder.append(':');
            }
            stringBuilder.append(this.getRequest().getName());
            needColon = true;
        }
        if (this.getResponse() != null) {
            if (needColon) {
                stringBuilder.append(':');
            }
            stringBuilder.append(this.getResponse().getName());
            needColon = true;
        }
        return stringBuilder.toString();
    }

}
