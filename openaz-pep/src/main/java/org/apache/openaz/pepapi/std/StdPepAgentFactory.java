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

package org.apache.openaz.pepapi.std;

import org.apache.openaz.pepapi.*;
import org.apache.openaz.xacml.api.pdp.PDPEngineFactory;
import org.apache.openaz.xacml.util.FactoryException;

import java.util.List;
import java.util.Properties;


public class StdPepAgentFactory implements PepAgentFactory {

    private volatile PepAgent pepAgent;

    private PDPEngineFactory pdpEngineFactory;

    private Properties xacmlProperties;

    private PepConfig pepConfig;

    private List<ObligationStoreAware> obligationHandlers;

    public StdPepAgentFactory(String propertyFile) {
        this(PepUtils.loadProperties(propertyFile));
    }

    public StdPepAgentFactory(Properties properties) {
        this.xacmlProperties = properties;
        this.pepConfig = new StdPepConfig(properties);
        try {
            //FIXME: Error when invoking newInstance() with properties.
            pdpEngineFactory = PDPEngineFactory.newInstance();
        } catch (FactoryException e) {
            throw new PepException(e);
        }
    }

    @Override
    public PepAgent getPepAgent() {
        if(pepAgent == null) {
            synchronized(this) {
                if(this.pepAgent == null) {
                    StdPepAgent pa = new StdPepAgent();
                    pa.setPepConfig(pepConfig);
                    pa.setXacmlProperties(xacmlProperties);
                    pa.setPdpEngineFactory(pdpEngineFactory);
                    pa.setObligationHandlers(obligationHandlers);
                    pa.initialize();
                    pepAgent = pa;
                }
            }
        }
        return pepAgent;
    }

    public void setObligationHandlers(List<ObligationStoreAware> obligationHandlers) {
        this.obligationHandlers = obligationHandlers;
    }
}
