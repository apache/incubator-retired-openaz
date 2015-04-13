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
package com.att.research.xacml.api.pap;

public class PAPException extends Exception {
	private static final long serialVersionUID = 8291987599333392339L;

	public PAPException() {
	}

	public PAPException(String message) {
		super(message);
	}

	public PAPException(Throwable cause) {
		super(cause);
	}

	public PAPException(String message, Throwable cause) {
		super(message, cause);
	}

	public PAPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
