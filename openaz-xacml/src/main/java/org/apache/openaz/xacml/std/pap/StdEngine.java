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
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.std.pap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.pap.PAPEngine;
import org.apache.openaz.xacml.api.pap.PAPException;
import org.apache.openaz.xacml.api.pap.PDP;
import org.apache.openaz.xacml.api.pap.PDPGroup;
import org.apache.openaz.xacml.api.pap.PDPPolicy;
import org.apache.openaz.xacml.api.pap.PDPStatus;
import org.apache.openaz.xacml.util.XACMLProperties;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

/**
 * This is a simple PAP engine that uses some property files and a simple directory structure in the file
 * system to manage a policy repository and set of PDP nodes.
 */
public class StdEngine extends StdPDPItemSetChangeNotifier implements PAPEngine {
    private static Log logger = LogFactory.getLog(StdEngine.class);

    public static String PROP_PAP_REPO = "xacml.pap.pdps";
    public static String PROP_PAP_GROUPS = "xacml.pap.groups";
    public static String PROP_PAP_GROUPS_DEFAULT = "xacml.pap.groups.default";
    public static String PROP_PAP_GROUPS_DEFAULT_NAME = "default";

    protected final Path repository;
    protected Set<StdPDPGroup> groups;

    public StdEngine() throws PAPException, IOException {
        //
        // Get the location in the file system of our repository
        //
        this.repository = Paths.get(XACMLProperties.getProperty(PROP_PAP_REPO));
        //
        // Initialize
        //
        this.intialize();
    }

    public StdEngine(Properties properties) throws PAPException, IOException {
        //
        // Get the location in the file system of our repository
        //
        this.repository = Paths.get(properties.getProperty(PROP_PAP_REPO));
        //
        // Initialize
        //
        this.intialize();
    }

    public StdEngine(Path repository) throws PAPException, IOException {
        //
        // Save our location
        //
        this.repository = repository;
        //
        // Initialize
        //
        this.intialize();
    }

    private void intialize() throws PAPException, IOException {
        //
        // Sanity check the repository path
        //
        if (this.repository == null) {
            throw new PAPException("No repository specified.");
        }
        if (Files.notExists(this.repository)) {
            Files.createDirectory(repository);
        }
        if (!Files.isDirectory(this.repository)) {
            throw new PAPException("Repository is NOT a directory: " + this.repository.toAbsolutePath());
        }
        if (!Files.isWritable(this.repository)) {
            throw new PAPException("Repository is NOT writable: " + this.repository.toAbsolutePath());
        }
        //
        // Load our groups
        //
        this.loadGroups();
    }

    private void loadGroups() throws PAPException {
        //
        // Create a properties object
        //
        Properties properties = new Properties();
        Path file = Paths.get(this.repository.toString(), XACMLProperties.XACML_PROPERTIES_NAME);
        try {
            //
            // Load the properties
            //
            try (InputStream is = new FileInputStream(file.toFile())) {
                properties.load(is);
            }

            //
            // Parse it
            //
            this.groups = this.readProperties(this.repository, properties);
        } catch (IOException e) {
            logger.error("Failed to load " + file.toAbsolutePath().toString());
            this.groups = new HashSet<StdPDPGroup>();
        }
        //
        // Initialize the default group
        //
        PDPGroup defaultGroup = this.initializeDefaultGroup(file, properties);
        logger.info("Default group is: " + defaultGroup.getId() + "=" + defaultGroup.getName());
    }

