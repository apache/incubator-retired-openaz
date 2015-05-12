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

package org.apache.openaz.pepapi.std.test.mapper;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {

    private String id;

    private List<String> accessUserGroup;

    public MedicalRecord(String id) {
        this.id = id;
        accessUserGroup = new ArrayList<String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAccessUserGroup() {
        return accessUserGroup;
    }

    public void setAccessUserGroup(List<String> accessUserGroup) {
        this.accessUserGroup = accessUserGroup;
    }

    public void addUserToAccessGroup(String user) {
        this.accessUserGroup.add(user);
    }

}
