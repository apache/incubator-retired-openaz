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
package org.apache.openaz.xacml.pdp.policy.dom;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * DOMPolicyRepair is an application for reading a XACML Policy or PolicySet document and ensuring it has the
 * required attributes and then writing the repaired Policy or PolicySet to an output file.
 */
public class DOMPolicyRepair {
    private static final String DEFAULT_VERSION = "1.0";

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        OutputStream outputStream = System.out;

        for (int i = 0; i < args.length;) {
            if (args[i].equals("-i")) {
                if (i + 1 < args.length) {
                    try {
                        inputStream = new FileInputStream(args[i + 1]);
                    } catch (IOException ex) {
                        System.err.println("IOException opening \"" + args[i + 1] + "\" for reading.");
                        System.exit(1);
                    }
                    i += 2;
                } else {
                    i++;
                }
            } else if (args[i].equals("-o")) {
                if (i + 1 < args.length) {
                    try {
                        outputStream = new FileOutputStream(args[i + 1]);
                    } catch (IOException ex) {
                        System.err.println("IOException opening \"" + args[i + 1] + "\" for writing.");
                        ex.printStackTrace(System.err);
                        System.exit(1);
                    }
                    i += 2;
                } else {
                    i++;
                }
            } else {
                System.err.println("Unrecognized command line option \"" + args[i] + "\"");
                System.exit(1);
            }
        }

        /*
         * Get the XML Parser for the input file
         */
        try {
            Document documentInput = DOMUtil.loadDocument(inputStream);
            Element elementRoot = DOMUtil.getFirstChildElement(documentInput);
            if (elementRoot == null) {
                System.err.println("No root element");
                System.exit(1);
            } else if (!XACML3.ELEMENT_POLICY.equals(elementRoot.getLocalName())
                       && !XACML3.ELEMENT_POLICYSET.equals(elementRoot.getLocalName())) {
                System.err.println("Root element is not a Policy or PolicySet");
                System.exit(1);
            }

            /*
             * Make sure there is a Version attribute
             */
            Node nodeVersion = DOMUtil.getAttribute(elementRoot, XACML3.ATTRIBUTE_VERSION);
            if (nodeVersion == null) {
                System.out.println("Adding Version attribute with value \"" + DEFAULT_VERSION + "\"");
                elementRoot.setAttribute(XACML3.ATTRIBUTE_VERSION, DEFAULT_VERSION);
            }

            /*
             * Write out the updated document
             */
            String newDocument = DOMUtil.toString(documentInput);
            outputStream.write(newDocument.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            System.exit(1);
        }
        System.exit(0);
    }

}
