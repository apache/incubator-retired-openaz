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
import java.util.Map;

import javax.xml.bind.JAXBElement;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeSelectorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.FunctionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableDefinitionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.VariableReferenceType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.openaz.xacml.admin.jpa.FunctionArgument;
import org.apache.openaz.xacml.admin.jpa.FunctionDefinition;
import org.apache.openaz.xacml.admin.util.JPAUtils;
import org.apache.openaz.xacml.admin.util.XACMLFunctionValidator;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;

public class ExpressionContainer extends ItemSetChangeNotifier implements Container.Hierarchical, Container.ItemSetChangeNotifier {
	private static final long serialVersionUID = 1L;
	private static Log logger	= LogFactory.getLog(ExpressionContainer.class);
	
	/**
     * String identifier of a file's "name" property.
     */
    public static String PROPERTY_NAME = "Name";

    /**
     * String identifier of an object's "id" property.
     */
    public static String PROPERTY_ID = "Id";

    /**
     * String identifier of an object's "datatype" property.
     */
    public static String PROPERTY_DATATYPE_SHORT = "shortDatatype";

    /**
     * String identifier of an object's "id" property.
     */
    public static String PROPERTY_ID_SHORT = "shortId";

    /**
     * String identifier of an object's "datatype" property.
     */
    public static String PROPERTY_DATATYPE = "Datatype";

    /**
     * List of the string identifiers for the available properties.
     */
    public static Collection<String> EXPRESSION_PROPERTIES;

    private final static Method EXPRESSIONITEM_NAME;
    
    private final static Method EXPRESSIONITEM_ID;

    private final static Method EXPRESSIONITEM_DATATYPE;

    private final static Method EXPRESSIONITEM_ID_SHORT;

    private final static Method EXPRESSIONITEM_DATATYPE_SHORT;

    static {
    	EXPRESSION_PROPERTIES = new ArrayList<String>();
    	EXPRESSION_PROPERTIES.add(PROPERTY_NAME);
    	EXPRESSION_PROPERTIES.add(PROPERTY_ID);
    	EXPRESSION_PROPERTIES.add(PROPERTY_DATATYPE);
    	EXPRESSION_PROPERTIES.add(PROPERTY_ID_SHORT);
    	EXPRESSION_PROPERTIES.add(PROPERTY_DATATYPE_SHORT);
    	EXPRESSION_PROPERTIES = Collections.unmodifiableCollection(EXPRESSION_PROPERTIES);
    	try {
    		EXPRESSIONITEM_NAME = ExpressionItem.class.getMethod("getName", new Class[]{});
    		EXPRESSIONITEM_ID = ExpressionItem.class.getMethod("getId", new Class[]{});
    		EXPRESSIONITEM_DATATYPE = ExpressionItem.class.getMethod("getDatatype", new Class[]{});
    		EXPRESSIONITEM_ID_SHORT = ExpressionItem.class.getMethod("getIdShort", new Class[]{});
    		EXPRESSIONITEM_DATATYPE_SHORT = ExpressionItem.class.getMethod("getDatatypeShort", new Class[]{});
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(
                    "Internal error finding methods in PolicyContainer");
        }
    }
    
    protected class ApplyParent {
    	ApplyType apply;
    	FunctionArgument argument;
    	
    	public ApplyParent(ApplyType apply, FunctionArgument argument) {
    		this.apply = apply;
    		this.argument = argument;
    	}

		public ApplyType getApply() {
			return apply;
		}

		public void setApply(ApplyType apply) {
			this.apply = apply;
		}

		public FunctionArgument getArgument() {
			return argument;
		}

