package org.openliberty.openaz.pepapi.std.test.obligation;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationStore;
import org.openliberty.openaz.pepapi.ObligationStoreAware;
import org.openliberty.openaz.pepapi.MatchAnyObligation;
import org.openliberty.openaz.pepapi.std.test.util.HasResult;

import java.util.Map;

@MatchAnyObligation("urn:oasis:names:tc:xacml:2.0:obligation:age-restriction")
public class AnnotatedAgeRestrictionObligationHandler implements ObligationStoreAware, HasResult {
	
	private static Log log = LogFactory.getLog(AnnotatedAgeRestrictionObligationHandler.class);
	
	private ObligationStore obligationStore;
	
	public String enforce() {
		Obligation ageOblg = obligationStore.getHandlerObligationById(
				this.getClass(),
				"urn:oasis:names:tc:xacml:2.0:obligation:age-restriction");
		String value = null;
		Assert.assertEquals("urn:oasis:names:tc:xacml:2.0:obligation:age-restriction", ageOblg.getId());
		log.info(ageOblg.getId());
		//Enforcement Logic
		Map<String, Object[]> attributeMap = ageOblg.getAttributeMap();
		Object[] values = attributeMap.get("urn:oasis:names:tc:xacml:1.0:subject:age");
		if(values != null) {
			value = (String)values[0];
		}
		return value;
	}

	@Override
	public void setObligationStore(ObligationStore obligationStore) {
		this.obligationStore = obligationStore;
	}

	@Override
	public String getResult() {
		return enforce();
	}
}