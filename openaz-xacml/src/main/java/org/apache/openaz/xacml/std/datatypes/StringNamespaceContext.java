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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

/**
 * StringNamespaceContext extends {@link javax.xml.namespace.NamespaceContext} by keeping the Namespace
 * definitions as Strings. This NamespaceContext applies to only a single scope and therefore can have at most
 * one default unbound (i.e. with no Prefix) Namespace. All other Namespaces in this context must have a
 * prefix.
 */
public class StringNamespaceContext extends ExtendedNamespaceContext {

    // structure to hold the namespace info, which may or may not include a prefix
    private class Namespace {
        private String prefix;
        private String namespace;

        // if we make this a first-class object, do not let people create it without args
        // private Namespace(){}

        /**
         * Create a new Namespace with only a namespaceURI
         * 
         * @param namespace
         */
        public Namespace(String namespace) {
            this.prefix = null;
            this.namespace = namespace;
        }

        /**
         * Create a new Namespace with both prefix and namespace URI
         * 
         * @param prefix
         * @param namespace
         */
        public Namespace(String prefix, String namespace) {
            this.prefix = prefix;
            this.namespace = namespace;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public String getNamespace() {
            return this.namespace;
        }

        @Override
        public String toString() {
            return "{" + prefix + "," + namespace + "}";
        }
    }

    /*
     * The access methods make it simpler to store Namespaces that have prefixes (which is all of them except
     * the default, if any) in a Map
     */
    private Map<String, Namespace> namespaceMap = new HashMap<String, Namespace>();

    private Namespace defaultNamespace = null;

    /**
     * Basic constructor
     */
    public StringNamespaceContext() {
    }

    /**
     * Constructor with a single entry consisting of only a Namespace URI without a Prefix (ie. the default
     * namespace)
     * 
     * @param namespaceURI
     * @throws Exception
     */
    public StringNamespaceContext(String namespaceURI) throws Exception {
        this.add(namespaceURI);
    }

    /**
     * Constructor with a single entry consisting of both a prefix and a namespace URI.
     *
     * @param prefix
     * @param namespaceURI
     * @throws Exception
     */
    public StringNamespaceContext(String prefix, String namespaceURI) throws Exception {
        this.add(prefix, namespaceURI);
    }

    /**
     * Add a default Namespace to the list using just the Namespace URI (without a prefix).
     *
     * @param namespaceURI
     * @throws Exception
     */
    public void add(String namespaceURI) throws Exception {
        if (defaultNamespace != null) {
            // caller is trying to replace the default namespace in this scope.
            // This probably indicates a mistake, so we do not allow it.
            // If we find that this is allowed, just delete this if block.
            throw new Exception("Default name already set");
        }
        // This allows multiple instances of the same URI with different (or no) prefixes
        defaultNamespace = new Namespace(namespaceURI);
    }

    /**
     * Add a Namespace to the list with both a Prefix and the Namespace URI.
     *
     * @param prefix
     * @param namespace
     * @throws Exception
     */
    public void add(String prefix, String namespace) throws Exception {
        // if prefix is missing or "", this is really the default namespace, so add it using the other method
        if (prefix == null || prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            this.add(namespace);
            return;
        }
        // prefix is non-null
        if (namespaceMap.get(prefix) != null) {
            throw new Exception("Namespace prefix '" + prefix + "' already in use (value='"
                                + namespaceMap.get(prefix) + "'");
        }
        namespaceMap.put(prefix, new Namespace(prefix, namespace));
    }

    //
    // Methods implementing NamespaceContext interface
    //

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        } else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            if (defaultNamespace == null) {
                return XMLConstants.NULL_NS_URI;
            } else {
                return defaultNamespace.getNamespace();
            }
        } else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            return XMLConstants.XML_NS_URI;
        } else if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        } else if (namespaceMap.get(prefix) == null) {
            return XMLConstants.NULL_NS_URI;
        } else {
            return namespaceMap.get(prefix).getNamespace();
        }
    }

    @Override
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        if (defaultNamespace != null && namespaceURI.equals(defaultNamespace.getNamespace())) {
            return XMLConstants.DEFAULT_NS_PREFIX;
        }
        if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        } else if (namespaceURI.equals(XMLConstants.XML_NS_URI)) {
            return XMLConstants.XML_NS_PREFIX;
        }
        // search the map looking for an entry that matches
        for (String key : namespaceMap.keySet()) {
            Namespace namespace = namespaceMap.get(key);
            if (namespace.getNamespace().equals(namespaceURI)) {
                return namespace.getPrefix();
            }
        }
        // if we get here then the URI was not in the map
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Iterator getPrefixes(String namespaceURI) {
        List<String> prefixList = new ArrayList<String>();
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        } else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            prefixList.add(XMLConstants.XMLNS_ATTRIBUTE);
        } else if (namespaceURI.equals(XMLConstants.XML_NS_URI)) {
            prefixList.add(XMLConstants.XML_NS_PREFIX);
        } else {
            if (defaultNamespace != null && defaultNamespace.getNamespace().equals(namespaceURI)) {
                prefixList.add(XMLConstants.DEFAULT_NS_PREFIX);
            }
            for (String key : namespaceMap.keySet()) {
                Namespace ns = namespaceMap.get(key);
                if (ns.getNamespace().equals(namespaceURI)) {
                    prefixList.add(ns.prefix);
                }
            }
        }

        return prefixList.iterator();
    }

    @Override
    public Iterator<String> getAllPrefixes() {
        // if the default namespace is not in use, just return the iterator for the prefixes in the map
        if (defaultNamespace == null) {
            return namespaceMap.keySet().iterator();
        }
        // we need to include the default namespace prefix in the iterator
        List<String> keyList = new ArrayList<String>();
        keyList.addAll(namespaceMap.keySet());
        keyList.add(XMLConstants.DEFAULT_NS_PREFIX);
        return keyList.iterator();
    }

}
