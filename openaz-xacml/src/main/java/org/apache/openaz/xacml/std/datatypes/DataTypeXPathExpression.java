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
package org.apache.openaz.xacml.std.datatypes;

import javax.xml.xpath.XPathExpression;

import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.XACML3;
import org.w3c.dom.Node;

/**
 * DataTypeXPathExpression extends {@link DataTypeBase} to implement the XACML xpathExpression data type.
 */
public class DataTypeXPathExpression extends DataTypeBase<XPathExpressionWrapper> {
    private static final DataTypeXPathExpression singleInstance = new DataTypeXPathExpression();

    private DataTypeXPathExpression() {
        super(XACML3.ID_DATATYPE_XPATHEXPRESSION, XPathExpressionWrapper.class);
    }

    public static DataTypeXPathExpression newInstance() {
        return singleInstance;
    }

    @Override
    public XPathExpressionWrapper convert(Object source) throws DataTypeException {
        if (source == null || source instanceof XPathExpressionWrapper) {
            return (XPathExpressionWrapper)source;
        } else if (source instanceof XPathExpression) {
            return new XPathExpressionWrapper((XPathExpression)source);
        } else if (source instanceof Node) {
            Node node = (Node)source;
            return new XPathExpressionWrapper(node.getOwnerDocument(), node.getTextContent());
        } else {
            return new XPathExpressionWrapper(this.convertToString(source));
        }
    }

}
