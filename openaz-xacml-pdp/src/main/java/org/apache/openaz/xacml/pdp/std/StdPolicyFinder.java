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
package org.apache.openaz.xacml.pdp.std;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.IdReferenceMatch;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.Version;
import org.apache.openaz.xacml.pdp.eval.EvaluationContext;
import org.apache.openaz.xacml.pdp.eval.EvaluationException;
import org.apache.openaz.xacml.pdp.eval.MatchResult;
import org.apache.openaz.xacml.pdp.policy.Policy;
import org.apache.openaz.xacml.pdp.policy.PolicyDef;
import org.apache.openaz.xacml.pdp.policy.PolicyFinder;
import org.apache.openaz.xacml.pdp.policy.PolicyFinderResult;
import org.apache.openaz.xacml.pdp.policy.PolicySet;
import org.apache.openaz.xacml.pdp.policy.PolicySetChild;
import org.apache.openaz.xacml.pdp.policy.dom.DOMPolicyDef;
import org.apache.openaz.xacml.std.StdStatus;
import org.apache.openaz.xacml.std.StdStatusCode;

/**
 * StdPolicyFinder implements the {@link org.apache.openaz.xacml.pdp.policy.PolicyFinder} interface to look
 * up policies by their internal ID or an externally visible ID.
 */
public class StdPolicyFinder implements PolicyFinder {
    private static final PolicyFinderResult<PolicyDef> PFR_MULTIPLE = new StdPolicyFinderResult<PolicyDef>(
                                                                                                           new StdStatus(
                                                                                                                         StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                                                                         "Multiple applicable root policies"));
    private static final PolicyFinderResult<PolicyDef> PFR_NOT_FOUND = new StdPolicyFinderResult<PolicyDef>(
                                                                                                            new StdStatus(
                                                                                                                          StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                                                                          "No matching root policy found"));

    private static final PolicyFinderResult<Policy> PFR_POLICY_NOT_FOUND = new StdPolicyFinderResult<Policy>(
                                                                                                             new StdStatus(
                                                                                                                           StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                                                                           "No matching policy found"));
    private static final PolicyFinderResult<Policy> PFR_NOT_A_POLICY = new StdPolicyFinderResult<Policy>(
                                                                                                         new StdStatus(
                                                                                                                       StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                                                                       "Not a policy"));
    private static final PolicyFinderResult<PolicySet> PFR_POLICYSET_NOT_FOUND = new StdPolicyFinderResult<PolicySet>(
                                                                                                                      new StdStatus(
                                                                                                                                    StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                                                                                    "No matching policy set found"));
    private static final PolicyFinderResult<PolicySet> PFR_NOT_A_POLICYSET = new StdPolicyFinderResult<PolicySet>(
                                                                                                                  new StdStatus(
                                                                                                                                StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                                                                                "Not a policy set"));

    private Log logger = LogFactory.getLog(this.getClass());
    private List<PolicyDef> listRoots = new ArrayList<PolicyDef>();
    private Map<Identifier, List<PolicyDef>> mapPolicies = new HashMap<Identifier, List<PolicyDef>>();

    public static class StdPolicyFinderException extends Exception {
        private static final long serialVersionUID = -8969282995787463288L;

        public StdPolicyFinderException(String msg) {
            super(msg);
        }

        public StdPolicyFinderException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    private void storeInPolicyMap(PolicyDef policyDef) {
        List<PolicyDef> listPolicyDefs = this.mapPolicies.get(policyDef.getIdentifier());
        if (listPolicyDefs == null) {
            listPolicyDefs = new ArrayList<PolicyDef>();
            this.mapPolicies.put(policyDef.getIdentifier(), listPolicyDefs);
        }
        listPolicyDefs.add(policyDef);
    }

    private <T extends PolicyDef> List<T> getFromPolicyMap(IdReferenceMatch idReferenceMatch,
                                                           Class<T> classPolicyDef) {
        /*
         * Get all of the PolicyDefs for the Identifier in the reference match
         */
        List<PolicyDef> listPolicyDefForId = this.mapPolicies.get(idReferenceMatch.getId());
        if (listPolicyDefForId == null) {
            return null;
        }

        /*
         * Iterate over all of the PolicyDefs that were found and select only the ones that match the version
         * request and the isPolicySet
         */
        List<T> listPolicyDefMatches = null;
        Iterator<PolicyDef> iterPolicyDefs = listPolicyDefForId.iterator();
        while (iterPolicyDefs.hasNext()) {
            PolicyDef policyDef = iterPolicyDefs.next();
            if (classPolicyDef.isInstance(policyDef) && policyDef.matches(idReferenceMatch)) {
                if (listPolicyDefMatches == null) {
                    listPolicyDefMatches = new ArrayList<T>();
                }
                listPolicyDefMatches.add(classPolicyDef.cast(policyDef));
            }
        }

        return listPolicyDefMatches;
    }

