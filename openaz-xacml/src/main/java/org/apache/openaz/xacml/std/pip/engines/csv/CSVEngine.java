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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.DataTypeFactory;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.api.pip.PIPResponse;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.pip.StdMutablePIPResponse;
import org.apache.openaz.xacml.std.pip.StdPIPResponse;
import org.apache.openaz.xacml.std.pip.engines.StdConfigurableEngine;
import org.apache.openaz.xacml.util.AttributeUtils;
import org.apache.openaz.xacml.util.FactoryException;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Splitter;

public class CSVEngine extends StdConfigurableEngine {

    protected Log logger = LogFactory.getLog(this.getClass());
    /*
     * Files that are smaller than this number are read into memory during startup. Larger files are read one
     * line at a time as needed to avoid overloading the JVM memory limit.
     */
    public static final long DEFAULT_MAX_FILE_SIZE_FOR_READALL = 100000000;

    public static final String PROP_CLASSNAME = "classname";

    public static final String PROP_MAXSIZE = "maxsize";
    public static final String PROP_SOURCE = "source";
    public static final String PROP_DELIMITER = "delimiter";
    public static final String PROP_QUOTE = "quote";
    public static final String PROP_SKIP = "skip";

    public static final String PROP_RESOLVERS = "resolvers";
    public static final String PROP_RESOLVER = "resolver";

    private static DataTypeFactory dataTypeFactory = null;

    static {
        try {
            dataTypeFactory = DataTypeFactory.newInstance();
        } catch (FactoryException fx) {
            throw new RuntimeException(fx);
        }
    }

    //
    // Values read from the properties file for use in managing the CSV file
    //
    private long maximumSize = DEFAULT_MAX_FILE_SIZE_FOR_READALL;
    private File csvSourceFile;
    private char csvDelimiter;
    private char csvQuote;
    private int csvSkip;
    //
    // big files must be read one line at a time; small files are read in all at once
    //
    private boolean fileIsBig = false;
    //
    // small files get all lines read at once into this list
    //
    private List<String[]> allLines = null;

    //
    // Our list of resolvers
    //
    private List<CSVResolver> csvResolvers = new ArrayList<CSVResolver>();

    public CSVEngine() {
    }

    /**
     * Helper to read the top-level propertied for the CSV file as-a-whole
     *
     * @param id
     * @param properties
     * @throws org.apache.openaz.xacml.api.pip.PIPException
     */
    private void readCSVFileConfiguration(String id, Properties properties) throws PIPException {

        String prefix = id + ".";
        //
        // Is there a max filesize to read into memory?
        //
        String maxSize = properties.getProperty(prefix + PROP_MAXSIZE,
                                                Long.toString(DEFAULT_MAX_FILE_SIZE_FOR_READALL));
        try {
            this.maximumSize = Long.parseLong(maxSize);
        } catch (NumberFormatException e) {
            String message = this.getName() + ": The maximum size specified is NOT parseable: "
                             + e.getLocalizedMessage();
            this.logger.error(message);
            this.maximumSize = DEFAULT_MAX_FILE_SIZE_FOR_READALL;
        }
        //
        // Get the file source
        //
        String sourcePathString = properties.getProperty(prefix + PROP_SOURCE);
        if (sourcePathString == null || sourcePathString.length() == 0) {
            String message = this.getName() + ": No csv.source parameter given";
            logger.error(message);
            throw new PIPException(message);
        }
        //
        // Now check the size of that file (and if it exists)
        //
        csvSourceFile = new File(sourcePathString);
        if (!csvSourceFile.exists() || csvSourceFile.length() == 0) {
            String message = this.getName() + ": The csv.source '" + csvSourceFile.getAbsolutePath()
                             + "' does not exist or has no content";
            logger.error(message);
            throw new PIPException(message);
        }
        if (csvSourceFile.length() > this.maximumSize) {
            if (logger.isDebugEnabled()) {
                logger.debug("File size is greater than max allowed (" + this.maximumSize + "): "
                             + csvSourceFile.length());
            }
            fileIsBig = true;
        }
        //
        // Get the properties for CSVReader
        //
        String tmpString = properties.getProperty(prefix + PROP_DELIMITER);
        if (tmpString == null || tmpString.length() != 1) {
            String message = this.getName() + ": The csv.delimiter must exist and be exactly 1 character";
            logger.error(message);
            throw new PIPException(message);
        }
        csvDelimiter = tmpString.charAt(0);

        tmpString = properties.getProperty(prefix + PROP_QUOTE);
        if (tmpString == null || tmpString.length() != 1) {
            String message = this.getName() + ": The csv.quote must exist and be exactly 1 character";
            logger.error(message);
            throw new PIPException(message);
        }
        csvQuote = tmpString.charAt(0);

        tmpString = properties.getProperty(prefix + PROP_SKIP);
        if (tmpString == null) {
            String message = this.getName() + ": The csv.skip must be set";
            logger.error(message);
            throw new PIPException(message);
        }
        try {
            csvSkip = Integer.parseInt(tmpString);
        } catch (NumberFormatException e) {
            String message = this.getName() + ": The csv.skip value of '" + tmpString
                             + "' cannot be converted to integer";
            logger.error(message);
            throw new PIPException(message);
        }
    }

