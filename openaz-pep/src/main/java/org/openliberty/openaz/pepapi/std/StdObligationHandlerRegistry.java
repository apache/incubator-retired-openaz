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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationHandler;
import org.openliberty.openaz.pepapi.ObligationHandlerRegistry;
import org.openliberty.openaz.pepapi.Attribute;
import org.openliberty.openaz.pepapi.MatchAllObligationAttributes;
import org.openliberty.openaz.pepapi.MatchAnyObligation;
import org.openliberty.openaz.pepapi.Matchable;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An <code>ObligationHandlerRegistry</code> implementation that accept handler classes that are either
 * ObligationHandler instances or contain any of the following annotations -
 * <code> @MatchAnyObligation, @MatchAllObligationAttributes</code> that represents Obligation criteria for
 * registration.
 */
public class StdObligationHandlerRegistry implements ObligationHandlerRegistry {

    private static final Log logger = LogFactory.getLog(StdObligationHandlerRegistry.class);

    private final Map<Class<?>, Matchable<Obligation>> oHandlerCriteriaMap;

    private StdObligationHandlerRegistry(List<?> oHandlers) {
        oHandlerCriteriaMap = new HashMap<Class<?>, Matchable<Obligation>>();
        for (Object oHandler : oHandlers) {
            Class<?> oHandlerClass = oHandler.getClass();
            Matchable<Obligation> matchable = null;
            if (oHandler instanceof ObligationHandler) {
                matchable = (ObligationHandler)oHandler;
            } else {
                matchable = processAnnotation(oHandlerClass);
            }

            if (matchable != null) {
                oHandlerCriteriaMap.put(oHandlerClass, matchable);
            } else {
                logger
                    .error("Obligation Handler Class: " + oHandlerClass
                           + " is not an instance of ObligationHandler or doesn't contain a valid Annotation");
                throw new IllegalArgumentException(
                                                   "Obligation Handler Class: "
                                                       + oHandlerClass
                                                       + " is not an instance of ObligationHandler or doesn't contain a valid Annotation");
            }
        }
    }

    /**
     * Process Annotations in the classes provided and translate those into <code>ObligationCriteria</code>.
     *
     * @param oHandlerClass
     * @return an ObligationCriteria instance.
     */
    private ObligationCriteria processAnnotation(Class<?> oHandlerClass) {
        ObligationCriteria criteria = null;
        for (Annotation a : oHandlerClass.getAnnotations()) {
            if (a.annotationType().equals(MatchAnyObligation.class)) {
                String[] obligationIds = ((MatchAnyObligation)a).value();
                ObligationCriteriaBuilder criteriaBuilder = new ObligationCriteriaBuilder();
                if (obligationIds != null && obligationIds.length > 0) {
                    criteriaBuilder.matchAnyObligationId(obligationIds);
                } else {
                    criteriaBuilder.matchAnyObligation();
                }
                criteria = criteriaBuilder.build();
            } else if (a.annotationType().equals(MatchAllObligationAttributes.class)) {
                ObligationCriteriaBuilder criteriaBuilder = new ObligationCriteriaBuilder();
                MatchAllObligationAttributes attributeObligationAnnotation = (MatchAllObligationAttributes)a;
                for (Attribute attribute : attributeObligationAnnotation.value()) {
                    String attributeId = attribute.id();
                    String[] anyValue = attribute.anyValue();
                    if (anyValue != null && anyValue.length > 0) {
                        criteriaBuilder.matchAttributeWithAnyGivenValue(attributeId, anyValue);
                    } else {
                        criteriaBuilder.matchAttribute(attributeId);
                    }
                }
                criteria = criteriaBuilder.build();
            }
        }
        return criteria;
    }

    /**
     * Returns a new instance of <code>StdObligationHandlerRegistry</code>.
     *
     * @param oHandlers
     * @return
     */
    public static ObligationHandlerRegistry newInstance(List<?> oHandlers) {
        return new StdObligationHandlerRegistry(oHandlers);
    }

    @Override
    public Map<Class<?>, Matchable<Obligation>> getRegisteredHandlerMap() {
        return this.oHandlerCriteriaMap;
    }
}
