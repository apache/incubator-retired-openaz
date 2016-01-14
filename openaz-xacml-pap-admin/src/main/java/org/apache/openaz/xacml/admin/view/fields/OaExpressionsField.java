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

import org.apache.openaz.xacml.admin.XacmlAdminUI;
import org.apache.openaz.xacml.admin.jpa.Obadvice;
import org.apache.openaz.xacml.admin.view.components.OaExpressionsEditorComponent;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.UI;

public class OaExpressionsField extends CustomField<Object> {
	private static final long serialVersionUID = 1L;
	private final EntityItem<Obadvice> obad;

	public OaExpressionsField(EntityItem<Obadvice> obad) {
		this.obad = obad;
	}

	@Override
	protected Component initContent() {
		return new OaExpressionsEditorComponent(this.obad, ((XacmlAdminUI)UI.getCurrent()).getObadviceExpressions());
	}

	@Override
	public Class<? extends Object> getType() {
		return Collection.class;
	}

}
