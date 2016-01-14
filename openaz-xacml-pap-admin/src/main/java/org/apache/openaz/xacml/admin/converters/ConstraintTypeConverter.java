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

import org.apache.openaz.xacml.admin.jpa.ConstraintType;
import com.vaadin.data.util.converter.Converter;

public class ConstraintTypeConverter  implements Converter<Object, ConstraintType> {
	private static final long serialVersionUID = 1L;
	private static final Log logger	= LogFactory.getLog(ConstraintTypeConverter.class);

	@Override
	public ConstraintType convertToModel(Object value,
			Class<? extends ConstraintType> targetType, Locale locale)
			throws Converter.ConversionException {
		if (logger.isTraceEnabled()) {
			logger.trace("convertToModel:" + value + " target " + targetType);
		}
		ConstraintType constraintValue = new ConstraintType();
		if (value == null) {
			return constraintValue;
		}
		// PLD TODO??
		return constraintValue;
	}

	@Override
	public Object convertToPresentation(ConstraintType value,
			Class<? extends Object> targetType, Locale locale)
			throws Converter.ConversionException {
		if (logger.isTraceEnabled()) {
			logger.trace("convertToPresentation:" + value + " target " + targetType);
		}
		if (value == null) {
			return null;
		}
		if (targetType.isAssignableFrom(String.class)) {
			return value.getConstraintType();
		}
		if (targetType.isInstance(Integer.class)) {
			return value.getId();
		}
		return null;
	}

	@Override
	public Class<ConstraintType> getModelType() {
		return ConstraintType.class;
	}

	@Override
	public Class<Object> getPresentationType() {
		return Object.class;
	}

}
