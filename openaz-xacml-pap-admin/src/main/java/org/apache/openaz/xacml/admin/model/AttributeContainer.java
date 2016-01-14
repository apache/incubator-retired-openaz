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

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.admin.jpa.Category;
import org.apache.openaz.xacml.admin.jpa.Datatype;
import org.apache.openaz.xacml.admin.util.JPAUtils;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.util.XACMLPolicyAggregator;
import org.apache.openaz.xacml.util.XACMLPolicyScanner;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Table;

public class AttributeContainer extends ItemSetChangeNotifier implements Container.Hierarchical, Container.ItemSetChangeNotifier {
	private static final long serialVersionUID = 1L;
	private static Log logger	= LogFactory.getLog(AttributeContainer.class);
	//private final AttributeContainer self = this;
	private final Map<Category, Map<Datatype, Map<String, Set<ContainerAttribute>>>> mapAttributes = new HashMap<Category, Map<Datatype, Map<String, Set<ContainerAttribute>>>>();

	class ContainerAttribute {
		Path				policy;
		boolean				isRoot;
		AttributeValue<?>	value;
		
		public ContainerAttribute(Path policy, boolean isRoot, AttributeValue<?> value) {
			this.policy = policy;
			this.isRoot = isRoot;
			this.value = value;
		}

		public Path getPolicy() {
			return policy;
		}

		public boolean isRoot() {
			return isRoot;
		}

		public AttributeValue<?> getValue() {
			return value;
		}
		
		public boolean isCustom() {
			return this.policy == null;
		}
	}
    /**
     * String identifier of an object's "id" property.
     */
    public static String PROPERTY_ID = "Id";

    /**
     * String identifier of an object's "category" property.
     */
    public static String PROPERTY_CATEGORY = "Category";

    /**
     * String identifier of an object's "datatype" property.
     */
    public static String PROPERTY_DATATYPE = "Datatype";

    /**
     * String identifier of an object's "value" property.
     */
    public static String PROPERTY_VALUES = "Values";

    /**
     * List of the string identifiers for the available properties.
     */
    public static Collection<String> ATTRIBUTE_PROPERTIES;

    private final static Method ATTRIBUTEITEM_ID;
    private final static Method ATTRIBUTEITEM_CATEGORY;
    private final static Method ATTRIBUTEITEM_DATATYPE;
    private final static Method ATTRIBUTEITEM_VALUES;
    static {
    	ATTRIBUTE_PROPERTIES = new ArrayList<String>();
    	ATTRIBUTE_PROPERTIES.add(PROPERTY_ID);
    	ATTRIBUTE_PROPERTIES.add(PROPERTY_CATEGORY);
    	ATTRIBUTE_PROPERTIES.add(PROPERTY_DATATYPE);
    	ATTRIBUTE_PROPERTIES.add(PROPERTY_VALUES);
    	ATTRIBUTE_PROPERTIES =  Collections.unmodifiableCollection(ATTRIBUTE_PROPERTIES);
    	try {
    		ATTRIBUTEITEM_ID = AttributeItem.class.getMethod("getId", new Class[]{});
    		ATTRIBUTEITEM_CATEGORY = AttributeItem.class.getMethod("getCategory", new Class[]{});
    		ATTRIBUTEITEM_DATATYPE = AttributeItem.class.getMethod("getDatatype", new Class[]{});
    		ATTRIBUTEITEM_VALUES = AttributeItem.class.getMethod("getValues", new Class[]{});
    	} catch (final NoSuchMethodException e) {
            throw new RuntimeException("Internal error finding methods in AttributeContainer");
        }
    }
    
	public AttributeContainer(Path rootPolicy, Collection<Path> referencedPolicies) {
		super();
		this.setContainer(this);
		this.initialize(rootPolicy, referencedPolicies);
	}
	
	protected void initialize(Path rootPolicy, Collection<Path> referencedPolicies) {
		XACMLPolicyAggregator aggregator = new XACMLPolicyAggregator();
		//
		// Scan the policy
		//
		new XACMLPolicyScanner(rootPolicy, aggregator).scan();
		this.addAttributes(aggregator, rootPolicy, true);
		aggregator = new XACMLPolicyAggregator();
		//
		// Scan the referenced policies
		//
		for (Path policy : referencedPolicies) {
			new XACMLPolicyScanner(policy, aggregator).scan();
			this.addAttributes(aggregator, policy, false);
			aggregator = new XACMLPolicyAggregator();
		}
	}
	
