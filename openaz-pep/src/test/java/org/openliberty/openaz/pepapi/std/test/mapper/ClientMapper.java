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

package org.openliberty.openaz.pepapi.std.test.mapper;

import com.att.research.xacml.api.XACML3;
import org.openliberty.openaz.pepapi.*;

public class ClientMapper implements ObjectMapper {

        private MapperRegistry mapperRegistry;

        private PepConfig pepConfig;
        
        @Override
        public Class<?> getMappedClass() {
                return Client.class;
        }

        @Override
        public void map(Object o, PepRequest pepRequest) {
                Client c = (Client)o;
                PepRequestAttributes resAttributes = pepRequest.getPepRequestAttributes(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE);
                resAttributes.addAttribute("jpmc:client:name", c.getName());
                resAttributes.addAttribute("jpmc:client:country-of-domicile", c.getCountryOfDomicile());
        }

        @Override
        public void setMapperRegistry(MapperRegistry mapperRegistry) {
                this.mapperRegistry = mapperRegistry;
        }

        @Override
        public void setPepConfig(PepConfig pepConfig) {
                this.pepConfig = pepConfig;
        }
}
