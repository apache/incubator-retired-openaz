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
package org.apache.openaz.xacml.std.pip.engines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.RequestAttributes;
import org.apache.openaz.xacml.api.pip.PIPEngine;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.api.pip.PIPResponse;
import org.apache.openaz.xacml.std.StdMutableAttribute;
import org.apache.openaz.xacml.std.pip.StdMutablePIPResponse;
import org.apache.openaz.xacml.std.pip.StdPIPRequest;
import org.apache.openaz.xacml.std.pip.StdPIPResponse;

/**
 * StdRequestEngine implements the {@link org.apache.openaz.xacml.api.pip.PIPEngine} interface to retrieve
 * matching {@link com.att.reserach.xacml.api.Attribute}s from a {@link org.apache.openaz.xacml.pip.Request}
 * object.
 */
public class RequestEngine implements PIPEngine {
    private Request request;

    protected Request getRequest() {
        return this.request;
    }

    /**
     * Creates a <code>StdRequestEngine</code> for retrieving <code>Attribute</code>s from a
     * <code>Request</code>.
     *
     * @param requestIn the <code>Request</code> to search
     */
    public RequestEngine(Request requestIn) {
        this.request = requestIn;
    }

    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public String getDescription() {
        return "PIPEngine for retrieving Attributes from the Request";
    }

    @Override
    public PIPResponse getAttributes(PIPRequest pipRequest, PIPFinder pipFinder) throws PIPException {
        Request thisRequest = this.getRequest();
        if (thisRequest == null) {
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        }

        Iterator<RequestAttributes> iterRequestAttributes = thisRequest.getRequestAttributes(pipRequest
            .getCategory());
        if (iterRequestAttributes == null || !iterRequestAttributes.hasNext()) {
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        }

        StdMutablePIPResponse pipResponse = null;

        while (iterRequestAttributes.hasNext()) {
            RequestAttributes requestAttributes = iterRequestAttributes.next();
            Iterator<Attribute> iterAttributes = requestAttributes.getAttributes(pipRequest.getAttributeId());
            while (iterAttributes.hasNext()) {
                Attribute attribute = iterAttributes.next();
                if (attribute.getValues().size() > 0
                    && (pipRequest.getIssuer() == null || pipRequest.getIssuer()
                        .equals(attribute.getIssuer()))) {
                    /*
                     * If all of the attribute values in the given Attribute match the requested data type, we
                     * can just return the whole Attribute as part of the response.
                     */
                    boolean bAllMatch = true;
                    for (AttributeValue<?> attributeValue : attribute.getValues()) {
                        if (!pipRequest.getDataTypeId().equals(attributeValue.getDataTypeId())) {
                            bAllMatch = false;
                            break;
                        }
                    }
                    if (bAllMatch) {
                        if (pipResponse == null) {
                            pipResponse = new StdMutablePIPResponse(attribute);
                        } else {
                            pipResponse.addAttribute(attribute);
                        }
                    } else {
                        /*
                         * Only a subset of the values match, so we have to construct a new Attribute
                         * containing only the matching values.
                         */
                        List<AttributeValue<?>> listAttributeValues = null;
                        for (AttributeValue<?> attributeValue : attribute.getValues()) {
                            if (pipRequest.getDataTypeId().equals(attributeValue.getDataTypeId())) {
                                if (listAttributeValues == null) {
                                    listAttributeValues = new ArrayList<AttributeValue<?>>();
                                }
                                listAttributeValues.add(attributeValue);
                            }
                        }
                        if (listAttributeValues != null) {
                            if (pipResponse == null) {
                                pipResponse = new StdMutablePIPResponse();
                            }
                            pipResponse.addAttribute(new StdMutableAttribute(attribute.getCategory(),
                                                                             attribute.getAttributeId(),
                                                                             listAttributeValues, attribute
                                                                                 .getIssuer(), attribute
                                                                                 .getIncludeInResults()));
                        }
                    }
                }
            }
        }

        if (pipResponse == null) {
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        } else {
            return pipResponse;
        }
    }

    @Override
    public Collection<PIPRequest> attributesRequired() {
        return Collections.emptyList();
    }

    @Override
    public Collection<PIPRequest> attributesProvided() {
        Set<PIPRequest> providedAttributes = new HashSet<PIPRequest>();
        for (RequestAttributes attributes : this.request.getRequestAttributes()) {
            for (Attribute attribute : attributes.getAttributes()) {
                Set<Identifier> datatypes = new HashSet<Identifier>();
                for (AttributeValue<?> value : attribute.getValues()) {
                    datatypes.add(value.getDataTypeId());
                }
                for (Identifier dt : datatypes) {
                    providedAttributes.add(new StdPIPRequest(attribute.getCategory(), attribute
                        .getAttributeId(), dt, attribute.getIssuer()));
                }
            }
        }
        return providedAttributes;
    }

}