		public void setArgument(FunctionArgument argument) {
			this.argument = argument;
		}
    }
    //
    // Our parent object information and which argument we are
    // from our parent (relevant to the Apply).
    //
    private final Object parent;
    private final FunctionArgument argument;
    //
    // The root object of the expression
    //
    private Object root;
    //
    // Our helper tables for organization purposes and to
    // make sure the correct functions/datatypes are being
    // setup.
    //
    private Map<ApplyType, ApplyParent>					applys = new HashMap<ApplyType, ApplyParent>();
    private Map<FunctionType, ApplyParent>				functions = new HashMap<FunctionType, ApplyParent>();
    private Map<AttributeValueType, ApplyParent>		values = new HashMap<AttributeValueType, ApplyParent>();
    private Map<AttributeDesignatorType, ApplyParent>	designators = new HashMap<AttributeDesignatorType, ApplyParent>();
    private Map<AttributeSelectorType, ApplyParent>		selectors = new HashMap<AttributeSelectorType, ApplyParent>();
    private Map<VariableReferenceType, ApplyParent>		variables = new HashMap<VariableReferenceType, ApplyParent>();
    private Map<ExpressionType, ApplyParent>			expressions = new HashMap<ExpressionType, ApplyParent>();
    	
    public ExpressionContainer(Object parent, Object root, FunctionArgument argument) {
		super();
		this.setContainer(this);
		this.parent = parent;
		this.root = root;
		this.argument = argument;
		this.initialize();
	}
    
    private void initialize() {
    	if (logger.isTraceEnabled()) {
    		logger.trace("Initializing: " + this.parent + " " + this.argument + " " + this.root);
    	}
    	//
    	// Make sure we support the parent object
    	//
    	@SuppressWarnings("unused")
		JAXBElement<?> rootElement = null;
    	if (this.parent instanceof ConditionType) {
    		rootElement = ((ConditionType) this.parent).getExpression();
    	} else if (this.parent instanceof VariableDefinitionType) {
    		rootElement = ((VariableDefinitionType) this.parent).getExpression();
    	} else if (this.parent instanceof AttributeAssignmentExpressionType) {
    		rootElement = ((AttributeAssignmentExpressionType) this.parent).getExpression();
    	} else if (this.parent instanceof ApplyType) {
    		//
    		// They must tell us which argument we are
    		//
    		if (this.argument == null) {
        		throw new IllegalArgumentException("Must supply Argument object when editing a parent ApplyType's child node");
    		}
    		//
    		// Finish the initialization
    		//
    		this.initializeRoot();
    	} else {
    		throw new IllegalArgumentException("Unsupported Parent Object: " + this.parent.getClass().getCanonicalName());
    	}
    	/*
    	//
    	// Check if there actually is a root
    	//
    	if (rootElement == null || rootElement.getValue() == null) {
    		//
    		// Creating a new one
    		//
    		return;
    	}
		//
		// Save the root
		//
		this.root = rootElement.getValue();
		*/
		//
		// Finish initializing
		//
		this.initializeRoot();
   }

    private void initializeRoot() {
    	//
    	// Sanity check
    	//
    	if (this.root == null) {
    		return;
    	}
		//
		// Figure out the expression type
		//
		if (this.root instanceof ApplyType) {
		   	if (logger.isTraceEnabled()) {
		   		logger.trace("Root Is Apply");
		   	}
		   	//
			// Save it
			//
			this.applys.put((ApplyType) this.root, null);
			//
			// Determine the function for this Apply
			//
			Map<String, FunctionDefinition> functions = JPAUtils.getFunctionIDMap();
			FunctionDefinition function  = functions.get(((ApplyType) this.root).getFunctionId());
			if (function == null) {
				logger.warn("root apply does not have a function defined");
				return;
			}
			//
			// Bring in its children
			//
			this.initializeChildren((ApplyType) this.root, function);
		} else if (this.root instanceof AttributeValueType) {
		   	if (logger.isTraceEnabled()) {
		   		logger.trace("Root Is Attribute Value");
		   	}
			//
			// Save it
			//
			this.values.put((AttributeValueType) this.root, null);
		} else if (this.root instanceof AttributeDesignatorType) {
		   	if (logger.isTraceEnabled()) {
		   		logger.trace("Root Is Attribute Designator");
		   	}
			//
			// Save it
			//
			this.designators.put((AttributeDesignatorType) this.root, null);
		} else if (this.root instanceof AttributeSelectorType) {
		   	if (logger.isTraceEnabled()) {
		   		logger.trace("Root Is Attribute Selector");
		   	}
			//
			// Save it
			//
			this.selectors.put((AttributeSelectorType) this.root, null);
		} else if (this.root instanceof VariableReferenceType) {
		   	if (logger.isTraceEnabled()) {
		   		logger.trace("Root Is Variable Reference");
		   	}
			//
			// Save it
			//
			this.variables.put((VariableReferenceType) this.root, null);
		} else if (this.root instanceof FunctionType) {
		   	if (logger.isTraceEnabled()) {
		   		logger.trace("Root Is Function");
		   	}
			//
			// Save it - Really? I don't think the root would ever be a function.
			//
			this.functions.put((FunctionType) this.root, null);
		} else {
			throw new IllegalArgumentException("Unsupported Expression Root Item: " + this.root.getClass().getCanonicalName());
		}
    }
    
