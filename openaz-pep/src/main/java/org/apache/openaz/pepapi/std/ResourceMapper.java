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

import java.net.URI;

public class ResourceMapper extends CategoryContainerMapper {

    public ResourceMapper() {
        super(Resource.class);
    }

    @Override
    public void map(Object o, PepRequest pepRequest) {
        Resource resource = (Resource) o;
        Object id = resource.getId();
        if (id == null) {
            id = getPepConfig().getDefaultResourceId();

            if (id != null) {
                PepRequestAttributes resourceAttributes = pepRequest
                        .getPepRequestAttributes(resource.getCategoryIdentifier());
                if (id instanceof String)
                    resourceAttributes.addAttribute(Resource.DEFAULT_IDENTIFIER_ID.stringValue(), (String) id);
                else if (id instanceof URI)
                    resourceAttributes.addAttribute(Resource.DEFAULT_IDENTIFIER_ID.stringValue(), (URI) id);
                else
                    throw new IllegalStateException("resource id is not an instance of String nor java.net.URI but " +
                            resource.getClass().getName());
            }
        }
        super.map(o, pepRequest);
    }
}
