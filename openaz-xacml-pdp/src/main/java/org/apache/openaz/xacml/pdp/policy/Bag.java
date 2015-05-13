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
package org.apache.openaz.xacml.pdp.policy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.AttributeValue;

/**
 * Bag represents a collection of XACML attribute values for the same attribute.
 */
public class Bag {
    public static final Bag EMPTY = new Bag();

    private List<AttributeValue<?>> attributeValues = new ArrayList<AttributeValue<?>>();

    /**
     * Gets the <code>List</code> of <code>AttributeValue</code>s for this <code>Bag</code>.
     *
     * @return the <code>List</code> of <code>AttributeValue</code>s for this <code>Bag</code>
     */
    public List<AttributeValue<?>> getAttributeValueList() {
        return this.attributeValues;
    }

    /**
     * Creates a new, empty <code>Bag</code>.
     */
    public Bag() {
    }

    /**
     * Creates a new <code>Bag</code> by copying the {@link org.apache.openaz.xacml.api.AttributeValue}s from
     * the given <code>Collection</code>.
     *
     * @param attributeValuesIn the <code>Collection</code> of <code>AttributeValue</code>s for this
     *            <code>Bag</code>. public Bag(Collection<AttributeValue<?>> attributeValuesIn) { if
     *            (attributeValuesIn != null) { this.attributeValues.addAll(attributeValuesIn); } } public
     *            Bag(Iterator<AttributeValue<?>> iterAttributeValuesIn) { if (iterAttributeValuesIn != null)
     *            { while (iterAttributeValuesIn.hasNext()) {
     *            this.attributeValues.add(iterAttributeValuesIn.next()); } } }
     */

    /**
     * Adds an <code>AttributeValue</code> to this <code>Bag</code>>
     *
     * @param attributeValue the <code>AttributeValue</code> to add
     */
    public void add(AttributeValue<?> attributeValue) {
        this.attributeValues.add(attributeValue);
    }

    /**
     * Gets the number of <code>AttributeValue</code>s in this <code>Bag</code>.
     *
     * @return the number of <code>AttributeValue</code>s in this <code>Bag</code>.
     */
    public int size() {
        return this.getAttributeValueList().size();
    }

    /**
     * Gets an <code>Iterator</code> over all of the <code>AttributeValue</code>s in this <code>Bag</code>.
     *
     * @return an <code>Iterator</code> over all of the <code>AttributeValue</code>s in this <code>Bag</code>.
     */
    public Iterator<AttributeValue<?>> getAttributeValues() {
        return this.getAttributeValueList().iterator();
    }

}
