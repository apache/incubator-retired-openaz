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

package org.openliberty.openaz.pepapi.std;

import com.att.research.xacml.api.*;
import com.att.research.xacml.api.Attribute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.Advice;
import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


final class StdPepResponse implements PepResponse {

    private static final Log logger = LogFactory.getLog(StdPepResponse.class);

    private final Result wrappedResult;

    private final PepConfig pepConfig;

    private final ObligationRouter obligationRouter;

    static PepResponse newInstance(PepConfig pepConfig, ObligationRouter obligationRouter, Result result) {
        return new StdPepResponse(pepConfig, obligationRouter, result);
    }

    private StdPepResponse(PepConfig pepConfig, ObligationRouter obligationRouter, Result result) {
        this.pepConfig = pepConfig;
        this.wrappedResult = result;
        this.obligationRouter = obligationRouter;
    }

    @Override
    public boolean allowed() throws PepException {
        if(obligationRouter != null) {
            obligationRouter.routeObligations(getObligations());
        }
        switch(wrappedResult.getDecision()) {
        case PERMIT:
            return true;
        case DENY:
            return false;
        case NOTAPPLICABLE:
            return enforceBehavior(pepConfig.getNotApplicableBehavior(), "Not Applicable");
        //TODO: Handle various indeterminate status codes.
        case INDETERMINATE:
        case INDETERMINATE_DENY:
        case INDETERMINATE_DENYPERMIT:
        case INDETERMINATE_PERMIT:
            Status status = wrappedResult.getStatus();
            String formatted = String.format("Decision: Indeterminate, Status Code: %s, Status Message: %s",
                                             status.getStatusCode(), status.getStatusMessage());
            logger.error(formatted);
            throw new PepException(formatted);
        default:
            throw new PepException("Invalid response from PDP");
        }
    }

    @Override
    public Map<String, Obligation> getObligations() throws PepException {
        Map<String, Obligation> obligationMap = new HashMap<String, Obligation>();
        for(com.att.research.xacml.api.Obligation wrappedObligation: wrappedResult.getObligations()) {
            Obligation obligation = new StdObligation(wrappedObligation);
            obligationMap.put(obligation.getId(), obligation);
        }
        return obligationMap;
    }

    @Override
    public Map<String, Advice> getAdvices() throws PepException {
        Map<String, Advice> adviceMap = new HashMap<String, Advice>();
        for(com.att.research.xacml.api.Advice wrappedAdvice: wrappedResult.getAssociatedAdvice()) {
            Advice advice = new StdAdvice(wrappedAdvice);
            adviceMap.put(advice.getId(), advice);
        }
        return adviceMap;
    }

    @Override
    public Object getAssociation() throws PepException {
        return null;
    }

    @Override
    public Collection<Attribute> getAttributes() {
        Collection<Attribute> attributes = new ArrayList<Attribute>();
        for(AttributeCategory category: wrappedResult.getAttributes()) {
            attributes.addAll(category.getAttributes());
        }
        return attributes;
    }

    @Override
    public Map<Identifier, Collection<Attribute>> getAttributesByCategory() {
        Map<Identifier, Collection<Attribute>> attributesByCategory = new HashMap<Identifier, Collection<Attribute>>();
        for(AttributeCategory category: wrappedResult.getAttributes()) {
            attributesByCategory.put(category.getCategory(), category.getAttributes());
        }
        return attributesByCategory;
    }

    @Override
    public Result getWrappedResult() {
        return wrappedResult;
    }

    private boolean enforceBehavior(
        PepResponseBehavior pepResponseBehavior, String decision) throws PepException {
        switch (pepResponseBehavior) {
        case RETURN_YES:
            return true;
        case RETURN_NO:
            return false;
        case THROW_EXCEPTION:
            logger.info("Throwing an exception as per configured behavior for decision: " + decision);
            throw new PepException("Exception being thrown based on configured " +
                                   "behavior for decision: " + decision);
        default:
            throw new PepException("Invalid PepResponseBehavior");
        }
    }
}
