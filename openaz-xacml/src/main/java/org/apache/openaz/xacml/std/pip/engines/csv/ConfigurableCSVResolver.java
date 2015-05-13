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
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.std.pip.engines.csv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.DataTypeFactory;
import org.apache.openaz.xacml.api.pip.PIPEngine;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.api.pip.PIPResponse;
import org.apache.openaz.xacml.std.StdMutableAttribute;
import org.apache.openaz.xacml.std.pip.StdPIPRequest;
import org.apache.openaz.xacml.std.pip.StdPIPResponse;
import org.apache.openaz.xacml.std.pip.engines.Configurables;
import org.apache.openaz.xacml.util.FactoryException;

import com.google.common.base.Splitter;

public class ConfigurableCSVResolver implements CSVResolver {

    public static final String PROP_PARAMETERS = "parameters";

    public static final String PROP_ID = "id";
    public static final String PROP_DATATYPE = "datatype";
    public static final String PROP_CATEGORY = "category";
    public static final String PROP_ISSUER = "issuer";

    public static final String PROP_COLUMN = "column";

    private static DataTypeFactory dataTypeFactory = null;

    static {
        try {
            dataTypeFactory = DataTypeFactory.newInstance();
        } catch (FactoryException fx) {
            throw new RuntimeException(fx);
        }
    }

    protected Log logger = LogFactory.getLog(this.getClass());

    private String id;
    private String defaultIssuer;

    private Map<Integer, PIPRequest> parameterMap = new HashMap<Integer, PIPRequest>();
    private Map<Integer, PIPRequest> fieldMap = new HashMap<Integer, PIPRequest>();

    public ConfigurableCSVResolver() {

    }

    /**
     * Helper to load the "parameters" that define the search criteria for the rows to find in the CSV file.
     *
     * @param id
     * @param properties
     * @throws org.apache.openaz.xacml.api.pip.PIPException
     */
    private void readSearchParameters(String id, Properties properties) throws PIPException {
        String parameterNamesString = properties.getProperty(id + "." + PROP_PARAMETERS);
        if (parameterNamesString == null || parameterNamesString.length() == 0) {
            String message = id + ".parameters must not be empty";
            logger.error(message);
            throw new PIPException(message);
        }
        for (String parameterName : Splitter.on(',').omitEmptyStrings().trimResults()
            .split(parameterNamesString)) {
            String parameterIdPrefix = id + ".parameter." + parameterName;
            String tmpString = properties.getProperty(parameterIdPrefix + "." + PROP_COLUMN);
            if (tmpString == null || tmpString.length() == 0) {
                String message = id + ": parameter " + parameterName + " missing number for '.column'";
                logger.error(message);
                throw new PIPException(message);
            }
            int column;
            try {
                column = Integer.parseInt(tmpString);
            } catch (NumberFormatException e) {
                String message = id + ": parameter " + parameterName + ".column is not a number in '"
                                 + tmpString + "'";
                logger.error(message);
                throw new PIPException(message);
            }

            PIPRequest request = Configurables.getPIPRequest(parameterIdPrefix, properties, null);
            if (request != null) {
                this.parameterMap.put(column, request);
            } else {
                String message = id + ": attribute not defined";
                this.logger.error(message);
                throw new PIPException(message);
            }
        }
    }

    /**
     * Helper - read the definitions of the fields that can be returned from this PIP
     *
     * @param id
     * @param properties
     * @throws org.apache.openaz.xacml.api.pip.PIPException
     */
    private void readPIPRequestFieldDefinitions(String id, Properties properties) throws PIPException {
        String fieldNamesString = properties.getProperty(id + ".fields");
        if (fieldNamesString == null || fieldNamesString.length() == 0) {
            String message = id + ".fields must not be empty";
            logger.error(message);
            throw new PIPException(message);
        }
        for (String fieldName : Splitter.on(',').trimResults().omitEmptyStrings().split(fieldNamesString)) {
            String fieldIdPrefix = id + ".field." + fieldName;
            String tmpString = properties.getProperty(fieldIdPrefix + "." + "column");
            if (tmpString == null || tmpString.length() == 0) {
                String message = id + ": field " + fieldName + " missing number for '.column'";
                logger.error(message);
                throw new PIPException(message);
            }
            int column;
            try {
                column = Integer.parseInt(tmpString);
            } catch (NumberFormatException e) {
                String message = id + ": field " + fieldName + ".column is not a number in '" + tmpString
                                 + "'";
                logger.error(message);
                throw new PIPException(message);
            }

            PIPRequest pipRequest = Configurables
                .getPIPRequest(fieldIdPrefix, properties, this.defaultIssuer);
            if (pipRequest != null) {
                this.fieldMap.put(column, pipRequest);
            } else {
                String message = id + ": attribute not defined column " + column;
                this.logger.error(message);
                throw new PIPException(message);
            }
        }
    }

