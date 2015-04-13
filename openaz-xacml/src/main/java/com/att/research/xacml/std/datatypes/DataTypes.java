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

/**
 * DataTypes provides constant instances of the built-in {@link com.att.research.xacml.api.DataType} implementation classes.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class DataTypes {
	private DataTypes() {
	}
	
	public static final DataTypeString				DT_STRING				= DataTypeString.newInstance();
	public static final DataTypeBoolean				DT_BOOLEAN				= DataTypeBoolean.newInstance();
	public static final DataTypeInteger				DT_INTEGER				= DataTypeInteger.newInstance();
	public static final DataTypeDouble				DT_DOUBLE				= DataTypeDouble.newInstance();
	public static final DataTypeTime				DT_TIME					= DataTypeTime.newInstance();
	public static final DataTypeDate				DT_DATE					= DataTypeDate.newInstance();
	public static final DataTypeDateTime			DT_DATETIME				= DataTypeDateTime.newInstance();
	public static final DataTypeDayTimeDuration		DT_DAYTIMEDURATION		= DataTypeDayTimeDuration.newInstance();
	public static final DataTypeYearMonthDuration	DT_YEARMONTHDURATION	= DataTypeYearMonthDuration.newInstance();
	public static final DataTypeAnyURI				DT_ANYURI				= DataTypeAnyURI.newInstance();
	public static final DataTypeHexBinary			DT_HEXBINARY			= DataTypeHexBinary.newInstance();
	public static final DataTypeBase64Binary		DT_BASE64BINARY			= DataTypeBase64Binary.newInstance();
	public static final DataTypeX500Name			DT_X500NAME				= DataTypeX500Name.newInstance();
	public static final DataTypeRFC822Name			DT_RFC822NAME			= DataTypeRFC822Name.newInstance();
	public static final DataTypeIpAddress			DT_IPADDRESS			= DataTypeIpAddress.newInstance();
	public static final DataTypeDNSName				DT_DNSNAME				= DataTypeDNSName.newInstance();
	public static final DataTypeXPathExpression		DT_XPATHEXPRESSION		= DataTypeXPathExpression.newInstance();
}
