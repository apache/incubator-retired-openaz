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

package org.apache.openaz.pepapi.std.test.util;

import org.apache.openaz.pepapi.PepAgent;
import org.apache.openaz.pepapi.PepResponse;

import java.util.concurrent.Callable;

public class AzInvoker implements Callable<String> {

    private final PepAgent pepAgent;

    private final Object subject;

    private final Object action;

    private final Object resource;

    private final long sleepDuration;

    private final HasResult handler;

    public AzInvoker(PepAgent pepAgent, Object subject, Object action, Object resource, HasResult handler,
                     long sleepDuration) {
        this.pepAgent = pepAgent;
        this.subject = subject;
        this.action = action;
        this.resource = resource;
        this.handler = handler;
        this.sleepDuration = sleepDuration;
    }

    private String invoke() throws InterruptedException {
        PepResponse response = pepAgent.decide(subject, action, resource);
        if (response != null) {
            response.allowed();
        }
        Thread.sleep(this.sleepDuration);
        return handler.getResult();
    }

    @Override
    public String call() throws Exception {
        return invoke();
    }

    public long getSleepDuration() {
        return sleepDuration;
    }

    public HasResult getPep() {
        return handler;
    }
}
