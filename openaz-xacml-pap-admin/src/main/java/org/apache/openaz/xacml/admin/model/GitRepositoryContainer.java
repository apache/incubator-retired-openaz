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
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.openaz.xacml.admin.model;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;

import org.apache.openaz.xacml.util.XACMLPolicyScanner;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.Resource;
import com.vaadin.ui.TextArea;
import com.vaadin.util.FileTypeResolver;

/**
 * A hierarchical container wrapper for a filesystem.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class GitRepositoryContainer extends ItemSetChangeNotifier implements Container.Hierarchical {
	private Log logger	= LogFactory.getLog(GitRepositoryContainer.class);
	
    /**
     * String identifier of a file's "name" property.
     */
    public static String PROPERTY_NAME = "Name";

    /**
     * String identifier of a file's "size" property.
     */
    public static String PROPERTY_SIZE = "Size";

    /**
     * String identifier of a file's "icon" property.
     */
    public static String PROPERTY_ICON = "Icon";

    /**
     * String identifier of a file's "last modified" property.
     */
    public static String PROPERTY_LASTMODIFIED = "Last Modified";

    /**
     * String identifier of a file's "version" property.
     */
    public static String PROPERTY_VERSION = "Version";
    
    /**
     * String identifier of a file's "status" property.
     */
    public static String PROPERTY_STATUS = "Status";
    
    /**
     * String identifier of a file's "data" property.
     */
    public static String PROPERTY_DATA = "Data";
 
    /**
     * List of the string identifiers for the available properties.
     */
    public static Collection<String> FILE_PROPERTIES;

    private final static Method FILEITEM_LASTMODIFIED;

    private final static Method FILEITEM_NAME;

    private final static Method FILEITEM_ICON;

    private final static Method FILEITEM_SIZE;
    
    private final static Method FILEITEM_VERSION;
    
    private final static Method FILEITEM_STATUS;
    
    private final static Method FILEITEM_DATA;

    static {

        FILE_PROPERTIES = new ArrayList<String>();
        FILE_PROPERTIES.add(PROPERTY_NAME);
        FILE_PROPERTIES.add(PROPERTY_ICON);
        FILE_PROPERTIES.add(PROPERTY_SIZE);
        FILE_PROPERTIES.add(PROPERTY_LASTMODIFIED);
        FILE_PROPERTIES.add(PROPERTY_VERSION);
        FILE_PROPERTIES.add(PROPERTY_STATUS);
        FILE_PROPERTIES.add(PROPERTY_DATA);
        FILE_PROPERTIES = Collections.unmodifiableCollection(FILE_PROPERTIES);
        try {
        	FILEITEM_VERSION = FileItem.class.getMethod("getVersion", new Class[]{});
            FILEITEM_LASTMODIFIED = FileItem.class.getMethod("lastModified", new Class[] {});
            FILEITEM_NAME = FileItem.class.getMethod("getName", new Class[] {});
            FILEITEM_ICON = FileItem.class.getMethod("getIcon", new Class[] {});
            FILEITEM_SIZE = FileItem.class.getMethod("getSize", new Class[] {});
            FILEITEM_STATUS = FileItem.class.getMethod("getStatus", new Class[] {});
            FILEITEM_DATA = FileItem.class.getMethod("getData", new Class[] {});
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(
                    "Internal error finding methods in FilesystemContainer");
        }
    }

    private File[] roots = new File[] {};
    
    private FilenameFilter filter = null;

    private boolean recursive = true;
    
    private Path repository = null;

    /**
     * Constructs a new <code>FileSystemContainer</code> with the specified file
     * as the root of the filesystem. The files are included recursively.
     * 
     * @param root
     *            the root file for the new file-system container. Null values
     *            are ignored.
     */
    public GitRepositoryContainer(Path repository, File root) {
    	super();
    	this.repository = repository;
        if (root != null) {
            roots = new File[] { root };
        }
        this.setContainer(this);
    }

    /**
     * Constructs a new <code>FileSystemContainer</code> with the specified file
     * as the root of the filesystem. The files are included recursively.
     * 
     * @param root
     *            the root file for the new file-system container.
     * @param recursive
     *            should the container recursively contain subdirectories.
     */
    public GitRepositoryContainer(Path repository, File root, boolean recursive) {
        this(repository, root);
        setRecursive(recursive);
        this.setContainer(this);
   }

    /**
     * Constructs a new <code>FileSystemContainer</code> with the specified file
     * as the root of the filesystem.
     * 
     * @param root
     *            the root file for the new file-system container.
     * @param extension
     *            the Filename extension (w/o separator) to limit the files in
     *            container.
     * @param recursive
     *            should the container recursively contain subdirectories.
     */
    public GitRepositoryContainer(Path repository, File root, String extension, boolean recursive) {
        this(repository, root);
        this.setFilter(extension);
        setRecursive(recursive);
        this.setContainer(this);
    }

    /**
     * Constructs a new <code>FileSystemContainer</code> with the specified root
     * and recursivity status.
     * 
     * @param root
     *            the root file for the new file-system container.
     * @param filter
     *            the Filename filter to limit the files in container.
     * @param recursive
     *            should the container recursively contain subdirectories.
     */
    public GitRepositoryContainer(Path repository, File root, FilenameFilter filter, boolean recursive) {
        this(repository, root);
        this.setFilter(filter);
        setRecursive(recursive);
    }

    /**
     * Adds new root file directory. Adds a file to be included as root file
     * directory in the <code>FilesystemContainer</code>.
     * 
     * @param root
     *            the File to be added as root directory. Null values are
     *            ignored.
     */
    public void addRoot(File root) {
        if (root != null) {
            final File[] newRoots = new File[roots.length + 1];
            for (int i = 0; i < roots.length; i++) {
                newRoots[i] = roots[i];
            }
            newRoots[roots.length] = root;
            roots = newRoots;
        }
    }

    /**
     * Tests if the specified Item in the container may have children. Since a
     * <code>FileSystemContainer</code> contains files and directories, this
     * method returns <code>true</code> for directory Items only.
     * 
     * @param itemId
     *            the id of the item.
     * @return <code>true</code> if the specified Item is a directory,
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean areChildrenAllowed(Object itemId) {
    	if (logger.isTraceEnabled()) {
    		logger.trace("areChildrenAllowed: " + ((File)itemId).hashCode() + " " + ((File)itemId).getName());
    	}
        return itemId instanceof File && ((File) itemId).canRead()
                && ((File) itemId).isDirectory();
    }

    /*
     * Gets the ID's of all Items who are children of the specified Item. Don't
     * add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    @Override
    public Collection<File> getChildren(Object itemId) {

        if (!(itemId instanceof File)) {
            return Collections.unmodifiableCollection(new LinkedList<File>());
        }
        File[] f;
        if (filter != null) {
            f = ((File) itemId).listFiles(filter);
        } else {
            f = ((File) itemId).listFiles();
        }

        if (f == null) {
            return Collections.unmodifiableCollection(new LinkedList<File>());
        }

        final List<File> l = Arrays.asList(f);
        Collections.sort(l);

        return Collections.unmodifiableCollection(l);
    }

    /*
     * Gets the parent item of the specified Item. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    @Override
    public Object getParent(Object itemId) {
    	if (logger.isTraceEnabled()) {
    		logger.trace("getParent: " + ((File)itemId).hashCode() + " " + ((File)itemId).getName());
    	}

        if (!(itemId instanceof File)) {
            return null;
        }
        return ((File) itemId).getParentFile();
    }

    /*
     * Tests if the specified Item has any children. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    @Override
    public boolean hasChildren(Object itemId) {
    	if (logger.isTraceEnabled()) {
    		logger.trace("hasChildren: " + ((File)itemId).hashCode() + " " + ((File)itemId).getName());
    	}

        if (!(itemId instanceof File)) {
            return false;
        }
        String[] l;
        if (filter != null) {
            l = ((File) itemId).list(filter);
        } else {
            l = ((File) itemId).list();
        }
        return l != null && l.length > 0;
    }

    /*
     * Tests if the specified Item is the root of the filesystem. Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public boolean isRoot(Object itemId) {
    	if (logger.isTraceEnabled()) {
    		logger.trace("isRoot: " + ((File)itemId).hashCode() + " " + ((File)itemId).getName());
    	}

        if (!(itemId instanceof File)) {
            return false;
        }
        for (int i = 0; i < roots.length; i++) {
            if (roots[i].equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Gets the ID's of all root Items in the container. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Collection<File> rootItemIds() {

        File[] f;

        // in single root case we use children
        if (roots.length == 1) {
            if (filter != null) {
                f = roots[0].listFiles(filter);
            } else {
                f = roots[0].listFiles();
            }
        } else {
            f = roots;
        }

        if (f == null) {
            return Collections.unmodifiableCollection(new LinkedList<File>());
        }

        final List<File> l = Arrays.asList(f);
        Collections.sort(l);

        return Collections.unmodifiableCollection(l);
    }

    /**
     * Returns <code>false</code> when conversion from files to directories is
     * not supported.
     * 
     * @param itemId
     *            the ID of the item.
     * @param areChildrenAllowed
     *            the boolean value specifying if the Item can have children or
     *            not.
     * @return <code>true</code> if the operaton is successful otherwise
     *         <code>false</code>.
     * @throws UnsupportedOperationException
     *             if the setChildrenAllowed is not supported.
     */
    @Override
    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
            throws UnsupportedOperationException {

        throw new UnsupportedOperationException(
                "Conversion file to/from directory is not supported");
    }

    /**
     * Returns <code>false</code> when moving files around in the filesystem is
     * not supported.
     * 
     * @param itemId
     *            the ID of the item.
     * @param newParentId
     *            the ID of the Item that's to be the new parent of the Item
     *            identified with itemId.
     * @return <code>true</code> if the operation is successful otherwise
     *         <code>false</code>.
     * @throws UnsupportedOperationException
     *             if the setParent is not supported.
     */
    @Override
    public boolean setParent(Object itemId, Object newParentId)
            throws UnsupportedOperationException {
    	if (logger.isTraceEnabled()) {
    		logger.trace("setParent: " + 
    							((File)itemId).hashCode() + " " + 
    							((File)itemId).getName() + " to: " + 
    							((File)newParentId).hashCode() + " " + 
    							((File)newParentId).getName());
    	}
    	
    	Path path = Paths.get(((File) itemId).getAbsolutePath());
    	Path parent = Paths.get(((File) newParentId).getAbsolutePath());
    	boolean ok = path.getParent() == parent;
    	
    	if (ok) {
    		fireItemSetChange();
    	}
    	return ok;
    }

    /*
     * Tests if the filesystem contains the specified Item. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public boolean containsId(Object itemId) {
    	if (logger.isTraceEnabled()) {
    		logger.trace("containsId: " + ((File)itemId).hashCode() + " " + ((File)itemId).getName());
    	}

        if (!(itemId instanceof File)) {
            return false;
        }
        boolean val = false;

        // Try to match all roots
        for (int i = 0; i < roots.length; i++) {
            try {
                val |= ((File) itemId).getCanonicalPath().startsWith(
                        roots[i].getCanonicalPath());
            } catch (final IOException e) { //NOPMD
                // Exception ignored
            }

        }
        if (val && filter != null) {
            val &= filter.accept(((File) itemId).getParentFile(),
                    ((File) itemId).getName());
        }
        return val;
    }

    /*
     * Gets the specified Item from the filesystem. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    @Override
    public Item getItem(Object itemId) {

    	if (logger.isTraceEnabled()) {
    		logger.trace("getItem: " + ((File)itemId).hashCode() + " " + ((File)itemId).getName());
    	}

    	if (!(itemId instanceof File)) {
            return null;
        }
        return new FileItem((File) itemId);
    }
    
    public Item updateItem(Object itemId) {
    	if (logger.isTraceEnabled()) {
    		logger.trace("updateItem: " + ((File)itemId).hashCode() + " " + ((File)itemId).getName());
    	}

    	if (!(itemId instanceof File)) {
            return null;
        }
    	
    	this.fireItemSetChange();
    	
        return new FileItem((File) itemId);
    }

    /**
     * Internal recursive method to add the files under the specified directory
     * to the collection.
     * 
     * @param col
     *            the collection where the found items are added
     * @param f
     *            the root file where to start adding files
     */
    private void addItemIds(Collection<File> col, File f) {
        File[] l;
        if (filter != null) {
            l = f.listFiles(filter);
        } else {
            l = f.listFiles();
        }
        if (l == null) {
            // File.listFiles returns null if File does not exist or if there
            // was an IO error (permission denied)
            return;
        }
        final List<File> ll = Arrays.asList(l);
        Collections.sort(ll);

        for (final Iterator<File> i = ll.iterator(); i.hasNext();) {
            final File lf = i.next();
            col.add(lf);
            if (lf.isDirectory()) {
                addItemIds(col, lf);
            }
        }
    }

    /*
     * Gets the IDs of Items in the filesystem. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    @Override
    public Collection<File> getItemIds() {

        if (recursive) {
            final Collection<File> col = new ArrayList<File>();
            for (int i = 0; i < roots.length; i++) {
                addItemIds(col, roots[i]);
            }
            return Collections.unmodifiableCollection(col);
        } else {
            File[] f;
            if (roots.length == 1) {
                if (filter != null) {
                    f = roots[0].listFiles(filter);
                } else {
                    f = roots[0].listFiles();
                }
            } else {
                f = roots;
            }

            if (f == null) {
                return Collections
                        .unmodifiableCollection(new LinkedList<File>());
            }

            final List<File> l = Arrays.asList(f);
            Collections.sort(l);
            return Collections.unmodifiableCollection(l);
        }

    }

    /**
     * Gets the specified property of the specified file Item. The available
     * file properties are "Name", "Size" and "Last Modified". If propertyId is
     * not one of those, <code>null</code> is returned.
     * 
     * @param itemId
     *            the ID of the file whose property is requested.
     * @param propertyId
     *            the property's ID.
     * @return the requested property's value, or <code>null</code>
     */
    @Override
    public Property<?> getContainerProperty(Object itemId, Object propertyId) {

        if (!(itemId instanceof File)) {
            return null;
        }

        if (propertyId.equals(PROPERTY_NAME)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new FileItem((File) itemId), FILEITEM_NAME, null);
        }

        if (propertyId.equals(PROPERTY_ICON)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new FileItem((File) itemId), FILEITEM_ICON, null);
        }

        if (propertyId.equals(PROPERTY_SIZE)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new FileItem((File) itemId), FILEITEM_SIZE, null);
        }

        if (propertyId.equals(PROPERTY_LASTMODIFIED)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new FileItem((File) itemId), FILEITEM_LASTMODIFIED, null);
        }
        
        if (propertyId.equals(PROPERTY_VERSION)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new FileItem((File) itemId), FILEITEM_VERSION, null);
        }

        if (propertyId.equals(PROPERTY_STATUS)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new FileItem((File) itemId), FILEITEM_STATUS, null);
        }

        if (propertyId.equals(PROPERTY_DATA)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new FileItem((File) itemId), FILEITEM_DATA, null);
        }

        return null;
    }

    /**
     * Gets the collection of available file properties.
     * 
     * @return Unmodifiable collection containing all available file properties.
     */
    @Override
    public Collection<String> getContainerPropertyIds() {
        return FILE_PROPERTIES;
    }

    /**
     * Gets the specified property's data type. "Name" is a <code>String</code>,
     * "Size" is a <code>Long</code>, "Last Modified" is a <code>Date</code>. If
     * propertyId is not one of those, <code>null</code> is returned.
     * 
     * @param propertyId
     *            the ID of the property whose type is requested.
     * @return data type of the requested property, or <code>null</code>
     */
    @Override
    public Class<?> getType(Object propertyId) {

        if (propertyId.equals(PROPERTY_NAME)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_ICON)) {
            return Resource.class;
        }
        if (propertyId.equals(PROPERTY_SIZE)) {
            return Long.class;
        }
        if (propertyId.equals(PROPERTY_LASTMODIFIED)) {
            return Date.class;
        }
        if (propertyId.equals(PROPERTY_VERSION)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_STATUS)) {
        	return TextArea.class;
        }
        if (propertyId.equals(PROPERTY_DATA)) {
        	return Object.class;
        }
        return null;
    }

    /**
     * Internal method to recursively calculate the number of files under a root
     * directory.
     * 
     * @param f
     *            the root to start counting from.
     */
    private int getFileCounts(File f) {
        File[] l;
        if (filter != null) {
            l = f.listFiles(filter);
        } else {
            l = f.listFiles();
        }

        if (l == null) {
            return 0;
        }
        int ret = l.length;
        for (int i = 0; i < l.length; i++) {
            if (l[i].isDirectory()) {
                ret += getFileCounts(l[i]);
            }
        }
        return ret;
    }

    /**
     * Gets the number of Items in the container. In effect, this is the
     * combined amount of files and directories.
     * 
     * @return Number of Items in the container.
     */
    @Override
    public int size() {

        if (recursive) {
            int counts = 0;
            for (int i = 0; i < roots.length; i++) {
                counts += getFileCounts(roots[i]);
            }
            return counts;
        } else {
            File[] f;
            if (roots.length == 1) {
                if (filter != null) {
                    f = roots[0].listFiles(filter);
                } else {
                    f = roots[0].listFiles();
                }
            } else {
                f = roots;
            }

            if (f == null) {
                return 0;
            }
            return f.length;
        }
    }
    

    /**
     * A Item wrapper for files in a filesystem.
     * 
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public class FileItem implements Item {

        /**
         * The wrapped file.
         */
        private final File file;
        
        private Object data = null;

        /**
         * Constructs a FileItem from a existing file.
         */
        private FileItem(File file) {
        	if (logger.isTraceEnabled()) {
        		logger.trace("FileItem constructor: " + file.hashCode() + " " + file.getName());
        	}
            this.file = file;
        }

        /*
         * Gets the specified property of this file. Don't add a JavaDoc comment
         * here, we use the default documentation from implemented interface.
         */
        @Override
        public Property<?> getItemProperty(Object id) {
            return getContainerProperty(file, id);
        }

        /*
         * Gets the IDs of all properties available for this item Don't add a
         * JavaDoc comment here, we use the default documentation from
         * implemented interface.
         */
        @Override
        public Collection<String> getItemPropertyIds() {
            return getContainerPropertyIds();
        }

        /**
         * Calculates a integer hash-code for the Property that's unique inside
         * the Item containing the Property. Two different Properties inside the
         * same Item contained in the same list always have different
         * hash-codes, though Properties in different Items may have identical
         * hash-codes.
         * 
         * @return A locally unique hash-code as integer
         */
        @Override
        public int hashCode() {
            return file.hashCode() ^ GitRepositoryContainer.this.hashCode();
        }

        /**
         * Tests if the given object is the same as the this object. Two
         * Properties got from an Item with the same ID are equal.
         * 
         * @param obj
         *            an object to compare with this object.
         * @return <code>true</code> if the given object is the same as this
         *         object, <code>false</code> if not
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof FileItem)) {
                return false;
            }
            final FileItem fi = (FileItem) obj;
            return fi.getHost() == getHost() && fi.file.equals(file);
        }

        /**
         * Gets the host of this file.
         */
        private GitRepositoryContainer getHost() {
            return GitRepositoryContainer.this;
        }
        
        /**
         * Gets the file's version
         * 
         * @return Integer
         */
        
        public String getVersion() {
        	/*
        	 * If its a directory, there is no version
        	 */
        	if (this.file.isDirectory()) {
        		return null;
        	}
        	try {
				return XACMLPolicyScanner.getVersion(Paths.get(this.file.getAbsolutePath()));
			} catch (IOException e) {
				logger.error("Could not get version: " + e);
				return "n/a";
			}
        }

        /**
         * Gets the last modified date of this file.
         * 
         * @return Date
         */
        public Date lastModified() {
            return new Date(file.lastModified());
        }

        /**
         * Gets the name of this file.
         * 
         * @return file name of this file.
         */
        public String getName() {
            return file.getName();
        }
        
        public File getFile() {
        	return file;
        }

        /**
         * Gets the icon of this file.
         * 
         * @return the icon of this file.
         */
        public Resource getIcon() {
            return FileTypeResolver.getIcon(file);
        }

        /**
         * Gets the size of this file.
         * 
         * @return size
         */
        public Long getSize() {
            if (file.isDirectory()) {
                return null;
            }
            return file.length();
        }

        /**
         * Gets the status of this file.
         * 
         * @return status of this file.
         */
        public TextArea getStatus() {
        	TextArea area = null;
        	try {
        		Path repoPath = this.getHost().repository;
				Git git = Git.open(repoPath.toFile());
				
				//
				// I would like to use absolutePath, but that seems to barf when
				// we try to relativize this if a full path is not given.
				//
				Path relativePath = repoPath.relativize(Paths.get(this.file.getPath()));
				
				Status status = git.status().addPath(relativePath.toString()).call();
				if (logger.isDebugEnabled()) {
					logger.debug(this.file.getAbsolutePath());
					logger.debug("Added: " + status.getAdded());
					logger.debug("Changed: " + status.getChanged());
					logger.debug("Conflicting: " + status.getConflicting());
					logger.debug("Missing: " + status.getMissing());
					logger.debug("Modified: " + status.getModified());
					logger.debug("Removed: " + status.getRemoved());
					logger.debug("Uncommitted: " + status.getUncommittedChanges());
					logger.debug("Untracked: " + status.getUntracked());
					logger.debug("Untracked folders; " + status.getUntrackedFolders());
				}
				//
				// Are we a file or directory?
				//
				StringBuffer buffer = new StringBuffer();
				int length = 0;
				if (this.file.isFile()) {
					if (status.getAdded().contains(relativePath.toString())) {
						buffer.append("Added" + "\n");
						length++;
					}
					if (status.getChanged().contains(relativePath.toString())) {
						buffer.append("Changed" + "\n");
						length++;
					}
					if (status.getConflicting().contains(relativePath.toString())) {
						buffer.append("Conflicting" + "\n");
						length++;
					}
					if (status.getMissing().contains(relativePath.toString())) {
						buffer.append("Missing" + "\n");
						length++;
					}
					if (status.getModified().contains(relativePath.toString())) {
						buffer.append("Modified" + "\n");
						length++;
					}
					if (status.getRemoved().contains(relativePath.toString())) {
						buffer.append("Removed" + "\n");
						length++;
					}
					if (status.getUncommittedChanges().contains(relativePath.toString())) {
						buffer.append("Uncommitted" + "\n");
						length++;
					}
					if (status.getUntracked().contains(relativePath.toString())) {
						buffer.append("Untracked (New)" + "\n");
						length++;
					}
					if (status.getUntrackedFolders().contains(relativePath.toString())) {
						buffer.append("Untracked Folders (New)" + "\n");
						length++;
					}
				} else if (this.file.isDirectory()) {
					if (status.getUntracked().size() > 0) {
						buffer.append("Untracked (New)" + "\n");
						length++;
					}
					if (status.getUntrackedFolders().size() > 0) {
						buffer.append("Untracked Folders (New)" + "\n");
						length++;
					}
				}
				if (length > 0) {
					area = new TextArea();
					area.setValue(buffer.toString().trim());
					area.setWidth("100.0%");
					area.setRows(length);
					area.setReadOnly(true);
				}
			} catch (IOException | NoWorkTreeException | GitAPIException e) {
				logger.error(e);
			}
            return area;
        }

        /**
         * Gets the file's data
         * 
         * @return file data
         */
        public Object getData() {
        	return this.data;
        }
        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
