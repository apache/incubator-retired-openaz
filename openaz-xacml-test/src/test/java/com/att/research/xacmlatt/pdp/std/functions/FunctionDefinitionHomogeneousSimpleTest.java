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
package com.att.research.xacmlatt.pdp.std.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.att.research.xacml.api.Status;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.policy.Bag;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;
import com.att.research.xacmlatt.pdp.policy.FunctionArgumentAttributeValue;
import com.att.research.xacmlatt.pdp.policy.FunctionArgumentBag;

/**
 * FunctionDefinitionHomogeneousSimple is an abstract class, so we have to test it by creating a sub-class.
 * The constructor is tested by default when an instance of the sub-class is created for other tests. Each of
 * these functions needs to be tested for each type of function to be sure the values are correct, so this is
 * just a simple test to see that the mechanism works. TO RUN - use jUnit In Eclipse select this file or the
 * enclosing directory, right-click and select Run As/JUnit Test
 */
public class FunctionDefinitionHomogeneousSimpleTest {

    @Test
    public void testGetDataTypeArgs() {

        // test a simple instance using the Equality class
        FunctionDefinitionEquality<String> fd = new FunctionDefinitionEquality<String>(
                                                                                       XACML3.ID_FUNCTION_STRING_EQUAL,
                                                                                       DataTypes.DT_STRING);
        assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeArgs().getId());
    }

    @Test
    public void testGetNumArgs() {
        // test a simple instance using the Equality class
        FunctionDefinitionEquality<String> fd = new FunctionDefinitionEquality<String>(
                                                                                       XACML3.ID_FUNCTION_STRING_EQUAL,
                                                                                       DataTypes.DT_STRING);
        assertEquals(new Integer(2), fd.getNumArgs());
    }

    @Test
    public void testValidateArguments() {
        // create some arguments to use later
        FunctionArgumentAttributeValue stringAttr1 = null;
        FunctionArgumentAttributeValue stringAttr2 = null;
        FunctionArgumentAttributeValue stringAttr3 = null;
        FunctionArgumentAttributeValue intAttr = null;
        try {
            stringAttr1 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("abc"));
            stringAttr2 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("def"));
            stringAttr3 = new FunctionArgumentAttributeValue(DataTypes.DT_STRING.createAttributeValue("ghi"));
            intAttr = new FunctionArgumentAttributeValue(DataTypes.DT_INTEGER.createAttributeValue(1));
        } catch (Exception e) {
            fail("creating attribute e=" + e);
        }

        FunctionDefinitionEquality<String> fd = new FunctionDefinitionEquality<String>(
                                                                                       XACML3.ID_FUNCTION_STRING_EQUAL,
                                                                                       DataTypes.DT_STRING);
        List<String> convertedValues = new ArrayList<String>();
        List<FunctionArgument> listFunctionArguments = new ArrayList<FunctionArgument>();

        // test correct # of args, both of them strings
        listFunctionArguments.add(stringAttr1);
        listFunctionArguments.add(stringAttr2);
        Status status = fd.validateArguments(listFunctionArguments, convertedValues);
        assertTrue(status.isOk());
        assertEquals(convertedValues.size(), 2);

        // test too few args
        listFunctionArguments.remove(1);
        status = fd.validateArguments(listFunctionArguments, convertedValues);
        assertFalse(status.isOk());
        assertEquals("Expected 2 arguments, got 1", status.getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", status.getStatusCode()
            .getStatusCodeValue().stringValue());

        // test too many args
        listFunctionArguments.add(stringAttr2);
        listFunctionArguments.add(stringAttr3);
        status = fd.validateArguments(listFunctionArguments, convertedValues);
        assertFalse(status.isOk());
        assertEquals("Expected 2 arguments, got 3", status.getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", status.getStatusCode()
            .getStatusCodeValue().stringValue());

        // test with null arg
        listFunctionArguments.clear();
        listFunctionArguments.add(null);
        listFunctionArguments.add(stringAttr1);
        status = fd.validateArguments(listFunctionArguments, convertedValues);
        assertFalse(status.isOk());
        assertEquals("Got null argument at arg index 0", status.getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", status.getStatusCode()
            .getStatusCodeValue().stringValue());

        // test function that takes 0 args
        // TODO test with func that specifies 0 args? ASSUME for now that there are no such functions since a
        // function needs to operate on something
        // fail("need to test function with 0 args and various inputs - see validateArguments code");

        // test with one is a bag
        listFunctionArguments.clear();
        listFunctionArguments.add(stringAttr1);
        Bag bag = new Bag();
        FunctionArgument bagArg = new FunctionArgumentBag(bag);
        listFunctionArguments.add(bagArg);
        status = fd.validateArguments(listFunctionArguments, convertedValues);
        assertFalse(status.isOk());
        assertEquals("Expected a simple value, saw a bag at arg index 1", status.getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", status.getStatusCode()
            .getStatusCodeValue().stringValue());

        // test with string and int
        listFunctionArguments.clear();
        listFunctionArguments.add(stringAttr1);
        listFunctionArguments.add(intAttr);
        status = fd.validateArguments(listFunctionArguments, convertedValues);
        assertFalse(status.isOk());
        assertEquals("Expected data type 'string' saw 'integer' at arg index 1", status.getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", status.getStatusCode()
            .getStatusCodeValue().stringValue());
    }

}
