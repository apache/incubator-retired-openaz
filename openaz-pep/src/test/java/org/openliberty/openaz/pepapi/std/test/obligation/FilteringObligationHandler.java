package org.openliberty.openaz.pepapi.std.test.obligation;

import junit.framework.Assert;
import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationHandler;
import org.openliberty.openaz.pepapi.ObligationStore;

import java.util.Map;
import java.util.Set;

public class FilteringObligationHandler implements ObligationHandler {
	
	private ObligationStore obligationStore;
	
	public void enforce() {
		Set<Obligation> obligationSet = obligationStore.getHandlerObligations(this.getClass());
		if(obligationSet.size() == 1) {
			for(Obligation obligation: obligationSet) {
				Assert.assertEquals("urn:oasis:names:tc:xacml:2.0:obligation:obligation-1", 
						obligation.getId());
			}
		}else {
			Assert.assertFalse(true);
		}
	}

	@Override
	public boolean match(Obligation obligation) {
		Map<String, Object[]> map = obligation.getAttributeMap();
		if(map.containsKey("jpmc:obligation:obligation-type")) {
			Object[] values = map.get("jpmc:obligation:obligation-type");
			if(values != null && values.length != 0) {
				for(Object value: values) {
					return value.equals("Filtering");
				}
			}
		}
		return false;
	}

	@Override
	public void setObligationStore(ObligationStore obligationStore) {
		this.obligationStore = obligationStore;
	}
}
