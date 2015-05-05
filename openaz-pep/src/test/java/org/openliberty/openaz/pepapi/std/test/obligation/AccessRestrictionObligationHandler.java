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
import org.openliberty.openaz.pepapi.ObligationHandler;
import org.openliberty.openaz.pepapi.ObligationStore;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AccessRestrictionObligationHandler implements ObligationHandler {

    private static Log log = LogFactory.getLog(AccessRestrictionObligationHandler.class);

    private ObligationStore obligationStore;

    public void enforce() {
        Set<Obligation> accessOblgSet = obligationStore.getHandlerObligations(this.getClass());
        Assert.assertEquals(true, accessOblgSet.size() == 1);
        for (Obligation oblg : accessOblgSet) {
            Map<String, Object[]> attributeMap = oblg.getAttributeMap();
            Assert.assertNotNull(attributeMap);
            for (Entry<String, Object[]> e : attributeMap.entrySet()) {
                if (e.getKey().equals("urn:oasis:names:tc:xacml:1.0:subject:subject-id")) {
                    Assert.assertNotNull(e.getValue());
                }
                if (e.getKey().equals("urn:oasis:names:tc:xacml:1.0:resource:resource-access-group")) {
                    Object[] values = e.getValue();
                    Assert.assertNotNull(values);
                    Assert.assertEquals(3, values.length);
                }
            }
        }
        Obligation accessGroupOblg = obligationStore
            .getHandlerObligationById(this.getClass(),
                                      "urn:oasis:names:tc:xacml:2.0:obligation:access-restriction");
        Assert.assertNotNull(accessGroupOblg);
        log.info(accessGroupOblg.getId());
    }

    @Override
    public boolean match(Obligation obligation) {
        return obligation.getId().equals("urn:oasis:names:tc:xacml:2.0:obligation:access-restriction");
    }

    @Override
    public void setObligationStore(ObligationStore oStore) {
        this.obligationStore = oStore;
    }

}
