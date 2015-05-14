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
package org.apache.openaz.xacml.std.pip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.api.pip.PIPResponse;
import org.apache.openaz.xacml.std.StdStatus;

/**
 * Mutable implementation of the {@link org.apache.openaz.xacml.api.pip.PIPResponse} interface with methods for
 * keeping a collection of {@link org.apache.openaz.xacml.api.Attribute}s with a
 * {@link org.apache.openaz.xacml.api.Status}.
 */
public class StdMutablePIPResponse implements PIPResponse {
    private static final List<Attribute> EMPTY_LIST = Collections
        .unmodifiableList(new ArrayList<Attribute>());

    private List<Attribute> attributes = EMPTY_LIST;
    private Status status;
    private boolean simple;

    /**
     * Creates a new <code>StdMutablePIPResponse</code> with the given
     * {@link org.apache.openaz.xacml.api.Status}.
     *
     * @param statusIn the <code>Status</code> of the new <code>StdMutablePIPResponse</code>
     */
    public StdMutablePIPResponse(Status statusIn) {
        this.status = statusIn;
        this.simple = true;
    }

    /**
     * Creates a new <code>StdMutablePIPResponse</code> with an OK {@link org.apache.openaz.xacml.api.Status}
     * and the single given {@link org.apache.openaz.xacml.api.Attribute}.
     *
     * @param attribute the <code>Attribute</code> for the new <code>StdMutablePIPResponse</code>>
     */
    public StdMutablePIPResponse(Attribute attribute) {
        this(StdStatus.STATUS_OK);
        if (attribute != null) {
            this.addAttribute(attribute);
        }
    }

    /**
     * Creates a new <code>StdMutablePIPResponse</code> with an OK {@link org.apache.openaz.xacml.api.Status}
     * and a copy of the given <code>Collection</code> of {@link org.apache.openaz.xacml.api.Attribute}s.
     *
     * @param attributesIn the <code>Collection</code> of <code>Attribute</code>s for the new
     *            <code>StdMutablePIPResponse</code>.
     */
    public StdMutablePIPResponse(Collection<Attribute> attributesIn) {
        this(StdStatus.STATUS_OK);
        if (attributesIn != null) {
            this.addAttributes(attributesIn);
        }
    }

    /**
     * Creates a new <code>StdMutablePIPResponse</code> with an OK {@link org.apache.openaz.xacml.api.Status}.
     */
    public StdMutablePIPResponse() {
        this(StdStatus.STATUS_OK);
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Status} for this <code>StdMutablePIPResponse</code>>
     *
     * @param statusIn the <code>Status</code> for this <code>StdMutablePIPResponse</code>.
     */
    public void setStatus(Status statusIn) {
        this.status = statusIn;
    }

    @Override
    public boolean isSimple() {
        return this.simple;
    }

    @Override
    public Collection<Attribute> getAttributes() {
        return (this.attributes == EMPTY_LIST ? this.attributes : Collections
            .unmodifiableCollection(this.attributes));
    }

    /**
     * Adds a single {@link org.apache.openaz.xacml.api.Attribute} to this <code>StdMutablePIPResponse</code>.
     *
     * @param attributeIn the <code>Attribute</code> to add to this <code>StdMutablePIPResponse</code>.
     */
    public void addAttribute(Attribute attributeIn) {
        if (this.attributes == EMPTY_LIST) {
            this.attributes = new ArrayList<Attribute>();
        }
        /*
         * Determine if the simple status should be changed or not
         */
        if (this.simple && this.attributes.size() > 0) {
            this.simple = false;
        }
        this.attributes.add(attributeIn);
    }

    /**
     * Adds a copy of the given <code>Collection</code> of {@link org.apache.openaz.xacml.api.Attribute}s to
     * this <code>StdMutablePIPResponse</code>.
     *
     * @param attributesIn the <code>Collection</code> of <code>Attribute</code>s to add to this
     *            <code>StdMutablePIPResponse</code>.
     */
    public void addAttributes(Collection<Attribute> attributesIn) {
        if (attributesIn != null && attributesIn.size() > 0) {
            if (this.attributes == EMPTY_LIST) {
                this.attributes = new ArrayList<Attribute>();
            }
            if (this.simple && (this.attributes.size() > 0 || attributesIn.size() > 1)) {
                this.simple = false;
            }
            this.attributes.addAll(attributesIn);
        }
    }

    /**
     * Sets the {@link org.apache.openaz.xacml.api.Attribute}s in this <code>StdMutablePIPResponse</code> to a
     * copy of the given <code>Collection</code>.
     *
     * @param attributesIn the <code>Collection</code> of <code>Attribute</code>s to set in this
     *            <code>StdMutablePIPResponse</code>.
     */
    public void setAttributes(Collection<Attribute> attributesIn) {
        this.attributes = EMPTY_LIST;
        this.simple = true;
        this.addAttributes(attributesIn);
    }

}
