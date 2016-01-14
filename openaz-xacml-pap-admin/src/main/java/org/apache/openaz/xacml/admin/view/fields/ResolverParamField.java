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

import org.apache.openaz.xacml.admin.jpa.PIPResolver;
import org.apache.openaz.xacml.admin.view.components.PIPParameterComponent;
import org.apache.openaz.xacml.admin.view.events.FormChangedEventListener;
import org.apache.openaz.xacml.admin.view.windows.PIPSQLResolverEditorWindow;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.VerticalLayout;

public class ResolverParamField extends CustomField<Object> implements FormChangedEventListener {
	private static final long serialVersionUID = 1L;
	private final EntityItem<PIPResolver> entity;
	private VerticalLayout mainLayout = new VerticalLayout();

	public ResolverParamField(EntityItem<PIPResolver> entity) {
		this.entity = entity;
		//
		// Make sure we can auto-fit
		//
		this.setWidth("-1px");
		this.mainLayout.setWidth("-1px");
	}

	@Override
	public void onFormChanged() {
		this.fireEvent(new com.vaadin.ui.Field.ValueChangeEvent(this));
	}

	@Override
	protected Component initContent() {
		Component c = null;
		if (this.entity.getEntity().getPipconfiguration().getPiptype().isSQL() ||
			this.entity.getEntity().getPipconfiguration().getPiptype().isHyperCSV() ||
			this.entity.getEntity().getPipconfiguration().getPiptype().isLDAP() ||
			this.entity.getEntity().getPipconfiguration().getPiptype().isCSV() ) {
			//
			//
			//
			PIPSQLResolverEditorWindow comp = new PIPSQLResolverEditorWindow(this.entity);
			comp.addListener(this);
			c = comp;
		} else {
			//
			//
			//
			PIPParameterComponent comp = new PIPParameterComponent(this.entity.getEntity());
			comp.addListener(this);
			c = comp;
		}
		if (c != null) {
			this.mainLayout.addComponent(c);
		}
		return this.mainLayout;
	}

	
	@Override
	public void discard() throws SourceException {
		if (this.mainLayout.getComponentCount() == 0) {
			return;
		}
		Component c = this.mainLayout.getComponent(0);
		if (c instanceof PIPSQLResolverEditorWindow) {
			((PIPSQLResolverEditorWindow)c).discard();
		}
		super.discard();
	}

	@Override
	public void validate() throws InvalidValueException {
		if (this.mainLayout.getComponentCount() == 0) {
			return;
		}
		Component c = this.mainLayout.getComponent(0);
		if (c instanceof PIPSQLResolverEditorWindow) {
			((PIPSQLResolverEditorWindow)c).validate();
		}
		super.validate();
	}

	@Override
	public void commit() throws SourceException, InvalidValueException {
		if (this.mainLayout.getComponentCount() == 0) {
			return;
		}
		Component c = this.mainLayout.getComponent(0);
		if (c instanceof PIPSQLResolverEditorWindow) {
			((PIPSQLResolverEditorWindow)c).commit();
		}
		super.commit();
	}

	@Override
	public Class<? extends Object> getType() {
		return Collection.class;
	}

}
