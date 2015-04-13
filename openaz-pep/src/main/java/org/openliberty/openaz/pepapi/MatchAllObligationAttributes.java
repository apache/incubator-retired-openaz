package org.openliberty.openaz.pepapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a union of Obligation Attribute match criterion. 
 * All attribute criterion supplied will be conjunctively matched by the framework.
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchAllObligationAttributes {
	
	/**
	 * 
	 * @return
	 */
	Attribute[] value();
	
}
