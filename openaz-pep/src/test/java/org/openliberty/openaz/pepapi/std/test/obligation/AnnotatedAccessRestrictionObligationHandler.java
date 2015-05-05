/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

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
        Obligation accessGroupOblg = obligationStore
            .getHandlerObligationById(this.getClass(),
                                      "urn:oasis:names:tc:xacml:2.0:obligation:access-restriction");
        Assert.assertEquals("urn:oasis:names:tc:xacml:2.0:obligation:access-restriction",
                            accessGroupOblg.getId());
        log.info(accessGroupOblg.getId());
        for (Entry<String, Object[]> e : accessGroupOblg.getAttributeMap().entrySet()) {
            if (e.getKey().equals("urn:oasis:names:tc:xacml:1.0:subject:subject-id")) {
                Assert.assertNotNull(e.getValue());
            }
            if (e.getKey().equals("urn:oasis:names:tc:xacml:1.0:resource:resource-access-group")) {
                Object[] values = e.getValue();
                Assert.assertNotNull(values);
                Assert.assertEquals(3, values.length);
            }
        }
        // Enforcement Logic
    }

    @Override
    public void setObligationStore(ObligationStore obligationStore) {
        this.obligationStore = obligationStore;
    }
}
