package org.openliberty.openaz.pepapi;

import java.util.Map;

/**
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 *
 */
public interface ObligationRouter {
	
	/**
	 * 
	 * @param obligationMap
	 */
	public void routeObligations(Map<String, Obligation> obligationMap);
	
}
