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
package org.apache.openaz.xacml.std.pip.engines.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

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

import com.google.common.base.Splitter;
import com.google.common.cache.Cache;

/**
 * LDAPEngine extends {@link org.apache.openaz.xacml.std.pip.engines.StdConfigurableEngine} to implement a
 * generic PIP for accessing data from and LDAP server, including a configurable cache to avoid repeat
 * queries.
 */
public class LDAPEngine extends StdConfigurableEngine {
    public static final String PROP_RESOLVERS = "resolvers";
    public static final String PROP_RESOLVER = "resolver";
    public static final String PROP_LDAP_SCOPE = "scope";

    private static final String LDAP_SCOPE_SUBTREE = "subtree";
    private static final String LDAP_SCOPE_OBJECT = "object";
    private static final String LDAP_SCOPE_ONELEVEL = "onelevel";
    private static final String DEFAULT_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final String DEFAULT_SCOPE = LDAP_SCOPE_SUBTREE;

    private Log logger = LogFactory.getLog(this.getClass());
    private Hashtable<Object, Object> ldapEnvironment = new Hashtable<Object, Object>();
    private List<LDAPResolver> ldapResolvers = new ArrayList<LDAPResolver>();
    private int ldapScope;

    /*
     * In addition, we pull the following standard LDAP properties from the configuration
     * Context.AUTHORITATIVE: boolean Context.BATCHSIZE: integer Context.DNSURL: String
     * Context.INITIAL_CONTEXT_FACTORY: String Context.LANGUAGE: String Context.OBJECT_FACTORIES: String
     * Context.PROVIDER_URL: String Context.REFERRAL: String Context.SECURITY_AUTHENTICATION: String
     * Context.SECURITY_CREDENTIALS: String Context.SECURITY_PRINCIPAL: String Context.SECURITY_PROTOCOL:
     * String Context.STATE_FACTORIES: String Context.URL_PKG_PREFIXES: String
     */

    public LDAPEngine() {
    }

    private boolean configureStringProperty(String propertyPrefix, String property, Properties properties,
                                            String defaultValue) {
        String propertyValue = properties.getProperty(propertyPrefix + property, defaultValue);
        if (propertyValue != null) {
            this.ldapEnvironment.put(property, propertyValue);
            return true;
        } else {
            return false;
        }
    }

    private boolean configureIntegerProperty(String propertyPrefix, String property, Properties properties,
                                             Integer defaultValue) {
        String propertyValue = properties.getProperty(propertyPrefix + property);
        if (propertyValue == null) {
            if (defaultValue != null) {
                this.ldapEnvironment.put(property, defaultValue);
                return true;
            } else {
                return false;
            }
        } else {
            try {
                this.ldapEnvironment.put(property, Integer.parseInt(propertyValue));
                return true;
            } catch (NumberFormatException ex) {
                this.logger.error("Invalid Integer '" + propertyValue + "' for '" + property + "' property");
                return false;
            }
        }
    }

