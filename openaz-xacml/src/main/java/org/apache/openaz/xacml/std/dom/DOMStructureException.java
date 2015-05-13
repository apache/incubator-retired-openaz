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
package org.apache.openaz.xacml.std.dom;

import org.w3c.dom.Node;

public class DOMStructureException extends Exception {
    private static final long serialVersionUID = -3752478535859021127L;

    private Node nodeError;

    public DOMStructureException() {
    }

    public DOMStructureException(String message) {
        super(message);
    }

    public DOMStructureException(Throwable cause) {
        super(cause);
    }

    public DOMStructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public DOMStructureException(Node nodeErrorIn, String message, Throwable cause) {
        super(message, cause);
        this.nodeError = nodeErrorIn;
    }

    public DOMStructureException(Node nodeErrorIn, String message) {
        super(message);
        this.nodeError = nodeErrorIn;
    }

    public DOMStructureException(Node nodeErrorIn, Throwable cause) {
        super(cause);
        this.nodeError = nodeErrorIn;
    }

    public Node getNodeError() {
        return this.nodeError;
    }

}
