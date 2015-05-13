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

import java.net.URI;
import java.util.UUID;

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.util.ObjUtil;

/**
 * IdentifierImpl provides a common implementation of the {@link org.apache.openaz.xacml.api.Identifier}
 * interface with a stored, fixed <code>URI</code>.
 */
public class IdentifierImpl implements Identifier {
    private URI uri;

    /**
     * Creates a new <code>IdentifierImpl</code> with the given <code>URI</code> id.
     *
     * @param uriIn the <code>URI</code> for the identifier
     */
    public IdentifierImpl(URI uriIn) {
        if (uriIn == null) {
            throw new IllegalArgumentException("Null URI");
        }
        this.uri = uriIn;
    }

    /**
     * Creates a new <code>IdentifierImp</code> with the given <code>String</code> id.
     *
     * @param idIn the <code>String</code> for the category id
     */
    public IdentifierImpl(String idIn) {
        this(URI.create(idIn));
    }

    public IdentifierImpl(Identifier identifierBase, String id) {
        this(URI.create(identifierBase.stringValue() + ":" + id));
    }

    public static Identifier gensym(String pfx) {
        UUID uuid = UUID.randomUUID();
        return new IdentifierImpl(pfx + ":" + uuid.toString());
    }

    public static Identifier gensym() {
        return gensym("urn:gensym");
    }

    @Override
    public URI getUri() {
        return this.uri;
    }

    @Override
    public String toString() {
        return this.stringValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof Identifier)) {
            return false;
        } else {
            return ObjUtil.equalsAllowNull(this.getUri(), ((Identifier)obj).getUri());
        }
    }

    @Override
    public int hashCode() {
        URI thisURI = this.getUri();
        return (thisURI == null ? super.hashCode() : thisURI.hashCode());
    }

    @Override
    public String stringValue() {
        URI thisURI = this.getUri();
        return (thisURI == null ? null : thisURI.toString());
    }
}