    @Override
    public void configure(String id, Properties properties) throws PIPException {
        /*
         * Handle the standard properties
         */
        super.configure(id, properties);
        String propertyPrefix = id + ".";

        /*
         * Configure the LDAP environment: I think the only required property is the provider_url
         */
        if (!this.configureStringProperty(propertyPrefix, Context.PROVIDER_URL, properties, null)) {
            throw new PIPException("Invalid configuration for " + this.getClass().getName() + ": No "
                                   + propertyPrefix + Context.PROVIDER_URL);
        }
        this.configureStringProperty(propertyPrefix, Context.AUTHORITATIVE, properties, null);
        this.configureIntegerProperty(propertyPrefix, Context.BATCHSIZE, properties, null);
        this.configureStringProperty(propertyPrefix, Context.DNS_URL, properties, null);
        this.configureStringProperty(propertyPrefix, Context.INITIAL_CONTEXT_FACTORY, properties,
                                     DEFAULT_CONTEXT_FACTORY);
        this.configureStringProperty(propertyPrefix, Context.LANGUAGE, properties, null);
        this.configureStringProperty(propertyPrefix, Context.OBJECT_FACTORIES, properties, null);
        this.configureStringProperty(propertyPrefix, Context.REFERRAL, properties, null);
        this.configureStringProperty(propertyPrefix, Context.SECURITY_AUTHENTICATION, properties, null);
        this.configureStringProperty(propertyPrefix, Context.SECURITY_CREDENTIALS, properties, null);
        this.configureStringProperty(propertyPrefix, Context.SECURITY_PRINCIPAL, properties, null);
        this.configureStringProperty(propertyPrefix, Context.SECURITY_PROTOCOL, properties, null);
        this.configureStringProperty(propertyPrefix, Context.STATE_FACTORIES, properties, null);
        this.configureStringProperty(propertyPrefix, Context.URL_PKG_PREFIXES, properties, null);

        String ldapScopeValue = properties.getProperty(propertyPrefix + PROP_LDAP_SCOPE, DEFAULT_SCOPE);
        if (LDAP_SCOPE_SUBTREE.equals(ldapScopeValue)) {
            this.ldapScope = SearchControls.SUBTREE_SCOPE;
        } else if (LDAP_SCOPE_OBJECT.equals(ldapScopeValue)) {
            this.ldapScope = SearchControls.OBJECT_SCOPE;
        } else if (LDAP_SCOPE_ONELEVEL.equals(ldapScopeValue)) {
            this.ldapScope = SearchControls.ONELEVEL_SCOPE;
        } else {
            this.logger.warn("Invalid LDAP Scope value '" + ldapScopeValue + "'; using " + DEFAULT_SCOPE);
            this.ldapScope = SearchControls.SUBTREE_SCOPE;
        }

        /*
         * Get list of resolvers defined for this LDAP Engine
         */
        String resolversList = properties.getProperty(propertyPrefix + PROP_RESOLVERS);
        if (resolversList == null || resolversList.isEmpty()) {
            throw new PIPException("Invalid configuration for " + this.getClass().getName() + ": No "
                                   + propertyPrefix + PROP_RESOLVERS);
        }

        /*
         * Iterate the resolvers
         */
        for (String resolver : Splitter.on(',').trimResults().omitEmptyStrings().split(resolversList)) {
            /*
             * Get the LDAPResolver for this LDAPEngine
             */
            String resolverClassName = properties.getProperty(propertyPrefix + PROP_RESOLVER + "." + resolver
                                                              + ".classname");
            if (resolverClassName == null) {
                throw new PIPException("Invalid configuration for " + this.getClass().getName() + ": No "
                                       + propertyPrefix + PROP_RESOLVER + "." + resolver + ".classname");
            }

            LDAPResolver ldapResolverNew = null;
            try {
                Class<?> classResolver = Class.forName(resolverClassName);
                if (!LDAPResolver.class.isAssignableFrom(classResolver)) {
                    this.logger.error("LDAPResolver class " + resolverClassName + " does not implement "
                                      + LDAPResolver.class.getCanonicalName());
                    throw new PIPException("LDAPResolver class " + resolverClassName + " does not implement "
                                           + LDAPResolver.class.getCanonicalName());
                }
                ldapResolverNew = LDAPResolver.class.cast(classResolver.newInstance());
            } catch (Exception ex) {
                this.logger.error("Exception instantiating LDAPResolver for class '" + resolverClassName
                                  + "': " + ex.getMessage(), ex);
                throw new PIPException("Exception instantiating LDAPResolver for class '" + resolverClassName
                                       + "'", ex);
            }
            assert ldapResolverNew != null;
            ldapResolverNew.configure(propertyPrefix + PROP_RESOLVER + "." + resolver, properties,
                                      this.getIssuer());

            this.ldapResolvers.add(ldapResolverNew);
        }

    }

