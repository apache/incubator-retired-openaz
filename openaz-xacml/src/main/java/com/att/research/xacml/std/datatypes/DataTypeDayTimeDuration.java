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

import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.XACML3;

/**
 * DataTypeDayTimeDuration extends {@link DataTypeBase} to implement the XACML dayTimeDuration
 * data type.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class DataTypeDayTimeDuration extends DataTypeSemanticStringBase<XPathDayTimeDuration> {
	private static final DataTypeDayTimeDuration	singleInstance	= new DataTypeDayTimeDuration();
	
	private DataTypeDayTimeDuration() {
		super(XACML3.ID_DATATYPE_DAYTIMEDURATION, XPathDayTimeDuration.class);
	}
	
	public static DataTypeDayTimeDuration newInstance() {
		return singleInstance;
	}

	@Override
	public XPathDayTimeDuration convert(Object source) throws DataTypeException {
		if (source == null || (source instanceof XPathDayTimeDuration)) {
			return (XPathDayTimeDuration)source;
		} else {
			String stringValue	= this.convertToString(source);
			if (stringValue == null) {
				return null;
			}
			XPathDayTimeDuration	xpathDayTimeDuration	= null;
			try {
				xpathDayTimeDuration	= XPathDayTimeDuration.newInstance(stringValue);
			} catch (ParseException ex) {
				throw new DataTypeException(this, "Failed to convert \"" + source.getClass().getCanonicalName() + "\" with value \"" + stringValue + "\" to DayTimeDuration", ex);
			}
			return xpathDayTimeDuration;
		}
	}

}
