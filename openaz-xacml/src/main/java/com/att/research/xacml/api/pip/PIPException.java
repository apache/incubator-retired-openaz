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
package com.att.research.xacml.api.pip;

/**
 * PIPException extends <code>Exception</code> to represent errors that can occur as a result of querying a
 * {@link PIPEngine} for {@link com.att.research.xacml.api.Attribute}s.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class PIPException extends Exception {
	private static final long serialVersionUID = -6656926395983776184L;
	private PIPRequest pipRequest;
	private PIPEngine pipEngine;
	
	public PIPException() {
	}

	public PIPException(String message) {
		super(message);
	}

	public PIPException(Throwable cause) {
		super(cause);
	}

	public PIPException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PIPException(PIPEngine pipEngineIn, PIPRequest pipRequestIn, String message, Throwable cause) {
		this(message, cause);
		this.pipEngine	= pipEngineIn;
		this.pipRequest	= pipRequestIn;
	}
	
	public PIPException(PIPEngine pipEngineIn, PIPRequest pipRequestIn, String message) {
		this(message);
		this.pipEngine	= pipEngineIn;
		this.pipRequest	= pipRequestIn;
	}

	/**
	 * Gets the <code>PIPRequest</code> that caused this <code>PIPException</code>
	 * 
	 * @return the <code>PIPRequest</code> that caused this <code>PIPException</code>
	 */
	public PIPRequest getPIPRequest() {
		return this.pipRequest;
	}
	
	/**
	 * Gets the <code>PIPEngine</code> that caused this <code>PIPException</code>.
	 * 
	 * @return the <code>PIPEngine</code> that caused this <code>PIPException</code>
	 */
	public PIPEngine getPIPEngine() {
		return this.pipEngine;
	}
}
