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

package org.apache.openaz.xacml.admin.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AdviceExpressionsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeSelectorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionsType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;

public class ObligationAdviceContainer extends ItemSetChangeNotifier implements Container.Hierarchical, Container.ItemSetChangeNotifier {
	private static final long serialVersionUID = 1L;
	private static Log logger	= LogFactory.getLog(ObligationAdviceContainer.class);
	
    public static String PROPERTY_NAME = "Name";
    public static String PROPERTY_ID = "Id";
    public static String PROPERTY_ID_SHORT = "ShortId";
    public static String PROPERTY_EFFECT = "Effect";
    public static String PROPERTY_CATEGORY = "Category";
    public static String PROPERTY_CATEGORY_SHORT = "ShortCategory";
    public static String PROPERTY_DATATYPE = "Datatype";
    public static String PROPERTY_DATATYPE_SHORT = "ShortDatatype";

    /**
     * List of the string identifiers for the available properties.
     */
    public static Collection<String> OBADVICE_PROPERTIES;

    private final static Method OBADVICE_ITEM_NAME;
    
    private final static Method OBADVICE_ITEM_ID;

    private final static Method OBADVICE_ITEM_ID_SHORT;

    private final static Method OBADVICE_ITEM_EFFECT;

    private final static Method OBADVICE_ITEM_DATATYPE;

    private final static Method OBADVICE_ITEM_DATATYPE_SHORT;

    private final static Method OBADVICE_ITEM_CATEGORY;

    private final static Method OBADVICE_ITEM_CATEGORY_SHORT;

    static {
    	OBADVICE_PROPERTIES = new ArrayList<String>();
    	OBADVICE_PROPERTIES.add(PROPERTY_NAME);
    	OBADVICE_PROPERTIES.add(PROPERTY_ID);
    	OBADVICE_PROPERTIES.add(PROPERTY_ID_SHORT);
    	OBADVICE_PROPERTIES.add(PROPERTY_EFFECT);
    	OBADVICE_PROPERTIES.add(PROPERTY_CATEGORY);
    	OBADVICE_PROPERTIES.add(PROPERTY_DATATYPE_SHORT);
    	OBADVICE_PROPERTIES.add(PROPERTY_DATATYPE);
    	OBADVICE_PROPERTIES.add(PROPERTY_CATEGORY_SHORT);
    	OBADVICE_PROPERTIES = Collections.unmodifiableCollection(OBADVICE_PROPERTIES);
    	try {
    		OBADVICE_ITEM_NAME = ObAdviceItem.class.getMethod("getName", new Class[]{});
    		OBADVICE_ITEM_ID = ObAdviceItem.class.getMethod("getId", new Class[]{});
    		OBADVICE_ITEM_ID_SHORT = ObAdviceItem.class.getMethod("getIdShort", new Class[]{});
    		OBADVICE_ITEM_EFFECT = ObAdviceItem.class.getMethod("getEffect", new Class[]{});
    		OBADVICE_ITEM_DATATYPE = ObAdviceItem.class.getMethod("getDatatype", new Class[]{});
    		OBADVICE_ITEM_DATATYPE_SHORT = ObAdviceItem.class.getMethod("getDatatypeShort", new Class[]{});
    		OBADVICE_ITEM_CATEGORY = ObAdviceItem.class.getMethod("getCategory", new Class[]{});
    		OBADVICE_ITEM_CATEGORY_SHORT = ObAdviceItem.class.getMethod("getCategoryShort", new Class[]{});
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(
                    "Internal error finding methods in ObligationAdviceContainer");
        }
    }
    //
    // Our root object
    //
    private final Object root;
    //
    // Our helper maps to control the hierarchy
    //
    private List<AdviceExpressionType> rootAdvice = new ArrayList<AdviceExpressionType>();
    private List<ObligationExpressionType> rootObligations = new ArrayList<ObligationExpressionType>();
    private Map<AttributeAssignmentExpressionType, AdviceExpressionType> adviceExpressions = new HashMap<AttributeAssignmentExpressionType, AdviceExpressionType>();
    private Map<AttributeAssignmentExpressionType, ObligationExpressionType> obligationExpressions = new HashMap<AttributeAssignmentExpressionType, ObligationExpressionType>();
    private Map<AttributeValueType, AttributeAssignmentExpressionType> values = new HashMap<AttributeValueType, AttributeAssignmentExpressionType>();
    private Map<AttributeDesignatorType, AttributeAssignmentExpressionType> designators = new HashMap<AttributeDesignatorType, AttributeAssignmentExpressionType>();
    private Map<AttributeSelectorType, AttributeAssignmentExpressionType> selectors = new HashMap<AttributeSelectorType, AttributeAssignmentExpressionType>();
    private Map<ApplyType, AttributeAssignmentExpressionType> applys = new HashMap<ApplyType, AttributeAssignmentExpressionType>();
    
	public ObligationAdviceContainer(Object root) {
		super();
		this.setContainer(this);
		//
		// Save
		//
		this.root = root;
		//
		// Initialize
		//
		this.initialize();
	}
	
