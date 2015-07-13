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
package org.apache.openaz.xacml.pdp.policy.dom;

import java.io.File;
import java.io.InputStream;

import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.PolicyDef;
import org.apache.openaz.xacml.pdp.policy.PolicySet;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * DOMPolicyDef extends {@link org.apache.openaz.xacml.pdp.policy.PolicyDef} with methods for loading them
 * from a <code>File</code>.
 */
public abstract class DOMPolicyDef {
    protected DOMPolicyDef() {
    }

    protected static PolicyDef newInstance(Document document, PolicySet policySetParent)
        throws DOMStructureException {
        PolicyDef policyDef = null;
        try {
            Node rootNode = DOMUtil.getFirstChildElement(document);
            if (rootNode == null) {
                throw new DOMStructureException("No child in document");
            }

            if (DOMUtil.isInNamespace(rootNode, XACML3.XMLNS)) {
                if (XACML3.ELEMENT_POLICY.equals(rootNode.getLocalName())) {
                    policyDef = DOMPolicy.newInstance(rootNode, policySetParent, null);
                    if (policyDef == null) {
                        throw new DOMStructureException("Failed to parse Policy");
                    }
                } else if (XACML3.ELEMENT_POLICYSET.equals(rootNode.getLocalName())) {
                    policyDef = DOMPolicySet.newInstance(rootNode, policySetParent, null);
                    if (policyDef == null) {
                        throw new DOMStructureException("Failed to parse PolicySet");
                    }
                } else {
                    throw DOMUtil.newUnexpectedElementException(rootNode);
                }
            } else {
                throw DOMUtil.newUnexpectedElementException(rootNode);
            }
        } catch (Exception ex) {
            throw new DOMStructureException("Exception parsing Policy: " + ex.getMessage(), ex);
        }
        return policyDef;
    }

    public static PolicyDef load(InputStream inputStream) throws DOMStructureException {
        PolicyDef policyDef = null;
        try {
            Document document = DOMUtil.loadDocument(inputStream);
            if (document == null) {
                throw new DOMStructureException("Null document returned");
            }
            policyDef = newInstance(document, null);
        } catch (Exception ex) {
            throw new DOMStructureException("Exception loading Policy from input stream: " + ex.getMessage(),
                                            ex);
        }
        return policyDef;
    }

    /**
     * Creates a new <code>PolicyDef</code> derived object by loading the given <code>File</code> containing a
     * XACML 3.0 Policy or PolicySet.
     *
     * @param filePolicy the <code>File</code> containing the XACML Policy or PolicySet
     * @return the newly created <code>PolicyDef</code>
     * @throws DOMStructureException if there is an error loading the <code>PolicyDef</code>
     */
    public static PolicyDef load(File filePolicy) throws DOMStructureException {
        /*
         * Parse the XML file
         */
        PolicyDef policyDef = null;
        try {
            Document document = DOMUtil.loadDocument(filePolicy);
            if (document == null) {
                throw new DOMStructureException("Null document returned");
            }
            policyDef = newInstance(document, null);
        } catch (Exception ex) {
            throw new DOMStructureException("Exception loading Policy file \"" + filePolicy.getAbsolutePath()
                                            + "\": " + ex.getMessage(), ex);
        }
        return policyDef;
    }
}
