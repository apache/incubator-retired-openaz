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

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML3;

/**
 * Container class that maps attributes to predefined XACML AccessSubject category.
 */
public class Subject extends CategoryContainer {

    public static final Identifier DEFAULT_IDENTIFIER_ID = XACML3.ID_SUBJECT_SUBJECT_ID;
    private String idValue;

    private Subject() {
        super(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT);
    }

    /**
     * Creates a new Subject instance
     *
     * @return
     */
    public static Subject newInstance() {
        return new Subject();
    }

    /**
     * Creates a new Subject instance containing a single default attribute with the given String value.
     *
     * @param idValue
     * @return
     */
    public static Subject newInstance(String idValue) {
        return newInstance().withId(idValue);
    }

    /**
     * Sets the Id of the subject
     *
     * @param idValue
     * @return
     */
    public Subject withId(String idValue) {
        this.idValue = idValue;
        addAttribute(DEFAULT_IDENTIFIER_ID.stringValue(), idValue);
        return this;
    }

    /**
     * Sets the id of the subject and allows to set/override the default attribute key
     *
     * @param idKey
     * @param idValue
     * @return
     */
    public Subject withId(Identifier idKey, String idValue) {
        this.idValue = idValue;
        addAttribute(idKey.stringValue(), idValue);
        return this;
    }

    /**
     * Returns the value of the id
     *
     * @return
     */
    public String getId() {
        return idValue;
    }

}
