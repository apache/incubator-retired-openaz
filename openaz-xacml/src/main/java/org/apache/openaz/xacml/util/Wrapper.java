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
package org.apache.openaz.xacml.util;

/**
 * Defines an object that delegates its <code>equals</code>, <code>hashCode</code> and <code>toString</code>
 * methods to a wrapped object.
 *
 * @param <T> the Class of the wrapped object
 */
public class Wrapper<T> {
    private T wrappedObject;

    /**
     * Gets the <code>T</code> wrapped object.
     *
     * @return the <code>T</code> wrapped object.
     */
    protected T getWrappedObject() {
        return this.wrappedObject;
    }

    /**
     * Creates a new <code>Wrapper</code> around the given <code>T</code> object.
     *
     * @param wrappedObjectIn the <code>T</code> wrapped object.
     */
    public Wrapper(T wrappedObjectIn) {
        this.wrappedObject = wrappedObjectIn;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else {
            return this.wrappedObject.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return this.wrappedObject.hashCode();
    }

    @Override
    public String toString() {
        return this.wrappedObject.toString();
    }
}
