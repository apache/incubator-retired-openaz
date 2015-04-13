package org.openliberty.openaz.pepapi.std.test.obligation;

import junit.framework.Assert;
import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationHandler;
import org.openliberty.openaz.pepapi.ObligationStore;

import java.util.HashSet;
import java.util.Set;


public class CatchAllObligationHandler implements ObligationHandler {
	
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
	public boolean match(Obligation obligation) {
		return true;
	}

	@Override
	public void setObligationStore(ObligationStore obligationStore) {
		this.obligationStore = obligationStore;
	}

}
