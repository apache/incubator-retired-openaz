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

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.pepapi.PepConfig;
import org.apache.openaz.pepapi.PepResponseBehavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public final class StdPepConfig implements PepConfig {

    private static final Log logger = LogFactory.getLog(StdPepConfig.class);

    private static final String PEP_ISSUER = "pep.issuer";

    private static final String PEP_INDETERMINATE_BEHAVIOR = "pep.indeterminate.behavior";

    private static final String PEP_NOTAPPLICABLE_BEHAVIOR = "pep.notapplicable.behavior";

    private static final String PEP_MAPPER_CLASSES = "pep.mapper.classes";

    private String issuer;

    private PepResponseBehavior indeterminateBehavior;

    private PepResponseBehavior notApplicableBehavior;

    private List<String> mapperClassNames;

    public StdPepConfig() {
        // Defaults
        indeterminateBehavior = PepResponseBehavior.THROW_EXCEPTION;
        notApplicableBehavior = PepResponseBehavior.RETURN_NO;
        mapperClassNames = Collections.emptyList();
    }

    public StdPepConfig(Properties properties) {
        this();
        issuer = properties.getProperty(PEP_ISSUER);

        String indeterminateString = properties.getProperty(PEP_INDETERMINATE_BEHAVIOR);
        if (!StringUtils.isEmpty(indeterminateString)) {
            PepResponseBehavior indeterminateBehavior = PepResponseBehavior.valueOf(indeterminateString);
            if (indeterminateBehavior == null) {
                logger.error("Invalid indeterminate behavior found in configuration.");
                // TODO: Throw exception ?
            }
            this.indeterminateBehavior = indeterminateBehavior;
        }

        String notapplicableString = properties.getProperty(PEP_NOTAPPLICABLE_BEHAVIOR);
        if (!StringUtils.isEmpty(notapplicableString)) {
            PepResponseBehavior notApplicableBehavior = PepResponseBehavior.valueOf(notapplicableString);
            if (notApplicableBehavior == null) {
                logger.error("Invalid notapplicable behavior found in configuration.");
                // TODO: Throw exception ?
            }
            this.notApplicableBehavior = notApplicableBehavior;
        }

        String mapperClassNameString = properties.getProperty(PEP_MAPPER_CLASSES);
        if (!StringUtils.isEmpty(mapperClassNameString)) {
            List<String> mapperClassNames = new ArrayList<String>();
            for (String className : Splitter.on(",").omitEmptyStrings().trimResults()
                    .split(mapperClassNameString)) {
                mapperClassNames.add(className);
            }
            this.mapperClassNames = Collections.unmodifiableList(mapperClassNames);
        }

    }

    @Override
    public String getIssuer() {
        return issuer;
    }

    @Override
    public PepResponseBehavior getIndeterminateBehavior() {
        return indeterminateBehavior;
    }

    @Override
    public PepResponseBehavior getNotApplicableBehavior() {
        return notApplicableBehavior;
    }

    @Override
    public List<String> getMapperClassNames() {
        return mapperClassNames;
    }
}
