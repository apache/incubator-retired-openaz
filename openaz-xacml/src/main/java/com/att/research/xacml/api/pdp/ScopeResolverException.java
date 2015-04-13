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
package com.att.research.xacml.api.pdp;

/**
 * ScopeResolverException is thrown by the {@link ScopeResolver} interface methods.
 * 
 * @author car
 * @version $Revision$
 */
public class ScopeResolverException extends Exception {
	private static final long serialVersionUID = -5818416719823811113L;

	public ScopeResolverException() {
	}

	public ScopeResolverException(String message) {
		super(message);
	}

	public ScopeResolverException(Throwable cause) {
		super(cause);
	}

	public ScopeResolverException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScopeResolverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
