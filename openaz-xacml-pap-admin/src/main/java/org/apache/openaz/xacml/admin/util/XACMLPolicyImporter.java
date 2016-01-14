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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;

import org.apache.openaz.xacml.admin.XacmlAdminUI;
import org.apache.openaz.xacml.admin.jpa.Attribute;
import org.apache.openaz.xacml.admin.jpa.Category;
import org.apache.openaz.xacml.admin.jpa.ConstraintType;
import org.apache.openaz.xacml.admin.jpa.ConstraintValue;
import org.apache.openaz.xacml.admin.jpa.Datatype;
import org.apache.openaz.xacml.admin.jpa.Obadvice;
import org.apache.openaz.xacml.api.Advice;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Obligation;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.util.XACMLObjectCopy;
import org.apache.openaz.xacml.util.XACMLPolicyAggregator;
import org.apache.openaz.xacml.util.XACMLPolicyScanner.CallbackResult;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.provider.CachingMutableLocalEntityProvider;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.UI;

public class XACMLPolicyImporter extends XACMLPolicyAggregator {
	private static Log logger	= LogFactory.getLog(XACMLPolicyImporter.class);
	
	public enum OPTION {
		/*
		 * Overwrite any existing ID
		 */
		OVERWRITE_EXISTING,
		/*
		 * Update an existing ID - only ADDs information
		 */
		UPDATE_EXISTING,
		/*
		 * Does not change an existing ID
		 */
		DONOTCHANGE_EXISTING
	}
	
	public boolean	importAttributes = true;
	public boolean	ignoreStandardAttributes = true;
	public boolean	addConstraints = true;
	public OPTION	attributeOption = OPTION.OVERWRITE_EXISTING;
	public boolean	importObligations = true;
	public OPTION	obligationOption = OPTION.OVERWRITE_EXISTING;
	public boolean	importAdvice = true;
	public OPTION	adviceOption = OPTION.OVERWRITE_EXISTING;

	public XACMLPolicyImporter() {
	}

	public boolean isImportAttributes() {
		return importAttributes;
	}

	public void setImportAttributes(boolean importAttributes) {
		this.importAttributes = importAttributes;
	}

	public boolean isIgnoreStandardAttributes() {
		return ignoreStandardAttributes;
	}

	public void setIgnoreStandardAttributes(boolean ignoreStandardAttributes) {
		this.ignoreStandardAttributes = ignoreStandardAttributes;
	}

	public boolean isAddConstraints() {
		return addConstraints;
	}

	public void setAddConstraints(boolean addConstraints) {
		this.addConstraints = addConstraints;
	}

	public OPTION getAttributeOption() {
		return attributeOption;
	}

	public void setAttributeOption(OPTION attributeOption) {
		this.attributeOption = attributeOption;
	}

	public boolean isImportObligations() {
		return importObligations;
	}

	public void setImportObligations(boolean importObligations) {
		this.importObligations = importObligations;
	}

	public OPTION getObligationOption() {
		return obligationOption;
	}

	public void setObligationOption(OPTION obligationOption) {
		this.obligationOption = obligationOption;
	}

	public boolean isImportAdvice() {
		return importAdvice;
	}

	public void setImportAdvice(boolean importAdvice) {
		this.importAdvice = importAdvice;
	}

	public OPTION getAdviceOption() {
		return adviceOption;
	}

	public void setAdviceOption(OPTION adviceOption) {
		this.adviceOption = adviceOption;
	}
	@Override
	public CallbackResult onObligation(Object parent, ObligationExpressionType expression, Obligation obligation) {
		if (importObligations) {
			super.onObligation(parent, expression, obligation);
		}
		return CallbackResult.CONTINUE;
	}
	
	@Override
	public CallbackResult onAttribute(Object parent, Object container, org.apache.openaz.xacml.api.Attribute attribute) {
		if (importAttributes) {
			super.onAttribute(parent, container, attribute);
		}
		return CallbackResult.CONTINUE;
	}
	
	@Override
	public CallbackResult onAdvice(Object parent, AdviceExpressionType expression, Advice advice) {
		if (importAdvice) {
			super.onAdvice(parent, expression, advice);
		}
		return CallbackResult.CONTINUE;
	}

