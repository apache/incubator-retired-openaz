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
package org.apache.openaz.xacml.pdp.policy.expressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.DataTypeFactory;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.RequestAttributes;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.policy.Bag;
import org.apache.openaz.xacml.pdp.policy.ExpressionResult;
import org.apache.openaz.xacml.pdp.policy.PolicyDefaults;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;
import org.apache.openaz.xacml.std.datatypes.DataTypes;
import org.apache.openaz.xacml.std.datatypes.NodeNamespaceContext;
import org.apache.openaz.xacml.std.datatypes.XPathExpressionWrapper;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.apache.openaz.xacml.util.FactoryException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * AttributeSelector extends {@link org.apache.openaz.xacml.pdp.policy.expressions.AttributeRetrievalBase}
 * to implement the XACML AttributeSelector element.
 */
public class AttributeSelector extends AttributeRetrievalBase {
    private Identifier contextSelectorId;
    private String path;
    @SuppressWarnings("unused")
    private DataType<?> dataType;

    protected DataType<?> getDataType() {
        Identifier dataTypeIdThis = this.getDataTypeId();
        if (dataTypeIdThis == null) {
            return null;
        } else {
            DataTypeFactory dataTypeFactory = null;
            try {
                dataTypeFactory = DataTypeFactory.newInstance();
                if (dataTypeFactory == null) {
                    return null;
                }
            } catch (FactoryException ex) {
                return null;
            }
            return (this.dataType = dataTypeFactory.getDataType(dataTypeIdThis));
        }
    }

    public AttributeSelector(StatusCode statusCodeIn, String statusMessageIn) {
        super(statusCodeIn, statusMessageIn);
    }

    public AttributeSelector(StatusCode statusCodeIn) {
        super(statusCodeIn);
    }

    public AttributeSelector() {
    }

    public Identifier getContextSelectorId() {
        return this.contextSelectorId;
    }

    public void setContextSelectorId(Identifier identifier) {
        this.contextSelectorId = identifier;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String pathIn) {
        this.path = pathIn;
    }

