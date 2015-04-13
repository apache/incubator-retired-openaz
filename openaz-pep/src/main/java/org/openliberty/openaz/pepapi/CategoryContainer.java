package org.openliberty.openaz.pepapi;

import com.att.research.xacml.api.Identifier;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Abstraction for an attribute container of a specific XACML category.
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 * 
 */
public class CategoryContainer {

	private final Map<String, Object[]> attributeMap;
	
	private final Identifier categoryIdentifier;

	CategoryContainer(Identifier categoryIdentifier) {
		this.categoryIdentifier = categoryIdentifier;
		this.attributeMap = new HashMap<String, Object[]>();
	}

	private final void addToMap(String id, Object[] values) {
		if (values != null && values.length > 0) {
			attributeMap.put(id, values);
		}else {
			throw new IllegalArgumentException("Values cannot be null");
		}
	}
	
	public Identifier getCategoryIdentifier() {
		return this.categoryIdentifier;
	}
	
	/**
	 * Returns all the contained attributes as a Map of key - value pairs.
	 * 
	 * @return
	 */
	public Map<String, Object[]> getAttributeMap() {
		return Collections.unmodifiableMap(attributeMap);
	}
	
	/**
	 * Add a new attribute with the given id and one or more String values
	 * 
	 * @param id
	 * @param values
	 * @throws IllegalArgumentException, if values are null;
	 */
	public void addAttribute(String id, String... values) {
		addToMap(id, values);
	}
	
	/**
	 * Add a new attribute with the given id and one or more Long values
	 * 
	 * @param id
	 * @param values
	 * @throws IllegalArgumentException, if values are null;
	 */
	public void addAttribute(String id, Long... values) {
		addToMap(id, values);
	}
	
	/**
	 * Add a new attribute with the given id and one or more Integer values
	 * 
	 * @param id
	 * @param values
	 * @throws IllegalArgumentException, if values are null;
	 */
	public void addAttribute(String id, Integer... values) {
		addToMap(id, values);
	}
	
	/**
	 * Add a new attribute with the given id and one or more Double values
	 * 
	 * @param id
	 * @param values
	 * @throws IllegalArgumentException, if values are null;
	 */
	public void addAttribute(String id, Double... values) {
		addToMap(id, values);
	}
	
	/**
	 * Add a new attribute with the given id and one or more Boolean values
	 * 
	 * @param id
	 * @param values
	 * @throws IllegalArgumentException, if values are null;
	 */
	public void addAttribute(String id, Boolean... values) {
		addToMap(id, values);
	}
	
	/**
	 * Add a new attribute with the given id and one or more <code>java.util.Date</code> values
	 * 
	 * @param id
	 * @param values
	 * @throws IllegalArgumentException, if values are null;
	 */
	public void addAttribute(String id, Date... values) {
		addToMap(id, values);
	}
	
	/**
	 * Add a new attribute with the given id and one or more URI values
	 * 
	 * @param id
	 * @param values
	 * @throws IllegalArgumentException, if values are null;
	 */
	public void addAttribute(String id, URI... values) {
		addToMap(id, values);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Entry<String, Object[]> e: attributeMap.entrySet()) {
			builder.append("Attribute Id: " + e.getKey());
			builder.append(", Attribute Values: ");
			for(Object o: e.getValue()) {
				builder.append(o.toString() + ", ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}
}