    @Override
    public void configure(String id, Properties properties) throws PIPException {
        //
        // Get our standard configurable properties
        //
        super.configure(id, properties);
        //
        // Get the properties this class cares about
        //
        this.readCSVFileConfiguration(id, properties);
        //
        // Get resolvers
        //
        String propResolverPrefix = id + "." + PROP_RESOLVERS;
        String stringProp = properties.getProperty(propResolverPrefix);
        if (stringProp == null || stringProp.isEmpty()) {
            this.logger.error("No '" + propResolverPrefix + "' property");
            throw new PIPException("No '" + propResolverPrefix + "' property");
        }
        //
        // Go through all our resolvers
        //
        for (String resolverId : Splitter.on(',').trimResults().omitEmptyStrings().split(stringProp)) {
            this.createResolver(id + "." + PROP_RESOLVER + "." + resolverId, properties);
        }
        //
        // If the file is small, we read it fully into memory.
        //
        if (!this.fileIsBig) {
            try (CSVReader csvReader = new CSVReader(new FileReader(csvSourceFile), csvDelimiter, csvQuote,
                                                     csvSkip)) {
                this.allLines = csvReader.readAll();
                if (logger.isDebugEnabled()) {
                    logger.debug(id + ": All lines read from csv file, size=" + allLines.size());
                }
            } catch (IOException e) {
                String message = id + ": CSVReader unable to read csv.source '"
                                 + csvSourceFile.getAbsolutePath() + "': " + e;
                logger.error(message, e);
                throw new PIPException(message);
            }
        }
    }

    /**
     * Creates a new {@link org.apache.openaz.xacml.std.pip.engines.csv.CSVResolver} by looking up the
     * "classname" property for the given <code>String</code> resolver ID and then calling its
     * <code>configure</code> method.
     *
     * @param resolverId the <code>String</code> identifier of the resolver to configure
     * @param properties the <code>Properties</code> to search for the "classname" and any resolver-specific
     *            properties
     * @throws org.apache.openaz.xacml.api.pip.PIPException if there is an error creating the
     *             <code>CSVResolver</code>.
     */
    protected void createResolver(String resolverId, Properties properties) throws PIPException {
        String propPrefix = resolverId + ".";
        String resolverClassName = properties.getProperty(propPrefix + PROP_CLASSNAME);
        if (resolverClassName == null || resolverClassName.length() == 0) {
            this.logger.error("No '" + propPrefix + PROP_CLASSNAME + "' property.");
            throw new PIPException("No '" + propPrefix + PROP_CLASSNAME + "' property.");
        }
        try {
            Class<?> resolverClass = Class.forName(resolverClassName);
            if (!CSVResolver.class.isAssignableFrom(resolverClass)) {
                this.logger.error("CSVResolver class " + propPrefix + " does not implement "
                                  + CSVResolver.class.getCanonicalName());
                throw new PIPException("CSVResolver class " + propPrefix + " does not implement "
                                       + CSVResolver.class.getCanonicalName());

            }
            //
            // Try to create the resolver
            //
            CSVResolver csvResolver = CSVResolver.class.cast(resolverClass.newInstance());
            //
            // Make sure it can configure itself
            //
            csvResolver.configure(resolverId, properties, this.getIssuer());
            //
            // Good
            //
            this.csvResolvers.add(csvResolver);
        } catch (Exception ex) {
            this.logger.error("Exception creating CSVResolver: " + ex.getMessage(), ex);
            throw new PIPException("Exception creating CSVResolver", ex);
        }
    }

