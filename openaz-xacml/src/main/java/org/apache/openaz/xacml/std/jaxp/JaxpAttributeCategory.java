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
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeCategory;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdAttributeCategory;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesType;

/**
 * JaxpAttributeCategory extends {@link org.apache.openaz.xacml.std.StdAttributeCategory} with methods for
 * creation from JAXP elements.
 */
public class JaxpAttributeCategory {

    protected JaxpAttributeCategory() {
    }

    public static AttributeCategory newInstance(AttributesType attributesType) {
        if (attributesType == null) {
            throw new NullPointerException("Null AttributesType");
        } else if (attributesType.getCategory() == null) {
            throw new IllegalArgumentException("Null categoryId for AttributesType");
        }
        Identifier identifierCategory = new IdentifierImpl(attributesType.getCategory());
        List<Attribute> listAttributes = new ArrayList<Attribute>();

        if (attributesType.getAttribute() != null && attributesType.getAttribute().size() > 0) {
            Iterator<AttributeType> iterAttributeTypes = attributesType.getAttribute().iterator();
            while (iterAttributeTypes.hasNext()) {
                listAttributes.add(JaxpAttribute.newInstance(identifierCategory, iterAttributeTypes.next()));
            }
        }
        return new StdAttributeCategory(identifierCategory, listAttributes);
    }

}
