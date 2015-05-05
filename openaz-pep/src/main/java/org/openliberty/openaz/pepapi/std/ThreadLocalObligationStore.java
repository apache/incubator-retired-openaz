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


import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationStore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Acts as a store for Obligation instances in the current thread of execution.
 *
 *
 */
public final class ThreadLocalObligationStore implements ObligationStore {

    private static final ThreadLocal<Map<Class<?>, Set<Obligation>>> obligationMapContainer =
        new ThreadLocal<Map<Class<?>, Set<Obligation>>>();

    private ThreadLocalObligationStore() {}

    public static ThreadLocalObligationStore newInstance() {
        return new ThreadLocalObligationStore();
    }

    /**
     * Set Obligations for the current thread of execution.
     *
     * @param obligationMap     a <code>Map</code> containing <code>Obligation</code> instances keyed by ObligationHandler Class.
     */
    void setObligations(Map<Class<?>, Set<Obligation>> obligationMap) {
        if(obligationMap != null && !obligationMap.isEmpty()) {
            obligationMapContainer.set(Collections.unmodifiableMap(obligationMap));
        } else {
            obligationMapContainer.set(null);
        }
    }

    /**
     * Returns all obligations in the current thread of execution.
     *
     * @return  a <code>Set</code> of <code>Obligation</code> instances.
     */
    public Set<Obligation> getAllObligations() {
        Set<Obligation> allObligations = new HashSet<Obligation>();
        Map<Class<?>, Set<Obligation>> obligationMap = obligationMapContainer.get();
        if(obligationMap != null) {
            for(Entry<Class<?>, Set<Obligation>> e: obligationMap.entrySet()) {
                allObligations.addAll(e.getValue());
            }
        }
        return allObligations;
    }

    /**
     * Returns all obligations that the given ObligationHandler can handle, in the current thread of execution.
     *
     * @param oHandlerClass
     * @return a <code>Set</code> of <code>Obligation</code> instances.
     */
    @Override
    public Set<Obligation> getHandlerObligations(Class<?> oHandlerClass) {
        Set<Obligation> obligations = new HashSet<Obligation>();
        Map<Class<?>, Set<Obligation>> obligationMap = obligationMapContainer.get();
        if(obligationMap != null && obligationMap.get(oHandlerClass) != null) {
            obligations.addAll(obligationMap.get(oHandlerClass));
        }
        return obligations;
    }


    @Override
    public Obligation getHandlerObligationById(Class<?> oHandlerClass,
            String obligationId) {
        Set<Obligation> obligations = getHandlerObligations(oHandlerClass);
        if(obligations != null) {
            for(Obligation obligation: obligations) {
                if(obligation.getId().equals(obligationId)) {
                    return obligation;
                }
            }
        }
        return null;
    }

    /**
     * Clear all obligations in the current thread.
     */
    void clear() {
        obligationMapContainer.remove();
    }
}
