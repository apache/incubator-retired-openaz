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

package org.apache.openaz.xacml.admin.util;

import com.vaadin.ui.Notification;

/**
 * A simple mechanism for displaying messages to the user.
 * 
 * At the moment this is a very thin layer on top of the Vaadin Notification class.
 * There are two reasons for this class existing:
 * 	- simplifying (slightly) the code, in that the type is in the method call just like logging
 * 	- this is a central point where all messages are done, which allows us to globally change how they are displayed if we wish.
 * 
 * @author glenngriffin
 *
 */
public class AdminNotification  {
	//
	// PUBLIC STATIC METHODS
	//
	
	public static void info(String caption) {
		Notification.show(caption, Notification.Type.HUMANIZED_MESSAGE);
	}

	public static void warn(String caption) {
		Notification.show(caption, Notification.Type.WARNING_MESSAGE);
	}
	
	public static void error(String caption) {
		Notification.show(caption, Notification.Type.ERROR_MESSAGE);
	}
	



}
