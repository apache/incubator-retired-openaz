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

package org.openliberty.openaz.pepapi;

/**
 * An interface that may be implemented to process the PepResponse that is returned from the main decide()
 * call before the final results are returned to the user.
 */
public interface PostDecisionHandler {

    /**
     * This method is used to apply post-decision custom processing to the
     * {@link org.openliberty.openaz.pepapi.PepResponse} after it has been returned.
     *
     * @param request
     * @throws org.openliberty.openaz.pepapi.PepException
     */
    void postDecide(PepRequest request, PepResponse response) throws PepException;

}
