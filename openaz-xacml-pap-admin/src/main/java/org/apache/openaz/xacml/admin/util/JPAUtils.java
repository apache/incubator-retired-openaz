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

package org.apache.openaz.xacml.admin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeSelectorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.admin.XacmlAdminUI;
import org.apache.openaz.xacml.admin.jpa.Attribute;
import org.apache.openaz.xacml.admin.jpa.Category;
import org.apache.openaz.xacml.admin.jpa.ConstraintType;
import org.apache.openaz.xacml.admin.jpa.Datatype;
import org.apache.openaz.xacml.admin.jpa.FunctionDefinition;
import org.apache.openaz.xacml.admin.jpa.Obadvice;
import org.apache.openaz.xacml.admin.jpa.PIPType;
import org.apache.openaz.xacml.admin.jpa.PolicyAlgorithms;
import org.apache.openaz.xacml.admin.jpa.RuleAlgorithms;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.UI;

public class JPAUtils {
	private static Log logger	= LogFactory.getLog(JPAUtils.class);
	
	private static final Object mapAccess = new Object();
	private static Map<Datatype, List<FunctionDefinition>> mapDatatype2Function = null;
	private static Map<String, FunctionDefinition> mapID2Function = null;
	private static final Object booleanAccess = new Object();
	private static Datatype booleanDatatype = null;
	
	public static Datatype getBooleanDatatype() {
		synchronized(booleanAccess) {
			if (booleanDatatype == null) {
				booleanDatatype = JPAUtils.findDatatype(XACML3.ID_DATATYPE_BOOLEAN);
			}
			return booleanDatatype;
		}
	}
	
	public static AttributeDesignatorType	createDesignator(Attribute attribute) {
		AttributeDesignatorType designator = new AttributeDesignatorType();
		designator.setAttributeId(attribute.getXacmlId());
		if (attribute.getCategoryBean() != null) {
			designator.setCategory(attribute.getCategoryBean().getXacmlId());
		} else {
			logger.warn("No category bean");
		}
		if (attribute.getDatatypeBean() != null) {
			designator.setDataType(attribute.getDatatypeBean().getXacmlId());
		} else {
			logger.warn("No datatype bean");
		}
		designator.setIssuer(attribute.getIssuer());
		designator.setMustBePresent(attribute.isMustBePresent());
		return designator;
	}
		
	public static AttributeSelectorType	createSelector(Attribute attribute) {
		AttributeSelectorType selector = new AttributeSelectorType();
		selector.setContextSelectorId(attribute.getXacmlId());
		selector.setPath(attribute.getSelectorPath());
		if (attribute.getCategoryBean() != null) {
			selector.setCategory(attribute.getCategoryBean().getXacmlId());
		} else {
			logger.warn("No category bean");
		}
		if (attribute.getDatatypeBean() != null) {
			selector.setDataType(attribute.getDatatypeBean().getXacmlId());
		} else {
			logger.warn("No datatype bean");
		}
		selector.setMustBePresent(attribute.isMustBePresent());
		return selector;
	}
	
	public static Attribute createAttribute(AttributeDesignatorType designator) {
		Attribute attribute = new Attribute();
		attribute.setCategoryBean(JPAUtils.findCategory(designator.getCategory()));
		attribute.setDatatypeBean(JPAUtils.findDatatype(designator.getDataType()));
		attribute.setXacmlId(designator.getAttributeId());
		attribute.setIssuer(designator.getIssuer());
		attribute.setIsDesignator(true);
		return attribute;
	}
	
	public static Attribute createAttribute(AttributeSelectorType selector) {
		Attribute attribute = new Attribute();
		attribute.setCategoryBean(JPAUtils.findCategory(selector.getCategory()));
		attribute.setDatatypeBean(JPAUtils.findDatatype(selector.getDataType()));
		attribute.setXacmlId(selector.getContextSelectorId());
		attribute.setSelectorPath(selector.getPath());
		attribute.setIsDesignator(false);
		return attribute;
	}
	
	public static Attribute findAttribute(Attribute attribute) {
		return JPAUtils.findAttribute(attribute.getCategoryBean(), attribute.getDatatypeBean(), attribute.getXacmlId());
	}
		
	public static Attribute findAttribute(Category category, Datatype datatype, String attributeID) {
		if (category == null || datatype == null | attributeID == null) {
			return null;
		}
		return JPAUtils.findAttribute(category.getXacmlId(), datatype.getXacmlId(), attributeID);
	}
	
	public static Attribute findAttribute(String category, String dataType, String attributeId) {
		if (category == null || dataType == null | attributeId == null) {
			return null;
		}
		JPAContainer<Attribute> attributes = ((XacmlAdminUI)UI.getCurrent()).getAttributes();
		for (Object id : attributes.getItemIds()) {
			Attribute a = attributes.getItem(id).getEntity();
			if (a.getCategoryBean().getXacmlId().equals(category) &&
						a.getDatatypeBean().getXacmlId().equals(dataType) &&
						a.getXacmlId().equals(attributeId)) {
				return a;
			}
		}
		return null;
	}

