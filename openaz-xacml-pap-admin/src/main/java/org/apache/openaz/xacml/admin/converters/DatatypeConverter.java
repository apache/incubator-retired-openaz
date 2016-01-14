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

import org.apache.openaz.xacml.admin.jpa.Datatype;
import org.apache.openaz.xacml.api.Identifier;
import com.vaadin.data.util.converter.Converter;

public class DatatypeConverter implements Converter<Object, Datatype> {
	private static final long serialVersionUID = 1L;

	@Override
	public Datatype convertToModel(Object value,
			Class<? extends Datatype> targetType, Locale locale)
			throws Converter.ConversionException {
		Datatype datatype = new Datatype();
		if (value == null) {
			return datatype;
		}
		if (value instanceof Identifier) {
			datatype.setXacmlId(((Identifier)value).stringValue());
		} else {
			datatype.setXacmlId(value.toString());
		}
		return datatype;
	}

	@Override
	public Object convertToPresentation(Datatype value,
			Class<? extends Object> targetType, Locale locale)
			throws Converter.ConversionException {
		if (value == null) {
			return null;
		}
		if (targetType.isInstance(String.class) ||
			targetType.getName().equals(String.class.getName())) {
			return value.getXacmlId();
		}
		if (targetType.isInstance(Identifier.class) ||
			targetType.getName().equals(Identifier.class.getName())) {
			return value.getIdentifer();
		}
		return value.getIdentifer();
	}

	@Override
	public Class<Datatype> getModelType() {
		return Datatype.class;
	}

	@Override
	public Class<Object> getPresentationType() {
		return Object.class;
	}

}
