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
package org.apache.openaz.xacml.util;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

/**
 * Helper static class for policy writing.
 */
public class XACMLPolicyWriter {
    private static final Log logger = LogFactory.getLog(XACMLPolicyWriter.class);

    /**
     * Helper static class that does the work to write a policy set to a file on disk.
     */
    public static Path writePolicyFile(Path filename, PolicySetType policySet) {
        JAXBElement<PolicySetType> policySetElement = new ObjectFactory().createPolicySet(policySet);
        try {
            JAXBContext context = JAXBContext.newInstance(PolicySetType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(policySetElement, filename.toFile());

            if (Files.exists(filename)) {
                return filename;
            } else {
                logger.error("File does not exist after marshalling.");
                return null;
            }

        } catch (JAXBException e) {
            logger.error("writePolicyFile failed: " + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Helper static class that does the work to write a policy set to an output stream.
     */
    public static void writePolicyFile(OutputStream os, PolicySetType policySet) {
        JAXBElement<PolicySetType> policySetElement = new ObjectFactory().createPolicySet(policySet);
        try {
            JAXBContext context = JAXBContext.newInstance(PolicySetType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(policySetElement, os);
        } catch (JAXBException e) {
            logger.error("writePolicyFile failed: " + e.getLocalizedMessage());
        }
    }

    /**
     * Helper static class that does the work to write a policy to a file on disk.
     */
    public static Path writePolicyFile(Path filename, PolicyType policy) {
        JAXBElement<PolicyType> policyElement = new ObjectFactory().createPolicy(policy);
        try {
            JAXBContext context = JAXBContext.newInstance(PolicyType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(policyElement, filename.toFile());

            if (Files.exists(filename)) {
                return filename;
            } else {
                logger.error("File does not exist after marshalling.");
                return null;
            }

        } catch (JAXBException e) {
            logger.error("writePolicyFile failed: " + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Helper static class that does the work to write a policy set to an output stream.
     */
    public static void writePolicyFile(OutputStream os, PolicyType policy) {
        JAXBElement<PolicyType> policySetElement = new ObjectFactory().createPolicy(policy);
        try {
            JAXBContext context = JAXBContext.newInstance(PolicyType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(policySetElement, os);
        } catch (JAXBException e) {
            logger.error("writePolicyFile failed: " + e.getLocalizedMessage());
        }
    }

}
