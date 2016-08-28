/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.apache.openaz.xacml.admin.view.validators;

import org.apache.openaz.xacml.admin.jpa.Datatype;
import org.apache.openaz.xacml.api.XACML3;
import com.vaadin.data.Validator;

public class ValidatorFactory {

	public static Validator	newInstance(Datatype datatype) {
		
		if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_ANYURI)) {
			return new AnyURIValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_BASE64BINARY)) {
			return new Base64BinaryValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_BOOLEAN)) {
			return new BooleanValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_DATE)) {
			return new DateValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_DATETIME)) {
			return new DateTimeValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_DAYTIMEDURATION)) {
			return new DayTimeDurationValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_DNSNAME)) {
			return new DNSNameValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_DOUBLE)) {
			return new DoubleValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_HEXBINARY)) {
			return new HexBinaryValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_INTEGER)) {
			return new IntegerValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_IPADDRESS)) {
			return new IpAddressValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_RFC822NAME)) {
			return new RFC822NameValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_STRING)) {
			return new StringValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_TIME)) {
			return new TimeValidator();
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_X500NAME)) {
			return new X500NameValidator();
		/*
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_XPATHEXPRESSION)) {
			
		*/
		} else if (datatype.getIdentifer().equals(XACML3.ID_DATATYPE_YEARMONTHDURATION)) {
			return new YearMonthDurationValidator();
		}
		
		return null;
	}
}
