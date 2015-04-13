package org.openliberty.openaz.pepapi;

/**
 * An interface that can be used for preliminary processing
 * of a PepRequest before it is actually submitted to the 
 * main decide() method.
 *
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public interface PreDecisionHandler {
    
	/** 
	 * This method is used to apply preliminary custom
	 * processing to the {@link org.openliberty.openaz.pepapi.PepRequest} prior to its
	 * being submitted.
	 *
	 * @param request
	 * @throws org.openliberty.openaz.pepapi.PepException
	 */
	public void preDecide(PepRequest request) 
    	throws PepException;
}
