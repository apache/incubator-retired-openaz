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

public class TestConfig {

    private PepAgentFactory pepAgentFactory;

    @Before
    public void setup() {
        pepAgentFactory = new StdPepAgentFactory("properties/testconfig.xacml.properties");
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
    public void testPermitWithDefaultsMatch() {
        Subject subject = Subject.newInstance();
        Action action = Action.newInstance();
        Resource resource = Resource.newInstance();
        PepResponse response = getPepAgent().decide(subject, action, resource);
        Assert.assertNotNull(response);
        Assert.assertEquals(true, response.allowed());
    }

    /**
     *
     */
    @Test
    public void testPermitWithDefaultsMismatch() {
        Subject subject = Subject.newInstance("non-default-subject-id");
        Action action = Action.newInstance("non-default-action-id");
        Resource resource = Resource.newInstance("non-default-resource-id");
        PepResponse response = getPepAgent().decide(subject, action, resource);
        Assert.assertNotNull(response);
        Assert.assertEquals(false, response.allowed());
    }

    public PepAgent getPepAgent() {
        return pepAgentFactory.getPepAgent();
    }
}
