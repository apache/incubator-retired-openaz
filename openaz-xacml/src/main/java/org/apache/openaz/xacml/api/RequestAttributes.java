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

import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Defines the API for objects that represent XACML 3.0 Attributes elements within a Request by extending the
 * {@link AttributeCategory} interface with methods for accessing DOM {@link org.w3c.dom.Node}s representing
 * XACML 3.0 Content elements.
 */
public interface RequestAttributes extends AttributeCategory {
    /**
     * Gets the root {@link org.w3c.dom.Node} from the XACML 3.0 Content element of an Attributes element in a
     * Request.
     *
     * @return the root <code>Node</code> from the XACML 3.0 Content element of an Attributes element in a
     *         Request.
     */
    Node getContentRoot();

    /**
     * Finds the {@link org.w3c.dom.Node} referenced by the given {@link javax.xml.xpath.XPathExpression}
     * within the XACML Content element in this <code>RequestAttributes</code> object.
     *
     * @param xpathExpression the <code>XPathExpression</code> to apply to the Content element.
     * @return the <code>Node</code> returned by the given <code>XPathExpression</code> or null if not found.
     */
    Node getContentNodeByXpathExpression(XPathExpression xpathExpression);

    /**
     * Finds the {@link org.w3c.dom.NodeList} referenced by the given {@link javax.xml.xpath.XPathExpression}
     * within the XACML Content element in this <code>RequestAttributes</code> object.
     *
     * @param xpathExpression the <code>XPathExpression</code> to apply to the Content element.
     * @return the <code>NodeList</code> containing all <code>Node</code>s that match the
     *         <code>XPathExpression</code>
     */
    NodeList getContentNodeListByXpathExpression(XPathExpression xpathExpression);

    /**
     * Returns the <code>String</code> representing the xml:Id attribute for the XACML Attributes element
     * represented by this <code>RequestAttributes</code> object.
     *
     * @return the <code>String</code> representing the xml:Id attribute for the XACML Attributes element
     *         represented by this <code>RequestAttributes</code> object. May be null.
     */
    String getXmlId();

    /**
     * Implementations of the <code>RequestAttributes</code> interface must override the <code>equals</code>
     * method with the following semantics: Two <code>RequestAttributes</code> objects (<code>r1</code> and
     * <code>r2</code>) are equal if: {@code r1.super.equals(r2)} AND
     * {@code r1.getContentRoot() == null && r2.getContentRoot() == null} OR
     * {@code r1.getContentRoot().equals(r2.getContentRoot())} AND
     * {@code r1.getXmlId() == null && r2.getXmlId() == null} OR {@code r1.getXmlId().equals(r2.getXmlId())}
     */
    @Override
    boolean equals(Object obj);
}
