package org.openliberty.openaz.pepapi.std;


import org.openliberty.openaz.pepapi.Subject;

/**
 * Created by ajith on 12/11/14.
 */
public class SubjectMapper extends CategoryContainerMapper {

    public SubjectMapper() {
        super(Subject.class);
    }

    @Override
    protected String resolveAttributeId(String attributeId) {
        if(attributeId.equals(Subject.SUBJECT_ID_KEY)) {
            return getPepConfig().getDefaultSubjectId();
        }
        return attributeId;
    }
}
