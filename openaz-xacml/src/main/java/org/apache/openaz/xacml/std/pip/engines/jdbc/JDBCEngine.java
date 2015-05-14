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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.api.pip.PIPResponse;
import org.apache.openaz.xacml.std.pip.StdMutablePIPResponse;
import org.apache.openaz.xacml.std.pip.StdPIPResponse;
import org.apache.openaz.xacml.std.pip.engines.StdConfigurableEngine;
import org.apache.openaz.xacml.util.AttributeUtils;

import com.google.common.base.Splitter;

/**
 * PIPEgineJDBC extends {@link org.apache.openaz.xacml.std.pip.engines.StdConfigurableEngine} to implement a
 * PIP that retrieves XACML attributes from a database using JDBC. This is a minimal implementation that does
 * not do any caching of results. It does not perform JDBC connection pooling either.
 */
public class JDBCEngine extends StdConfigurableEngine {
    public static final String PROP_TYPE = "type";
    public static final String PROP_JDBC_DRIVER = "jdbc.driver";
    public static final String PROP_JDBC_URL = "jdbc.url";
    public static final String PROP_JDBC_CONN = "jdbc.conn";
    public static final String PROP_JDBC_CONN_USER = "jdbc.conn.user";
    public static final String PROP_JDBC_CONN_PASS = "jdbc.conn.password";
    public static final String PROP_RESOLVERS = "resolvers";
    public static final String PROP_RESOLVER = "resolver";
    public static final String PROP_CLASSNAME = "classname";

    public static final String TYPE_JDBC = "jdbc";
    public static final String TYPE_JNDI = "jndi";

    protected Log logger = LogFactory.getLog(this.getClass());
    private String type;
    private String jndiDataSource;
    private String jdbcDriverClass;
    private boolean jdbcDriverClassLoaded;
    private String jdbcUrl;
    private Properties jdbcConnProperties = new Properties();
    private List<JDBCResolver> jdbcResolvers = new ArrayList<JDBCResolver>();

    /**
     * If the JDBC driver <code>Class</code> has not been loaded yet, do so now.
     *
     * @throws ClassNotFoundException
     */
    protected void loadDriverClass() throws ClassNotFoundException {
        if (!this.jdbcDriverClassLoaded) {
            synchronized (this) {
                if (!this.jdbcDriverClassLoaded) {
                    Class.forName(this.jdbcDriverClass);
                    this.jdbcDriverClassLoaded = true;
                }
            }
        }
    }

    /**
     * Creates a JDBC {@link java.sql.Connection} to the database. Extensions to the <code>JDBCEngine</code>
     * class can perform connection pooling or other connection reuse optimizations here.
     *
     * @return a <code>Connection</code> to use to execute the query
     * @throws org.apache.openaz.xacml.api.pip.PIPException if there is an error creating the JDBC
     *             <code>Connection</code>.
     */
    protected Connection getConnection() throws PIPException {
        /*
         * Check what type we are
         */
        if (this.type.equals(TYPE_JDBC)) {
            return this.getJDBCConnection();
        } else {
            return this.getJNDIConnection();
        }
    }

    protected Connection getJDBCConnection() throws PIPException {
        /*
         * Ensure the driver class is loaded
         */
        try {
            this.loadDriverClass();
        } catch (ClassNotFoundException ex) {
            this.logger.error("ClassNotFoundException loading JDBC driver class '" + this.jdbcDriverClass
                              + "'", ex);
            throw new PIPException("ClassNotFoundException loading JDBC driver class '"
                                   + this.jdbcDriverClass + "'", ex);
        }

        /*
         * Try to create a new Connection
         */
        Connection connectionResult = null;
        try {
            connectionResult = DriverManager.getConnection(this.jdbcUrl, this.jdbcConnProperties);
        } catch (SQLException ex) {
            this.logger.error("SQLException creating Connection", ex);
            throw new PIPException("SQLException creating Connection", ex);
        }

        return connectionResult;
    }

    protected Connection getJNDIConnection() throws PIPException {
        try {
            Context initialContext = new InitialContext();
            DataSource datasource = (DataSource)initialContext.lookup(this.jndiDataSource);
            if (datasource == null) {
                throw new PIPException("");
            }
            return datasource.getConnection();
        } catch (NamingException | SQLException e) {
            this.logger.error("JNDIException creating Connection", e);
            throw new PIPException("JNDIException creating Connection", e);
        }
    }

