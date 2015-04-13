package org.openliberty.openaz.pepapi;


import java.util.Map;

/**
 * Abstraction for a Obligation Handler registration mechanism. Subclasses provide specific implementations.   
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 */
public interface ObligationHandlerRegistry {
	
	/**
	 * Returns a Map of <code>Matchable</code> implementations keyed by handler Class. 
	 * 
	 * @return
	 */
	public Map<Class<?>, Matchable<Obligation>> getRegisteredHandlerMap();
	
}
