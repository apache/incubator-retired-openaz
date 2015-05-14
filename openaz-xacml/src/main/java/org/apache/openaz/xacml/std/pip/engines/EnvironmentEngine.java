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
import java.util.Date;
import java.util.List;

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.api.pip.PIPEngine;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.api.pip.PIPResponse;
import org.apache.openaz.xacml.std.StdMutableAttribute;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.apache.openaz.xacml.std.pip.StdPIPRequest;
import org.apache.openaz.xacml.std.pip.StdPIPResponse;
import org.apache.openaz.xacml.std.pip.StdSinglePIPResponse;

/**
 * EnvironmentEngine implements the {@link org.apache.openaz.xacml.api.pip.PipEngine} interface to provide
 * values for standard environment attributes.
 */
public class EnvironmentEngine implements PIPEngine {
    private Date contextTime;

    private StdSinglePIPResponse responseTime;
    private StdSinglePIPResponse responseDate;
    private StdSinglePIPResponse responseDateTime;

    protected StdSinglePIPResponse getResponseTime() throws DataTypeException {
        if (this.responseTime == null) {
            this.responseTime = new StdSinglePIPResponse(
                                                         new StdMutableAttribute(
                                                                                 XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT,
                                                                                 XACML3.ID_ENVIRONMENT_CURRENT_TIME,
                                                                                 DataTypes.DT_TIME
                                                                                     .createAttributeValue(this.contextTime)));
        }
        return this.responseTime;
    }

    protected StdSinglePIPResponse getResponseDate() throws DataTypeException {
        if (this.responseDate == null) {
            this.responseDate = new StdSinglePIPResponse(
                                                         new StdMutableAttribute(
                                                                                 XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT,
                                                                                 XACML3.ID_ENVIRONMENT_CURRENT_DATE,
                                                                                 DataTypes.DT_DATE
                                                                                     .createAttributeValue(this.contextTime)));
        }
        return this.responseDate;
    }

    protected StdSinglePIPResponse getResponseDateTime() throws DataTypeException {
        if (this.responseDateTime == null) {
            this.responseDateTime = new StdSinglePIPResponse(
                                                             new StdMutableAttribute(
                                                                                     XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT,
                                                                                     XACML3.ID_ENVIRONMENT_CURRENT_DATETIME,
                                                                                     DataTypes.DT_DATETIME
                                                                                         .createAttributeValue(this.contextTime)));
        }
        return this.responseDateTime;
    }

    public EnvironmentEngine(Date dateContextTimeIn) {
        this.contextTime = dateContextTimeIn;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public String getDescription() {
        return "Environment attribute PIP";
    }

    @Override
    public Collection<PIPRequest> attributesRequired() {
        return Collections.emptyList();
    }

    @Override
    public Collection<PIPRequest> attributesProvided() {
        List<PIPRequest> attributes = new ArrayList<PIPRequest>();
        attributes.add(new StdPIPRequest(XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT,
                                         XACML3.ID_ENVIRONMENT_CURRENT_DATE, XACML3.ID_DATATYPE_DATE, null));
        attributes.add(new StdPIPRequest(XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT,
                                         XACML3.ID_ENVIRONMENT_CURRENT_TIME, XACML3.ID_DATATYPE_TIME, null));
        attributes.add(new StdPIPRequest(XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT,
                                         XACML3.ID_ENVIRONMENT_CURRENT_DATETIME, XACML3.ID_DATATYPE_DATETIME,
                                         null));
        return attributes;
    }

    @Override
    public PIPResponse getAttributes(PIPRequest pipRequest, PIPFinder pipFinder) throws PIPException {
        /*
         * Make sure this is a request for an environment attribute and no issuer has been set
         */
        if (!XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT.equals(pipRequest.getCategory())
            || pipRequest.getIssuer() != null && pipRequest.getIssuer().length() > 0) {
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        }

        /*
         * See which environment attribute is requested
         */
        Identifier attributeIdRequest = pipRequest.getAttributeId();
        StdSinglePIPResponse pipResponse = null;
        try {
            if (XACML3.ID_ENVIRONMENT_CURRENT_DATE.equals(attributeIdRequest)) {
                pipResponse = this.getResponseDate();
            } else if (XACML3.ID_ENVIRONMENT_CURRENT_TIME.equals(attributeIdRequest)) {
                pipResponse = this.getResponseTime();
            } else if (XACML3.ID_ENVIRONMENT_CURRENT_DATETIME.equals(attributeIdRequest)) {
                pipResponse = this.getResponseDateTime();
            }
        } catch (DataTypeException ex) {
            throw new PIPException("DataTypeException getting \"" + attributeIdRequest.stringValue() + "\"",
                                   ex);
        }

        if (pipResponse == null) {
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        }

        /*
         * Ensure the data types match
         */
        AttributeValue<?> attributeValuePipResponse = pipResponse.getSingleAttribute().getValues().iterator()
            .next();
        if (attributeValuePipResponse.getDataTypeId().equals(pipRequest.getDataTypeId())) {
            return pipResponse;
        } else {
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        }
    }

}
