package org.openliberty.openaz.pepapi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Represents an Attribute match criterion, where an attribute with the given Id can take any of the values provided.
 * If no value is available, then value matching is ignored. 
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
@Target({})
@Retention(RetentionPolicy.CLASS)
public @interface Attribute {
	
	/**
	 * 
	 * @return
	 */
	String id();
	
	/**
	 * 
	 * @return
	 */
	String[] anyValue() default {};
}
