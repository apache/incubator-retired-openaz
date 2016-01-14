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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.admin.jpa.PIPConfigParam;
import org.apache.openaz.xacml.admin.jpa.PIPConfiguration;
import org.apache.openaz.xacml.admin.view.events.FormChangedEventListener;
import org.apache.openaz.xacml.admin.view.events.FormChangedEventNotifier;
import org.apache.openaz.xacml.std.pip.engines.csv.CSVEngine;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class CSVPIPConfigurationComponent extends CustomComponent implements FormChangedEventNotifier {
	
	@AutoGenerated
	private VerticalLayout mainLayout;

	@AutoGenerated
	private TextField textFieldSkip;

	@AutoGenerated
	private TextField textFieldQuote;

	@AutoGenerated
	private TextField textFieldDelimiter;

	@AutoGenerated
	private TextField textFieldFile;

	public static String CLASSNAME = "org.apache.openaz.xacml.std.pip.engines.csv.CSVEngine";
	
	

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Log logger	= LogFactory.getLog(CSVPIPConfigurationComponent.class);
	private final CSVPIPConfigurationComponent self = this;
	private final BasicNotifier notifier = new BasicNotifier();
	private final EntityItem<PIPConfiguration> entity;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 * @param entityConfig 
	 * @param configParamField 
	 */
	public CSVPIPConfigurationComponent(EntityItem<PIPConfiguration> entityConfig) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		//
		// Save
		//
		this.entity = entityConfig;
		//
		// initialize
		//
		this.initialize();
		//
		//  Focus
		//
		this.textFieldFile.focus();
	}
	
	protected void initialize() {
		if (logger.isDebugEnabled()) {
			logger.debug("initializing " + this.entity.getEntity().toString());
		}
		//
		// What are our current values?
		//
		Set<PIPConfigParam> unneeded = new HashSet<PIPConfigParam>();
		for (PIPConfigParam param : this.entity.getEntity().getPipconfigParams()) {
			if (param.getParamName().equals(CSVEngine.PROP_SOURCE)) {
				this.textFieldFile.setData(param);
			} else if (param.getParamName().equals(CSVEngine.PROP_DELIMITER)) {
				this.textFieldDelimiter.setData(param);
			} else if (param.getParamName().equals(CSVEngine.PROP_QUOTE)) {
				this.textFieldQuote.setData(param);
			} else if (param.getParamName().equals(CSVEngine.PROP_SKIP)) {
				this.textFieldSkip.setData(param);
			} else {
				unneeded.add(param);
			}
		}
		if (unneeded.isEmpty() == false) {
			this.entity.getEntity().getPipconfigParams().removeAll(unneeded);
		}
		//
		//
		//
		this.initializeEntity();
		this.initializeText();
	}
	
	protected void initializeEntity() {
		//
		// Initialize the entity
		//
		this.entity.getEntity().setClassname(CLASSNAME);
		this.entity.getEntity().setRequiresResolvers(true);
	}
	
	protected void initializeText() {
		//
		//
		//
		this.textFieldFile.setRequired(true);
		this.textFieldFile.setRequiredError("You need to specify a path to the CSV file on the server.");
		this.textFieldFile.setImmediate(true);
		this.textFieldFile.setNullRepresentation("");
		//
		//
		//
		PIPConfigParam param = (PIPConfigParam) this.textFieldFile.getData();
		if (param != null) {
			this.textFieldFile.setValue(param.getParamValue());
		}
		this.textFieldFile.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				PIPConfigParam param = (PIPConfigParam) self.textFieldFile.getData();
				if (param == null) {
					param = new PIPConfigParam(CSVEngine.PROP_SOURCE);
					self.entity.getEntity().addPipconfigParam(param);
					self.textFieldFile.setData(param);
				}
				param.setParamValue(self.textFieldFile.getValue());
				self.fireFormChangedEvent();
			}			
		});
		this.textFieldFile.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(TextChangeEvent event) {
				PIPConfigParam param = (PIPConfigParam) self.textFieldFile.getData();
				if (param == null) {
					param = new PIPConfigParam(CSVEngine.PROP_SOURCE);
					self.entity.getEntity().addPipconfigParam(param);
					self.textFieldFile.setData(param);
				}
				param.setParamValue(self.textFieldFile.getValue());
				self.fireFormChangedEvent();
			}			
		});
		//
		//
		//
		this.textFieldDelimiter.setNullRepresentation("");
		param = (PIPConfigParam) this.textFieldDelimiter.getData();
		if (param != null) {
			this.textFieldDelimiter.setValue(param.getParamValue());
		}
		this.textFieldDelimiter.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				PIPConfigParam param = (PIPConfigParam) self.textFieldDelimiter.getData();
				if (param == null) {
					param = new PIPConfigParam(CSVEngine.PROP_DELIMITER);
					self.entity.getEntity().addPipconfigParam(param);
					self.textFieldDelimiter.setData(param);
				}
				param.setParamValue(self.textFieldDelimiter.getValue());
				self.fireFormChangedEvent();
			}			
		});
		//
		//
		//
		this.textFieldQuote.setNullRepresentation("");
		param = (PIPConfigParam) this.textFieldQuote.getData();
		if (param != null) {
			this.textFieldQuote.setValue(param.getParamValue());
		}
		this.textFieldQuote.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				PIPConfigParam param = (PIPConfigParam) self.textFieldQuote.getData();
				if (param == null) {
					param = new PIPConfigParam(CSVEngine.PROP_QUOTE);
					self.entity.getEntity().addPipconfigParam(param);
					self.textFieldQuote.setData(param);
				}
				param.setParamValue(self.textFieldQuote.getValue());
				self.fireFormChangedEvent();
			}			
		});
		//
		//
		//
		this.textFieldSkip.setNullRepresentation("");
		param = (PIPConfigParam) this.textFieldSkip.getData();
		if (param != null) {
			this.textFieldSkip.setValue(param.getParamValue());
		}
		this.textFieldSkip.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				PIPConfigParam param = (PIPConfigParam) self.textFieldSkip.getData();
				if (param == null) {
					param = new PIPConfigParam(CSVEngine.PROP_SKIP);
					self.entity.getEntity().addPipconfigParam(param);
					self.textFieldSkip.setData(param);
				}
				param.setParamValue(self.textFieldSkip.getValue());
				self.fireFormChangedEvent();
			}			
		});
		this.textFieldSkip.setConverter(new StringToIntegerConverter());
	}
	
	public void validate() throws InvalidValueException {
		if (logger.isDebugEnabled()) {
			logger.debug("validate");
		}
		this.textFieldFile.validate();
		this.textFieldDelimiter.validate();
		this.textFieldQuote.validate();
		this.textFieldSkip.validate();
	}
	
	public void commit() throws SourceException, InvalidValueException {
		if (logger.isDebugEnabled()) {
			logger.debug("commit");
		}
		this.textFieldFile.commit();
		this.textFieldDelimiter.commit();
		this.textFieldQuote.commit();
		
		if (this.textFieldSkip.getValue() == null || this.textFieldSkip.getValue().isEmpty()) {
			this.entity.getEntity().removePipconfigParam((PIPConfigParam) this.textFieldSkip.getData());
			this.textFieldSkip.setData(null);
		}
		this.textFieldSkip.commit();
	}

	public void discard() throws SourceException {
		if (logger.isDebugEnabled()) {
			logger.debug("discard");
		}
		this.textFieldFile.discard();
		this.textFieldDelimiter.discard();
		this.textFieldQuote.discard();
		this.textFieldSkip.discard();
		
		this.entity.getEntity().getPipconfigParams().remove(CSVEngine.PROP_SOURCE);
		this.entity.getEntity().getPipconfigParams().remove(CSVEngine.PROP_DELIMITER);
		this.entity.getEntity().getPipconfigParams().remove(CSVEngine.PROP_QUOTE);
		this.entity.getEntity().getPipconfigParams().remove(CSVEngine.PROP_SKIP);
		this.entity.getEntity().getPipconfigParams().remove(CSVEngine.PROP_MAXSIZE);
	}

	@Override
	public boolean addListener(FormChangedEventListener listener) {
		return this.notifier.addListener(listener);
	}

	@Override
	public boolean removeListener(FormChangedEventListener listener) {
		return this.notifier.removeListener(listener);
	}

	@Override
	public void fireFormChangedEvent() {
		this.notifier.fireFormChangedEvent();
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
		
		// textFieldFile
		textFieldFile = new TextField();
		textFieldFile.setCaption("Path to CSV File");
		textFieldFile.setImmediate(false);
		textFieldFile
				.setDescription("This is the path to CSV file on the PDP node.");
		textFieldFile.setWidth("-1px");
		textFieldFile.setHeight("-1px");
		textFieldFile.setInvalidAllowed(false);
		textFieldFile.setRequired(true);
		textFieldFile
				.setInputPrompt("Eg. \"c:\\data.csv\" \"http://foo.com/data.csv\"");
		mainLayout.addComponent(textFieldFile);
		mainLayout.setExpandRatio(textFieldFile, 1.0f);
		
		// textFieldDelimiter
		textFieldDelimiter = new TextField();
		textFieldDelimiter.setCaption("Delimiter");
		textFieldDelimiter.setImmediate(false);
		textFieldDelimiter
				.setDescription("Enter a separator character or string that delineates columns in each row.");
		textFieldDelimiter.setWidth("-1px");
		textFieldDelimiter.setHeight("-1px");
		textFieldDelimiter.setInputPrompt("Eg. \",\" or \"|\"");
		textFieldDelimiter.setNullSettingAllowed(true);
		mainLayout.addComponent(textFieldDelimiter);
		mainLayout.setExpandRatio(textFieldDelimiter, 1.0f);
		
		// textFieldQuote
		textFieldQuote = new TextField();
		textFieldQuote.setCaption("Quote");
		textFieldQuote.setImmediate(false);
		textFieldQuote
				.setDescription("Enter character used for quoted elements.");
		textFieldQuote.setWidth("-1px");
		textFieldQuote.setHeight("-1px");
		textFieldQuote.setInputPrompt("Eg. \" or '");
		textFieldQuote.setNullSettingAllowed(true);
		mainLayout.addComponent(textFieldQuote);
		
		// textFieldSkip
		textFieldSkip = new TextField();
		textFieldSkip.setCaption("Skip Lines");
		textFieldSkip.setImmediate(false);
		textFieldSkip
				.setDescription("Skips the number of lines at the beginning of the file.");
		textFieldSkip.setWidth("-1px");
		textFieldSkip.setHeight("-1px");
		textFieldSkip.setInputPrompt("Eg. 1 or 2");
		mainLayout.addComponent(textFieldSkip);
		
		return mainLayout;
	}

}
