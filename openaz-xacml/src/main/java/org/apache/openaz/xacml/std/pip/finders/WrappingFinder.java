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
package org.apache.openaz.xacml.std.pip.finders;

import org.apache.openaz.xacml.api.pip.PIPEngine;
import org.apache.openaz.xacml.api.pip.PIPException;
import org.apache.openaz.xacml.api.pip.PIPFinder;
import org.apache.openaz.xacml.api.pip.PIPRequest;
import org.apache.openaz.xacml.api.pip.PIPResponse;
import org.apache.openaz.xacml.std.pip.StdPIPResponse;

/**
 * WrappingFinder implements {@link org.apache.openaz.xacml.api.pip.PIPFinder} by wrapping another
 * <code>PIPFinder</code> to intercept calls to <code>getAttributes</code> and do some other processing before
 * calling it on the wrapped <code>PIPFinder</code>.
 */
public abstract class WrappingFinder implements PIPFinder {
    private PIPFinder wrappedFinder;

    protected PIPFinder getWrappedFinder() {
        return this.wrappedFinder;
    }

    public WrappingFinder(PIPFinder wrappedFinderIn) {
        this.wrappedFinder = wrappedFinderIn;
    }

    /**
     * Gets the {@link org.apache.openaz.xacml.api.pip.PIPResponse} from the <code>getAttributes</code> call on
     * the wrapped <code>PIPFinder</code>, using the given <code>PIPFinder</code> as the root for recursive
     * calls.
     *
     * @param pipRequest the <code>PIPRequest</code>
     * @param exclude the <code>PIPEngine</code> to exclude from recursive calls
     * @param pipFinderParent the <code>PIPFinder</code> to start from for recursive calls
     * @return the <code>PIPResponse</code> from the wrapped <code>PIPFinder</code> or the empty
     *         <code>PIPResponse</code> if there is no wrapped <code>PIPFinder</code>
     * @throws org.apache.openaz.xacml.api.pip.PIPException if there is an error getting attributes from the
     *             wrapped <code>PIPFinder</code>
     */
    protected PIPResponse getAttributesWrapped(PIPRequest pipRequest, PIPEngine exclude,
                                               PIPFinder pipFinderParent) throws PIPException {
        PIPFinder thisWrappedFinder = this.getWrappedFinder();
        if (thisWrappedFinder == null) {
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        } else {
            return thisWrappedFinder.getAttributes(pipRequest, exclude, pipFinderParent);
        }
    }

    protected abstract PIPResponse getAttributesInternal(PIPRequest pipRequest, PIPEngine exclude,
                                                         PIPFinder pipFinderParent) throws PIPException;

    @Override
    public PIPResponse getAttributes(PIPRequest pipRequest, PIPEngine exclude) throws PIPException {
        return this.getAttributesInternal(pipRequest, exclude, this);
    }

    @Override
    public PIPResponse getMatchingAttributes(PIPRequest pipRequest, PIPEngine exclude) throws PIPException {
        return StdPIPResponse.getMatchingResponse(pipRequest, this.getAttributes(pipRequest, exclude));
    }

    @Override
    public PIPResponse getAttributes(PIPRequest pipRequest, PIPEngine exclude, PIPFinder pipFinderParent)
        throws PIPException {
        return this.getAttributesInternal(pipRequest, exclude, pipFinderParent);
    }

    @Override
    public PIPResponse getMatchingAttributes(PIPRequest pipRequest, PIPEngine exclude,
                                             PIPFinder pipFinderParent) throws PIPException {
        return StdPIPResponse.getMatchingResponse(pipRequest, this.getAttributesInternal(pipRequest, exclude,
                                                                                         pipFinderParent));
    }

}
