package org.openliberty.openaz.pepapi.std;

import org.openliberty.openaz.pepapi.Obligation;

import java.util.*;

public final class ObligationAttributeCriterion implements ObligationCriterion {
	
	private String id;
	
	private Set<String> valueSet;
	
	public ObligationAttributeCriterion(String id) {
		this.id = id;
		this.valueSet = new HashSet<String>();
	}
	
	ObligationAttributeCriterion(String id, String...values) {
		this(id);
		if(values != null && values.length > 0){
			this.valueSet.addAll(Arrays.asList(values));
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((valueSet == null) ? 0 : valueSet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObligationAttributeCriterion other = (ObligationAttributeCriterion) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (valueSet == null) {
			if (other.valueSet != null)
				return false;
		} else if (!valueSet.equals(other.valueSet))
			return false;
		return true;
	}

	public String getId() {
		return id;
	}

	public Set<String> getValueSet() {
		return Collections.unmodifiableSet(valueSet);
	}
	
	@Override
	public boolean match(Obligation obligation) {
		Map<String, Object[]> obligationAttrMap = obligation.getAttributeMap();
		if(!obligationAttrMap.containsKey(this.id)) {
			return false;
		}
		//Proceed with value matching, if the AttributeMatch has a defined value set to match.
		if(!valueSet.isEmpty()) {
			Object[] attributeValues = obligationAttrMap.get(this.id);
			boolean valueFound = false;
			if(attributeValues != null) {
				for(Object attributeValue: attributeValues) {
					if(valueSet.contains(attributeValue)) {
						valueFound = true;
						break;
					}
				}
			}
			return valueFound;
		}
		return true;
	}
}