    @Override
    public PIPResponse getAttributes(PIPRequest pipRequest, PIPFinder pipFinder) throws PIPException {
        /*
         * Make sure we have at least one resolver.
         */
        if (this.ldapResolvers.size() == 0) {
            throw new IllegalStateException(this.getClass().getCanonicalName() + " is not configured");
        }

        StdMutablePIPResponse mutablePIPResponse = new StdMutablePIPResponse();
        for (LDAPResolver ldapResolver : this.ldapResolvers) {
            this.getAttributes(pipRequest, pipFinder, mutablePIPResponse, ldapResolver);
        }
        if (mutablePIPResponse.getAttributes().size() == 0) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("returning empty response");
            }
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Returning " + mutablePIPResponse.getAttributes().size() + " attributes");
                this.logger.debug(mutablePIPResponse.getAttributes());
            }
            return new StdPIPResponse(mutablePIPResponse);
        }
    }

    public void getAttributes(PIPRequest pipRequest, PIPFinder pipFinder,
                              StdMutablePIPResponse mutablePIPResponse, LDAPResolver ldapResolver)
        throws PIPException {
        /*
         * Check with the resolver to get the base string
         */
        String stringBase = ldapResolver.getBase(this, pipRequest, pipFinder);
        if (stringBase == null) {
            this.logger.warn(this.getName() + " does not handle " + pipRequest.toString());
            return;
        }

        /*
         * Get the filter string
         */
        String stringFilter = ldapResolver.getFilterString(this, pipRequest, pipFinder);

        /*
         * Check the cache
         */
        Cache<String, PIPResponse> cache = this.getCache();
        String cacheKey = stringBase + "::" + (stringFilter == null ? "" : stringFilter);
        if (cache != null) {
            PIPResponse pipResponse = cache.getIfPresent(cacheKey);
            if (pipResponse != null) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Returning cached response: " + pipResponse);
                }
                mutablePIPResponse.addAttributes(pipResponse.getAttributes());
                return;
            }
        }
        /*
         * Not in the cache, so set up the LDAP query session
         */
        DirContext dirContext = null;
        PIPResponse pipResponse = null;
        try {
            /*
             * Create the DirContext
             */
            dirContext = new InitialDirContext(this.ldapEnvironment);

            /*
             * Set up the search controls
             */
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(this.ldapScope);

            /*
             * Do the search
             */
            NamingEnumeration<SearchResult> namingEnumeration = dirContext.search(stringBase, stringFilter,
                                                                                  searchControls);
            if (namingEnumeration != null && namingEnumeration.hasMore()) {
                while (namingEnumeration.hasMore()) {
                    List<Attribute> listAttributes = ldapResolver.decodeResult(namingEnumeration.next());
                    if (listAttributes != null && listAttributes.size() > 0) {
                        mutablePIPResponse.addAttributes(listAttributes);
                    }
                }
            }
            /*
             * Put in the cache
             */
            if (cache != null) {
                cache.put(cacheKey, pipResponse);
            }
        } catch (NamingException ex) {
            this.logger.error("NamingException creating the DirContext: " + ex.getMessage(), ex);
        } finally {
            if (dirContext != null) {
                try {
                    dirContext.close();
                } catch (Exception ex) {
                    this.logger.warn("Exception closing DirContext: " + ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public Collection<PIPRequest> attributesRequired() {
        Set<PIPRequest> requiredAttributes = new HashSet<PIPRequest>();
        for (LDAPResolver resolver : this.ldapResolvers) {
            resolver.attributesRequired(requiredAttributes);
        }
        return requiredAttributes;
    }

    @Override
    public Collection<PIPRequest> attributesProvided() {
        Set<PIPRequest> providedAttributes = new HashSet<PIPRequest>();
        for (LDAPResolver resolver : this.ldapResolvers) {
            resolver.attributesProvided(providedAttributes);
        }
        return providedAttributes;
    }
}
