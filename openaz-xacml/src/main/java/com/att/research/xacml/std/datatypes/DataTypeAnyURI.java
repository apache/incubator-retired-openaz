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
package com.att.research.xacml.std.datatypes;

import java.net.URI;
import java.net.URISyntaxException;

import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML;

/**
 * DataTypeAnyURI extends {@link DataTypeBase} for the XACML anyURI data type.
 * 
 * @author car
 * @version $Revision: 1.2 $
 */
public class DataTypeAnyURI extends DataTypeBase<URI> {
	private static final DataTypeAnyURI	singleInstance	= new DataTypeAnyURI();
	
	/**
	 * Creates a <code>DataTypeAnyURI</code> with the XACML anyURI id and the java <code>URI</code> class.
	 */
	private DataTypeAnyURI() {
		super(XACML.ID_DATATYPE_ANYURI, URI.class);
	}
	
	public static DataTypeAnyURI newInstance() {
		return singleInstance;
	}

	@Override
	public URI convert(Object source) throws DataTypeException {
		if (source == null || (source instanceof URI)) {
			return (URI)source;
		} else if (source instanceof Identifier) {
			return ((Identifier)source).getUri();
		} else {
			String stringValue	= this.convertToString(source);
			if (stringValue == null) {
				return null;
			}
			URI		uriValue	= null;
			try {
				//uriValue	= URI.create(stringValue);
				uriValue	= new URI(stringValue);
			} catch (URISyntaxException ex) {
				throw new DataTypeException(this, "Failed to convert \"" + source.getClass().getCanonicalName() + "\" with value \"" + stringValue + "\" to anyURI", ex);
			}
			return uriValue;
		}
	}

	@Override
	public String toStringValue(URI source) throws DataTypeException {
		return (source == null ? null : source.toString());
	}
}
