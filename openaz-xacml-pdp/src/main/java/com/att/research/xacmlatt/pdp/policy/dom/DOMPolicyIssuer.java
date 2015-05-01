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
package com.att.research.xacmlatt.pdp.policy.dom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdStatusCode;
import com.att.research.xacml.std.dom.DOMAttribute;
import com.att.research.xacml.std.dom.DOMProperties;
import com.att.research.xacml.std.dom.DOMStructureException;
import com.att.research.xacml.std.dom.DOMUtil;
import com.att.research.xacmlatt.pdp.policy.PolicyIssuer;

/**
 * DOMPolicyIssuer extends {@link com.att.research.xacmlatt.pdp.policy.PolicyIsser} with methods for creation from
 * DOM {@link org.w3c.dom.Node}s.
 * 
 * @author car
 * @version $Revision: 1.2 $
 */
public class DOMPolicyIssuer extends PolicyIssuer {
        private static Log 			logger							= LogFactory.getLog(DOMPolicyIssuer.class);
        private static Identifier	identifierCategoryPolicyIssuer 	= new IdentifierImpl("urn:att:names:tc:xacml:3.0:policy-issuer");
        
        protected DOMPolicyIssuer() {
                super();
        }
        
        /**
         * Creates a new <code>DOMPolicyIssuer</code> by parsing the given <code>Node</code> representing a XACML PolicyIssuer element.
         * 
         * @param nodePolicyIssuer the <code>Node</code> representing the PolicyIssuer element
         * @return the new <code>DOMPolicyIssuer</code> parsed from the given <code>Node</code>
         * @throws DOMStructureException if the conversion is not possible
         */
        public static PolicyIssuer newInstance(Node nodePolicyIssuer) throws DOMStructureException {
                Element elementPolicyIssuer		= DOMUtil.getElement(nodePolicyIssuer);
                boolean bLenient				= DOMProperties.isLenient();
                
                DOMPolicyIssuer domPolicyIssuer	= new DOMPolicyIssuer();
                
                try {
                        NodeList children	= elementPolicyIssuer.getChildNodes();
                        int numChildren;
                        if (children != null && (numChildren = children.getLength()) > 0) {
                                for (int i = 0 ; i < numChildren ; i++) {
                                        Node child	= children.item(i);
                                        if (DOMUtil.isElement(child)) {
                                                if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                                                        String childName	= child.getLocalName();
                                                        if (XACML3.ELEMENT_CONTENT.equals(childName)) {
                                                                if (domPolicyIssuer.getContent() != null && !bLenient) {
                                                                        throw DOMUtil.newUnexpectedElementException(child, nodePolicyIssuer);
                                                                }
                                                                domPolicyIssuer.setContent(child);
                                                        } else if (XACML3.ELEMENT_ATTRIBUTE.equals(childName)) {
                                                                domPolicyIssuer.add(DOMAttribute.newInstance(identifierCategoryPolicyIssuer, child));
                                                        } else if (!bLenient) {
                                                                throw DOMUtil.newUnexpectedElementException(child, nodePolicyIssuer);
                                                        }
                                                }
                                        }
                                }
                        }
                } catch (DOMStructureException ex) {
                        domPolicyIssuer.setStatus(StdStatusCode.STATUS_CODE_SYNTAX_ERROR, ex.getMessage());
                        if (DOMProperties.throwsExceptions()) {
                                throw ex;
                        }
                }
                
                return domPolicyIssuer;		
        }
        
        public static boolean repair(Node nodePolicyIssuer) throws DOMStructureException {
                Element elementPolicyIssuer		= DOMUtil.getElement(nodePolicyIssuer);
                boolean result					= false;
                
                boolean sawContent				= false;
                NodeList children	= elementPolicyIssuer.getChildNodes();
                int numChildren;
                if (children != null && (numChildren = children.getLength()) > 0) {
                        for (int i = 0 ; i < numChildren ; i++) {
                                Node child	= children.item(i);
                                if (DOMUtil.isElement(child)) {
                                        if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                                                String childName	= child.getLocalName();
                                                if (XACML3.ELEMENT_CONTENT.equals(childName)) {
                                                        if (sawContent) {
                                                                logger.warn("Unexpected element " + child.getNodeName());
                                                                elementPolicyIssuer.removeChild(child);
                                                                result	= true;
                                                        } else {
                                                                sawContent	= true;
                                                        }
                                                } else if (XACML3.ELEMENT_ATTRIBUTE.equals(childName)) {
                                                        result	= DOMAttribute.repair(child) || result;
                                                } else {
                                                        logger.warn("Unexpected element " + child.getNodeName());
                                                        elementPolicyIssuer.removeChild(child);
                                                        result	= true;
                                                }
                                        }
                                }
                        }
                }
                
                return result;
        }

}
