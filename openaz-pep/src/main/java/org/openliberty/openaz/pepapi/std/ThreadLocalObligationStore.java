package org.openliberty.openaz.pepapi.std;


import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationStore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Acts as a store for Obligation instances in the current thread of execution.
 * 
 * @author Ajith Nair, David Laurance, Darshak Kothari
 * 
 */
public final class ThreadLocalObligationStore implements ObligationStore {
	
	private static final ThreadLocal<Map<Class<?>, Set<Obligation>>> obligationMapContainer =
			new ThreadLocal<Map<Class<?>, Set<Obligation>>>();
	
	private ThreadLocalObligationStore(){}
	
	public static ThreadLocalObligationStore newInstance() {
		return new ThreadLocalObligationStore();
	}
	
	/**
	 * Set Obligations for the current thread of execution.
	 * 
	 * @param obligationMap	a <code>Map</code> containing <code>Obligation</code> instances keyed by ObligationHandler Class.
	 */
	void setObligations(Map<Class<?>, Set<Obligation>> obligationMap) {
		if(obligationMap != null && !obligationMap.isEmpty()) {
			obligationMapContainer.set(Collections.unmodifiableMap(obligationMap));
		}else {
			obligationMapContainer.set(null);
		}
	}
	
	/**
	 * Returns all obligations in the current thread of execution. 
	 * 
	 * @return	a <code>Set</code> of <code>Obligation</code> instances.
	 */
	public Set<Obligation> getAllObligations() {
		Set<Obligation> allObligations = new HashSet<Obligation>();
		Map<Class<?>, Set<Obligation>> obligationMap = obligationMapContainer.get();
		if(obligationMap != null){
			for(Entry<Class<?>, Set<Obligation>> e: obligationMap.entrySet()){
				allObligations.addAll(e.getValue());
			}
		}
		return allObligations;
	}
	
	/**
	 * Returns all obligations that the given ObligationHandler can handle, in the current thread of execution.
	 * 
	 * @param oHandlerClass
	 * @return a <code>Set</code> of <code>Obligation</code> instances.
	 */
	@Override
	public Set<Obligation> getHandlerObligations(Class<?> oHandlerClass) {
		Set<Obligation> obligations = new HashSet<Obligation>();
		Map<Class<?>, Set<Obligation>> obligationMap = obligationMapContainer.get();
		if(obligationMap != null && obligationMap.get(oHandlerClass) != null){
			obligations.addAll(obligationMap.get(oHandlerClass));
		}
		return obligations;
	}
	

	@Override
	public Obligation getHandlerObligationById(Class<?> oHandlerClass,
			String obligationId) {
		Set<Obligation> obligations = getHandlerObligations(oHandlerClass);
		if(obligations != null){
			for(Obligation obligation: obligations){
				if(obligation.getId().equals(obligationId)){
					return obligation;
				}
			}
		}
		return null;
	}
	
	/**
	 * Clear all obligations in the current thread.
	 */
	void clear() {
		obligationMapContainer.remove();
	}
}
