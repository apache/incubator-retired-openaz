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

package org.apache.openaz.xacml.admin.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeSelectorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.FunctionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableDefinitionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableReferenceType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.util.XACMLPolicyScanner;
import org.apache.openaz.xacml.util.XACMLPolicyScanner.CallbackResult;
import com.google.gwt.thirdparty.guava.common.base.Splitter;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Table;

public class PolicyContainer extends ItemSetChangeNotifier implements Container.Hierarchical, Container.Ordered, Container.ItemSetChangeNotifier {
	private static final long serialVersionUID = 1L;
	private static Log logger	= LogFactory.getLog(PolicyContainer.class);
	private final PolicyContainer self = this;
	
    /**
     * String identifier of a file's "name" property.
     */
    public static String PROPERTY_NAME = "Name";

    /**
     * String identifier of a file's "id" property.
     */
    public static String PROPERTY_ID = "Id";

    /**
     * String identifier of a file's "short ID" property.
     */
    public static String PROPERTY_SHORTID = "ShortId";

    /**
     * String identifier of a file's "Algorithm" property.
     */
    public static String PROPERTY_ALGORITHM = "Algorithm";

    /**
     * String identifier of a file's "ShortAlgorithm" property.
     */
    public static String PROPERTY_SHORTALGORITHM = "ShortAlgorithm";

    /**
     * String identifier of a file's "Description" property.
     */
    public static String PROPERTY_DESCRIPTION = "Description";

    /**
     * String identifier of a file's "icon" property.
     */
    public static String PROPERTY_ICON = "Icon";

    /**
     * String identifier of a file's "Status" property.
     */
    public static String PROPERTY_STATUS = "Status";

    /**
     * String identifier of a file's "Attributes" property.
     */
    public static String PROPERTY_ATTRIBUTES = "Attributes";

    /**
     * List of the string identifiers for the available properties.
     */
    public static Collection<String> POLICY_PROPERTIES;

    private final static Method POLICYITEM_NAME;
    
    private final static Method POLICYITEM_ID;

    private final static Method POLICYITEM_SHORTID;

    private final static Method POLICYITEM_ALGORITHM;

    private final static Method POLICYITEM_SHORTALGORITHM;

    private final static Method POLICYITEM_DESCRIPTION;

    private final static Method POLICYITEM_ICON;
    
    private final static Method POLICYITEM_STATUS;
    
    private final static Method POLICYITEM_ATTRIBUTES;
    
    static {

    	POLICY_PROPERTIES = new ArrayList<String>();
    	POLICY_PROPERTIES.add(PROPERTY_NAME);
    	POLICY_PROPERTIES.add(PROPERTY_ID);
    	POLICY_PROPERTIES.add(PROPERTY_SHORTID);
    	POLICY_PROPERTIES.add(PROPERTY_ALGORITHM);
    	POLICY_PROPERTIES.add(PROPERTY_SHORTALGORITHM);
    	POLICY_PROPERTIES.add(PROPERTY_DESCRIPTION);
    	POLICY_PROPERTIES.add(PROPERTY_ICON);
    	POLICY_PROPERTIES.add(PROPERTY_STATUS);
    	POLICY_PROPERTIES.add(PROPERTY_ATTRIBUTES);
    	POLICY_PROPERTIES = Collections.unmodifiableCollection(POLICY_PROPERTIES);
        try {
        	POLICYITEM_NAME = PolicyItem.class.getMethod("getName", new Class[]{});
        	POLICYITEM_ID = PolicyItem.class.getMethod("getId", new Class[]{});
        	POLICYITEM_SHORTID = PolicyItem.class.getMethod("getShortId", new Class[]{});
        	POLICYITEM_ALGORITHM = PolicyItem.class.getMethod("getAlgorithm", new Class[] {});
        	POLICYITEM_SHORTALGORITHM = PolicyItem.class.getMethod("getShortAlgorithm", new Class[] {});
            POLICYITEM_DESCRIPTION = PolicyItem.class.getMethod("getDescription", new Class[] {});
            POLICYITEM_ICON = PolicyItem.class.getMethod("getIcon", new Class[] {});
            POLICYITEM_STATUS = PolicyItem.class.getMethod("getStatus", new Class[] {});
            POLICYITEM_ATTRIBUTES = PolicyItem.class.getMethod("getAttributes", new Class[] {});
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(
                    "Internal error finding methods in PolicyContainer");
        }
    }
    
    private final File file;
    private Object root;
    
    private Map<PolicySetType, PolicySetType> policySets = new HashMap<PolicySetType, PolicySetType>();
    private Map<PolicyType, PolicySetType> policies = new HashMap<PolicyType, PolicySetType>();
    private Map<RuleType, PolicyType> rules = new HashMap<RuleType, PolicyType>();
    private Map<TargetType, Object> targets = new HashMap<TargetType, Object>();
    private Map<AnyOfType, TargetType> anyofs = new HashMap<AnyOfType, TargetType>();
    private Map<AllOfType, AnyOfType> allofs = new HashMap<AllOfType, AnyOfType>();
    private Map<MatchType, AllOfType> matches = new HashMap<MatchType, AllOfType>();
    private Map<ObligationExpressionType, Object> obligations = new HashMap<ObligationExpressionType, Object>();
    private Map<AdviceExpressionType, Object> advice = new HashMap<AdviceExpressionType, Object>();
    private Map<ConditionType, RuleType> conditions = new HashMap<ConditionType, RuleType>();
    private Map<VariableDefinitionType, PolicyType> variables = new HashMap<VariableDefinitionType, PolicyType>();
        
	public PolicyContainer(File file) throws IOException {
		super();
		this.setContainer(this);
		this.file = file;
		this.readData();
		if (logger.isTraceEnabled()) {
			logger.trace("New Policy Container: " + this.file.getName());
		}
	}
	
	private void readData() throws IOException {
		Object data = null;
		try (InputStream is = Files.newInputStream(Paths.get(this.file.getAbsolutePath()))) {
			data = XACMLPolicyScanner.readPolicy(is);
		} catch (IOException e) {
			logger.error("Failed to load policy.");
		}
		XACMLPolicyScanner scanner = null;
		if (data instanceof PolicySetType) {
			this.root = data;
			scanner = new XACMLPolicyScanner((PolicySetType) this.root);
		} else if (data instanceof PolicyType) {
			this.root = data;
			scanner = new XACMLPolicyScanner((PolicyType) this.root);
		} else {
			if (data != null) {
				logger.error("invalid root object: " + data.getClass().getCanonicalName());
			} else {
				logger.error("could not parse the file");
			}
			throw new IOException("Invalid Xacml Policy File");
		}
		scanner.scan(new XACMLPolicyScanner.SimpleCallback() {
			
			@Override
			public CallbackResult onPreVisitRule(PolicyType parent, RuleType rule) {
				self.addRule(parent, rule, false);
				return CallbackResult.CONTINUE;
			}
			
			@Override
			public CallbackResult onPreVisitPolicySet(PolicySetType parent,
					PolicySetType policySet) {
				self.addPolicySet(parent, policySet, false);
				return CallbackResult.CONTINUE;
			}
			
			@Override
			public CallbackResult onPreVisitPolicy(PolicySetType parent,
					PolicyType policy) {
				self.addPolicy(parent, policy, false);
				return CallbackResult.CONTINUE;
			}
			
		});
	}
	
   private boolean	isObjectSupported(Object itemId) {
       if (!(itemId instanceof PolicySetType) &&
           	!(itemId instanceof PolicyType) &&
           	!(itemId instanceof RuleType) &&
           	!(itemId instanceof TargetType) &&
           	!(itemId instanceof ObligationExpressionType) &&
           	!(itemId instanceof AdviceExpressionType) &&
           	!(itemId instanceof AnyOfType) &&
           	!(itemId instanceof AllOfType) &&
           	!(itemId instanceof MatchType) &&
           	!(itemId instanceof ConditionType) &&
           	!(itemId instanceof VariableDefinitionType)) {
               return false;
           }
	   return true;
   }

