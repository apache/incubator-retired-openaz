package org.openliberty.openaz.pepapi;

/**
 * An interface that may be implemented to process the 
 * PepResponse that is returned from the main decide()
 * call before the final results are returned to the user.
 *
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public interface PostDecisionHandler {
    
	/** 
	 * This method is used to apply post-decision custom
	 * processing to the {@link org.openliberty.openaz.pepapi.PepResponse} after it has
	 * been returned.
	 *
	 * @param request
	 * @throws org.openliberty.openaz.pepapi.PepException
	 */
    public void postDecide(PepRequest request, PepResponse response) 
    	throws PepException;
    
}