    @Override
    protected boolean validateComponent() {
        if (!super.validateComponent()) {
            return false;
        } else if (this.getPath() == null) {
            this.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, "Missing Path");
            return false;
        } else {
            return true;
        }
    }

    /**
     * If there is a context selector ID, get the attributes from the given <code>RequestAttributes</code>
     * with that ID, ensure they are <code>XPathExpression</code>s and return them.
     *
     * @param requestAttributes
     * @return
     */
    protected List<XPathExpression> getContextSelectorValues(RequestAttributes requestAttributes) {
        Identifier thisContextSelectorId = this.getContextSelectorId();
        if (thisContextSelectorId == null) {
            return null;
        }
        List<XPathExpression> listXPathExpressions = null;
        Iterator<Attribute> iterAttributes = requestAttributes.getAttributes(thisContextSelectorId);
        if (iterAttributes != null) {
            while (iterAttributes.hasNext()) {
                Attribute attribute = iterAttributes.next();
                Iterator<AttributeValue<XPathExpressionWrapper>> iterXPathExpressions = attribute
                    .findValues(DataTypes.DT_XPATHEXPRESSION);
                if (iterXPathExpressions != null && iterXPathExpressions.hasNext()) {
                    if (listXPathExpressions == null) {
                        listXPathExpressions = new ArrayList<XPathExpression>();
                    }
                    listXPathExpressions.add(iterXPathExpressions.next().getValue());
                }
            }
        }
        return listXPathExpressions;
    }

    @Override
    public ExpressionResult evaluate(EvaluationContext evaluationContext, PolicyDefaults policyDefaults)
        throws EvaluationException {
        if (!this.validate()) {
            return ExpressionResult.newError(new StdStatus(this.getStatusCode(), this.getStatusMessage()));
        }

        /*
         * Get the DataType for this AttributeSelector for converting the resulting nodes into AttributeValues
         */
        DataType<?> thisDataType = this.getDataType();

        /*
         * Get the Request so we can find the XPathExpression to locate the root node and to find the Content
         * element of the requested category.
         */
        Request request = evaluationContext.getRequest();
        assert request != null;

        /*
         * Get the RequestAttributes objects for our Category. If none are found, then we abort quickly with
         * either an empty or indeterminate result.
         */
        Iterator<RequestAttributes> iterRequestAttributes = request.getRequestAttributes(this.getCategory());
        if (iterRequestAttributes == null || !iterRequestAttributes.hasNext()) {
            return this.getEmptyResult("No Attributes with Category " + this.getCategory().toString(), null);
        }

        /*
         * Section 5.30 of the XACML 3.0 specification is a little vague about how to use the
         * ContextSelectorId in the face of having multiple Attributes elements with the same CategoryId. My
         * interpretation is that each is distinct, so we look for an attribute matching the ContextSelectorId
         * in each matching Attributes element and use that to search the Content in that particular
         * Attributes element. If either an Attribute matching the context selector id is not found or there
         * is no Content, then that particular Attributes element is skipped.
         */
        Bag bagAttributeValues = new Bag();
        StdStatus statusFirstError = null;
        while (iterRequestAttributes.hasNext()) {
            RequestAttributes requestAttributes = iterRequestAttributes.next();

            /*
             * See if we have a Content element to query.
             */
            Node nodeContentRoot = requestAttributes.getContentRoot();
            if (nodeContentRoot != null) {
                List<Node> listNodesToQuery = new ArrayList<Node>();
                List<XPathExpression> listXPathExpressions = this.getContextSelectorValues(requestAttributes);
                if (listXPathExpressions == null) {
                    listNodesToQuery.add(nodeContentRoot);
                } else {
                    Iterator<XPathExpression> iterXPathExpressions = listXPathExpressions.iterator();
                    while (iterXPathExpressions.hasNext()) {
                        XPathExpression xpathExpression = iterXPathExpressions.next();
                        Node nodeContent = requestAttributes.getContentNodeByXpathExpression(xpathExpression);
                        if (nodeContent != null) {
                            listNodesToQuery.add(nodeContent);
                        }
                    }
                }

                /*
                 * If there are any nodes to query, do so now and add the results
                 */
                if (listNodesToQuery.size() > 0) {
                    for (Node nodeToQuery : listNodesToQuery) {
                        NodeList nodeList = null;
                        try {
                            XPath xPath = XPathFactory.newInstance().newXPath();
                            xPath
                                .setNamespaceContext(new NodeNamespaceContext(nodeToQuery.getOwnerDocument()));
                            XPathExpression xPathExpression = xPath.compile(this.getPath());
                            Node nodeToQueryDocumentRoot = null;
                            try {
                                nodeToQueryDocumentRoot = DOMUtil.getDirectDocumentChild(nodeToQuery);
                            } catch (DOMStructureException ex) {
                                return ExpressionResult
                                    .newError(new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                            "Exception processing context node: "
                                                                + ex.getMessage()));
                            }
                            nodeList = (NodeList)xPathExpression.evaluate(nodeToQueryDocumentRoot,
                                                                          XPathConstants.NODESET);
                        } catch (XPathExpressionException ex) {
                            if (statusFirstError == null) {
                                statusFirstError = new StdStatus(StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                 "XPathExpressionException: "
                                                                     + ex.getMessage());
                            }
                        }
                        if (nodeList != null && nodeList.getLength() > 0) {
                            for (int i = 0; i < nodeList.getLength(); i++) {
                                AttributeValue<?> attributeValueNode = null;
                                try {
                                    attributeValueNode = thisDataType.createAttributeValue(nodeList.item(i));
                                } catch (DataTypeException ex) {
                                    if (statusFirstError == null) {
                                        statusFirstError = new StdStatus(
                                                                         StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                         ex.getMessage());
                                    }
                                }
                                if (attributeValueNode != null) {
                                    bagAttributeValues.add(attributeValueNode);
                                } else if (statusFirstError == null) {
                                    statusFirstError = new StdStatus(
                                                                     StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                     "Unable to convert node to "
                                                                         + this.getDataTypeId().toString());
                                }
                            }
                        }
                    }
                }
            }

        }

        if (bagAttributeValues.size() == 0) {
            if (statusFirstError == null) {
                return this.getEmptyResult("No Content found", null);
            } else {
                return ExpressionResult.newError(statusFirstError);
            }
        } else {
            return ExpressionResult.newBag(bagAttributeValues);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");

        stringBuilder.append("super=");
        stringBuilder.append(super.toString());

        Object objectToDump;
        if ((objectToDump = this.getContextSelectorId()) != null) {
            stringBuilder.append(",contextSelectorId=");
            stringBuilder.append(objectToDump.toString());
        }
        if ((objectToDump = this.getPath()) != null) {
            stringBuilder.append(",path=");
            stringBuilder.append((String)objectToDump);
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
