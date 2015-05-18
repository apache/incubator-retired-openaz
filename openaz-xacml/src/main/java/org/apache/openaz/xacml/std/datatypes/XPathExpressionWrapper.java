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
package org.apache.openaz.xacml.std.datatypes;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * XPathExpressionWrapper implements the {@link javax.xml.xpath.XPathExpression} interface to wrap another
 * <code>XPathExpression</code> and keep the path expression that was used to create it.
 */
public class XPathExpressionWrapper implements XPathExpression {
    private XPathExpression xpathExpressionWrapped;
    private String path;
    private ExtendedNamespaceContext namespaceContext;
    private Status status;

    public XPathExpressionWrapper(ExtendedNamespaceContext namespaceContextIn, String pathIn) {
        this.namespaceContext = namespaceContextIn;
        this.path = pathIn;
    }

    public XPathExpressionWrapper(Document documentIn, String pathIn) {
        this(new NodeNamespaceContext(documentIn), pathIn);
        if (pathIn == null || pathIn.length() == 0) {
            throw new IllegalArgumentException("XPathExpression must have XPath value");
        }
    }

    public XPathExpressionWrapper(String pathIn) {
        this((ExtendedNamespaceContext)null, pathIn);
    }

    public XPathExpressionWrapper(Node node) {
        this(node.getOwnerDocument(), node.getTextContent());
    }

    public XPathExpressionWrapper(XPathExpression xpathExpression) {
        this.xpathExpressionWrapped = xpathExpression;
    }

    public synchronized XPathExpression getXpathExpressionWrapped() {
        if (this.xpathExpressionWrapped == null && (this.getStatus() == null || this.getStatus().isOk())) {
            String thisPath = this.getPath();
            if (thisPath != null) {
                XPath xPath = XPathFactory.newInstance().newXPath();
                NamespaceContext namespaceContextThis = this.getNamespaceContext();
                if (namespaceContextThis != null) {
                    xPath.setNamespaceContext(namespaceContextThis);
                }
                try {
                    this.xpathExpressionWrapped = xPath.compile(thisPath);
                } catch (XPathExpressionException ex) {
                    this.status = new StdStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR,
                                                "Error compiling XPath " + thisPath + ": " + ex.getMessage());
                }
            }
        }
        return this.xpathExpressionWrapped;
    }

    public String getPath() {
        return this.path;
    }

    public ExtendedNamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }

    public Status getStatus() {
        return this.status;
    }

    @Override
    public Object evaluate(Object item, QName returnType) throws XPathExpressionException {
        XPathExpression thisXPathExpression = this.getXpathExpressionWrapped();
        return (thisXPathExpression == null ? null : thisXPathExpression.evaluate(item, returnType));
    }

    @Override
    public String evaluate(Object item) throws XPathExpressionException {
        XPathExpression thisXPathExpression = this.getXpathExpressionWrapped();
        return (thisXPathExpression == null ? null : thisXPathExpression.evaluate(item));
    }

    @Override
    public Object evaluate(InputSource source, QName returnType) throws XPathExpressionException {
        XPathExpression thisXPathExpression = this.getXpathExpressionWrapped();
        return (thisXPathExpression == null ? null : thisXPathExpression.evaluate(source, returnType));
    }

    @Override
    public String evaluate(InputSource source) throws XPathExpressionException {
        XPathExpression thisXPathExpression = this.getXpathExpressionWrapped();
        return (thisXPathExpression == null ? null : thisXPathExpression.evaluate(source));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof XPathExpressionWrapper)) {
            return false;
        }
        XPathExpressionWrapper other = (XPathExpressionWrapper)o;
        return this.path.equals(other.path);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (path != null) {
            result = 31 * result + path.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("path=" + this.path);
        // the document is not printed by toString, but what we really want from it is the Namespace
        // attributes
        sb.append(",Namespace=" + this.namespaceContext);
        sb.append(",status=" + this.status);
        sb.append(",xpathExpressionWrapped="
                  + ((this.xpathExpressionWrapped == null) ? "null" : "(XpathExpression object)"));
        sb.append("}");
        return sb.toString();
    }
}
