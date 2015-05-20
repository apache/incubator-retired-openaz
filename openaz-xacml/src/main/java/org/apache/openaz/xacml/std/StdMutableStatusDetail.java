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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.openaz.xacml.api.MissingAttributeDetail;
import org.apache.openaz.xacml.api.StatusDetail;
import org.apache.openaz.xacml.util.ListUtil;

/**
 * Mutable implementation of the {@link org.apache.openaz.xacml.api.StatusDetail} interface to implement the
 * XACML StatusDetail element.
 */
public class StdMutableStatusDetail implements StatusDetail {
    private static final List<MissingAttributeDetail> EMPTY_LIST = Collections
        .unmodifiableList(new ArrayList<MissingAttributeDetail>());

    private List<MissingAttributeDetail> missingAttributeDetails;

    /**
     * Creates a new empty <code>StdMutableStatusDetail</code>.
     */
    public StdMutableStatusDetail() {
        this.missingAttributeDetails = EMPTY_LIST;
    }

    /**
     * Creates a new <code>StdMutableStatusDetail</code> with a copy of the given <code>Collection</code> of
     * {@link org.apache.openaz.xacml.api.MissingAttributeDetail}s.
     *
     * @param missingAttributeDetailsIn the <code>Collection</code> of <code>MissingAttributeDetail</code>s to
     *            copy
     */
    public StdMutableStatusDetail(Collection<MissingAttributeDetail> missingAttributeDetailsIn) {
        this();
        this.setMissingAttributeDetails(missingAttributeDetailsIn);
    }

    /**
     * Creates a new <code>StdMutableStatusDetail</code> with the given single
     * {@link org.apache.openaz.xacml.api.MissingAttributeDetail}.
     *
     * @param missingAttributeDetail the <code>MissingAttributeDetail</code> for the new
     *            <code>StdMutableStatusDetail</code>.
     */
    public StdMutableStatusDetail(MissingAttributeDetail missingAttributeDetail) {
        this();
        this.addMissingAttributeDetail(missingAttributeDetail);
    }

    /**
     * Creates a new <code>StdMutableStatusDetail</code> that is a copy of the given
     * {@link org.apache.openaz.xacml.api.StatusDetail}.
     *
     * @param statusDetail the <code>StatusDetail</code> to copy
     * @return a new <code>StdMutableStatusDetail</code> that is a copy of the given <code>StatusDetail</code>
     *         .
     */
    public static StdMutableStatusDetail copy(StatusDetail statusDetail) {
        return new StdMutableStatusDetail(statusDetail.getMissingAttributeDetails());
    }

    @Override
    public Collection<MissingAttributeDetail> getMissingAttributeDetails() {
        return this.missingAttributeDetails == EMPTY_LIST ? this.missingAttributeDetails : Collections
            .unmodifiableCollection(this.missingAttributeDetails);
    }

    /**
     * Adds a {@link org.apache.openaz.xacml.api.MissingAttributeDetail} to this
     * <code>StdMutableStatusDetail</code>.
     *
     * @param missingAttributeDetail the <code>MissingAttributeDetail</code> to add to this
     *            <code>StdMutableStatusDetail</code>.
     */
    public void addMissingAttributeDetail(MissingAttributeDetail missingAttributeDetail) {
        if (this.missingAttributeDetails == EMPTY_LIST) {
            this.missingAttributeDetails = new ArrayList<MissingAttributeDetail>();
        }
        this.missingAttributeDetails.add(missingAttributeDetail);
    }

    /**
     * Adds a copy of the given <code>Collection</code> of
     * {@link org.apache.openaz.xacml.api.MissingAttributeDetail}s to this <code>StdMutableStatusDetail</code>.
     *
     * @param missingAttributeDetailsIn the <code>Collection</code> of <code>MissingAttributeDetail</code>s to
     *            add to this <code>StdMutableStatusDetail</code>.
     */
    public void addMissingAttributeDetails(Collection<MissingAttributeDetail> missingAttributeDetailsIn) {
        if (missingAttributeDetailsIn != null && missingAttributeDetailsIn.size() > 0) {
            if (this.missingAttributeDetails == EMPTY_LIST) {
                this.missingAttributeDetails = new ArrayList<MissingAttributeDetail>();
            }
            this.missingAttributeDetails.addAll(missingAttributeDetailsIn);
        }
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.MissingAttributeDetail}s for this
     * <code>StdMutableStatusDetail</code> to a copy of the given <code>Collection</code>.
     *
     * @param missingAttributeDetailsIn the <code>Collection</code> of <code>MissingAttributeDetail</code>s to
     *            set in this <code>StdMutableStatusDetail</code>.
     */
    public void setMissingAttributeDetails(Collection<MissingAttributeDetail> missingAttributeDetailsIn) {
        this.missingAttributeDetails = EMPTY_LIST;
        this.addMissingAttributeDetails(missingAttributeDetailsIn);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof StatusDetail)) {
            return false;
        } else {
            StatusDetail objStatusDetail = (StatusDetail)obj;
            return ListUtil.equalsAllowNulls(this.getMissingAttributeDetails(),
                                             objStatusDetail.getMissingAttributeDetails());
        }
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        if (getMissingAttributeDetails() != null) {
            result = 31 * result + getMissingAttributeDetails().hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        Collection<MissingAttributeDetail> listMissingAttributeDetail = this.getMissingAttributeDetails();
        if (listMissingAttributeDetail.size() > 0) {
            stringBuilder.append("missingAttributeDetails=[");
            stringBuilder.append(ListUtil.toString(listMissingAttributeDetail));
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    public StatusDetail merge(StatusDetail statusDetail) {
        if (statusDetail == null) {
            return this;
        }
        Collection<MissingAttributeDetail> listMissingAttributeDetails = statusDetail
            .getMissingAttributeDetails();
        if (listMissingAttributeDetails.size() == 0) {
            return this;
        }
        if (this.getMissingAttributeDetails().size() == 0) {
            return statusDetail;
        }
        StdMutableStatusDetail stdStatusDetail = StdMutableStatusDetail.copy(this);
        stdStatusDetail.addMissingAttributeDetails(listMissingAttributeDetails);
        return stdStatusDetail;
    }

}
