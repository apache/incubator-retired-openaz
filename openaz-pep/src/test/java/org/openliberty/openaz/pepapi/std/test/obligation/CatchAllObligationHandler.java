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
