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
package org.apache.openaz.xacml.std.pip.engines.ldap;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

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
import org.apache.openaz.xacml.std.StdAttribute;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.apache.openaz.xacml.std.pip.StdPIPRequest;
import org.apache.openaz.xacml.std.pip.engines.Configurables;
import org.apache.openaz.xacml.util.FactoryException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class ConfigurableLDAPResolver implements LDAPResolver {

    private static DataTypeFactory dataTypeFactory = null;

    static {
        try {
            dataTypeFactory = DataTypeFactory.newInstance();
        } catch (FactoryException fx) {
            throw new RuntimeException(fx);
        }
        Velocity.setProperty("runtime.log.logsystem.log4j.logger", "MAIN_LOG");
        Velocity.init();
    }

    private Log logger = LogFactory.getLog(this.getClass());

    private String defaultIssuer;
    private String id;
    private String base;
    private String filter;
    private Map<String, PIPRequest> baseParameters;
    private Map<String, PIPRequest> filterParameters;
    private Map<String, PIPRequest> filterView;

    public ConfigurableLDAPResolver() {
    }

    @Override
    public void configure(String id, Properties properties, String defaultIssuer) throws PIPException {
        /*
         * Save these values
         */
        this.id = id;
        this.defaultIssuer = defaultIssuer;

        this.base = properties.getProperty(id + ".base");
        this.filter = properties.getProperty(id + ".filter");
        Set<String> baseParametersNames = prepareVelocityTemplate(this.base);
        Set<String> filterParametersNames = prepareVelocityTemplate(this.filter);

        this.baseParameters = Configurables.getPIPRequestMap(id + ".base", "parameters", properties, null);

        this.filterParameters = Configurables
            .getPIPRequestMap(id + ".filter", "parameters", properties, null);

        // make sure we have all required parameters
        if (!this.baseParameters.keySet().containsAll(baseParametersNames)) {
            throw new PIPException(
                                   "The 'base' template contains parameters that were not specified in its map.");
        }
        if (!this.filterParameters.keySet().containsAll(filterParametersNames)) {
            throw new PIPException(
                                   "The 'filter' template contains parameters that were not specified in its map.");
        }
        this.filterView = Configurables.getPIPRequestMap(id + ".filter", "view", properties, defaultIssuer);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("(" + id + ") " + "\nbase '" + this.base + "', parameters "
                              + this.baseParameters + "\nfilter '" + this.filter + "', parameters "
                              + this.filterParameters + ", view " + this.filterView);
        }
    }

    public void store(String id, Properties properties) throws PIPException {
        properties.setProperty(id + ".base", this.base);
        properties.setProperty(id + ".filter", this.filter);
        Configurables.setPIPRequestMap(this.baseParameters, id + ".base", "parameters", properties);
        Configurables.setPIPRequestMap(this.filterParameters, id + ".filter", "parameters", properties);
        Configurables.setPIPRequestMap(this.filterView, id + ".filter", "view", properties);
    }

    /*
     * @return the set of parameters names required by the given velocity template
     */
    private Set<String> prepareVelocityTemplate(String template) throws PIPException {
        VelocityContext vctx = new VelocityContext();
        EventCartridge vec = new EventCartridge();
        VelocityParameterReader reader = new VelocityParameterReader();
        vec.addEventHandler(reader);
        vec.attachToContext(vctx);

        try {
            Velocity.evaluate(vctx, new StringWriter(), "LdapResolver", template);
        } catch (ParseErrorException pex) {
            throw new PIPException("Velocity template preparation failed", pex);
        } catch (MethodInvocationException mix) {
            throw new PIPException("Velocity template preparation failed", mix);
        } catch (ResourceNotFoundException rnfx) {
            throw new PIPException("Velocity template preparation failed", rnfx);
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("(" + id + ") " + template + " with parameters " + reader.parameters);
        }

        return reader.parameters;
    }

    private String evaluateVelocityTemplate(String template,
                                            final Map<String, PIPRequest> templateParameters,
                                            final PIPFinder pipFinder)
        throws PIPException {
        StringWriter out = new StringWriter();
        VelocityContext vctx = new VelocityContext();
        EventCartridge vec = new EventCartridge();
        VelocityParameterWriter writer = new VelocityParameterWriter(pipFinder, templateParameters);
        vec.addEventHandler(writer);
        vec.attachToContext(vctx);

        try {
            Velocity.evaluate(vctx, out, "LdapResolver", template);
        } catch (ParseErrorException pex) {
            throw new PIPException("Velocity template evaluation failed", pex);
        } catch (MethodInvocationException mix) {
            throw new PIPException("Velocity template evaluation failed", mix);
        } catch (ResourceNotFoundException rnfx) {
            throw new PIPException("Velocity template evaluation failed", rnfx);
        }

        this.logger.warn("(" + id + ") " + " template yields " + out.toString());

        return out.toString();
    }

    private Object evaluatePIPRequest(PIPRequest pipRequest, PIPFinder pipFinder)
        throws PIPException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("(" + id + ") " + pipRequest);
        }
        PIPResponse pipResponse = pipFinder.getMatchingAttributes(pipRequest, null);
        if (pipResponse.getStatus() == null || pipResponse.getStatus().isOk()) {
            Collection<Attribute> listAttributes = pipResponse.getAttributes();
            if (listAttributes.size() > 0) {
                if (listAttributes.size() > 1) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("(" + id + ") " + "PIPFinder returned more than one Attribute for "
                                          + pipRequest);
                    }
                    throw new PIPException("PIPFinder returned more than one Attribute for "
                                           + pipRequest.toString());
                }
                Collection<AttributeValue<?>> listAttributeValuesReturned = listAttributes.iterator().next()
                    .getValues();
                if (listAttributeValuesReturned.size() > 0) {
                    if (listAttributeValuesReturned.size() > 1) {
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("(" + id + ") "
                                              + "PIPFinder returned more than one AttributeValue for "
                                              + pipRequest);
                        }
                        return null;
                    }
                    AttributeValue<?> attributeValue = listAttributeValuesReturned.iterator().next();
                    // this is to hoping the string representation of the value is accurate
                    try {
                        return DataTypes.DT_STRING.convert(attributeValue.getValue());
                    } catch (DataTypeException dtx) {
                        throw new PIPException("Fauiled to extract attribute value", dtx);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String getBase(PIPEngine pipEngine, PIPRequest pipRequest, PIPFinder pipFinder)
        throws PIPException {

        if (!filterView.containsValue(pipRequest)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("(" + id + ") " + pipRequest + " not in " + filterView);
            }
            return null;
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("(" + id + ") " + pipRequest);
        }
        return evaluateVelocityTemplate(this.base, this.baseParameters, pipFinder);
    }

    public void setBase(String base) throws PIPException {
        Set<String> baseParametersNames = prepareVelocityTemplate(base);
        // make sure we have all required parameters
        if (!this.baseParameters.keySet().containsAll(baseParametersNames)) {
            throw new PIPException(
                                   "The 'base' template contains parameters that were not specified in its map.");
        }
        this.base = base;
    }

    @Override
    public String getFilterString(PIPEngine pipEngine, PIPRequest pipRequest, PIPFinder pipFinder)
        throws PIPException {

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("(" + id + ") " + pipRequest);
        }

        if (!filterView.containsValue(pipRequest)) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("(" + id + ") " + "request " + pipRequest + " not in " + filterView);
            }
            return null;
        }

        return evaluateVelocityTemplate(this.filter, this.filterParameters, pipFinder);
    }

    public void setFilterString(String filter) throws PIPException {
        Set<String> filterParametersNames = prepareVelocityTemplate(filter);
        // make sure we have all required parameters
        if (!this.filterParameters.keySet().containsAll(filterParametersNames)) {
            throw new PIPException(
                                   "The 'filter' template contains parameters that were not specified in its map.");
        }
        this.filter = filter;
    }

    private Attribute decodeResultValue(SearchResult searchResult, String view, PIPRequest viewRequest) {
        AttributeValue<?> attributeValue = null;
        Collection<AttributeValue<?>> attributeMultiValue = null;
        DataType<?> dataType = null;

        this.logger.warn("(" + id + ") " + "SearchResult attributes: " + searchResult.getAttributes());
        try {
            dataType = dataTypeFactory.getDataType(viewRequest.getDataTypeId());
            if (dataType == null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("(" + id + ") " + "Unknown data type in " + viewRequest);
                }
                return null;
            }

            if ("dn".equalsIgnoreCase(view)) {
                attributeValue = dataType.createAttributeValue(searchResult.getNameInNamespace());
            } else {
                javax.naming.directory.Attribute dirAttr = searchResult.getAttributes().get(view);
                if (dirAttr != null) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("(" + id + ") " + "directory attribute '" + view + "' value is '"
                                          + dirAttr + "'");
                    }
                    // we could guide this more elaborately by object class ..
                    if (dirAttr.size() == 1) {
                        attributeValue = dataType.createAttributeValue(dirAttr.get().toString());
                    } else {
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("(" + id + ") " + "SearchResult yields a multi-valued '" + view
                                              + "'");
                        }
                        attributeMultiValue = new HashSet<AttributeValue<?>>();
                        // we should
                        for (int i = 0; i < dirAttr.size(); i++) {
                            attributeMultiValue.add(dataType.createAttributeValue(dirAttr.get().toString()));
                        }
                    }
                } else {
                    this.logger.warn("(" + id + ") " + "SearchResult did not provide a value for '" + view
                                     + "'");
                    return null;
                }
            }
        } catch (DataTypeException dtx) {
            this.logger.error("(" + id + ") " + "Failed to decode search result", dtx);
            return null;
        } catch (NamingException nx) {
            this.logger.error("(" + id + ") " + "Failed to decode search result", nx);
            return null;
        }

        Attribute attr = null;
        if (attributeMultiValue == null) {
            attr = new StdAttribute(viewRequest.getCategory(), viewRequest.getAttributeId(), attributeValue,
                                    viewRequest.getIssuer(), false);
        } else {
            attr = new StdAttribute(viewRequest.getCategory(), viewRequest.getAttributeId(),
                                    attributeMultiValue, viewRequest.getIssuer(), false);
        }
        this.logger.warn("(" + id + ") " + " providing attribute " + attr);
        return attr;
    }

    @Override
    public List<Attribute> decodeResult(SearchResult searchResult) throws PIPException {
        List<Attribute> attributes = new ArrayList<Attribute>();
        for (Map.Entry<String, PIPRequest> viewEntry : this.filterView.entrySet()) {
            Attribute attribute = this.decodeResultValue(searchResult, viewEntry.getKey(),
                                                         viewEntry.getValue());
            if (attribute != null) {
                attributes.add(attribute);
            }
        }
        return attributes;
    }

    private class VelocityParameterHandler implements ReferenceInsertionEventHandler {

        /* velocity parameter pattern: we're just trying to extract the name */
        private Pattern vpp = Pattern.compile("\\{(\\w)+\\}");

        @Override
        public Object referenceInsert(String theReference, Object theValue) {
            /*
             * unfortunately Velocity does not give us simply the variable name but it's whole template
             * representation, i.e. ${var_name} or derivatives. We look for whatever is between { and }
             */
            Matcher vvm = vpp.matcher(theReference);
            String param = null;
            // Check all occurance
            if (vvm.find()) {
                String vv = vvm.group();
                param = vv.substring(1, vv.length() - 1);
            } else {
                // variable name pattern not right?
                param = "";
            }
            if (ConfigurableLDAPResolver.this.logger.isTraceEnabled()) {
                ConfigurableLDAPResolver.this.logger.trace("(" + id + ") " + "Velocity parameter: " + param);
            }
            return param;
        }
    }

    /* */
    private class VelocityParameterReader extends VelocityParameterHandler {

        private Set<String> parameters = new HashSet<String>();

        @Override
        public Object referenceInsert(String theReference, Object theValue) {
            String param = (String)super.referenceInsert(theReference, theValue);
            parameters.add(param);
            return "";
        }
    }

    private class VelocityParameterWriter extends VelocityParameterHandler {

        private PIPFinder finder;
        private Map<String, PIPRequest> parameters;

        public VelocityParameterWriter(PIPFinder finder, Map<String, PIPRequest> parameters) {
            this.finder = finder;
            this.parameters = parameters;
        }

        @Override
        public Object referenceInsert(String theReference, Object theValue) {

            String param = (String)super.referenceInsert(theReference, theValue);
            try {
                PIPRequest request = parameters.get(param);
                if (ConfigurableLDAPResolver.this.logger.isTraceEnabled()) {
                    ConfigurableLDAPResolver.this.logger.trace("(" + id + ") " + "Velocity parameter: "
                                                               + param + " requests " + request);
                }
                if (null == request)
                    throw new RuntimeException("Parameter '" + param + "' is not available");
                Object val = ConfigurableLDAPResolver.this.evaluatePIPRequest(request, this.finder);

                if (null != val) {
                    return val;
                } else {
                    if (param.startsWith("_")) {
                        return "*";
                    } else {
                        return null;
                    }
                }
            } catch (PIPException pipx) {
                throw new RuntimeException(pipx);
            }
        }
    }

    @Override
    public void attributesRequired(Collection<PIPRequest> attributes) {
        for (String key : this.filterView.keySet()) {
            attributes.add(new StdPIPRequest(this.filterView.get(key)));
        }
    }

    @Override
    public void attributesProvided(Collection<PIPRequest> attributes) {
        for (String key : this.filterParameters.keySet()) {
            PIPRequest attribute = this.filterParameters.get(key);
            attributes.add(new StdPIPRequest(attribute.getCategory(), attribute.getAttributeId(), attribute
                .getDataTypeId(),
                                             (attribute.getIssuer() != null
                                                 ? attribute.getIssuer() : this.defaultIssuer)));
        }
    }

}
