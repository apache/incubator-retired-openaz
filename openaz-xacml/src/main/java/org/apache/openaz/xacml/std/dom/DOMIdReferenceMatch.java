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
import org.apache.openaz.xacml.api.IdReferenceMatch;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.VersionMatch;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdIdReferenceMatch;
import org.apache.openaz.xacml.std.StdVersionMatch;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOMIdReferenceMatch extends {@link org.apache.openaz.xacml.std.StdIdReferenceMatch} with methods for
 * creation from DOM {@link org.w3c.dom.Node}s.
 */
public class DOMIdReferenceMatch extends StdIdReferenceMatch {
    private static final Log logger = LogFactory.getLog(DOMIdReferenceMatch.class);

    protected DOMIdReferenceMatch(Identifier idIn, VersionMatch versionIn, VersionMatch earliestVersionIn,
                                  VersionMatch latestVersionIn) {
        super(idIn, versionIn, earliestVersionIn, latestVersionIn);
    }

    public static IdReferenceMatch newInstance(Node nodeIdReferenceMatch) throws DOMStructureException {
        Element elementIdReferenceMatch = DOMUtil.getElement(nodeIdReferenceMatch);
        boolean bLenient = DOMProperties.isLenient();

        Identifier idReferenceMatch = DOMUtil.getIdentifierContent(elementIdReferenceMatch, !bLenient);

        String versionString = DOMUtil.getStringAttribute(elementIdReferenceMatch, XACML3.ATTRIBUTE_VERSION);
        String versionEarliestString = DOMUtil.getStringAttribute(elementIdReferenceMatch,
                                                                  XACML3.ATTRIBUTE_EARLIESTVERSION);
        String versionLatestString = DOMUtil.getStringAttribute(elementIdReferenceMatch,
                                                                XACML3.ATTRIBUTE_LATESTVERSION);

        VersionMatch version = null;
        VersionMatch versionEarliest = null;
        VersionMatch versionLatest = null;

        if (versionString != null) {
            try {
                version = StdVersionMatch.newInstance(versionString);
            } catch (ParseException ex) {
                if (!bLenient) {
                    throw new DOMStructureException(nodeIdReferenceMatch,
                                                    "Invalid " + XACML3.ATTRIBUTE_VERSION + " string \""
                                                        + versionString + "\" in \""
                                                        + DOMUtil.getNodeLabel(nodeIdReferenceMatch), ex);
                }
            }
        }
        if (versionEarliestString != null) {
            try {
                versionEarliest = StdVersionMatch.newInstance(versionEarliestString);
            } catch (ParseException ex) {
                if (!bLenient) {
                    throw new DOMStructureException(nodeIdReferenceMatch,
                                                    "Invalid " + XACML3.ATTRIBUTE_EARLIESTVERSION
                                                        + " string \"" + versionEarliestString + "\" in \""
                                                        + DOMUtil.getNodeLabel(nodeIdReferenceMatch), ex);
                }
            }
        }
        if (versionLatestString != null) {
            try {
                versionLatest = StdVersionMatch.newInstance(versionLatestString);
            } catch (ParseException ex) {
                if (!bLenient) {
                    throw new DOMStructureException(nodeIdReferenceMatch,
                                                    "Invalid " + XACML3.ATTRIBUTE_LATESTVERSION
                                                        + " string \"" + versionLatestString + "\" in \""
                                                        + DOMUtil.getNodeLabel(nodeIdReferenceMatch), ex);
                }
            }
        }

        return new DOMIdReferenceMatch(idReferenceMatch, version, versionEarliest, versionLatest);
    }

    public static boolean repair(Node nodeIdReferenceMatch) throws DOMStructureException {
        Element elementIdReferenceMatch = DOMUtil.getElement(nodeIdReferenceMatch);
        boolean result = false;

        result = DOMUtil.repairIdentifierContent(elementIdReferenceMatch, logger) || result;
        result = DOMUtil.repairVersionMatchAttribute(elementIdReferenceMatch, XACML3.ATTRIBUTE_VERSION,
                                                     logger) || result;
        result = DOMUtil.repairVersionMatchAttribute(elementIdReferenceMatch,
                                                     XACML3.ATTRIBUTE_EARLIESTVERSION, logger) || result;
        result = DOMUtil.repairVersionMatchAttribute(elementIdReferenceMatch, XACML3.ATTRIBUTE_LATESTVERSION,
                                                     logger) || result;

        return result;
    }
}
