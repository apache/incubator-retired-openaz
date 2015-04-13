package org.openliberty.openaz.pepapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an Obligation criteria that matches any of the supplied Obligation ids.
 * If no ids are provided, then any Obligation will be matched(catch-all).   
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchAnyObligation {
	
	/**
	 * 
	 * @return
	 */
	String[] value() default {};
		
}