    private <T extends PolicyDef> T getBestMatchN(List<T> matches) {
        T bestMatch = null;
        Version bestVersion = null;
        Iterator<T> iterMatches = matches.iterator();

        while (iterMatches.hasNext()) {
            T match = iterMatches.next();
            if (bestMatch == null) {
                bestMatch = match;
                bestVersion = match.getVersion();
            } else {
                Version matchVersion = match.getVersion();
                if (matchVersion != null && matchVersion.compareTo(bestVersion) > 0) {
                    bestMatch = match;
                    bestVersion = matchVersion;
                }
            }
        }
        return bestMatch;
    }

    private <T extends PolicyDef> T getBestMatch(List<T> matches) {
        switch (matches.size()) {
        case 0:
            return null;
        case 1:
            return matches.get(0);
        default:
            return this.getBestMatchN(matches);
        }
    }

    private PolicyDef loadPolicyDefFromURI(URI uri) throws StdPolicyFinderException {
        this.logger.info("Loading policy from URI " + uri.toString());
        URL url = null;
        try {
            url = uri.toURL();
            this.logger.debug("Loading policy from URL " + url.toString());
        } catch (MalformedURLException ex) {
            this.logger.debug("Unknown protocol for URI " + uri.toString());
            return null;
        } 
        
        try (InputStream inputStream = url.openStream()) {
            return DOMPolicyDef.load(inputStream);
        } catch (Exception ex) {
            this.logger.error("Exception loading policy definition", ex);
            throw new StdPolicyFinderException("Exception loading policy def from \"" + uri.toString()
                                               + "\": " + ex.getMessage(), ex);
        }
    }

    /**
     * Looks up the given {@link org.apache.openaz.xacml.api.Identifier} in the map first. If not found, and
     * the <code>Identifier</code> contains a URL, then attempts to retrieve the document from the URL and
     * caches it.
     *
     * @param idReferenceMatch the <code>IdReferenceMatch</code> to look up
     * @return a <code>PolicyFinderResult</code> with the requested <code>Policy</code> or an error status
     */
    private PolicyFinderResult<Policy> lookupPolicyByIdentifier(IdReferenceMatch idReferenceMatch) {
        List<Policy> listCachedPolicies = this.getFromPolicyMap(idReferenceMatch, Policy.class);
        if (listCachedPolicies == null) {
            Identifier id = idReferenceMatch.getId();
            if (id != null) {
                URI uri = id.getUri();
                if (uri != null && uri.isAbsolute()) {
                    PolicyDef policyDef = null;
                    try {
                        policyDef = this.loadPolicyDefFromURI(uri);
                    } catch (StdPolicyFinderException ex) {
                        return new StdPolicyFinderResult<Policy>(
                                                                 new StdStatus(
                                                                               StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                               ex.getMessage()));
                    }
                    if (policyDef != null) {
                        if (policyDef instanceof Policy) {
                            List<PolicyDef> listPolicyDefs = new ArrayList<PolicyDef>();
                            listPolicyDefs.add(policyDef);
                            this.mapPolicies.put(id, listPolicyDefs);
                            this.mapPolicies.put(policyDef.getIdentifier(), listPolicyDefs);
                            return new StdPolicyFinderResult<Policy>((Policy)policyDef);
                        } else {
                            return PFR_NOT_A_POLICY;
                        }
                    } else {
                        return PFR_POLICY_NOT_FOUND;
                    }
                }
            }
        }
        if (listCachedPolicies != null) {
            return new StdPolicyFinderResult<Policy>(this.getBestMatch(listCachedPolicies));
        } else {
            return PFR_POLICY_NOT_FOUND;
        }
    }

    /**
     * Looks up the given {@link org.apache.openaz.xacml.api.Identifier} in the map first. If not found, and
     * the <code>Identifier</code> contains a URL, then attempts to retrieve the document from the URL and
     * caches it.
     *
     * @param idReferenceMatch the <code>IdReferenceMatch</code> to look up
     * @return a <code>PolicyFinderResult</code> with the requested <code>PolicySet</code> or an error status
     */
    private PolicyFinderResult<PolicySet> lookupPolicySetByIdentifier(IdReferenceMatch idReferenceMatch) {
        List<PolicySet> listCachedPolicySets = this.getFromPolicyMap(idReferenceMatch, PolicySet.class);
        if (listCachedPolicySets == null) {
            Identifier id = idReferenceMatch.getId();
            if (id != null) {
                URI uri = id.getUri();
                if (uri != null && uri.isAbsolute()) {
                    PolicyDef policyDef = null;
                    try {
                        policyDef = this.loadPolicyDefFromURI(uri);
                    } catch (StdPolicyFinderException ex) {
                        return new StdPolicyFinderResult<PolicySet>(
                                                                    new StdStatus(
                                                                                  StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                                  ex.getMessage()));
                    }
                    if (policyDef != null) {
                        if (policyDef instanceof PolicySet) {
                            List<PolicyDef> listPolicyDefs = new ArrayList<PolicyDef>();
                            listPolicyDefs.add(policyDef);
                            this.mapPolicies.put(id, listPolicyDefs);
                            this.mapPolicies.put(policyDef.getIdentifier(), listPolicyDefs);
                            return new StdPolicyFinderResult<PolicySet>((PolicySet)policyDef);
                        } else {
                            return PFR_NOT_A_POLICYSET;
                        }
                    } else {
                        return PFR_POLICYSET_NOT_FOUND;
                    }
                }
            }
        }
        if (listCachedPolicySets != null) {
            return new StdPolicyFinderResult<PolicySet>(this.getBestMatch(listCachedPolicySets));
        } else {
            return PFR_POLICYSET_NOT_FOUND;
        }
    }

