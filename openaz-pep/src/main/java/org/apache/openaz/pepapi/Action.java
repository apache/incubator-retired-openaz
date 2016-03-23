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

/**
 * Container class that maps attributes to predefined XACML Action category.
 */
public class Action extends CategoryContainer {

    private String id;

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
     * Creates a new Action instance with id
     *
     * @param id
     * @return
     */
    public static Action newInstance(String id) {
        Action a = newInstance().withId(id);
        a.addAttribute(XACML3.ID_ACTION_ACTION_ID.stringValue(), id);
        return a;
    }

    /**
     *
     * @param id
     * @return
     */
    public Action withId(String id) {
        this.id = id;
        return this;
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

}
