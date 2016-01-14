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
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.admin.jpa.PIPConfiguration;
import org.apache.openaz.xacml.admin.jpa.PIPResolver;
import org.apache.openaz.xacml.api.pap.PDP;
import org.apache.openaz.xacml.api.pap.PDPGroup;
import org.apache.openaz.xacml.api.pap.PDPPIPConfig;
import org.apache.openaz.xacml.api.pip.PIPException;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;

public class PDPPIPContainer extends ItemSetChangeNotifier implements Container.Hierarchical, Container.ItemSetChangeNotifier {
	private static final long serialVersionUID = 1L;
	private static Log logger	= LogFactory.getLog(PDPPIPContainer.class);

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
     * List of the string identifiers for the available properties.
     */
    public static Collection<String> PDPPIP_PROPERTIES;

    private final static Method PDPPIPITEM_ID;
    
    private final static Method PDPPIPITEM_NAME;
    
    private final static Method PDPPIPITEM_DESCRIPTION;
    
    static {
    	PDPPIP_PROPERTIES = new ArrayList<String>();
    	PDPPIP_PROPERTIES.add(PROPERTY_ID);
    	PDPPIP_PROPERTIES.add(PROPERTY_NAME);
    	PDPPIP_PROPERTIES.add(PROPERTY_DESCRIPTION);
    	PDPPIP_PROPERTIES = Collections.unmodifiableCollection(PDPPIP_PROPERTIES);
        try {
        	PDPPIPITEM_ID = PDPPIPItem.class.getMethod("getId", new Class[]{});
        	PDPPIPITEM_NAME = PDPPIPItem.class.getMethod("getName", new Class[]{});
        	PDPPIPITEM_DESCRIPTION = PDPPIPItem.class.getMethod("getDescription", new Class[] {});
       } catch (final NoSuchMethodException e) {
            throw new RuntimeException(
                    "Internal error finding methods in PDPPIPContainer");
        }
    }
    
    private final Object data;
    
    List<PIPConfiguration> configurations = new ArrayList<PIPConfiguration>();
 
	public PDPPIPContainer(Object data) {
		super();
		setContainer(this);
		//
		// Save our object
		//
		this.data = data;
		//
		// Is it supported?
		//
		if (this.isPDPGroup() == false && this.isPDP() == false) {
			throw new IllegalArgumentException("This container only supported PDPGroup and PDP objects.");
		}
		//
		// Initialize our internal structures
		//
		initialize();
	}
	
	private boolean	isSupported(Object itemId) {
		if (this.isConfiguration(itemId) ||
			this.isResolver(itemId) ) {
			return true;
		}
		return false;
	}
	
	private boolean isPDPGroup() {
		return this.data instanceof PDPGroup;
	}

	private boolean isPDP() {
		return this.data instanceof PDP;
	}
	
	private boolean isConfiguration(Object itemId) {
		return itemId instanceof PIPConfiguration;
	}
	
	private boolean isResolver(Object itemId) {
		return itemId instanceof PIPResolver;
	}
	
	private void initialize() {
		assert this.data != null;
		//
		// Get the list of configurations
		//
		Set<PDPPIPConfig> configs = null;
		if (this.isPDPGroup()) {
			configs = ((PDPGroup) this.data).getPipConfigs();
		} else if (this.isPDP()) {
			configs = ((PDP) this.data).getPipConfigs();
		} else {
			throw new IllegalArgumentException("This container only supported PDPGroup and PDP objects.");
		}
		//
		// Map these to a list of PIPConfiguration objects. That
		// way we can match them up to the database.
		//
		for (PDPPIPConfig config : configs) {
			Properties properties = new Properties();
			properties.putAll(config.getConfiguration());
			try {
				PIPConfiguration pipConfig = new PIPConfiguration(config.getId(), properties);
				if (logger.isDebugEnabled()) {
					logger.debug("Found config: " + pipConfig);
				}
				this.configurations.add(pipConfig);
			} catch (PIPException e) {
				logger.error("Failed to create PIPConfiguration: " + e.getLocalizedMessage());
			}
		}
	}
	

	public void refresh() {
		this.configurations.clear();
		this.initialize();
		this.fireItemSetChange();
	}
	
	@Override
	public Item getItem(Object itemId) {
		if (this.isSupported(itemId)) {
			return new PDPPIPItem(itemId);
		}
		return null;
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return PDPPIP_PROPERTIES;
	}

	@Override
	public Collection<?> getItemIds() {
		final Collection<Object> items = new ArrayList<Object>();
		for (PIPConfiguration config : this.configurations) {
			items.add(config);
			/*
			for (PIPResolver resolver : config.getPipresolvers()) {
				items.add(resolver);
			}
			*/
		}
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
		if (this.isSupported(itemId) == false) {
			return null;
		}
		
        if (propertyId.equals(PROPERTY_ID)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPPIPItem(itemId), PDPPIPITEM_ID, null);
        }