//            if ("".equals(file.getName())) {
 //               return file.getAbsolutePath();
  //          }
            return file.getName();
        }

        /**
         * Filesystem container does not support adding new properties.
         * 
         * @see com.vaadin.data.Item#addItemProperty(Object, Property)
         */
        @Override
        public boolean addItemProperty(Object id, @SuppressWarnings("rawtypes") Property property) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Filesystem container does not support adding new properties");
        }

        /**
         * Filesystem container does not support removing properties.
         * 
         * @see com.vaadin.data.Item#removeItemProperty(Object)
         */
        @Override
        public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Filesystem container does not support property removal");
        }
    }

    /**
     * Generic file extension filter for displaying only files having certain
     * extension.
     * 
     * @author Vaadin Ltd.
     * @since 3.0
     */
    public class FileExtensionFilter implements FilenameFilter, Serializable {

        private final String filter;

        /**
         * Constructs a new FileExtensionFilter using given extension.
         * 
         * @param fileExtension
         *            the File extension without the separator (dot).
         */
        public FileExtensionFilter(String fileExtension) {
            filter = "." + fileExtension;
        }

        /**
         * Allows only files with the extension and directories.
         * 
         * @see java.io.FilenameFilter#accept(File, String)
         */
        @Override
        public boolean accept(File dir, String name) {
            if (name.endsWith(filter)) {
                return true;
            }
            return new File(dir, name).isDirectory();
        }

    }

    /**
     * Returns the file filter used to limit the files in this container.
     * 
     * @return Used filter instance or null if no filter is assigned.
     */
    public FilenameFilter getFilter() {
        return filter;
    }

    /**
     * Sets the file filter used to limit the files in this container.
     * 
     * @param filter
     *            The filter to set. <code>null</code> disables filtering.
     */
    public void setFilter(FilenameFilter filter) {
        this.filter = filter;
    }

    /**
     * Sets the file filter used to limit the files in this container.
     * 
     * @param extension
     *            the Filename extension (w/o separator) to limit the files in
     *            container.
     */
    public void setFilter(String extension) {
        filter = new FileExtensionFilter(extension);
    }

    /**
     * Is this container recursive filesystem.
     * 
     * @return <code>true</code> if container is recursive, <code>false</code>
     *         otherwise.
     */
    public boolean isRecursive() {
        return recursive;
    }

    /**
     * Sets the container recursive property. Set this to false to limit the
     * files directly under the root file.
     * <p>
     * Note : This is meaningful only if the root really is a directory.
     * </p>
     * 
     * @param recursive
     *            the New value for recursive property.
     */
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#addContainerProperty(java.lang.Object,
     * java.lang.Class, java.lang.Object)
     */
    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "File system container does not support this operation");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#addItem()
     */
    @Override
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Git repository container does not support this operation");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#addItem(java.lang.Object)
     */
    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
    	if (! (itemId instanceof File)) {
            throw new UnsupportedOperationException(
                    "Git repository container does not support this operation for Objects that are not files.");
    	}
    	if (logger.isTraceEnabled()) {
    		logger.trace("addItem: " + ((File)itemId).hashCode() + " " + ((File)itemId).getName());
    	}

    	fireItemSetChange();
  	
        return new FileItem((File) itemId);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeAllItems()
     */
    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "File system container does not support this operation");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeItem(java.lang.Object)
     */
    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
    	
    	if (logger.isTraceEnabled()) {
    		logger.trace("removeItem: " + ((File)itemId).hashCode() + " " + ((File)itemId).getName());
    	}

    	fireItemSetChange();
    	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeContainerProperty(java.lang.Object )
     */
    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "File system container does not support this operation");
    }

	public Object getRoot(int index) {
		if (index >= this.roots.length) {
			return null;
		}
		return this.roots[index];
	}
}
