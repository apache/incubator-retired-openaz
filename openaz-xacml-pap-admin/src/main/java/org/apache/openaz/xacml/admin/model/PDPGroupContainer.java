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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.api.pap.PAPEngine;
import org.apache.openaz.xacml.api.pap.PAPException;
import org.apache.openaz.xacml.api.pap.PDP;
import org.apache.openaz.xacml.api.pap.PDPGroup;
import org.apache.openaz.xacml.api.pap.PDPPIPConfig;
import org.apache.openaz.xacml.api.pap.PDPPolicy;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.Resource;

public class PDPGroupContainer extends ItemSetChangeNotifier implements Container.Indexed, Container.ItemSetChangeNotifier {
	private static final long serialVersionUID = 1L;
	private static Log logger	= LogFactory.getLog(PDPGroupContainer.class);
	
    /**
     * String identifier of a file's "Id" property.
     */
    public static String PROPERTY_ID = "Id";

   /**
     * String identifier of a file's "name" property.
     */
    public static String PROPERTY_NAME = "Name";

    /**
     * String identifier of a file's "Description" property.
     */
    public static String PROPERTY_DESCRIPTION = "Description";

    /**
     * String identifier of a file's "Default" property.
     */
    public static String PROPERTY_DEFAULT = "Default";

    /**
     * String identifier of a file's "icon" property.
     */
    public static String PROPERTY_ICON = "Icon";

    /**
     * String identifier of a file's "Status" property.
     */
    public static String PROPERTY_STATUS = "Status";

    /**
     * String identifier of a file's "Status" property.
     */
    public static String PROPERTY_PDPS = "PDPs";

    /**
     * String identifier of a file's "Status" property.
     */
    public static String PROPERTY_POLICIES = "Policies";

    /**
     * String identifier of a file's "Status" property.
     */
    public static String PROPERTY_PIPCONFIG = "PIP Configurations";

    /**
     * List of the string identifiers for the available properties.
     */
    public static Collection<String> PDP_PROPERTIES;

    private final static Method PDPITEM_ID;
    
    private final static Method PDPITEM_NAME;
    
    private final static Method PDPITEM_DESCRIPTION;

    private final static Method PDPITEM_DEFAULT;

    private final static Method PDPITEM_ICON;
    
    private final static Method PDPITEM_STATUS;
        
    private final static Method PDPITEM_PDPS;
    
    private final static Method PDPITEM_POLICIES;
    
    private final static Method PDPITEM_PIPCONFIG;
        
    
    static {
    	PDP_PROPERTIES = new ArrayList<String>();
    	PDP_PROPERTIES.add(PROPERTY_ID);
    	PDP_PROPERTIES.add(PROPERTY_NAME);
    	PDP_PROPERTIES.add(PROPERTY_DESCRIPTION);
    	PDP_PROPERTIES.add(PROPERTY_DEFAULT);
    	PDP_PROPERTIES.add(PROPERTY_ICON);
    	PDP_PROPERTIES.add(PROPERTY_STATUS);
    	PDP_PROPERTIES.add(PROPERTY_PDPS);
    	PDP_PROPERTIES.add(PROPERTY_POLICIES);
    	PDP_PROPERTIES.add(PROPERTY_PIPCONFIG);
    	PDP_PROPERTIES = Collections.unmodifiableCollection(PDP_PROPERTIES);
        try {
        	PDPITEM_ID = PDPGroupItem.class.getMethod("getId", new Class[]{});
        	PDPITEM_NAME = PDPGroupItem.class.getMethod("getName", new Class[]{});
        	PDPITEM_DESCRIPTION = PDPGroupItem.class.getMethod("getDescription", new Class[] {});
        	PDPITEM_DEFAULT = PDPGroupItem.class.getMethod("getDefault", new Class[] {});
            PDPITEM_ICON = PDPGroupItem.class.getMethod("getIcon", new Class[] {});
            PDPITEM_STATUS = PDPGroupItem.class.getMethod("getStatus", new Class[] {});
            PDPITEM_PDPS = PDPGroupItem.class.getMethod("getPDPs", new Class[] {});
            PDPITEM_POLICIES = PDPGroupItem.class.getMethod("getPolicies", new Class[] {});
            PDPITEM_PIPCONFIG = PDPGroupItem.class.getMethod("getPipConfigs", new Class[] {});
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(
                    "Internal error finding methods in PDPContainer");
        }
    }
    
