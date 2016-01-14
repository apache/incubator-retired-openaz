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
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.IndexDiff;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;

public class GitStatusContainer extends ItemSetChangeNotifier implements Container.Ordered, Container.ItemSetChangeNotifier {
	private static final long serialVersionUID = 1L;
	private Log logger	= LogFactory.getLog(GitStatusContainer.class);

	/**
     * String identifier of a git file/directory's "name" property.
     */
    public static String PROPERTY_NAME = "Name";

	/**
     * String identifier of a git file/directory's "status" property.
     */
    public static String PROPERTY_STATUS = "Status";

	/**
     * String identifier of a git file/directory's "entry" property.
     */
    public static String PROPERTY_ENTRY = "Entry";

    /**
     * List of the string identifiers for the available properties.
     */
    public static Collection<String> GITSTATUS_PROPERTIES;
    
    private final static Method GITSTATUSITEM_NAME;
    
    private final static Method GITSTATUSITEM_STATUS;
    
    private final static Method GITSTATUSITEM_ENTRY;
    
    static {
    	GITSTATUS_PROPERTIES = new ArrayList<String>();
    	GITSTATUS_PROPERTIES.add(PROPERTY_NAME);
    	GITSTATUS_PROPERTIES.add(PROPERTY_STATUS);
    	GITSTATUS_PROPERTIES.add(PROPERTY_ENTRY);
    	GITSTATUS_PROPERTIES = Collections.unmodifiableCollection(GITSTATUS_PROPERTIES);
    	try {
    		GITSTATUSITEM_NAME = StatusItem.class.getMethod("getName", new Class[]{});
    		GITSTATUSITEM_STATUS = StatusItem.class.getMethod("getStatus", new Class[]{});
    		GITSTATUSITEM_ENTRY = StatusItem.class.getMethod("getGitEntry", new Class[]{});
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException("Internal error finding methods in GitStatusContainer");
        }
    }
    
    public class GitEntry {
    	String	name;
    	boolean added = false;
    	boolean changed = false;
    	boolean conflicting = false;
    	boolean ignoredNotInIndex = false;
    	boolean missing = false;
    	boolean modified = false;
    	boolean removed = false;
    	boolean uncommitted = false;
    	boolean untracked = false;
    	boolean untrackedFolders = false;
    	
    	public GitEntry(String name) {
    		this.name = name;
    	}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isAdded() {
			return added;
		}

		public void setAdded(boolean added) {
			this.added = added;
		}

		public boolean isChanged() {
			return changed;
		}

		public void setChanged(boolean changed) {
			this.changed = changed;
		}

		public boolean isConflicting() {
			return conflicting;
		}

		public void setConflicting(boolean conflicting) {
			this.conflicting = conflicting;
		}

		public boolean isIgnoredNotInIndex() {
			return ignoredNotInIndex;
		}

		public void setIgnoredNotInIndex(boolean ignoredNotInIndex) {
			this.ignoredNotInIndex = ignoredNotInIndex;
		}

		public boolean isMissing() {
			return missing;
		}

		public void setMissing(boolean missing) {
			this.missing = missing;
		}

		public boolean isModified() {
			return modified;
		}

		public void setModified(boolean modified) {
			this.modified = modified;
		}

		public boolean isRemoved() {
			return removed;
		}

		public void setRemoved(boolean removed) {
			this.removed = removed;
		}

		public boolean isUncommitted() {
			return uncommitted;
		}

		public void setUncommitted(boolean uncommitted) {
			this.uncommitted = uncommitted;
		}

		public boolean isUntracked() {
			return untracked;
		}

		public void setUntracked(boolean untracked) {
			this.untracked = untracked;
		}

		public boolean isUntrackedFolders() {
			return untrackedFolders;
		}

		public void setUntrackedFolders(boolean untrackedFolders) {
			this.untrackedFolders = untrackedFolders;
		}