    private PDPGroup initializeDefaultGroup(Path file, Properties properties) throws PAPException {
        //
        // Make sure we have the default group
        //
        PDPGroup group = this.getDefaultGroup();
        if (group != null) {
            return group;
        }
        //
        // We don't have the default group, create it
        //
        String defaultId = properties.getProperty(PROP_PAP_GROUPS_DEFAULT, PROP_PAP_GROUPS_DEFAULT_NAME);
        logger.warn("Default group does NOT exist, creating " + defaultId);
        Path defaultPath = Paths.get(this.repository.toString(), defaultId);
        try {
            //
            // Does it exist?
            //
            if (Files.notExists(defaultPath)) {
                //
                // Create its directory
                //
                Files.createDirectory(defaultPath);
                //
                // Create property files
                //
                {
                    Properties props = new Properties();
                    props.setProperty(XACMLProperties.PROP_REFERENCEDPOLICIES, "");
                    props.setProperty(XACMLProperties.PROP_ROOTPOLICIES, "");
                    Path policyPath = Paths.get(defaultPath.toAbsolutePath().toString(),
                                                "xacml.policy.properties");
                    Files.createFile(policyPath);
                    try (OutputStream os = Files.newOutputStream(policyPath)) {
                        props.store(os, "");
                    } catch (IOException e) {
                        logger.error("Failed to write default policy properties", e);
                    }
                }
                {
                    Properties props = new Properties();
                    props.setProperty(XACMLProperties.PROP_PIP_ENGINES, "");
                    Path pipPath = Paths.get(defaultPath.toAbsolutePath().toString(), "xacml.pip.properties");
                    Files.createFile(pipPath);
                    try (OutputStream os = Files.newOutputStream(pipPath)) {
                        props.store(os, "");
                    } catch (IOException e) {
                        logger.error("Failed to write default pip properties", e);
                    }
                }
            }
            //
            // Create the default group
            //
            StdPDPGroup newDefault = new StdPDPGroup(defaultId, true, "default",
                                                     "The default group where new PDP's are put.",
                                                     defaultPath);
            //
            // Add it to our list
            //
            this.groups.add(newDefault);
            //
            // Save our properties out since we have
            // a new default group.
            //
            StdEngine.setGroupProperties(newDefault, properties);
            //
            // Save it to disk
            //
            try {
                try (OutputStream os = Files.newOutputStream(file)) {
                    properties.store(os, "");
                }
            } catch (IOException e) {
                logger.error("Failed to save properties with new default group information.", e);
            }
            //
            // Return it
            //
            return newDefault;
        } catch (IOException e) {
            logger.error("Failed to create default group: " + defaultId, e);
            throw new PAPException("Failed to create default group");
        }
    }

    @Override
    public PDPGroup getDefaultGroup() throws PAPException {
        for (PDPGroup group : this.groups) {
            if (group.isDefaultGroup()) {
                return group;
            }
        }
        //
        // Default group doesn't exist
        //
        return null;
    }

    @Override
    public void SetDefaultGroup(PDPGroup group) throws PAPException {

        boolean changesMade = false;
        for (PDPGroup aGroup : groups) {
            if (aGroup.getId().equals(group.getId())) {
                if (!aGroup.isDefaultGroup()) {
                    // TODO - since the original code checked for type we do also.
                    if (aGroup instanceof StdPDPGroup) {
                        ((StdPDPGroup)aGroup).setDefault(true);
                        changesMade = true;
                    } else {
                        throw new IllegalArgumentException("Group in groups of unknown type '"
                                                           + aGroup.getClass().getName() + "'");
                    }
                }
            } else {
                // not the new default group
                if (aGroup.isDefaultGroup()) {
                    // TODO - since the original code checked for type we do also.
                    if (aGroup instanceof StdPDPGroup) {
                        ((StdPDPGroup)aGroup).setDefault(false);
                        changesMade = true;
                    } else {
                        throw new IllegalArgumentException("Group in groups of unknown type '"
                                                           + aGroup.getClass().getName() + "'");
                    }
                }
            }
        }
        if (changesMade) {
            this.doSave();
        }
    }

    @Override
    public Set<PDPGroup> getPDPGroups() throws PAPException {
        final Set<PDPGroup> grps = new HashSet<PDPGroup>();
        for (PDPGroup g : this.groups) {
            grps.add(g);
        }
        return Collections.unmodifiableSet(grps);
    }

    @Override
    public PDPGroup getGroup(String id) throws PAPException {
        for (PDPGroup g : this.groups) {
            if (g.getId().equals(id)) {
                return g;
            }
        }
        return null;
    }

