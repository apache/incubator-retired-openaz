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
package org.apache.openaz.xacml.pdp.eval;

import java.util.Properties;

import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.pdp.policy.PolicyFinder;
import org.apache.openaz.xacml.pdp.util.OpenAZPDPProperties;
import org.apache.openaz.xacml.util.FactoryException;
import org.apache.openaz.xacml.util.FactoryFinder;

/**
 * EvaluationContextFactory provides methods for creating {@link EvaluationContext} objects based on
 * configuration information found in standard places. (TODO: Detail what these are)
 */
public abstract class EvaluationContextFactory {
    private static final String	FACTORYID = OpenAZPDPProperties.PROP_EVALUATIONCONTEXTFACTORY;
    private static final String DEFAULT_FACTORY_CLASSNAME = "org.apache.openaz.xacml.pdp.std.StdEvaluationContextFactory";

    protected EvaluationContextFactory() {
    }

    public static EvaluationContextFactory newInstance() throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, EvaluationContextFactory.class);
    }

    public static EvaluationContextFactory newInstance(Properties properties) throws FactoryException {
        return FactoryFinder.find(FACTORYID, DEFAULT_FACTORY_CLASSNAME, EvaluationContextFactory.class,
                                  properties);
    }

    public static EvaluationContextFactory newInstance(String className, ClassLoader classLoader)
        throws FactoryException {
        return FactoryFinder.newInstance(className, EvaluationContextFactory.class, classLoader, false);
    }

    public static EvaluationContextFactory newInstance(String className) throws FactoryException {
        return FactoryFinder.newInstance(className, EvaluationContextFactory.class, null, true);
    }

    /**
     * Gets a new {@link org.apache.openaz.xacml.pdp.eval.EvaluationContext} for the given
     * {@link org.apache.openaz.xacml.api.Request}.
     *
     * @param request the <code>Request</code> for the new <code>EvaluationContext</code>
     * @return a new <code>EvaluationContext</code> for the given <code>Request</code>
     */
    public abstract EvaluationContext getEvaluationContext(Request request);

    /**
     * Sets the {@link org.apache.openaz.xacml.pdp.policy.PolicyFinder} for this
     * <code>EvaluationContextFactory</code> to an explicit instance instead of the default or configured
     * value.
     *
     * @param policyFinder the <code>PolicyFinder</code> to use in creating new <code>EvaluationContext</code>
     *            s.
     */
    public abstract void setPolicyFinder(PolicyFinder policyFinder);

    /**
     * Sets the {@link org.apache.openaz.xacml.api.pip.PIPFinder} for this
     * <code>EvaluationContextFactory</code> to an explicit instance instaed of the default or configured
     * value.
     *
     * @param pipFinder the <code>PIPFinder</code> to use in creating new <code>EvaluationContext</code>s.
     */
    public abstract void setPIPFinder(PIPFinder pipFinder);

}
