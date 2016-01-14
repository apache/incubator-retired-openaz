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
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.api.pap.PDP;
import org.apache.openaz.xacml.api.pap.PDPGroup;
import org.apache.openaz.xacml.api.pap.PDPPolicy;
import org.apache.openaz.xacml.std.pap.StdPDPPolicy;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;

public class PDPPolicyContainer extends ItemSetChangeNotifier implements Container.Indexed {
	private static final long serialVersionUID = 1L;
	private static Log logger	= LogFactory.getLog(PDPPolicyContainer.class);
	
	 /**
     * String identifier of a file's "Id" property.
     */
    public static String PROPERTY_ID = "Id";

   /**
     * String identifier of a file's "name" property.
     */
    public static String PROPERTY_NAME = "Name";

    /**
      * String identifier of a file's "name" property.
      */
     public static String PROPERTY_VERSION = "Version";
     
    /**
     * String identifier of a file's "Description" property.
     */
    public static String PROPERTY_DESCRIPTION = "Description";
    
    /**
     * String identifier of a file's "IsRoot" property.
     */
    public static String PROPERTY_ISROOT = "Root";

    /**
     * List of the string identifiers for the available properties.
     */
    public static Collection<String> PDPPOLICY_PROPERTIES;

    private final static Method PDPPOLICYITEM_ID;
    
    private final static Method PDPPOLICYITEM_NAME;
    
    private final static Method PDPPOLICYITEM_VERSION;
    
    private final static Method PDPPOLICYITEM_DESCRIPTION;
    
    private final static Method PDPPOLICYITEM_ISROOT;
    
    private final static Method PDPPOLICYITEM_SETISROOT;
   
    static {
    	PDPPOLICY_PROPERTIES = new ArrayList<String>();
    	PDPPOLICY_PROPERTIES.add(PROPERTY_ID);
    	PDPPOLICY_PROPERTIES.add(PROPERTY_NAME);
    	PDPPOLICY_PROPERTIES.add(PROPERTY_VERSION);
    	PDPPOLICY_PROPERTIES.add(PROPERTY_DESCRIPTION);
    	PDPPOLICY_PROPERTIES.add(PROPERTY_ISROOT);
    	PDPPOLICY_PROPERTIES = Collections.unmodifiableCollection(PDPPOLICY_PROPERTIES);
        try {
        	PDPPOLICYITEM_ID = PDPPolicyItem.class.getMethod("getId", new Class[]{});
        	PDPPOLICYITEM_NAME = PDPPolicyItem.class.getMethod("getName", new Class[]{});
        	PDPPOLICYITEM_VERSION = PDPPolicyItem.class.getMethod("getVersion", new Class[]{});
        	PDPPOLICYITEM_DESCRIPTION = PDPPolicyItem.class.getMethod("getDescription", new Class[] {});
        	PDPPOLICYITEM_ISROOT = PDPPolicyItem.class.getMethod("getRoot", new Class[] {});
        	PDPPOLICYITEM_SETISROOT = PDPPolicyItem.class.getMethod("setRoot", new Class[] {Boolean.class});
       } catch (final NoSuchMethodException e) {
            throw new RuntimeException(
                    "Internal error finding methods in PDPContainer");
        }
    }
    
    private final Object data;
    private List<PDPPolicy> policies;
    
	public PDPPolicyContainer(Object data) {
		super();
		this.data = data;
		if (this.data instanceof PDPGroup) {
			policies = new ArrayList<PDPPolicy> (((PDPGroup) this.data).getPolicies());
		}
		if (this.data instanceof PDP) {
			policies = new ArrayList<PDPPolicy> (((PDP) this.data).getPolicies());
		}
		if (this.data instanceof Set) {
			policies = new ArrayList<PDPPolicy> ((Set<PDPPolicy>)data);
		}
		if (this.policies == null) {
			logger.info("NULL policies");
			throw new NullPointerException("PDPPolicyContainer created with unexpected Object type '" + data.getClass().getName() + "'");
		}
		this.setContainer(this);
	}
	
