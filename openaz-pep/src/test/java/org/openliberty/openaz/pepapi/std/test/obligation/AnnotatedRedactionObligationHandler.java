package org.openliberty.openaz.pepapi.std.test.obligation;

import junit.framework.Assert;
import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationStore;
import org.openliberty.openaz.pepapi.ObligationStoreAware;
import org.openliberty.openaz.pepapi.Attribute;
import org.openliberty.openaz.pepapi.MatchAllObligationAttributes;

import java.util.Set;

@MatchAllObligationAttributes(
		@Attribute(id="urn:oasis:names:tc:xacml:1.0:subject:age")
)
public class AnnotatedRedactionObligationHandler implements ObligationStoreAware {
	
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
	public void setObligationStore(ObligationStore obligationStore) {
		this.obligationStore = obligationStore;
	}
}
