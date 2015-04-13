package org.openliberty.openaz.pepapi;

import java.util.Set;

/**
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
public interface ObligationStore {
	
	/**
	 * 
	 * @param oHandlerClass
	 * @return
	 */
	public Set<Obligation> getHandlerObligations(Class<?> oHandlerClass);
	
	/**
	 * 
	 * @param oHandlerClass
	 * @param obligationId
	 * @return
	 */
	public Obligation getHandlerObligationById(Class<?> oHandlerClass, String obligationId);
	
}
