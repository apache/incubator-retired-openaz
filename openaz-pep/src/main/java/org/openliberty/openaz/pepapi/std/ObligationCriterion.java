package org.openliberty.openaz.pepapi.std;


import org.openliberty.openaz.pepapi.Obligation;

public interface ObligationCriterion {
	
	public boolean match(Obligation obligation);
	
}
