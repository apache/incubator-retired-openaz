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

import org.apache.openaz.pepapi.*;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.std.StdMutableRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

final class StdPepRequest implements PepRequest {

    private static final String REQUEST_ATTR_ID_PREFIX = "attributes";

    private final StdMutableRequest wrappedRequest;

    private final Map<Identifier, PepRequestAttributes> pepRequestAttributesMapByCategory;

    private final MapperRegistry mapperRegistry;

    private final PepConfig pepConfig;

    private final Object[] requestObjects;

    private final AtomicInteger idCounter;

    static StdPepRequest newInstance(PepConfig pepConfig, MapperRegistry mapperRegistry,
                                     Object[] requestObjects) {
        StdPepRequest stdPepRequest = new StdPepRequest(pepConfig, mapperRegistry, requestObjects);
        stdPepRequest.map();
        return stdPepRequest;
    }

    /**
     * @return
     */
    private String generateRequestAttributesXmlId() {
        return REQUEST_ATTR_ID_PREFIX + idCounter.getAndIncrement();
    }

    private StdPepRequest(PepConfig pepConfig, MapperRegistry mapperRegistry, Object[] requestObjects) {
        this.pepConfig = pepConfig;
        this.mapperRegistry = mapperRegistry;
        this.requestObjects = requestObjects;
        this.pepRequestAttributesMapByCategory = new HashMap<Identifier, PepRequestAttributes>();
        this.idCounter = new AtomicInteger(1);
        this.wrappedRequest = new StdMutableRequest();
    }

    @Override
    public PepRequestAttributes getPepRequestAttributes(Identifier categoryIdentifier) {
        PepRequestAttributes pepRequestAttributes = pepRequestAttributesMapByCategory.get(categoryIdentifier);
        if (pepRequestAttributes == null) {
            String xmlId = generateRequestAttributesXmlId();
            StdPepRequestAttributes p = new StdPepRequestAttributes(xmlId, categoryIdentifier);
            p.setIssuer(pepConfig.getIssuer());
            pepRequestAttributes = p;
            pepRequestAttributesMapByCategory.put(categoryIdentifier, pepRequestAttributes);
            wrappedRequest.add(pepRequestAttributes.getWrappedRequestAttributes());
        }
        return pepRequestAttributes;
    }

    private void map() {
        if (requestObjects == null) {
            throw new IllegalArgumentException("One or more arguments are null");
        }
        for (Object o : requestObjects) {
            if (o == null) {
                throw new IllegalArgumentException("One or more arguments are null");
            }
            ObjectMapper mapper = mapperRegistry.getMapper(o.getClass());
            if (mapper == null) {
                throw new IllegalArgumentException("No mappers found for class: " + o.getClass().getName());
            }
            mapper.map(o, this);
        }
    }

    @Override
    public Request getWrappedRequest() {
        return wrappedRequest;
    }

}
