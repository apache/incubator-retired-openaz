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

import org.apache.openaz.xacml.util.StringUtils;

/**
 * VariableMap is a collection of {@link org.apache.openaz.xacml.pdp.policy.VariableDefinition}s that are
 * accessible by the variable identifier.
 */
public class VariableMap {
    private List<VariableDefinition> variableDefinitions;
    private Map<String, VariableDefinition> mapVariableDefinitions;

    private void ensureVariableDefinitions() {
        if (this.variableDefinitions == null) {
            this.variableDefinitions = new ArrayList<VariableDefinition>();
        }
    }

    private void ensureMap() {
        if (this.mapVariableDefinitions == null) {
            this.mapVariableDefinitions = new HashMap<String, VariableDefinition>();
        }
    }

    public VariableMap() {
    }

    /**
     * Gets the <code>VariableDefinition</code> with the given <code>String</code> id.
     *
     * @param variableId the <code>String</code> identifier of the <code>VariableDefinition</code> to retrieve
     * @return the <code>VariableDefinition</code> with the given <code>String</code> id or null if not found.
     */
    public VariableDefinition getVariableDefinition(String variableId) {
        return (this.mapVariableDefinitions == null ? null : this.mapVariableDefinitions.get(variableId));
    }

    /**
     * Gets an <code>Iterator</code> over the <code>VariableDefinition</code>s in this
     * <code>VariableMap</code> in the order they were added.
     *
     * @return an <code>Iterator</code> over the <code>VariableDefinition</code>s in this
     *         <code>VariableMap</code>
     */
    public Iterator<VariableDefinition> getVariableDefinitions() {
        return (this.variableDefinitions == null ? null : this.variableDefinitions.iterator());
    }

    /**
     * Adds the given <code>VariableDefinition</code> to this <code>VariableMap</code>.
     *
     * @param variableDefinition the <code>VariableDefinition</code> to add
     */
    public void add(VariableDefinition variableDefinition) {
        this.ensureMap();
        this.ensureVariableDefinitions();
        this.variableDefinitions.add(variableDefinition);
        this.mapVariableDefinitions.put(variableDefinition.getId(), variableDefinition);
    }

    /**
     * Adds the contents of the given <code>Collection</code> of <code>VariableDefinition</code>s to the set
     * of <code>VariableDefinition</code>s in this <code>VariableMap</code>>
     *
     * @param listVariableDefinitions the <code>Collection</code> of <code>VariableDefinition</code>s to add
     */
    public void addVariableDefinitions(Collection<VariableDefinition> listVariableDefinitions) {
        for (VariableDefinition variableDefinition : listVariableDefinitions) {
            this.add(variableDefinition);
        }
    }

    /**
     * Sets the <code>VariableDefinition</code>s in this <code>VariableMap</code> to the contents of the given
     * <code>Collection</code>.
     *
     * @param listVariableDefinitions the <code>Collection</code> of <code>VariableDefinition</code> to set
     */
    public void setVariableDefinitions(Collection<VariableDefinition> listVariableDefinitions) {
        this.variableDefinitions = null;
        this.mapVariableDefinitions = null;
        if (listVariableDefinitions != null) {
            this.addVariableDefinitions(variableDefinitions);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        if (this.mapVariableDefinitions.size() > 0) {
            stringBuilder.append("variableDefinitions=");
            stringBuilder.append(StringUtils.toString(this.mapVariableDefinitions.values().iterator()));
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
