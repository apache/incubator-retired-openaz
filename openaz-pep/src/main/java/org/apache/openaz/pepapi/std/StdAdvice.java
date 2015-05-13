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

package org.apache.openaz.pepapi.std;

import org.apache.openaz.pepapi.Advice;
import org.apache.openaz.xacml.api.AttributeAssignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class StdAdvice implements Advice {

    private org.apache.openaz.xacml.api.Advice wrappedAdvice;

    StdAdvice(org.apache.openaz.xacml.api.Advice advice) {
        this.wrappedAdvice = advice;
    }

    /**
     * Return the Id for this Advice.
     *
     * @return a string containing the Id of this Advice
     */
    @Override
    public String getId() {
        return wrappedAdvice.getId().stringValue();
    }

    @Override
    public Map<String, Object[]> getAttributeMap() {
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        for (AttributeAssignment a : wrappedAdvice.getAttributeAssignments()) {
            String attributeId = a.getAttributeId().stringValue();
            List<Object> values = map.get(attributeId);
            if (values == null) {
                values = new ArrayList<Object>();
                map.put(attributeId, values);
            }
            values.add(a.getAttributeValue().getValue());
        }
        Map<String, Object[]> attributeMap = new HashMap<String, Object[]>();
        for (Map.Entry<String, List<Object>> e : map.entrySet()) {
            attributeMap.put(e.getKey(), e.getValue().toArray(new Object[1]));
        }
        return attributeMap;
    }
}
