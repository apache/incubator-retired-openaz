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
package org.apache.openaz.xacml.rest;

import org.apache.openaz.xacml.util.XACMLProperties;

/**
 * These are XACML Properties that are relevant to the RESTful API interface for the PDP, PAP and AC
 * interfaces.
 */
public class XACMLRestProperties extends XACMLProperties {
    /**
     * A unique identifier for the PDP servlet instance. Usually set to the URL it is running as in the J2EE
     * container. Eg. http://localhost:8080/pdp/
     */
    public static final String PROP_PDP_ID = "xacml.rest.pdp.id";
    /**
     * A PDP servlet's configuration directory. Holds the pip and policy configuration data as well as the
     * local policy cache. Eg: /opt/app/xacml/config
     */
    public static final String PROP_PDP_CONFIG = "xacml.rest.pdp.config";
    /**
     * Set this property to true or false if the PDP servlet should register itself upon startup with the PAP
     * servlet.
     */
    public static final String PROP_PDP_REGISTER = "xacml.rest.pdp.register";
    /**
     * Number of seconds the PDP will sleep while retrying registration with the PAP. This value must be
     * greater or equal to 5.
     */
    public static final String PROP_PDP_REGISTER_SLEEP = "xacml.rest.pdp.register.sleep";
    /**
     * Number of retry attempts at registration with the PAP. A value of -1 indicates infinite retries.
     */
    public static final String PROP_PDP_REGISTER_RETRIES = "xacml.rest.pdp.register.retries";
    /**
     * Max content length accepted for an incoming POST XML/JSON request. Default is 32767 bytes.
     */
    public static final String PROP_PDP_MAX_CONTENT = "xacml.rest.pdp.maxcontent";
    /**
     * Custom HTTP header used by PDP to send the value of the PROP_PDP_ID
     */
    public static final String PROP_PDP_HTTP_HEADER_ID = "X-XACML-PDP-ID";
    /**
     * Custom HHTP header used by PDP to send its heartbeat value.
     */
    public static final String PROP_PDP_HTTP_HEADER_HB = "X-XACML-PDP-HB";
    /**
     * The URL of the PAP servlet. Used by PDP servlet's to communicate. Because administrators can set
     * whatever context they want to run the PAP servlet, it isn't easy to determine a return URL for the PAP
     * servlet. This is especially true upon initialization.
     */
    public static final String PROP_PAP_URL = "xacml.rest.pap.url";
    /**
     * Upon startup, have the PAP servlet send latest configuration information to all the PDP nodes it knows
     * about.
     */
    public static final String PROP_PAP_INITIATE_PDP_CONFIG = "xacml.rest.pap.initiate.pdp";
    /**
     * The interval the PAP servlet uses to send heartbeat requests to the PDP nodes.
     */
    public static final String PROP_PAP_HEARTBEAT_INTERVAL = "xacml.rest.pap.heartbeat.interval";
    /**
     * Timeout value used by the PAP servlet when trying to check the heartbeat of a PDP node.
     */
    public static final String PROP_PAP_HEARTBEAT_TIMEOUT = "xacml.rest.pap.heartbeat.timeout";
    /*
     * Local path to where the GIT repository exists. Eg. /opt/app/xacml/repository
     */
    public static final String PROP_ADMIN_REPOSITORY = "xacml.rest.admin.repository";
    /*
     * Local path to where user workspaces exist. The user workspace contains temporary files, the user's
     * clone of the GIT repository, anything specific to the user, etc.
     */
    public static final String PROP_ADMIN_WORKSPACE = "xacml.rest.admin.workspace";
    /*
     * This is the domain you can setup for your organization, it should be a URI. Eg. com:sample:foo
     */
    public static final String PROP_ADMIN_DOMAIN = "xacml.rest.admin.domain";
    /**
     * PROP_ADMIN_USER_NAME is simply a name for the logged in user. AC authentication is out the scope of the
     * web application itself. It is up to the developer to setup authentication as they please in the J2EE
     * container used to run the web application. Whatever authentication mechanism they use, they should then
     * set the attribute into the HttpSession object. The Admin Console will be able to read that value
     * (default to "guest") in. ((HttpServletRequest)
     * request).getSession().setAttribute("xacml.rest.admin.user.name", "Homer");
     */
    public static final String PROP_ADMIN_USER_NAME = "xacml.rest.admin.user.name";
    /**
     * PROP_ADMIN_USER_ID is an id for the logged in user. Eg. hs1234
     *
     * @see #PROP_ADMIN_USER_NAME for more information.
     */
    public static final String PROP_ADMIN_USER_ID = "xacml.rest.admin.user.id";
    /**
     * PROP_ADMIN_USER_EMAIL is a user's email address.
     *
     * @see #PROP_ADMIN_USER_NAME for more information.
     */
    public static final String PROP_ADMIN_USER_EMAIL = "xacml.rest.admin.user.email";
    /**
     * Directory path containing sub-directories where the Subscriber servlet puts files sent through data
     * feeds.
     */
    public static final String PROP_SUBSCRIBER_INCOMING = "xacml.subscriber.incoming";
    /**
     * The specific data feed name for the Subscriber servlet to register for.
     */
    public static final String PROP_SUBSCRIBER_FEED = "xacml.subscriber.feed";
}
