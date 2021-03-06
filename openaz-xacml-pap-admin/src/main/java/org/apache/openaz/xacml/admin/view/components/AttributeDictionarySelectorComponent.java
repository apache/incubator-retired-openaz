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

package org.apache.openaz.xacml.admin.view.components;

import org.apache.openaz.xacml.admin.XacmlAdminUI;
import org.apache.openaz.xacml.admin.components.AttributeDictionary;
import org.apache.openaz.xacml.admin.jpa.Attribute;
import org.apache.openaz.xacml.admin.jpa.Category;
import org.apache.openaz.xacml.admin.jpa.Datatype;
import org.apache.openaz.xacml.admin.view.events.AttributeChangedEventListener;
import org.apache.openaz.xacml.admin.view.events.AttributeChangedEventNotifier;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.provider.CachingLocalEntityProvider;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class AttributeDictionarySelectorComponent extends CustomComponent implements AttributeChangedEventNotifier {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private VerticalLayout mainLayout;
	@AutoGenerated
	private ListSelect listSelectAttribute;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_2;
	@AutoGenerated
	private Button buttonNewAttribute;
	@AutoGenerated
	private ComboBox comboBoxCategoryFilter;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final AttributeDictionarySelectorComponent self = this;
	private final Datatype datatype;
	private final Attribute initialAttribute;
	private final BasicNotifier notifier = new BasicNotifier();
	private static final JPAContainer<Category>	categories = new JPAContainer<Category>(Category.class);
	private static final JPAContainer<Attribute>	attributes = new JPAContainer<Attribute>(Attribute.class);
	static {
		attributes.setEntityProvider(new CachingLocalEntityProvider<Attribute>(Attribute.class, ((XacmlAdminUI)UI.getCurrent()).getEntityManager()));
		categories.setEntityProvider(new CachingLocalEntityProvider<Category>(Category.class, ((XacmlAdminUI)UI.getCurrent()).getEntityManager()));
		attributes.sort(new String[]{"xacmlId"}, new boolean[]{true});
		categories.sort(new String[]{"xacmlId"}, new boolean[]{true});
	}
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public AttributeDictionarySelectorComponent(Datatype datatype, Attribute initialAttribute) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		//
		// Save pointer and finish container initialization
		//
		this.datatype = datatype;
		this.initialAttribute = initialAttribute;
		//
		// Initialize
		//
		this.initializeCategoryFilter();
		this.initializeAttributes();
		this.initializeButtons();
		//
		// Set our focus
		//
		this.listSelectAttribute.focus();
	}
	
	protected void initializeCategoryFilter() {
		//
		// Remove any filters
		//
		AttributeDictionarySelectorComponent.categories.removeAllContainerFilters();
		//
		// Initialize data source and GUI properties
		//
		this.comboBoxCategoryFilter.setContainerDataSource(AttributeDictionarySelectorComponent.categories);
		this.comboBoxCategoryFilter.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		this.comboBoxCategoryFilter.setItemCaptionPropertyId("xacmlId");
		this.comboBoxCategoryFilter.setImmediate(true);
		//
		// Respond to events
		//
		this.comboBoxCategoryFilter.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				//
				// Clear any existing filters
				//
				AttributeDictionarySelectorComponent.attributes.removeAllContainerFilters();
				//
				// Get the current selection
				//
				Object id = self.comboBoxCategoryFilter.getValue();
				//
				// Is anything currently selected?
				//
				if (id != null) {
					//
					// Yes - add the new filter into the container
					//
					AttributeDictionarySelectorComponent.attributes.addContainerFilter(new Compare.Equal("categoryBean", AttributeDictionarySelectorComponent.categories.getItem(id).getEntity()));
				}
			}
		});
	}
	
	protected void initializeAttributes() {
		//
		// Remove any filters
		//
		AttributeDictionarySelectorComponent.attributes.removeAllContainerFilters();
		//
		// Initialize data source and GUI properties
		//
		this.listSelectAttribute.setContainerDataSource(AttributeDictionarySelectorComponent.attributes);
		this.listSelectAttribute.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		this.listSelectAttribute.setItemCaptionPropertyId("xacmlId");
		this.listSelectAttribute.setImmediate(true);
		this.listSelectAttribute.setHeight(7, Unit.EM);
		//
		// Filter by datatype
		//
		if (this.datatype != null) {
			AttributeDictionarySelectorComponent.attributes.addContainerFilter(new Compare.Equal("datatypeBean", this.datatype));
		}
		//
		// Is there a default selection?  Is there an id?
		//
		if (this.initialAttribute != null && this.initialAttribute.getId() != 0) {
			this.listSelectAttribute.select(this.initialAttribute.getId());
		}
		//
		// Respond to events
		//
		this.listSelectAttribute.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				self.fireAttributeChanged(self.getAttribute());
			}
		});
	}
	
	protected void initializeButtons() {
		this.buttonNewAttribute.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				AttributeDictionary.createNewAttributeWindow();
			}
		});
	}
	
	@Override
	public void commit() {
		this.listSelectAttribute.commit();
	}
	
	@Override
	public Attribute getAttribute() {
		Object id = this.listSelectAttribute.getValue();
		if (id == null) {
			return null;
		}
		return AttributeDictionarySelectorComponent.attributes.getItem(id).getEntity();
	}

	@Override
	public boolean addListener(AttributeChangedEventListener listener) {
		return this.notifier.addListener(listener);
	}

	@Override
	public boolean removeListener(AttributeChangedEventListener listener) {
		return this.notifier.removeListener(listener);
	}

	@Override
	public void fireAttributeChanged(Attribute attribute) {
		this.notifier.fireAttributeChanged(attribute);
	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("-1px");
		mainLayout.setHeight("-1px");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		// top-level component properties
		setWidth("-1px");
		setHeight("-1px");
		
		// horizontalLayout_2
		horizontalLayout_2 = buildHorizontalLayout_2();
		mainLayout.addComponent(horizontalLayout_2);
		
		// listSelectAttribute
		listSelectAttribute = new ListSelect();
		listSelectAttribute.setCaption("Dictionary Attributes");
		listSelectAttribute.setImmediate(false);
		listSelectAttribute.setWidth("100.0%");
		listSelectAttribute.setHeight("-1px");
		listSelectAttribute.setInvalidAllowed(false);
		listSelectAttribute.setRequired(true);
		mainLayout.addComponent(listSelectAttribute);
		mainLayout.setExpandRatio(listSelectAttribute, 1.0f);
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_2() {
		// common part: create layout
		horizontalLayout_2 = new HorizontalLayout();
		horizontalLayout_2.setImmediate(false);
		horizontalLayout_2.setWidth("-1px");
		horizontalLayout_2.setHeight("-1px");
		horizontalLayout_2.setMargin(false);
		horizontalLayout_2.setSpacing(true);
		
		// comboBoxCategoryFilter
		comboBoxCategoryFilter = new ComboBox();
		comboBoxCategoryFilter.setCaption("Filter Category");
		comboBoxCategoryFilter.setImmediate(false);
		comboBoxCategoryFilter.setWidth("-1px");
		comboBoxCategoryFilter.setHeight("-1px");
		horizontalLayout_2.addComponent(comboBoxCategoryFilter);
		horizontalLayout_2.setExpandRatio(comboBoxCategoryFilter, 1.0f);
		
		// buttonNewAttribute
		buttonNewAttribute = new Button();
		buttonNewAttribute.setCaption("New Attribute");
		buttonNewAttribute.setImmediate(true);
		buttonNewAttribute
				.setDescription("Click to create a new attribute in the dictionary.");
		buttonNewAttribute.setWidth("-1px");
		buttonNewAttribute.setHeight("-1px");
		horizontalLayout_2.addComponent(buttonNewAttribute);
		horizontalLayout_2.setComponentAlignment(buttonNewAttribute,
				new Alignment(10));
		
		return horizontalLayout_2;
	}

}
