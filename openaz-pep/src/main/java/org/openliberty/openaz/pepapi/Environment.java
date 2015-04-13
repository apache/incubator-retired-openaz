package org.openliberty.openaz.pepapi;

import com.att.research.xacml.api.XACML3;

/**
 * 
 * Container class that maps attributes to predefined XACML Environment category.
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
public final class Environment extends CategoryContainer {

	private Environment() {
		super(XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT);
	}
	
	/**
	 * Creates a new Environment instance
	 * 
	 * @return
	 */
	public static Environment newInstance() {
		return new Environment();
	}

}
