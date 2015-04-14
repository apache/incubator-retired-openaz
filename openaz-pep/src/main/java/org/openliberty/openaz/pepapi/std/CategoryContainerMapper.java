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

package org.openliberty.openaz.pepapi.std;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.*;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;


public class CategoryContainerMapper implements ObjectMapper {

	private static final Log logger = LogFactory.getLog(CategoryContainerMapper.class);

	private Class<?> mappedClass;

	private MapperRegistry mapperRegistry;

	private PepConfig pepConfig;

	public CategoryContainerMapper(Class<?> mappedClass) {
		this.mappedClass = mappedClass;
	}

	@Override
	public Class<?> getMappedClass() {
		return this.mappedClass;
	}

	@Override
	public void map(Object o, PepRequest pepRequest) {
		CategoryContainer a = (CategoryContainer)o;
		PepRequestAttributes pepRequestAttributes = pepRequest.getPepRequestAttributes(a.getCategoryIdentifier());
		Map<String, Object[]> aMap = a.getAttributeMap();
		if(aMap != null) {
			for(Entry<String, Object[]> e: aMap.entrySet()) {
				String attributeId = resolveAttributeId(e.getKey());
				Object[] values = e.getValue();
				if(values != null && values.length > 0) {
					map(pepRequestAttributes, attributeId, values);
				} else {
					logger.error("No value assigned for attribute : " + attributeId);
					throw new IllegalArgumentException("No or null value for attribute : " + attributeId);
				}
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

	protected String resolveAttributeId(String attributeId) {
		return attributeId;
	}

	private final void map(PepRequestAttributes pepRequestAttributes, String key, Object... values) {
		Object value = values[0];
		if (value instanceof String) {
        	pepRequestAttributes.addAttribute(key, (String[]) values);
        } else if (value instanceof Long) {
        	pepRequestAttributes.addAttribute(key, (Long[]) values);
        } else if (value instanceof Integer) {
        	pepRequestAttributes.addAttribute(key, (Integer[]) values);
        } else if (value instanceof Double) {
        	pepRequestAttributes.addAttribute(key, (Double[]) values);
        } else if (value instanceof Boolean) {
        	pepRequestAttributes.addAttribute(key, (Boolean[]) values);
        } else if (value instanceof URI) {
        	pepRequestAttributes.addAttribute(key, (URI[]) values);
        } else if (value instanceof Date) {
        	pepRequestAttributes.addAttribute(key, (Date[]) values);
        }else {
			logger.error("Type: " + value.getClass().getName() + " cannot be mapped for attribute: " + key);
        	throw new PepException("Can't map an object of class: " + value.getClass().getName());
        }
	}

	protected PepConfig getPepConfig() {
		return this.pepConfig;
	}

	protected MapperRegistry getMapperRegistry() {
		return this.mapperRegistry;
	}
}