    @Override
    public void newGroup(String name, String description) throws PAPException, NullPointerException {
        //
        // Null check
        //
        if (name == null) {
            throw new NullPointerException();
        }
        //
        // Do we already have this group?
        //
        for (PDPGroup group : this.groups) {
            if (group.getName().equals(name)) {
                throw new PAPException("Group with this name=" + name + " already exists.");
            }
        }

        // create an Id that can be used as a file name and a properties file key.
        // Ids must not contain \/:*?"<>|=,;
        // The ID must also be unique within the current set of PDPGroups.
        String id = createNewPDPGroupId(name);

        //
        // Construct the directory path
        //
        Path groupPath = Paths.get(this.repository.toString(), id);
        //
        // If it exists already
        //
        if (Files.exists(groupPath)) {
            logger.warn("addGroup " + id + " directory exists" + groupPath.toString());
        } else {
            try {
                //
                // Create the directory
                //
                Files.createDirectory(groupPath);
            } catch (IOException e) {
                logger.error("Failed to create " + groupPath);
                throw new PAPException("Failed to create " + id);
            }
        }
        //
        // Create the Policies
        //

        Path policyProperties = Paths.get(groupPath.toString(), "xacml.policy.properties");
        if (Files.exists(policyProperties)) {
            logger.warn("addGroup " + id + " file exists: " + policyProperties.toString());
        } else {
            Properties props = new Properties();
            props.setProperty(XACMLProperties.PROP_REFERENCEDPOLICIES, "");
            props.setProperty(XACMLProperties.PROP_ROOTPOLICIES, "");
            try {
                Files.createFile(policyProperties);
                try (OutputStream os = Files.newOutputStream(policyProperties)) {
                    props.store(os, "");
                }
            } catch (IOException e) {
                logger.error("Failed to create " + policyProperties);
                throw new PAPException("Failed to create " + id);
            }
        }
        //
        // Create the PIP config
        //
        Path pipProperties = Paths.get(groupPath.toString(), "xacml.pip.properties");
        if (Files.exists(pipProperties)) {
            logger.warn("addGroup " + id + " file exists: " + pipProperties.toString());
        } else {
            try {
                Properties props = new Properties();
                props.setProperty(XACMLProperties.PROP_PIP_ENGINES, "");
                Files.createFile(pipProperties);
                try (OutputStream os = Files.newOutputStream(pipProperties)) {
                    props.store(os, "");
                }
            } catch (IOException e) {
                logger.error("Failed to create " + pipProperties);
                throw new PAPException("Failed to create " + id);
            }

        }
        //
        // Ok now add it
        //
        StdPDPGroup newGroup = new StdPDPGroup(id, name, description, groupPath);
        if (this.groups.add(newGroup)) {
            // save the new group in our properties and notify any listeners of the change
            groupChanged(newGroup);
        }

    }

    /**
     * Helper to create a new Group ID. Use the Name field to create the Id. The Name is expected to not be
     * null; if it is then this method throws an exception. The name is supposed to be unique within the
     * current set of groups, so creating the ID based on the name will create a unique string.
     *
     * @param name
     * @return
     */
    private String createNewPDPGroupId(String name) {
        String id = name;
        // replace "bad" characters with sequences that will be ok for file names and properties keys.
        id = id.replace(" ", "_sp_");
        id = id.replace("\t", "_tab_");
        id = id.replace("\\", "_bksl_");
        id = id.replace("/", "_sl_");
        id = id.replace(":", "_col_");
        id = id.replace("*", "_ast_");
        id = id.replace("?", "_q_");
        id = id.replace("\"", "_quo_");
        id = id.replace("<", "_lt_");
        id = id.replace(">", "_gt_");
        id = id.replace("|", "_bar_");
        id = id.replace("=", "_eq_");
        id = id.replace(",", "_com_");
        id = id.replace(";", "_scom_");

        return id;
    }

