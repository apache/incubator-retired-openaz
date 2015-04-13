package org.openliberty.openaz.pepapi.std;



import java.util.HashSet;
import java.util.Set;


public final class ObligationCriteriaBuilder {

	private Set<ObligationCriterion> criteria = new HashSet<ObligationCriterion>();

	public ObligationCriteriaBuilder matchAttribute(String attributeId) {
		criteria.add(new ObligationAttributeCriterion(attributeId));
		return this;
	}

	public ObligationCriteriaBuilder matchAttributeWithAnyGivenValue(String attributeId,
			String... values) {
		criteria.add(new ObligationAttributeCriterion(attributeId, values));
		return this;
	}
	
	public ObligationCriteriaBuilder matchAnyObligationId(String... obligationIds) {
		criteria.add(new ObligationIdCriterion(obligationIds));
		return this;
	}
	
	public ObligationCriteriaBuilder matchAnyObligation() {
		criteria.add(new MatchAnyCriterion());
		return this;
	}
	
	public final ObligationCriteria build() {
		return new ObligationCriteria(criteria);
	}
}
