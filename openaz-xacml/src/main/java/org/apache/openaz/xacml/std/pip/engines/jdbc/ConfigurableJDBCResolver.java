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
package org.apache.openaz.xacml.std.pip.engines.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.DataTypeFactory;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.api.pip.PIPEngine;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.api.pip.PIPResponse;
import org.apache.openaz.xacml.std.StdAttribute;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.apache.openaz.xacml.std.datatypes.ISO8601Date;
import org.apache.openaz.xacml.std.datatypes.ISO8601DateTime;
import org.apache.openaz.xacml.std.pip.StdPIPRequest;
import org.apache.openaz.xacml.std.pip.engines.Configurables;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * Implements the {@link JDBCResolver} for SQL queries with parameters in their prepared statements specified
 * as XACML Attribute values.
 */
public class ConfigurableJDBCResolver implements JDBCResolver {
    public static final String PROP_SELECT = "select";
    public static final String PROP_SELECT_FIELDS = "fields";
    public static final String PROP_SELECT_FIELD = "field";
    public static final String PROP_SELECT_PARAMETERS = "parameters";
    public static final String PROP_SELECT_PARAMETER = "parameter";

    private Log logger = LogFactory.getLog(this.getClass());
    private String defaultIssuer;
    private Set<PIPRequest> supportedRequests = new HashSet<PIPRequest>();
    private Set<PIPRequest> supportedRequestsNoIssuer = new HashSet<PIPRequest>();
    private Map<String, PIPRequest> mapFields = new HashMap<String, PIPRequest>();
    private String sqlQuery;
    private List<PIPRequest> parameters = new ArrayList<PIPRequest>();
    private static DataTypeFactory dataTypeFactory = null;

    static {
        try {
            dataTypeFactory = DataTypeFactory.newInstance();
        } catch (Exception ex) {
            LogFactory.getLog(ConfigurableJDBCResolver.class).error("Exception geting DataTypeFactory: "
                                                                        + ex.toString(), ex);
        }
    }

    /**
     * Determines if the given {@link org.apache.openaz.xacml.api.pip.PIPRequest} can be answered with this
     * <code>ConfigurableJDBCResolver</code>.
     *
     * @param pipRequest the <code>PIPRequest</code> to check
     * @return true if the given <code>PIPRequest</code> is supported by this
     *         <code>ConfigurableJDBCResolver</code>, else false
     */
    protected boolean isSupported(PIPRequest pipRequest) {
        if (pipRequest.getIssuer() == null) {
            return this.supportedRequestsNoIssuer.contains(pipRequest);
        } else {
            return this.supportedRequests.contains(pipRequest);
        }
    }

    /**
     * Creates a new <code>ConfigurableJDBCResolver</code> that can provide XACML Attributes for the given
     * <code>Collection</code> of {@link org.apache.openaz.xacml.api.pip.PIPRequests}s. The mapping from
     * database table field names to XACML Attributes is provided by the <code>fieldsIn</code>
     * <code>Map</code>. The SQL query <code>String</code> is provided by <code>sqlQueryIn</code>. The query
     * string may contain prepared statement parameter place-holders <code>('?')</code>. The XACML Attributes
     * whose values are used for those place-holders are provided by the given <code>parametersIn</code>
     * <code>List</code>.
     *
     * @param supportedRequestsIn the <code>Collection</code> of <code>PIPRequest</code>s that are supported
     *            by the new <code>ConfiurableJDBCResolver</code>
     * @param fieldsIn the <code>Map</code> from <code>String</code> field names to <code>PIPRequest</code>s
     *            in the database table
     * @param sqlQueryIn the <code>String</code> SQL query that retrieves records that satisfy
     *            <code>PIPRequest</code>s
     * @param parametersIn the <code>List</code> of <code>PIPRequest</code>s representing parameter values
     *            found in <code>sqlQueryIn</code>.
     */
    /*
     * public ConfigurableJDBCResolver(Collection<PIPRequest> supportedRequestsIn, Map<String,PIPRequest>
     * fieldsIn, String sqlQueryIn, List<PIPRequest> parametersIn) { this(); if (supportedRequestsIn != null)
     * { this.supportedRequests.addAll(supportedRequestsIn); for (PIPRequest pipRequest : supportedRequestsIn)
     * { if (pipRequest.getIssuer() != null) { this.supportedRequestsNoIssuer.add(new
     * StdPIPRequest(pipRequest.getCategory(), pipRequest.getAttributeId(), pipRequest.getDataTypeId())); }
     * else { this.supportedRequestsNoIssuer.add(pipRequest); } } } if (fieldsIn != null) { for (String field
     * : fieldsIn.keySet()) { this.mapFields.put(field, fieldsIn.get(field)); } } this.sqlQuery = sqlQueryIn;
     * if (parametersIn != null) { this.parameters.addAll(parametersIn); } }
     */