	protected void addAttributes(XACMLPolicyAggregator aggregator, Path policy, boolean isRoot) {
		for (Identifier cat : aggregator.getAttributeMap().keySet()) {
			Category category = JPAUtils.findCategory(cat);
			if (category == null) {
				logger.warn("Could not find category: " + cat);
				continue;
			}
			if (this.mapAttributes.containsKey(category) == false) {
				this.mapAttributes.put(category, new HashMap<Datatype, Map<String, Set<ContainerAttribute>>>());
			}
			Map<Datatype, Map<String, Set<ContainerAttribute>>> datatypeMap = this.mapAttributes.get(category);
			for (Identifier dt : aggregator.getAttributeMap().get(cat).keySet()) {
				Datatype datatype = JPAUtils.findDatatype(dt);
				if (datatype == null) {
					logger.warn("Could not find datatype: " + dt);
				}
				//
				// Need a unique datatype object
				//
				datatype = new Datatype((int) System.currentTimeMillis(), datatype);
				if (datatypeMap.containsKey(datatype) == false) {
					datatypeMap.put(datatype, new HashMap<String, Set<ContainerAttribute>>());
				}
				Map<String, Set<ContainerAttribute>> attributeMap = datatypeMap.get(datatype);
				for (Identifier id : aggregator.getAttributeMap().get(cat).get(dt).keySet()) {
					if (attributeMap.containsKey(id.stringValue()) == false) {
						attributeMap.put(id.stringValue(), new HashSet<ContainerAttribute>());
					}
					for (AttributeValue<?> attribute : aggregator.getAttributeMap().get(cat).get(dt).get(id)) {
						attributeMap.get(id.stringValue()).add(new ContainerAttribute(policy, isRoot, attribute));
					}
				}
			}
		}
	}
	
	protected boolean isObjectSupported(Object itemId) {
		if (itemId instanceof Category ||
			itemId instanceof Datatype ||
			itemId instanceof String ||
			itemId instanceof ContainerAttribute) {
			return true;
		}
		return false;
	}

	@Override
	public Item getItem(Object itemId) {
		if (this.isObjectSupported(itemId)) {
			return new AttributeItem(itemId);
		}
		return null;
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return ATTRIBUTE_PROPERTIES;
	}

	@Override
	public Collection<?> getItemIds() {
		final Collection<Object> items = new ArrayList<Object>();
		for (Category category : this.mapAttributes.keySet()) {
			items.add(category);
			for (Datatype datatype : this.mapAttributes.get(category).keySet()) {
				items.add(datatype);
				for (String id : this.mapAttributes.get(category).get(datatype).keySet()) {
					items.add(id);
					for (ContainerAttribute attribute : this.mapAttributes.get(category).get(datatype).get(id)) {
						items.add(attribute);
					}
				}
			}
		}
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
		if (this.isObjectSupported(itemId) == false) {
			return null;
		}
        if (propertyId.equals(PROPERTY_ID)) {
            return new MethodProperty<Object>(getType(propertyId), 
            						new AttributeItem(itemId), ATTRIBUTEITEM_ID, null);
        }
        if (propertyId.equals(PROPERTY_CATEGORY)) {
            return new MethodProperty<Object>(getType(propertyId), 
            						new AttributeItem(itemId), ATTRIBUTEITEM_CATEGORY, null);
        }
        if (propertyId.equals(PROPERTY_DATATYPE)) {
            return new MethodProperty<Object>(getType(propertyId), 
            						new AttributeItem(itemId), ATTRIBUTEITEM_DATATYPE, null);
        }
        if (propertyId.equals(PROPERTY_VALUES)) {
            return new MethodProperty<Object>(getType(propertyId), 
            						new AttributeItem(itemId), ATTRIBUTEITEM_VALUES, null);
        }
		return null;
	}

