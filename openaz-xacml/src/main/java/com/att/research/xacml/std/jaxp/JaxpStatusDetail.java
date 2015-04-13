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
package com.att.research.xacml.std.jaxp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MissingAttributeDetailType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.StatusDetailType;

import com.att.research.xacml.api.MissingAttributeDetail;
import com.att.research.xacml.std.StdMutableStatusDetail;

/**
 * JaxpStatusDetail extends {@link com.att.research.xacml.std.StdMutableStatusDetail} with methods for creation from
 * JAXP elements.
 * 
 * @author car
 * @version $Revision: 1.1 $
 */
public class JaxpStatusDetail extends StdMutableStatusDetail {

	protected JaxpStatusDetail(Collection<MissingAttributeDetail> missingAttributeDetailsIn) {
		super(missingAttributeDetailsIn);
	}

	public static JaxpStatusDetail newInstance(StatusDetailType statusDetailType) {
		if (statusDetailType == null) {
			throw new NullPointerException("Null StatusDetailType");
		}
		List<MissingAttributeDetail>	listMissingAttributeDetails	= null;
		if (statusDetailType.getAny() != null && statusDetailType.getAny().size() > 0) {
			Iterator<Object>	iterObjects	= statusDetailType.getAny().iterator();
			while (iterObjects.hasNext()) {
				Object object	= iterObjects.next();
				if (object instanceof MissingAttributeDetailType) {
					if (listMissingAttributeDetails == null) {
						listMissingAttributeDetails	= new ArrayList<MissingAttributeDetail>();
					}
					listMissingAttributeDetails.add(JaxpMissingAttributeDetail.newInstance((MissingAttributeDetailType)object));
				}
			}
		}
		return new JaxpStatusDetail(listMissingAttributeDetails);
	}
}
