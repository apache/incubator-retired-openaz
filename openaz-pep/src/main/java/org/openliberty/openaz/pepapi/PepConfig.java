package org.openliberty.openaz.pepapi;

import java.util.List;

/**
 * @authors Ajith Nair, David Laurance, Darshak Kothari
 */
public interface PepConfig {

    /**
     *
     * @return
     */
    public String getIssuer();

    /**
     *
     * @return
     */
    public String getDefaultSubjectId();

    /**
     *
     * @return
     */
    public String getDefaultResourceId();

    /**
     *
     * @return
     */
    public String getDefaultActionId();

    /**
     *
     * @return
     */
    public PepResponseBehavior getIndeterminateBehavior();

    /**
     *
     * @return
     */
    public PepResponseBehavior getNotApplicableBehavior();

    /**
     *
     * @return
     */
    public List<String> getMapperClassNames();
}
