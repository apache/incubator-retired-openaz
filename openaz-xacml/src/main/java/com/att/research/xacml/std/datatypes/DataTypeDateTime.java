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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.XACML;

/**
 * DataTypeDateTime extends {@link DataTypeBase} for the XACML DateTime type.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class DataTypeDateTime extends DataTypeSemanticStringBase<ISO8601DateTime> {
	private static final DataTypeDateTime	singleInstance	= new DataTypeDateTime();
	
	private DataTypeDateTime() {
		super(XACML.ID_DATATYPE_DATETIME, ISO8601DateTime.class);
	}
	
	public static DataTypeDateTime newInstance() {
		return singleInstance;
	}

	@Override
	public ISO8601DateTime convert(Object source) throws DataTypeException {
		if (source == null || (source instanceof ISO8601DateTime)) {
			return (ISO8601DateTime)source;
		} else if (source instanceof Calendar) {
			return ISO8601DateTime.fromCalendar((Calendar)source);
		} else if (source instanceof Date) {
			return ISO8601DateTime.fromDate((Date)source);
		} else {
			String stringValue	= this.convertToString(source);
			ISO8601DateTime	dateTime	= null;
			try {
				dateTime	= ISO8601DateTime.fromISO8601DateTimeString(stringValue);
			} catch (ParseException ex) {
				throw new DataTypeException(this, "Failed to convert \"" + source.getClass().getCanonicalName() + "\" with value \"" + stringValue + "\" to DateTime", ex);
			}
			return dateTime;
		}
	}

}
