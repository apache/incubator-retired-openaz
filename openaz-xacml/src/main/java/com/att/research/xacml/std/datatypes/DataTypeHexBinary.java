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
 * DataTypeHexBinary extends {@link DataTypeBase} to implement the XACML hexBinary data type.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class DataTypeHexBinary extends DataTypeSemanticStringBase<HexBinary> {
	private static final DataTypeHexBinary	singleInstance	= new DataTypeHexBinary();
	
	private DataTypeHexBinary() {
		super(XACML.ID_DATATYPE_HEXBINARY, HexBinary.class);
	}
	
	public static DataTypeHexBinary newInstance() {
		return singleInstance;
	}

	@Override
	public HexBinary convert(Object source) throws DataTypeException {
		if (source == null || (source instanceof HexBinary)) {
			return (HexBinary)source;
		} else if (source instanceof byte[]) {
			return new HexBinary((byte[])source);
		} else {
			String	stringValue	= this.convertToString(source);
			if (stringValue == null) {
				return null;
			}
			HexBinary	hexBinary	= null;
			try {
				hexBinary	= HexBinary.newInstance(stringValue);
			} catch (Exception ex) {
				throw new DataTypeException(this, "Failed to convert \"" + source.getClass().getCanonicalName() + "\" with value \"" + stringValue + "\" to HexBinary", ex);				
			}
			return hexBinary;
		}
	}

}
