package org.openliberty.openaz.pepapi.std;

import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.Matchable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class ObligationCriteria implements Matchable<Obligation> {
	
	private Set<ObligationCriterion> criteria;
	
	ObligationCriteria(Collection<ObligationCriterion> criteria){
		this.criteria = new HashSet<ObligationCriterion>();
		this.criteria.addAll(criteria);
	}

	@Override
	public boolean match(Obligation obligation) {
		for(ObligationCriterion criterion: criteria){
			if(!criterion.match(obligation)){
				return false;
			}
		}
		return true;
	}
}