    private void initializeChildren(ApplyType apply, FunctionDefinition function) {
    	int index = 1;
    	for (JAXBElement<?> child : apply.getExpression()) {
    		//
    		// Validate the child
    		//
    		if (child.getValue() == null) {
    			logger.warn("child element " + index + "has a null object.");
    			index++;
    			continue;
    		}
		   	if (logger.isTraceEnabled()) {
		   		logger.trace("Child " + index + " is " + child.getValue());
		   	}
    		//
    		// Get the argument for this child
    		//
    		if (function == null) {
    			throw new IllegalArgumentException("Apply has children but no function defined.");
    		}
    		FunctionArgument argument = XACMLFunctionValidator.getFunctionArgument(index, function);
		   	if (logger.isTraceEnabled()) {
		   		logger.trace("Child's argument is: " + argument);
		   	}
    		if (argument == null) {
    			//throw new Exception("Unable to find function argument: " + index + " " + function.getId() + " " + function.getShortname());
    			return;
    		}
			//
			// See if its another apply type
			//
    		if (child.getValue() instanceof ApplyType) {
    			//
    			// Save it
    			//
    			this.applys.put((ApplyType) child.getValue(), new ApplyParent(apply, argument));
    			//
    			// Get its function information
    			//
    			Map<String, FunctionDefinition> functions = JPAUtils.getFunctionIDMap();
    			FunctionDefinition childFunction  = functions.get(((ApplyType) child.getValue()).getFunctionId());
    			if (childFunction == null) {
    				logger.warn("Apply object " + index + " does not have a function defined");
    			} else {
	    			//
	    			// Bring in its children
	    			//
	    			this.initializeChildren((ApplyType) child.getValue(), childFunction);
    			}
    		} else if (child.getValue() instanceof AttributeValueType) {
    			//
    			// Save it
    			//
    			this.values.put((AttributeValueType) child.getValue(), new ApplyParent(apply, argument));
    		} else if (child.getValue() instanceof AttributeDesignatorType) {
    			//
    			// Save it
    			//
    			this.designators.put((AttributeDesignatorType) child.getValue(), new ApplyParent(apply, argument));
    		} else if (child.getValue() instanceof AttributeSelectorType) {
    			//
    			// Save it
    			//
    			this.selectors.put((AttributeSelectorType) child.getValue(), new ApplyParent(apply, argument));
    		} else if (child.getValue() instanceof VariableReferenceType) {
    			//
    			// Save it
    			//
    			this.variables.put((VariableReferenceType) child.getValue(), new ApplyParent(apply, argument));
    		} else if (child.getValue() instanceof FunctionType) {
    			//
    			// Save it
    			//
    			this.functions.put((FunctionType) child.getValue(), new ApplyParent(apply, argument));
    		} else if (child.getValue() instanceof ExpressionType) {
    			//
    			// Save it
    			//
    			this.expressions.put((ExpressionType) child.getValue(), new ApplyParent(apply, argument));
    		} else {
    			logger.error("Unknown child type: " + child.getClass().getCanonicalName());
    		}
    		index++;
    	}
    }
    