    @Override
    public void configure(String id, Properties properties, String defaultIssuer) throws PIPException {
        //
        // Save our ID (i.e. name)
        //
        this.id = id;
        this.defaultIssuer = defaultIssuer;
        //
        // Get the "parameters", i.e. the fields to use for uniquely identifying a single row, from the
        // properties
        //
        readSearchParameters(id, properties);
        //
        // Get the list of fields that the caller may ask for from a given row
        //
        readPIPRequestFieldDefinitions(id, properties);
    }

    @Override
    public void attributesRequired(Collection<PIPRequest> parameters) {
        for (Integer key : this.parameterMap.keySet()) {
            parameters.add(new StdPIPRequest(this.parameterMap.get(key)));
        }
    }

    @Override
    public void attributesProvided(Collection<PIPRequest> attributes) {
        for (Integer key : this.fieldMap.keySet()) {
            attributes.add(new StdPIPRequest(this.fieldMap.get(key)));
        }
    }

    @Override
    public boolean supportRequest(PIPRequest pipRequest) {
        for (Integer key : this.fieldMap.keySet()) {
            PIPRequest request = this.fieldMap.get(key);
            if (pipRequest == null) {
                return false;
            }
            if (request.equals(pipRequest)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(this.id + " supports " + pipRequest);
                }
                return true;
            }
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(this.id + " does NOT support " + pipRequest);
        }
        return false;
    }

    @Override
    public Map<Integer, List<AttributeValue<?>>> getColumnParameterValues(PIPEngine engine,
                                                                          PIPRequest request, PIPFinder finder)
        throws PIPException {
        Map<Integer, List<AttributeValue<?>>> map = new HashMap<Integer, List<AttributeValue<?>>>();
        for (Integer column : this.parameterMap.keySet()) {
            PIPRequest requestParameter = this.parameterMap.get(column);
            //
            // Get the parameter attributes
            //
            PIPResponse pipResponse = finder.getMatchingAttributes(requestParameter, null);
            if (pipResponse == null || !pipResponse.getStatus().isOk()
                || pipResponse == StdPIPResponse.PIP_RESPONSE_EMPTY) {
                //
                // We must have at least one attribute value. If none exist, then return null
                //
                return null;
            }
            //
            // Accumulate the values
            //
            List<AttributeValue<?>> values = new ArrayList<AttributeValue<?>>();
            for (Attribute attr : pipResponse.getAttributes()) {
                values.addAll(attr.getValues());
            }
            //
            // Add it to our map
            //
            map.put(column, values);
        }
        return map;
    }

    @Override
    public List<Attribute> decodeResult(String[] line) throws PIPException {
        //
        // Return all the fields
        //
        List<Attribute> attributeList = new ArrayList<Attribute>();
        for (Integer column : this.fieldMap.keySet()) {
            //
            // Sanity check, we should have the required number of columns
            //
            if (column >= line.length) {
                return null;
            }
            //
            // Does it have a value?
            //
            String value = line[column];
            if (value.length() == 0) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Column " + column + " does not have a value.");
                }
                continue;
            }
            //
            // Convert it to appropriate data type
            //
            PIPRequest request = this.fieldMap.get(column);
            //
            // Does the attribute already exist?
            //
            StdMutableAttribute attribute = null;
            for (Attribute a : attributeList) {
                if (a.getCategory() == request.getCategory()
                    && a.getAttributeId() == request.getAttributeId()) {
                    attribute = (StdMutableAttribute)a;
                }
            }
            if (attribute == null) {
                attribute = new StdMutableAttribute();
                attribute.setCategory(request.getCategory());
                attribute.setAttributeId(request.getAttributeId());
                attribute.setIssuer(request.getIssuer());
                attributeList.add(attribute);
            }
            //
            // Add it in
            //
            DataType<?> dt = dataTypeFactory.getDataType(request.getDataTypeId());
            try {
                attribute.addValue(dt.createAttributeValue(value));
            } catch (DataTypeException e) {
                String message = this.id + ": " + e.getLocalizedMessage();
                this.logger.error(message);
                return null;
            }
        }
        return attributeList;
    }

}
