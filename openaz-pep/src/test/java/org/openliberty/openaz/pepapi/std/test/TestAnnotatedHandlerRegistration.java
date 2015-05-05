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

package org.openliberty.openaz.pepapi.std.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openliberty.openaz.pepapi.*;
import org.openliberty.openaz.pepapi.std.StdPepAgentFactory;
import org.openliberty.openaz.pepapi.std.test.obligation.AnnotatedCatchAllObligationHandler;
import org.openliberty.openaz.pepapi.std.test.obligation.AnnotatedFilteringObligationHandler;
import org.openliberty.openaz.pepapi.std.test.obligation.AnnotatedRedactionObligationHandler;

public class TestAnnotatedHandlerRegistration {

    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(TestAnnotatedHandlerRegistration.class);

    private PepAgentFactory pepAgentFactory;

    // TODO: Need to wire
    private AnnotatedFilteringObligationHandler filterHandler;

    // TODO: Need to wire
    private AnnotatedRedactionObligationHandler redactionHandler;

    // TODO: Need to wire
    private AnnotatedCatchAllObligationHandler catchAllHandler;

    @Before
    public void setup() {
        pepAgentFactory = new StdPepAgentFactory("xacml.properties");
    }

    /**
     *
     */
    @Ignore
    @Test
    public void testPepAgent() {
        Assert.assertNotNull(getPepAgent());
    }

    /**
     *
     */
    @Ignore
    @Test
    public void testRegistration() {
        Subject subject = Subject.newInstance("John Smith");
        subject.addAttribute("urn:oasis:names:tc:xacml:1.0:subject:age", "45");
        PepResponse response = getPepAgent().decide(subject, Action.newInstance("view"),
                                                    Resource.newInstance("resource1"));
        Assert.assertNotNull(response);
        Assert.assertEquals(true, response.allowed());
        filterHandler.enforce();
        redactionHandler.enforce();
        catchAllHandler.enforce();
    }

    public PepAgent getPepAgent() {
        return pepAgentFactory.getPepAgent();
    }

}
