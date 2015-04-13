package org.openliberty.openaz.pepapi.std;


import org.openliberty.openaz.pepapi.Action;

/**
 * Created by ajith on 12/11/14.
 */
public class ActionMapper extends CategoryContainerMapper {

    public ActionMapper() {
        super(Action.class);
    }

    @Override
    protected String resolveAttributeId(String attributeId) {
        if(attributeId.equals(Action.ACTION_ID_KEY)) {
            return getPepConfig().getDefaultActionId();
        }
        return attributeId;
    }
}
