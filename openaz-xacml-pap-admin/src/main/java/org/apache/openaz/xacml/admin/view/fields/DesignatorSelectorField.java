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

import org.apache.openaz.xacml.admin.jpa.Attribute;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.OptionGroup;

public class DesignatorSelectorField extends CustomField<Object> {
	private static final long serialVersionUID = 1L;
	private final DesignatorSelectorField self = this;
	private final OptionGroup selectDesignator = new OptionGroup("Type of attribute");

	public DesignatorSelectorField(EntityItem<Attribute> attributeEntity) {
		this.selectDesignator.setImmediate(true);
		this.selectDesignator.addItem(Attribute.ATTRIBUTE_DESIGNATOR);
		this.selectDesignator.addItem(Attribute.ATTRIBUTE_SELECTOR);
		if (attributeEntity.getEntity().isDesignator()) {
			this.selectDesignator.select(Attribute.ATTRIBUTE_DESIGNATOR);
		} else {
			this.selectDesignator.select(Attribute.ATTRIBUTE_SELECTOR);
		}
		//
		// Listen when designator vs selector changes
		//
		this.selectDesignator.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				Object value = self.selectDesignator.getValue();
				if (value != null) {
					if (value.toString().equals(Attribute.ATTRIBUTE_DESIGNATOR)) {
						self.setValue('1');
					} else if (value.toString().equals(Attribute.ATTRIBUTE_SELECTOR)) {
						self.setValue('0');
					}
				}
			}
		});
	}

	@Override
	protected Component initContent() {
		return this.selectDesignator;
	}

	@Override
	public Class<? extends Object> getType() {
		return Character.class;
	}

}
