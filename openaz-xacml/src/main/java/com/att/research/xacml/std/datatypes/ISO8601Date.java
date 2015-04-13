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
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.att.research.xacml.api.SemanticString;

/**
 * ISO8601Date is a representation of an ISO8601 format date without a time component.
 * NOTE: This is actually the extended specification for XACML3.0, not a strict ISO8601 date
 * 
 * @author car
 * @version $Revision: 1.2 $
 */
public class ISO8601Date implements IDateTime<ISO8601Date>, Comparable<ISO8601Date>, SemanticString {
	private ISO8601DateTime	dateTime;
	
	protected ISO8601Date(ISO8601DateTime iso8601DateTime) {
		this.dateTime	= iso8601DateTime;
	}
	
	public ISO8601Date(String timeZone, int yearIn, int monthIn, int dayIn) {
		this.dateTime	= new ISO8601DateTime(timeZone, yearIn, monthIn, dayIn, 0, 0, 0, 0);
	}
	
	public ISO8601Date(TimeZone timeZone, int yearIn, int monthIn, int dayIn) {
		this.dateTime	= new ISO8601DateTime(timeZone, yearIn, monthIn, dayIn, 0, 0, 0, 0);
	}
	
	public ISO8601Date(ISO8601TimeZone timeZone, int yearIn, int monthIn, int dayIn) {
		this((timeZone == null ? null : timeZone.getTimeZoneString()), yearIn, monthIn, dayIn);
	}
	
	public ISO8601Date(int yearIn, int monthIn, int dayIn) {
		this((String)null, yearIn, monthIn, dayIn);
	}
	
	public boolean getHasTimeZone() {
		return this.dateTime.getHasTimeZone();
	}
	public Calendar getCalendar() {
		return this.dateTime.getCalendar();
	}
	public String getTimeZone() {
		return this.dateTime.getTimeZone();
	}
	public int getYear() {
		return this.dateTime.getYear();
	}
	public int getMonth() {
		return this.dateTime.getMonth();
	}
	public int getDay() {
		return this.dateTime.getDay();
	}
	
	public ISO8601Date add(ISO8601Duration iso8601Duration) {
		return new ISO8601Date(this.dateTime.add(iso8601Duration));
	}
	
	public ISO8601Date sub(ISO8601Duration iso8601Duration) {
		return new ISO8601Date(this.dateTime.sub(iso8601Duration));
	}
	
	@Override
	public int hashCode() {
		return this.dateTime.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null || !(obj instanceof ISO8601Date)) {
			return false;
		} else {
			ISO8601Date iso8601Date	= (ISO8601Date)obj;
			return this.dateTime.equals(iso8601Date.dateTime);
		}
	}

	/**
	 * Gets the <code>String</code> representation of the ISO8601 date represented by this <code>ISO8601Date</code> object.
	 * 
	 * @param includeTimeZone <code>boolean</code> indicating whether the time zone information should be included
	 * @return the <code>String</code> representation of the ISO8601 date represented by this <code>ISO8601Date</code> object.
	 */
	public String stringValue(boolean includeTimeZone) {
		StringBuilder	stringBuilder	= new StringBuilder();
		stringBuilder.append(String.format("%d-%02d-%02d", this.dateTime.getYear(), this.dateTime.getMonth(), this.dateTime.getDay()));
		if (includeTimeZone && this.dateTime.getHasTimeZone()) {
			stringBuilder.append(this.dateTime.getTimeZone());
		}
		return stringBuilder.toString();
	}
	
	@Override
	public String stringValue() {
		return this.stringValue(true);
	}
	
	@Override
	public String toString() {
		return this.stringValue(true);
	}

	@Override
	public int compareTo(ISO8601Date o) {
		return this.dateTime.compareTo(o.dateTime);
	}
	
	public static ISO8601Date fromCalendar(Calendar calendar) {
		int year;
		if (calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
			year	= 1 - calendar.get(Calendar.YEAR);
		} else {
			year	= calendar.get(Calendar.YEAR);
		}
		return new ISO8601Date(calendar.getTimeZone(), year, calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	public static ISO8601Date fromDate(Date date) {
		Calendar cal	= Calendar.getInstance();
		cal.setTime(date);
		return ISO8601Date.fromCalendar(cal);
	}
	
	public static ISO8601Date fromISO8601DateString(String strDate) throws ParseException {
		int	startPos	= ParseUtils.nextNonWhite(strDate, 0);
		
		ParseUtils.ParseValue<Integer> parseValue	= ParseUtils.getSignedValue(strDate, startPos);
		if (parseValue == null) {
			throw new ParseException("Invalid year", startPos);
		}
		int year		= parseValue.getValue();
		if (parseValue.getNextPos() < 4) {
			throw new ParseException("Invalid year (must be at least 4 digits)", startPos);
		}
		startPos		+= parseValue.getNextPos();
		if (startPos >= strDate.length() || strDate.charAt(startPos) != '-') {
			throw new ParseException("Invalid date", startPos);
		}
		startPos++;
		
		int month		= ParseUtils.getTwoDigitValue(strDate,  startPos);
		if (month < 0 || month > 12) {
			throw new ParseException("Invalid month", startPos);
		}
		startPos		+= 2;
		if (startPos >= strDate.length() || strDate.charAt(startPos) != '-') {
			throw new ParseException("Invalid date", startPos);
		}
		startPos++;
		
		int day			= ParseUtils.getTwoDigitValue(strDate, startPos);
		if (day < 1 || day > 31) {
			throw new ParseException("Invalid day", startPos);
		}
		startPos		+= 2;
		
		/*
		 * Now determine if there is a timezone
		 */
		String timezone		= null;

		if (startPos < strDate.length()) {
			switch(strDate.charAt(startPos)) {
			case 'Z':
				timezone	= "GMT";
				startPos++;
				break;
			case '-':
			case '+':
				if (startPos+5 < strDate.length()) {
					timezone	= "GMT" + strDate.substring(startPos, startPos+6);
				} else {
					throw new ParseException("Invalid timezone", startPos);
				}
				break;
			default:
				throw new ParseException("Invalid timezone", startPos);
			}
		}
		
		return new ISO8601Date(timezone, year, month, day);
	}
	
}