	@Override
	public void onFinishScan(Object root) {
		if (this.importAttributes && this.doImportAttributes() > 0) {
			((XacmlAdminUI)UI.getCurrent()).refreshAttributes();
		}
		int changes = 0;
		if (this.importObligations) {
			changes += this.doImportObligations();
		}
		if (this.importAdvice) {
			changes += this.doImportAdvice();
		}
		//
		// If changes were made, we need to tell the UI so the
		// dictionary can refresh.
		//
		if (changes > 0) {
			((XacmlAdminUI)UI.getCurrent()).refreshObadvice();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected int doImportAttributes() {
		int changes = 0;
		//
		// Get our attributes. This container is modifiable.
		//
		JPAContainer<Attribute> attributes = ((XacmlAdminUI)UI.getCurrent()).getAttributes();
		//
		// Get mutable entity providers for these.
		//
		JPAContainer<Category> categories = new JPAContainer<Category>(Category.class);
		categories.setEntityProvider(new CachingMutableLocalEntityProvider<Category>(Category.class, ((XacmlAdminUI)UI.getCurrent()).getEntityManager()));

		JPAContainer<Datatype> datatypes = new JPAContainer<Datatype>(Datatype.class);
		datatypes.setEntityProvider(new CachingMutableLocalEntityProvider<Datatype>(Datatype.class, ((XacmlAdminUI)UI.getCurrent()).getEntityManager()));
		//
		// Work the map
		//
		for (Identifier cat : this.attributeMap.keySet()) {
			//
			// Does category exist?
			//
			Category category = JPAUtils.findCategory(cat);
			if (category == null) {
				//
				// This should rarely happen, but is possible since XACML 3.0
				// you can define your own categories.
				//
				logger.warn("New category: " + cat);
				category = new Category(cat, Category.CUSTOM);
				String shortName = XACMLConstants.extractShortName(cat.stringValue());
				if (shortName != null) {
					category.setShortName(shortName);
				} else {
					category.setShortName(category.getXacmlId());
				}
				//
				// Make sure the grouping is ok
				//
				if (category.getGrouping() == null) {
					category.setGrouping(category.getShortName());
				}
				//
				// Add it in
				//
				categories.addEntity(category);
				//
				// Tell the RO to update itself.
				//
				((XacmlAdminUI)UI.getCurrent()).getCategories().refresh();
			}
			Map<Identifier, Map<Identifier, Set<AttributeValue<?>>>> map = this.attributeMap.get(cat);
			for (Identifier dt : map.keySet()) {
				//
				// Does datatype exist?
				//
				Datatype datatype = JPAUtils.findDatatype(dt);
				if (datatype == null) {
					//
					// This should rarely happen, but is possible since XACML 3.0
					// you can define new datatypes.
					//
					logger.warn("New datatype: " + dt);
					datatype = new Datatype(dt, Datatype.CUSTOM);
					String shortName = XACMLConstants.extractShortName(dt.stringValue());
					if (shortName != null) {
						datatype.setShortName(shortName);
					} else {
						datatype.setShortName(datatype.getXacmlId());
					}
					//
					// Add it in
					//
					datatypes.addEntity(datatype);
					//
					// Tell the Read-Only property to update itself.
					//
					((XacmlAdminUI)UI.getCurrent()).getDatatypes().refresh();
				}
				//
				// Iterate the attributes
				//
				for (Identifier id : map.get(dt).keySet()) {
					//
					// Do we ignore it if its standard?
					//
					if (! this.ignoreStandardAttributes || 
						XACMLConstants.STANDARD_ATTRIBUTES.contains(id) == false) {
						//
						// Does it already exist?
						//
						Attribute newAttribute = null;
						Attribute currentAttribute = JPAUtils.findAttribute(category, datatype, id.stringValue());
						//
						// Support for an existing attribute
						//
						if (currentAttribute != null) {
							if (this.attributeOption == OPTION.OVERWRITE_EXISTING) {
								newAttribute = currentAttribute;
								newAttribute.setConstraintType(null);
								newAttribute.removeAllConstraintValues();
							} else if (this.attributeOption == OPTION.DONOTCHANGE_EXISTING) {
								logger.info("Do not change existing: " + currentAttribute);
								continue;
							} else if (this.attributeOption == OPTION.UPDATE_EXISTING) {
								newAttribute = currentAttribute;
							}
						} else {
							//
							// Create our new attribute
							//
							newAttribute = new Attribute(id.stringValue(), ((XacmlAdminUI)UI.getCurrent()).getUserid());
							newAttribute.setCategoryBean(category);
							newAttribute.setDatatypeBean(datatype);
						}
						//
						// Get all the values
						//
						Set<AttributeValue<?>> values = map.get(dt).get(id);
						//
						// Do we have more than 1? Also, omit boolean datatype which
						// doesn't make any sense to enumerate.
						//
						if (values.size() > 1 && dt.equals(XACML3.ID_DATATYPE_BOOLEAN) == false) {
							//
							// We have a lot of possible values, add as an enumeration
							//
							newAttribute.setConstraintType(JPAUtils.findConstraintType(ConstraintType.ENUMERATION_TYPE));
							for (AttributeValue<?> value : values) {
								Object val = value.getValue();
								String content;
								if (val instanceof Collection) {
									content = XACMLObjectCopy.getContent((List<Object>) value.getValue());
								} else {
									content = val.toString();
								}
								//
								// Check if we should add it in
								//
								boolean add = true;
								//
								// If we are updating an existing, we can really only do this for enumerations,
								// its impossible to resolve a regular expression or range.
								//
								if (currentAttribute != null && this.attributeOption == OPTION.UPDATE_EXISTING &&
											newAttribute.getConstraintType().getConstraintType().equals(ConstraintType.ENUMERATION_TYPE)) {
									//
									// Make sure it isn't there already, no duplicates.
									//
									for (ConstraintValue currentConstraintValue : newAttribute.getConstraintValues()) {
										if (currentConstraintValue.getValue().equals(content)) {
											add = false;
											break;
										}
									}
								}
								if (add && content.isEmpty() == false) {
									ConstraintValue newValue = new ConstraintValue("Enumeration", content);
									newValue.setAttribute(newAttribute);
									newAttribute.addConstraintValue(newValue);
								}
							}
						}
						//
						// Add it
						//
						if (newAttribute != null) {
							if (newAttribute.getId() == 0) {
								logger.info("Adding new attribute");
								if (attributes.addEntity(newAttribute) == null) {
									logger.error("Failed to add new attribute: " + newAttribute);
								} else {
									changes++;
								}
							} else {
								logger.info("Updating attribute " + newAttribute);
								try {
									attributes.commit();
									changes++;
								} catch (SourceException | InvalidValueException e) {
									logger.error("Update failed: " + e.getLocalizedMessage());
								}
							}
						}
					}
				}
			}
		}
		return changes;
	}

	protected int doImportObligations() {
		int changes = 0;
		JPAContainer<Obadvice> oa = ((XacmlAdminUI)UI.getCurrent()).getObadvice();
		for (Identifier id : this.obligationMap.keySet()) {
			for (EffectType effect : this.obligationMap.get(id).keySet()) {
				for (Obligation obligation : this.obligationMap.get(id).get(effect)) {
					Obadvice newObligation = null;
					Obadvice currentObligation = JPAUtils.findObligation(obligation.getId(), effect);
					//
					// Does it exist?
					//
					if (currentObligation != null) {
						if (this.obligationOption == OPTION.OVERWRITE_EXISTING) {
							newObligation = currentObligation;
							newObligation.removeAllExpressions();
						} else if (this.obligationOption == OPTION.DONOTCHANGE_EXISTING) {
							continue;
						} else if (this.obligationOption == OPTION.UPDATE_EXISTING) {
							newObligation = currentObligation;
						}
					} else {
						//
						// Create new one
						//
						newObligation = new Obadvice(obligation.getId(), ((XacmlAdminUI)UI.getCurrent()).getUserid());
						newObligation.setFulfillOn((effect == EffectType.PERMIT ? Obadvice.EFFECT_PERMIT : Obadvice.EFFECT_DENY));
					}
					//
					// TODO add the expressions
					//
					
					//
					// Add it in
					//
					if (newObligation != null) {
						if (newObligation.getId() == 0) {
							logger.info("Adding obligation " + newObligation);
							oa.addEntity(newObligation);
							changes++;
						} else {
							logger.info("Updating obligation " + newObligation);
							try {
								oa.commit();
								changes++;
							} catch (SourceException | InvalidValueException e) {
								logger.error("Update obligation failed " + e.getLocalizedMessage());
							}
						}
					}
				}
			}
		}
		return changes;
	}
	
	protected int doImportAdvice() {
		int changes = 0;
		JPAContainer<Obadvice> oa = ((XacmlAdminUI)UI.getCurrent()).getObadvice();
		for (Identifier id : this.adviceMap.keySet()) {
			for (EffectType effect : this.adviceMap.get(id).keySet()) {
				for (Advice advice : this.adviceMap.get(id).get(effect)) {
					Obadvice newAdvice = null;
					Obadvice currentAdvice = JPAUtils.findAdvice(advice.getId(), effect);
					//
					// Does it exist?
					//
					if (currentAdvice != null) {
						if (this.adviceOption == OPTION.OVERWRITE_EXISTING) {
							newAdvice = currentAdvice;
							newAdvice.removeAllExpressions();
						} else if (this.adviceOption == OPTION.DONOTCHANGE_EXISTING) {
							continue;
						} else if (this.adviceOption == OPTION.UPDATE_EXISTING) {
							newAdvice = currentAdvice;
						}
					} else {
						//
						// Create new one
						//
						newAdvice = new Obadvice(advice.getId(), ((XacmlAdminUI)UI.getCurrent()).getUserid());
						newAdvice.setType(Obadvice.ADVICE);
						newAdvice.setFulfillOn((effect == EffectType.PERMIT ? Obadvice.EFFECT_PERMIT : Obadvice.EFFECT_DENY));
					}
					//
					// TODO add the expressions
					//
					
					//
					// Add it in
					//
					if (newAdvice != null) {
						if (newAdvice.getId() == 0) {
							logger.info("Adding advice " + newAdvice);
							oa.addEntity(newAdvice);
							changes++;
						} else {
							logger.info("Updating advice " + newAdvice);
							try {
								oa.commit();
								changes++;
							} catch (SourceException | InvalidValueException e) {
								logger.error("Update advice failed " + e.getLocalizedMessage());
							}
						}
					}
				}
			}
		}
		return changes;
	}
}
