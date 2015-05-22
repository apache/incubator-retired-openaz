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
package org.apache.openaz.xacml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.std.dom.DOMDocumentRepair;
import org.apache.openaz.xacml.std.dom.DOMStructureException;
import org.apache.openaz.xacml.std.dom.DOMUtil;
import org.w3c.dom.Document;

/**
 * XACMLRepair is an application class that can load individual XACML documents or directories of XACML
 * documents, make any needed repairs on them, and write them back out to an output file or directory.
 */
public class XACMLRepair {
    private static final Log logger = LogFactory.getLog(XACMLRepair.class);

    public static final String PROP_DOCUMENT_REPAIR_CLASSNAME = "xacml.documentRepairClassName";

    private List<File> listInputFilesOrDirectories = new ArrayList<File>();
    private File outputFileOrDirectory;
    private boolean forceOutput;
    private String documentRepairClassName;
    private DOMDocumentRepair domDocumentRepair;
    private boolean verbose;

    private DOMDocumentRepair getDOMDocumentRepair() {
        if (this.domDocumentRepair == null) {
            if (this.documentRepairClassName == null) {
                this.documentRepairClassName = System.getProperty(PROP_DOCUMENT_REPAIR_CLASSNAME);
            }
            if (this.documentRepairClassName == null) {
                this.domDocumentRepair = new DOMDocumentRepair();
            } else {
                try {
                    Class<?> classDocumentRepair = Class.forName(this.documentRepairClassName);
                    if (!DOMDocumentRepair.class.isAssignableFrom(classDocumentRepair)) {
                        throw new IllegalArgumentException("Not a DOMDocumentRepair class");
                    }
                    this.domDocumentRepair = (DOMDocumentRepair)(classDocumentRepair.newInstance());
                } catch (Exception ex) {
                    System.err.println("Warning: Could not find Class " + this.documentRepairClassName + ":"
                                       + ex.getMessage() + ": using "
                                       + DOMDocumentRepair.class.getCanonicalName());
                    this.domDocumentRepair = new DOMDocumentRepair();
                }
            }
        }
        return this.domDocumentRepair;
    }

    private boolean init(String[] args) {
        for (int i = 0; i < args.length;) {
            if (args[i].equals("--input") || args[i].equals("-i")) {
                if (i + 1 < args.length) {
                    i++;
                    while (i < args.length && !args[i].startsWith("-")) {
                        this.listInputFilesOrDirectories.add(new File(args[i++]));
                    }
                } else {
                    System.err.println("Missing argument to " + args[i] + " command line option");
                    return false;
                }
            } else if (args[i].equals("--output") || args[i].equals("-o")) {
                if (i + 1 < args.length) {
                    this.outputFileOrDirectory = new File(args[i + 1]);
                    i += 2;
                } else {
                    System.err.println("Missing argument to " + args[i] + " command line option");
                    return false;
                }
            } else if (args[i].equals("--force") || args[i].equals("-f")) {
                if (i + 1 < args.length) {
                    this.forceOutput = true;
                    i += 1;
                } else {
                    System.err.println("Missing argument to " + args[i] + " command line option");
                    return false;
                }
            } else if (args[i].equals("--repairClass")) {
                if (i + 1 < args.length) {
                    this.documentRepairClassName = args[i + 1];
                    i += 2;
                } else {
                    System.err.println("Missing argument to " + args[i] + " command line option");
                    return false;
                }
            } else if (args[i].equals("--verbose") || args[i].equals("-i")) {
                this.verbose = true;
                i += 1;
            } else {
                System.err.println("Unknown command line option " + args[i]);
                return false;
            }
        }
        this.getDOMDocumentRepair();
        return true;
    }

