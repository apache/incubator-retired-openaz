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

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.std.datatypes;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;

import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.DataType;
import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.dom.DOMUtil;

/**
 * DataTypeBase provides common implementation components of the {@link com.att.research.xacml.api.DataType} interface
 * that is used by the specific instance implementations.
 * 
 * @author car
 * @version $Revision: 1.1 $
 *
 * @param <T> the value type for conversion
 */
public abstract class DataTypeBase<T> implements DataType<T> {
	private Identifier	id;
	private Class<T>	classConvert;
	
	protected DataTypeBase(Identifier identifierIn, Class<T> classConvertIn) {
		this.id				= identifierIn;
		this.classConvert	= classConvertIn;
	}
	
	protected Class<T> getClassConvert() {
		return this.classConvert;
	}
	
	private Object collapse(Collection<?> objects) throws DataTypeException {
		if (objects == null || objects.size() == 0) {
			return "";
		} else if (objects.size() == 1) {
			return objects.iterator().next();
		} else {
			Iterator<?>			iterObjects	= objects.iterator();
			StringBuilder		stringBuilder	= new StringBuilder();
			while (iterObjects.hasNext()) {
				stringBuilder.append(this.convertToString(iterObjects.next()));
			}
			return stringBuilder.toString();
		}
		
	}
	
	/**
	 * This is a common utility method to handle <code>String</code> conversion that can be
	 * used by all derived <code>DataTypeBase</code> classes.
	 * 
	 * @param obj the <code>Object</code> to convert to a <code>String</code>
	 * @return the <code>String</code> representation of the <code>Object</code>
	 */
	protected String convertToString(Object obj) throws DataTypeException {
		if (obj instanceof String) {
			return (String)obj;
		} else if (this.getClassConvert().isInstance(obj)) {
			return this.toStringValue(this.getClassConvert().cast(obj));
		} else if (obj instanceof Node) {
			return ((Node)obj).getTextContent();
		} else if (obj instanceof List) {
			return this.convertToString(this.collapse((Collection<?>)obj));
		} else {
			return obj.toString();
		}
	}

	@Override
	public Identifier getId() {
		return this.id;
	}
	
	@Override
	public AttributeValue<T> createAttributeValue(Object source) throws DataTypeException {
		/*
		 * If the given Object is a DOM Node, get the XPathCategory from it and use it to
		 * create the StdAttributeValue.
		 */
		if (source != null && (source instanceof Node)) {
			Identifier xpathCategory	= null;
			try {
				xpathCategory	= DOMUtil.getIdentifierAttribute((Node)source, XACML3.ATTRIBUTE_XPATHCATEGORY, false);
			} catch (Exception ex) {
				// TODO:
			}
			return new StdAttributeValue<T>(this.getId(), this.convert(source), xpathCategory);
		} else {
			return new StdAttributeValue<T>(this.getId(), this.convert(source));
		}
	}
	
	@Override
	public AttributeValue<T> createAttributeValue(Object source, Identifier xpathCategory) throws DataTypeException {
		return new StdAttributeValue<T>(this.getId(), this.convert(source), xpathCategory);
	}

	@Override
	public AttributeValue<T> convertAttributeValue(AttributeValue<?> attributeValue) throws DataTypeException {
		if (attributeValue == null) {
			return null;
		} else {
			return new StdAttributeValue<T>(this.getId(), this.convert(attributeValue.getValue()), attributeValue.getXPathCategory());
		}
	}
	
	@Override
	public String toStringValue(T source) throws DataTypeException {
		return (source == null ? null : source.toString());
	}

}
