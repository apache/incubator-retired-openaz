package org.openliberty.openaz.pepapi;

import java.util.Map;

/**
 * The Obligation interface provides access to an Obligation
 * object implementation that contains a set of zero or more
 * Attributes.
 * <p>
 * The Obligation has an id: {@link #getId()}
 * <p> 
 * Each attribute has an id, as well, which are used as the key Strings
 * of the Maps returned by method:
 * <ul>
 * <li>{@link #getAttributeMap()}</li>
 * </ul>
 * Each key String has an associated value, which can be an
 * an array of Objects.
 * <p>
 *
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public interface Obligation {
	
    /**
     * Return the Id for this Obligation.
     *
     * @return a string containing the Id of this Obligation
     */
    public String getId();
    
    /**
     * Returns a Map of Obligation Attribute name,object-value-array pairs,
     * indexed by name, where name is the AttributeId and the value
     * is an array of one or more Object values of the "attribute"
     * (where an array with length > 1 indicates a multi-valued attribute).
     * <p>
     * @return a Map of String (AttributeId name), Object array 
     * (Attribute values) pairs
     */
    public Map<String, Object[]> getAttributeMap();

}