	@Override
	public Item getItem(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getItem: " + itemId);
		}
        if (this.isObjectSupported(itemId) == false) {
            return null;
        }
		return new PolicyItem(itemId);
	}
	
	public Item updateItem(Object itemId) {
        if (this.isObjectSupported(itemId) == false) {
            return null;
        }

        this.fireItemSetChange();
        
        return new PolicyItem(itemId);
	}
	
	public Map<VariableDefinitionType, PolicyType>	getVariables() {
		return Collections.unmodifiableMap(this.variables);
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return POLICY_PROPERTIES;
	}

	@Override
	public Collection<?> getItemIds() {
		XACMLPolicyScanner scanner = null;
		final Collection<Object> items = new ArrayList<Object>();
		if (this.root instanceof PolicyType) {
			scanner = new XACMLPolicyScanner((PolicyType) this.root);
		} else if (this.root instanceof PolicySetType) {
			scanner = new XACMLPolicyScanner((PolicySetType) this.root);
		} else {
			return Collections.unmodifiableCollection(items);
		}
		
		scanner.scan(new XACMLPolicyScanner.SimpleCallback() {
			
			@Override
			public CallbackResult onPreVisitRule(PolicyType parent, RuleType rule) {
				items.add(rule);
				if (rule.getTarget() != null) {
					items.add(rule.getTarget());
				}
				if (rule.getCondition() != null) {
					items.add(rule.getCondition());
				}
				if (rule.getObligationExpressions() != null) {
					items.addAll(rule.getObligationExpressions().getObligationExpression());
				}
				if (rule.getAdviceExpressions() != null) {
					items.addAll(rule.getAdviceExpressions().getAdviceExpression());
				}
				return CallbackResult.CONTINUE;
			}
			
			@Override
			public CallbackResult onPreVisitPolicySet(PolicySetType parent, PolicySetType policySet) {
				items.add(policySet);
				if (policySet.getTarget() != null) {
					items.add(policySet.getTarget());
				}
				if (policySet.getObligationExpressions() != null) {
					items.addAll(policySet.getObligationExpressions().getObligationExpression());
				}
				if (policySet.getAdviceExpressions() != null) {
					items.addAll(policySet.getAdviceExpressions().getAdviceExpression());
				}
				return CallbackResult.CONTINUE;
			}
			
			@Override
			public CallbackResult onPreVisitPolicy(PolicySetType parent, PolicyType policy) {
				items.add(policy);
				if (policy.getTarget() != null) {
					items.add(policy.getTarget());
				}
				if (policy.getObligationExpressions() != null) {
					items.addAll(policy.getObligationExpressions().getObligationExpression());
				}
				if (policy.getAdviceExpressions() != null) {
					items.addAll(policy.getAdviceExpressions().getAdviceExpression());
				}
				return CallbackResult.CONTINUE;
			}
			
		});
		if (logger.isTraceEnabled()) {
			logger.trace("getItemIds: (" + items.size() + "):" + items);
		}
		return Collections.unmodifiableCollection(items);		
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
        if (this.isObjectSupported(itemId) == false) {
            return null;
        }

        if (propertyId.equals(PROPERTY_NAME)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PolicyItem(itemId), POLICYITEM_NAME, null);
        }

        if (propertyId.equals(PROPERTY_ID)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PolicyItem(itemId), POLICYITEM_ID, null);
        }

        if (propertyId.equals(PROPERTY_SHORTID)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PolicyItem(itemId), POLICYITEM_SHORTID, null);
        }

        if (propertyId.equals(PROPERTY_ICON)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PolicyItem(itemId), POLICYITEM_ICON, null);
        }

        if (propertyId.equals(PROPERTY_ALGORITHM)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PolicyItem(itemId), POLICYITEM_ALGORITHM, null);
        }

        if (propertyId.equals(PROPERTY_SHORTALGORITHM)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PolicyItem(itemId), POLICYITEM_SHORTALGORITHM, null);
        }

         if (propertyId.equals(PROPERTY_DESCRIPTION)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PolicyItem(itemId), POLICYITEM_DESCRIPTION, null);
        }

        if (propertyId.equals(PROPERTY_STATUS)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PolicyItem(itemId), POLICYITEM_STATUS, null);
        }

        if (propertyId.equals(PROPERTY_ATTRIBUTES)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PolicyItem(itemId), POLICYITEM_ATTRIBUTES, null);
        }

        return null;
	}

	@Override
	public Class<?> getType(Object propertyId) {
		
        if (propertyId.equals(PROPERTY_NAME)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_ID)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_SHORTID)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_ICON)) {
            return Resource.class;
        }
        if (propertyId.equals(PROPERTY_DESCRIPTION)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_ALGORITHM)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_SHORTALGORITHM)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_STATUS)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_ATTRIBUTES)) {
            return Table.class;
        }
		return null;
	}

	@Override
	public int size() {
		int size = 0;
		size += this.policySets.size();
		size += this.policies.size();
		size += this.rules.size();
		size += this.targets.size();
		size += this.obligations.size();
		size += this.advice.size();
		size += this.anyofs.size();
		size += this.allofs.size();
		size += this.matches.size();
		size += this.conditions.size();
		size += this.variables.size();
		
		return size;
	}

	@Override
	public boolean containsId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("containsId: " + itemId);
		}
		if (itemId instanceof PolicySetType) {
			if (this.root instanceof PolicySetType && ((PolicySetType) itemId).getPolicySetId().equals(((PolicySetType) this.root).getPolicySetId())) {
				return true;
			}
			return this.policySets.containsKey(itemId);
		}
		if (itemId instanceof PolicyType) {
			if (this.root instanceof PolicyType && ((PolicyType) itemId).getPolicyId().equals(((PolicyType) this.root).getPolicyId())) {
				return true;
			}
			return this.policies.containsKey(itemId);
		}
		if (itemId instanceof RuleType) {
			return this.rules.containsKey(itemId);
		}
		if (itemId instanceof TargetType) {
			return this.targets.containsKey(itemId);
		}
		if (itemId instanceof ObligationExpressionType) {
			return this.obligations.containsKey(itemId);
		}
		if (itemId instanceof AdviceExpressionType) {
			return this.advice.containsKey(itemId);
		}
		if (itemId instanceof AnyOfType) {
			return this.anyofs.containsKey(itemId);
		}
		if (itemId instanceof AllOfType) {
			return this.allofs.containsKey(itemId);
		}
		if (itemId instanceof MatchType) {
			return this.matches.containsKey(itemId);
		}
		if (itemId instanceof ConditionType) {
			return this.conditions.containsKey(itemId);
		}
		if (itemId instanceof VariableDefinitionType) {
			return this.variables.containsKey(itemId);
		}
		return false;
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Please use the addItem(Object, Object) method instead.");
	}

	/**
	 * Add's the new Policy object under the parent. This method appends the item to the end
	 * of the parent's list if applicable. If you want to add an item within a list, use the
	 * addItemAfter() method instead.
	 * 
	 * @param itemId
	 * @param parent
	 * @return
	 * @throws UnsupportedOperationException
	 */
	public Item addItem(Object itemId, Object parent) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("addItem: " + itemId);
		}
		if (itemId instanceof PolicySetType && parent instanceof PolicySetType) {
			this.addPolicySet((PolicySetType) parent, (PolicySetType) itemId, true);
		} else if (itemId instanceof PolicyType && parent instanceof PolicySetType) {
			this.addPolicy((PolicySetType) parent, (PolicyType) itemId, true);
		} else if (itemId instanceof RuleType && parent instanceof PolicyType) {
			this.addRule((PolicyType) parent, (RuleType) itemId, true);
		} else if (itemId instanceof TargetType && 
				(parent instanceof PolicyType || 
				parent instanceof PolicySetType || 
				parent instanceof RuleType)) {
			this.addTarget(parent, (TargetType) itemId, true);
		} else if (itemId instanceof ObligationExpressionType ||
					itemId instanceof ObligationExpressionsType) {
			if (parent instanceof PolicyType || 
				parent instanceof PolicySetType || 
				parent instanceof RuleType ||
				parent instanceof ObligationExpressionsType) {
				if (itemId instanceof ObligationExpressionType) {
					this.addObligation(parent, (ObligationExpressionType) itemId, true);
				} else {
					this.addObligations(parent, (ObligationExpressionsType) itemId, true);
				}
			}
		} else if (itemId instanceof AdviceExpressionType ||
					itemId instanceof AdviceExpressionsType) {
			if (parent instanceof PolicyType || 
				parent instanceof PolicySetType || 
				parent instanceof RuleType ||
				parent instanceof AdviceExpressionsType) {
				if (itemId instanceof AdviceExpressionType) {
					this.addAdvice(parent, (AdviceExpressionType) itemId, true);
				} else {
					this.addAdvice(parent, (AdviceExpressionsType) itemId, true);
				}
			}
		} else if (itemId instanceof AnyOfType && parent instanceof TargetType) {
			this.addAnyOf((TargetType) parent, (AnyOfType) itemId, true);
		} else if (itemId instanceof AllOfType && parent instanceof AnyOfType) {
			this.addAllOf((AnyOfType) parent, (AllOfType) itemId, true);
		} else if (itemId instanceof MatchType && parent instanceof AllOfType) {
			this.addMatch((AllOfType) parent, (MatchType) itemId, true);
		} else if (itemId instanceof ConditionType && parent instanceof RuleType) {
			this.addCondition((RuleType) parent, (ConditionType) itemId, true);
		} else if (itemId instanceof VariableDefinitionType && parent instanceof PolicyType) {
			this.addVariable((PolicyType) parent, (VariableDefinitionType) itemId, true);
		} else {
			throw new UnsupportedOperationException("Unknown itemid or parent type: " + itemId.getClass().getCanonicalName() + " " + parent.getClass().getCanonicalName());
		}
		//
		// Fire update event
		//
		this.fireItemSetChange();
		//
		// Create a new item
		//
		return new PolicyItem(itemId);
	}
	
	private void addVariable(PolicyType policy, VariableDefinitionType variable, boolean add) {
		if (add) {
			if (policy == null) {
				throw new NullPointerException();
			}
			policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(variable);
		}
		this.variables.put(variable, policy);
	}

	private void addCondition(RuleType rule, ConditionType condition, boolean add) {
		if (add) {
			if (rule == null) {
				throw new NullPointerException();
			}
			rule.setCondition(condition);
		}
		this.conditions.put(condition, rule);
	}

	private void addPolicySet(PolicySetType parent, PolicySetType policySet, boolean add) {
		if (policySet == null) {
			throw new NullPointerException();
		}
		if (parent == null && this.isRoot(policySet) == false) {
			logger.warn("adding a non-root policy set with no parent");
			return;
		}
		if (add) {
			if (parent == null) {
				throw new NullPointerException();
			}
			parent.getPolicySetOrPolicyOrPolicySetIdReference().add(new ObjectFactory().createPolicySet(policySet));
		}
		this.policySets.put(policySet, parent);
		this.addTarget(policySet, policySet.getTarget(), false);
		this.addObligations(policySet, policySet.getObligationExpressions(), false);
		this.addAdvice(policySet, policySet.getAdviceExpressions(), false);
	}
	
	private void addPolicy(PolicySetType parent, PolicyType policy, boolean add) {
		if (policy == null) {
			throw new NullPointerException();
		}
		if (parent == null && this.isRoot(policy) == false) {
			logger.warn("adding a non-root policy with no parent");
			return;
		}
		if (add) {
			if (parent == null) {
				throw new NullPointerException();
			}
			parent.getPolicySetOrPolicyOrPolicySetIdReference().add(new ObjectFactory().createPolicy(policy));
		}
		this.policies.put(policy, parent);
		this.addTarget(policy, policy.getTarget(), false);
		this.addObligations(policy, policy.getObligationExpressions(), false);
		this.addAdvice(policy, policy.getAdviceExpressions(), false);
		for (Object obj : policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()) {
			if (obj instanceof VariableDefinitionType) {
				this.addVariable(policy, (VariableDefinitionType) obj, false);
			}
		}
	}
	
	private void addRule(PolicyType parent, RuleType rule, boolean add) {
		if (rule == null) {
			throw new NullPointerException("Rule can't be null");
		}
		if (parent == null) {
			throw new NullPointerException("Parent policy can't be null");
		}
		if (this.isRoot(parent) == false && this.policies.get(parent) == null) {
			logger.warn("Could NOT find parent as root or in map: " + parent);
			return;
		}
		if (add) {
			parent.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().add(rule);
		}
		this.rules.put(rule, parent);
		this.addTarget(rule, rule.getTarget(), false);
		this.addObligations(rule, rule.getObligationExpressions(), false);
		this.addAdvice(rule, rule.getAdviceExpressions(), false);
		this.addCondition(rule, rule.getCondition(), add);
	}
	
	private void addTarget(Object parent, TargetType target, boolean add) {
		if (target == null) {
			logger.error("TargetType is NULL");
			return;
		}
		if (add) {
			if (parent == null) {
				throw new NullPointerException();
			}
			if (parent instanceof PolicySetType) {
				((PolicySetType) parent).setTarget(target);
			} else if (parent instanceof PolicyType) {
				((PolicyType) parent).setTarget(target);
			} else if (parent instanceof RuleType) {
				((RuleType) parent).setTarget(target);
			} else {
				logger.error("Adding target to unknown class: " + parent.getClass().getCanonicalName());
			}
		}
		this.targets.put(target, parent);
		this.addAnyOfs(target);
	}
	
	private void addAnyOfs(TargetType target) {
		for (AnyOfType anyof : target.getAnyOf()) {
			this.anyofs.put(anyof, target);
			this.addAllOfs(anyof);
		}
	}
	
	private void addAnyOf(TargetType target, AnyOfType anyof, boolean add) {
		if (add) {
			target.getAnyOf().add(anyof);
		}
		this.anyofs.put(anyof, target);
	}
	
	private void addAllOfs(AnyOfType anyof) {
		for (AllOfType allof : anyof.getAllOf()) {
			this.allofs.put(allof, anyof);
			this.addMatches(allof);
		}
	}
	
	private void addAllOf(AnyOfType anyof, AllOfType allof, boolean add) {
		if (add) {
			anyof.getAllOf().add(allof);
		}
		this.allofs.put(allof, anyof);
	}
	
	private void addMatches(AllOfType allofs) {
		for (MatchType match : allofs.getMatch()) {
			this.matches.put(match, allofs);
		}
	}

	private void addMatch(AllOfType allofs, MatchType match, boolean add) {
		if (add) {
			allofs.getMatch().add(match);
		}
		this.matches.put(match, allofs);
	}

	private void addObligations(Object parent, ObligationExpressionsType expressions, boolean bAdd) {
		if (expressions == null || expressions.getObligationExpression() == null) {
			return;
		}
		if (bAdd) {
			if (parent instanceof PolicySetType) {
				((PolicySetType) parent).setObligationExpressions(expressions);
			} else if (parent instanceof PolicyType) {
				((PolicyType) parent).setObligationExpressions(expressions);
			} else if (parent instanceof RuleType) {
				((RuleType) parent).setObligationExpressions(expressions);
			}
		}
		for (ObligationExpressionType expression : expressions.getObligationExpression()) {
			this.obligations.put(expression, parent);
		}
	}
	
	private void addObligation(Object parent, ObligationExpressionType expression, boolean bAdd) {
		ObligationExpressionsType expressions = null;
		if (parent instanceof PolicySetType) {
			expressions = ((PolicySetType) parent).getObligationExpressions();
			if (expressions == null) {
				expressions = new ObligationExpressionsType();
				((PolicySetType) parent).setObligationExpressions(expressions);
			}
		} else if (parent instanceof PolicyType) {
			expressions = ((PolicyType) parent).getObligationExpressions();
			if (expressions == null) {
				expressions = new ObligationExpressionsType();
				((PolicyType) parent).setObligationExpressions(expressions);
			}
		} else if (parent instanceof RuleType) {
			expressions = ((RuleType) parent).getObligationExpressions();
			if (expressions == null) {
				expressions = new ObligationExpressionsType();
				((RuleType) parent).setObligationExpressions(expressions);
			}
		} else if (parent instanceof ObligationExpressionsType) {
			expressions = (ObligationExpressionsType) parent;
			if (bAdd) {
				expressions.getObligationExpression().add(expression);
			}
			parent = this.getParent(expressions);
			this.obligations.put(expression, parent);
			return;
		}
		if (bAdd) {
			expressions.getObligationExpression().add(expression);
		}
		this.obligations.put(expression, parent);
	}
	
	private void addAdvice(Object parent, AdviceExpressionsType expressions, boolean bAdd) {
		if (expressions == null || expressions.getAdviceExpression() == null) {
			return;
		}
		if (bAdd) {
			if (parent instanceof PolicySetType) {
				((PolicySetType) parent).setAdviceExpressions(expressions);
			} else if (parent instanceof PolicyType) {
				((PolicyType) parent).setAdviceExpressions(expressions);
			} else if (parent instanceof RuleType) {
				((RuleType) parent).setAdviceExpressions(expressions);
			}
		}
		for (AdviceExpressionType expression : expressions.getAdviceExpression()) {
			this.advice.put(expression, parent);
		}
	}
	
	private void addAdvice(Object parent, AdviceExpressionType expression, boolean bAdd) {
		AdviceExpressionsType expressions = null;
		if (parent instanceof PolicySetType) {
			expressions = ((PolicySetType) parent).getAdviceExpressions();
			if (expressions == null) {
				expressions = new AdviceExpressionsType();
				((PolicySetType) parent).setAdviceExpressions(expressions);
			}
		} else if (parent instanceof PolicyType) {
			expressions = ((PolicyType) parent).getAdviceExpressions();
			if (expressions == null) {
				expressions = new AdviceExpressionsType();
				((PolicyType) parent).setAdviceExpressions(expressions);
			}
		} else if (parent instanceof RuleType) {
			expressions = ((RuleType) parent).getAdviceExpressions();
			if (expressions == null) {
				expressions = new AdviceExpressionsType();
				((RuleType) parent).setAdviceExpressions(expressions);
			}
		} else if (parent instanceof AdviceExpressionsType) {
			expressions = (AdviceExpressionsType) parent;
			if (bAdd) {
				expressions.getAdviceExpression().add(expression);
			}
			parent = this.getParent(expressions);
			this.advice.put(expression, parent);
			return;
		}
		if (bAdd) {
			expressions.getAdviceExpression().add(expression);
		}
		this.advice.put(expression, parent);
	}
	
	@Override
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Policy Container cannot add an unknown item.");
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type,
			Object defaultValue) throws UnsupportedOperationException {
		return false;
	}

	@Override
	public boolean removeContainerProperty(Object propertyId)
			throws UnsupportedOperationException {
		return false;
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Policy Container cannot remove all items. You must have a base Policy or Policy Set.");
	}

	@Override
	public Collection<?> getChildren(Object itemId) {
		final Collection<Object> items = new ArrayList<Object>();
		
		if (itemId instanceof PolicySetType) {
			PolicySetType policySet = (PolicySetType) itemId;
			if (policySet.getTarget() != null) {
				items.add(policySet.getTarget());
			}
			if (policySet.getObligationExpressions() != null) {
				items.addAll(policySet.getObligationExpressions().getObligationExpression());
			}
			if (policySet.getAdviceExpressions() != null) {
				items.addAll(policySet.getAdviceExpressions().getAdviceExpression());
			}
			List<JAXBElement<?>> children = policySet.getPolicySetOrPolicyOrPolicySetIdReference();
			for (JAXBElement<?> element : children) {
				if (element.getName().getLocalPart().equals("PolicySet")) {
					items.add(element.getValue());
				} else if (element.getName().getLocalPart().equals("Policy")) {
					items.add(element.getValue());
				}
			}
		} else if (itemId instanceof PolicyType) {
			PolicyType policy = (PolicyType) itemId;
			if (policy.getTarget() != null) {
				items.add(policy.getTarget());
			}
			List<Object> objs = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
			for (Object obj : objs) {
				if (obj instanceof VariableDefinitionType) {
					items.add(obj);
				} else if (obj instanceof RuleType) {
					items.add(obj);
				}
			}
			if (policy.getObligationExpressions() != null) {
				items.addAll(policy.getObligationExpressions().getObligationExpression());
			}
			if (policy.getAdviceExpressions() != null) {
				items.addAll(policy.getAdviceExpressions().getAdviceExpression());
			}
		} else if (itemId instanceof RuleType) {
			RuleType rule = (RuleType) itemId;
			if (rule.getTarget() != null) {
				items.add(rule.getTarget());
			}
			if (rule.getCondition() != null) {
				items.add(rule.getCondition());
			}
			if (rule.getObligationExpressions() != null) {
				items.addAll(((RuleType) itemId).getObligationExpressions().getObligationExpression());
			}
			if (rule.getAdviceExpressions() != null) {
				items.addAll(rule.getAdviceExpressions().getAdviceExpression());
			}
		} else if (itemId instanceof TargetType) {
			for (AnyOfType anyof : ((TargetType) itemId).getAnyOf()) {
				items.add(anyof);
			}
		} else if (itemId instanceof AnyOfType) {
			for (AllOfType allof : ((AnyOfType) itemId).getAllOf()) {
				items.add(allof);
			}
		} else if (itemId instanceof AllOfType) {
			for (MatchType match : ((AllOfType) itemId).getMatch()) {
				items.add(match);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getChildren: " + itemId + "(" + items.size() + ") " + items);
		}
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public Object getParent(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getParent: " + itemId);
		}
		assert itemId != null;
		if (itemId == null) {
			logger.fatal("getParent called with NULL object");
			return null;
		}
		if (itemId.equals(this.root)) {
			if (logger.isTraceEnabled()) {
				logger.trace("getParent is the root");
			}
			return null;
		}
		if (itemId instanceof PolicySetType) {
			return this.policySets.get(itemId);
		}
		if (itemId instanceof PolicyType) {
			return this.policies.get(itemId);
		}
		if (itemId instanceof RuleType) {
			return this.rules.get(itemId);
		}
		if (itemId instanceof TargetType) {
			return this.targets.get(itemId);
		}
		if (itemId instanceof ObligationExpressionType) {
			return this.obligations.get(itemId);
		}
		if (itemId instanceof AdviceExpressionType) {
			return this.advice.get(itemId);
		}
		if (itemId instanceof AllOfType) {
			return this.allofs.get(itemId);
		}
		if (itemId instanceof AnyOfType) {
			return this.anyofs.get(itemId);
		}
		if (itemId instanceof MatchType) {
			return this.matches.get(itemId);
		}
		if (itemId instanceof ConditionType) {
			return this.conditions.get(itemId);
		}
		if (itemId instanceof VariableDefinitionType) {
			return this.variables.get(itemId);
		}
		return null;
	}

	@Override
	public Collection<?> rootItemIds() {
		final Collection<Object> items = new ArrayList<Object>();
		items.add(this.root);
		if (logger.isTraceEnabled()) {
			logger.trace("rootItemIds: " + items);
		}
		return Collections.unmodifiableCollection(items);
	}
	
	@Override
	public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
		boolean result = this.setItemParent(itemId, newParentId);
		if (result == true) {
			if (logger.isTraceEnabled()) {
				logger.trace("setParent: " + itemId + " " + newParentId + " succeeded.");
			}
			this.fireItemSetChange();
		}
		return result;
	}

	protected boolean setItemParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("setItemParent: " + itemId + " " + newParentId);
		}
		if (newParentId instanceof PolicySetType) {
			return this.moveItemToPolicySet(itemId, (PolicySetType) newParentId);
		}
		if (newParentId instanceof PolicyType) {
			return this.moveItemToPolicy(itemId, (PolicyType) newParentId);
		}
		if (newParentId instanceof RuleType) {
			return this.moveItemToRule(itemId, (RuleType) newParentId);
		}
		if (newParentId instanceof TargetType) {
			return this.moveItemToTarget(itemId, (TargetType) newParentId);
		}
		if (newParentId instanceof AnyOfType) {
			return this.moveItemToAnyOf(itemId, (AnyOfType) newParentId);
		}
		if (newParentId instanceof AllOfType) {
			return this.moveItemToAllOf(itemId, (AllOfType) newParentId);
		}
		return false;
	}
	
	protected boolean moveItemToPolicySet(Object itemId, PolicySetType policySet) {
		if (itemId instanceof PolicySetType) {
			
			if (this.doRemoveItem(itemId)) {
				this.addPolicySet(policySet, (PolicySetType) itemId, true);
				return true;
			}
			return false;
		} else if (itemId instanceof PolicyType) {
			if (this.doRemoveItem(itemId)) {
				this.addPolicy(policySet, (PolicyType) itemId, true);
				return true;
			}
			return false;
		} else if (itemId instanceof TargetType) {
			if (this.doRemoveItem(itemId)) {
				this.addTarget(policySet, (TargetType) itemId, true);
				return true;
			}
			return false;
		} else if (itemId instanceof ObligationExpressionType) {
			if (this.doRemoveItem(itemId)) {
				this.addObligation(policySet, (ObligationExpressionType) itemId, true);
				return true;
			}
			return false;
		} else if (itemId instanceof AdviceExpressionType) {
			if (this.doRemoveItem(itemId)) {
				this.addAdvice(policySet, (AdviceExpressionType) itemId, true);
				return true;
			}
			return false;
		}
		logger.warn("Can't move this item to Policy Set: " + itemId.getClass().getCanonicalName());
		return false;
	}
	
	protected boolean moveItemToPolicy(Object itemId, PolicyType policy) {
		if (itemId instanceof RuleType) {
			if (this.doRemoveItem(itemId)) {
				this.addRule(policy, (RuleType) itemId, true);
				return true;
			}
			return false;
		} else if (itemId instanceof TargetType) {
			if (this.doRemoveItem(itemId)) {
				this.addTarget(policy, (TargetType) itemId, true);
				return true;
			}
			return false;
		} else if (itemId instanceof ObligationExpressionType) {
			if (this.doRemoveItem(itemId)) {
				this.addObligation(policy, (ObligationExpressionType) itemId, true);
				return true;
			}
			return false;
		} else if (itemId instanceof AdviceExpressionType) {
			if (this.doRemoveItem(itemId)) {
				this.addAdvice(policy, (AdviceExpressionType) itemId, true);
				return true;
			}
			return false;
		} else if (itemId instanceof VariableDefinitionType) {
			if (this.doRemoveItem(itemId)) {
				this.addVariable(policy, (VariableDefinitionType) itemId, true);
				return true;
			}
			return false;
		}
		logger.warn("Can't move this item to Policy: " + itemId.getClass().getCanonicalName());
		return false;
	}
	
	protected boolean moveItemToRule(Object itemId, RuleType rule) {
		if (itemId instanceof TargetType) {
			if (this.doRemoveItem(itemId)) {
				this.addTarget(rule, (TargetType) itemId, true);
				return true;
			}
			return false;
		} else if (itemId instanceof ObligationExpressionType) {
			if (this.doRemoveItem(itemId)) {
				this.addObligation(rule, (ObligationExpressionType) itemId, true);
				return true;
			}
			return false;
		} else if (itemId instanceof AdviceExpressionType) {
			if (this.doRemoveItem(itemId)) {
				this.addAdvice(rule, (AdviceExpressionsType) itemId, true);
				return true;
			}
			return false;
		} else if (itemId instanceof ConditionType) {
			if (this.doRemoveItem(itemId)) {
				this.addCondition(rule, (ConditionType) itemId, true);
				return true;
			}
			return false;
		}
		logger.warn("Can't move this item to Rule: " + itemId.getClass().getCanonicalName());
		return false;
	}
	
	protected boolean moveItemToTarget(Object itemId, TargetType target) {
		if (itemId instanceof AnyOfType) {
			if (this.doRemoveItem(itemId)) {
				this.addAnyOf(target, (AnyOfType) itemId, true);
				return true;
			}
			return false;
		}
		logger.warn("Can't move this item to target: " + itemId.getClass().getCanonicalName());
		return false;
	}
	
	protected boolean moveItemToAnyOf(Object itemId, AnyOfType anyOf) {
		if (itemId instanceof AllOfType) {
			if (this.doRemoveItem(itemId)) {
				this.addAllOf(anyOf, (AllOfType) itemId, true);
				return true;
			}
			return false;
		}
		logger.warn("Can't move this item to anyOf: " + itemId.getClass().getCanonicalName());
		return false;
	}
	
	protected boolean moveItemToAllOf(Object itemId, AllOfType allOf) {
		if (itemId instanceof MatchType) {
			if (this.doRemoveItem(itemId)) {
				this.addMatch(allOf, (MatchType) itemId, true);
				return true;
			}
			return false;
		}
		logger.warn("Can't move this item to allOf: " + itemId.getClass().getCanonicalName());
		return false;
	}

	@Override
	public boolean areChildrenAllowed(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("areChildrenAllowed: " + itemId);
		}
		if (itemId instanceof MatchType ||
			itemId instanceof ObligationExpressionType ||
			itemId instanceof AdviceExpressionType ||
			itemId instanceof ConditionType ||
			itemId instanceof VariableDefinitionType) {
			return false;
		}
		return true;
	}

	@Override
	public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
			throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("setChildrenAllowed: " + itemId + " " + areChildrenAllowed);
		}
		if (itemId instanceof MatchType ||
			itemId instanceof ObligationExpressionType ||
			itemId instanceof AdviceExpressionType ||
			itemId instanceof ConditionType ||
			itemId instanceof VariableDefinitionType) {
			if (areChildrenAllowed == true) {
				return false;
			}
			return true;
		}
		if (areChildrenAllowed == false) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isRoot(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("isRoot: " + itemId);
		}
		assert itemId != null;
		if (itemId == null) {
			//
			// This usually means the container's map's are screwed up.
			//
			logger.error("NULL isRoot item");
			return false;
		}
		if (itemId instanceof PolicyType && this.root instanceof PolicyType && itemId.equals(this.root)) {
			return true;
		} else if (itemId instanceof PolicySetType && this.root instanceof PolicySetType && itemId.equals(this.root)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean hasChildren(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("hasChildren: " + itemId);
		}
		if (itemId instanceof MatchType ||
			itemId instanceof ObligationExpressionType ||
			itemId instanceof AdviceExpressionType ||
			itemId instanceof ConditionType ||
			itemId instanceof VariableDefinitionType) {
			return false;
		}
		return true;
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeItem: " + itemId);
		}
		boolean result = this.doRemoveItem(itemId);
		if (result) {
			this.fireItemSetChange();
		}
		return result;
	}
	
	protected boolean doRemoveItem(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("doRemoveItem: " + itemId);
		}
		if (itemId instanceof PolicySetType) {
			PolicySetType parent = this.policySets.get(itemId);
			if (parent == null) {
				logger.error("policy set not found in map");
				assert false;
				return false;
			}
			return this.removePolicySetFromPolicySet(parent, (PolicySetType) itemId);
		} else if (itemId instanceof PolicyType) {
			PolicySetType parent = this.policies.get(itemId);
			if (parent == null) {
				logger.error("policy not found in map");
				assert false;
				return false;
			}
			return this.removePolicyFromPolicySet(parent, (PolicyType) itemId);
		} else if (itemId instanceof RuleType) {
			PolicyType parent = this.rules.get(itemId);
			if (parent == null) {
				logger.error("rule not found in map");
				assert false;
				return false;
			}
			if (parent.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().remove(itemId)) {
				this.removeRule((RuleType) itemId);
				return true;
			}
			logger.error("Failed to remove rule from parent policy");
			assert false;
			return false;
		} else if (itemId instanceof TargetType) {
			throw new UnsupportedOperationException("Cannot remove TargetType directly - please remove it via PolicySet/Policy/Rule");
		} else if (itemId instanceof ObligationExpressionType) {
			Object parent = this.obligations.get(itemId);
			if (parent == null) {
				logger.error("obligation not found in map");
				assert false;
				return false;
			}
			if (parent instanceof PolicySetType) {
				if (((PolicySetType) parent).getObligationExpressions() != null) {
					if (((PolicySetType) parent).getObligationExpressions().getObligationExpression().remove(itemId) == false) {
						logger.error("Failed to remove obligation expression from policy set");
						assert false;
						return false;
					}
				} else {
					logger.error("policy set does not contain obligation expressions, cannot remove");
					assert false;
					return false;
				}
			} else if (parent instanceof PolicyType) {
				if (((PolicyType) parent).getObligationExpressions() != null) {
					if (((PolicyType) parent).getObligationExpressions().getObligationExpression().remove(itemId) == false) {
						logger.error("Failed to remove obligation expression from policy");
						assert false;
						return false;
					}
				} else {
					logger.error("policy does not contain obligation expressions, cannot remove");
					assert false;
					return false;
				}
			} else if (parent instanceof RuleType) {
				if (((RuleType) parent).getObligationExpressions() != null) {
					if (((RuleType) parent).getObligationExpressions().getObligationExpression().remove(itemId) == false) {
						logger.error("Failed to remove obligation expression from policy set");
						assert false;
						return false;
					}
				} else {
					logger.error("rule does not contain obligation expressions, cannot remove");
					assert false;
					return false;
				}
			} else {
				logger.error("Unknown parent for obligation: " + parent.getClass().getCanonicalName());
				assert false;
				return false;
			}
			if (this.obligations.remove(itemId) == null) {
				logger.error("obligation map does not contain itemId");
				assert false;
			}
			return true;
		} else if (itemId instanceof AdviceExpressionType) {
			Object parent = this.advice.get(itemId);
			if (parent == null) {
				logger.error("advice not found in map");
				assert false;
				return false;
			}
			if (parent instanceof PolicySetType) {
				if (((PolicySetType) parent).getAdviceExpressions() != null) {
					if (((PolicySetType) parent).getAdviceExpressions().getAdviceExpression().remove(itemId) == false) {
						logger.error("Failed to remove advice expression from policy set");
						assert false;
						return false;
					}
				} else {
					logger.error("policy set does not contain advice expressions, cannot remove");
					assert false;
					return false;
				}
			} else if (parent instanceof PolicyType) {
				if (((PolicyType) parent).getAdviceExpressions() != null) {
					if (((PolicyType) parent).getAdviceExpressions().getAdviceExpression().remove(itemId) == false) {
						logger.error("Failed to remove advice expression from policy");
						assert false;
						return false;
					}
				} else {
					logger.error("policy does not contain advice expressions, cannot remove");
					assert false;
					return false;
				}
			} else if (parent instanceof RuleType) {
				if (((RuleType) parent).getAdviceExpressions() != null) {
					if (((RuleType) parent).getAdviceExpressions().getAdviceExpression().remove(itemId) == false) {
						logger.error("Failed to remove advice expression from rule");
						assert false;
						return false;
					}
				} else {
					logger.error("rule does not contain advice expressions, cannot remove");
					assert false;
					return false;
				}
			} else {
				logger.error("Unknown parent for advice: " + parent.getClass().getCanonicalName());
				assert false;
				return false;
			}
			if (this.advice.remove((AdviceExpressionType) itemId) == null) {
				logger.error("obligation map does not contain itemId");
				assert false;
			}
			return true;
		} else if (itemId instanceof AnyOfType) {
			TargetType parent = this.anyofs.get(itemId);
			if (parent == null) {
				logger.error("anyof not found in map");
				assert false;
				return false;
			}
			if (parent.getAnyOf().remove(itemId)) {
				this.removeAnyOf((AnyOfType) itemId);
				return true;
			} else {
				logger.error("Failed to remove itemId from target");
				assert false;
				return false;
			}
		} else if (itemId instanceof AllOfType) {
			AnyOfType parent = this.allofs.get(itemId);
			if (parent == null) {
				logger.error("allof not found in map");
				assert false;
				return false;
			}
			if (parent.getAllOf().remove(itemId)) {
				this.removeAllOf((AllOfType) itemId);
				return true;
			} else {
				logger.error("Failed to remove itemId from anyOf");
				assert false;
				return false;
			}
		} else if (itemId instanceof MatchType) {
			AllOfType parent = this.matches.get(itemId);
			if (parent == null) {
				logger.error("match not found in map");
				assert false;
				return false;
			}
			if (parent.getMatch().remove(itemId)) {
				this.removeMatch((MatchType) itemId);
				return true;
			} else {
				logger.error("Failed to remove itemId from allOf");
				assert false;
				return false;
			}
		} else if (itemId instanceof ConditionType) {
			RuleType rule = this.conditions.get(itemId);
			if (rule != null) {
				rule.setCondition(null);
				if (this.conditions.remove(itemId) == null) {
					logger.error("Failed to remove condition from map");
					assert false;
					return false;
				}
				return true;
			} else {
				logger.error("condition not found in map");
				assert false;
				return false;
			}
		} else if (itemId instanceof VariableDefinitionType) {
			PolicyType policy = this.variables.get(itemId);
			if (policy != null) {
				if (policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition().remove(itemId)) {
					if (this.variables.remove(itemId) == null) {
						logger.error("failed to remove variable from map");
						assert false;
						return false;
					}
					return true;
				} else {
					logger.error("failed to remove variable from policy");
					assert false;
					return false;
				}
			} else {
				logger.error("variable not found in map");
				assert false;
				return false;
			}
		}
		logger.error("Failed to remove policy set from policy set, not found.");
		return false;
	}
	
	protected boolean removePolicyFromPolicySet(PolicySetType parent, PolicyType policy) {
		for (JAXBElement<?> element : parent.getPolicySetOrPolicyOrPolicySetIdReference()) {
			if (PolicyType.class.isAssignableFrom(element.getDeclaredType()) &&
						((PolicyType) element.getValue()).getPolicyId().equals(policy.getPolicyId())) {
				if (parent.getPolicySetOrPolicyOrPolicySetIdReference().remove(element)) {
					this.removePolicy(policy);
					return true;
				}
				logger.error("Failed to remove policy from parent policy set");
				assert false;
				return false;
			}
		}
		logger.error("Failed to remove policy from policy set, not found.");
		return false;
	}

	protected boolean removePolicySetFromPolicySet(PolicySetType parent, PolicySetType policySet) {
		for (JAXBElement<?> element : parent.getPolicySetOrPolicyOrPolicySetIdReference()) {
			if (PolicySetType.class.isAssignableFrom(element.getDeclaredType())) {
				logger.info(element);
				if (((PolicySetType) element.getValue()).getPolicySetId().equals(policySet.getPolicySetId())) {
					if (parent.getPolicySetOrPolicyOrPolicySetIdReference().remove(element)) {
						this.removePolicySet(policySet);
						return true;
					}
					logger.error("Failed to remove policy set from parent policy set");
					assert false;
					return false;
				}
			}
		}
		return false;
	}
	
	protected void removePolicySet(PolicySetType policySet) {
		if (this.policySets.remove(policySet) == null) {
			logger.warn("Failed to remove policy set from map: " + policySet);
			return;
		}
		//
		// Remove its objects from the other maps
		//
		this.removeTarget(policySet.getTarget());
		this.removeObligations(policySet.getObligationExpressions());
		this.removeAdvice(policySet.getAdviceExpressions());
	}
	
	protected void removePolicy(PolicyType policy) {
		if (this.policies.remove(policy) == null) {
			logger.warn("Failed to remove policy from map: " + policy);
			return;
		}
		//
		// Remove its objects from the other maps
		//
		this.removeTarget(policy.getTarget());
		this.removeObligations(policy.getObligationExpressions());
		this.removeAdvice(policy.getAdviceExpressions());
		this.removeVariables(policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition());
	}
	
	protected void removeVariables(List<Object> combinerParametersOrRuleCombinerParametersOrVariableDefinition) {
		for (Object object : combinerParametersOrRuleCombinerParametersOrVariableDefinition) {
			if (object instanceof VariableDefinitionType) {
				this.variables.remove(object);
			}
		}
	}

	protected void removeRule(RuleType rule) {
		if (this.rules.remove(rule) == null) {
			logger.warn("Failed to remove rule from map: " + rule);
			return;
		}
		//
		// Remove its objects from the other maps
		//
		this.removeTarget(rule.getTarget());
		this.removeObligations(rule.getObligationExpressions());
		this.removeAdvice(rule.getAdviceExpressions());
		if (rule.getCondition() != null) {
			this.conditions.remove(rule.getCondition());
		}
	}
	
	protected void removeObligations(ObligationExpressionsType expressions) {
		if (expressions == null) {
			return;
		}
		for (ObligationExpressionType expression : expressions.getObligationExpression()) {
			if (this.obligations.remove(expression) == null) {
				logger.warn("Failed to remove obligation expression: " + expression);
			}
		}
	}
	
	protected void removeAdvice(AdviceExpressionsType expressions) {
		if (expressions == null) {
			return;
		}
		for (AdviceExpressionType expression : expressions.getAdviceExpression()) {
			if (this.advice.remove(expression) == null) {
				logger.warn("Failed to remove advice expression: " + expression);
			}
		}
	}
	
	protected void removeTarget(TargetType target) {
		if (this.targets.remove(target) == null) {
			logger.warn("Failed to remove target from map: " + target);
			return;
		}
		for (AnyOfType anyof : target.getAnyOf()) {
			this.removeAnyOf(anyof);
		}
	}
	
	protected void removeAnyOf(AnyOfType anyof) {
		if (this.anyofs.remove(anyof) == null) {
			logger.warn("failed to remove anyof from map: " + anyof);
			return;
		}
		for (AllOfType allof : anyof.getAllOf()) {
			this.removeAllOf(allof);
		}
	}
	
	protected void removeAllOf(AllOfType allof) {
		if (this.allofs.remove(allof) == null) {
			logger.warn("failed to remove allof from map: " + allof);
			return;
		}
		for (MatchType match : allof.getMatch()) {
			this.removeMatch(match);
		}
	}
	
	protected void removeMatch(MatchType match) {
		if (this.matches.remove(match) == null) {
			logger.warn("failed to remove match from map: " + match);
			return;
		}
	}

	@Override
	public Object nextItemId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("nextItemId: " + itemId);
		}
		return null;
	}

	@Override
	public Object prevItemId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("prevItemId: " + itemId);
		}
		return null;
	}

	@Override
	public Object firstItemId() {
		if (logger.isTraceEnabled()) {
			logger.trace("firstItemId: ");
		}
		return this.root;
	}

	@Override
	public Object lastItemId() {
		if (logger.isTraceEnabled()) {
			logger.trace("lastItemId: ");
		}
		return null;
	}

	@Override
	public boolean isFirstId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("isFirstId: " + itemId);
		}
		//
		// The Oasis classes do not have equals implemented. So I am
		// not too sure that using equals works.
		//
		if (this.root != null && itemId != null) {
			return this.root.equals(itemId);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("item is NOT the first ID" +  itemId);
		}
		return false;
	}

	@Override
	public boolean isLastId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("isLastId: " + itemId);
		}
		return false;
	}

	@Override
	public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Need to know what you want added. Please use addItemAfter(Object, Object) instead.");
	}

	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("addItemAfter: " + previousItemId + " new " + newItemId);
		}
		/*
		if (newItemId instanceof PolicySetType) {
			
		}
		//
		// Get our parents
		//
		Object parentPreviousItem = this.getParent(previousItemId);
		*/
		return null;
	}
	
	public Item moveAfterSibling(Object itemId, Object siblingId) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("moveAfterSibling: " + itemId + " sibling " + siblingId);
		}
		//
		// Get the parents, which should be the same
		//
		/*
		Object itemParent = this.getParent(itemId);
		Object siblingParent = this.getParent(siblingId);
		if (itemParent == null) {
			logger.error("can't move the root element");
			return null;
		}
		if (itemParent != siblingParent) {
			logger.error("parents are not the same");
			return null;
		}
		if (itemId instanceof PolicySetType) {
			assert(itemParent instanceof PolicySetType);
			if (itemParent instanceof PolicySetType) {
				if (siblingId instanceof PolicySetType || siblingId instanceof PolicyType) {
					int index = ((PolicySetType) itemParent).getPolicySetOrPolicyOrPolicySetIdReference().indexOf(siblingId);
					
				}				
			}
		}
		*/
		return null;
	}

	/**
	 * This class is returned to caller's to display the properties
	 * for each policy object.
	 * 
	 * @author pameladragosh
	 *
	 */
	public class PolicyItem implements Item {
		private static final long serialVersionUID = 1L;
		private final Object data;
		
		public PolicyItem(Object data) {
			this.data = data;
		}

		public String getName() {
			if (this.data instanceof RuleType) {
				return "Rule";
			}
			if (this.data instanceof PolicyType) {
				return "Policy";
			}
			if (this.data instanceof PolicySetType) {
				return "Policy Set";
			}
			if (this.data instanceof TargetType) {
				return "Target";
			}
			if (this.data instanceof AnyOfType) {
				return "Any Of";
			}
			if (this.data instanceof AllOfType) {
				return "All Of";
			}
			if (this.data instanceof MatchType) {
				return "Match";
			}
			if (this.data instanceof ObligationExpressionType) {
				return "Obligation";
			}
			if (this.data instanceof AdviceExpressionType) {
				return "Advice";
			}
			if (this.data instanceof ConditionType) {
				return "Condition";
			}
			if (this.data instanceof VariableDefinitionType) {
				return "Variable";
			}
			return null;
		}
		
		public String getId() {
			if (this.data instanceof RuleType) {
				return ((RuleType) this.data).getRuleId();
			} 
			if (this.data instanceof PolicyType) {
				return ((PolicyType) this.data).getPolicyId();
			}
			if (this.data instanceof PolicySetType) {
				return ((PolicySetType) this.data).getPolicySetId();
			}
			if (this.data instanceof ObligationExpressionType) {
				return ((ObligationExpressionType) this.data).getObligationId();
			}
			if (this.data instanceof AdviceExpressionType) {
				return ((AdviceExpressionType) this.data).getAdviceId();
			}
			/*
			if (this.data instanceof TargetType) {
				return this.data.toString();
			}
			if (this.data instanceof AnyOfType) {
				return this.data.toString();
			}
			if (this.data instanceof AllOfType) {
				return this.data.toString();
			}
			if (this.data instanceof MatchType) {
				((MatchType) this.data).getMatchId();
			}
			*/
			if (this.data instanceof VariableDefinitionType) {
				((VariableDefinitionType) this.data).getVariableId();
			}
			return null;
		}
		
		public String getShortId() {
			String id = this.getId();
			if (id == null) {
				return null;
			}
			String[] parts = id.split("[:]");
			
			if (parts != null && parts.length > 0) {
				return parts[parts.length - 1];
			}
			
			return null;
		}
		
		public String getDescription() {
			if (this.data instanceof RuleType) {
				return ((RuleType) this.data).getDescription();
			} else if (this.data instanceof PolicyType) {
				return ((PolicyType) this.data).getDescription();
			} else if (this.data instanceof PolicySetType) {
				return ((PolicySetType) this.data).getDescription();
			} else if (this.data instanceof MatchType) {
				StringBuilder builder = new StringBuilder();
				AttributeValueType value = ((MatchType) this.data).getAttributeValue();
				if (value != null) {
					builder.append(value.getContent());
					builder.append(" ");
				}
				String alg = this.getShortAlgorithm();
				if (alg != null && alg.length() > 0) {
					builder.append(alg);
					builder.append(" ");
				}
				if (((MatchType) this.data).getAttributeDesignator() != null) {
					builder.append(((MatchType) this.data).getAttributeDesignator().getAttributeId());
				} else if (((MatchType) this.data).getAttributeSelector() != null) {
					builder.append(((MatchType) this.data).getAttributeSelector().getContextSelectorId());
				}
				return builder.toString();
			}
			return null;
		}
		
		public String getAlgorithm() {
			if (this.data instanceof PolicyType) {
				return ((PolicyType) this.data).getRuleCombiningAlgId();
			}
			if (this.data instanceof PolicySetType) {
				return ((PolicySetType) this.data).getPolicyCombiningAlgId();
			}
			if (this.data instanceof RuleType) {
				return ((RuleType) this.data).getEffect().toString();
			}
			if (this.data instanceof MatchType) {
				return ((MatchType) this.data).getMatchId();
			}
			if (this.data instanceof ObligationExpressionType) {
				return ((ObligationExpressionType) this.data).getFulfillOn().toString();
			}
			if (this.data instanceof AdviceExpressionType) {
				return ((AdviceExpressionType) this.data).getAppliesTo().toString();
			}
			if (this.data instanceof ConditionType) {
				return this.getRootExpressionFunction(((ConditionType) this.data).getExpression());
			}
			if (this.data instanceof VariableDefinitionType) {
				//return this.getRootExpressionFunction(((VariableDefinitionType) this.data).getExpression());
				return ((VariableDefinitionType) this.data).getVariableId();
			}
			return null;
		}
		
		public String getShortAlgorithm() {
			String algorithm = null;
			if (this.data instanceof PolicyType) {
				algorithm = ((PolicyType) this.data).getRuleCombiningAlgId();
			}
			if (this.data instanceof PolicySetType) {
				algorithm = ((PolicySetType) this.data).getPolicyCombiningAlgId();
			}
			if (this.data instanceof RuleType) {
				return ((RuleType) this.data).getEffect().toString();
			}
			if (this.data instanceof ObligationExpressionType) {
				return ((ObligationExpressionType) this.data).getFulfillOn().toString();
			}
			if (this.data instanceof AdviceExpressionType) {
				return ((AdviceExpressionType) this.data).getAppliesTo().toString();
			}
			if (this.data instanceof MatchType) {
				algorithm = ((MatchType) this.data).getMatchId();
			}
			if (this.data instanceof ConditionType) {
				algorithm = this.getRootExpressionFunction(((ConditionType) this.data).getExpression());
				if (algorithm.startsWith("http")) {
					return algorithm;
				}
			}
			if (this.data instanceof VariableDefinitionType) {
				//algorithm = this.getRootExpressionFunction(((VariableDefinitionType) this.data).getExpression());
				return ((VariableDefinitionType) this.data).getVariableId();
			}
			if (algorithm != null) {
				Iterable<String> fields = Splitter.on(':').trimResults().omitEmptyStrings().split(algorithm);
				if (fields != null) {
					String lastId = null;
					for (String id : fields) {
						lastId = id;
					}
					return lastId;
				}
			}
			return null;
		}
		
        public Resource getIcon() {
			if (this.data instanceof PolicySetType) {
				return new ThemeResource("../runo/icons/16/folder.png");
			}
			if (this.data instanceof RuleType) {
				if (((RuleType) this.data).getEffect() == null) {
					logger.warn("Rule has a null Effect");
					return new ThemeResource("icons/deny-16.png");
				}
				if (((RuleType) this.data).getEffect() == EffectType.DENY) {
					return new ThemeResource("icons/deny-16.png");
				}
				return new ThemeResource("icons/permit-16.png");
			}
			if (this.data instanceof PolicyType) {
				return new ThemeResource("../runo/icons/16/document-txt.png");
			}
			if (this.data instanceof TargetType) {
				return new ThemeResource("icons/target-green-16.png");
			}
			if (this.data instanceof ObligationExpressionType) {
				return new ThemeResource("icons/obligation-16.png");
			}
			if (this.data instanceof AdviceExpressionType) {
				return new ThemeResource("icons/advice-16.png");
			}
			if (this.data instanceof ConditionType) {
				return new ThemeResource("icons/condition-16.png");
			}
			if (this.data instanceof VariableDefinitionType) {
				return new ThemeResource("icons/variable-16.png");
			}
			return null;
        }
        
        public String getStatus() {
        	StringBuffer buffer = new StringBuffer();
        	String a = this.getAlgorithm();
        	//String id = this.getId();
			if (this.data instanceof RuleType) {
				RuleType rule = ((RuleType) this.data);
				buffer.append("Rule");
				EffectType effect = rule.getEffect();
				if (effect == null) {
					rule.setEffect(EffectType.PERMIT);
				}
				if (rule.getEffect() == EffectType.PERMIT) {
					buffer.append(" (PERMIT)");
				} else if (rule.getEffect() == EffectType.DENY) {
					buffer.append(" (DENY)");
				}
			} else if (this.data instanceof PolicyType) {
				buffer.append("Policy");
			} else if (this.data instanceof PolicySetType) {
				buffer.append("Policy Set");
			}
			/*
			if (id != null) {
				String[] ids = id.split("[:]");
				if (ids.length > 0) {
					buffer.append(" " + ids[ids.length - 1]);
				}
			}
			*/
			if (a != null) {
				String[] algs = a.split("[:]");
				if (algs.length > 0) {
					buffer.append(" (" + algs[algs.length - 1] + ")");
				}
			}
			/*
			String d = this.getDescription();
			if (d != null) {
				buffer.append(" " + d);
			}
			*/
			return buffer.toString();
        }
        
        public Table getAttributes() {
        	/*
        	if (this.data instanceof MatchType) {
        		
        	}
        	*/
        	return null;
        }
        
        protected	String	getRootExpressionFunction(JAXBElement<?> element) {
        	if (element == null || element.getValue() == null) {
        		return null;
        	}
        	Object value = element.getValue();
        	if (value instanceof ApplyType) {
        		return ((ApplyType) value).getFunctionId();
        	}
        	if (value instanceof AttributeValueType) {
        		return ((AttributeValueType) value).getDataType();
        	}
        	if (value instanceof AttributeDesignatorType) {
        		return ((AttributeDesignatorType) value).getAttributeId();
        	}
        	if (value instanceof AttributeSelectorType) {
        		return ((AttributeSelectorType) value).getContextSelectorId();
        	}
        	if (value instanceof VariableReferenceType) {
        		return "Variable=" + ((VariableReferenceType) value).getVariableId();
        	}
        	if (value instanceof FunctionType) {
        		return ((FunctionType) value).getFunctionId();
        	}
        	return null;
        }
        
		@Override
		public Property<?> getItemProperty(Object id) {
            return getContainerProperty(data, id);
		}

		@Override
		public Collection<?> getItemPropertyIds() {
            return getContainerPropertyIds();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Policy container does not support adding new properties");
		}

		@Override
		public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Policy container does not support property removal");
		}

		@Override
		public String toString() {
			return this.getName();
		}
		
	}
}
