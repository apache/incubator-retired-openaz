package org.openliberty.openaz.pepapi.std;

import com.att.research.xacml.api.AttributeAssignment;
import org.openliberty.openaz.pepapi.Advice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


final class StdAdvice implements Advice {

    private com.att.research.xacml.api.Advice wrappedAdvice;

    StdAdvice(com.att.research.xacml.api.Advice advice) {
        this.wrappedAdvice = advice;
    }

    /**
     * Return the Id for this Advice.
     *
     * @return a string containing the Id of this Advice
     */
    public String getId(){
        return wrappedAdvice.getId().stringValue();
    }

    @Override
    public Map<String, Object[]> getAttributeMap() {
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        for(AttributeAssignment a: wrappedAdvice.getAttributeAssignments()) {
            String attributeId = a.getAttributeId().stringValue();
            List<Object> values = map.get(attributeId);
            if(values == null) {
                values = new ArrayList<Object>();
                map.put(attributeId, values);
            }
            values.add(a.getAttributeValue().getValue());
        }
        Map<String, Object[]> attributeMap = new HashMap<String, Object[]>();
        for(Map.Entry<String, List<Object>> e: map.entrySet()) {
            attributeMap.put(e.getKey(), e.getValue().toArray(new Object[1]));
        }
        return attributeMap;
    }
}
