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
package org.apache.openaz.xacml.pdp.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TargetedCombinerParameterMap is a utility for maintaining a collection of
 * {@link org.apache.openaz.xacml.policy.TargetedCombinerParameter} objects with the mappings to their
 * targets.
 *
 * @param <T> the type of the identifier for the <code>TargetedCombinerParameter</code>s in the map
 * @param <U> the type of the object referenced by the identifier
 */
public class TargetedCombinerParameterMap<T, U> {
    List<TargetedCombinerParameter<T, U>> targetedCombinerParameters = null;
    Map<T, U> mapTargetIdToTarget = new HashMap<T, U>();
    Map<U, List<CombinerParameter>> mapTargetToCombinerParameters = null;

    private void ensureTargetedCombinerParameters() {
        if (this.targetedCombinerParameters == null) {
            this.targetedCombinerParameters = new ArrayList<TargetedCombinerParameter<T, U>>();
        }
    }

    /**
     * Gets the target from the given <code>TargetedCombinerParameter</code> if present. If not, find the
     * target in the target id to target mapping, update the <code>TargetedCombinerParameter</code> and then
     * return the target.
     *
     * @param targetedCombinerParameter the <code>TargetedCombinerParameter</code> to resolve
     * @return the target for the given <code>TargetedCombinerParameter</code>
     */
    protected U resolve(TargetedCombinerParameter<T, U> targetedCombinerParameter) {
        U result;
        if ((result = targetedCombinerParameter.getTarget()) != null) {
            return result;
        } else if ((result = this.mapTargetIdToTarget.get(targetedCombinerParameter.getTargetId())) != null) {
            targetedCombinerParameter.setTarget(result);
            return result;
        } else {
            return null;
        }
    }

    /**
     * Ensures the <code>Map</code> from targets to <code>List</code> of <code>CombinerParameter</code>s has
     * been created if needed.
     *
     * @throws IllegalStateException if there are <code>TargetedCombinerParameter</code>s that cannot be
     *             resolved
     */
    protected void ensureMap() throws IllegalStateException {
        if (this.mapTargetToCombinerParameters == null
            && this.targetedCombinerParameters != null && this.targetedCombinerParameters.size() > 0) {
            this.mapTargetToCombinerParameters = new HashMap<U, List<CombinerParameter>>();
            for (TargetedCombinerParameter<T, U> targetedCombinerParameter : this.targetedCombinerParameters) {
                U target = this.resolve(targetedCombinerParameter);
                if (target == null) {
                    throw new IllegalStateException("Unresolved TargetCombinerParameter \""
                        + targetedCombinerParameter.toString() + "\"");
                }
                List<CombinerParameter> listCombinerParameters = this.mapTargetToCombinerParameters
                    .get(target);
                if (listCombinerParameters == null) {
                    listCombinerParameters = new ArrayList<CombinerParameter>();
                    this.mapTargetToCombinerParameters.put(target, listCombinerParameters);
                }
                listCombinerParameters.add(targetedCombinerParameter);
            }
        }
    }

    /**
     * Creates a new <code>TargetedCombinerParameterMap</code>.
     */
    public TargetedCombinerParameterMap() {
    }

    /**
     * Adds a new target object to the identifier map.
     *
     * @param targetId the id for the target
     * @param target the target
     */
    public void addTarget(T targetId, U target) {
        this.mapTargetIdToTarget.put(targetId, target);
    }

    /**
     * Adds a new <code>TargetedCombinerParameter</code> to this <code>TargetedCombinerParameterMap</code>.
     *
     * @param targetdCombinerParameter the <code>TargetedCombinerParameter</code> to add
     */
    public void addCombinerParameter(TargetedCombinerParameter<T, U> targetdCombinerParameter) {
        this.ensureTargetedCombinerParameters();
        this.targetedCombinerParameters.add(targetdCombinerParameter);
        this.mapTargetToCombinerParameters = null;
    }

    /**
     * Adds the contents of the given <code>Collection</code> of <code>TargetedCombinerParameter</code>s to
     * this <code>TargetedCombinerParameterMap</code>.
     *
     * @param listTargetedCombinerParameters the <code>Collection</code> of
     *            <code>TargetedCombinerParameter</code>s to add
     */
    public void addCombinerParameters(Collection<TargetedCombinerParameter<T, U>> listTargetedCombinerParameters) {
        this.ensureTargetedCombinerParameters();
        this.targetedCombinerParameters.addAll(listTargetedCombinerParameters);
        this.mapTargetToCombinerParameters = null;
    }

    /**
     * Sets the set of <code>TargetedCombinerParameter</code>s for this
     * <code>TargetedCombinerParameterMap</code> to the contents of the given <code>Collection</code>>
     *
     * @param listTargetedCombinerParameters the <code>Collection</code> of
     *            <code>TargetedCombinerParameter</code>s to set
     */
    public void setCombinerParameters(Collection<TargetedCombinerParameter<T, U>> listTargetedCombinerParameters) {
        this.targetedCombinerParameters = null;
        if (listTargetedCombinerParameters != null) {
            this.addCombinerParameters(targetedCombinerParameters);
        }
    }

    /**
     * Looks up the given target in the map for any {@link CombinerParameter}s for the given target.
     *
     * @param target the target
     * @return a <code>List</code> of <code>CombinerParameter</code>s for the target or null if none
     * @throws IllegalStateException if there are <code>TargetedCombinerParameter</code>s that cannot be
     *             resolved
     */
    public List<CombinerParameter> getCombinerParameters(U target) throws IllegalStateException {
        this.ensureMap();
        return (this.mapTargetToCombinerParameters == null ? null : this.mapTargetToCombinerParameters
            .get(target));
    }

    public Iterator<TargetedCombinerParameter<T, U>> getTargetedCombinerParameters() {
        return (this.targetedCombinerParameters == null ? null : this.targetedCombinerParameters.iterator());
    }

}