        if (propertyId.equals(PROPERTY_NAME)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPPIPItem(itemId), PDPPIPITEM_NAME, null);
        }
        
        if (propertyId.equals(PROPERTY_DESCRIPTION)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new PDPPIPItem(itemId), PDPPIPITEM_DESCRIPTION, null);
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
		return null;
	}

	@Override
	public int size() {
		/*
		int size = 0;
		for (PIPConfiguration config : this.configurations) {
			size++;
			size += config.getPipresolvers().size();
		}
		return size;
		*/
		return this.configurations.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		//
		// Let's try this by using the Id
		//
		for (PIPConfiguration config : this.configurations) {
			if (this.isConfiguration(itemId)) {
				if (((PIPConfiguration) itemId).getId() == config.getId()) {
					return true;
				}
			} else if (this.isResolver(itemId)) {
				for (PIPResolver resolver : config.getPipresolvers()) {
					if (((PIPResolver) itemId).getId() == resolver.getId()) {
						return true;
					}
				}				
			} else {
				throw new IllegalArgumentException("This container only supports pip configuration and resolvers objects.");
			}
		}
		return false;
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		if (this.isConfiguration(itemId)) {
			this.configurations.add((PIPConfiguration) itemId);
		// } else if (this.isResolver(itemId)) {
		}
		throw new UnsupportedOperationException("Cannot add unsupported object.");
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot add unknown object.");
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Adding properties not supported.");
	}

	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Removing properties not supported.");
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		//
		// Remove everything
		//
		this.configurations.clear();
		return true;
	}

	@Override
	public Collection<?> getChildren(Object itemId) {
		if (this.isConfiguration(itemId)) {
			Collection<Object> children = new ArrayList<Object>();
			for (PIPConfiguration config : this.configurations) {
				if (config.getId() == ((PIPConfiguration) itemId).getId()) {
					/*
					 * Not for this release
					 * 
					children.addAll(config.getPipresolvers());
					*/
					break;
				}
			}
			return Collections.unmodifiableCollection(children);
		}
		return Collections.emptyList();
	}

	@Override
	public Object getParent(Object itemId) {
		if (this.isResolver(itemId)) {
			return ((PIPResolver) itemId).getPipconfiguration();
		}
		return null;
	}

	@Override
	public Collection<?> rootItemIds() {
		Collection<Object> roots = new ArrayList<Object>();
		roots.addAll(this.configurations);
		return Collections.unmodifiableCollection(roots);
	}

	@Override
	public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Not allowed in this implementation.");
	}

	@Override
	public boolean areChildrenAllowed(Object itemId) {
		if (this.isConfiguration(itemId)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot change which objects can or cannot have children.");
	}

	@Override
	public boolean isRoot(Object itemId) {
		if (this.isConfiguration(itemId)) {
			return this.containsId(itemId);
		}
		return false;
	}

	@Override
	public boolean hasChildren(Object itemId) {
		if (this.isConfiguration(itemId)) {
			//return ((PIPConfiguration) itemId).getPipresolvers().size() > 0;
			//
			// Not this implementation
			return false;
		}
		return false;
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Not allowed in this implementation.");
		/*
		if (this.isConfiguration(itemId)) {
			return this.configurations.remove(itemId);
		}
		if (this.isResolver(itemId)) {
			((PIPResolver) itemId).getPipconfiguration().removePipresolver((PIPResolver) itemId);
		}
		throw new UnsupportedOperationException("Object not supported by this container.");
		*/
	}

	public class PDPPIPItem implements Item {
		private static final long serialVersionUID = 1L;
		
		private final Object config;
		
		public PDPPIPItem(Object config) {
			this.config = config;
		}
		
		public String getId() {
			if (this.config instanceof PIPConfiguration) {
				return Integer.toString(((PIPConfiguration) this.config).getId());
			}
			if (this.config instanceof PIPResolver) {
				return Integer.toString(((PIPResolver) this.config).getId());
			}
			return null;
		}
		
		public String getName() {
			if (this.config instanceof PIPConfiguration) {
				return ((PIPConfiguration) this.config).getName();
			}
			if (this.config instanceof PIPResolver) {
				return ((PIPResolver) this.config).getName();
			}
			return null;
		}
		
		public String getDescription() {
			if (this.config instanceof PIPConfiguration) {
				return ((PIPConfiguration) this.config).getDescription();
			}
			if (this.config instanceof PIPResolver) {
				return ((PIPResolver) this.config).getDescription();
			}
			return null;
		}

		@Override
		public Property<?> getItemProperty(Object id) {
            return getContainerProperty(config, id);
		}

		@Override
		public Collection<?> getItemPropertyIds() {
	        return getContainerPropertyIds();
		}

		@Override
		public boolean addItemProperty(Object id, @SuppressWarnings("rawtypes") Property property)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Cannot add property.");
		}

		@Override
		public boolean removeItemProperty(Object id)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Cannot remove property.");
		}	
	}
}
