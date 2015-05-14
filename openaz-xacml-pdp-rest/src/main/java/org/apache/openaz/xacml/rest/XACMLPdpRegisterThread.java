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
package org.apache.openaz.xacml.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.rest.XACMLPdpServlet.PutRequest;
import org.apache.openaz.xacml.util.XACMLProperties;

public class XACMLPdpRegisterThread implements Runnable {
    private static final Log logger = LogFactory.getLog(XACMLPdpRegisterThread.class);

    public volatile boolean isRunning = false;

    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    public synchronized void terminate() {
        this.isRunning = false;
    }

    /**
     * This is our thread that runs on startup to tell the PAP server we are up-and-running.
     */
    @Override
    public void run() {
        synchronized (this) {
            this.isRunning = true;
        }
        boolean registered = false;
        boolean interrupted = false;
        int seconds;
        try {
            seconds = Integer.parseInt(XACMLProperties
                .getProperty(XACMLRestProperties.PROP_PDP_REGISTER_SLEEP));
        } catch (NumberFormatException e) {
            logger.error("REGISTER_SLEEP: ", e);
            seconds = 5;
        }
        if (seconds < 5) {
            seconds = 5;
        }
        int retries;
        try {
            retries = Integer.parseInt(XACMLProperties
                .getProperty(XACMLRestProperties.PROP_PDP_REGISTER_RETRIES));
        } catch (NumberFormatException e) {
            logger.error("REGISTER_SLEEP: ", e);
            retries = -1;
        }
        while (!registered && !interrupted && this.isRunning()) {
            HttpURLConnection connection = null;
            try {
                //
                // Get the PAP Servlet URL
                //
                URL url = new URL(XACMLProperties.getProperty(XACMLRestProperties.PROP_PAP_URL));
                logger.info("Registering with " + url.toString());
                boolean finished = false;
                while (!finished) {
                    //
                    // Open up the connection
                    //
                    connection = (HttpURLConnection)url.openConnection();
                    //
                    // Setup our method and headers
                    //
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Accept", "text/x-java-properties");
                    connection.setRequestProperty("Content-Type", "text/x-java-properties");
                    connection.setRequestProperty(XACMLRestProperties.PROP_PDP_HTTP_HEADER_ID,
                                                  XACMLProperties
                                                      .getProperty(XACMLRestProperties.PROP_PDP_ID));
                    connection.setUseCaches(false);
                    //
                    // Adding this in. It seems the HttpUrlConnection class does NOT
                    // properly forward our headers for POST re-direction. It does so
                    // for a GET re-direction.
                    //
                    // So we need to handle this ourselves.
                    //
                    connection.setInstanceFollowRedirects(false);
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    try {
                        //
                        // Send our current policy configuration
                        //
                        String lists = XACMLProperties.PROP_ROOTPOLICIES + "="
                                       + XACMLProperties.getProperty(XACMLProperties.PROP_ROOTPOLICIES);
                        lists = lists + "\n" + XACMLProperties.PROP_REFERENCEDPOLICIES + "="
                                + XACMLProperties.getProperty(XACMLProperties.PROP_REFERENCEDPOLICIES) + "\n";
                        try (InputStream listsInputStream = new ByteArrayInputStream(lists.getBytes());
                            InputStream pipInputStream = Files.newInputStream(XACMLPdpLoader.getPIPConfig());
                            OutputStream os = connection.getOutputStream()) {
                            IOUtils.copy(listsInputStream, os);

                            //
                            // Send our current PIP configuration
                            //
                            IOUtils.copy(pipInputStream, os);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to send property file", e);
                    }
                    //
                    // Do the connect
                    //
                    connection.connect();
                    if (connection.getResponseCode() == 204) {
                        logger.info("Success. We are configured correctly.");
                        finished = true;
                        registered = true;
                    } else if (connection.getResponseCode() == 200) {
                        logger.info("Success. We have a new configuration.");
                        Properties properties = new Properties();
                        properties.load(connection.getInputStream());
                        logger.info("New properties: " + properties.toString());
                        //
                        // Queue it
                        //
                        // The incoming properties does NOT include urls
                        PutRequest req = new PutRequest(
                                                        XACMLProperties
                                                            .getPolicyProperties(properties, false),
                                                        XACMLProperties.getPipProperties(properties));
                        XACMLPdpServlet.queue.offer(req);
                        //
                        // We are now registered
                        //
                        finished = true;
                        registered = true;
                    } else if (connection.getResponseCode() >= 300 && connection.getResponseCode() <= 399) {
                        //
                        // Re-direction
                        //
                        String newLocation = connection.getHeaderField("Location");
                        if (newLocation == null || newLocation.isEmpty()) {
                            logger.warn("Did not receive a valid re-direction location");
                            finished = true;
                        } else {
                            logger.info("New Location: " + newLocation);
                            url = new URL(newLocation);
                        }
                    } else {
                        logger.warn("Failed: " + connection.getResponseCode() + "  message: "
                                    + connection.getResponseMessage());
                        finished = true;
                    }
                }
            } catch (Exception e) {
                logger.error(e);
            } finally {
                // cleanup the connection
                if (connection != null) {
                    try {
                        // For some reason trying to get the inputStream from the connection
                        // throws an exception rather than returning null when the InputStream does not exist.
                        InputStream is = null;
                        try {
                            is = connection.getInputStream();
                        } catch (Exception e1) { //NOPMD
                            // ignore this
                        }
                        if (is != null) {
                            is.close();
                        }

                    } catch (IOException ex) {
                        logger.error("Failed to close connection: " + ex, ex);
                    }
                    connection.disconnect();
                }
            }
            //
            // Wait a little while to try again
            //
            try {
                if (!registered) {
                    if (retries > 0) {
                        retries--;
                    } else if (retries == 0) {
                        break;
                    }
                    Thread.sleep(seconds * 1000);
                }
            } catch (InterruptedException e) {
                interrupted = true;
                this.terminate();
            }
        }
        synchronized (this) {
            this.isRunning = false;
        }
        logger.info("Thread exiting...(registered=" + registered + ", interrupted=" + interrupted
                    + ", isRunning=" + this.isRunning() + ", retries=" + retries + ")");
    }

}
