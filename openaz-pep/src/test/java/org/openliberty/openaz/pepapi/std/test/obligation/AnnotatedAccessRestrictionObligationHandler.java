package org.openliberty.openaz.pepapi.std.test.obligation;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pepapi.Obligation;
import org.openliberty.openaz.pepapi.ObligationStore;
import org.openliberty.openaz.pepapi.ObligationStoreAware;
import org.openliberty.openaz.pepapi.MatchAnyObligation;

import java.util.Map.Entry;

@MatchAnyObligation("urn:oasis:names:tc:xacml:2.0:obligation:access-restriction")
public class AnnotatedAccessRestrictionObligationHandler implements ObligationStoreAware {
	
	private static Log log = LogFactory.getLog(AnnotatedAccessRestrictionObligationHandler.class);
	
	private ObligationStore obligationStore;
	
	public void enforce() {
		Obligation accessGroupOblg = obligationStore.getHandlerObligationById(
				this.getClass(),
				"urn:oasis:names:tc:xacml:2.0:obligation:access-restriction");
		Assert.assertEquals("urn:oasis:names:tc:xacml:2.0:obligation:access-restriction", 
				accessGroupOblg.getId());
		log.info(accessGroupOblg.getId());
		for(Entry<String, Object[]> e: accessGroupOblg.getAttributeMap().entrySet()){
			if(e.getKey().equals("urn:oasis:names:tc:xacml:1.0:subject:subject-id")){
				Assert.assertNotNull(e.getValue());
			}
			if(e.getKey().equals("urn:oasis:names:tc:xacml:1.0:resource:resource-access-group")){
				Object[] values = e.getValue();
				Assert.assertNotNull(values);
				Assert.assertEquals(3, values.length);
			}
		}
		//Enforcement Logic
	}

	@Override
	public void setObligationStore(ObligationStore obligationStore) {
		this.obligationStore = obligationStore;
	}
}
