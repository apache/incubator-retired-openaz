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

import org.apache.openaz.xacml.api.pap.PAPEngine;
import org.apache.openaz.xacml.api.pap.PDP;
import org.apache.openaz.xacml.api.pap.PDPGroup;
import org.apache.openaz.xacml.api.pap.PDPPIPConfig;
import org.apache.openaz.xacml.api.pap.PDPPolicy;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.Resource;

public class PDPContainer extends ItemSetChangeNotifier implements Container.Indexed, Container.ItemSetChangeNotifier {
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

    private final static Method PDPITEM_ICON;
    
    private final static Method PDPITEM_STATUS;
        
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
        	PDPITEM_ID = PDPItem.class.getMethod("getId", new Class[]{});
        	PDPITEM_NAME = PDPItem.class.getMethod("getName", new Class[]{});
        	PDPITEM_DESCRIPTION = PDPItem.class.getMethod("getDescription", new Class[] {});
            PDPITEM_ICON = PDPItem.class.getMethod("getIcon", new Class[] {});
            PDPITEM_STATUS = PDPItem.class.getMethod("getStatus", new Class[] {});
            PDPITEM_POLICIES = PDPItem.class.getMethod("getPolicies", new Class[] {});
            PDPITEM_PIPCONFIG = PDPItem.class.getMethod("getPipConfigs", new Class[] {});
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(
                    "Internal error finding methods in PDPContainer");
        }
    }
    
 	private PAPEngine papEngine = null; //NOPMD
	private PDPGroup group;
	private List<PDP> pdps = Collections.synchronizedList(new ArrayList<PDP>());
	
	public PDPContainer(PDPGroup group) {
		super();
		this.setContainer(this);
		//
		//
		//
		this.group = group;
		this.pdps.addAll(this.group.getPdps());
	}
	
	public synchronized void refresh(PDPGroup group) {
		synchronized(this.group) {
			this.group = group;
		}
		synchronized (this.pdps) {
			this.pdps.clear();
			this.pdps.addAll(this.group.getPdps());
		}
	}

	@Override
	public Object nextItemId(Object itemId) {
		synchronized (this.pdps) {
			int index = this.pdps.indexOf(itemId);
			if (index == -1) {
				return null;
			}
			if (index == this.pdps.size() - 1) {
				return null;
			}
			return this.pdps.get(index + 1);
		}
	}

	@Override
	public Object prevItemId(Object itemId) {
		synchronized (this.pdps) {
			int index = this.pdps.indexOf(itemId);
			if (index == -1) {
				return null;
			}
			if (index == 0) {
				return null;
			}
			return this.pdps.get(index - 1);
		}
	}

	@Override
	public Object firstItemId() {
		synchronized (this.pdps) {
			if (this.pdps.size() > 0) {
				return this.pdps.get(0);
			}
		}
		return null;
	}

	@Override
	public Object lastItemId() {
		synchronized (this.pdps) {
			if (this.pdps.size() > 0) {
				return this.pdps.get(this.pdps.size() - 1);
			}
		}
		return null;
	}

	@Override
	public boolean isFirstId(Object itemId) {
		synchronized (this.pdps) {
			if (this.pdps.size() > 0) {
				return this.pdps.get(0).equals(itemId);
			}
		}
		return false;
	}

	@Override
	public boolean isLastId(Object itemId) {
		synchronized (this.pdps) {
			if (this.pdps.size() > 0) {
				return this.pdps.get(this.pdps.size() - 1).equals(itemId);
			}
		}
		return false;
	}

	@Override
	public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Use addPDP method");
	}

	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Use addPDP method");
	}

	@Override
	public Item getItem(Object itemId) {
		/*
		if (itemId instanceof PDP) {
		}
		*/
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return PDP_PROPERTIES;
	}

	@Override
	public Collection<?> getItemIds() {
		synchronized(this.pdps) {
			return Collections.unmodifiableList(this.pdps);
		}
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
        if (propertyId.equals(PROPERTY_ID)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPItem((PDP) itemId), PDPITEM_ID, null);
        }

        if (propertyId.equals(PROPERTY_NAME)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPItem((PDP) itemId), PDPITEM_NAME, null);
        }

        if (propertyId.equals(PROPERTY_DESCRIPTION)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPItem((PDP) itemId), PDPITEM_DESCRIPTION, null);
        }

        if (propertyId.equals(PROPERTY_ICON)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPItem((PDP) itemId), PDPITEM_ICON, null);
        }

        if (propertyId.equals(PROPERTY_STATUS)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPItem((PDP) itemId), PDPITEM_STATUS, null);
        }

        if (propertyId.equals(PROPERTY_POLICIES)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPItem((PDP) itemId), PDPITEM_POLICIES, null);
        }

        if (propertyId.equals(PROPERTY_PIPCONFIG)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPItem((PDP) itemId), PDPITEM_PIPCONFIG, null);
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
        if (propertyId.equals(PROPERTY_ICON)) {
            return Resource.class;
        }
        if (propertyId.equals(PROPERTY_STATUS)) {
            return String.class;
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
		return this.pdps.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		return this.pdps.contains(itemId);
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot add a property");
	}

	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot remove a property");
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot remove all items.");
	}

	@Override
	public int indexOfId(Object itemId) {
		return this.pdps.indexOf(itemId);
	}

	@Override
	public Object getIdByIndex(int index) {
		return this.pdps.get(index);
	}

	@Override
	public List<?> getItemIds(int startIndex, int numberOfItems) {
		synchronized (this.pdps) {
			int endIndex = startIndex + numberOfItems;
			if (endIndex > this.pdps.size()) {
				endIndex = this.pdps.size() - 1;
			}
			return this.pdps.subList(startIndex, endIndex);
		}
	}

	@Override
	public Object addItemAt(int index) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot add item.");
	}

	@Override
	public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot add item.");
	}

	public class PDPItem implements Item {
		private static final long serialVersionUID = 1L;
		private final PDP pdp;
		
		public PDPItem(PDP pdp) {
			this.pdp = pdp;
		}

		public String getId() {
			if (logger.isTraceEnabled()) {
				logger.trace("getId: " + this.pdp);
			}
			return this.pdp.getId();
		}
		
		public String getName() {
			if (logger.isTraceEnabled()) {
				logger.trace("getName: " + this.pdp);
			}
			return this.pdp.getName();
		}
		
		public String getDescription() {
			if (logger.isTraceEnabled()) {
				logger.trace("getDescription: " + this.pdp);
			}
			return this.pdp.getDescription();
		}
		
        public Resource getIcon() {
			if (logger.isTraceEnabled()) {
				logger.trace("getIcon: " + this.pdp);
			}
        	return null;
        }
        
        public String	getStatus() {
        	String status = this.pdp.getStatus().getStatus().toString();
        	Set<String> errors = this.pdp.getStatus().getLoadErrors();
        	if (errors.size() > 0) {
        		status = status + String.format(" %d errors", errors.size());
        	}
        	Set<String> warnings = this.pdp.getStatus().getLoadWarnings();
        	if (warnings.size() > 0) {
        		status = status + String.format(" %d warnings", warnings.size());
        	}
        	return status;
        }
        
        public Set<PDPPolicy> getPolicies() {
 			if (logger.isTraceEnabled()) {
				logger.trace("getPolicies: " + this.pdp);
			}
 			return this.pdp.getPolicies();
        }
        
        public Set<PDPPIPConfig> getPipConfigs() {
			if (logger.isTraceEnabled()) {
				logger.trace("getPIPConfigs: " + this.pdp);
			}
			return this.pdp.getPipConfigs();
        }
        
		@Override
		public Property<?> getItemProperty(Object id) {
            return getContainerProperty(this.pdp, id);
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
