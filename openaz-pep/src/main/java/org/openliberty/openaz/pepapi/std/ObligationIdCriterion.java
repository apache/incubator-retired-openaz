package org.openliberty.openaz.pepapi.std;

import org.openliberty.openaz.pepapi.Obligation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ObligationIdCriterion implements ObligationCriterion {
	
	private Set<String> obligationIdSet;
	
	public ObligationIdCriterion(String... obligationIds){
		this.obligationIdSet = new HashSet<String>();
		if(obligationIds != null) {
			this.obligationIdSet.addAll(Arrays.asList(obligationIds));
		}
	}
	
	@Override
	public boolean match(Obligation obligation) {
		return this.obligationIdSet.contains(obligation.getId());
	}

}
