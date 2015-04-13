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

import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.XACML;

/**
 * DataTypeString extends {@link DataTypeBase} to represent XACML 3.0 Strings as java <code>String</code>s.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class DataTypeString extends DataTypeBase<String> {
	private static final DataTypeString	singleInstance	= new DataTypeString();
	
	private DataTypeString() {
		super(XACML.ID_DATATYPE_STRING, String.class);
	}
	
	public static DataTypeString newInstance() {
		return singleInstance;
	}

	@Override
	public String convert(Object source) throws DataTypeException {
		if (source == null || (source instanceof String)) {
			return (String)source;
		} else {
			return this.convertToString(source);
		}
	}
}
