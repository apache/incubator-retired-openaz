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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.pepapi.*;
import org.apache.openaz.pepapi.std.StdPepAgentFactory;
import org.apache.openaz.pepapi.std.test.mapper.BusinessRequestContext;
import org.apache.openaz.pepapi.std.test.mapper.Client;
import org.apache.openaz.pepapi.std.test.mapper.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestMapper {

    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(TestMapper.class);

    private PepAgentFactory pepAgentFactory;

    @Before
    public void setup() {
        this.pepAgentFactory = new StdPepAgentFactory("/properties/testmapper.xacml.properties");
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
        Subject subject = Subject.newInstance("John Smith");
        subject.addAttribute("urn:oasis:names:tc:xacml:1.0:subject:role-id", "ROLE_DOCUMENT_WRITER");

        Action action = Action.newInstance("write");

        Document doc = new Document(1, "OnBoarding Document", "ABC Corporation", "John Smith");
        PepResponse response = getPepAgent().decide(subject, action, doc);
        Assert.assertNotNull(response);
        Assert.assertEquals(true, response.allowed());
    }

    /**
     *
     */
    @Test
    public void testNotApplicable() {
        Subject subject = Subject.newInstance("John Smith");
        subject.addAttribute("urn:oasis:names:tc:xacml:1.0:subject:role-id", "ROLE_DOCUMENT_WRITER");

        Action action = Action.newInstance("write");
        Document doc = new Document(2, "OnBoarding Document", "XYZ Corporation", "Jim Doe");
        PepResponse response = getPepAgent().decide(subject, action, doc);
        Assert.assertNotNull(response);
        Assert.assertEquals(false, response.allowed());
    }

    @Test(expected = PepException.class)
    public void testMix() {
        Subject subject = Subject.newInstance("John Smith");
        subject.addAttribute("urn:oasis:names:tc:xacml:1.0:subject:role-id", "ROLE_DOCUMENT_WRITER");

        Action action = Action.newInstance("write");

        Document doc1 = new Document(1, "OnBoarding Document", "ABC Corporation", "John Smith");
        Document doc2 = new Document(2, "OnBoarding Document", "XYZ Corporation", "Jim Doe");
        List<Object> resourceList = new ArrayList<Object>();
        resourceList.add(doc1);
        resourceList.add(doc2);

        PepResponse response = getPepAgent().decide(subject, action, resourceList);
        Assert.assertNotNull(response);
        response.allowed();
    }

    @Test
    public void testVarArgsPermit() {
        Subject subject = Subject.newInstance("John Smith");
        subject.addAttribute("urn:oasis:names:tc:xacml:1.0:subject:role-id", "ROLE_DOCUMENT_READER");
        BusinessRequestContext bc = new BusinessRequestContext("USA", "05:00 EST");

        Action action = Action.newInstance("read");
        List<Object> resources = new ArrayList<Object>();
        resources.add(new Document(1, "OnBoarding Document", "XYZ Corporation", "Jim Doe"));
        resources.add(new Client("XYZ Corporation", "USA"));

        PepResponse response = getPepAgent().decide(subject, action, resources, bc);
        Assert.assertNotNull(response);
        Assert.assertEquals(true, response.allowed());
    }

    @Test
    public void testVarArgsDeny() {
        Subject subject = Subject.newInstance("John Smith");
        subject.addAttribute("urn:oasis:names:tc:xacml:1.0:subject:role-id", "ROLE_DOCUMENT_READER");
        BusinessRequestContext bc = new BusinessRequestContext("INDIA", "05:00 IST");

        List<Object> resources = new ArrayList<Object>();
        resources.add(new Document(2, "OnBoarding Document", "XYZ Corporation", "Jim Doe"));
        resources.add(new Client("XYZ Corporation", "USA"));

        Action action = Action.newInstance("write");

        PepResponse response = getPepAgent().decide(subject, action, resources, bc);
        Assert.assertNotNull(response);
        Assert.assertEquals(false, response.allowed());
    }

    public PepAgent getPepAgent() {
        return pepAgentFactory.getPepAgent();
    }
}
