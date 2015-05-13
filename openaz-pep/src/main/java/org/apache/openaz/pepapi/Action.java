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

package org.apache.openaz.pepapi;

import org.apache.openaz.xacml.api.XACML3;

;

/**
 * Container class that maps attributes to predefined XACML Action category.
 */
public class Action extends CategoryContainer {

    public static final String ACTION_ID_KEY = "ACTION_ID_KEY";

    private String actionIdValue;

    private Action() {
        super(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION);
    }

    /**
     * Creates a new Action instance
     *
     * @return
     */
    public static Action newInstance() {
        return new Action();
    }

    /**
     * Create a new Action instance containing a single default attribute with the given value
     *
     * @param actionIdValue
     * @return
     */
    public static Action newInstance(String actionIdValue) {
        Action a = new Action();
        a.actionIdValue = actionIdValue;
        a.addAttribute(ACTION_ID_KEY, actionIdValue);
        return a;
    }

    /**
     * Get the value for default attribute.
     *
     * @return
     */
    public String getActionIdValue() {
        return actionIdValue;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("action-id value: " + actionIdValue);
        builder.append("\n");
        builder.append(super.toString());
        return builder.toString();
    }
}