    @Override
    public void updateGroup(PDPGroup group) throws PAPException {
        if (group == null || group.getId() == null) {
            throw new PAPException("Group or id is null");
        }
        if (group.getName() == null || group.getName().trim().length() == 0) {
            throw new PAPException("New name for group cannot be null or blank");
        }
        StdPDPGroup existingGroup = (StdPDPGroup)getGroup(group.getId());
        if (existingGroup == null) {
            throw new PAPException("Update found no existing group with id '" + group.getId() + "'");
        }

        // We do dramatically different things when the Name changes
        // because the Name is essentially the identity of the group (as the User knows it) so when the
        // Identity changes we have to change the group ID.
        if (group.getName().equals(existingGroup.getName())) {

            // update the disk
            try {
                ((StdPDPGroup)group).saveGroupConfiguration();
            } catch (IOException e) {
                throw new PAPException("Unable to save new configuration for '" + group.getName() + "': "
                                       + e.getMessage());
            }
            // update the group in the set by simply replacing the old instance with the new one
            this.groups.remove(existingGroup);
            this.groups.add((StdPDPGroup)group);

        } else {
            // the name/identity of the group has changed
            // generate the new id
            String newId = createNewPDPGroupId(group.getName());

            // make sure no other group uses the new id
            for (PDPGroup g : groups) {
                if (g.getId().equals(newId)) {
                    throw new PAPException("Replacement name maps to ID '" + newId
                                           + "' which is already in use");
                }
            }
            ((StdPDPGroup)group).setId(newId);

            // rename the existing directory to the new id
            Path oldPath = existingGroup.getDirectory();
            Path newPath = Paths.get(oldPath.getParent().toString(), newId);
            ((StdPDPGroup)group).setDirectory(newPath);

            try {
                boolean success = oldPath.toFile().renameTo(newPath.toFile());
                if (!success) {
                    throw new PAPException("Unable to rename directory; reason unknown");
                }
            } catch (Exception e) {
                logger.error("Move '" + oldPath + "' to '" + newPath + "': " + e.getMessage(), e);
                throw new PAPException("Unable to move directory from '" + oldPath + "' to '" + newPath
                                       + "': " + e.getMessage());
            }
            // update the disk
            try {
                ((StdPDPGroup)group).saveGroupConfiguration();
            } catch (IOException e) {
                throw new PAPException("Unable to save new configuration for '" + group.getName() + "': "
                                       + e.getMessage());
            }

            // save the new group into the Set
            groups.remove(existingGroup);
            groups.add((StdPDPGroup)group);

        }

        // perhaps only the group changed, but if the name/id changed it may look to a listener like more than
        // one group
        changed();

    }