	@Override
	public Class<?> getType(Object propertyId) {
        if (propertyId.equals(PROPERTY_ID)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_CATEGORY)) {
            return Category.class;
        }
        if (propertyId.equals(PROPERTY_DATATYPE)) {
            return Datatype.class;
        }
        if (propertyId.equals(PROPERTY_VALUES)) {
            return Table.class;
        }
		return null;
	}

	@Override
	public int size() {
		return this.mapAttributes.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		if (this.isObjectSupported(itemId) == false) {
			return false;
		}
		if (itemId instanceof Category) {
			return this.mapAttributes.containsKey(itemId);
		}
		for (Category category : this.mapAttributes.keySet()) {
			if (itemId instanceof Datatype) {
				return this.mapAttributes.get(category).containsKey(itemId);
			}
			for (Datatype datatype : this.mapAttributes.get(category).keySet()) {
				if (itemId instanceof String) {
					return this.mapAttributes.get(category).get(datatype).containsKey(itemId);
				}
				for (String id : this.mapAttributes.get(category).get(datatype).keySet()) {
					if (itemId instanceof ContainerAttribute) {
						return this.mapAttributes.get(category).get(datatype).get(id).contains(itemId);
					}
				}
			}
		}
		return false;
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Use addItem(Category, Datatype, AttributeValue<?>) instead.");
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Use addItem(Category, Datatype, AttributeValue<?>) instead.");
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
		throw new UnsupportedOperationException("Can't remove all the items. You can remove custom user attributes.");
	}

	@Override
	public Collection<?> getChildren(Object itemId) {
		//
		// PLD TODO - this may not work for Datatype
		//
		final Collection<Object> items = new ArrayList<Object>();
		for (Category category : this.mapAttributes.keySet()) {
			for (Datatype datatype : this.mapAttributes.get(category).keySet()) {
				if (itemId instanceof Category) {
					items.add(datatype);
				}
				for (String id : this.mapAttributes.get(category).get(datatype).keySet()) {
					if (itemId instanceof Category ||
						itemId instanceof Datatype) {
						items.add(id);
						items.addAll(this.mapAttributes.get(category).get(datatype).get(id));
					} else if (itemId instanceof String) {
						items.addAll(this.mapAttributes.get(category).get(datatype).get(id));
					}
				}
			}
		}
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public Object getParent(Object itemId) {
		if (this.isObjectSupported(itemId) == false) {
			return null;
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<?> rootItemIds() {
		final Collection<Object> items = new ArrayList<Object>();
		items.add(this.mapAttributes.keySet());
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't move attributes around. Use addItem(Category, Datatype, Attribute).");
	}

	@Override
	public boolean areChildrenAllowed(Object itemId) {
		if (itemId instanceof Category ||
			itemId instanceof Datatype ||
			itemId instanceof String) {
			return true;
		}
		return false;
	}

	@Override
	public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
			throws UnsupportedOperationException {
		if (itemId instanceof Category ||
			itemId instanceof Datatype ||
			itemId instanceof String) {
			if (areChildrenAllowed) {
				return true;
			}
			return false;
		}
		if (areChildrenAllowed == false) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isRoot(Object itemId) {
		return this.mapAttributes.containsKey(itemId);
	}

	@Override
	public boolean hasChildren(Object itemId) {
		if (itemId instanceof ContainerAttribute) {
			return false;
		}
		if (itemId instanceof Category) {
			if (this.mapAttributes.containsKey(itemId)) {
				return this.mapAttributes.get(itemId).size() > 0;
			}
			return false;
		}
		//
		// PLD TODO - this may not work. Datatype may prove difficult
		// to distinguish which category it is in.
		//
		for (Category category : this.mapAttributes.keySet()) {
			if (itemId instanceof Datatype) {
				if (this.mapAttributes.get(category).containsKey(itemId)) {
					return this.mapAttributes.get(category).get(itemId).size() > 0;
				}
				continue;
			}
			for (Datatype datatype : this.mapAttributes.get(category).keySet()) {
				if (itemId instanceof String) {
					if (this.mapAttributes.get(category).get(datatype).containsKey(itemId)) {
						return this.mapAttributes.get(category).get(datatype).get(itemId).size() > 0;
					}
					continue;
				}
			}
		}
		return false;
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		if (! (itemId instanceof ContainerAttribute)) {
			return false;
		}
		for (Category category : this.mapAttributes.keySet()) {
			for (Datatype datatype : this.mapAttributes.get(category).keySet()) {
				for (String id : this.mapAttributes.get(category).get(datatype).keySet()) {
					if (this.mapAttributes.get(category).get(datatype).get(id).contains(itemId)) {
						return this.mapAttributes.get(category).get(datatype).get(id).remove(itemId);
					}
				}
			}
		}
		return false;
	}
	
	public class AttributeItem implements Item {
		private static final long serialVersionUID = 1L;
		private final Object data;
		
		public AttributeItem(Object data) {
			this.data = data;
		}
		
		public String getId() {
			return null;
		}
		
		public Category getCategory() {
			return null;
		}
		
		public Datatype getDatatype() {
			return null;
		}
		
		public Table	getValues() {
			return null;
		}

		@Override
		public Property<?> getItemProperty(Object id) {
            return getContainerProperty(this.data, id);
		}

		@Override
		public Collection<?> getItemPropertyIds() {
	          return getContainerPropertyIds();
		}

		@Override
		public boolean addItemProperty(Object id, @SuppressWarnings("rawtypes") Property property) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Attribute container does not support adding new properties");
		}
		@Override
		public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Attribute container does not support removing properties");
		}
	}
}
