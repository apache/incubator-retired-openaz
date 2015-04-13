package org.openliberty.openaz.pepapi;

/**
 * This enum provides the options that can be set using the
 * {@link org.openliberty.openaz.pepapi.PepResponseFactory} to determine the behavior when
 * {@link org.openliberty.openaz.pepapi.PepResponse#allowed()} is called AND the
 * decision is either Indeterminate or NotApplicable.
 *
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public enum PepResponseBehavior {
	
	/** The behavior is to allow (Permit) access by returning true when the condition for which this behavior is assigned occurs  */
    RETURN_YES,
    
	/** The behavior is to disallow (Deny) access by returning false when the condition for which this behavior is assigned occurs  */
    RETURN_NO,
    
	/** The behavior is to disallow (Deny) access by throwing a PepException when the condition for which this behavior is assigned occurs  */
    THROW_EXCEPTION
}
