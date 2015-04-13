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
package com.att.research.xacmlatt.pdp.eval;

/**
 * EvaluationContextException extends <code>Exception</code> to represent errors thrown by
 * methods in the {@link EvaluationContext} and {@link EvaluationContextFactory}.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class EvaluationContextException extends Exception {
	private static final long serialVersionUID = -8270506903118536839L;

	public EvaluationContextException() {
	}

	public EvaluationContextException(String message) {
		super(message);
	}

	public EvaluationContextException(Throwable cause) {
		super(cause);
	}

	public EvaluationContextException(String message, Throwable cause) {
		super(message, cause);
	}

	public EvaluationContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
