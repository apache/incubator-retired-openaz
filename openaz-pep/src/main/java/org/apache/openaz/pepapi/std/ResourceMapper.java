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

import org.apache.openaz.pepapi.PepRequest;
import org.apache.openaz.pepapi.PepRequestAttributes;
import org.apache.openaz.pepapi.Resource;
import org.apache.openaz.xacml.api.XACML3;

import java.net.URI;

public class ResourceMapper extends CategoryContainerMapper {

    public ResourceMapper() {
        super(Resource.class);
    }

    @Override
    public void map(Object o, PepRequest pepRequest) {
        Resource r = (Resource) o;
        Object id = r.getId();
        if (id == null) {
            id = getPepConfig().getDefaultResourceId();

            if (id != null) {
                PepRequestAttributes resourceAttributes = pepRequest
                        .getPepRequestAttributes(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE);
                if (id instanceof String)
                    resourceAttributes.addAttribute(XACML3.ID_RESOURCE_RESOURCE_ID.stringValue(), (String) id);
                else if (id instanceof URI)
                    resourceAttributes.addAttribute(XACML3.ID_RESOURCE_RESOURCE_ID.stringValue(), (URI) id);
                else
                    throw new IllegalStateException("resource id is not an instance of String nor java.net.URI but " +
                            r.getClass().getName());
            }
        }
        super.map(o, pepRequest);
    }
}