		public String getStatus() {
			StringBuilder builder = new StringBuilder();
			if (this.isAdded()) {
				builder.append("Added" + System.lineSeparator());
			}
			if (this.isChanged()) {
				builder.append("Changed" + System.lineSeparator());
			}
			if (this.isConflicting()) {
				builder.append("Conflicting" + System.lineSeparator());
			}
			if (this.isMissing()) {
				builder.append("Missing" + System.lineSeparator());
			}
			if (this.isModified()) {
				builder.append("Modified" + System.lineSeparator());
			}
			if (this.isRemoved()) {
				builder.append("Removed" + System.lineSeparator());
			}
			if (this.isUncommitted()) {
				builder.append("Uncommitted" + System.lineSeparator());
			}
			if (this.isUntracked()) {
				builder.append("Untracked" + System.lineSeparator());
			}
			if (this.isUntrackedFolders()) {
				builder.append("Untracked Folders" + System.lineSeparator());
			}
			return builder.toString();
		}

		@Override
		public String toString() {
			return "GitEntry [name=" + name + ", added=" + added + ", changed="
					+ changed + ", conflicting=" + conflicting
					+ ", ignoredNotInIndex=" + ignoredNotInIndex + ", missing="
					+ missing + ", modified=" + modified + ", removed="
					+ removed + ", uncommitted=" + uncommitted + ", untracked="
					+ untracked + ", untrackedFolders=" + untrackedFolders
					+ "]";
		}
    }
    
    //
    // This is our data, a sorted map
    //
    private TreeMap<String, GitEntry> map = null;
    private Map<String, IndexDiff.StageState> conflictingStage = null;
    int conflictCount = 0;
    
	public GitStatusContainer(Status status) {
		super();
		this.setContainer(this);
		//
		// Initialize
		//
		this.refreshStatus(status);
	}
	
	public void refreshStatus(Status status) {
		//
		// Save this
		//
		this.conflictingStage = status.getConflictingStageState();
		if (logger.isDebugEnabled()) {
			logger.debug("conflictingStage: " + this.conflictingStage.size());
		}
		//
		// Re-create this
		//
		this.map = new TreeMap<String, GitEntry>();
		this.conflictCount = 0;
		//
		// Iterate through everything
		//
		for (String id : status.getAdded()) {
			if (id.endsWith(".gitignore") || id.endsWith(".DS_Store")) {
				continue;
			}
			GitEntry entry = this.map.get(id);
			if (entry == null) {
				entry = new GitEntry(id);
				this.map.put(id, entry);
			}
			entry.setAdded(true);
		}
		for (String id : status.getChanged()) {
			if (id.endsWith(".gitignore") || id.endsWith(".DS_Store")) {
				continue;
			}
			GitEntry entry = this.map.get(id);
			if (entry == null) {
				entry = new GitEntry(id);
				this.map.put(id, entry);
			}
			entry.setChanged(true);
		}
		for (String id : status.getConflicting()) {
			if (id.endsWith(".gitignore") || id.endsWith(".DS_Store")) {
				continue;
			}
			GitEntry entry = this.map.get(id);
			if (entry == null) {
				entry = new GitEntry(id);
				this.map.put(id, entry);
			}
			entry.setConflicting(true);
			//
			//
			//
			conflictCount++;
		}
		for (String id : status.getMissing()) {
			if (id.endsWith(".gitignore") || id.endsWith(".DS_Store")) {
				continue;
			}
			GitEntry entry = this.map.get(id);
			if (entry == null) {
				entry = new GitEntry(id);
				this.map.put(id, entry);
			}
			entry.setMissing(true);
		}
		for (String id : status.getModified()) {
			if (id.endsWith(".gitignore") || id.endsWith(".DS_Store")) {
				continue;
			}
			GitEntry entry = this.map.get(id);
			if (entry == null) {
				entry = new GitEntry(id);
				this.map.put(id, entry);
			}
			entry.setModified(true);
		}
		for (String id : status.getRemoved()) {
			if (id.endsWith(".gitignore") || id.endsWith(".DS_Store")) {
				continue;
			}
			GitEntry entry = this.map.get(id);
			if (entry == null) {
				entry = new GitEntry(id);
				this.map.put(id, entry);
			}
			entry.setRemoved(true);
		}
		for (String id : status.getUncommittedChanges()) {
			if (id.endsWith(".gitignore") || id.endsWith(".DS_Store")) {
				continue;
			}
			GitEntry entry = this.map.get(id);
			if (entry == null) {
				entry = new GitEntry(id);
				this.map.put(id, entry);
			}
			entry.setUncommitted(true);
		}
		for (String id : status.getUntracked()) {
			if (id.endsWith(".gitignore") || id.endsWith(".DS_Store")) {
				continue;
			}
			GitEntry entry = this.map.get(id);
			if (entry == null) {
				entry = new GitEntry(id);
				this.map.put(id, entry);
			}
			entry.setUntracked(true);
		}
		for (String id : status.getUntrackedFolders()) {
			if (id.endsWith(".gitignore") || id.endsWith(".DS_Store")) {
				continue;
			}
			GitEntry entry = this.map.get(id);
			if (entry == null) {
				entry = new GitEntry(id);
				this.map.put(id, entry);
			}
			entry.setUntrackedFolders(true);
		}
	}
	
