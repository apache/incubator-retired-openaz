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
package org.apache.openaz.xacml.pdp.test.conformance;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ConformanceTestSet represents a collection of <code>ConformanceTest</code>s ordered by the test name. It
 * has methods for scanning a directory to generate an ordered set.
 */
public class ConformanceTestSet {
    private static final Log logger = LogFactory.getLog(ConformanceTestSet.class);
    private List<ConformanceTest> listConformanceTests = new ArrayList<ConformanceTest>();

    protected List<ConformanceTest> getListConformanceTests() {
        return this.listConformanceTests;
    }

    protected ConformanceTestSet() {

    }

    private static String getTestName(String fileName, int itemPos) {
        return (itemPos == 0 ? "NULL" : fileName.substring(0, itemPos));
    }

    private static String getTestName(File file) {
        String fileName = file.getName();
        int itemPos = fileName.indexOf("Policy");
        if (itemPos >= 0) {
            return getTestName(fileName, itemPos);
        } else if ((itemPos = fileName.indexOf("Request")) >= 0) {
            return getTestName(fileName, itemPos);
        } else if ((itemPos = fileName.indexOf("Response")) >= 0) {
            return getTestName(fileName, itemPos);
        } else if ((itemPos = fileName.indexOf("Repository")) >= 0) {
            return getTestName(fileName, itemPos);
        } else {
            return null;
        }
    }

    public static ConformanceTestSet loadDirectory(File fileDir) throws IOException {
        final Map<String, ConformanceTest> mapConformanceTests = new HashMap<String, ConformanceTest>();

        Files.walkFileTree(fileDir.toPath(), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                logger.info("Scanning directory " + dir.getFileName());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                File fileVisited = file.toFile();
                String fileName = fileVisited.getName();
                if (fileName.endsWith(".xml") || fileName.endsWith(".properties")) {
                    String testName = getTestName(fileVisited);
                    if (testName != null) {
                        ConformanceTest conformanceTest = mapConformanceTests.get(testName);
                        if (conformanceTest == null) {
                            logger.info("Added test " + testName);
                            conformanceTest = new ConformanceTest(testName);
                            mapConformanceTests.put(testName, conformanceTest);
                        }
                        if (fileName.endsWith("Policy.xml")) {
                            conformanceTest.getRepository().addRootPolicy(fileVisited);
                        } else if (fileName.endsWith("Repository.properties")) {
                            conformanceTest.getRepository().load(fileVisited);
                        } else if (fileName.endsWith("Request.xml")) {
                            conformanceTest.setRequest(fileVisited);
                        } else if (fileName.endsWith("Response.xml")) {
                            conformanceTest.setResponse(fileVisited);
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                logger.warn("Skipped " + file.getFileName());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

        /*
         * Sort the keyset and pull out the tests that have the required components
         */
        List<String> listTestNames = new ArrayList<String>();
        listTestNames.addAll(mapConformanceTests.keySet());
        Collections.sort(listTestNames);

        ConformanceTestSet conformanceTestSet = new ConformanceTestSet();
        Iterator<String> iterTestNames = listTestNames.iterator();
        while (iterTestNames.hasNext()) {
            ConformanceTest conformanceTest = mapConformanceTests.get(iterTestNames.next());
            if (conformanceTest.isComplete()) {
                conformanceTestSet.addConformanceTest(conformanceTest);
                logger.debug("Added conformance test " + conformanceTest.getTestName());
            } else {
                logger.warn("Incomplete conformance test " + conformanceTest.getTestName());
            }
        }

        return conformanceTestSet;

    }

    public Iterator<ConformanceTest> getConformanceTests() {
        return this.listConformanceTests.iterator();
    }

    public void addConformanceTest(ConformanceTest conformanceTest) {
        this.listConformanceTests.add(conformanceTest);
    }

    public void addConformanceTestSet(ConformanceTestSet conformanceTestSet) {
        this.listConformanceTests.addAll(conformanceTestSet.getListConformanceTests());
    }

    public static void main(String[] args) {
        for (String dir : args) {
            try {
                ConformanceTestSet conformanceTestSet = ConformanceTestSet.loadDirectory(new File(dir));
                Iterator<ConformanceTest> iterConformanceTests = conformanceTestSet.getConformanceTests();
                if (iterConformanceTests == null) {
                    System.out.println("No tests found in " + dir);
                } else {
                    System.out.println("Tests found in " + dir);
                    while (iterConformanceTests.hasNext()) {
                        System.out.println(iterConformanceTests.next().toString());
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}
