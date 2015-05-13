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
package org.apache.openaz.xacml.std.jaxp;

import java.util.Iterator;

import javax.xml.bind.JAXBElement;

import org.apache.openaz.xacml.api.Decision;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdMutableResult;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.IdReferenceType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResultType;

/**
 * JaxpResult extends {@link org.apache.openaz.xacml.std.StdMutableResult} with methods for creation from JAXP
 * elements.
 */
public class JaxpResult extends StdMutableResult {

    protected JaxpResult() {
    }

    public static JaxpResult newInstance(ResultType resultType) {
        if (resultType == null) {
            throw new NullPointerException("Null ResultType");
        } else if (resultType.getDecision() == null) {
            throw new IllegalArgumentException("Null Decision in ResultType");
        }
        JaxpResult jaxpResult = new JaxpResult();

        switch (resultType.getDecision()) {
        case DENY:
            jaxpResult.setDecision(Decision.DENY);
            break;
        case INDETERMINATE:
            jaxpResult.setDecision(Decision.INDETERMINATE);
            break;
        case NOT_APPLICABLE:
            jaxpResult.setDecision(Decision.NOTAPPLICABLE);
            break;
        case PERMIT:
            jaxpResult.setDecision(Decision.PERMIT);
            break;
        default:
            throw new IllegalArgumentException("Invalid Decision in ResultType \""
                                               + resultType.getDecision().toString() + "\"");
        }

        if (resultType.getStatus() != null) {
            jaxpResult.setStatus(JaxpStatus.newInstance(resultType.getStatus()));
        }

        if (resultType.getObligations() != null && resultType.getObligations().getObligation() != null
            && resultType.getObligations().getObligation().size() > 0) {
            Iterator<ObligationType> iterObligationTypes = resultType.getObligations().getObligation()
                .iterator();
            while (iterObligationTypes.hasNext()) {
                jaxpResult.addObligation(JaxpObligation.newInstance(iterObligationTypes.next()));
            }
        }

        if (resultType.getAssociatedAdvice() != null && resultType.getAssociatedAdvice().getAdvice() != null
            && resultType.getAssociatedAdvice().getAdvice().size() > 0) {
            Iterator<AdviceType> iterAdviceTypes = resultType.getAssociatedAdvice().getAdvice().iterator();
            while (iterAdviceTypes.hasNext()) {
                jaxpResult.addAdvice(JaxpAdvice.newInstance(iterAdviceTypes.next()));
            }
        }

        if (resultType.getAttributes() != null && resultType.getAttributes().size() > 0) {
            Iterator<AttributesType> iterAttributesTypes = resultType.getAttributes().iterator();
            while (iterAttributesTypes.hasNext()) {
                jaxpResult
                    .addAttributeCategory(JaxpAttributeCategory.newInstance(iterAttributesTypes.next()));
            }
        }

        if (resultType.getPolicyIdentifierList() != null
            && resultType.getPolicyIdentifierList().getPolicyIdReferenceOrPolicySetIdReference() != null
            && resultType.getPolicyIdentifierList().getPolicyIdReferenceOrPolicySetIdReference().size() > 0) {
            Iterator<JAXBElement<IdReferenceType>> iterJAXBElements = resultType.getPolicyIdentifierList()
                .getPolicyIdReferenceOrPolicySetIdReference().iterator();
            while (iterJAXBElements.hasNext()) {
                JAXBElement<IdReferenceType> jaxbElement = iterJAXBElements.next();
                if (jaxbElement.getName().getLocalPart().equals(XACML3.ELEMENT_POLICYIDREFERENCE)) {
                    jaxpResult.addPolicyIdentifier(JaxpIdReference.newInstance(jaxbElement.getValue()));
                } else if (jaxbElement.getName().getLocalPart().equals(XACML3.ELEMENT_POLICYSETIDREFERENCE)) {
                    jaxpResult.addPolicySetIdentifier(JaxpIdReference.newInstance(jaxbElement.getValue()));
                } else {
                    throw new IllegalArgumentException("Unexpected IdReferenceType found \""
                                                       + jaxbElement.getName().getLocalPart() + "\"");
                }
            }
        }

        return jaxpResult;
    }

}
