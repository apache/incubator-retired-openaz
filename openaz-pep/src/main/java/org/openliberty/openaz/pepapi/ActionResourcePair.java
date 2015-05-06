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

package org.openliberty.openaz.pepapi;

/**
 * A convenient abstraction for an action - resource pair.
 */
public final class ActionResourcePair {

    private final Object action;

    private final Object resource;

    /**
     * Creates a new action - resource pair
     *
     * @param action an Object representing the action being performed.
     * @param resource an Object representing the resource on which the action is being performed.
     */
    public ActionResourcePair(Object action, Object resource) {
        this.resource = resource;
        this.action = action;
    }

    /**
     * Returns the resource associated with this action - resource pair
     *
     * @return an Object representing the resource.
     */
    public Object getResource() {
        return resource;
    }

    /**
     * Returns the action associated with this action - resource pair.
     *
     * @return an Object representing the action.
     */
    public Object getAction() {
        return action;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (action == null ? 0 : action.hashCode());
        result = prime * result + (resource == null ? 0 : resource.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ActionResourcePair other = (ActionResourcePair)obj;
        if (action == null && other.action != null) {
            return false;
        } else if (!action.equals(other.action)) {
            return false;
        }

        if (resource == null && other.resource != null) {
            return false;
        } else if (!resource.equals(other.resource)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nAction: " + action.toString());
        builder.append("\nResource: " + resource.toString());
        return builder.toString();
    }

}
