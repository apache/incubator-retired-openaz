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
package com.att.research.xacml.std;

import com.att.research.xacml.api.RequestAttributesReference;
import com.att.research.xacml.util.ObjUtil;

/**
 * Immutable implementation of the {@link com.att.research.xacml.api.RequestAttributesReference} interface.
 *  
 * @author Christopher A. Rath
 * @version $Revision: 1.1 $
 */
public class StdRequestAttributesReference implements RequestAttributesReference {
	private String	referenceId;

	/**
	 * Creates a new <code>StdRequestAttributesReference</code> with the given <code>String</code> representing the xml:Id.
	 * 
	 * @param referenceIdIn the <code>String</code> representing the xml:Id of the XACML AttributesReference represented by the new <code>StdRequestAttributesReference</code>.
	 */
	public StdRequestAttributesReference(String referenceIdIn) {
		this.referenceId	= referenceIdIn;
	}

	@Override
	public String getReferenceId() {
		return this.referenceId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj == null || !(obj instanceof RequestAttributesReference)) {
			return false;
		} else {
			RequestAttributesReference objRequestAttributesReference	= (RequestAttributesReference)obj;
			return ObjUtil.equalsAllowNull(this.getReferenceId(), objRequestAttributesReference.getReferenceId());
		}
	}
	
	@Override
	public String toString() {
		StringBuilder	stringBuilder	= new StringBuilder("{");
		Object			objectToDump	= this.getReferenceId();
		if (objectToDump != null) {
			stringBuilder.append("referenceId=");
			stringBuilder.append((String)objectToDump);
		}
		stringBuilder.append('}');
		return stringBuilder.toString();
	}

}