	@Override
	public Object nextItemId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("nextItemId: " + itemId);
		}
		int index = this.policies.indexOf(itemId);
		if (index == -1 || (index + 1) >= this.policies.size()) {
			return null;
		}		
		return new PDPPolicyItem(this.policies.get(index + 1));
	}

	@Override
	public Object prevItemId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("prevItemId: " + itemId);
		}
		int index = this.policies.indexOf(itemId);
		if (index <= 0) {
			return null;
		}
		return new PDPPolicyItem(this.policies.get(index - 1));
	}

	@Override
	public Object firstItemId() {
		if (logger.isTraceEnabled()) {
			logger.trace("firstItemId: ");
		}
		if (this.policies.isEmpty()) {
			return null;
		}
		return new PDPPolicyItem(this.policies.get(0));
	}

	@Override
	public Object lastItemId() {
		if (logger.isTraceEnabled()) {
			logger.trace("lastItemid: ");
		}
		if (this.policies.isEmpty()) {
			return null;
		}
		return new PDPPolicyItem(this.policies.get(this.policies.size() - 1));
	}

	@Override
	public boolean isFirstId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("isFirstId: " + itemId);
		}
		if (this.policies.isEmpty()) {
			return false;
		}
		return itemId.equals(this.policies.get(0));
	}

	@Override
	public boolean isLastId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("isLastId: " + itemId);
		}
		if (this.policies.isEmpty()) {
			return false;
		}
		return itemId.equals(this.policies.get(this.policies.size() - 1));
	}

	@Override
	public Object addItemAfter(Object previousItemId)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Item getItem(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getItem: " + itemId);
		}
		if (itemId instanceof PDPPolicy) {
			return new PDPPolicyItem((PDPPolicy) itemId);
		}
		return null;
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return PDPPOLICY_PROPERTIES;
	}

	@Override
	public Collection<?> getItemIds() {
		final Collection<Object> items = new ArrayList<Object>();
		items.addAll(this.policies);
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
		if (itemId instanceof PDPPolicy == false) {
			return null;
		}
		
		if (propertyId.equals(PROPERTY_ID)) {
			return new MethodProperty<Object>(getType(propertyId),
							new PDPPolicyItem((PDPPolicy) itemId), PDPPOLICYITEM_ID, null);
		}

		if (propertyId.equals(PROPERTY_NAME)) {
			return new MethodProperty<Object>(getType(propertyId),
							new PDPPolicyItem((PDPPolicy) itemId), PDPPOLICYITEM_NAME, null);
		}
		
		if (propertyId.equals(PROPERTY_VERSION)) {
			return new MethodProperty<Object>(getType(propertyId),
							new PDPPolicyItem((PDPPolicy) itemId), PDPPOLICYITEM_VERSION, null);
		}

		if (propertyId.equals(PROPERTY_DESCRIPTION)) {
			return new MethodProperty<Object>(getType(propertyId),
							new PDPPolicyItem((PDPPolicy) itemId), PDPPOLICYITEM_DESCRIPTION, null);
		}
		
		if (propertyId.equals(PROPERTY_ISROOT)) {
			return new MethodProperty<Object>(getType(propertyId),
							new PDPPolicyItem((PDPPolicy) itemId), PDPPOLICYITEM_ISROOT, PDPPOLICYITEM_SETISROOT);
		}
		
		return null;
	}

	@Override
	public Class<?> getType(Object propertyId) {
        if (propertyId.equals(PROPERTY_ID)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_NAME)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_VERSION)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_DESCRIPTION)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_ISROOT)) {
            return Boolean.class;
        }
		return null;
	}

	@Override
	public int size() {
		if (logger.isTraceEnabled()) {
			logger.trace("size: " + this.policies.size());
		}
		return this.policies.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("containsId: " + itemId);
		}
		return this.policies.contains(itemId);
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("addItem: " + itemId);
		}
		if (itemId instanceof PDPPolicy) {
			this.policies.add((PDPPolicy) itemId);
			return new PDPPolicyItem((PDPPolicy)itemId);
		}
		return null;
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot add an empty policy.");
	}

	@Override
	public boolean removeItem(Object itemId)
			throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeItem: " + itemId);
		}
		return this.policies.remove(itemId);
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
		//this.policies = new ArrayList<PDPPolicy>();
		//return true;
		return false;
	}

	@Override
	public int indexOfId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("indexOfId: " + itemId);
		}
		return this.policies.indexOf(itemId);
	}

	@Override
	public Object getIdByIndex(int index) {
		if (logger.isTraceEnabled()) {
			logger.trace("getIdByIndex: " + index);
		}
		return this.policies.get(index);
	}

	@Override
	public List<?> getItemIds(int startIndex, int numberOfItems) {
		if (logger.isTraceEnabled()) {
			logger.trace("getItemIds: " + startIndex + " " + numberOfItems);
		}
		if (numberOfItems < 0) {
			throw new IllegalArgumentException();
		}
		return this.policies.subList(startIndex, startIndex + numberOfItems);
	}

	@Override
	public Object addItemAt(int index) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("addItemAt: " + index);
		}
		return null;
	}

	@Override
	public Item addItemAt(int index, Object newItemId)
			throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("addItemAt: " + index + " " + newItemId);
		}
		return null;
	}	
	
	public class PDPPolicyItem implements Item {
		private static final long serialVersionUID = 1L;
		
		private final PDPPolicy policy;
		
		public PDPPolicyItem(PDPPolicy itemId) {
			this.policy = itemId;
		}

		public String getId() {
			if (logger.isTraceEnabled()) {
				logger.trace("getId: " + this.policy);
			}
			return this.policy.getId();
		}
		
		public String getName() {
			if (logger.isTraceEnabled()) {
				logger.trace("getName: " + this.policy);
			}
			return this.policy.getName();
		}
		
		public String getVersion() {
			if (logger.isTraceEnabled()) {
				logger.trace("getVersion: " + this.policy);
			}
			return this.policy.getVersion();
		}
		
		public String getDescription() {
			if (logger.isTraceEnabled()) {
				logger.trace("getDescription: " + this.policy);
			}
			return this.policy.getDescription();
		}
		
		public boolean getRoot() {
			if (logger.isTraceEnabled()) {
				logger.trace("isRoot: " + this.policy);
			}
			return this.policy.isRoot();
		}
		
		public void setRoot(Boolean root) {
			((StdPDPPolicy)this.policy).setRoot(root);
		}
		
		@Override
		public Property<?> getItemProperty(Object id) {
            return getContainerProperty(policy, id);
		}

		@Override
		public Collection<?> getItemPropertyIds() {
	        return getContainerPropertyIds();
		}

		@Override
		public boolean addItemProperty(Object id, @SuppressWarnings("rawtypes") Property property)
				throws UnsupportedOperationException {
	           throw new UnsupportedOperationException("PDP Policy container "
	                    + "does not support adding new properties");
		}

		@Override
		public boolean removeItemProperty(Object id)
				throws UnsupportedOperationException {
            throw new UnsupportedOperationException(
                    "PDP Policy container does not support property removal");
		}

	}
}

