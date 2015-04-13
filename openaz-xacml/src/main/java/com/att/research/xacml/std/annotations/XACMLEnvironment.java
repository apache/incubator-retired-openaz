/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.std.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface XACMLEnvironment {
	String	category() default "urn:oasis:names:tc:xacml:3.0:attribute-category:environment";
	String	attributeId() default "urn:oasis:names:tc:xacml:1.0:environment:current-dateTime";
	String	datatype() default XACMLRequest.nullString;
	String	issuer() default XACMLRequest.nullString;
	String	id() default XACMLRequest.nullString;
	boolean includeInResults() default false;
}