    protected void getAttributes(PIPRequest pipRequest, PIPFinder pipFinder, JDBCResolver jdbcResolver,
                                 StdMutablePIPResponse pipResponse) throws PIPException {
        /*
         * First we need to get a PreparedStatement
         */
        Connection connection = this.getConnection();
        PreparedStatement preparedStatement = jdbcResolver.getPreparedStatement(this, pipRequest, pipFinder,
                                                                                connection);
        if (preparedStatement == null) {
            this.logger.debug(this.getName() + " does not handle " + pipRequest.toString());
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) { //NOPMD
            }
            return;
        }

        /*
         * Is it in the cache?
         */
        this.logger.debug(preparedStatement.toString());
        // Cache<String, PIPResponse> cache = this.getCache();
        //if (cache != null) {
            // TODO - a cache key
        //}

        /*
         * Execute the prepared statement
         */
        ResultSet resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException ex) {
            this.logger.error("SQLException executing query: " + ex.toString(), ex);
            // TODO: Should we re-throw the exception, or just return an empty response?
        }
        if (resultSet == null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                this.logger.error("SQLException closing preparedStatment: " + e.toString(), e);
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) { //NOPMD
            }
            return;
        }
        try {
            /*
             * Get all the results
             */
            while (resultSet.next()) {
                List<Attribute> listAttributes = jdbcResolver.decodeResult(resultSet);
                if (listAttributes != null) {
                    pipResponse.addAttributes(listAttributes);
                }
            }
            /*
             * Save it in the cache
             */
            //if (cache != null) {
                // TODO
            //}
        } catch (SQLException ex) {
            this.logger.error("SQLException decoding results: " + ex.toString());
            // TODO: Should we re-throw the exception or just continue
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    this.logger.error("SQLException closing resultSet: " + e.toString()
                                      + "  (May be memory leak)");
                }
            }
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                this.logger.error("SQLException closing preparedStatement: " + e.toString()
                                  + "  (May be memory leak)");
            }
            try {
                connection.close();
            } catch (SQLException e) {
                this.logger.error("SQLException closing connection: " + e.toString()
                                  + "  (May be memory leak)");
            }
        }
    }

    @Override
    public PIPResponse getAttributes(PIPRequest pipRequest, PIPFinder pipFinder) throws PIPException {
        if (this.jdbcResolvers.size() == 0) {
            throw new IllegalStateException(this.getClass().getCanonicalName() + " is not configured");
        }

        StdMutablePIPResponse mutablePIPResponse = new StdMutablePIPResponse();
        for (JDBCResolver jdbcResolver : this.jdbcResolvers) {
            this.getAttributes(pipRequest, pipFinder, jdbcResolver, mutablePIPResponse);
        }
        if (mutablePIPResponse.getAttributes().size() == 0) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("returning empty response");
            }
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.trace("Returning " + mutablePIPResponse.getAttributes().size() + " attributes");
                for (Attribute attribute : mutablePIPResponse.getAttributes()) {
                    this.logger.debug(AttributeUtils.prettyPrint(attribute));
                }
            } /*else if (this.logger.isDebugEnabled()) {
                // this.logger.debug("Returning " + mutablePIPResponse.getAttributes().size() +
                // " attributes");
                // this.logger.debug(mutablePIPResponse.getAttributes());
            }*/
            return new StdPIPResponse(mutablePIPResponse);
        }
    }

    /**
     * Creates a new {@link org.apache.openaz.xacml.std.pip.engines.jdbc.JDBCResolver} by looking up the
     * "classname" property for the given <code>String</code> resolver ID and then calling its
     * <code>configure</code> method.
     *
     * @param resolverId the <code>String</code> identifier of the resolver to configure
     * @param properties the <code>Properties</code> to search for the "classname" and any resolver-specific
     *            properties
     * @throws org.apache.openaz.xacml.api.pip.PIPException if there is an error creating the
     *             <code>JDBCResolver</code>.
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
            if (!JDBCResolver.class.isAssignableFrom(resolverClass)) {
                this.logger.error("JDBCResolver class " + propPrefix + " does not implement "
                                  + JDBCResolver.class.getCanonicalName());
                throw new PIPException("JDBCResolver class " + propPrefix + " does not implement "
                                       + JDBCResolver.class.getCanonicalName());

            }
            JDBCResolver jdbcResolver = JDBCResolver.class.cast(resolverClass.newInstance());
            jdbcResolver.configure(resolverId, properties, this.getIssuer());
            this.jdbcResolvers.add(jdbcResolver);
        } catch (Exception ex) {
            this.logger.error("Exception creating JDBCResolver: " + ex.getMessage(), ex);
            throw new PIPException("Exception creating JDBCResolver", ex);
        }
    }

    @Override
    public void configure(String id, Properties properties) throws PIPException {
        //
        // Our configurable properties
        //
        super.configure(id, properties);
        //
        // Prefix
        //
        String propPrefix = id + ".";
        //
        // What is our type?
        //
        this.type = properties.getProperty(propPrefix + PROP_TYPE, TYPE_JDBC);
        //
        // These are mandatory for our engine to work.
        //
        if ((this.jdbcDriverClass = properties.getProperty(propPrefix + PROP_JDBC_DRIVER)) == null) {
            this.logger.error("No '" + propPrefix + PROP_JDBC_DRIVER + "' property");
            throw new PIPException("No '" + propPrefix + PROP_JDBC_DRIVER + "' property");
        }
        try {
            Class.forName(this.jdbcDriverClass);
        } catch (Exception ex) {
            this.logger.error("Exception instantiating JDBC driver class '" + this.jdbcDriverClass + "'", ex);
            throw new PIPException(
                                   "Exception instantiating JDBC driver class '" + this.jdbcDriverClass + "'",
                                   ex);
        }

        if ((this.jdbcUrl = properties.getProperty(propPrefix + PROP_JDBC_URL)) == null) {
            this.logger.error("No '" + propPrefix + PROP_JDBC_URL + "' property");
            throw new PIPException("No '" + propPrefix + PROP_JDBC_URL + "' property");
        }
        //
        // Go through all our resolvers
        //
        String propResolverPrefix = propPrefix + PROP_RESOLVERS;
        String stringProp = properties.getProperty(propResolverPrefix);
        if (stringProp == null || stringProp.isEmpty()) {
            this.logger.error("No '" + propResolverPrefix + "' property");
            throw new PIPException("No '" + propResolverPrefix + "' property");
        }
        for (String resolverId : Splitter.on(',').trimResults().omitEmptyStrings().split(stringProp)) {
            this.createResolver(propPrefix + PROP_RESOLVER + "." + resolverId, properties);
        }
        //
        // Check for these properties. They are not required.
        //
        if ((stringProp = properties.getProperty(propPrefix + PROP_JDBC_CONN_USER)) != null) {
            this.jdbcConnProperties.setProperty("user", stringProp);
        }
        if ((stringProp = properties.getProperty(propPrefix + PROP_JDBC_CONN_PASS)) != null) {
            this.jdbcConnProperties.setProperty("password", stringProp);
        }
        String jdbcConnPrefix = propPrefix + PROP_JDBC_CONN;
        if ((stringProp = properties.getProperty(jdbcConnPrefix)) != null) {
            jdbcConnPrefix = jdbcConnPrefix + ".";
            String[] connProperties = stringProp.split("[,]", 0);
            for (String connProperty : connProperties) {
                if ((stringProp = properties.getProperty(jdbcConnPrefix + connProperty)) != null) {
                    this.jdbcConnProperties.setProperty(connProperty, stringProp);
                }
            }
        }
    }

    @Override
    public Collection<PIPRequest> attributesRequired() {
        Set<PIPRequest> attributes = new HashSet<PIPRequest>();
        for (JDBCResolver jdbcResolver : this.jdbcResolvers) {
            jdbcResolver.attributesRequired(attributes);
        }
        return attributes;
    }

    @Override
    public Collection<PIPRequest> attributesProvided() {
        Set<PIPRequest> attributes = new HashSet<PIPRequest>();
        for (JDBCResolver jdbcResolver : this.jdbcResolvers) {
            jdbcResolver.attributesProvided(attributes);
        }
        return attributes;
    }
}
