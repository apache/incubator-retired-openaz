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
import org.apache.openaz.xacml.api.RequestReference;
import org.apache.openaz.xacml.std.StdMutableRequest;
import org.apache.openaz.xacml.std.StdMutableRequestReference;
import org.apache.openaz.xacml.std.StdRequestAttributesReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
final class MultiRequest implements PepRequest {

    private static final String REQUEST_ATTR_ID_PREFIX = "attributes";

    private final Map<Identifier, PepRequestAttributes> pepRequestAttributesMapByCategory;

    private final MapperRegistry mapperRegistry;

    private final PepConfig pepConfig;

    private final Object[] sharedRequestObjects;

    private List<?> associations;

    private final AtomicInteger idCounter;

    private final StdMutableRequest wrappedRequest;

    private StdMutableRequestReference currentRequestReference;

    private RequestReference sharedRequestReference;

    static MultiRequest newInstance(PepConfig pepConfig, MapperRegistry mapperRegistry, List<?> associations,
                                    Object[] sharedRequestObjects) {
        MultiRequest m = new MultiRequest(pepConfig, mapperRegistry, associations, sharedRequestObjects);
        m.mapSharedRequestObjects();
        m.mapAssociations();
        return m;
    }

    private MultiRequest(PepConfig pepConfig, MapperRegistry mapperRegistry, List<?> associations,
                         Object[] sharedRequestObjects) {
        this.pepRequestAttributesMapByCategory = new HashMap<Identifier, PepRequestAttributes>();
        this.sharedRequestObjects = sharedRequestObjects;
        this.associations = associations;
        this.mapperRegistry = mapperRegistry;
        this.pepConfig = pepConfig;
        this.idCounter = new AtomicInteger(1);
        this.wrappedRequest = new StdMutableRequest();
        this.currentRequestReference = new StdMutableRequestReference();
    }

    private void mapSharedRequestObjects() {
        if (sharedRequestObjects == null) {
            throw new IllegalArgumentException("One or more arguments are null");
        }
        for (Object o : sharedRequestObjects) {
            if (o == null) {
                throw new IllegalArgumentException("One or more arguments are null");
            }
            ObjectMapper mapper = mapperRegistry.getMapper(o.getClass());
            if (mapper == null) {
                throw new IllegalArgumentException("No mappers found for class: " + o.getClass().getName());
            }
            mapper.map(o, this);
        }
        // Collect
        sharedRequestReference = currentRequestReference;
    }

    private void mapAssociations() {
        if (associations == null) {
            throw new IllegalArgumentException("One or more arguments are null");
        }
        for (Object association : associations) {
            if (association == null) {
                throw new IllegalArgumentException("One or more arguments are null");
            }

            // Prepare
            pepRequestAttributesMapByCategory.clear();
            currentRequestReference = new StdMutableRequestReference(
                                                                     sharedRequestReference
                                                                         .getAttributesReferences());
            wrappedRequest.add(currentRequestReference);

            // Map
            ObjectMapper mapper = mapperRegistry.getMapper(association.getClass());
            if (mapper == null) {
                throw new IllegalArgumentException("No mappers found for class: "
                                                   + association.getClass().getName());
            }
            mapper.map(association, this);
        }
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
            currentRequestReference.add(new StdRequestAttributesReference(xmlId));
        }
        return pepRequestAttributes;
    }

    private String generateRequestAttributesXmlId() {
        return REQUEST_ATTR_ID_PREFIX + idCounter.getAndIncrement();
    }

    @Override
    public Request getWrappedRequest() {
        return wrappedRequest;
    }
}
