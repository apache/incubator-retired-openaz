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

public interface FormChangedEventNotifier {
	boolean	addListener(FormChangedEventListener listener);
	boolean	removeListener(FormChangedEventListener listener);
	void		fireFormChangedEvent();
	
	class BasicNotifier implements FormChangedEventNotifier {
		Collection<FormChangedEventListener> listeners = null;

		@Override
		public boolean addListener(FormChangedEventListener listener) {
			if (this.listeners == null) {
				this.listeners = new ArrayList<FormChangedEventListener>();
			}
			return this.listeners.add(listener);
		}

		@Override
		public boolean removeListener(FormChangedEventListener listener) {
			if (this.listeners == null) {
				this.listeners = new ArrayList<FormChangedEventListener>();
			}
			return this.listeners.remove(listener);
		}

		@Override
		public void fireFormChangedEvent() {
			if (this.listeners == null) {
				return;
			}
			for (FormChangedEventListener listener : this.listeners) {
				listener.onFormChanged();
			}
		}
		
	}
}
