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

package org.apache.openaz.xacml.admin.view.fields;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;


public class ExpressionField extends CustomField<Object> {
	private static final long serialVersionUID = 1L;
	private static final Log logger	= LogFactory.getLog(ExpressionField.class); //NOPMD
	
	@Override
	protected Component initContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends Object> getType() {
		return Collection.class;
	}

}