    protected boolean	isObjectSupported(Object itemId) {
		if (itemId instanceof ApplyType ||
			itemId instanceof AttributeValueType ||
			itemId instanceof AttributeDesignatorType ||
			itemId instanceof AttributeSelectorType ||
			itemId instanceof VariableReferenceType ||
			itemId instanceof FunctionType ||
			itemId instanceof ExpressionType) {
			return true;
		}
		return false;
    }
    
    protected boolean isParentObjectSupport(Object parent) {
		if (parent instanceof ApplyType ||
			parent instanceof ConditionType ||
			parent instanceof VariableDefinitionType ||
			parent instanceof AttributeAssignmentExpressionType) {
			return true;
		}
		return false;
    }
    
	public void updateItem(Object itemId) {
		//
		// Sanity check
		//
		if (this.isObjectSupported(itemId) == false) {
			return;// null;
		}
		//
		// Notify - the real reason for this function
		//
		this.fireItemSetChange();
		//
		// Return the item
		//
		//return new ExpressionItem(itemId);
	}

	@Override
	public Item getItem(Object itemId) {
		if (this.isObjectSupported(itemId) == false) {
			return null;
		}
		return new ExpressionItem(itemId);
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return EXPRESSION_PROPERTIES;
	}

	@Override
	public Collection<?> getItemIds() {
		final Collection<Object> items = new ArrayList<Object>();
		if (this.root != null) {
			//
			// Add the root object
			//
			items.add(this.root);
			//
			// If its an apply, it could have children
			//
			if (this.root instanceof ApplyType) {
				items.add(this.getChildrenIds((ApplyType) this.root, true));
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getItemIds (" + items.size() + "):" + items);
		}
		return Collections.unmodifiableCollection(items);
	}
	
	protected Collection<?> getChildrenIds(ApplyType apply, boolean recursive) {
		Collection<Object> items = new ArrayList<Object>();
    	for (JAXBElement<?> child : apply.getExpression()) {
    		//
    		// Make sure there's a value
    		//
    		if (child.getValue() == null) {
    			continue;
    		}
    		//
    		// What kind is it?
    		//
    		if (child.getValue() instanceof ApplyType) {
    			items.add(child.getValue());
    			//
    			// Do we add its children?
    			//
    			if (recursive) {
        			items.addAll(this.getChildrenIds((ApplyType) child.getValue(), true));
    			}
    		} else if (child.getValue() instanceof AttributeValueType) {
    			items.add(child.getValue());
    		} else if (child.getValue() instanceof AttributeDesignatorType) {
    			items.add(child.getValue());
    		} else if (child.getValue() instanceof AttributeSelectorType) {
    			items.add(child.getValue());
    		} else if (child.getValue() instanceof VariableReferenceType) {
    			items.add(child.getValue());
    		} else if (child.getValue() instanceof FunctionType) {
    			items.add(child.getValue());
    		} else if (child.getValue() instanceof ExpressionType) {
    			items.add(child.getValue());
    		}
    	}		
		if (logger.isTraceEnabled()) {
			logger.trace("getChildrenIds " + apply.getFunctionId() + " (" + items.size() + "):" + items);
		}
		return items;
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
		if (this.isObjectSupported(itemId) == false) {
			return null;
		}
        if (propertyId.equals(PROPERTY_NAME)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ExpressionItem(itemId), EXPRESSIONITEM_NAME, null);
        }

        if (propertyId.equals(PROPERTY_ID)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ExpressionItem(itemId), EXPRESSIONITEM_ID, null);
        }
		
