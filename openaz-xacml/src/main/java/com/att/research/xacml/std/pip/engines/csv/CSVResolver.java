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
package com.att.research.xacml.std.pip.engines.csv;

import java.util.List;
import java.util.Map;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.pip.PIPEngine;
import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPFinder;
import com.att.research.xacml.api.pip.PIPRequest;
import com.att.research.xacml.std.pip.engines.ConfigurableResolver;

/**
 * CSVResolver is the interface used by the {@link CSVEngine} to
 * create list of column parameters to check for XACML attribute requests and convert the results
 * into XACML attributes.
 * 
 * @author pameladragosh
 *
 */
public interface CSVResolver extends ConfigurableResolver {

	/**
	 * Method to determine if resolver can support the PIPRequest
	 * 
	 * @param pipRequest
	 * @return true if the resolver can provide the PIPRequest attribute
	 */
	boolean supportRequest(PIPRequest pipRequest);

	/**
	 * Returns a mapping of column's to a list of attribute values. The PIPEngine uses the map
	 * to determine if a line from a CSV file matches the given values. For columns with multiple
	 * possible values, only one value needs to match.
	 * 
	 * @param engine
	 * @param request
	 * @param finder
	 * @return
	 * @throws com.att.research.xacml.api.pip.PIPException
	 */
	Map<Integer, List<AttributeValue<?>>>	getColumnParameterValues(PIPEngine engine, PIPRequest request, PIPFinder finder) throws PIPException;

	/**
	 * Parses the CSV line and returns array of attributes.
	 * 
	 * @param line - line read from CSV file broken into fields.
	 * @return list of attributes 
	 * @throws com.att.research.xacml.api.pip.PIPException
	 */
	List<Attribute>	decodeResult(String[] line)  throws PIPException;

}
