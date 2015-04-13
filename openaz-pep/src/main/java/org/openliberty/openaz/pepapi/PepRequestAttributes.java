package org.openliberty.openaz.pepapi;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.RequestAttributes;

import java.net.URI;
import java.util.Date;

/**
 * Convenient wrapper around a RequestAttributes{@link com.att.research.xacml.api.RequestAttributes} instance,
 * representing a collection of request attributes that belong to a particular category.
 *
 */
public interface PepRequestAttributes {

	/**
	 * Returns an Indentifier representing the attribute category that the PepRequestAttributes encapsulates
	 *
	 * @return Identifier
	 */
	public Identifier getCategory();

	/**
	 * Returns an id representing the xml:id
	 *
	 * @return Identifier
	 */
	public String getId();

	/**
	 * Creates and adds an attribute with the name as the AttributeId,
	 * Date array elements as AttributeValue(s) into the underlying attribute collection.
	 * The attribute will NOT be returned by the PDP in the response after request evaluation.
	 * 
	 * @param name
	 *            a string with a name to be used as AttributeId
	 * @param values
	 *            a Date array to be used as AttributeValue(s)
	 * @throws IllegalArgumentException if the array is null
	 */
	public void addAttribute(String name, Date... values);

	/**
	 * Creates and adds an attribute with the name as the AttributeId,
	 * String array elements as AttributeValue(s) into the underlying attribute collection.
	 * The attribute will NOT be returned by the PDP in the response after request evaluation.
	 *
	 * @param name
	 *            a string with a name to be used as AttributeId
	 * @param values
	 *            a String array to be used as AttributeValue(s)
	 * @throws IllegalArgumentException if the array is null
	 */
	public void addAttribute(String name, String... values);

	/**
	 * Creates and adds an attribute with the name as the AttributeId,
	 * Integer array elements as AttributeValue(s) into the underlying attribute collection.
	 * The attribute will NOT be returned by the PDP in the response after request evaluation.
	 *
	 * @param name
	 *            a string with a name to be used as AttributeId
	 * @param values
	 *            an Integer array to be used as AttributeValue(s)
	 * @throws IllegalArgumentException if the array is null
	 */
	public void addAttribute(String name, Integer... values);

	/**
	 * Creates and adds an attribute with the name as the AttributeId,
	 * Boolean array elements as AttributeValue(s) into the underlying attribute collection.
	 * The attribute will NOT be returned by the PDP in the response after request evaluation.
	 *
	 * @param name
	 *            a string with a name to be used as AttributeId
	 * @param values
	 *            a Boolean array to be used as AttributeValue(s)
	 * @throws IllegalArgumentException if the array is null
	 */
	public void addAttribute(String name, Boolean... values);


	/**
	 * Creates and adds an attribute with the name as the AttributeId,
	 * Long array elements as AttributeValue(s) into the underlying attribute collection.
	 * The attribute will NOT be returned by the PDP in the response after request evaluation.
	 *
	 * @param name
	 *            a string with a name to be used as AttributeId
	 * @param values
	 *            a Long array to be used as AttributeValue(s)
	 * @throws IllegalArgumentException if the array is null
	 */
	public void addAttribute(String name, Long... values);

	/**
	 * Creates and adds an attribute with the name as the AttributeId,
	 * Double array elements as AttributeValue(s) into the underlying attribute collection.
	 * The attribute will NOT be returned by the PDP in the response after request evaluation.
	 *
	 * @param name
	 *            a string with a name to be used as AttributeId
	 * @param values
	 *            a Double array to be used as AttributeValue(s)
	 * @throws IllegalArgumentException if the array is null
	 */
	public void addAttribute(String name, Double... values);

	/**
	 * Creates and adds an attribute with the name as the AttributeId,
	 * URI array elements as AttributeValue(s) into the underlying attribute collection.
	 * The attribute will NOT be returned by the PDP in the response after request evaluation.
	 * 
	 * @param name
	 *            a string AttributeId of the attribute being set
	 * @param values
	 *            a URI array to be used as AttributeValue(s
	 * @throws IllegalArgumentException if the array is null
	 */
	public void addAttribute(String name, URI... values);

	/**
	 *
	 * @return
	 */
	public RequestAttributes getWrappedRequestAttributes();

}
