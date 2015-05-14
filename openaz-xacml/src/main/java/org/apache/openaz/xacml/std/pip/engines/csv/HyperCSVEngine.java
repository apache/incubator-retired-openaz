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

import java.util.Properties;
import java.nio.file.FileSystems;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.std.pip.engines.jdbc.JDBCEngine;

public class HyperCSVEngine extends JDBCEngine {

    public static final String PROP_SOURCE = "source";
    public static final String PROP_TARGET = "target";
    public static final String PROP_DEFINITION = "definition";

    public static final String HYPER_DRIVER = "org.hsqldb.jdbcDriver";
    public static final String HYPER_URL = "jdbc:hsqldb:mem:xacml";
    public static final String HYPER_USER = "sa";
    public static final String HYPER_PASS = "";

    private static enum HyperInitState {
        init,
        completed,
        failed
    };

    private String source, target, definition;
    private HyperInitState hyperInitState = HyperInitState.init;

    public HyperCSVEngine() {
    }

    @Override
    public void configure(String id, Properties properties) throws PIPException {
        // hyper sql global option
        System.getProperties().setProperty("textdb.allow_full_path", "true");

        // JDBCEngine props that are fixed for a in-memory hypersql text db
        String prop = null;
        prop = id + "." + PROP_JDBC_DRIVER;
        properties.setProperty(prop, HYPER_DRIVER);
        prop = id + "." + PROP_JDBC_URL;
        properties.setProperty(prop, HYPER_URL);
        prop = id + "." + PROP_JDBC_CONN_USER;
        properties.setProperty(prop, HYPER_USER);
        prop = id + "." + PROP_JDBC_CONN_PASS;
        properties.setProperty(prop, HYPER_PASS);

        super.configure(id, properties);

        prop = id + "." + PROP_DEFINITION;
        if ((this.definition = properties.getProperty(prop)) == null) {
            this.logger.error("Missing '" + prop + "' property");
            throw new PIPException("Missing '" + prop + "' property");
        }

        prop = id + "." + PROP_SOURCE;
        if ((this.source = properties.getProperty(prop)) == null) {
            this.logger.error("Missing '" + prop + "' property");
            throw new PIPException("Missing '" + prop + "' property");
        }

        prop = id + "." + PROP_TARGET;
        if ((this.target = properties.getProperty(prop)) == null) {
            this.target = FileSystems.getDefault().getPath(this.source).getFileName().toString();
            this.target = this.target.substring(0, this.target.indexOf('.'));
            this.logger.info("Target set to '" + this.target + "'");
        }
        //
        // early initialization
        //
        try {
            getConnection().close();
        } catch (SQLException sqlx) {
            throw new PIPException("The HyperSQL initialization failed");
        }
    }

    /*
     * Late initialization approach ..
     */
    @Override
    protected Connection getConnection() throws PIPException {
        switch (this.hyperInitState) {
        case init:
            try {
                hyperInit();
                this.hyperInitState = HyperInitState.completed;
            } catch (PIPException pipx) {
                this.hyperInitState = HyperInitState.failed;
                throw pipx;
            }
        case completed:
            return super.getConnection();
        case failed:
            throw new PIPException("The HyperSQL initialization failed");
        }
        throw new PIPException("?? How did we get here");
    }

    public void hyperReset() {
        this.hyperInitState = HyperInitState.init;
    }

    private void hyperInit() throws PIPException {
        this.logger.info("Starting csv load from '" + this.source + "' in '" + this.target + "'");

        StringBuilder createTable = new StringBuilder();
        createTable.append("CREATE TEXT TABLE IF NOT EXISTS ").append(this.target).append("(")
            .append(this.definition).append(")");

        StringBuilder linkTable = new StringBuilder();
        linkTable.append("SET TABLE ").append(this.target).append(" SOURCE ").append("\"")
            .append(this.source).append(";ignore_first=true;all_quoted=true\"");

        Connection conn = super.getConnection();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute(createTable.toString());
            stmt.execute(linkTable.toString());
        } catch (SQLException sqlx) {
            throw new PIPException("Failed to inititialize HyperSQL", sqlx);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqlx) { //NOPMD
            }
        }

        this.logger.info("Loading '" + this.target + "' from '" + this.source + "' completed");
    }
}