    @Override
    public PIPResponse getAttributes(PIPRequest pipRequest, PIPFinder pipFinder) throws PIPException {
        //
        // Do we have any resolvers defined?
        //
        if (this.csvResolvers.size() == 0) {
            throw new IllegalStateException(this.getClass().getCanonicalName() + " is not configured");
        }
        //
        // Do any of our resolvers support this?
        //
        List<CSVResolver> resolvers = new ArrayList<CSVResolver>();
        for (CSVResolver resolver : this.csvResolvers) {
            if (resolver.supportRequest(pipRequest)) {
                resolvers.add(resolver);
            }
        }
        if (resolvers.size() == 0) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("does not support this pip request: " + pipRequest);
            }
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("supports this pip request: " + pipRequest);
        }
        //
        // We have at least one, have the resolvers prepare themselves.
        //
        List<Map<Integer, List<AttributeValue<?>>>> listParameters = new ArrayList<Map<Integer, List<AttributeValue<?>>>>();
        for (CSVResolver resolver : resolvers) {
            Map<Integer, List<AttributeValue<?>>> map = resolver.getColumnParameterValues(this, pipRequest,
                                                                                          pipFinder);
            //
            // If the resolver cannot find all its parameter values, then we
            //
            if (map == null || map.isEmpty()) {
                this.logger.warn("Resolver could not find parameters.");
                return StdPIPResponse.PIP_RESPONSE_EMPTY;
            }
            listParameters.add(map);
        }
        //
        // Look at each line of the file to see if it matches the (non-unique) criteria in the parameters
        // and add the value in the associated column from the CSV file to the list of response Attributes.
        //
        StdMutablePIPResponse mutablePIPResponse = new StdMutablePIPResponse();
        //
        // for smaller files, this is the index in the allLines List
        //
        int lineIndex = 0;
        //
        // for big files we need to read one line at a time from the CSVReader
        //
        CSVReader csvReader = null;

        try {
            if (this.fileIsBig) {
                csvReader = new CSVReader(new FileReader(csvSourceFile), csvDelimiter, csvQuote, csvSkip);
            }

            while (true) {
                String[] line = null;
                if (this.fileIsBig) {
                    line = csvReader.readNext();
                    if (line == null) {
                        // end of file
                        break;
                    }
                } else {
                    if (lineIndex < this.allLines.size()) {
                        line = this.allLines.get(lineIndex);
                        lineIndex++;
                    } else {
                        //
                        // end of (previously-read) list
                        //
                        break;
                    }
                }
                //
                // Does the line match?
                //
                if (!this.doesLineMatch(line, listParameters)) {
                    continue;
                }
                //
                // Ask each resolver to return any attributes from the line
                //
                for (CSVResolver resolver : resolvers) {
                    List<Attribute> attributes = resolver.decodeResult(line);
                    if (attributes != null && attributes.size() > 0) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("resolver returned " + attributes.size() + " attributes");
                        }
                        mutablePIPResponse.addAttributes(attributes);
                    }
                }
            }
            //
            // Done reading the file
            //
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Returning " + mutablePIPResponse.getAttributes().size() + " attributes");
                for (Attribute attribute : mutablePIPResponse.getAttributes()) {
                    this.logger.debug(System.lineSeparator() + AttributeUtils.prettyPrint(attribute));
                }
            }
            return new StdPIPResponse(mutablePIPResponse);
        } catch (Exception e) {
            String message = this.getName() + ": Error processing line: " + e;
            logger.error(message, e);
            return new StdPIPResponse(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                    e.getMessage()));
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException e) {
                    this.logger.error("Close CSV Reader: " + e.getLocalizedMessage());
                }
            }
        }
    }

    protected boolean doesLineMatch(String[] line, List<Map<Integer, List<AttributeValue<?>>>> listParameters) {
        for (Map<Integer, List<AttributeValue<?>>> map : listParameters) {
            for (Integer column : map.keySet()) {
                //
                // Sanity check, we should have the required number of columns
                //
                if (column >= line.length) {
                    return false;
                }
                //
                // Does it have a value?
                //
                String lineValue = line[column];
                if (lineValue.length() == 0) {
                    return false;
                }
                //
                // Now check the value
                //
                boolean foundMatch = false;
                for (AttributeValue<?> value : map.get(column)) {
                    DataType<?> dt = dataTypeFactory.getDataType(value.getDataTypeId());
                    try {
                        //
                        // Convert the value. NOTE: This may be time-consuming as opposed to
                        // converting the parameter value to a String once and then doing string
                        // comparisons. But, using a CSV is already a performance issue and should
                        // only be used for testing purposes or very small data sets. Use of a cache
                        // can help performance if the CSV file is necessary in a production environment.
                        //
                        Object convertedValue = dt.convert(lineValue);
                        if (convertedValue.equals(value.getValue())) {
                            //
                            // It matches
                            //
                            foundMatch = true;
                            break;
                        }
                    } catch (DataTypeException e) {
                        String message = column + " could not convert lineValue to " + dt.getId();
                        this.logger.error(message);
                    }
                }
                //
                // Did a match happen?
                //
                if (!foundMatch) {
                    /*
                     * if (this.logger.isDebugEnabled()) {
                     * this.logger.debug("Failed to find value for column " + column); }
                     */
                    return false;
                }
            }
        }
        //
        // If we get here, we found at least one match for all the
        // parameters.
        //
        return true;
    }

    @Override
    public Collection<PIPRequest> attributesRequired() {
        Set<PIPRequest> requiredAttributes = new HashSet<PIPRequest>();
        for (CSVResolver resolver : this.csvResolvers) {
            resolver.attributesRequired(requiredAttributes);
        }
        return requiredAttributes;
    }

    @Override
    public Collection<PIPRequest> attributesProvided() {
        Set<PIPRequest> attributes = new HashSet<PIPRequest>();
        for (CSVResolver resolver : this.csvResolvers) {
            resolver.attributesProvided(attributes);
        }
        return attributes;
    }

}