 	private PAPEngine papEngine = null;
 	private List<PDPGroup> groups = Collections.synchronizedList(new ArrayList<PDPGroup>());
 	
    public PDPGroupContainer(PAPEngine engine) {
		super();
		this.setContainer(this);
		//
		//
		//
		this.papEngine = engine;
		//
		//
		//
		this.refreshGroups();
	}
    
    public boolean isSupported(Object itemId) {
    	if (itemId instanceof PDPGroup) {
    		return true;
    	}
    	return false;
    }
	
	public synchronized void refreshGroups() {
		synchronized(this.groups) { 
			this.groups.clear();
			try {
				this.groups.addAll(this.papEngine.getPDPGroups());
			} catch (PAPException e) {
				String message = "Unable to retrieve Groups from server: " + e;
				logger.error(message, e);
			}
		}
		//
		// Notify that we have changed
		//
		this.fireItemSetChange();
	}
	
	public List<PDPGroup>	getGroups() {
		return Collections.unmodifiableList(this.groups);
	}
	
	public void makeDefault(PDPGroup group) {
		try {
			this.papEngine.SetDefaultGroup(group);
		} catch (PAPException e) {
			String message = "Unable to set Default Group on server: " + e;
			logger.error(message, e);
		}
	}
	
	public void removeGroup(PDPGroup group, PDPGroup newGroup) throws PAPException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeGroup: " + group + " new group for PDPs: " + newGroup);
		}
		if (group.isDefaultGroup()) {
			throw new UnsupportedOperationException("You can't remove the Default Group.");
		}
		try {
			this.papEngine.removeGroup(group, newGroup);
		} catch (NullPointerException | PAPException e) {
			logger.error("Failed to removeGroup " + group.getId(), e);
			throw new PAPException("Failed to remove group '" + group.getId()+ "'", e);
		}
	}
	
	public void removePDP(PDP pdp, PDPGroup group) throws PAPException {
		if (logger.isTraceEnabled()) {
			logger.trace("removePDP: " + pdp + " from group: " + group);
		}
		try {
			this.papEngine.removePDP(pdp);
		} catch (PAPException e) {
			logger.error("Failed to removePDP " + pdp.getId(), e);
			throw new PAPException("Failed to remove pdp '" + pdp.getId()+ "'", e);
		}
	}
	
	public void updatePDP(PDP pdp) {
		try {
			papEngine.updatePDP(pdp);
		} catch (PAPException e) {
			logger.error(e);
		}
	}
	
	public void updateGroup(PDPGroup group) {
		try {
			papEngine.updateGroup(group);
		} catch (PAPException e) {
			logger.error(e);
		}
	}
	
	@Override
	public Item getItem(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getItem: " + itemId);
		}
		if (this.isSupported(itemId)) {
			return new PDPGroupItem((PDPGroup) itemId);
		}
		return null;
	}
	
	@Override
	public Collection<?> getContainerPropertyIds() {
		return PDP_PROPERTIES;
	}

	@Override
	public Collection<?> getItemIds() {
		final Collection<Object> items = new ArrayList<Object>();
		items.addAll(this.groups);
		if (logger.isTraceEnabled()) {
			logger.trace("getItemIds: " + items);
		}
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
		
        if (propertyId.equals(PROPERTY_ID)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPGroupItem((PDPGroup) itemId), PDPITEM_ID, null);
        }

        if (propertyId.equals(PROPERTY_NAME)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPGroupItem((PDPGroup) itemId), PDPITEM_NAME, null);
        }

        if (propertyId.equals(PROPERTY_DESCRIPTION)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPGroupItem((PDPGroup) itemId), PDPITEM_DESCRIPTION, null);
        }

        if (propertyId.equals(PROPERTY_DEFAULT)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPGroupItem((PDPGroup) itemId), PDPITEM_DEFAULT, null);
        }

        if (propertyId.equals(PROPERTY_ICON)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPGroupItem((PDPGroup) itemId), PDPITEM_ICON, null);
        }

        if (propertyId.equals(PROPERTY_STATUS)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPGroupItem((PDPGroup) itemId), PDPITEM_STATUS, null);
        }

        if (propertyId.equals(PROPERTY_PDPS)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPGroupItem((PDPGroup) itemId), PDPITEM_PDPS, null);
        }

        if (propertyId.equals(PROPERTY_POLICIES)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPGroupItem((PDPGroup) itemId), PDPITEM_POLICIES, null);
        }

        if (propertyId.equals(PROPERTY_PIPCONFIG)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPGroupItem((PDPGroup) itemId), PDPITEM_PIPCONFIG, null);
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
        if (propertyId.equals(PROPERTY_DESCRIPTION)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_DEFAULT)) {
            return Boolean.class;
        }
        if (propertyId.equals(PROPERTY_ICON)) {
            return Resource.class;
        }
        if (propertyId.equals(PROPERTY_STATUS)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_PDPS)) {
            return Set.class;
        }
        if (propertyId.equals(PROPERTY_POLICIES)) {
            return Set.class;
        }
        if (propertyId.equals(PROPERTY_PIPCONFIG)) {
            return Set.class;
        }
		return null;
	}

	@Override
	public int size() {
		return this.groups.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("containsId: " + itemId);
		}
		if (this.isSupported(itemId) == false) {
			return false;
		}
		return this.groups.contains(itemId);
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("PDP Container cannot add a given item.");
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("PDP Container cannot add a given item.");
	}
	
	public void addNewGroup(String name, String description) throws NullPointerException, PAPException {
		if (logger.isTraceEnabled()) {
			logger.trace("addNewGroup " + name + " " + description);
		}
		this.papEngine.newGroup(name, description);
	}
	
	public void addNewPDP(String id, PDPGroup group, String name, String description) throws NullPointerException, PAPException {
		if (logger.isTraceEnabled()) {
			logger.trace("addNewPDP " + id + " " + name + " " + description);
		}
		this.papEngine.newPDP(id, group, name, description);
	}
	
	public void movePDP(PDP pdp, PDPGroup group) {
		try {
			this.papEngine.movePDP(pdp, group);
		} catch (PAPException e) {
			String message = "Unable to move PDP to new group on server: " + e;
			logger.error(message, e);
		}
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot add a container property.");
	}

	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot remove a container property.");
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("PDP Container cannot remove all items. You must have at least the Default group.");
	}

	@Override
	public void addItemSetChangeListener(ItemSetChangeListener listener) {
        if (getItemSetChangeListeners() == null) {
            setItemSetChangeListeners(new LinkedList<Container.ItemSetChangeListener>());
        }
        getItemSetChangeListeners().add(listener);	
	}

	@Override
	public Object nextItemId(Object itemId) {
		if (this.isSupported(itemId) == false) {
			return null;
		}
		int index = this.groups.indexOf(itemId);
		if (index == -1) {
			//
			// We don't know this group
			//
			return null;
		}
		//
		// Is it the last one?
		//
		if (index == this.groups.size() - 1) {
			//
			// Yes
			//
			return null;
		}
		//
		// Return the next one
		//
		return this.groups.get(index + 1);
	}

	@Override
	public Object prevItemId(Object itemId) {
		if (this.isSupported(itemId) == false) {
			return null;
		}
		int index = this.groups.indexOf(itemId);
		if (index == -1) {
			//
			// We don't know this group
			//
			return null;
		}
		//
		// Is it the first one?
		//
		if (index == 0) {
			//
			// Yes
			//
			return null;
		}
		//
		// Return the previous one
		//
		return this.groups.get(index - 1);
	}

	@Override
	public Object firstItemId() {
		synchronized (this.groups) {
			if (this.groups.size() > 0) {
				return this.groups.get(0);
			}
		}
		return null;
	}

	@Override
	public Object lastItemId() {
		synchronized (this.groups) {
			if (this.groups.size() > 0) {
				return this.groups.get(this.groups.size() - 1);
			}
		}
		return null;
	}

	@Override
	public boolean isFirstId(Object itemId) {
		synchronized (this.groups) {
			if (this.groups.size() > 0) {
				return this.groups.get(0).equals(itemId);
			}
		}
		return false;
	}

	@Override
	public boolean isLastId(Object itemId) {
		synchronized (this.groups) {
			if (this.groups.size() > 0) {
				return this.groups.get(this.groups.size() - 1).equals(itemId);
			}
		}
		return false;
	}

	@Override
	public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot addItemAfter, there really is no real ordering.");
	}

	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot addItemAfter, there really is no real ordering.");
	}

	@Override
	public int indexOfId(Object itemId) {
		return this.groups.indexOf(itemId);
	}

	@Override
	public Object getIdByIndex(int index) {
		return this.groups.get(index);
	}

	@Override
	public List<?> getItemIds(int startIndex, int numberOfItems) {
		synchronized (this.groups) {
			int endIndex = startIndex + numberOfItems;
			if (endIndex > this.groups.size()) {
				endIndex = this.groups.size() - 1;
			}
			return this.groups.subList(startIndex, endIndex);
		}
	}

	@Override
	public Object addItemAt(int index) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot addItemAt");
	}

	@Override
	public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot addItemAt");
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeItem: " + itemId);
		}
		if (this.isSupported(itemId) == false) {
			return false;
		}
		//
		// You cannot remove the default group
		//
		if (((PDPGroup) itemId).getId().equals("Default")) {
			throw new UnsupportedOperationException("You can't remove the Default Group.");
		}
		//
		// Remove PDPGroup and  move any PDP's in it into the default group
		//
		try {
			this.papEngine.removeGroup((PDPGroup) itemId, this.papEngine.getDefaultGroup());
			return true;
		} catch (NullPointerException | PAPException e) {
			logger.error("Failed to remove group", e);
		}
		return false;
	}

	public class PDPGroupItem implements Item {
		private static final long serialVersionUID = 1L;
		
		private final PDPGroup group;
		
		public PDPGroupItem(PDPGroup itemId) {
			this.group = itemId;
		}

		public String getId() {
			if (logger.isTraceEnabled()) {
				logger.trace("getId: " + this.group);
			}
			return this.group.getId();
		}
		
		public String getName() {
			if (logger.isTraceEnabled()) {
				logger.trace("getName: " + this.group);
			}
			return this.group.getName();
		}
		
		public String getDescription() {
			if (logger.isTraceEnabled()) {
				logger.trace("getDescription: " + this.group);
			}
			return this.group.getDescription();
		}
		
		public Boolean getDefault() {
			if (logger.isTraceEnabled()) {
				logger.trace("getDefault: " + this.group);
			}
			return this.group.isDefaultGroup();
		}
		
        public Resource getIcon() {
			if (logger.isTraceEnabled()) {
				logger.trace("getIcon: " + this.group);
			}
        	return null;
        }
        
        public String	getStatus() {
			return this.group.getStatus().getStatus().toString();
        }
        
        public Set<PDP>		getPDPs() {
        	return Collections.unmodifiableSet(this.group.getPdps());
        }
        
        public Set<PDPPolicy> getPolicies() {
 			if (logger.isTraceEnabled()) {
				logger.trace("getPolicies: " + this.group);
			}
 			return this.group.getPolicies();
        }
        
        public Set<PDPPIPConfig> getPipConfigs() {
			if (logger.isTraceEnabled()) {
				logger.trace("getPIPConfigs: " + this.group);
			}
			return this.group.getPipConfigs();
        }
        
		@Override
		public Property<?> getItemProperty(Object id) {
            return getContainerProperty(this.group, id);
		}

		@Override
		public Collection<?> getItemPropertyIds() {
	        return getContainerPropertyIds();
		}

		@Override
		public boolean addItemProperty(Object id, @SuppressWarnings("rawtypes") Property property) throws UnsupportedOperationException {
			throw new UnsupportedOperationException("PDP container does not support adding new properties");
		}

		@Override
		public boolean removeItemProperty(Object id)
				throws UnsupportedOperationException {
            throw new UnsupportedOperationException(
                    "PDP container does not support property removal");
		}	
	}
}
