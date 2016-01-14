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

package org.apache.openaz.xacml.admin.converters;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.admin.jpa.ConstraintValue;
import com.vaadin.data.util.converter.Converter;

public class ConstraintValueConverter implements Converter<Object, ConstraintValue> {
	private static final long serialVersionUID = 1L;
	private static final Log logger	= LogFactory.getLog(ConstraintValueConverter.class);
	
	@Override
	public ConstraintValue convertToModel(Object value,
			Class<? extends ConstraintValue> targetType, Locale locale)
			throws Converter.ConversionException {
		if (logger.isTraceEnabled()) {
			logger.trace("convertToModel:" + value + " target " + targetType);
		}
		ConstraintValue newValue = new ConstraintValue();
		if (value == null) {
			return newValue;
		}
		// PLD TODO?
		return newValue;
	}

	@Override
	public Object convertToPresentation(ConstraintValue value,
			Class<? extends Object> targetType, Locale locale)
			throws Converter.ConversionException {
		if (logger.isTraceEnabled()) {
			logger.trace("convertToPresentation:" + value + " target " + targetType);
		}
		if (value == null) {
			return null;
		}
		return value.getProperty();
	}

	@Override
	public Class<ConstraintValue> getModelType() {
		return ConstraintValue.class;
	}

	@Override
	public Class<Object> getPresentationType() {
		return Object.class;
	}

}