	public static Category	findCategory(Identifier cat) {
		JPAContainer<Category> categories = ((XacmlAdminUI)UI.getCurrent()).getCategories();
		for (Object id : categories.getItemIds()) {
			Category c = categories.getItem(id).getEntity();
			if (c.getIdentifer().equals(cat)) {
				return c;
			}
		}
		return null;
	}
	
	public static Category	findCategory(String cat) {
		JPAContainer<Category> categories = ((XacmlAdminUI)UI.getCurrent()).getCategories();
		for (Object id : categories.getItemIds()) {
			Category c = categories.getItem(id).getEntity();
			if (c.getIdentifer().stringValue().equals(cat)) {
				return c;
			}
		}
		return null;
	}
	
	public static Datatype	findDatatype(Identifier dt) {
		JPAContainer<Datatype> datatypes =  ((XacmlAdminUI)UI.getCurrent()).getDatatypes();
		
		for (Object id : datatypes.getItemIds()) {
			Datatype d = datatypes.getItem(id).getEntity();
			if (d.getIdentifer().equals(dt)) {
				return d;
			}
		}		
		return null;
	}
	
	public static Datatype	findDatatype(String dt) {
		JPAContainer<Datatype> datatypes =  ((XacmlAdminUI)UI.getCurrent()).getDatatypes();
		
		for (Object id : datatypes.getItemIds()) {
			Datatype d = datatypes.getItem(id).getEntity();
			if (d.getIdentifer().stringValue().equals(dt)) {
				return d;
			}
		}		
		return null;
	}
	
	public static Datatype	findDatatype(int datatypeId) {
		JPAContainer<Datatype> datatypes =  ((XacmlAdminUI)UI.getCurrent()).getDatatypes();
		
		for (Object id : datatypes.getItemIds()) {
			Datatype d = datatypes.getItem(id).getEntity();
			if (d.getId() == datatypeId) {
				return d;
			}
		}		
		return null;
	}
	
	public static ConstraintType	findConstraintType(String type) {
		JPAContainer<ConstraintType> types = ((XacmlAdminUI)UI.getCurrent()).getConstraintTypes();
		for (Object id : types.getItemIds()) {
			ConstraintType value = types.getItem(id).getEntity();
			if (value.getConstraintType().equals(type)) {
				return value;
			}
		}
		return null;
	}

	public static FunctionDefinition findFunction(String functionId) {
		if (functionId == null) {
			throw new IllegalArgumentException("Cannot find a null function id");
		}
		JPAContainer<FunctionDefinition> functions = ((XacmlAdminUI)UI.getCurrent()).getFunctionDefinitions();
		for (Object id : functions.getItemIds()) {
			FunctionDefinition value = functions.getItem(id).getEntity();
			if (value.getXacmlid().equals(functionId)) {
				return value;
			}
		}
		return null;
	}
	
	public static PolicyAlgorithms findPolicyAlgorithm(String algorithm) {
		if (algorithm == null) {
			throw new IllegalArgumentException("Cannot find a null algorithm");
		}
		JPAContainer<PolicyAlgorithms> algorithms = ((XacmlAdminUI)UI.getCurrent()).getPolicyAlgorithms();
		for (Object id : algorithms.getItemIds()) {
			PolicyAlgorithms alg = algorithms.getItem(id).getEntity();
			if (alg.getXacmlId().equals(algorithm)) {
				return alg;
			}
		}
		return null;
	}
	
	public static RuleAlgorithms findRuleAlgorithm(String algorithm) {
		if (algorithm == null) {
			throw new IllegalArgumentException("Cannot find a null algorithm");
		}
		JPAContainer<RuleAlgorithms> algorithms = ((XacmlAdminUI)UI.getCurrent()).getRuleAlgorithms();
		for (Object id : algorithms.getItemIds()) {
			RuleAlgorithms alg = algorithms.getItem(id).getEntity();
			if (alg.getXacmlId().equals(algorithm)) {
				return alg;
			}
		}
		return null;
	}
	
	public static Obadvice findObligation(Identifier id, EffectType effect) {
		if (id == null) {
			return null;
		}
		return JPAUtils.findObligation(id.stringValue(), effect);
	}
	
	public static Obadvice findObligation(String id, EffectType effect) {
		JPAContainer<Obadvice> oa = ((XacmlAdminUI)UI.getCurrent()).getObadvice();
		for (Object oaID : oa.getItemIds()) {
			Obadvice obligation = oa.getItem(oaID).getEntity();
			if (obligation.getType().equals(Obadvice.OBLIGATION) && 
				obligation.getXacmlId().equals(id) &&
				obligation.getFulfillOn().equals((effect == EffectType.PERMIT ? Obadvice.EFFECT_PERMIT : Obadvice.EFFECT_DENY))) {
				return obligation;
			}
		}
		return null;
	}
	