	protected void initialize() {
		if (this.root instanceof AdviceExpressionsType) {
			for (AdviceExpressionType advice : ((AdviceExpressionsType) this.root).getAdviceExpression()) {
				this.rootAdvice.add(advice);
				for (AttributeAssignmentExpressionType assignment : advice.getAttributeAssignmentExpression()) {
					this.adviceExpressions.put(assignment, advice);
					this.addExpression(assignment.getExpression(), assignment);
				}
			}
		} else if (this.root instanceof ObligationExpressionsType) {
			for (ObligationExpressionType obligation : ((ObligationExpressionsType) this.root).getObligationExpression()) {
				this.rootObligations.add(obligation);
				for (AttributeAssignmentExpressionType assignment : obligation.getAttributeAssignmentExpression()) {
					this.obligationExpressions.put(assignment, obligation);
					this.addExpression(assignment.getExpression(), assignment);
				}
			}
		} else {
			throw new IllegalArgumentException("This container supports only advice or obligation expressions.");
		}
	}
	
	private void addExpression(JAXBElement<?> element, AttributeAssignmentExpressionType parent) {
		if (element.getValue() == null) {
			return;
		}
		if (element.getValue() instanceof AttributeValueType) {
			this.values.put((AttributeValueType) element.getValue(), parent);
		} else if (element.getValue() instanceof AttributeDesignatorType) {
			this.designators.put((AttributeDesignatorType) element.getValue(), parent);
		} else if (element.getValue() instanceof AttributeSelectorType) {
			this.selectors.put((AttributeSelectorType) element.getValue(), parent);
		} else if (element.getValue() instanceof ApplyType) {
			this.applys.put((ApplyType) element.getValue(), parent);
		} else {
			//
			// TODO
			//
			logger.error("Adding unknown expression type");
		}
	}

	public boolean isObjectSupported(Object itemId) {
		if (itemId instanceof AdviceExpressionType) {
			return true;
		}
		if (itemId instanceof ObligationExpressionType) {
			return true;
		}
		if (itemId instanceof AttributeAssignmentExpressionType) {
			return true;
		}
		if (itemId instanceof AttributeValueType) {
			return true;
		}
		if (itemId instanceof AttributeDesignatorType) {
			return true;
		}
		if (itemId instanceof AttributeSelectorType) {
			return true;
		}
		if (itemId instanceof ApplyType) {
			return true;
		}
		return false;
	}
	
	public void updateItem(Object itemId) {
		this.fireItemSetChange();
	}

