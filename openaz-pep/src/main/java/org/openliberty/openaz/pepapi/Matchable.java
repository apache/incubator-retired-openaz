package org.openliberty.openaz.pepapi;

/**
 * Interface that abstracts an object that can be matched. Concrete implementations provide a match() function. 
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 * @param <T>
 */
public interface Matchable<T> {
	
	/**
	 * Returns a boolean result after matching the given Object 
	 * 
	 * @param t
	 * @return a <code>boolean</code> value 
	 */
	public boolean match(T t);
	
}