    /**
     * Adds the given <code>PolicyDef</code> to the map of loaded <code>PolicyDef</code>s and adds its child
     * <code>PolicyDef</code>s recursively.
     *
     * @param policyDef the <code>PolicyDef</code> to add
     */
    private void updatePolicyMap(PolicyDef policyDef) {
        this.storeInPolicyMap(policyDef);
        if (policyDef instanceof PolicySet) {
            Iterator<PolicySetChild> iterChildren = ((PolicySet)policyDef).getChildren();
            if (iterChildren != null) {
                while (iterChildren.hasNext()) {
                    PolicySetChild policySetChild = iterChildren.next();
                    if (policySetChild instanceof PolicyDef) {
                        this.updatePolicyMap((PolicyDef)policySetChild);
                    }
                }
            }
        }
    }

    public StdPolicyFinder(Collection<PolicyDef> listRootPolicies, Collection<PolicyDef> referencedPolicyDefs) {
        if (listRootPolicies != null) {
            for (PolicyDef policyDef : listRootPolicies) {
                this.listRoots.add(policyDef);
                this.updatePolicyMap(policyDef);
            }
        }
        if (referencedPolicyDefs != null) {
            for (PolicyDef policyDef : referencedPolicyDefs) {
                this.storeInPolicyMap(policyDef);
            }
        }
    }

    /**
     * Creates a new <code>StdPolicyFinder</code> with the given <code>PolicyDef</code> as the root element.
     *
     * @param rootPolicyDef the <code>PolicyDef</code> acting as the root element
     */
    public StdPolicyFinder(PolicyDef rootPolicyDef, Collection<PolicyDef> referencedPolicyDefs) {
        if (rootPolicyDef != null) {
            this.listRoots.add(rootPolicyDef);
            this.updatePolicyMap(rootPolicyDef);
        }

        if (referencedPolicyDefs != null) {
            for (PolicyDef policyDef : referencedPolicyDefs) {
                this.storeInPolicyMap(policyDef);
            }
        }
    }

    @Override
    public PolicyFinderResult<PolicyDef> getRootPolicyDef(EvaluationContext evaluationContext) {
        PolicyDef policyDefFirstMatch = null;
        Iterator<PolicyDef> iterRootPolicies = this.listRoots.iterator();
        PolicyFinderResult<PolicyDef> firstIndeterminate = null;
        while (iterRootPolicies.hasNext()) {
            PolicyDef policyDef = iterRootPolicies.next();
            MatchResult matchResult = null;
            try {
                matchResult = policyDef.match(evaluationContext);
                switch (matchResult.getMatchCode()) {
                case INDETERMINATE:
                    if (firstIndeterminate == null) {
                        firstIndeterminate = new StdPolicyFinderResult<PolicyDef>(matchResult.getStatus());
                    }
                    break;
                case MATCH:
                    if (policyDefFirstMatch == null) {
                        policyDefFirstMatch = policyDef;
                    } else {
                        return PFR_MULTIPLE;
                    }
                    break;
                case NOMATCH:
                    break;
                }
            } catch (EvaluationException ex) {
                if (firstIndeterminate == null) {
                    firstIndeterminate = new StdPolicyFinderResult<PolicyDef>(
                                                                              new StdStatus(
                                                                                            StdStatusCode.STATUS_CODE_PROCESSING_ERROR,
                                                                                            ex.getMessage()));
                }
            }
        }

        if (policyDefFirstMatch == null) {
            if (firstIndeterminate != null) {
                return firstIndeterminate;
            } else {
                return PFR_NOT_FOUND;
            }
        } else {
            return new StdPolicyFinderResult<PolicyDef>(policyDefFirstMatch);
        }
    }

    @Override
    public PolicyFinderResult<Policy> getPolicy(IdReferenceMatch idReferenceMatch) {
        return this.lookupPolicyByIdentifier(idReferenceMatch);
    }

    @Override
    public PolicyFinderResult<PolicySet> getPolicySet(IdReferenceMatch idReferenceMatch) {
        return this.lookupPolicySetByIdentifier(idReferenceMatch);
    }

    public void addReferencedPolicy(PolicyDef policyDef) {
        this.updatePolicyMap(policyDef);
    }
}