	@Override
	public Item getItem(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getItem: " + itemId);
		}
		if (this.isObjectSupported(itemId) == false) {
			return null;
		}
		return new ObAdviceItem(itemId);
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return OBADVICE_PROPERTIES;
	}

	@Override
	public Collection<?> getItemIds() {
		final Collection<Object> items = new ArrayList<Object>();
		if (this.root instanceof ObligationExpressionsType) {
			items.addAll(this.rootObligations);
			if (this.obligationExpressions.isEmpty() == false) {
				items.addAll(this.obligationExpressions.keySet());
			}
		} else if (this.root instanceof AdviceExpressionsType) {
			items.addAll(this.rootAdvice);
			if (this.adviceExpressions.isEmpty() == false) {
				items.addAll(this.adviceExpressions.keySet());
			}
		}
		if (this.values.isEmpty() == false) {
			items.add(this.values.keySet());
		}
		if (this.designators.isEmpty() == false) {
			items.add(this.designators.keySet());
		}
		if (this.selectors.isEmpty() == false) {
			items.add(this.selectors.keySet());
		}
		if (this.applys.isEmpty() == false) {
			items.add(this.applys.keySet());
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getItemIds (" + items.size() + "):" + items);
		}
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
		if (this.isObjectSupported(itemId) == false) {
			return null;
		}
        if (propertyId.equals(PROPERTY_NAME)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ObAdviceItem(itemId), OBADVICE_ITEM_NAME, null);
        }

        if (propertyId.equals(PROPERTY_ID)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ObAdviceItem(itemId), OBADVICE_ITEM_ID, null);
        }
		
        if (propertyId.equals(PROPERTY_ID_SHORT)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ObAdviceItem(itemId), OBADVICE_ITEM_ID_SHORT, null);
        }
		
        if (propertyId.equals(PROPERTY_EFFECT)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ObAdviceItem(itemId), OBADVICE_ITEM_EFFECT, null);
        }
		
        if (propertyId.equals(PROPERTY_DATATYPE)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ObAdviceItem(itemId), OBADVICE_ITEM_DATATYPE, null);
        }
		
        if (propertyId.equals(PROPERTY_DATATYPE_SHORT)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ObAdviceItem(itemId), OBADVICE_ITEM_DATATYPE_SHORT, null);
        }
		
        if (propertyId.equals(PROPERTY_CATEGORY)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ObAdviceItem(itemId), OBADVICE_ITEM_CATEGORY, null);
        }
		
        if (propertyId.equals(PROPERTY_CATEGORY_SHORT)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ObAdviceItem(itemId), OBADVICE_ITEM_CATEGORY_SHORT, null);
        }
		
		return null;
	}

	@Override
	public Class<?> getType(Object propertyId) {
        if (propertyId.equals(PROPERTY_NAME)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_ID)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_ID_SHORT)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_EFFECT)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_DATATYPE)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_DATATYPE_SHORT)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_CATEGORY)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_CATEGORY_SHORT)) {
            return String.class;
        }
		return null;
	}

	@Override
	public int size() {
		int size = 0;
		if (this.root instanceof ObligationExpressionsType) {
			size += this.rootObligations.size();
			size += this.obligationExpressions.size();
		} else if (this.root instanceof AdviceExpressionsType) {
			size += this.rootAdvice.size();
			size += this.adviceExpressions.size();
		}
		size += this.values.size();
		size += this.designators.size();
		size += this.selectors.size();
		size += this.applys.size();
		if (logger.isTraceEnabled()) {
			logger.trace("size: " + size);
		}
		return size;
	}

	@Override
	public boolean containsId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("containsId: " + itemId);
		}
		if (itemId instanceof AdviceExpressionType) {
			return this.rootAdvice.contains(itemId);
		}
		if (itemId instanceof ObligationExpressionType) {
			return this.rootObligations.contains(itemId);
		}
		if (itemId instanceof AttributeAssignmentExpressionType) {
			if (this.root instanceof ObligationExpressionsType) {
				return this.obligationExpressions.containsKey(itemId);
			} else if (this.root instanceof AdviceExpressionsType) {
				return this.adviceExpressions.containsKey(itemId);
			}
		}
		if (itemId instanceof AttributeValueType) {
			return this.values.containsKey(itemId);
		}
		if (itemId instanceof AttributeDesignatorType) {
			return this.designators.containsKey(itemId);
		}
		if (itemId instanceof AttributeSelectorType) {
			return this.selectors.containsKey(itemId);
		}
		if (itemId instanceof ApplyType) {
			return this.applys.containsKey(itemId);
		}
		return false;
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		if (itemId instanceof ObligationExpressionType ||
			itemId instanceof AdviceExpressionType) {
			return this.addItem(itemId, null);
		}
		throw new UnsupportedOperationException("Must be Obligation or Advice Expression Type.");
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		if (this.root instanceof ObligationExpressionsType) {
			return this.addItem(new ObligationExpressionType(), null);
		} else if (this.root instanceof AdviceExpressionsType) {
			return this.addItem(new AdviceExpressionType(), null);
		}
		//
		// Should never get here
		//
		return null;
	}
	
	public Item addItem(Object itemId, Object parent) {
		if (logger.isTraceEnabled()) {
			logger.trace("addItem: " + itemId + " " + parent);
		}
		//
		// Check itemId to see if its supported
		//
		if (this.isObjectSupported(itemId) == false) {
			logger.error("Unsupported itemid: " + itemId.getClass().getCanonicalName());
			return null;
		}
		//
		// Determine what they are trying to add
		//
		if (this.root instanceof ObligationExpressionsType) {
			if (itemId instanceof ObligationExpressionType) {
				//
				// Adding a new root obligation expression, is it already in the parent.
				//
				if (((ObligationExpressionsType)this.root).getObligationExpression().contains(itemId) == false) {
					//
					// It doesn't exist in the object, add it in
					//
					((ObligationExpressionsType)this.root).getObligationExpression().add((ObligationExpressionType) itemId);
				}
				//
				// Track this
				//
				this.rootObligations.add((ObligationExpressionType) itemId);
				//
				// Notify
				//
				this.fireItemSetChange();
				//
				// Return the item
				//
				return new ObAdviceItem(itemId);
			}
			if (itemId instanceof AttributeAssignmentExpressionType) {
				//
				// Sanity check the parent
				//
				if (parent instanceof ObligationExpressionType == false) {
					logger.error("Incorrect parent type: " + parent.getClass().getCanonicalName());
					return null;
				}
				//
				// Does the parent object exist?
				//
				if (((ObligationExpressionsType)this.root).getObligationExpression().contains(parent) == false) {
					//
					// This is a new obligation
					//
					logger.info("addItem - parent not found, adding." + ((ObligationExpressionType) parent).getObligationId());
					((ObligationExpressionsType)this.root).getObligationExpression().add((ObligationExpressionType) parent);
					//
					// track it
					//
					this.rootObligations.add((ObligationExpressionType) parent);
				}
				//
				// Check if the item needs to be added to the parent object
				//
				if (((ObligationExpressionType) parent).getAttributeAssignmentExpression().contains(itemId) == false) {
					//
					// Put the assignment into the parent
					//
					((ObligationExpressionType) parent).getAttributeAssignmentExpression().add((AttributeAssignmentExpressionType) itemId);
					//
					// Add the contained expression
					//
					this.addExpression(((AttributeAssignmentExpressionType) itemId).getExpression(), (AttributeAssignmentExpressionType) itemId);
				}
				//
				// track this in our map
				//
				this.obligationExpressions.put((AttributeAssignmentExpressionType) itemId, (ObligationExpressionType) parent);
				//
				// Notify
				//
				this.fireItemSetChange();
				//
				// Return the item
				//
				return new ObAdviceItem(itemId);
			}
			if (parent instanceof AttributeAssignmentExpressionType) {
				//
				// Does the parent object exist?
				//
				if (this.obligationExpressions.containsKey(parent) == false) {
					//
					// No - we can't add it. Need more information.
					//
					logger.info("addItem - parent not found, adding." + ((AttributeAssignmentExpressionType) parent).getAttributeId());
					return null;
				}
				if (itemId instanceof AttributeValueType) {
					this.values.put((AttributeValueType) itemId, (AttributeAssignmentExpressionType) parent);
				} else if (itemId instanceof AttributeDesignatorType) {
					this.designators.put((AttributeDesignatorType) itemId, (AttributeAssignmentExpressionType) parent);
				} else if (itemId instanceof AttributeSelectorType) {
					this.selectors.put((AttributeSelectorType) itemId, (AttributeAssignmentExpressionType) parent);
				} else if (itemId instanceof ApplyType) {
					this.applys.put((ApplyType) itemId, (AttributeAssignmentExpressionType) parent);
				} else {
					logger.error("Should not get here. The object was checked in the beginning of the function. Someone removed or altered that check.");
					assert false;
					return null;
				}
				//
				// Notify
				//
				this.fireItemSetChange();
				//
				// Return the item
				//
				return new ObAdviceItem(itemId);
			}
		} else if (this.root instanceof AdviceExpressionsType) {
			//
			// Are we adding new root advice expression?
			//
			if (itemId instanceof AdviceExpressionType) {
				//
				// Adding a new root obligation expression, is it already in the parent.
				//
				if (((AdviceExpressionsType)this.root).getAdviceExpression().contains(itemId) == false) {
					//
					// No - add it in
					//
					((AdviceExpressionsType)this.root).getAdviceExpression().add((AdviceExpressionType) itemId);
				}
				//
				// Track this object
				//
				this.rootAdvice.add((AdviceExpressionType) itemId);
				//
				// Notify
				//
				this.fireItemSetChange();
				//
				// Return the new item
				//
				return new ObAdviceItem(itemId);
			}
			if (itemId instanceof AttributeAssignmentExpressionType) {
				//
				// Sanity check
				//
				if (parent instanceof AdviceExpressionType == false) {
					logger.error("Incorrect parent type: " + parent.getClass().getCanonicalName());
					return null;
				}
				//
				// Does the parent object exist?
				//
				if (((AdviceExpressionsType)this.root).getAdviceExpression().contains(parent) == false) {
					//
					// This is a new obligation
					//
					logger.info("addItem - parent not found, adding." + ((AdviceExpressionType) parent).getAdviceId());
					((AdviceExpressionsType)this.root).getAdviceExpression().add((AdviceExpressionType) parent);
					//
					// Track it
					//
					this.rootAdvice.add((AdviceExpressionType) parent);
				}
				//
				// Check if the item needs to be added to the parent object
				//
				if (((AdviceExpressionType) parent).getAttributeAssignmentExpression().contains(itemId) == false) {
					//
					// Put the assignment into the parent
					//
					((AdviceExpressionType) parent).getAttributeAssignmentExpression().add((AttributeAssignmentExpressionType) itemId);
					//
					// Add the contained expression
					//
					this.addExpression(((AttributeAssignmentExpressionType) itemId).getExpression(), (AttributeAssignmentExpressionType) itemId);
				}
				//
				// track this in our map
				//
				this.adviceExpressions.put((AttributeAssignmentExpressionType) itemId, (AdviceExpressionType) parent);
				//
				// Notify
				//
				this.fireItemSetChange();
				//
				// Return the item
				//
				return new ObAdviceItem(itemId);
			}
			if (parent instanceof AttributeAssignmentExpressionType) {
				//
				// Does the parent object exist?
				//
				if (this.adviceExpressions.containsKey(parent) == false) {
					//
					// No - we can't add it. Need more information.
					//
					logger.info("addItem - parent not found, adding." + ((AttributeAssignmentExpressionType) parent).getAttributeId());
					return null;
				}
				if (itemId instanceof AttributeValueType) {
					this.values.put((AttributeValueType) itemId, (AttributeAssignmentExpressionType) parent);
				} else if (itemId instanceof AttributeDesignatorType) {
					this.designators.put((AttributeDesignatorType) itemId, (AttributeAssignmentExpressionType) parent);
				} else if (itemId instanceof AttributeSelectorType) {
					this.selectors.put((AttributeSelectorType) itemId, (AttributeAssignmentExpressionType) parent);
				} else if (itemId instanceof ApplyType) {
					this.applys.put((ApplyType) itemId, (AttributeAssignmentExpressionType) parent);
				} else {
					logger.error("Should not get here. Someone altered the object supported check or removed the code.");
					assert false;
					return null;
				}
				//
				// Notify
				//
				this.fireItemSetChange();
				//
				// Return new item
				//
				return new ObAdviceItem(itemId);
			}
		} else {
			//
			// We should not ever get here.
			//
			logger.error("The root object is incorrect.");
			return null;
		}
		logger.error("Unsupported combination of itemId and parent classes.");
		return null;
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type,
			Object defaultValue) throws UnsupportedOperationException {
		return false;
	}

	@Override
	public boolean removeContainerProperty(Object propertyId)
			throws UnsupportedOperationException {
		return false;
	}
	
	public boolean removeAllAssignments () {
		if (logger.isTraceEnabled()) {
			logger.trace("removeAllAssignments:");
		}
		if (this.root instanceof ObligationExpressionsType) {
			((ObligationExpressionsType)this.root).getObligationExpression().clear();
			this.obligationExpressions.clear();
		} else if (this.root instanceof AdviceExpressionsType) {
			((AdviceExpressionsType)this.root).getAdviceExpression().clear();
			this.adviceExpressions.clear();
		}
		this.values.clear();
		this.designators.clear();
		this.selectors.clear();
		this.applys.clear();
		//
		// Notify
		//
		this.fireItemSetChange();
		return true;
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeAllItems:");
		}
		if (this.root instanceof ObligationExpressionsType) {
			((ObligationExpressionsType)this.root).getObligationExpression().clear();
			this.rootObligations.clear();
			this.obligationExpressions.clear();
		} else if (this.root instanceof AdviceExpressionsType) {
			((AdviceExpressionsType)this.root).getAdviceExpression().clear();
			this.rootAdvice.clear();
			this.adviceExpressions.clear();
		}
		this.values.clear();
		this.designators.clear();
		this.selectors.clear();
		this.applys.clear();
		//
		// Notify
		//
		this.fireItemSetChange();
		return true;
	}

	@Override
	public Collection<?> getChildren(Object itemId) {
		final Collection<Object> items = new ArrayList<Object>();
		if (itemId instanceof AdviceExpressionType) {
			if (this.rootAdvice.contains(itemId)) {
				items.addAll(((AdviceExpressionType) itemId).getAttributeAssignmentExpression());
			} else {
				logger.error("getChildren: itemId not in root advice expression " + ((AdviceExpressionType) itemId).getAdviceId());
			}
		}
		if (itemId instanceof ObligationExpressionType) {
			if (this.rootObligations.contains(itemId)) {
				items.addAll(((ObligationExpressionType) itemId).getAttributeAssignmentExpression());
			} else {
				logger.error("getChildren: itemId not in root obligation expression " + ((ObligationExpressionType) itemId).getObligationId());
			}
		}
		if (itemId instanceof AttributeAssignmentExpressionType) {
			if (this.root instanceof ObligationExpressionsType) {
				if (this.obligationExpressions.containsKey(itemId)) {
					JAXBElement<?> element = ((AttributeAssignmentExpressionType) itemId).getExpression();
					if (element != null && element.getValue() != null) {
						items.add(element.getValue());
					}
				} else {
					logger.error("getChildren: itemId not in obligation expressions " + ((AttributeAssignmentExpressionType) itemId).getAttributeId());
				}
			} else if (this.root instanceof AdviceExpressionsType) {
				if (this.adviceExpressions.containsKey(itemId)) {
					JAXBElement<?> element = ((AttributeAssignmentExpressionType) itemId).getExpression();
					if (element != null && element.getValue() != null) {
						items.add(element.getValue());
					}
				} else {
					logger.error("getChildren: itemId not in advice expressions " + ((AttributeAssignmentExpressionType) itemId).getAttributeId());
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getChildren " + itemId + "(" + items.size() + "):" + items);
		}
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public Object getParent(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getParent: " + itemId);
		}
		assert itemId != null;
		if (itemId instanceof AdviceExpressionType) {
			if (this.root instanceof AdviceExpressionsType) {
				return this.root;
			}
			return null;
		}
		if (itemId instanceof ObligationExpressionType) {
			if (this.root instanceof ObligationExpressionsType) {
				return this.root;
			}
			return null;
		}
		if (itemId instanceof AttributeAssignmentExpressionType) {
			if (this.root instanceof ObligationExpressionsType) {
				return this.obligationExpressions.get(itemId);
			} else if (this.root instanceof AdviceExpressionsType) {
				return this.adviceExpressions.get(itemId);
			}
		}
		if (itemId instanceof AttributeValueType) {
			return this.values.get(itemId);
		}
		if (itemId instanceof AttributeDesignatorType) {
			return this.designators.get(itemId);
		}
		if (itemId instanceof AttributeSelectorType) {
			return this.selectors.get(itemId);
		}
		if (itemId instanceof ApplyType) {
			return this.applys.get(itemId);
		}
		return null;
	}

	@Override
	public Collection<?> rootItemIds() {
		final Collection<Object> items = new ArrayList<Object>();
		if (this.root instanceof ObligationExpressionsType) {
			items.addAll(this.rootObligations);
		} else if (this.root instanceof AdviceExpressionsType) {
			items.addAll(this.rootAdvice);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("rootItemIds (" + items.size() + "):" + items);
		}
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("setParent: " + itemId);
		}
		if (itemId instanceof AdviceExpressionType) {
			return false;
		}
		if (itemId instanceof ObligationExpressionType) {
			return false;
		}
		if (itemId instanceof AttributeAssignmentExpressionType) {
			if (this.root instanceof ObligationExpressionsType && newParentId instanceof ObligationExpressionType) {
				//
				// Remove it from its parent object
				//
				ObligationExpressionType oldParent = this.obligationExpressions.get(itemId);
				if (oldParent.getAttributeAssignmentExpression().remove(itemId)) {
					//
					// See if its in the new parent
					//
					if (((ObligationExpressionType) newParentId).getAttributeAssignmentExpression().contains(itemId) == false) {
						//
						// Nope, add it in
						//
						((ObligationExpressionType) newParentId).getAttributeAssignmentExpression().add((AttributeAssignmentExpressionType) itemId);
					}
					//
					// Update our tracking
					//
					this.obligationExpressions.put((AttributeAssignmentExpressionType) itemId, (ObligationExpressionType) newParentId);
					//
					// Fire
					//
					this.fireItemSetChange();
					return true;
				}
			} else if (this.root instanceof AdviceExpressionsType) {
				//
				// Remove it from its parent object
				//
				AdviceExpressionType oldParent = this.adviceExpressions.get(itemId);
				if (oldParent.getAttributeAssignmentExpression().remove(itemId)) {
					//
					// See if its in the new parent
					//
					if (((AdviceExpressionType) newParentId).getAttributeAssignmentExpression().contains(itemId) == false) {
						//
						// Nope, add it in
						//
						((AdviceExpressionType) newParentId).getAttributeAssignmentExpression().add((AttributeAssignmentExpressionType) itemId);
					}
					//
					// Update our tracking
					//
					this.adviceExpressions.put((AttributeAssignmentExpressionType) itemId, (AdviceExpressionType) newParentId);
					//
					// Fire
					//
					this.fireItemSetChange();
					return true;
				}
			}
			return false;
		}
		if (itemId instanceof AttributeValueType && newParentId instanceof AttributeAssignmentExpressionType) {
			AttributeAssignmentExpressionType oldParent = this.values.get(itemId);
			if (oldParent != null &&
				oldParent.getExpression() != null &&
				oldParent.getExpression().getValue() != null &&
				oldParent.getExpression().getValue() == itemId) {
				//
				// Remove from old parent
				//
				oldParent.setExpression(null);
				//
				// Put in new parent
				//
				((AttributeAssignmentExpressionType) newParentId).setExpression(new ObjectFactory().createAttributeValue((AttributeValueType) itemId));
				//
				// track it
				//
				this.values.put((AttributeValueType) itemId, (AttributeAssignmentExpressionType) newParentId);
				//
				// Fire
				//
				this.fireItemSetChange();
				return true;
			}
			return false;
		}
		if (itemId instanceof AttributeDesignatorType && newParentId instanceof AttributeAssignmentExpressionType) {
			AttributeAssignmentExpressionType oldParent = this.designators.get(itemId);
			if (oldParent != null &&
				oldParent.getExpression() != null &&
				oldParent.getExpression().getValue() != null &&
				oldParent.getExpression().getValue() == itemId) {
				//
				// Remove from old parent
				//
				oldParent.setExpression(null);
				//
				// Put in new parent
				//
				((AttributeAssignmentExpressionType) newParentId).setExpression(new ObjectFactory().createAttributeDesignator((AttributeDesignatorType) itemId));
				//
				// track it
				//
				this.designators.put((AttributeDesignatorType) itemId, (AttributeAssignmentExpressionType) newParentId);
				//
				// Fire
				//
				this.fireItemSetChange();
				return true;
			}
			return false;
		}
		if (itemId instanceof AttributeSelectorType && newParentId instanceof AttributeAssignmentExpressionType) {
			AttributeAssignmentExpressionType oldParent = this.selectors.get(itemId);
			if (oldParent != null &&
				oldParent.getExpression() != null &&
				oldParent.getExpression().getValue() != null &&
				oldParent.getExpression().getValue() == itemId) {
				//
				// Remove from old parent
				//
				oldParent.setExpression(null);
				//
				// Put in new parent
				//
				((AttributeAssignmentExpressionType) newParentId).setExpression(new ObjectFactory().createAttributeSelector((AttributeSelectorType) itemId));
				//
				// track it
				//
				this.selectors.put((AttributeSelectorType) itemId, (AttributeAssignmentExpressionType) newParentId);
				//
				// Fire
				//
				this.fireItemSetChange();
				return true;
			}
			return false;
		}
		if (itemId instanceof ApplyType && newParentId instanceof AttributeAssignmentExpressionType) {
			AttributeAssignmentExpressionType oldParent = this.applys.get(itemId);
			if (oldParent != null &&
				oldParent.getExpression() != null &&
				oldParent.getExpression().getValue() != null &&
				oldParent.getExpression().getValue() == itemId) {
				//
				// Remove from old parent
				//
				oldParent.setExpression(null);
				//
				// Put in new parent
				//
				((AttributeAssignmentExpressionType) newParentId).setExpression(new ObjectFactory().createApply((ApplyType) itemId));
				//
				// track it
				//
				this.applys.put((ApplyType) itemId, (AttributeAssignmentExpressionType) newParentId);
				//
				// Fire
				//
				this.fireItemSetChange();
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean areChildrenAllowed(Object itemId) {
		if (itemId instanceof AdviceExpressionType) {
			return true;
		}
		if (itemId instanceof ObligationExpressionType) {
			return true;
		}
		if (itemId instanceof AttributeAssignmentExpressionType) {
			return true;
		}
		if (itemId instanceof AttributeValueType) {
			return false;
		}
		if (itemId instanceof AttributeDesignatorType) {
			return false;
		}
		if (itemId instanceof AttributeSelectorType) {
			return false;
		}
		if (itemId instanceof ApplyType) {
			return false;
		}
		return false;
	}

	@Override
	public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
		if (itemId instanceof AdviceExpressionType) {
			return (areChildrenAllowed ? true : false);
		}
		if (itemId instanceof ObligationExpressionType) {
			return (areChildrenAllowed ? true : false);
		}
		if (itemId instanceof AttributeAssignmentExpressionType) {
			return (areChildrenAllowed ? true : false);
		}
		if (itemId instanceof AttributeValueType) {
			return (areChildrenAllowed == false ? true : false);
		}
		if (itemId instanceof AttributeDesignatorType) {
			return (areChildrenAllowed == false ? true : false);
		}
		if (itemId instanceof AttributeSelectorType) {
			return (areChildrenAllowed == false ? true : false);
		}
		if (itemId instanceof ApplyType) {
			return (areChildrenAllowed == false ? true : false);
		}
		return false;
	}

	@Override
	public boolean isRoot(Object itemId) {
		if (this.root instanceof AdviceExpressionsType) {
			return this.rootAdvice.contains(itemId);
		} else if (this.root instanceof ObligationExpressionsType) {
			return this.rootObligations.contains(itemId);
		}
		return false;
	}

	@Override
	public boolean hasChildren(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("hasChildren: " + itemId);
		}
		if (itemId instanceof AdviceExpressionType && this.root instanceof AdviceExpressionsType &&
				this.rootAdvice.contains(itemId)) {
			return ((AdviceExpressionType) itemId).getAttributeAssignmentExpression().size() > 0;
		}
		if (itemId instanceof ObligationExpressionType && this.root instanceof ObligationExpressionsType &&
				this.rootObligations.contains(itemId)) {
			return ((ObligationExpressionType) itemId).getAttributeAssignmentExpression().size() > 0;
		}
		if (itemId instanceof AttributeAssignmentExpressionType) {
			if (this.root instanceof ObligationExpressionsType) {
				return this.obligationExpressions.size() > 0;
			} else if (this.root instanceof AdviceExpressionsType) {
				return this.adviceExpressions.size() > 0;
			}
		}
		return false;
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeItem: " + itemId);
		}
		if (this.root instanceof ObligationExpressionsType) {
			if (itemId instanceof ObligationExpressionType) {
				if (((ObligationExpressionsType) this.root).getObligationExpression().remove(itemId)) {
					//
					// Remove this
					//
					if (this.rootObligations.remove(itemId) == false) {
						//
						//
						//
						assert false;
						logger.error("Removing item " + itemId + " failed to remove it from root obligation list");
					}
					//
					// Notify
					//
					this.fireItemSetChange();
					return true ;
				}
			} else if (itemId instanceof AttributeAssignmentExpressionType) {
				ObligationExpressionType parent = this.obligationExpressions.get(itemId);
				if (parent != null && parent.getAttributeAssignmentExpression().remove(itemId)) {
					if (this.obligationExpressions.remove(itemId) == null) {
						assert false;
						logger.error("Removing item " + itemId + " failed to remove it from obligation expressions map");
					}
					//
					// Notify
					//
					this.fireItemSetChange();
					return true;
				}
			}
		} else if (this.root instanceof AdviceExpressionsType) {
			if (itemId instanceof AdviceExpressionType) {
				if (((AdviceExpressionsType) this.root).getAdviceExpression().remove(itemId)) {
					if (this.rootAdvice.remove(itemId) == false) {
						assert false;
						logger.error("Removing item " + itemId + " failed to remove it from root advice list");
					}
					//
					// Notify
					//
					this.fireItemSetChange();
					return true;
				}
			} else if (itemId instanceof AttributeAssignmentExpressionType) {
				AdviceExpressionType parent = this.adviceExpressions.get(itemId);
				if (parent != null && parent.getAttributeAssignmentExpression().remove(itemId)) {
					if (this.adviceExpressions.remove(itemId) == null) {
						assert false;
						logger.error("Removing item " + itemId + " failed to remove it from advice expressions map");
					}
					//
					// Notify
					//
					this.fireItemSetChange();
					return true;
				}
			}
		}
		if (itemId instanceof AttributeValueType) {
			AttributeAssignmentExpressionType parent = this.values.get(itemId);
			if (parent != null && 
				parent.getExpression() != null && 
				parent.getExpression().getValue() != null && 
				parent.getExpression().getValue().equals(itemId)) {
				parent.setExpression(null);
				//
				// Notify
				//
				this.fireItemSetChange();
				return this.values.remove(itemId) == null;
			}
		}
		if (itemId instanceof AttributeDesignatorType) {
			AttributeAssignmentExpressionType parent = this.designators.get(itemId);
			if (parent != null && 
				parent.getExpression() != null && 
				parent.getExpression().getValue() != null && 
				parent.getExpression().getValue().equals(itemId)) {
				parent.setExpression(null);
				//
				// Notify
				//
				this.fireItemSetChange();
				return this.designators.remove(itemId) == null;
			}
		}
		if (itemId instanceof AttributeSelectorType) {
			AttributeAssignmentExpressionType parent = this.selectors.get(itemId);
			if (parent != null && 
				parent.getExpression() != null && 
				parent.getExpression().getValue() != null && 
				parent.getExpression().getValue().equals(itemId)) {
				parent.setExpression(null);
				//
				// Notify
				//
				this.fireItemSetChange();
				return this.selectors.remove(itemId) == null;
			}
		}
		if (itemId instanceof ApplyType) {
			AttributeAssignmentExpressionType parent = this.applys.get(itemId);
			if (parent != null && 
				parent.getExpression() != null && 
				parent.getExpression().getValue() != null && 
				parent.getExpression().getValue().equals(itemId)) {
				parent.setExpression(null);
				//
				// Notify
				//
				this.fireItemSetChange();
				return this.applys.remove(itemId) == null;
			}
		}
		return false;
	}

	public class ObAdviceItem implements Item {
		private static final long serialVersionUID = 1L;
		private final Object data;
		
		public ObAdviceItem(Object data) {
			this.data = data;
		}
		
		public String getName() {
			if (this.data instanceof AdviceExpressionType) {
				return "Advice";
			}
			if (this.data instanceof ObligationExpressionType) {
				return "Obligation";
			}
			if (this.data instanceof AttributeAssignmentExpressionType) {
				return "Attribute Assignment Expression";
			}
			if (this.data instanceof AttributeValueType) {
				return "Attribute Value";
			}
			if (this.data instanceof AttributeDesignatorType) {
				return "Attribute Designator";
			}
			if (this.data instanceof AttributeSelectorType) {
				return "Attribute Selector";
			}
			if (this.data instanceof ApplyType) {
				return "Apply";
			}
			return null;
		}
		
		public String getId() {
			if (this.data instanceof AdviceExpressionType) {
				return ((AdviceExpressionType) this.data).getAdviceId();
			}
			if (this.data instanceof ObligationExpressionType) {
				return ((ObligationExpressionType) this.data).getObligationId();
			}
			if (this.data instanceof AttributeAssignmentExpressionType) {
				return ((AttributeAssignmentExpressionType) this.data).getAttributeId();
			}
			if (this.data instanceof AttributeValueType) {
				StringBuilder builder = new StringBuilder();
				for (Object content : ((AttributeValueType) this.data).getContent()) {
					builder.append(content);
				}
				return builder.toString();
			}
			if (this.data instanceof AttributeDesignatorType) {
				return ((AttributeDesignatorType) this.data).getAttributeId();
			}
			if (this.data instanceof AttributeSelectorType) {
				return ((AttributeSelectorType) this.data).getContextSelectorId();
			}
			if (this.data instanceof ApplyType) {
				return ((ApplyType) this.data).getFunctionId();
			}
			return null;
		}
		
		public String getIdShort() {
			String id = this.getId();
			if (id == null) {
				return id;
			}
			if (this.data instanceof AttributeValueType) {
				return id;
			}
			//
			// Make it short
			//
			String[] parts = id.split("[:]");
			
			if (parts != null && parts.length > 0) {
				return parts[parts.length - 1];
			}
			return id;
		}
		
		public String getEffect() {
			if (this.data instanceof AdviceExpressionType) {
				return ((AdviceExpressionType) this.data).getAppliesTo().toString();
			}
			if (this.data instanceof ObligationExpressionType) {
				return ((ObligationExpressionType) this.data).getFulfillOn().toString();
			}
			return null;
		}
		
		public String getDatatype() {
			/*
			if (this.data instanceof AdviceExpressionType) {
			}
			if (this.data instanceof ObligationExpressionType) {
			}
			if (this.data instanceof AttributeAssignmentExpressionType) {
			}
			*/
			if (this.data instanceof AttributeValueType) {
				return ((AttributeValueType) this.data).getDataType();
			}
			if (this.data instanceof AttributeDesignatorType) {
				return ((AttributeDesignatorType) this.data).getDataType();
			}
			if (this.data instanceof AttributeSelectorType) {
				return ((AttributeSelectorType) this.data).getDataType();
			}
			/*
			if (this.data instanceof ApplyType) {
				
			}
			*/
			return null;
		}

		public String getDatatypeShort() {
			String dt = this.getDatatype();
			if (dt == null) {
				return dt;
			}
			//
			// Get short part
			//
			int index = dt.lastIndexOf('#');
			if (index == -1) {
				String[] parts = dt.split("[:]");
				
				if (parts != null && parts.length > 0) {
					return parts[parts.length - 1];
				}
			} else {
				return dt.substring(index + 1);
			}
			return dt;
		}
		
		public String getCategory() {
			/*
			if (this.data instanceof AdviceExpressionType) {
			}
			if (this.data instanceof ObligationExpressionType) {
			}
			*/
			if (this.data instanceof AttributeAssignmentExpressionType) {
				return ((AttributeAssignmentExpressionType) this.data).getCategory();
			}
			/*
			if (this.data instanceof AttributeValueType) {
			}
			*/
			if (this.data instanceof AttributeDesignatorType) {
				return ((AttributeDesignatorType) this.data).getCategory();
			}
			if (this.data instanceof AttributeSelectorType) {
				return ((AttributeSelectorType) this.data).getCategory();
			}
			/*
			if (this.data instanceof ApplyType) {
				
			}
			*/
			return null;
		}
		
		public String getCategoryShort() {
			String id = this.getCategory();
			if (id == null) {
				return id;
			}
			String[] parts = id.split("[:]");
			
			if (parts != null && parts.length > 0) {
				return parts[parts.length - 1];
			}
			return id;
		}
		
		@Override
		public Property<?> getItemProperty(Object id) {
            return getContainerProperty(this.data, id);
		}
		@Override
		public Collection<?> getItemPropertyIds() {
           return getContainerPropertyIds();
		}
		@Override
		public boolean addItemProperty(Object id, @SuppressWarnings("rawtypes") Property property) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Expression container does not support adding new properties");
		}
		@Override
		public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Expression container does not support removing properties");
		}		
	}

}