    private boolean run(InputStream inputStream, File fileOrig, OutputStream outputStream, File fileDest)
        throws Exception {
        String msg = "Repairing " + (fileOrig == null ? "stdin" : fileOrig.getAbsoluteFile());
        if (this.verbose) {
            System.out.println(msg);
        }
        logger.info(msg);
        Document documentFile = null;
        try {
            documentFile = DOMUtil.loadDocument(inputStream);
        } catch (DOMStructureException ex) {
            System.err.println((msg = "Error loading "
                                      + (fileOrig == null ? "from stdin" : fileOrig.getAbsoluteFile()) + ": "
                                      + ex.getMessage()));
            logger.error(msg);
            return false;
        }
        if (documentFile == null) {
            System.err.println((msg = "No document "
                                      + (fileOrig == null ? "from stdin" : fileOrig.getAbsoluteFile())));
            logger.error(msg);
            return false;
        }
        boolean bUpdated = false;
        DOMDocumentRepair domDocumentRepair = this.getDOMDocumentRepair();
        try {
            bUpdated = domDocumentRepair.repair(documentFile);
        } catch (DOMStructureException ex) {
            System.err.println((msg = "Error repairing "
                                      + (fileOrig == null ? "from stdin" : fileOrig.getAbsoluteFile()) + ": "
                                      + ex.getMessage()));
            logger.error(msg);
            return false;
        } catch (DOMDocumentRepair.UnsupportedDocumentTypeException ex) {
            msg = "Unknown document type in " + (fileOrig == null ? "stdin" : fileOrig.getAbsoluteFile())
                  + ": skipping";
            if (this.verbose) {
                System.err.println(msg);
            }
            logger.debug(msg);
            return false;
        }
        if (bUpdated) {
            msg = "Repairs made in " + (fileOrig == null ? "stdin" : fileOrig.getAbsoluteFile());
            if (verbose) {
                System.out.println(msg);
            }
            logger.debug(msg);
        }
        if (bUpdated || this.forceOutput) {
            System.out.println((msg = "Writing to "
                                      + (fileDest == null ? "stdout" : fileDest.getAbsoluteFile())));
            logger.info(msg);
            String newDocument = DOMUtil.toString(documentFile);
            outputStream.write(newDocument.getBytes());
            outputStream.flush();
            return true;
        } else {
            return false;
        }
    }

    private void run(InputStream inputStream, File fileOrig) throws Exception {
        if (this.outputFileOrDirectory == null) {
            this.run(inputStream, fileOrig, System.out, null);
        } else if (this.outputFileOrDirectory.exists()) {
            if (this.outputFileOrDirectory.isDirectory()) {
                File fileOutput = new File(this.outputFileOrDirectory, fileOrig.getName());
                boolean bWritten = false;
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOutput)) {
                    bWritten = this.run(inputStream, fileOrig, fileOutputStream, fileOutput);
                }
                if (!bWritten) {
                    fileOutput.delete();
                }
            } else {
                boolean bWritten = false;
                try (FileOutputStream fileOutputStream = new FileOutputStream(this.outputFileOrDirectory)) {
                    bWritten = this.run(inputStream, fileOrig, fileOutputStream, this.outputFileOrDirectory);
                }
                if (!bWritten) {
                    this.outputFileOrDirectory.delete();
                }
            }
        }
    }

    private void run(File inputFile) throws Exception {
        String msg;
        if (!inputFile.exists()) {
            System.err.println((msg = "Input file " + inputFile.getAbsolutePath() + " does not exist."));
            logger.error(msg);
            return;
        } else if (inputFile.isDirectory()) {
            File[] directoryContents = inputFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".xml");
                }
            });
            if (directoryContents != null && directoryContents.length > 0) {
                for (File file : directoryContents) {
                    this.run(file);
                }
            }
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {
                this.run(fileInputStream, inputFile);
            }
        }
    }

    private void run() throws Exception {
        if (this.listInputFilesOrDirectories.size() == 0) {
            this.run(System.in, (File)null);
        } else {
            for (File inputFile : this.listInputFilesOrDirectories) {
                this.run(inputFile);
            }
        }
    }

    public XACMLRepair() {
    }

    public static void main(String[] args) {
        XACMLRepair xacmlRepair = new XACMLRepair();
        try {
            if (xacmlRepair.init(args)) {
                xacmlRepair.run();
            }
        } catch (Exception ex) {
            System.err.println("Exception: " + ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(1);
        }
        System.exit(0);
    }

}
