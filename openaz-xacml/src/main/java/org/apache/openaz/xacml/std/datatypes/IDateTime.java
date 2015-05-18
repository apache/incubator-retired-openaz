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

/**
 * IDateTime is the minimal interface an object needs to support in order to be used in XACML functions that
 * expect a Date, DateTime, or Time.
 *
 * @param T the data type of the object implementing the interface
 */
public interface IDateTime<T> {
    /**
     * Adds the given <code>ISO8601Duration</code> to the <code>IDateTime</code> object.
     *
     * @param iso8601Duration the <code>ISO8601Duration</code> to add
     * @return a new <code>T</code> with the given <code>ISO8601Duration</code> added
     */
    T add(ISO8601Duration iso8601Duration);

    /**
     * Subtracts the given <code>ISO8601Duration</code> to the <code>IDateTime</code> object.
     *
     * @param iso8601Duration the <code>ISO8601Duration</code> to subtract
     * @return a new <code>T</code> with the given <code>ISO8601Duration</code> subtracted
     */
    T sub(ISO8601Duration iso8601Duration);
}
