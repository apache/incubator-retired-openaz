package org.openliberty.openaz.pepapi.std;


import org.openliberty.openaz.pepapi.Obligation;

public final class MatchAnyCriterion implements ObligationCriterion {

	@Override
	public boolean match(Obligation obligation) {
		return true;
	}

}
