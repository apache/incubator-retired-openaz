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

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.IdReference;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Version;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdIdReference;
import org.apache.openaz.xacml.std.StdVersion;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOMIdReference extends {@link org.apache.openaz.xacml.std.StdIdReference} with methods for creation from DOM
 * {@link org.w3c.dom.Node}s.
 */
public class DOMIdReference extends StdIdReference {
    private static Log logger = LogFactory.getLog(DOMIdReference.class);

    protected DOMIdReference(Identifier idReferenceIn, Version versionIn) {
        super(idReferenceIn, versionIn);
    }

    /**
     * Creates a new <code>IdReference</code> by parsing the given <code>Node</code> as a XACML
     * "IdReferenceType" element.
     *
     * @param nodeIdReference the <code>Node</code> to parse
     * @return a new <code>IDReference</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if the conversion cannot be made
     */
    public static IdReference newInstance(Node nodeIdReference) throws DOMStructureException {
        Element elementIdReference = DOMUtil.getElement(nodeIdReference);
        boolean bLenient = DOMProperties.isLenient();

        Identifier idReference = DOMUtil.getIdentifierContent(elementIdReference, !bLenient);

        String versionString = DOMUtil.getStringAttribute(elementIdReference, XACML3.ATTRIBUTE_VERSION);
        Version version = null;
        if (versionString != null) {
            try {
                version = StdVersion.newInstance(versionString);
            } catch (ParseException ex) {
                if (!bLenient) {
                    throw new DOMStructureException(nodeIdReference, "Invalid version \"" + versionString
                                                                     + "\" in \""
                                                                     + DOMUtil.getNodeLabel(nodeIdReference)
                                                                     + "\"");
                }
            }
        }

        return new DOMIdReference(idReference, version);
    }

    public static boolean repair(Node nodeIdReference) throws DOMStructureException {
        Element elementIdReference = DOMUtil.getElement(nodeIdReference);
        boolean result = false;

        result = DOMUtil.repairIdentifierContent(elementIdReference, logger) || result;

        String versionString = DOMUtil.getStringAttribute(elementIdReference, XACML3.ATTRIBUTE_VERSION);
        if (versionString != null) {
            try {
                StdVersion.newInstance(versionString);
            } catch (ParseException ex) {
                logger.warn("Deleting invalid Version string " + versionString, ex);
                elementIdReference.removeAttribute(XACML3.ATTRIBUTE_VERSION);
                result = true;
            }
        }

        return result;
    }
}
