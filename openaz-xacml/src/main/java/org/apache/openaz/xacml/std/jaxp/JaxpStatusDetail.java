/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

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
package org.apache.openaz.xacml.std.jaxp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.MissingAttributeDetail;
import org.apache.openaz.xacml.std.StdMutableStatusDetail;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MissingAttributeDetailType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.StatusDetailType;

/**
 * JaxpStatusDetail extends {@link org.apache.openaz.xacml.std.StdMutableStatusDetail} with methods for
 * creation from JAXP elements.
 */
public class JaxpStatusDetail extends StdMutableStatusDetail {

    protected JaxpStatusDetail(Collection<MissingAttributeDetail> missingAttributeDetailsIn) {
        super(missingAttributeDetailsIn);
    }

    public static JaxpStatusDetail newInstance(StatusDetailType statusDetailType) {
        if (statusDetailType == null) {
            throw new NullPointerException("Null StatusDetailType");
        }
        List<MissingAttributeDetail> listMissingAttributeDetails = null;
        if (statusDetailType.getAny() != null && statusDetailType.getAny().size() > 0) {
            Iterator<Object> iterObjects = statusDetailType.getAny().iterator();
            while (iterObjects.hasNext()) {
                Object object = iterObjects.next();
                if (object instanceof MissingAttributeDetailType) {
                    if (listMissingAttributeDetails == null) {
                        listMissingAttributeDetails = new ArrayList<MissingAttributeDetail>();
                    }
                    listMissingAttributeDetails.add(JaxpMissingAttributeDetail
                        .newInstance((MissingAttributeDetailType)object));
                }
            }
        }
        return new JaxpStatusDetail(listMissingAttributeDetails);
    }
}
