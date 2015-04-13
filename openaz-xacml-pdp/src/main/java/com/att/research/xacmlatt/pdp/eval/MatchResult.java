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

import com.att.research.xacml.api.Status;
import com.att.research.xacml.std.StdStatus;

/**
 * MatchResult is the value returned by the {@link Matchable} interface.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class MatchResult {
	public static enum MatchCode {
		INDETERMINATE,
		MATCH,
		NOMATCH
	}
	
	public static MatchResult	MM_MATCH	= new MatchResult(MatchCode.MATCH);
	public static MatchResult	MM_NOMATCH	= new MatchResult(MatchCode.NOMATCH);
	
	private MatchCode	matchCode;
	private Status		status;
	
	public MatchResult(MatchCode matchCodeIn, Status statusIn) {
		this.matchCode	= matchCodeIn;
		this.status		= statusIn;
	}
	
	public MatchResult(MatchCode matchCodeIn) {
		this(matchCodeIn, StdStatus.STATUS_OK);
	}
	
	public MatchResult(Status statusIn) {
		this(MatchCode.INDETERMINATE, statusIn);
	}
	
	public MatchCode getMatchCode() {
		return this.matchCode;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder	= new StringBuilder("{");
		
		stringBuilder.append("matchCode=");
		stringBuilder.append(this.getMatchCode());
		Status thisStatus	= this.getStatus();
		if (thisStatus != null) {
			stringBuilder.append(", status=");
			stringBuilder.append(thisStatus.toString());
		}
		stringBuilder.append('}');
		return stringBuilder.toString();
	}
}
