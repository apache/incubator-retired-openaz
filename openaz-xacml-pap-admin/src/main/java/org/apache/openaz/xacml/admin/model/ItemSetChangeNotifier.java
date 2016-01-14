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

import java.io.Serializable;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedList;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;

public class ItemSetChangeNotifier implements Container.ItemSetChangeNotifier {
	private static final long serialVersionUID = 1L;
    private Collection<Container.ItemSetChangeListener> itemSetChangeListeners = null;
    private Container container = null;
    
    public ItemSetChangeNotifier() {
    }
    
    protected void setContainer(Container c) {
    	this.container = c;
    }

	@Override
	public void addItemSetChangeListener(ItemSetChangeListener listener) {
        if (getItemSetChangeListeners() == null) {
            setItemSetChangeListeners(new LinkedList<Container.ItemSetChangeListener>());
        }
        getItemSetChangeListeners().add(listener);	}

	// TODO - Container.ItemSetChangeNotifier.addListener has been deprecated and replaced with
	// Container.ItemSetChangeNotifier.addItemSetChangeListener
	@Override
	public void addListener(ItemSetChangeListener listener) {
        addItemSetChangeListener(listener);
	}

	@Override
	public void removeItemSetChangeListener(ItemSetChangeListener listener) {
        if (getItemSetChangeListeners() != null) {
            getItemSetChangeListeners().remove(listener);
        }
    }

	// TODO - Container.ItemSetChangeNotifier.removeListener has been deprecated and replaced with
	// Container.ItemSetChangeNotifier.removeItemSetChangeListener
	@Override
	public void removeListener(ItemSetChangeListener listener) {
        removeItemSetChangeListener(listener);
	}
	
	protected static class BaseItemSetChangeEvent extends EventObject implements
	    Container.ItemSetChangeEvent, Serializable {
		private static final long serialVersionUID = 1L;

		protected BaseItemSetChangeEvent(Container source) {
		    super(source);
		}
		
		@Override
		public Container getContainer() {
		    return (Container) getSource();
		}
	}

    protected void setItemSetChangeListeners(
            Collection<Container.ItemSetChangeListener> itemSetChangeListeners) {
        this.itemSetChangeListeners = itemSetChangeListeners;
    }
    protected Collection<Container.ItemSetChangeListener> getItemSetChangeListeners() {
        return itemSetChangeListeners;
    }
   /**
     * Sends a simple Item set change event to all interested listeners,
     * indicating that anything in the contents may have changed (items added,
     * removed etc.).
     */
    protected void fireItemSetChange() {
        fireItemSetChange(new BaseItemSetChangeEvent(this.container));
    }

    /**
     * Sends an Item set change event to all registered interested listeners.
     * 
     * @param event
     *            the item set change event to send, optionally with additional
     *            information
     */
    protected void fireItemSetChange(ItemSetChangeEvent event) {
        if (getItemSetChangeListeners() != null) {
            final Object[] l = getItemSetChangeListeners().toArray();
            for (int i = 0; i < l.length; i++) {
                ((Container.ItemSetChangeListener) l[i])
                        .containerItemSetChange(event);
            }
        }
    }
}
