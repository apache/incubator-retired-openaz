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
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.std.pap;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.openaz.xacml.api.pap.PDP;
import org.apache.openaz.xacml.api.pap.PDPGroup;

public class StdPDPItemSetChangeNotifier {

    private Collection<StdItemSetChangeListener> listeners;

    public interface StdItemSetChangeListener {

        void changed();

        void groupChanged(PDPGroup group);

        void pdpChanged(PDP pdp);
    }

    public void addItemSetChangeListener(StdItemSetChangeListener listener) {
        if (this.listeners == null) {
            this.listeners = new LinkedList<StdItemSetChangeListener>();
        }
        this.listeners.add(listener);
    }

    public void removeItemSetChangeListener(StdItemSetChangeListener listener) {
        if (this.listeners != null) {
            this.listeners.remove(listener);
        }
    }

    public void fireChanged() {
        if (this.listeners == null) {
            return;
        }
        for (StdItemSetChangeListener l : this.listeners) {
            l.changed();
        }
    }

    public void firePDPGroupChanged(PDPGroup group) {
        if (this.listeners == null) {
            return;
        }
        for (StdItemSetChangeListener l : this.listeners) {
            l.groupChanged(group);
        }
    }

    public void firePDPChanged(PDP pdp) {
        if (this.listeners == null) {
            return;
        }
        for (StdItemSetChangeListener l : this.listeners) {
            l.pdpChanged(pdp);
        }
    }
}
