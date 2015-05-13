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
package org.apache.openaz.xacml.api.pip;

/**
 * PIPException extends <code>Exception</code> to represent errors that can occur as a result of querying a
 * {@link PIPEngine} for {@link org.apache.openaz.xacml.api.Attribute}s.
 */
public class PIPException extends Exception {
    private static final long serialVersionUID = -6656926395983776184L;
    private PIPRequest pipRequest;
    private PIPEngine pipEngine;

    public PIPException() {
    }

    public PIPException(String message) {
        super(message);
    }

    public PIPException(Throwable cause) {
        super(cause);
    }

    public PIPException(String message, Throwable cause) {
        super(message, cause);
    }

    public PIPException(PIPEngine pipEngineIn, PIPRequest pipRequestIn, String message, Throwable cause) {
        this(message, cause);
        this.pipEngine = pipEngineIn;
        this.pipRequest = pipRequestIn;
    }

    public PIPException(PIPEngine pipEngineIn, PIPRequest pipRequestIn, String message) {
        this(message);
        this.pipEngine = pipEngineIn;
        this.pipRequest = pipRequestIn;
    }

    /**
     * Gets the <code>PIPRequest</code> that caused this <code>PIPException</code>
     *
     * @return the <code>PIPRequest</code> that caused this <code>PIPException</code>
     */
    public PIPRequest getPIPRequest() {
        return this.pipRequest;
    }

    /**
     * Gets the <code>PIPEngine</code> that caused this <code>PIPException</code>.
     *
     * @return the <code>PIPEngine</code> that caused this <code>PIPException</code>
     */
    public PIPEngine getPIPEngine() {
        return this.pipEngine;
    }
}
