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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * NodeNamespaceContext extends {@link javax.xml.namespace.NamespaceContext} by delegating to the owning
 * {@link org.w3c.dom.Document}.
 */
public class NodeNamespaceContext extends ExtendedNamespaceContext {
    private Document document;

    public NodeNamespaceContext(Document documentIn) {
        this.document = documentIn;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            return this.document.lookupNamespaceURI(null);
        } else {
            return this.document.lookupNamespaceURI(prefix);
        }
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return this.document.lookupPrefix(namespaceURI);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }

    @Override
    public Iterator<String> getAllPrefixes() {
        NamedNodeMap attributes = document.getDocumentElement().getAttributes();
        List<String> prefixList = new ArrayList<String>();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node node = attributes.item(i);
            if (node.getNodeName().startsWith("xmlns")) {
                // this is a namespace definition
                int index = node.getNodeName().indexOf(":");
                if (node.getNodeName().length() < index + 1) {
                    index = -1;
                }
                if (index < 0) {
                    // default namespace
                    prefixList.add(XMLConstants.DEFAULT_NS_PREFIX);
                } else {
                    String prefix = node.getNodeName().substring(index + 1);
                    prefixList.add(prefix);
                }
            }
        }
        return prefixList.iterator();
    }

}
