package org.openliberty.openaz.pepapi;

import java.util.Map;

/**
 *
 */
public interface Advice {

    /**
     *
     * @return
     */
    public String getId();

    /**
     *
     */
    public Map<String, Object[]> getAttributeMap();

}
