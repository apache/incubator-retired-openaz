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

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * ExtendedNamespaceContext allows holders of {@link javax.xml.namespace.NamespaceContext} to iterate through
 * all elements within the Namespace, including the default. The Iterator returned by this contains the
 * Prefixes used in the Namespace (including the Default if it is defined). The caller can then use the
 * Iterator values to call getNamespaceURI(prefix) defined in {@link javax.xml.namespace.NamespaceContext}.
 */
public abstract class ExtendedNamespaceContext implements NamespaceContext {

    /**
     * Get an Iterator that returns all prefixes in use in the Namespace, including the default if defined.
     * The caller should use the returned values to call <code>getNamespaceURI(valueFromIterator)</code> to
     * get the Namespace URIs associated with the prefix.
     *
     * @return
     */
    public abstract Iterator<String> getAllPrefixes();

    @Override
    public String toString() {
        Iterator<String> prefixIt = this.getAllPrefixes();
        StringBuffer sb = new StringBuffer("{[");
        while (prefixIt.hasNext()) {
            String prefix = prefixIt.next();
            String namespaceUri = this.getNamespaceURI(prefix);

            sb.append("{");
            if (prefix == XMLConstants.DEFAULT_NS_PREFIX) {
                sb.append(namespaceUri);
            } else {
                sb.append(prefix + "," + namespaceUri);
            }
            sb.append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

}
