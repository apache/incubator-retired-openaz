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
package org.apache.openaz.xacml.pdp.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.w3c.dom.Node;

/**
 * PolicyIssuer extends {@link org.apache.openaz.xacml.pdp.policy.PolicyComponent} to represent the XACML
 * 3.0 PolicyIssuer element in Policies and PolicySets.
 */
public class PolicyIssuer extends PolicyComponent {
    private Node content;
    private List<Attribute> attributes;

    public PolicyIssuer(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public PolicyIssuer(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public PolicyIssuer() {
    }

    public Node getContent() {
        return this.content;
    }

    public void setContent(Node nodeContent) {
        this.content = nodeContent;
    }

    public Iterator<Attribute> getAttributes() {
        return (this.attributes == null ? null : this.attributes.iterator());
    }

    public void setAttributes(Collection<Attribute> listAttributes) {
        this.attributes = null;
        if (listAttributes != null) {
            this.add(listAttributes);
        }
    }

    public void add(Attribute attribute) {
        if (this.attributes == null) {
            this.attributes = new ArrayList<Attribute>();
        }
        this.attributes.add(attribute);
    }

    public void add(Collection<Attribute> listAttributes) {
        if (this.attributes == null) {
            this.attributes = new ArrayList<Attribute>();
        }
        this.attributes.addAll(listAttributes);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        Object objectToDump;
        if ((objectToDump = this.getContent()) != null) {
            stringBuilder.append(",content=");
            stringBuilder.append(objectToDump.toString());
        }
        Iterator<Attribute> iterAttributes = this.getAttributes();
        if (iterAttributes != null && iterAttributes.hasNext()) {
            stringBuilder.append(",attributes=[");
            stringBuilder.append(iterAttributes.next().toString());
            while (iterAttributes.hasNext()) {
                stringBuilder.append(',');
                stringBuilder.append(iterAttributes.next().toString());
            }
            stringBuilder.append(']');
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    protected boolean validateComponent() {
        this.setStatus(StdStatusCode.STATUS_CODE_OK, null);
        return true;
    }

}