    public ConfigurableJDBCResolver() {
        if (dataTypeFactory == null) {
            throw new IllegalStateException("No DataTypeFactory instance created");
        }
    }

    public Map<String, PIPRequest> getMapFields() {
        return mapFields;
    }

    public List<PIPRequest> getParameters() {
        return parameters;
    }

    public Properties generateProperties(String id, String select) {
        return generateProperties(id, select, this.mapFields, this.parameters);
    }

    public static Properties generateProperties(String id, String select, Map<String, PIPRequest> mapFields,
                                                List<PIPRequest> parameters) {
        Properties properties = new Properties();
        //
        // Set the select statement
        //
        properties.setProperty(Joiner.on('.').join(id, PROP_SELECT), select);
        //
        // Set the fields
        //
        if (mapFields.size() > 0) {
            properties.setProperty(Joiner.on('.').join(id, PROP_SELECT_FIELDS),
                                   Joiner.on(',').join(mapFields.keySet()));
            for (String field : mapFields.keySet()) {
                PIPRequest request = mapFields.get(field);
                String fieldPrefix = Joiner.on('.').join(id, PROP_SELECT_FIELD);
                properties.setProperty(fieldPrefix + ".id", request.getAttributeId().stringValue());
                properties.setProperty(fieldPrefix + ".datatype", request.getDataTypeId().stringValue());
                properties.setProperty(fieldPrefix + ".category", request.getCategory().stringValue());
                if (request.getIssuer() != null) {
                    properties.setProperty(fieldPrefix + ".issuer", request.getIssuer());
                }
            }
        }
        //
        // Set the parameters
        //
        if (parameters.size() > 0) {
            String params = "1";
            for (int i = 2; i <= parameters.size(); i++) {
                params = params + "," + i;
            }
            properties.setProperty(Joiner.on('.').join(id, PROP_SELECT_PARAMETERS), params);
            int position = 1;
            for (PIPRequest request : parameters) {
                String fieldPrefix = Joiner.on('.').join(id, PROP_SELECT_PARAMETER, position++);
                properties.setProperty(fieldPrefix + ".id", request.getAttributeId().stringValue());
                properties.setProperty(fieldPrefix + ".datatype", request.getDataTypeId().stringValue());
                properties.setProperty(fieldPrefix + ".category", request.getCategory().stringValue());
                if (request.getIssuer() != null) {
                    properties.setProperty(fieldPrefix + ".issuer", request.getIssuer());
                }
            }
        }
        return properties;
    }

    /*
     * protected PIPRequest getPIPRequest(String idPrefix, Properties properties) throws PIPException { String
     * stringProp = idPrefix + PROP_ID; String attributeId = properties.getProperty(stringProp); if
     * (attributeId == null || attributeId.length() == 0) { this.logger.error("No '" + stringProp +
     * "' property"); throw new PIPException("No '" + stringProp + "' property"); } stringProp = idPrefix +
     * PROP_DATATYPE; String dataTypeId = properties.getProperty(stringProp); if (dataTypeId == null ||
     * dataTypeId.length() == 0) { this.logger.error("No '" + stringProp + "' property"); throw new
     * PIPException("No '" + stringProp + "' property"); } stringProp = idPrefix + PROP_CATEGORY; String
     * categoryId = properties.getProperty(stringProp); if (categoryId == null) { this.logger.error("No '" +
     * stringProp + "' property"); throw new PIPException("No '" + stringProp + "' property"); } stringProp =
     * idPrefix + PROP_ISSUER; String issuer = properties.getProperty(stringProp); return new
     * StdPIPRequest(new IdentifierImpl(categoryId), new IdentifierImpl(attributeId), new
     * IdentifierImpl(dataTypeId), issuer); }
     */

