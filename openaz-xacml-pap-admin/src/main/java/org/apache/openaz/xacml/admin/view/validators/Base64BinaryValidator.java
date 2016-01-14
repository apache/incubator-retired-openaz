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

// import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;

import com.vaadin.data.Validator;

public class Base64BinaryValidator implements Validator {
	private static final long serialVersionUID = 1L;

	public Base64BinaryValidator() {
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (value instanceof String) {
			// try {
				new Base64().decode((String) value);
			// Base64().decode(String) inherited from 
			// org.apache.commons.codec.binary.BaseNCodec
			// does not throw DecoderException in version 1.10
			// of org.apache.commons.codec (the version we are
			// using).  This may need to be uncommented in later
			// versions.
			//
			// TODO - Since this does not throw an exception under
			//        any circumstance, I question whether it is
			//        a valid method of validating the input value.
			//
			// } catch (DecoderException e) {
			// 	throw new InvalidValueException(e.getLocalizedMessage());
			// }
		} else
			throw new InvalidValueException("Unrecognized Base64 Binary");
	}
}
