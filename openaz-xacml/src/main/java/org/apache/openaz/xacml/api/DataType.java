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

package org.apache.openaz.xacml.api;

/**
 * Defines the API for objects that represent XACML 3.0 data types.
 *
 * @param <T> the class of the java objects that represent the XACML values
 */
public interface DataType<T> {
    /**
     * Gets the {@link org.apache.openaz.xacml.api.Identifier} representing the XACML data type id for this
     * <code>DataType</code>.
     *
     * @return the <code>Identifier</code> representing the XACML data type id for this <code>DataType</code>.
     */
    Identifier getId();

    /**
     * Converts the given <code>Object</code> to a <code>T</code> object if possible. If the
     * <code>Object</code> is an instance of <code>T</code> then the object itself should be returned, cast to
     * <code>T</code>.
     *
     * @param source the source object to be converted
     * @return a <code>T</code> object
     * @throws UnsupportedOperationException if the given source object cannot be converted to a
     *             <code>T</code>
     */
    T convert(Object source) throws DataTypeException;

    /**
     * Converts the given <code>T</code> to a semantically meaningful <code>String</code>.
     *
     * @param source the source object to be converted
     * @return the semantically meaningful <code>String</code> representation of the <code>Object</code>
     * @throws DataTypeException if there is an error doing the conversion
     */
    String toStringValue(T source) throws DataTypeException;

    /**
     * Converts the given <code>Object</code> to a {@link org.apache.openaz.xacml.api.AttributeValue} whose
     * value is an instance of class <code>T</code>.
     *
     * @param source the source object to be converted
     * @return a new {@link org.apache.openaz.xacml.api.AttributeValue}
     * @throws UnsupportedOperationException if the given source object cannot be converted to a
     *             <code>T</code>
     */
    AttributeValue<T> createAttributeValue(Object source) throws DataTypeException;

    /**
     * Converts the given <code>Object</code> to a {@link org.apache.openaz.xacml.api.AttributeValue} whose
     * value is an instance of class <code>T</code>. If not null, the <code>xpathCategory</code> is used in
     * the newly created <code>AttributeValue</code>
     *
     * @param source the source object to be converted
     * @param xpathCategory the <code>Identifier</code> for the XPathCategory of the new
     *            <code>AttributeValue</code>
     * @return a new {@link org.apache.openaz.xacml.api.AttributeValue}
     * @throws UnsupportedOperationException if the given source object cannot be converted to a
     *             <code>T</code>
     */
    AttributeValue<T> createAttributeValue(Object source, Identifier xpathCategory) throws DataTypeException;

    /**
     * Converts the given {@link org.apache.openaz.xacml.api.AttributeValue} of an unknown data type to an
     * <code>AttributeValue</code> whose value is represented by an instance of class <code>T</code>.
     *
     * @param attributeValueFrom the <code>AttributeValue</code> to convert
     * @return an <code>AttributeValue</code> whose value is represented by an instance of class
     *         <code>T</code> if possible.
     * @throws UnsupportedOperationException
     */
    AttributeValue<T> convertAttributeValue(AttributeValue<?> attributeValueFrom) throws DataTypeException;
}
