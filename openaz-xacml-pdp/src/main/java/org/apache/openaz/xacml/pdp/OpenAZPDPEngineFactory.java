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

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.pdp;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.pdp.PDPEngine;
import org.apache.openaz.xacml.api.pdp.PDPEngineFactory;
import org.apache.openaz.xacml.pdp.eval.EvaluationContextFactory;
import org.apache.openaz.xacml.util.FactoryException;

/**
 * OpenAZPDPEngineFactory extends {@link org.apache.openaz.xacml.api.pdp.PDPEngineFactory} by implementing the
 * abstract <code>newEngine</code> method to create a {@link OpenAZPDPEngine} instance and initialize it with
 * policies and PIP instances based on configuration information provided to the factory.
 */
public class OpenAZPDPEngineFactory extends PDPEngineFactory {
    private Log logger = LogFactory.getLog(this.getClass());

    public OpenAZPDPEngineFactory() {
    }

    @Override
    public PDPEngine newEngine() throws FactoryException {
        EvaluationContextFactory evaluationContextFactory = EvaluationContextFactory.newInstance();
        if (evaluationContextFactory == null) {
            this.logger.error("Null EvaluationContextFactory");
            throw new FactoryException("Null EvaluationContextFactory");
        }
        return new OpenAZPDPEngine(evaluationContextFactory, this.getDefaultBehavior(), this.getScopeResolver());
    }

    @Override
    public PDPEngine newEngine(Properties properties) throws FactoryException {
        EvaluationContextFactory evaluationContextFactory = EvaluationContextFactory.newInstance(properties);
        if (evaluationContextFactory == null) {
            this.logger.error("Null EvaluationContextFactory");
            throw new FactoryException("Null EvaluationContextFactory");
        }
        return new OpenAZPDPEngine(evaluationContextFactory, this.getDefaultBehavior(), this.getScopeResolver());
    }
}