	public static Obadvice findAdvice(Identifier id, EffectType effect) {
		if (id == null) {
			return null;
		}
		return JPAUtils.findAdvice(id.stringValue(), effect);
	}
	
	public static Obadvice findAdvice(String id, EffectType effect) {
		JPAContainer<Obadvice> oa = ((XacmlAdminUI)UI.getCurrent()).getObadvice();
		for (Object oaID : oa.getItemIds()) {
			Obadvice advice = oa.getItem(oaID).getEntity();
			if (advice.getType().equals(Obadvice.ADVICE) && 
				advice.getXacmlId().equals(id) &&
				advice.getFulfillOn().equals((effect == EffectType.PERMIT ? Obadvice.EFFECT_PERMIT : Obadvice.EFFECT_DENY))) {
				return advice;
			}
		}
		return null;
	}
	
	public static Attribute isStandardAttribute(Attribute attribute) {
		return JPAUtils.isStandardAttribute(attribute.getCategoryBean(), attribute.getDatatypeBean(), attribute.getXacmlId());
	}
	
	public static Attribute isStandardAttribute(Category categoryBean, Datatype datatypeBean, String xacmlId) {
		if (categoryBean == null || datatypeBean == null || xacmlId == null) {
			return null;
		}
		return JPAUtils.isStandardAttribute(categoryBean.getXacmlId(), datatypeBean.getXacmlId(), xacmlId);
	}

	public static Attribute	isStandardAttribute(String category, String datatype, String id) {
		if (category == null || datatype == null || id == null) {
			return null;
		}
		Category cat = JPAUtils.findCategory(category);
		if (cat == null) {
			return null;
		}
		Datatype dt = JPAUtils.findDatatype(datatype);
		if (dt == null) {
			return null;
		}
		Identifier identifier = null;
		Iterator<Identifier> iter = XACMLConstants.STANDARD_ATTRIBUTES.iterator();
		while (iter.hasNext()) {
			Identifier i = iter.next();
			if (i.stringValue().equals(id)) {
				identifier = i;
				break;
			}
		}
		if (identifier == null) {
			return null;
		}
		Attribute attribute = new Attribute();
		attribute.setCategoryBean(cat);
		attribute.setDatatypeBean(dt);
		attribute.setXacmlId(identifier.stringValue());
		return attribute;
	}
	
	/**
	 * Builds a map in memory of a functions return datatype to function definition. Useful in limiting the number
	 * of SQL calls to DB especially when we don't expect these to change much.
	 * 
	 * @return - A HashMap of Datatype JPA Container ID's to FunctionDefinition objects
	 */
	public static Map<Datatype, List<FunctionDefinition>>	getFunctionDatatypeMap() {
		
		synchronized(mapAccess) {
			if (mapDatatype2Function == null) {
				buildFunctionMaps();
			}
		}
		return mapDatatype2Function;
	}
	
	public static Map<String, FunctionDefinition> getFunctionIDMap() {
		synchronized(mapAccess) {
			if (mapID2Function == null) {
				buildFunctionMaps();
			}
		}
		return mapID2Function;
	}
	
	private static void buildFunctionMaps() {
		mapDatatype2Function = new HashMap<Datatype, List<FunctionDefinition>>();
		mapID2Function = new HashMap<String, FunctionDefinition>();
		JPAContainer<FunctionDefinition> functions = ((XacmlAdminUI)UI.getCurrent()).getFunctionDefinitions();
		for (Object id : functions.getItemIds()) {
			FunctionDefinition value = functions.getItem(id).getEntity();
			mapID2Function.put(value.getXacmlid(), value);
			if (mapDatatype2Function.containsKey(value.getDatatypeBean()) == false) {
				mapDatatype2Function.put(value.getDatatypeBean(), new ArrayList<FunctionDefinition>());
			}
			mapDatatype2Function.get(value.getDatatypeBean()).add(value);
		}
	}
	
	public static void dumpDatatype2FunctionMap() {
		if (logger.isDebugEnabled() == false) {
			return;
		}
		Map<Datatype, List<FunctionDefinition>> map = getFunctionDatatypeMap();
		for (Datatype dt : map.keySet()) {
			for (FunctionDefinition function: map.get(dt)) {
				logger.debug("Datatype: " + (dt != null ? dt.getId() : "null") + " " + function.getXacmlid() + " (" + (function.getDatatypeBean() != null ? function.getDatatypeBean().getId() : "null") + ")");
			}
		}
	}

	public static PIPType	getPIPType(String typeName) {
		for (Object id : ((XacmlAdminUI) UI.getCurrent()).getPIPTypes().getItemIds()) {
			PIPType type = ((XacmlAdminUI) UI.getCurrent()).getPIPTypes().getItem(id).getEntity();
			if (type.getType().equals(typeName)) {
				return type;
			}
		}
		return null;
	}

}
