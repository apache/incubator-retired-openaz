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
 * DataTypeDouble implements conversion to the XACML Double data type.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class DataTypeDouble extends DataTypeBase<Double> {
	private static final DataTypeDouble	singleInstance	= new DataTypeDouble();
	
	private DataTypeDouble() {
		super(XACML.ID_DATATYPE_DOUBLE, Double.class);
	}
	
	public static DataTypeDouble newInstance() {
		return singleInstance;
	}

	@Override
	public Double convert(Object source) throws DataTypeException {
		if (source == null || (source instanceof Double)) {
			return (Double)source;
		} else {
			String stringValue	= this.convertToString(source);
			Double	intValue	= null;
			try {
				// the XML representation of Infinity is "INF" and "-INF",
				// but the Java Double represenation is "Infinity" and "-Infinity"
				if (stringValue.equals("INF")) {
						stringValue = "Infinity";
				} else if (stringValue.equals("-INF")) {
						stringValue = "-Infinity";
				}
				intValue	= Double.parseDouble(stringValue);
			} catch (NumberFormatException ex) {
				throw new DataTypeException(this, "Failed to convert from \"" + source.getClass().getCanonicalName() + "\" with value \"" + stringValue + "\" to double", ex);
			}
			return intValue;
		}
	}
}
