package org.apache.openaz.pepapi.std.test.mapper;

import org.apache.openaz.pepapi.Action;
import org.apache.openaz.pepapi.PepRequest;
import org.apache.openaz.pepapi.PepRequestAttributes;
import org.apache.openaz.pepapi.std.CategoryContainerMapper;
import org.apache.openaz.xacml.api.XACML3;

/**
 * Created by ajithnair on 6/30/16.
 */
public class MyActionMapper extends CategoryContainerMapper {

    private static final String MY_ACTION_ID = "my-namespace:action-id";

    public MyActionMapper() {
        super(Action.class);
    }

    @Override
    public void map(Object o, PepRequest pepRequest) {
        Action action = (Action) o;
        PepRequestAttributes actionAttributes
                = pepRequest.getPepRequestAttributes(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION);
        actionAttributes.addAttribute(MY_ACTION_ID, action.getId());
        super.map(o, pepRequest);
    }
}
