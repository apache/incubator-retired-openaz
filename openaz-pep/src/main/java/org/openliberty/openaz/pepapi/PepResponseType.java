package org.openliberty.openaz.pepapi;

/**
 *
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public enum PepResponseType {
	/** The PepResponse returned for this query type will contain
	 * only the list of resource action associations that are
	 * allowed.
	 */
	ONLY_ALLOWED_RESULTS, 
	/**
	 * The PepResponse returned for this query type will contain
	 * only the list of resource action associations that are
	 * denied.
	 */
	ONLY_DENIED_RESULTS, 
	/** The PepResponse returned for this query type will contain
	 * the complete list of results for each resource action association
	 * that was requested, including allowed, denied, notapplicable,
	 * and indeterminate.
	 */
	ALL_RESULTS;
}
