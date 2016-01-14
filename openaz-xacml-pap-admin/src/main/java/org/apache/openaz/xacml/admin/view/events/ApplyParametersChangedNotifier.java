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

package org.apache.openaz.xacml.admin.view.events;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.openaz.xacml.admin.jpa.FunctionArgument;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;

public interface ApplyParametersChangedNotifier {

	boolean	addListener(ApplyParametersChangedListener listener);
	
	boolean	removeListener(ApplyParametersChangedListener listener);
	
	void fireEvent(ApplyType apply, ApplyType parent, FunctionArgument argument, Object container);
	
	class BasicNotifier implements ApplyParametersChangedNotifier {
		Collection<ApplyParametersChangedListener> listeners = null;
		
		@Override
		public boolean addListener(ApplyParametersChangedListener listener) {
			if (this.listeners == null) {
				this.listeners = new ArrayList<ApplyParametersChangedListener>();
			}
			return this.listeners.add(listener);
		}

		@Override
		public boolean removeListener(ApplyParametersChangedListener listener) {
			if (this.listeners == null) {
				return false;
			}
			return this.listeners.remove(listener);
		}

		@Override
		public void fireEvent(ApplyType apply, ApplyType parent, FunctionArgument argument, Object container) {
			if (this.listeners == null) {
				return;
			}
			for (ApplyParametersChangedListener listener : this.listeners) {
				listener.applyParameterChanged(apply, parent, argument, container);
			}
		}
		
	}

}
