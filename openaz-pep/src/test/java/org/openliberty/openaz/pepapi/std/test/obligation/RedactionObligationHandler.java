package org.openliberty.openaz.pepapi.std.test.obligation;

import junit.framework.Assert;
import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationHandler;
import org.openliberty.openaz.pepapi.ObligationStore;

import java.util.Map;
import java.util.Set;

public class RedactionObligationHandler implements ObligationHandler {
	
	private ObligationStore obligationStore;
	
	public void enforce() {
		Set<Obligation> obligationSet = obligationStore.getHandlerObligations(this.getClass());
		if(obligationSet.size() == 1) {
			for(Obligation obligation: obligationSet) {
				Assert.assertEquals("urn:oasis:names:tc:xacml:2.0:obligation:obligation-2", 
						obligation.getId());
			}
		}else {
			Assert.assertFalse(true);
		}
	}

	@Override
	public boolean match(Obligation obligation) {
		Map<String, Object[]> map = obligation.getAttributeMap();
		return map.containsKey("urn:oasis:names:tc:xacml:1.0:subject:age");
	}

	@Override
	public void setObligationStore(ObligationStore obligationStore) {
		this.obligationStore = obligationStore;
	}
}
