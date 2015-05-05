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

package com.att.research.xacmlatt.pdp.std.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.att.research.xacmlatt.pdp.policy.Bag;
import com.att.research.xacmlatt.pdp.policy.ExpressionResult;
import com.att.research.xacmlatt.pdp.policy.FunctionArgument;
import com.att.research.xacmlatt.pdp.policy.FunctionArgumentBag;
import com.att.research.xacmlatt.pdp.std.StdFunctions;

/**
 * Test of PDP Functions (See XACML core spec section A.3) TO RUN - use jUnit In Eclipse select this file or
 * the enclosing directory, right-click and select Run As/JUnit Test
 */
public class FunctionDefinitionBagOneAndOnlyTest {

    /*
     * variables useful in the following tests
     */
    List<FunctionArgument> arguments = new ArrayList<FunctionArgument>();

    @Test
    public void testString() {
        String v1 = new String("abc");
        String v2 = new String("def");
        BigInteger vOtherType = BigInteger.valueOf(11);

        Bag bag0 = new Bag();
        Bag bag1 = new Bag();
        bag1.add(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), v1));
        Bag bag2 = new Bag();
        bag2.add(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), v1));
        bag2.add(new StdAttributeValue<String>(DataTypes.DT_STRING.getId(), v2));
        Bag bagOtherType = new Bag();
        bagOtherType.add(new StdAttributeValue<BigInteger>(DataTypes.DT_INTEGER.getId(), vOtherType));

        FunctionArgumentBag attrBag0 = new FunctionArgumentBag(bag0);
        FunctionArgumentBag attrBag1 = new FunctionArgumentBag(bag1);
        FunctionArgumentBag attrBag2 = new FunctionArgumentBag(bag2);
        FunctionArgumentBag attrBagOtherType = new FunctionArgumentBag(bagOtherType);

        FunctionDefinitionBagOneAndOnly<?> fd = (FunctionDefinitionBagOneAndOnly<?>)StdFunctions.FD_STRING_ONE_AND_ONLY;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_STRING_ONE_AND_ONLY, fd.getId());
        assertEquals(DataTypes.DT_STRING.getId(), fd.getDataTypeId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertFalse(fd.returnsBag());

        // bag with only one
        arguments.clear();
        arguments.add(attrBag1);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        assertEquals(String.class, res.getValue().getValue().getClass());
        String resValue = (String)res.getValue().getValue();
        assertEquals(v1, resValue);

        // null bag
        arguments.clear();
        arguments.add(null);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:string-one-and-only Got null argument", res.getStatus().getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());

        // bag with exactly one but of other type in it
        arguments.clear();
        arguments.add(attrBagOtherType);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:string-one-and-only Element in bag of wrong type. Expected string got integer",
                     res.getStatus().getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());

        // bag with none
        arguments.clear();
        arguments.add(attrBag0);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:string-one-and-only Expected 1 but Bag has 0 elements", res.getStatus()
            .getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());

        // bag with multiple
        arguments.clear();
        arguments.add(attrBag2);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:string-one-and-only Expected 1 but Bag has 2 elements", res.getStatus()
            .getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());
    }

    @Test
    public void testBoolean() {
        Boolean v1 = new Boolean(true);
        Boolean v2 = new Boolean(false);
        BigInteger vOtherType = BigInteger.valueOf(11);

        Bag bag0 = new Bag();
        Bag bag1 = new Bag();
        bag1.add(new StdAttributeValue<Boolean>(DataTypes.DT_BOOLEAN.getId(), v1));
        Bag bag2 = new Bag();
        bag2.add(new StdAttributeValue<Boolean>(DataTypes.DT_BOOLEAN.getId(), v1));
        bag2.add(new StdAttributeValue<Boolean>(DataTypes.DT_BOOLEAN.getId(), v2));
        Bag bagOtherType = new Bag();
        bagOtherType.add(new StdAttributeValue<BigInteger>(DataTypes.DT_STRING.getId(), vOtherType));

        FunctionArgumentBag attrBag0 = new FunctionArgumentBag(bag0);
        FunctionArgumentBag attrBag1 = new FunctionArgumentBag(bag1);
        FunctionArgumentBag attrBag2 = new FunctionArgumentBag(bag2);
        FunctionArgumentBag attrBagOtherType = new FunctionArgumentBag(bagOtherType);

        FunctionDefinitionBagOneAndOnly<?> fd = (FunctionDefinitionBagOneAndOnly<?>)StdFunctions.FD_BOOLEAN_ONE_AND_ONLY;

        // check identity and type of the thing created
        assertEquals(XACML3.ID_FUNCTION_BOOLEAN_ONE_AND_ONLY, fd.getId());
        assertEquals(DataTypes.DT_BOOLEAN.getId(), fd.getDataTypeId());

        // just to be safe... If tests take too long these can probably be eliminated
        assertFalse(fd.returnsBag());

        // bag with only one
        arguments.clear();
        arguments.add(attrBag1);
        ExpressionResult res = fd.evaluate(null, arguments);
        assertTrue(res.isOk());
        assertEquals(Boolean.class, res.getValue().getValue().getClass());
        Boolean resValue = (Boolean)res.getValue().getValue();
        assertEquals(new Boolean(true), resValue);

        // null bag
        arguments.clear();
        arguments.add(null);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:boolean-one-and-only Got null argument", res.getStatus().getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());

        // bag with exactly one but of other type in it
        arguments.clear();
        arguments.add(attrBagOtherType);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:boolean-one-and-only Element in bag of wrong type. Expected boolean got string",
                     res.getStatus().getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());

        // bag with none
        arguments.clear();
        arguments.add(attrBag0);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:boolean-one-and-only Expected 1 but Bag has 0 elements", res.getStatus()
            .getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());

        // bag with multiple
        arguments.clear();
        arguments.add(attrBag2);
        res = fd.evaluate(null, arguments);
        assertFalse(res.isOk());
        assertEquals("function:boolean-one-and-only Expected 1 but Bag has 2 elements", res.getStatus()
            .getStatusMessage());
        assertEquals("urn:oasis:names:tc:xacml:1.0:status:processing-error", res.getStatus().getStatusCode()
            .getStatusCodeValue().stringValue());
    }

    //
    //
    // REST OF DATA TYPES OMITTED
    // because they "should" all work the same
    //
    //

}
