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
package org.apache.openaz.xacml.std;

import java.util.Collection;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.RequestAttributes;
import org.apache.openaz.xacml.util.ObjUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.RequestAttributes} interface.
 */
public class StdRequestAttributes extends StdAttributeCategory implements RequestAttributes {
    private Log logger = LogFactory.getLog(this.getClass());

    private Node contentRoot;
    private String xmlId;

    /**
     * Creates a new <code>StdRequestAttributes</code> with the given
     * {@link org.apache.openaz.xacml.api.Identifier} representing its XACML Category, the given
     * <code>Collection</code> of {@link org.apache.openaz.xacml.api.Attribute}s, the given
     * {@link org.w3c.dom.Node} representing the XACML Content element and the given <code>String</code> as
     * the optional xml:Id.
     *
     * @param identifierCategory the <code>Identifier</code> representing the XACML Category for the new
     *            <code>StdRequestAttributes</code>
     * @param listAttributes the <code>Collection</code> of <code>Attribute</code>s included in the new
     *            <code>StdRequestAttributes</code>
     * @param nodeContentRoot the <code>Node</code> representing the XACML Content element for the new
     *            <code>StdRequestAttributes</code>
     * @param xmlIdIn the <code>String</code> representing the xml:Id of the XACML Attributes element
     *            represented by this <code>StdRequestAttributes</code>
     */
    public StdRequestAttributes(Identifier identifierCategory, Collection<Attribute> listAttributes,
                                Node nodeContentRoot, String xmlIdIn) {
        super(identifierCategory, listAttributes);
        this.contentRoot = nodeContentRoot;
        this.xmlId = xmlIdIn;
    }

    /**
     * Creates a new <code>StdRequestAttributes</code> by copying the given
     * {@link org.apache.openaz.xacml.api.RequestAttributes}.
     *
     * @param requestAttributes the <code>RequestAttributes</code> to copy
     */
    public StdRequestAttributes(RequestAttributes requestAttributes) {
        super(requestAttributes);
        this.contentRoot = requestAttributes.getContentRoot();
        this.xmlId = requestAttributes.getXmlId();
    }

    @Override
    public String getXmlId() {
        return this.xmlId;
    }

    @Override
    public Node getContentRoot() {
        return this.contentRoot;
    }

    @Override
    public Node getContentNodeByXpathExpression(XPathExpression xpathExpression) {
        if (xpathExpression == null) {
            throw new NullPointerException("Null XPathExpression");
        }
        Node nodeRootThis = this.getContentRoot();
        if (nodeRootThis == null) {
            return null;
        }
        Node matchingNode = null;
        try {
            matchingNode = (Node)xpathExpression.evaluate(nodeRootThis, XPathConstants.NODE);
        } catch (XPathExpressionException ex) {
            this.logger.warn("Failed to retrieve node for \"" + xpathExpression.toString() + "\"", ex);
        }
        return matchingNode;
    }

    @Override
    public NodeList getContentNodeListByXpathExpression(XPathExpression xpathExpression) {
        if (xpathExpression == null) {
            throw new NullPointerException("Null XPathExpression");
        }
        Node nodeRootThis = this.getContentRoot();
        if (nodeRootThis == null) {
            return null;
        }
        NodeList matchingNodeList = null;
        try {
            matchingNodeList = (NodeList)xpathExpression.evaluate(nodeRootThis, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            this.logger.warn("Failed to retrieve nodelist for \"" + xpathExpression.toString() + "\"", ex);
        }
        return matchingNodeList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof RequestAttributes)) {
            return false;
        } else {
            RequestAttributes objRequestAttributes = (RequestAttributes)obj;
            // Content nodes need to be handled specially because content.equals(content) does not work unless
            // they are the same object, and
            // content.isEqualNode(content) only works if the nodes have identical newlines and spaces, which
            // is not what we care about.
            if (!ObjUtil.xmlEqualsAllowNull(this.getContentRoot(), objRequestAttributes.getContentRoot())) {
                return false;
            }

            return super.equals(objRequestAttributes)
                   && ObjUtil.equalsAllowNull(this.getXmlId(), objRequestAttributes.getXmlId());
        }
    }
    
    public int hashCode() {
        int result = 17;
        if (getContentRoot() != null) {
            result = 31 * result + getContentRoot().hashCode();
        }
        if (getXmlId() != null) {
            result = 31 * result + getXmlId().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        Object objectToDump;
        if ((objectToDump = this.getContentRoot()) != null) {
            stringBuilder.append(',');
            stringBuilder.append("contentRoot=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getXmlId()) != null) {
            stringBuilder.append(',');
            stringBuilder.append("xmlId=");
            stringBuilder.append((String)objectToDump);
        }

        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
