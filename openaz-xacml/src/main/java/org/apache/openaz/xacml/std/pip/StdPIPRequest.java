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

import java.util.Collection;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.util.ObjUtil;

public class StdPIPRequest implements PIPRequest {
    private Identifier category;
    private Identifier attributeId;
    private Identifier dataTypeId;
    private String issuer;

    private static Identifier getDataType(Attribute attribute) {
        Collection<AttributeValue<?>> values = attribute.getValues();
        if (values != null && values.size() > 0) {
            return values.iterator().next().getDataTypeId();
        } else {
            return null;
        }
    }

    public StdPIPRequest(Identifier identifierCategory, Identifier identifierAttribute,
                         Identifier identifierDataType) {
        this.category = identifierCategory;
        this.attributeId = identifierAttribute;
        this.dataTypeId = identifierDataType;
    }

    public StdPIPRequest(Identifier identifierCategory, Identifier identifierAttribute,
                         Identifier identifierDataType, String issuerIn) {
        this(identifierCategory, identifierAttribute, identifierDataType);
        this.issuer = issuerIn;
    }

    public StdPIPRequest(Attribute attribute, Identifier identifierDataType) {
        this(attribute.getCategory(), attribute.getAttributeId(), identifierDataType, attribute.getIssuer());
    }

    public StdPIPRequest(Attribute attribute) {
        this(attribute, getDataType(attribute));
    }

    public StdPIPRequest(PIPRequest req) {
        this(req.getCategory(), req.getAttributeId(), req.getDataTypeId(), req.getIssuer());
    }

    @Override
    public Identifier getCategory() {
        return this.category;
    }

    @Override
    public Identifier getAttributeId() {
        return this.attributeId;
    }

    @Override
    public Identifier getDataTypeId() {
        return this.dataTypeId;
    }

    @Override
    public String getIssuer() {
        return this.issuer;
    }

    @Override
    public int hashCode() {
        Identifier identifier;
        int hc = 1;
        if ((identifier = this.getCategory()) != null) {
            hc += identifier.hashCode();
        }
        if ((identifier = this.getAttributeId()) != null) {
            hc += identifier.hashCode();
        }
        if ((identifier = this.getDataTypeId()) != null) {
            hc += identifier.hashCode();
        }
        String thisIssuer = this.getIssuer();
        if (thisIssuer != null) {
            hc += thisIssuer.hashCode();
        }
        return hc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof PIPRequest)) {
            return false;
        } else {
            PIPRequest pipRequest = (PIPRequest)obj;
            return ObjUtil.equalsAllowNull(this.getCategory(), pipRequest.getCategory())
                   && ObjUtil.equalsAllowNull(this.getAttributeId(), pipRequest.getAttributeId())
                   && ObjUtil.equalsAllowNull(this.getDataTypeId(), pipRequest.getDataTypeId())
                   && ObjUtil.equalsAllowNull(this.getIssuer(), pipRequest.getIssuer());
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        boolean needsComma = false;
        Object objectToDump;

        if ((objectToDump = this.getCategory()) != null) {
            stringBuilder.append("category=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }
        if ((objectToDump = this.getAttributeId()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("attributeId=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }
        if ((objectToDump = this.getDataTypeId()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("dataTypeId=");
            stringBuilder.append(objectToDump.toString());
            needsComma = true;
        }
        if ((objectToDump = this.getIssuer()) != null) {
            if (needsComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("issuer=");
            stringBuilder.append((String)objectToDump);
            needsComma = true;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
