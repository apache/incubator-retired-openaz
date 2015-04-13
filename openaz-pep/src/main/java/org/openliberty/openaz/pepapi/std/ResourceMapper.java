package org.openliberty.openaz.pepapi.std;


import org.openliberty.openaz.pepapi.Resource;

/**
 * Created by ajith on 12/11/14.
 */
public class ResourceMapper extends CategoryContainerMapper {

    public ResourceMapper() {
        super(Resource.class);
    }

    @Override
    protected String resolveAttributeId(String attributeId) {
        if(attributeId.equals(Resource.RESOURCE_ID_KEY)) {
            return getPepConfig().getDefaultResourceId();
        }
        return attributeId;
    }
}
