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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.pepapi.*;


public final class ArrayMapper implements ObjectMapper {

    private static final Log logger = LogFactory.getLog(ArrayMapper.class);

    private PepConfig pepConfig;

    private MapperRegistry mapperRegistry;

    @Override
    public Class<Object[]> getMappedClass() {
        return Object[].class;
    }

    @Override
    public void map(Object o, PepRequest pepRequest) {
        Object[] array = (Object[])o;
        if(array != null && array.length > 0) {
            ObjectMapper mapper = mapperRegistry.getMapper(array[0].getClass());
            if(mapper != null) {
                for(Object item: array) {
                    mapper.map(item, pepRequest);
                }
            } else {
                logger.error("Can't map an Object of class: " + array[0].getClass().getName());
                throw new PepException("Can't map an Object of class: " + array[0].getClass().getName());
            }
        }
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
