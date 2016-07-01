package org.apache.openaz.pepapi.std.test.mapper;

import org.apache.openaz.pepapi.PepRequest;
import org.apache.openaz.pepapi.PepRequestAttributes;
import org.apache.openaz.pepapi.Subject;
import org.apache.openaz.pepapi.std.CategoryContainerMapper;
import org.apache.openaz.xacml.api.XACML3;

/**
 * Created by ajithnair on 6/30/16.
 */
public class MySubjectMapper extends CategoryContainerMapper {

    private static final String MY_SUBJECT_ID = "my-namespace:subject-id";


    public MySubjectMapper() {
        super(Subject.class);
    }

    @Override
    public void map(Object o, PepRequest pepRequest) {
        Subject subject = (Subject) o;
        PepRequestAttributes subjectAttributes
                = pepRequest.getPepRequestAttributes(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT);
        subjectAttributes.addAttribute(MY_SUBJECT_ID, subject.getId());
        super.map(o, pepRequest);
    }
}
