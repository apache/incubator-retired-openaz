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

import org.apache.openaz.xacml.api.XACML3;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * DOMDocumentRepair provides methods for examining a DOM {@link org.w3c.dom.Document} for XACML document
 * types and doing repair on them. This class supports XACML Request and Response documents.
 */
public class DOMDocumentRepair {
    public static class UnsupportedDocumentTypeException extends Exception {
        private static final long serialVersionUID = -1845303652188504199L;

        public UnsupportedDocumentTypeException(String message) {
            super(message);
        }

        public UnsupportedDocumentTypeException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public DOMDocumentRepair() {
    }

    protected boolean repairRequest(Node nodeRequest) throws DOMStructureException {
        return DOMRequest.repair(nodeRequest);
    }

    protected boolean repairResponse(Node nodeResponse) throws DOMStructureException {
        return DOMResponse.repair(nodeResponse);
    }

    /**
     * Determines what kind of XACML document is represented by the given <code>Document</code> and attempts
     * to repair it.
     *
     * @param document the <code>Document</code> to check
     * @return true if any repairs were made in the <code>Document</code>, else false
     * @throws DOMStructureException if there were unrecoverable errors found
     * @throws org.apache.openaz.xacml.std.dom.DOMDocumentRepair.UnsupportedDocumentTypeException if the root
     *             element is not a XACML Request or Response.
     */
    public boolean repair(Document document) throws DOMStructureException, UnsupportedDocumentTypeException {
        Node firstChild = DOMUtil.getFirstChildElement(document);
        if (firstChild == null || !DOMUtil.isElement(firstChild)) {
            return false;
        }

        if (!DOMUtil.isInNamespace(firstChild, XACML3.XMLNS)) {
            throw new UnsupportedDocumentTypeException("Not a XACML document: "
                                                       + DOMUtil.getNodeLabel(firstChild));
        }
        if (XACML3.ELEMENT_REQUEST.equals(firstChild.getLocalName())) {
            return this.repairRequest(firstChild);
        } else if (XACML3.ELEMENT_RESPONSE.equals(firstChild.getLocalName())) {
            return this.repairResponse(firstChild);
        } else {
            throw new UnsupportedDocumentTypeException("Not a XACML Request or Response: "
                                                       + DOMUtil.getNodeLabel(firstChild));
        }
    }

}