	public Map<String, IndexDiff.StageState> getConflictingStageState() {
		return this.conflictingStage;
	}
	
	public int	getConflictCount() {
		return this.conflictCount;
	}

	@Override
	public Item getItem(Object itemId) {
		return new StatusItem(map.get(itemId));
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return GITSTATUS_PROPERTIES;
	}

	@Override
	public Collection<?> getItemIds() {
		final Collection<String> items = new ArrayList<String>();
		items.addAll(this.map.keySet());
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public Property<Object> getContainerProperty(Object itemId, Object propertyId) {
		GitEntry entry = this.map.get(itemId);
		if (entry == null) {
//			logger.error("unknown itemId: " + itemId);
			return null;
		}
        if (propertyId.equals(PROPERTY_NAME)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new StatusItem(entry), GITSTATUSITEM_NAME, null);
        }
        if (propertyId.equals(PROPERTY_STATUS)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new StatusItem(entry), GITSTATUSITEM_STATUS, null);
        }
        if (propertyId.equals(PROPERTY_ENTRY)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new StatusItem(entry), GITSTATUSITEM_ENTRY, null);
        }
		return null;
	}

	@Override
	public Class<?> getType(Object propertyId) {
        if (propertyId.equals(PROPERTY_NAME)) {
        	return String.class;
        }
        if (propertyId.equals(PROPERTY_STATUS)) {
        	return String.class;
        }
        if (propertyId.equals(PROPERTY_ENTRY)) {
        	return GitEntry.class;
        }
		return null;
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		return this.map.containsKey(itemId);
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't add items.");
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't add items.");
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't remove items.");
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't add properties.");
	}

	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't remove properties.");
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't remove items.");
	}

	@Override
	public Object nextItemId(Object itemId) {
		return this.map.higherKey((String) itemId);
	}

	@Override
	public Object prevItemId(Object itemId) {
		return this.map.lowerKey((String) itemId);
	}

	@Override
	public Object firstItemId() {
		return this.map.firstKey();
	}

	@Override
	public Object lastItemId() {
		return this.map.lastKey();
	}

	@Override
	public boolean isFirstId(Object itemId) {
		return itemId.equals(this.map.firstKey());
	}

	@Override
	public boolean isLastId(Object itemId) {
		return itemId.equals(this.map.lastKey());
	}

	@Override
	public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't add items.");
	}

	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't add items.");
	}

	public class StatusItem implements Item {
		private static final long serialVersionUID = 1L;
		private final GitEntry entry;
		
		public StatusItem(GitEntry itemId) {
			this.entry = itemId;
		}

		public String getName() {
			return this.entry.getName();
		}
		
		public String getStatus() {
			return this.entry.getStatus();
		}

		public GitEntry getGitEntry() {
			return this.entry;
		}

		@Override
		public Property<?> getItemProperty(Object id) {
            return getContainerProperty(this.entry, id);
		}

		@Override
		public Collection<?> getItemPropertyIds() {
			return getContainerPropertyIds();
		}

		@Override
		public boolean addItemProperty(Object id, @SuppressWarnings("rawtypes") Property property) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Container does not support adding new properties");
		}

		@Override
		public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Container does not support removing properties");
		}
	}
}