    protected void configureField(String id, String fieldName, Properties properties) throws PIPException {
        PIPRequest pipRequestField = Configurables.getPIPRequest(id + "." + PROP_SELECT_FIELD + "."
                                                                 + fieldName, properties, this.defaultIssuer);
        this.supportedRequests.add(pipRequestField);
        this.supportedRequestsNoIssuer.add(new StdPIPRequest(pipRequestField.getCategory(), pipRequestField
            .getAttributeId(), pipRequestField.getDataTypeId()));
        this.mapFields.put(fieldName, pipRequestField);
    }

    protected void configureParameter(String id, String parameterName, Properties properties)
        throws PIPException {
        PIPRequest pipRequestParameter = Configurables.getPIPRequest(id + "." + PROP_SELECT_PARAMETER + "."
                                                                     + parameterName, properties, null);
        this.parameters.add(pipRequestParameter);
    }

    @Override
    public void configure(String id, Properties properties, String defaultIssuer) throws PIPException {
        /*
         * Save our default issuer
         */
        this.defaultIssuer = defaultIssuer;
        /*
         * Get the SELECT statement to be used in the prepared statement
         */
        String idPrefix = id + ".";
        String stringProp = idPrefix + PROP_SELECT;
        this.sqlQuery = properties.getProperty(stringProp);
        if (this.sqlQuery == null || this.sqlQuery.length() == 0) {
            this.logger.error("No '" + stringProp + "' property");
            throw new PIPException("No '" + stringProp + "' property");
        }

        /*
         * Get the list of database columns returned by the query
         */
        stringProp = idPrefix + PROP_SELECT_FIELDS;
        String fields = properties.getProperty(stringProp);
        if (fields == null || fields.length() == 0) {
            this.logger.error("No '" + stringProp + "' property");
            throw new PIPException("No '" + stringProp + "' property");
        }
        for (String field : Splitter.on(',').trimResults().omitEmptyStrings().split(fields)) {
            this.configureField(id, field, properties);
        }

        /*
         * Get the list of query parameters. This may be null
         */
        stringProp = idPrefix + PROP_SELECT_PARAMETERS;
        String parameters = properties.getProperty(stringProp);
        if (parameters != null && parameters.length() > 0) {
            for (String parameter : Splitter.on(',').trimResults().omitEmptyStrings().split(parameters)) {
                this.configureParameter(id, parameter, properties);
            }
        }
    }

    @Override
    public PreparedStatement getPreparedStatement(PIPEngine pipEngine, PIPRequest pipRequest,
                                                  PIPFinder pipFinder, Connection connection)
        throws PIPException {
        /*
         * Do we support the request?
         */
        if (!this.isSupported(pipRequest)) {
            return null;
        }

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(this.sqlQuery);
        } catch (SQLException ex) {
            this.logger.error("SQLException creating PreparedStatement: " + ex.toString(), ex);
            // TODO: throw the exception or return a null PreparedStatement?
            return null;
        }

