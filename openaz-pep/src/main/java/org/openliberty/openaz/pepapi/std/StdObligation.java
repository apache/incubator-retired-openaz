package org.openliberty.openaz.pepapi.std;

import com.att.research.xacml.api.AttributeAssignment;
import org.openliberty.openaz.pepapi.Obligation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


final class StdObligation implements Obligation {

	private com.att.research.xacml.api.Obligation wrappedObligation;

	StdObligation(com.att.research.xacml.api.Obligation obligation) {
		this.wrappedObligation = obligation;
	}

	/**
     * Return the Id for this Obligation.
     *
     * @return a string containing the Id of this Obligation
     */
    public String getId(){
		return wrappedObligation.getId().stringValue();
    }

	@Override
	public Map<String, Object[]> getAttributeMap() {
		Map<String, List<Object>> map = new HashMap<String, List<Object>>();
		for(AttributeAssignment a: wrappedObligation.getAttributeAssignments()) {
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
