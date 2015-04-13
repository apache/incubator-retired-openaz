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
package com.att.research.xacml.std.pip.engines;

import java.util.Collection;
import java.util.Properties;

import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPRequest;

public interface ConfigurableResolver {
	/**
	 * Configures this <code>JDBCResolver</code> using the given <code>Properties</code>>
	 * 
	 * @param id the <code>String</code> identifier for locating properties for this <code>JDBCResolver</code>
	 * @param properties the <code>Properties</code> to search for properties
	 * @param defaultIssuer the default issuer value if none is defined specifically.
	 * @throws com.att.research.xacml.api.pip.PIPException if there is an error configuring this <code>JDBCResolver</code>
	 */
	public void configure(String id, Properties properties, String defaultIssuer) throws PIPException;

	/**
	 * Adds attributes required by the resolver to return an attribute.
	 * 
	 * @param attributes - A modifiable collection
	 */
	public void	attributesRequired(Collection<PIPRequest> attributes);
	
	/**
	 * Adds attributes provided by the resolver.
	 * 
	 * @param attributes - A modifiable collection
	 */
	public void	attributesProvided(Collection<PIPRequest> attributes);
	
}
