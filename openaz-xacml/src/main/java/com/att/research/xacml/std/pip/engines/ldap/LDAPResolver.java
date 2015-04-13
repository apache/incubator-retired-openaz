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
package com.att.research.xacml.std.pip.engines.ldap;

import java.util.List;

import javax.naming.directory.SearchResult;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.pip.PIPEngine;
import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPFinder;
import com.att.research.xacml.api.pip.PIPRequest;
import com.att.research.xacml.std.pip.engines.ConfigurableResolver;

/**
 * LDAPResolver is the interface used by the {@link LDAPEngine} to convert
 * a request for a XACML attribute into an LDAP query string, including retrieving any required attributes needed to construct
 * the query string, and convert the response into a collection of {@link com.att.research.xacml.api.Attribute}s.
 * 
 * @author car
 * @version $Revision$
 */
public interface LDAPResolver extends ConfigurableResolver {
	/**
	 * Gets the base <code>String</code> to be used in the <code>search</code> method of a {@link javax.naming.directory.DirectoryContext}.
	 * 
	 * @param pipEngine the {@link com.att.research.xacml.api.pip.PIPEngine} making the request
	 * @param pipRequest the <code>PIPRequest</code> to convert
	 * @param pipFinder the {@link com.att.research.xacml.api.pip.PIPFinder} to use when resolving required attributes
	 * @return the base <code>String</code> or null if the <code>PIPRequest</code> cannot be satisfied by this <code>LDAPResolver</code>
	 */
	public String getBase(PIPEngine pipEngine, PIPRequest pipRequest, PIPFinder pipFinder) throws PIPException;
	
	/**
	 * Converts the given <code>PIPRequest</code> into an LDAP filter string to use
	 * in the <code>search</code> method of a {@link javax.naming.directory.DirectoryContext}.
	 * 
	 * @param pipEngine the <code>PIPEngine</code> making the request
	 * @param pipRequest the <code>PIPRequest</code> to convert
	 * @param pipFinder the <code>PIPFinder</code> to use when resolving required attributes
	 * @return the filter string to use or null if the given <code>PIPRequest</code> cannot be satisfied by this <code>LDAPResolver</code>
	 * @throws com.att.research.xacml.api.pip.PIPException if there is an error retrieving any required attributes
	 */
	public String getFilterString(PIPEngine pipEngine, PIPRequest pipRequest, PIPFinder pipFinder) throws PIPException;
	
	/**
	 * Converts a {@link javax.naming.directory.SearchResult} into a <code>List</code> of {@link com.att.research.xacml.api.Attribute}s.
	 * 
	 * @param searchResult the <code>SearchResult</code> to convert
	 * @return a <code>List</code> of <code>Attribute</code>s or null if the <code>SearchResult</code> connot be converted.
	 * @throws com.att.research.xacml.api.pip.PIPException if there is an error decoding the <code>SearchResult</code>
	 */
	public List<Attribute> decodeResult(SearchResult searchResult) throws PIPException;
	
}