    @Override
    public void removeGroup(PDPGroup group, PDPGroup newGroup) throws PAPException, NullPointerException {
        if (group == null) {
            throw new NullPointerException();
        }
        //
        // Does this group exist?
        //
        if (!this.groups.contains(group)) {
            logger.error("This group doesn't exist.");
            throw new PAPException("The group '" + group.getId() + "' does not exist");
        }
        //
        // Is it the default group?
        //
        if (group.isDefaultGroup()) {
            throw new PAPException("You cannot delete the default group.");
        }
        Set<PDP> pdps = group.getPdps();
        //
        // Are there PDPs? If so, then we need a target group
        //
        if (!pdps.isEmpty() && newGroup == null) {
            throw new NullPointerException(
                                           "Group targeted for deletion has PDPs, you must provide a new group for them.");
        }
        //
        // Move the PDPs
        //
        if (!pdps.isEmpty()) {
            if (!(newGroup instanceof StdPDPGroup)) {
                throw new PAPException("Unexpected class for newGroup: "
                                       + newGroup.getClass().getCanonicalName());
            }
            // The movePDP function will modify the set of PDPs in the group.
            // To avoid concurrent modification exceptions we need to duplicate the list before calling that
            // function.
            List<PDP> pdpList = new ArrayList<PDP>();
            for (PDP pdp : pdps) {
                pdpList.add(pdp);
            }
            // now we can use the PDPs from the list without having ConcurrentAccessExceptions
            for (PDP pdp : pdpList) {
                this.movePDP(pdp, newGroup);
            }
        }
        //
        // remove the directory for the group
        //
        String id = group.getId();
        Path groupPath = Paths.get(this.repository.toString(), id);
        //
        // If it exists already
        //
        if (!Files.exists(groupPath)) {
            logger.warn("removeGroup " + id + " directory does not exist" + groupPath.toString());
        } else {
            try {
                Files.walkFileTree(groupPath, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return super.visitFile(file, attrs);
                    }

                });
                //
                // delete the directory
                //
                Files.delete(groupPath);
            } catch (IOException e) {
                logger.error("Failed to delete " + groupPath + ": " + e);
                throw new PAPException("Failed to delete " + id);
            }
        }

        // remove the group from the set of all groups
        groups.remove(group);

        //
        // Save changes
        //
        changed();
        this.doSave();
    }

    @Override
    public PDPGroup getPDPGroup(PDP pdp) throws PAPException {
        for (PDPGroup group : this.groups) {
            if (group.getPdps().contains(pdp)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public PDPGroup getPDPGroup(String pdpId) throws PAPException {
        for (PDPGroup group : this.groups) {
            for (PDP pdp : group.getPdps()) {
                if (pdp.getId().equals(pdpId)) {
                    return group;
                }
            }
        }
        return null;
    }

    @Override
    public PDP getPDP(String pdpId) throws PAPException {
        for (PDPGroup group : this.groups) {
            for (PDP pdp : group.getPdps()) {
                if (pdp.getId().equals(pdpId)) {
                    return pdp;
                }
            }
        }
        return null;
    }

    @Override
    public void newPDP(String id, PDPGroup group, String name, String description) throws PAPException,
        NullPointerException {
        if (group == null) {
            throw new PAPException("You must specify which group the PDP will belong to.");
        }
        if (!this.groups.contains(group)) {
            throw new PAPException("Unknown group, not in our list.");
        }
        for (PDP p : group.getPdps()) {
            if (p.getId().equals(id)) {
                throw new PAPException("A PDP with this ID exists.");
            }
        }
        if (group instanceof StdPDPGroup) {
            StdPDP pdp = new StdPDP(id, name, description);
            if (((StdPDPGroup)group).addPDP(pdp)) {
                //
                // Save the properties and notify any listeners
                //
                pdpChanged(pdp);
                return;
            }
        }
    }

    @Override
    public void movePDP(PDP pdp, PDPGroup newGroup) throws PAPException {
        if (newGroup == null) {
            throw new NullPointerException("You must specify which group the PDP will belong to.");
        }
        PDPGroup currentGroup = this.getPDPGroup(pdp);
        if (currentGroup == null) {
            throw new PAPException("PDP must already belong to a group.");
        }
        if (currentGroup.equals(newGroup)) {
            logger.warn("Already in that group.");
            return;
        }
        if (currentGroup instanceof StdPDPGroup && newGroup instanceof StdPDPGroup) {
            if (((StdPDPGroup)currentGroup).removePDP(pdp)) {
                boolean result = ((StdPDPGroup)newGroup).addPDP(pdp);
                if (result) {
                    //
                    // Save the configuration
                    //
                    this.doSave();
                } else {
                    logger.error("Failed to add to new group, putting back into original group.");
                    if (!((StdPDPGroup)currentGroup).removePDP(pdp)) {
                        logger.error("Failed to put PDP back into original group.");
                    }
                }
            }
        } else {
            String message = "Unknown PDP group class: " + newGroup.getClass().getCanonicalName() + " and "
                             + currentGroup.getClass().getCanonicalName();
            logger.warn(message);
            throw new PAPException(message);
        }
    }

    @Override
    public void updatePDP(PDP pdp) throws PAPException {
        PDP currentPDP = this.getPDP(pdp.getId());
        if (currentPDP == null) {
            String message = "Unknown PDP id '" + pdp.getId() + "'";
            logger.warn(message);
            throw new PAPException(message);
        }

        // the only things that the user can change are name and description
        currentPDP.setDescription(pdp.getDescription());
        currentPDP.setName(pdp.getName());

        this.doSave();
    }

    @Override
    public void removePDP(PDP pdp) throws PAPException {
        PDPGroup group = this.getPDPGroup(pdp);
        if (group == null) {
            throw new NullPointerException();
        }
        if (group instanceof StdPDPGroup) {
            boolean result = ((StdPDPGroup)group).removePDP(pdp);
            if (result) {
                this.doSave();
            }
            return;
        }
        String message = "Unknown PDP group class: " + group.getClass().getCanonicalName();
        logger.warn(message);
        throw new PAPException(message);
    }

    @Override
    /**
     * Should never be called - Detailed status is held on the PDP, not the PAP
     */
    public PDPStatus getStatus(PDP pdp) throws PAPException {
        return getPDP(pdp.getId()).getStatus();
    }

    @Override
    public void publishPolicy(String id, String name, boolean isRoot, InputStream policy, PDPGroup group)
        throws PAPException {
        if (group == null) {
            throw new NullPointerException();
        }
        if (group instanceof StdPDPGroup && this.groups.contains(group)) {
            ((StdPDPGroup)group).publishPolicy(id, name, isRoot, policy);
            return;
        }
        logger.warn("unknown PDP Group: " + group);
        throw new PAPException("Unknown PDP Group: " + group.getId());
    }

    // Currently not used on the PAP side. This is done by ((StdPDPGroup) group).copyPolicyToFile
    @Override
    public void copyPolicy(PDPPolicy policy, PDPGroup group) throws PAPException {
    }

    @Override
    public void removePolicy(PDPPolicy policy, PDPGroup group) throws PAPException {
        if (group == null) {
            throw new NullPointerException();
        }
        if (group instanceof StdPDPGroup && this.groups.contains(group)) {
            ((StdPDPGroup)group).removePolicy(policy);
            return;
        }
        logger.warn("unknown PDP Group: " + group);
        throw new PAPException("Unknown PDP Group: " + group.getId());
    }

    //
    // HELPER methods
    //

    private Set<StdPDPGroup> readProperties(Path repository, Properties properties) throws PAPException {
        Set<StdPDPGroup> groups = new HashSet<StdPDPGroup>();
        //
        // See if there is a groups property
        //
        String groupList = properties.getProperty(PROP_PAP_GROUPS, "");
        if (groupList == null) {
            logger.warn("null group list " + PROP_PAP_GROUPS);
            groupList = "";
        }
        if (logger.isDebugEnabled()) {
            logger.debug("group list: " + groupList);
        }
        //
        // Iterate the groups, converting to a set ensures we have unique groups.
        //
        for (String id : Splitter.on(',').trimResults().omitEmptyStrings().split(groupList)) {
            //
            // Add our Group Object
            //
            StdPDPGroup g = new StdPDPGroup(id.trim(), id.equals(properties
                .getProperty(PROP_PAP_GROUPS_DEFAULT, PROP_PAP_GROUPS_DEFAULT_NAME)), properties,
                                            Paths.get(repository.toString(), id));

            //
            // Add it in
            //
            groups.add(g);
        }
        //
        // Dump what we got
        //
        if (logger.isDebugEnabled()) {
            logger.debug("PDP Group List: " + groups.toString());
        }
        return groups;
    }

    private void saveConfiguration() throws PAPException, IOException {
        //
        // Create our properties object
        //
        Properties properties = new Properties() {
            private static final long serialVersionUID = 1L;

            // For Debugging it is helpful for the file to be in a sorted order,
            // any by returning the keys in the natural Alpha order for strings we get close enough.
            // TreeSet is sorted, and this just overrides the normal Properties method to get the keys.
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super.keySet()));
            }
        };
        //
        // Iterate our groups
        //
        List<String> ids = new ArrayList<String>();
        for (PDPGroup group : this.groups) {
            ids.add(group.getId());
            properties.setProperty(group.getId() + ".name", (group.getName() == null ? "" : group.getName()));
            properties.setProperty(group.getId() + ".description", (group.getDescription() == null
                ? "" : group.getDescription()));
            //
            // Iterate its PDPs
            //
            List<String> pdps = new ArrayList<String>();
            for (PDP pdp : group.getPdps()) {
                pdps.add(pdp.getId());
                properties.setProperty(pdp.getId() + ".name", (pdp.getName() == null ? "" : pdp.getName()));
                properties.setProperty(pdp.getId() + ".description",
                                       (pdp.getDescription() == null ? "" : pdp.getDescription()));
            }
            String pdpList = "";
            if (pdps.size() == 1) {
                pdpList = pdps.get(0);
            } else if (pdps.size() > 1) {
                pdpList = Joiner.on(',').skipNulls().join(pdps);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Group " + group.getId() + " PDPS: " + pdpList);
            }
            properties.setProperty(group.getId() + ".pdps", pdpList);
        }
        if (ids.isEmpty()) {
            throw new PAPException("Inconsistency - we have NO groups. We should have at least one.");
        }
        String groupList = "";
        if (ids.size() == 1) {
            groupList = ids.get(0);
        } else if (ids.size() > 1) {
            groupList = Joiner.on(',').skipNulls().join(ids);
        }
        logger.info("New Group List: " + groupList);

        properties.setProperty(PROP_PAP_GROUPS, groupList);
        //
        // Get the default group
        //
        PDPGroup defaultGroup = this.getDefaultGroup();
        if (defaultGroup == null) {
            throw new PAPException("Invalid state - no default group.");
        }
        properties.setProperty(PROP_PAP_GROUPS_DEFAULT, defaultGroup.getId());
        //
        // Now we can save the file
        //
        Path file = Paths.get(this.repository.toString(), "xacml.properties");
        try (OutputStream os = Files.newOutputStream(file)) {
            properties.store(os, "");
        }
    }

    public static void removeGroupProperties(String id, Properties properties) {
        for (Object key : properties.keySet()) {
            if (key.toString().startsWith(id + ".")) {
                properties.remove(key);
            }
        }
    }

    public static void setGroupProperties(PDPGroup group, Properties properties) {
        //
        // make sure its in the list of groups
        //
        Iterable<String> groups = Splitter.on(',').trimResults().omitEmptyStrings()
            .split(properties.getProperty(PROP_PAP_GROUPS, ""));
        boolean inList = false;
        for (String g : groups) {
            if (g.equals(group.getId())) {
                inList = true;
            }
        }
        if (!inList) {
            Set<String> grps = Sets.newHashSet(groups);
            grps.add(group.getId());
            String newGroupList = "";
            
            if (grps.size() == 1) {
                newGroupList = grps.iterator().next();
            } else if (grps.size() > 1) {
                newGroupList = Joiner.on(',').skipNulls().join(grps);
            }
            logger.info("New Group List: " + newGroupList);
            properties.setProperty(PROP_PAP_GROUPS, newGroupList);
        }
        //
        // Set its properties
        //
        properties.setProperty(group.getId() + ".name", group.getName());
        properties.setProperty(group.getId() + ".description", group.getDescription());
        //
        // Set its PDP list
        //
        if (group.getPdps().size() > 0) {
            String pdpList = "";
            if (group.getPdps().size() == 1) {
                pdpList = group.getPdps().iterator().next().getId();
            } else if (group.getPdps().size() > 1) {
                Set<String> ids = new HashSet<String>();
                for (PDP pdp : group.getPdps()) {
                    ids.add(pdp.getId());
                }
                pdpList = Joiner.on(',').skipNulls().join(ids);
            }
            properties.setProperty(group.getId() + ".pdps", pdpList);
        } else {
            properties.setProperty(group.getId() + ".pdps", "");
        }
    }

    public void changed() {
        if (logger.isDebugEnabled()) {
            logger.debug("changed");
        }
        this.doSave();
        this.fireChanged();
    }

    public void groupChanged(PDPGroup group) {
        if (logger.isDebugEnabled()) {
            logger.debug("groupChanged: " + group);
        }
        this.doSave();
        this.firePDPGroupChanged(group);
    }

    public void pdpChanged(PDP pdp) {
        if (logger.isDebugEnabled()) {
            logger.debug("pdpChanged: " + pdp);
        }
        this.doSave();
        this.firePDPChanged(pdp);
    }

    private void doSave() {
        try {
            //
            // Save the configuration
            //
            this.saveConfiguration();
        } catch (IOException e) {
            logger.error("Failed to save configuration", e);
        } catch (PAPException e) {
            logger.error("Failed to save configuration", e);
        }
    }

}
