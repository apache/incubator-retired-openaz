package org.openliberty.openaz.pepapi.std.test.obligation;

import junit.framework.Assert;
import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationStore;
import org.openliberty.openaz.pepapi.ObligationStoreAware;
import org.openliberty.openaz.pepapi.MatchAnyObligation;

import java.util.HashSet;
import java.util.Set;

@MatchAnyObligation
public class AnnotatedCatchAllObligationHandler implements ObligationStoreAware {
	
	private ObligationStore obligationStore;
	
	public void enforce() {
		Set<Obligation> obligationSet = obligationStore.getHandlerObligations(this.getClass());
		if(obligationSet.size() == 2) {
			Set<String> obligationIds = new HashSet<String>();
			for(Obligation oblg: obligationSet){
				obligationIds.add(oblg.getId());
			}
			Assert.assertTrue(obligationIds.contains("urn:oasis:names:tc:xacml:2.0:obligation:obligation-1"));
			Assert.assertTrue(obligationIds.contains("urn:oasis:names:tc:xacml:2.0:obligation:obligation-2"));
		}else {
			Assert.assertFalse(true);
		}
		
	}

	@Override
	public void setObligationStore(ObligationStore obligationStore) {
		this.obligationStore = obligationStore;
	}
}
