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
import com.att.research.xacml.api.Identifier;

public abstract class DataTypeSemanticStringBase<T extends com.att.research.xacml.api.SemanticString> extends DataTypeBase<T> {

	public DataTypeSemanticStringBase(Identifier identifierIn, Class<T> classConvertIn) {
		super(identifierIn, classConvertIn);
	}

	@Override
	public String toStringValue(T source) throws DataTypeException {
		return (source == null ? null : source.stringValue());
	}

}
