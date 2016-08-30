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

package org.apache.openaz.pepapi.std.test;

import org.apache.openaz.pepapi.*;
import org.apache.openaz.pepapi.std.StdPepAgentFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TestAPI {

    private PepAgentFactory pepAgentFactory;

    @Before
    public void setup() {
        pepAgentFactory = new StdPepAgentFactory("properties/testapi.xacml.properties");
    }

    /**
     *
     */
    @Test
    public void testPepAgent() {
        Assert.assertNotNull(getPepAgent());
    }

    /**
     *
     */
    @Test
    public void testPermit() {
        PepResponse response = getPepAgent().simpleDecide("Julius Hibbert", "read",
                                                          "http://medico.com/record/patient/BartSimpson");
        Assert.assertNotNull(response);
        Assert.assertEquals(true, response.allowed());
    }

    /**
     *
     */
    @Test
    public void testPermitWithLocationMatch() {
        Subject subject = Subject.newInstance("Bob");
        Action action = Action.newInstance("read");
        Resource resource = Resource.newInstance(URI.create("/record/patient/Alice"))
                .withLocation(URI.create("http://medical-records.com/"));
        PepResponse response = getPepAgent().decide(subject, action, resource);
        Assert.assertNotNull(response);
        Assert.assertEquals(true, response.allowed());
    }

    /**
     *
     */
    @Test
    public void testPermitWithLocationMismatch() {
        Subject subject = Subject.newInstance("Bob");
        Action action = Action.newInstance("read");
        Resource resource = Resource.newInstance(URI.create("/record/patient/Alice"))
                .withLocation(URI.create("http://restricted-records.com/"));
        PepResponse response = getPepAgent().decide(subject, action, resource);
        Assert.assertNotNull(response);
        Assert.assertEquals(false, response.allowed());
    }

    /**
     *
     */
    @Test
    public void testNotApplicable() {
        PepResponse response = getPepAgent().simpleDecide("Julius Hibbert", "read",
                                                          "http://medico.com/record/patient/JohnSmith");
        Assert.assertNotNull(response);
        Assert.assertEquals(false, response.allowed());
    }

    /**
     *
     */
    @Test
    public void testMultiRequest() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(Action.newInstance("read"));
        actions.add(Action.newInstance("write"));
        actions.add(Action.newInstance("update"));
        actions.add(Action.newInstance("delete"));

        List<PepResponse> responses = getPepAgent()
            .bulkDecide(actions, Subject.newInstance("Julius Hibbert"),
                        Resource.newInstance("http://medico.com/record/patient/BartSimpson"));
        Assert.assertNotNull(responses);
        Assert.assertEquals(true, responses.get(0).allowed());
        Assert.assertEquals(true, responses.get(1).allowed());
        Assert.assertEquals(false, responses.get(2).allowed());
        Assert.assertEquals(false, responses.get(3).allowed());

    }

    public PepAgent getPepAgent() {
        return pepAgentFactory.getPepAgent();
    }
}