        if (propertyId.equals(PROPERTY_DATATYPE)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ExpressionItem(itemId), EXPRESSIONITEM_DATATYPE, null);
        }
		
        if (propertyId.equals(PROPERTY_ID_SHORT)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ExpressionItem(itemId), EXPRESSIONITEM_ID_SHORT, null);
        }
		
        if (propertyId.equals(PROPERTY_DATATYPE_SHORT)) {
            return new MethodProperty<Object>(getType(propertyId),
                    new ExpressionItem(itemId), EXPRESSIONITEM_DATATYPE_SHORT, null);
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
        if (propertyId.equals(PROPERTY_DATATYPE)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_ID_SHORT)) {
            return String.class;
        }
        if (propertyId.equals(PROPERTY_DATATYPE_SHORT)) {
            return String.class;
        }
		return null;
	}

	@Override
	public int size() {
		int size = 0;
		size += this.applys.size();
		size += this.designators.size();
		size += this.functions.size();
		size += this.selectors.size();
		size += this.values.size();
		size += this.variables.size();
		size += this.expressions.size();
		return size;
	}

	@Override
	public boolean containsId(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("containsId: " + itemId);
		}
		if (itemId instanceof ApplyType) {
			return this.applys.containsKey(itemId);
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
		if (itemId instanceof VariableReferenceType) {
			return this.variables.containsKey(itemId);
		}
		if (itemId instanceof FunctionType) {
			return this.functions.containsKey(itemId);
		}
		if (itemId instanceof ExpressionType) {
			return this.expressions.containsKey(itemId);
		}
		return false;
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Please use the addItem(Object, Object) method instead.");
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("use addItem(Object itemId)");
	}
	
	public Item addItem(Object itemId, ApplyType parent, FunctionArgument argument) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("addItem: " + itemId + " " + parent + " " + argument);
		}
		//
		// Make sure we support the object
		//
		if (this.isObjectSupported(itemId) == false) {
			return null;
		}
		//
		// Is is a root?
		//
		if (parent == null) {
			//
			// Setting root
			//
			if (this.root != null) {
				throw new UnsupportedOperationException("Cannot add another root item. Remove the current root first.");
			}
			//
			// Save the root information
			//
			this.root = itemId;
			//
			// Add its children
			//
			this.initializeRoot();
			//
			// Add it to our root container
			//
			if (this.parent instanceof ApplyType) {
				((ApplyType) this.parent).getExpression().add(this.createElement(this.root));
			} else if (this.parent instanceof ConditionType) {
				((ConditionType) this.parent).setExpression(this.createElement(this.root));
			} else if (this.parent instanceof VariableDefinitionType) {
				((VariableDefinitionType) this.parent).setExpression(this.createElement(this.root));
			} else if (this.parent instanceof AttributeAssignmentExpressionType) {
				((AttributeAssignmentExpressionType) this.parent).setExpression(this.createElement(this.root));
			} else {
				logger.error("unknown parent class: " + this.parent.getClass().getCanonicalName());
			}
			//
			// Notify that we changed
			//
			this.fireItemSetChange();
			//
			// Return new item
			//
			return new ExpressionItem(this.root);
		}
		//
		// Check what kind of item this is
		//
		if (itemId instanceof ApplyType) {
			//
			this.applys.put((ApplyType) itemId, new ApplyParent(parent, argument));
			((ApplyType) parent).getExpression().add(new ObjectFactory().createApply((ApplyType) itemId));
			//
			// Get its function information
			//
			Map<String, FunctionDefinition> functions = JPAUtils.getFunctionIDMap();
			FunctionDefinition childFunction  = functions.get(((ApplyType) itemId).getFunctionId());
			if (childFunction == null) {
				//
				// NO function defined
				//
				logger.warn("no function defined for apply being added.");
			} else {
				//
				// Add its children
				//
				this.initializeChildren((ApplyType) itemId, childFunction);
			}
		} else if (itemId instanceof AttributeValueType) {
			//
			this.values.put((AttributeValueType) itemId, new ApplyParent(parent, argument));
			parent.getExpression().add(new ObjectFactory().createAttributeValue((AttributeValueType) itemId));
			//
		} else if (itemId instanceof AttributeDesignatorType) {
			//
			this.designators.put((AttributeDesignatorType) itemId, new ApplyParent(parent, argument));
			parent.getExpression().add(new ObjectFactory().createAttributeDesignator((AttributeDesignatorType) itemId));
			//
		} else if (itemId instanceof AttributeSelectorType) {
			//
			this.selectors.put((AttributeSelectorType) itemId, new ApplyParent(parent, argument));
			parent.getExpression().add(new ObjectFactory().createAttributeSelector((AttributeSelectorType) itemId));
			//
		} else if (itemId instanceof VariableReferenceType) {
			//
			this.variables.put((VariableReferenceType) itemId, new ApplyParent(parent, argument));
			parent.getExpression().add(new ObjectFactory().createVariableReference((VariableReferenceType) itemId));
			//
		} else if (itemId instanceof FunctionType) {
			//
			this.functions.put((FunctionType) itemId, new ApplyParent(parent, argument));
			parent.getExpression().add(new ObjectFactory().createFunction((FunctionType) itemId));
			//
		} else if (itemId instanceof ExpressionType) {
			//
			this.expressions.put((ExpressionType) itemId, new ApplyParent(parent, argument));
			parent.getExpression().add(new ObjectFactory().createExpression((ExpressionType) itemId));
		} else {
			logger.error("unknown itemId class: " + itemId.getClass().getCanonicalName());
			return null;
		}
		//
		// Notify
		//
		this.fireItemSetChange();
		return new ExpressionItem(itemId);
	}
	
	private JAXBElement<?> createElement(Object item) {
		if (item instanceof ApplyType) {
			return new ObjectFactory().createApply((ApplyType) item);
		} else if (item instanceof AttributeValueType) {
			return new ObjectFactory().createAttributeValue((AttributeValueType) item);
		} else if (item instanceof AttributeDesignatorType) {
			return new ObjectFactory().createAttributeDesignator((AttributeDesignatorType) item);
		} else if (item instanceof AttributeSelectorType) {
			return new ObjectFactory().createAttributeSelector((AttributeSelectorType) item);
		} else if (item instanceof VariableReferenceType) {
			return new ObjectFactory().createVariableReference((VariableReferenceType) item);
		} else if (item instanceof FunctionType) {
			return new ObjectFactory().createFunction((FunctionType) item);
		} else if (item instanceof ExpressionType) {
			return new ObjectFactory().createExpression((ExpressionType) item);
		}
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

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeAllItems: ");
		}
		boolean result = this.doRemoveAllItems();
		if (result == false) {
			return false;
		}
		//
		// Notify
		//
		this.fireItemSetChange();
		//
		// Done
		//
		return true;
	}

	public boolean doRemoveAllItems() throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("doRemoveAllItems: ");
		}
		//
		// Removing the root item, make sure its removed from
		// the parent.
		//
		if (this.parent instanceof ConditionType) {
			((ConditionType) this.parent).setExpression(null);
		} else if (this.parent instanceof VariableDefinitionType) {
			((VariableDefinitionType) this.parent).setExpression(null);
		} else if (this.parent instanceof AttributeAssignmentExpressionType) {
			((AttributeAssignmentExpressionType) this.parent).setExpression(null);
		} else if (this.parent instanceof ApplyType) {
			//
			// TODO ?? Special case
			//
			return false;
		} else {
			return false;
		}
		//
		// Null our root
		//
		this.root = null;
		//
		// Clear out our maps
		//
		this.applys.clear();
		this.designators.clear();
		this.functions.clear();
		this.values.clear();
		this.selectors.clear();
		this.variables.clear();
		this.expressions.clear();
		//
		// Done
		//
		return true;
	}

	@Override
	public Collection<?> getChildren(Object itemId) {
		final Collection<Object> items = new ArrayList<Object>();
		if (itemId instanceof ApplyType) {
			items.addAll(this.getChildrenIds((ApplyType) itemId, false));
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getChildren " + itemId + " (" + items.size() + "):" + items);
		}
		return Collections.unmodifiableCollection(items);
	}
	
	public FunctionArgument getArgument(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getArgument: " + itemId);
		}
		//
		// First check if its a root
		//
		if (this.isRoot(itemId)) {
			return null;
		}
		//
		// Not a root - should be in the maps
		//
		if (itemId instanceof ApplyType) {
			return this.applys.get(itemId).getArgument();
		}
		if (itemId instanceof AttributeValueType) {
			return this.values.get(itemId).getArgument();
		}
		if (itemId instanceof AttributeDesignatorType) {
			return this.designators.get(itemId).getArgument();
		}
		if (itemId instanceof AttributeSelectorType) {
			return this.selectors.get(itemId).getArgument();
		}
		if (itemId instanceof VariableReferenceType) {
			return this.variables.get(itemId).getArgument();
		}
		if (itemId instanceof FunctionType) {
			return this.functions.get(itemId).getArgument();
		}
		if (itemId instanceof ExpressionType) {
			return this.expressions.get(itemId).getArgument();
		}
		return null;
	}

	@Override
	public Object getParent(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getParent: " + itemId);
		}
		//
		// First check if its a root
		//
		if (this.isRoot(itemId)) {
			return null;
		}
		//
		// Not a root - should be in the maps
		//
		if (itemId instanceof ApplyType) {
			return this.applys.get(itemId).getApply();
		}
		if (itemId instanceof AttributeValueType) {
			return this.values.get(itemId).getApply();
		}
		if (itemId instanceof AttributeDesignatorType) {
			return this.designators.get(itemId).getApply();
		}
		if (itemId instanceof AttributeSelectorType) {
			return this.selectors.get(itemId).getApply();
		}
		if (itemId instanceof VariableReferenceType) {
			return this.variables.get(itemId).getApply();
		}
		if (itemId instanceof FunctionType) {
			return this.functions.get(itemId).getApply();
		}
		if (itemId instanceof ExpressionType) {
			return this.expressions.get(itemId).getApply();
		}
		return null;
	}

	@Override
	public Collection<?> rootItemIds() {
		final Collection<Object> items = new ArrayList<Object>();
		if (this.root != null) {
			items.add(this.root);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("rootItemIds " + " (" + items.size() + "):" + items);
		}
		return Collections.unmodifiableCollection(items);
	}

	@Override
	public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
		//
		// TODO we can support this later
		//
		throw new UnsupportedOperationException("Should we support this? Can be tricky. Most likely user dragging an item from one area to another. For now, use removeItem, addItem.");
	}

	@Override
	public boolean areChildrenAllowed(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("areChildrenAllowed: " + itemId);
		}
		if (itemId instanceof ApplyType) {
			return true;
		}
		return false;
	}

	@Override
	public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
			throws UnsupportedOperationException {
		if (itemId instanceof ApplyType && areChildrenAllowed) {
			return true;
		}
		if (! areChildrenAllowed) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isRoot(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("isRoot: " + itemId);
		}
		if (itemId == null) {
			logger.error("isRoot itemId is NULL");
		}
		return this.root == itemId;
	}

	@Override
	public boolean hasChildren(Object itemId) {
		if (logger.isTraceEnabled()) {
			logger.trace("hasChildren: " + itemId);
		}
		if (itemId instanceof ApplyType) {
			return ((ApplyType)itemId).getExpression().size() > 0;
		}
		return false;
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeItem: " + itemId);
		}
		//
		// Check if they are removing the root
		//
		if (this.root == itemId) {
			//
			// Removing the root item, make sure its removed from
			// the parent.
			//
			boolean result = this.doRemoveAllItems();
			if (result == false) {
				return false;
			}
			//
			// Notify
			//
			this.fireItemSetChange();
			return true;
		}
		//
		// There should be a parent
		//
		ApplyParent parent = null;
		//
		// Remove the item from the maps
		//
		if (itemId instanceof ApplyType) {
			parent = this.applys.get(itemId);
			if (parent == null) {
				return false;
			}
			if (this.applys.remove(itemId) == null) {
				return false;
			}
		} else if (itemId instanceof AttributeValueType) {
			parent = this.values.get(itemId);
			if (this.values.remove(itemId) == null) {
				return false;
			}
		} else if (itemId instanceof AttributeDesignatorType) {
			parent = this.designators.get(itemId);
			if (this.designators.remove(itemId) == null) {
				return false;
			}
		} else if (itemId instanceof AttributeSelectorType) {
			parent = this.selectors.get(itemId);
			if (this.selectors.remove(itemId) == null) {
				return false;
			}
		} else if (itemId instanceof VariableReferenceType) {
			parent = this.variables.get(itemId);
			if (this.variables.remove(itemId) == null) {
				return false;
			}
		} else if (itemId instanceof FunctionType) {
			parent = this.functions.get(itemId);
			if (this.functions.remove(itemId) == null) {
				return false;
			}
		} else if (itemId instanceof ExpressionType) {
			parent = this.expressions.get(itemId);
			if (this.expressions.remove(itemId) != null) {
				return false;
			}
		} else {
			return false;
		}
		//
		// Remove it from the parent Apply
		//
		boolean removed = false;
		for (JAXBElement<?> element : parent.getApply().getExpression()) {
			if (element.getValue().equals(itemId)) {
				if (parent.getApply().getExpression().remove(element)) {
					removed = true;
					break;
				}
				break;
			}
		}
		if (! removed) {
			//
			// Out of sync
			//
			logger.warn("Removing item from parent returned false, although we were able to remove it from our maps.");
		}
		//
		// Notify
		//
		this.fireItemSetChange();
		return true;
	}

	public class ExpressionItem implements Item {
		private static final long serialVersionUID = 1L;
		private final Object data;
		
		public ExpressionItem(Object data) {
			this.data = data;
		}
		
		public String getName() {
			if (this.data instanceof ApplyType) {
				if (((ApplyType) this.data).getDescription() != null) {
					return "Apply - " + ((ApplyType) this.data).getDescription();
				}
				return "Apply";
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
			if (this.data instanceof VariableReferenceType) {
				return "Variable Reference";
			}
			if (this.data instanceof FunctionType) {
				return "Function";
			}
			if (this.data instanceof ExpressionType) {
				return "<Argument Placeholder>";
			}
			return null;
		}
		
		public String getId() {
			if (this.data instanceof ApplyType) {
				return ((ApplyType) this.data).getFunctionId();
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
				return ((AttributeSelectorType) this.data).getPath();
			}
			if (this.data instanceof VariableReferenceType) {
				return ((VariableReferenceType) this.data).getVariableId();
			}
			if (this.data instanceof FunctionType) {
				return ((FunctionType) this.data).getFunctionId();
			}
			return null;
		}
		
		public String getIdShort() {
			String id = this.getId();
			if (id == null) {
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
		
		public String getDatatype() {
			if (this.data instanceof ApplyType) {
				
				Map<String, FunctionDefinition> map = JPAUtils.getFunctionIDMap();
				FunctionDefinition function = map.get(((ApplyType) this.data).getFunctionId());
				if (function != null) {
					return function.getDatatypeBean().getXacmlId();
				}
			}
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
			if (this.data instanceof VariableReferenceType) {
				if (this.function instanceof FunctionArgument) {
					return ((FunctionArgument) this.function).getDatatypeBean().getXacmlId();
				}
			}
			*/
			/*
			if (this.data instanceof FunctionType) {
				if (this.function instanceof FunctionArgument) {
					return ((FunctionArgument) this.function).getDatatypeBean().getXacmlId();
				}
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
