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
package com.att.research.xacml.api.pip;

import com.att.research.xacml.api.Identifier;

/**
 * PIPRequest is the interface that objects implement to represent a request to a {@link PIPEngine} to retrieve
 * attributes with values that meet a given request.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public interface PIPRequest {
	/**
	 * Gets the {@link com.att.research.xacml.api.Identifier} of the category of the attributes to retrieve.
	 * 
	 * @return the <code>Identifier</code> for the category of the attributes to retrieve
	 */ 
	public Identifier getCategory();
	
	/**
	 * Gets the <code>Identifier</code> of the attributes to retrieve.
	 * 
	 * @return the <code>Identifier</code> of the attributes to retrieve.
	 */
	public Identifier getAttributeId();
	
	/**
	 * Gets the <code>Identifier</code> of the requested data type for attribute values.
	 * 
	 * @return the <code>Identifier</code> of the requested data type for attribute values
	 */
	public Identifier getDataTypeId();
	
	/**
	 * Gets the <code>String</code> issuer identifier for the attributes to retrieve.
	 * 
	 * @return the <code>String</code> issuer identifier for the attributes to retrieve or null if there is no requirement to match the issuer.
	 */
	public String getIssuer();
	
}
