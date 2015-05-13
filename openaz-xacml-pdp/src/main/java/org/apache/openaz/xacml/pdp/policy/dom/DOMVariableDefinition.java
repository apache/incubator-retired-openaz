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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.pdp.policy.Policy;
import org.apache.openaz.xacml.pdp.policy.VariableDefinition;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.dom.DOMProperties;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOMVariableDefinition extends {@link org.apache.openaz.xacml.pdp.policy.VariableDefinition} with methods
 * for creation from DOM {@link org.w3c.dom.Node}s.
 */
public class DOMVariableDefinition extends VariableDefinition {
    private static final Log logger = LogFactory.getLog(DOMVariableDefinition.class);

    protected DOMVariableDefinition() {
    }

    /**
     * Creates a new <code>VariableDefinition</code> by parsing the given <code>Node</code> representing a
     * XACML VariableDefinition element.
     *
     * @param nodeVariableDefinition the <code>Node</code> representing the XACML VariableDefinition element
     * @param policy the <code>Policy</code> encompassing the VariableDefinition element
     * @return a new <code>VariableDefinition</code> parsed from the given <code>Node</code>
     * @throws DOMStructureException if there is an error parsing the <code>Node</code>
     */
    public static VariableDefinition newInstance(Node nodeVariableDefinition, Policy policy)
        throws DOMStructureException {
        Element elementVariableDefinition = DOMUtil.getElement(nodeVariableDefinition);
        boolean bLenient = DOMProperties.isLenient();

        DOMVariableDefinition domVariableDefinition = new DOMVariableDefinition();

        try {
            Element elementExpression = DOMUtil.getFirstChildElement(elementVariableDefinition);
            if (elementExpression != null) {
                if (DOMExpression.isExpression(elementExpression)) {
                    domVariableDefinition.setExpression(DOMExpression.newInstance(elementExpression, policy));
                } else if (!bLenient) {
                    throw DOMUtil.newUnexpectedElementException(elementExpression, elementVariableDefinition);
                }
            } else if (!bLenient) {
                throw DOMUtil.newMissingElementException(elementVariableDefinition, XACML3.XMLNS,
                                                         XACML3.ELEMENT_EXPRESSION);
            }
            domVariableDefinition.setId(DOMUtil.getStringAttribute(elementVariableDefinition,
                                                                   XACML3.ATTRIBUTE_VARIABLEID, !bLenient));
        } catch (DOMStructureException ex) {
            domVariableDefinition.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
            if (DOMProperties.throwsExceptions()) {
                throw ex;
            }
        }
        return domVariableDefinition;
    }

    public static boolean repair(Node nodeVariableDefinition) throws DOMStructureException {
        Element elementVariableDefinition = DOMUtil.getElement(nodeVariableDefinition);
        boolean result = false;

        Element elementExpression = DOMUtil.getFirstChildElement(elementVariableDefinition);
        if (elementExpression != null) {
            if (DOMExpression.isExpression(elementExpression)) {
                result = result || DOMExpression.repair(elementExpression);
            } else {
                logger.warn("Unexpected element " + elementExpression.getNodeName());
                elementVariableDefinition.removeChild(elementExpression);
                result = true;
            }
        } else {
            throw DOMUtil.newMissingElementException(elementVariableDefinition, XACML3.XMLNS,
                                                     XACML3.ELEMENT_EXPRESSION);
        }

        result = result
                 || DOMUtil.repairStringAttribute(elementVariableDefinition, XACML3.ATTRIBUTE_VARIABLEID,
                                                  "variable", logger);
        return result;
    }
}
