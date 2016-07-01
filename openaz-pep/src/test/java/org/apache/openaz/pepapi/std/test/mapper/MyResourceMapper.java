package org.apache.openaz.pepapi.std.test.mapper;

import org.apache.openaz.pepapi.PepRequest;
import org.apache.openaz.pepapi.PepRequestAttributes;
import org.apache.openaz.pepapi.Resource;
import org.apache.openaz.pepapi.std.CategoryContainerMapper;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.api.pep.PEPException;

import java.net.URI;

/**
 * Created by ajithnair on 7/1/16.
 */
public class MyResourceMapper extends CategoryContainerMapper {

    private static final String MY_RESOURCE_ID = "my-namespace:resource-id";

    private static final String MY_RESOURCE_LOCATION = "my-namespace:resource-location";

    public MyResourceMapper() {
        super(Resource.class);
    }

    @Override
    public void map(Object o, PepRequest pepRequest) {
        Resource resource = (Resource) o;
        PepRequestAttributes resourceAttributes
                = pepRequest.getPepRequestAttributes(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE);
        Object resourceId = resource.getId();
        if (resourceId instanceof URI) {
            resourceAttributes.addAttribute(MY_RESOURCE_ID, (URI) resourceId);
        } else if (resourceId instanceof String) {
            resourceAttributes.addAttribute(MY_RESOURCE_ID, (String) resourceId);
        } else {
            throw new IllegalArgumentException("Invalid type: " + resourceId.getClass());
        }

        if (resource.getLocation() != null) {
            resourceAttributes.addAttribute(MY_RESOURCE_LOCATION, resource.getLocation());
        }
        super.map(o, pepRequest);
    }
}