        if (this.parameters.size() > 0) {
            /*
             * Gather all of the AttributeValues for parameters to the prepared statement. For now, we assume
             * a single value for each parameter. If there are multiple values we will log an error and return
             * a null PreparedStatement. TODO: Should the interface change to return a cross-product of
             * PreparedStatements to deal with multiple values for parameters? If not, should we just take the
             * first value and use it as the parameter value?
             */
            for (int i = 0; i < this.parameters.size(); i++) {
                PIPRequest pipRequestParameter = this.parameters.get(i);
                PIPResponse pipResponse = pipFinder.getMatchingAttributes(pipRequestParameter, null);
                if (pipResponse.getStatus() == null || pipResponse.getStatus().isOk()) {
                    Collection<Attribute> listAttributes = pipResponse.getAttributes();
                    if (listAttributes.size() > 0) {
                        if (listAttributes.size() > 1) {
                            this.logger.error("PIPFinder returned more than one Attribute for "
                                              + pipRequestParameter.toString());
                            throw new PIPException("PIPFinder returned more than one Attribute for "
                                                   + pipRequestParameter.toString());
                        }
                        Collection<AttributeValue<?>> listAttributeValuesReturned = listAttributes.iterator()
                            .next().getValues();
                        if (listAttributeValuesReturned.size() > 0) {
                            if (listAttributeValuesReturned.size() > 1) {
                                this.logger.warn("PIPFinder returned more than one AttributeValue for "
                                                 + pipRequestParameter.toString());
                                return null;
                            }
                            AttributeValue<?> attributeValue = listAttributeValuesReturned.iterator().next();
                            Identifier identifierAttributeValueDataType = attributeValue.getDataTypeId();
                            try {
                                if (identifierAttributeValueDataType.equals(XACML3.ID_DATATYPE_INTEGER)) {
                                    preparedStatement.setInt(i + 1,
                                                             DataTypes.DT_INTEGER.convert(attributeValue
                                                                                              .getValue())
                                                                 .intValue());
                                } else if (identifierAttributeValueDataType.equals(XACML3.ID_DATATYPE_DOUBLE)) {
                                    preparedStatement.setDouble(i + 1, DataTypes.DT_DOUBLE
                                        .convert(attributeValue.getValue()));
                                } else if (identifierAttributeValueDataType
                                    .equals(XACML3.ID_DATATYPE_BOOLEAN)) {
                                    preparedStatement.setBoolean(i + 1, DataTypes.DT_BOOLEAN
                                        .convert(attributeValue.getValue()));
                                } else if (identifierAttributeValueDataType
                                    .equals(XACML3.ID_DATATYPE_DATETIME)) {
                                    ISO8601DateTime iso8601DateTime = DataTypes.DT_DATETIME
                                        .convert(attributeValue.getValue());
                                    java.sql.Date sqlDate = new java.sql.Date(iso8601DateTime.getCalendar()
                                        .getTimeInMillis());
                                    preparedStatement.setDate(i + 1, sqlDate, iso8601DateTime.getCalendar());
                                } else if (identifierAttributeValueDataType.equals(XACML3.ID_DATATYPE_DATE)) {
                                    ISO8601Date iso8601Date = DataTypes.DT_DATE.convert(attributeValue
                                        .getValue());
                                    java.sql.Date sqlDate = new java.sql.Date(iso8601Date.getCalendar()
                                        .getTimeInMillis());
                                    preparedStatement.setDate(i + 1, sqlDate, iso8601Date.getCalendar());
                                } else {
                                    preparedStatement.setString(i + 1, DataTypes.DT_STRING
                                        .convert(attributeValue.getValue()));
                                }
                            } catch (Exception ex) {
                                this.logger.error("Exception setting parameter " + (i + 1) + " to "
                                                  + attributeValue.toString() + ": " + ex.toString(), ex);
                                return null;
                            }
                        } else {
                            this.logger.warn("No AttributeValues returned for parameter "
                                             + pipRequestParameter.toString());
                            return null;
                        }
                    } else {
                        this.logger.warn("No Attributes returned for parameter "
                                         + pipRequestParameter.toString());
                        return null;
                    }
                } else {
                    this.logger.warn("PIPFinder returned status " + pipResponse.getStatus().toString());
                    return null;
                }
            }
        }

