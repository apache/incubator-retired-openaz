package org.openliberty.openaz.pepapi.std.test.obligation;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationHandler;
import org.openliberty.openaz.pepapi.ObligationStore;

import java.util.Set;

public class AuditObligationHandler implements ObligationHandler {
	
	private static Log log = LogFactory.getLog(AuditObligationHandler.class);
	
	private ObligationStore obligationStore;
	
	public void enforce() {
		Set<Obligation> auditOblgSet = obligationStore.getHandlerObligations(this.getClass());
		Assert.assertEquals(true, auditOblgSet.size() == 1);
		Obligation auditOblg = obligationStore.getHandlerObligationById(
				this.getClass(),
				"urn:oasis:names:tc:xacml:2.0:obligation:audit");
		Assert.assertNotNull(auditOblg);
		log.info(auditOblg.getId());
	}

	@Override
	public boolean match(Obligation t) {
		return t.getId().equals("urn:oasis:names:tc:xacml:2.0:obligation:audit");
	}

	@Override
	public void setObligationStore(ObligationStore obligationStore) {
		this.obligationStore = obligationStore;
	}
}
