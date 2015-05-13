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
package org.apache.openaz.xacml.std;

import java.util.Collection;

import org.apache.openaz.xacml.api.MissingAttributeDetail;
import org.apache.openaz.xacml.api.StatusDetail;
import org.apache.openaz.xacml.util.Wrapper;

/**
 * Immutable implementation of the {@link org.apache.openaz.xacml.api.StatusDetail} interface.
 */
public class StdStatusDetail extends Wrapper<StatusDetail> implements StatusDetail {
    /**
     * Creates a new immutable <code>StdStatusDetail</code> that wraps the given
     * {@link org.apache.openaz.xacml.api.StatusDetail}. The caller agrees not to modify the given
     * <code>StatusDetail</code> as long as the new <code>StdStatusDetail</code> refers to it.
     *
     * @param statusDetail the <code>StatusDetail</code> to wrap
     */
    public StdStatusDetail(StatusDetail statusDetail) {
        super(statusDetail);
    }

    /**
     * Creates a new immutable empty <code>StdStatusDetail</code>.
     */
    public StdStatusDetail() {
        this(new StdMutableStatusDetail());
    }

    /**
     * Creates a new immutable <code>StdStatusDetail</code> with the given <code>Collection</code> of
     * {@link org.apache.openaz.xamcl.api.MissingAttributeDetail}s.
     *
     * @param missingAttributeDetailsIn the <code>Collection</code> of <code>MissingAttributeDetail</code>s
     *            for the new <code>StdStatusDetail</code>.
     */
    public StdStatusDetail(Collection<MissingAttributeDetail> missingAttributeDetailsIn) {
        this(new StdMutableStatusDetail(missingAttributeDetailsIn));
    }

    /**
     * Creates a new immutable <code>StdStatusDetail</code> with the given
     * {@link org.apache.openaz.xacml.api.MissingAttributeDetail}.
     *
     * @param missingAttributeDetail the <code>MissingAttributeDetail</code> for the new
     *            <code>StdStatusDetail</code>
     */
    public StdStatusDetail(MissingAttributeDetail missingAttributeDetail) {
        this(new StdMutableStatusDetail(missingAttributeDetail));
    }

    /**
     * Creates a new
     * <code>StdStatusDetail that is a copy of the given {@link org.apache.openaz.xacml.api.StatusDetail}.
     *
     * @param statusDetail the <code>StatusDetail</code> to copy
     * @return a new <code>StdStatusDetail</code> that is a copy of the given <code>StatusDetail</code>.
     */
    public static StdStatusDetail copy(StatusDetail statusDetail) {
        return new StdStatusDetail(statusDetail.getMissingAttributeDetails());
    }

    @Override
    public Collection<MissingAttributeDetail> getMissingAttributeDetails() {
        return this.getWrappedObject().getMissingAttributeDetails();
    }

    @Override
    public StatusDetail merge(StatusDetail statusDetail) {
        return new StdStatusDetail(this.getWrappedObject().merge(statusDetail));
    }

}
