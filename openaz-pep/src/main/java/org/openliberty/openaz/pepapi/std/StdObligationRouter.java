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
import org.openliberty.openaz.pepapi.ObligationHandlerRegistry;
import org.openliberty.openaz.pepapi.ObligationRouter;
import org.openliberty.openaz.pepapi.UnhandleableObligationException;
import org.openliberty.openaz.pepapi.Matchable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Entity that routes obligations at runtime.
 * 
 * @see org.openliberty.openaz.pepapi.Obligation
 * @author Ajith Nair
 */
public final class StdObligationRouter implements ObligationRouter {
        
        private static final Log logger = LogFactory.getLog(StdObligationRouter.class);
        
        private final ObligationHandlerRegistry registrationHandler;
        
        private final ThreadLocalObligationStore obligationStore;
        
        StdObligationRouter(ObligationHandlerRegistry registrationHandler,
                                                ThreadLocalObligationStore threadLocalOStore) {
                this.registrationHandler = registrationHandler;
                this.obligationStore = threadLocalOStore;
        }

        public static StdObligationRouter newInstance(
                        ObligationHandlerRegistry registrationHandler,
                        ThreadLocalObligationStore threadLocalOStore) {
                return new StdObligationRouter(registrationHandler,
                                threadLocalOStore);
        }
        
        /**
         * Handles runtime obligations and routes to appropriate policy enforcement points as required.
         * 
         * @param obligationMap	a <code>Map</code> of <code>Obligation</code>s keyed by Obligation ID.
         * @throws org.openliberty.openaz.pepapi.UnhandleableObligationException	if an Obligation cannot be handled/routed.
         */
        @Override
        public void routeObligations(Map<String, Obligation> obligationMap) {
                //Clear any stale Obligations on the current thread.
                obligationStore.clear();
                if(obligationMap != null) {
                        Map<Class<?>, Set<Obligation>> obligationMapByHandlerClass 
                                                                        = new HashMap<Class<?>, Set<Obligation>>();
                        for(Entry<String, Obligation> oe: obligationMap.entrySet()) {
                                boolean isObligationHandleable = false;
                                String obligationId = oe.getKey();
                                Obligation obligation = oe.getValue();
                                for(Entry<Class<?>, Matchable<Obligation>> pe :
                                                                this.registrationHandler.getRegisteredHandlerMap().entrySet()) {
                                        Class<?> handlerClass = pe.getKey();
                                        Matchable<Obligation> matchable = pe.getValue();
                                        if(matchable.match(obligation)) {
                                                Set<Obligation> handlerObligationSet = obligationMapByHandlerClass.get(handlerClass);
                                                if(handlerObligationSet == null){
                                                        handlerObligationSet = new HashSet<Obligation>();
                                                        obligationMapByHandlerClass.put(handlerClass, handlerObligationSet);
                                                }
                                                handlerObligationSet.add(obligation);
                                                isObligationHandleable = true;
                                                if(logger.isDebugEnabled()) {
                                                        logger.debug("Obligation - " + obligationId + " matched by Handler - " + handlerClass);
                                                }
                                        }
                                }
                                if(!isObligationHandleable) {
                                        throw new UnhandleableObligationException(
                                                        "No ObligationHandlers available for handling Obligation: "
                                                                        + oe.getKey());
                                }
                        }
                        obligationStore.setObligations(obligationMapByHandlerClass);
                }
        }
}