        return preparedStatement;
    }

    /**
     * Creates an {@link org.apache.openaz.xacml.api.Attribute} from the value associated with the field with
     * the given <code>fieldName</code>.
     *
     * @param resultSet the {@link java.sql.ResultSet} containing the current row from the database
     * @param fieldName the <code>String</code> name of the field containing the attribute value
     * @param pipRequestAttribute the {@link org.apache.openaz.xacml.api.pip.PIPRequest} for the
     *            <code>Attribute</code> to create
     * @return a new <code>Attribute</code> with the value of the given <code>fieldName</code>.
     */
    protected Attribute getAttributeFromResultSet(ResultSet resultSet, String fieldName,
                                                  PIPRequest pipRequestAttribute) {
        AttributeValue<?> attributeValue = null;
        Identifier identifierDataType = pipRequestAttribute.getDataTypeId();
        try {
            DataType<?> dataType = dataTypeFactory.getDataType(identifierDataType);
            if (dataType == null) {
                this.logger.warn("Unknown data type " + pipRequestAttribute.getDataTypeId().stringValue());
                return null;
            }
            /*
             * Try to find the column index
             */

            int columnIndex = -1;

            try {
                columnIndex = resultSet.findColumn(fieldName);
            } catch (Exception e) {
                /*
                 * The field name could be an integer, let's try that
                 */
                try {
                    columnIndex = Integer.parseInt(fieldName);
                } catch (Exception e1) {
                    logger.error("Failed to find column with label " + fieldName);
                }
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Column " + fieldName + " maps to column index " + columnIndex);
            }

            /*
             * Catch special cases for database types
             */
            if (identifierDataType.equals(XACML3.ID_DATATYPE_BOOLEAN)) {
                attributeValue = dataType.createAttributeValue(resultSet.getBoolean(columnIndex));
            } else if (identifierDataType.equals(XACML3.ID_DATATYPE_DATE)
                       || identifierDataType.equals(XACML3.ID_DATATYPE_DATETIME)) {
                attributeValue = dataType.createAttributeValue(resultSet.getDate(columnIndex));
            } else if (identifierDataType.equals(XACML3.ID_DATATYPE_DOUBLE)) {
                attributeValue = dataType.createAttributeValue(resultSet.getDouble(columnIndex));
            } else if (identifierDataType.equals(XACML3.ID_DATATYPE_INTEGER)) {
                attributeValue = dataType.createAttributeValue(resultSet.getInt(columnIndex));
            } else {
                /*
                 * Default to convert the string value from the database to the requested data type
                 */
                String stringValue = resultSet.getString(columnIndex);
                if (stringValue != null) {
                    attributeValue = dataType.createAttributeValue(stringValue);
                }
            }
        } catch (Exception ex) {
            this.logger.error("Exception getting value for fieldName '" + fieldName + "' as a "
                              + identifierDataType.stringValue() + ": " + ex.toString(), ex);
            return null;
        }
        String issuer = this.defaultIssuer;
        if (pipRequestAttribute.getIssuer() != null) {
            issuer = pipRequestAttribute.getIssuer();
        }
        return new StdAttribute(pipRequestAttribute.getCategory(), pipRequestAttribute.getAttributeId(),
                                attributeValue, issuer, false);
    }

    @Override
    public List<Attribute> decodeResult(ResultSet resultSet) throws PIPException {
        List<Attribute> listAttributes = new ArrayList<Attribute>();
        for (String fieldName : this.mapFields.keySet()) {
            PIPRequest pipRequestField = this.mapFields.get(fieldName);
            assert pipRequestField != null;

            Attribute attribute = this.getAttributeFromResultSet(resultSet, fieldName, pipRequestField);
            if (attribute != null) {
                listAttributes.add(attribute);
            }
        }
        return listAttributes;
    }

    @Override
    public void attributesRequired(Collection<PIPRequest> parameters) {
        for (PIPRequest parameter : this.parameters) {
            parameters.add(new StdPIPRequest(parameter.getCategory(), parameter.getAttributeId(), parameter
                .getDataTypeId(), parameter.getIssuer()));
        }
    }

    @Override
    public void attributesProvided(Collection<PIPRequest> attributes) {
        for (String key : this.mapFields.keySet()) {
            PIPRequest attribute = this.mapFields.get(key);
            attributes.add(new StdPIPRequest(attribute.getCategory(), attribute.getAttributeId(), attribute
                .getDataTypeId(),
                                             (attribute.getIssuer() != null
                                                 ? attribute.getIssuer() : this.defaultIssuer)));
        }
    }

}
