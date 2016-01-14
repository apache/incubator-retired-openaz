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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.openaz.xacml.admin.jpa.Datatype;
import org.apache.openaz.xacml.admin.model.AttributeContainer.ContainerAttribute;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;

public class AttributeValueContainer extends ItemSetChangeNotifier implements Container.Ordered, Container.ItemSetChangeNotifier {
	private static final long serialVersionUID = 1L;
	private final Datatype datatype; //NOPMD
	private final List<ContainerAttribute> attributes;

    /**
     * String identifier of an object's "Value" property.
     */
    public static String PROPERTY_VALUE = "Value";

    /**
     * String identifier of an object's "Source" property.
     */
    public static String PROPERTY_SOURCE = "Source";

    /**
     * List of the string identifiers for the available properties.
     */
    public static Collection<String> ATTRIBUTEVALUE_PROPERTIES;

    private final static Method ATTRIBUTEVALUEITEM_VALUE;
    private final static Method ATTRIBUTEVALUEITEM_SOURCE;
    static {
    	ATTRIBUTEVALUE_PROPERTIES = new ArrayList<String>();
    	ATTRIBUTEVALUE_PROPERTIES.add(PROPERTY_VALUE);
    	ATTRIBUTEVALUE_PROPERTIES.add(PROPERTY_SOURCE);
    	ATTRIBUTEVALUE_PROPERTIES =  Collections.unmodifiableCollection(ATTRIBUTEVALUE_PROPERTIES);
    	try {
    		ATTRIBUTEVALUEITEM_VALUE = AttributeValueItem.class.getMethod("getValue", new Class[]{});
    		ATTRIBUTEVALUEITEM_SOURCE = AttributeValueItem.class.getMethod("getSource", new Class[]{});
    	} catch (final NoSuchMethodException e) {
            throw new RuntimeException("Internal error finding methods in AttributeValueContainer");
        }
    }
    
    public AttributeValueContainer(Datatype datatype, List<ContainerAttribute> attributes) {
		this.datatype = datatype;
		this.attributes = attributes;
	}
    
    public boolean isObjectSupported(Object itemId) {
    	return itemId instanceof ContainerAttribute;
    }

	@Override
	public Item getItem(Object itemId) {
		if (itemId instanceof ContainerAttribute) {
			return new AttributeValueItem((ContainerAttribute) itemId);
		}
		return null;
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return ATTRIBUTEVALUE_PROPERTIES;
	}

	@Override
	public Collection<?> getItemIds() {
		return Collections.unmodifiableList(this.attributes);
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
		if (this.isObjectSupported(itemId) == false) {
			return null;
		}
        if (propertyId.equals(PROPERTY_VALUE)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new AttributeValueItem((ContainerAttribute) itemId), ATTRIBUTEVALUEITEM_VALUE, null);
        }

        if (propertyId.equals(PROPERTY_SOURCE)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new AttributeValueItem((ContainerAttribute) itemId), ATTRIBUTEVALUEITEM_SOURCE, null);
        }
		return null;
	}

	@Override
	public Class<?> getType(Object propertyId) {
        if (propertyId.equals(PROPERTY_VALUE)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_SOURCE)) {
            return String.class;
        }
		return null;
	}

	@Override
	public int size() {
		return this.attributes.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		return this.attributes.contains(itemId);
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		if (this.isObjectSupported(itemId) == false) {
			return null;
		}
		return new AttributeValueItem((ContainerAttribute) itemId);
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Please use addItem(Object itemId) - setup the container attribute first.");
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		if (this.isObjectSupported(itemId) == false) {
			return false;
		}
		throw new UnsupportedOperationException("TODO");
//		return this.attributes.remove(itemId);
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
		throw new UnsupportedOperationException("TODO");
//		this.attributes.clear();
//		return true;
	}

	@Override
	public Object nextItemId(Object itemId) {
		if (this.isObjectSupported(itemId) == false) {
			return null;
		}
		int index = this.getItemIndex((ContainerAttribute) itemId);
		if (index == -1 || index >= this.attributes.size()) {
			return null;
		}
		return this.attributes.get(index + 1);
	}

	@Override
	public Object prevItemId(Object itemId) {
		if (this.isObjectSupported(itemId) == false) {
			return null;
		}
		int index = this.getItemIndex((ContainerAttribute) itemId);
		if (index == -1 || index == 0) {
			return null;
		}
		return this.attributes.get(index - 1);
	}

	@Override
	public Object firstItemId() {
		if (this.attributes.size() > 0) {
			return this.attributes.get(0);
		}
		return null;
	}

	@Override
	public Object lastItemId() {
		if (this.attributes.size() > 0) {
			return this.attributes.get(this.attributes.size() - 1);
		}
		return null;
	}

	@Override
	public boolean isFirstId(Object itemId) {
		if (this.attributes.size() > 0) {
			return this.attributes.get(0).equals(itemId);
		}
		return false;
	}

	@Override
	public boolean isLastId(Object itemId) {
		if (this.attributes.size() > 0) {
			return this.attributes.get(this.attributes.size() - 1).equals(itemId);
		}
		return false;
	}

	@Override
	public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Use addItemAfter(Object previousItemId, Object newItemId) - please create the object yourself.");
	}

	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
		if (this.isObjectSupported(previousItemId) == false || this.isObjectSupported(newItemId) == false) {
			return null;
		}
		int index = this.getItemIndex((ContainerAttribute) previousItemId);
		if (index >= 0) {
			this.attributes.add(index, (ContainerAttribute) newItemId);
		}
		return null;
	}
	
	protected int	getItemIndex(ContainerAttribute itemId) {
		int index;
		for (index = 0; index < this.attributes.size(); index++) {
			if (this.attributes.get(index).equals(itemId)) {
				return index;
			}
		}
		return -1;
	}
	
	public class AttributeValueItem implements Item {
		private static final long serialVersionUID = 1L;
		private final ContainerAttribute attribute;
		
		public AttributeValueItem(ContainerAttribute attribute) {
			this.attribute = attribute;
		}

		public String getValue() {
			if (this.attribute == null) {
				return null;
			}
			return this.attribute.value.toString();
		}

		public String getSource() {
			if (this.attribute == null) {
				return null;
			}
			return this.attribute.value.toString();
		}

		@Override
		public Property<?> getItemProperty(Object id) {
            return getContainerProperty(this.attribute, id);
		}
		@Override
		public Collection<?> getItemPropertyIds() {
           return getContainerPropertyIds();
		}
		@Override
		public boolean addItemProperty(Object id, @SuppressWarnings("rawtypes") Property property) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Attribute Value container does not support adding new properties");
		}
		@Override
		public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Attribute Value container does not support removing properties");
		}
	}

}
