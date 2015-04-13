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
package com.att.research.xacml.api.pep;

/**
 * PEPException extends <code>Exception</code> to implement exceptions thrown by {@link PEPEngine} and {@link com.att.research.xacml.api.pep.PEPEngineFactory}
 * classes.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class PEPException extends Exception {
	private static final long serialVersionUID = 5438207617158925229L;

	public PEPException() {
	}

	public PEPException(String message) {
		super(message);
	}

	public PEPException(Throwable cause) {
		super(cause);
	}

	public PEPException(String message, Throwable cause) {
		super(message, cause);
	}

	public PEPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
