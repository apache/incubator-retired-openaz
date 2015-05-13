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
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.Status;
import org.apache.openaz.xacml.api.pdp.ScopeResolverResult;
import org.apache.openaz.xacml.util.StringUtils;

/**
 * StdScopeResolverResult implements the {@link org.apache.openaz.xacml.api.pdp.ScopeResolverResult} interface.
 */
public class StdScopeResolverResult implements ScopeResolverResult {
    private Status status;
    private List<Attribute> attributes = new ArrayList<Attribute>();

    public StdScopeResolverResult(Status statusIn, Collection<Attribute> attributesIn) {
        this.status = statusIn;
        if (attributesIn != null) {
            this.attributes.addAll(attributesIn);
        }
    }

    public StdScopeResolverResult(Status statusIn) {
        this(statusIn, null);
    }

    public StdScopeResolverResult(Collection<Attribute> attributesIn) {
        this(StdStatus.STATUS_OK, attributesIn);
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status statusIn) {
        this.status = statusIn;
    }

    @Override
    public Iterator<Attribute> getAttributes() {
        return this.attributes.iterator();
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        boolean needsComma = false;

        Status statusToDump = this.getStatus();
        if (statusToDump != null) {
            stringBuilder.append("status=");
            stringBuilder.append(statusToDump.toString());
            needsComma = true;
        }
        Iterator<Attribute> iterAttributes = this.getAttributes();
        if (iterAttributes != null && iterAttributes.hasNext()) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("attributes=");
            stringBuilder.append(StringUtils.toString(iterAttributes, true));
            needsComma = true;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
