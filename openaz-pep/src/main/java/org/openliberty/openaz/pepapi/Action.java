package org.openliberty.openaz.pepapi;

import com.att.research.xacml.api.XACML3;

;

/**
 * Container class that maps attributes to predefined XACML Action category.
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
public class Action extends CategoryContainer {

	public static final String ACTION_ID_KEY = "ACTION_ID_KEY";
	
	private String actionIdValue;
	
	private Action() {
		super(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION);
	}
	
	/**
	 * Creates a new Action instance
	 * 
	 * @return
	 */
	public static Action newInstance() {
		return new Action();
	}
	
	/**
	 * Create a new Action instance containing a single default attribute with the given value
	 * 
	 * @param actionIdValue
	 * @return
	 */
	public static Action newInstance(String actionIdValue) {
		Action a = new Action();
		a.actionIdValue = actionIdValue;
		a.addAttribute(ACTION_ID_KEY, actionIdValue);
		return a;
	}
	
	/**
	 * Get the value for default attribute.
	 * 
	 * @return
	 */
	public String getActionIdValue() {
		return actionIdValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("action-id value: " + actionIdValue);
		builder.append("\n");
		builder.append(super.toString());
		return builder.toString();
	}
}
