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

package org.openliberty.openaz.pepapi;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.Result;

import java.util.Collection;
import java.util.Map;

/**
 */
public interface PepResponse {

    /**
     * Returns the decision associated with the current result.
     * 
     * @return true if the user was granted access to the resource, otherwise false
     * @throws PepException if the {@link PepResponseBehavior} configured in the {@link PepResponseFactory}
     *             indicates that for the response should be thrown
     */
    public boolean allowed() throws PepException;

    /**
     * Return the set of {@link org.openliberty.openaz.pepapi.Obligation}s associated with the current result
     * indexed by ObligationId.
     * 
     * @return a Map of ObligationId, Obligation pairs
     * @throws PepException
     * @see org.openliberty.openaz.pepapi.Obligation#getId()
     */
    public Map<String, Obligation> getObligations() throws PepException;

    /**
     * Return the set of {@link org.openliberty.openaz.pepapi.Advice}s associated with the current result
     * indexed by adviceId.
     * 
     * @return a Map of adviceId, Advice pairs
     * @throws PepException
     * @see org.openliberty.openaz.pepapi.Advice#getId()
     */
    public Map<String, Advice> getAdvices() throws PepException;

    /**
     * Return the object association that is tied to the current result. The association is the same object
     * that was used to create the PepRequest and may be used to correlate the PepResponse results with the
     * association pairs that were used to create the PepRequest.
     * 
     * @return an object that was used as the action-resource in the PepRequest
     * @throws PepException
     */
    public Object getAssociation() throws PepException;

    /**
     * @return
     */
    public Collection<Attribute> getAttributes();

    /**
     * @return
     */
    public Map<Identifier, Collection<Attribute>> getAttributesByCategory();

    /**
     * @return
     */
    public Result getWrappedResult();

}
