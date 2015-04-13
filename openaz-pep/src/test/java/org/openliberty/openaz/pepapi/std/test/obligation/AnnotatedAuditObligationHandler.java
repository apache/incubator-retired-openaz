package org.openliberty.openaz.pepapi.std.test.obligation;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationStore;
import org.openliberty.openaz.pepapi.ObligationStoreAware;
import org.openliberty.openaz.pepapi.MatchAnyObligation;

import java.util.Set;

@MatchAnyObligation("urn:oasis:names:tc:xacml:2.0:obligation:audit")
public class AnnotatedAuditObligationHandler implements ObligationStoreAware {
	
	private static Log log = LogFactory.getLog(AnnotatedAuditObligationHandler.class);
	
	private ObligationStore obligationStore;
	
	public void enforce() {
		Set<Obligation> obligationSet = obligationStore.getHandlerObligations(this.getClass());
		if(obligationSet.size() == 1) {
			for(Obligation obligation: obligationSet) {
				Assert.assertEquals("urn:oasis:names:tc:xacml:2.0:obligation:audit", obligation.getId());
				log.info(obligation.getId());
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
