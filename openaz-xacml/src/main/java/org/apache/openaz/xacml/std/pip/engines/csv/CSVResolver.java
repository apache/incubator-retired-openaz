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
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.std.pip.engines.csv;

import java.util.List;
import java.util.Map;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.pip.PIPEngine;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.std.pip.engines.ConfigurableResolver;

/**
 * CSVResolver is the interface used by the {@link CSVEngine} to create list of column parameters to check for
 * XACML attribute requests and convert the results into XACML attributes.
 */
public interface CSVResolver extends ConfigurableResolver {

    /**
     * Method to determine if resolver can support the PIPRequest
     *
     * @param pipRequest
     * @return true if the resolver can provide the PIPRequest attribute
     */
    boolean supportRequest(PIPRequest pipRequest);

    /**
     * Returns a mapping of column's to a list of attribute values. The PIPEngine uses the map to determine if
     * a line from a CSV file matches the given values. For columns with multiple possible values, only one
     * value needs to match.
     *
     * @param engine
     * @param request
     * @param finder
     * @return
     * @throws org.apache.openaz.xacml.api.pip.PIPException
     */
    Map<Integer, List<AttributeValue<?>>> getColumnParameterValues(PIPEngine engine, PIPRequest request,
                                                                   PIPFinder finder) throws PIPException;

    /**
     * Parses the CSV line and returns array of attributes.
     *
     * @param line - line read from CSV file broken into fields.
     * @return list of attributes
     * @throws org.apache.openaz.xacml.api.pip.PIPException
     */
    List<Attribute> decodeResult(String[] line) throws PIPException;

}
