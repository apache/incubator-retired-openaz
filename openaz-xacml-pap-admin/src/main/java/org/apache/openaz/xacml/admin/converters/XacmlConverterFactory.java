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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.admin.jpa.Category;
import org.apache.openaz.xacml.admin.jpa.ConstraintType;
import org.apache.openaz.xacml.admin.jpa.ConstraintValue;
import org.apache.openaz.xacml.admin.jpa.Datatype;
import org.apache.openaz.xacml.api.Identifier;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

public class XacmlConverterFactory extends DefaultConverterFactory {
	private static final long serialVersionUID = 1L;
	private static final Log logger	= LogFactory.getLog(XacmlConverterFactory.class);
	
    @SuppressWarnings("unchecked")
	@Override
    public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL>
            createConverter(Class<PRESENTATION> presentationType,
                            Class<MODEL> modelType) {
    	if (logger.isTraceEnabled()) {
    		logger.trace("createConverter: " + presentationType + " from model " + modelType);
    	}
    	//
        // Handle one particular type conversion for Categories
    	//
        if (Category.class == modelType) {
            return (Converter<PRESENTATION, MODEL>) new CategoryConverter();
        }
        //
        // Handle one particular type conversion for Datatypes
        //
        if (Datatype.class == modelType) {
            return (Converter<PRESENTATION, MODEL>) new DatatypeConverter();
        }
        //
        // Handle one particular type conversion for ConstraintType
        //
        if (ConstraintType.class == modelType) {
        	return (Converter<PRESENTATION, MODEL>) new ConstraintTypeConverter();
        }
        //
        // Handle one particular type conversion for ConstraintType
        //
        if (ConstraintValue.class == modelType) {
        	return (Converter<PRESENTATION, MODEL>) new ConstraintValueConverter();
        }
        //
        // Handle one particular type conversion for Identifiers
        //
        if (Identifier.class == modelType) {
        	return (Converter<PRESENTATION, MODEL>) new IdentifierConverter();
        }
        //
        // Default to the supertype
        //
        return super.createConverter(presentationType,
                                     modelType);
    }

}
