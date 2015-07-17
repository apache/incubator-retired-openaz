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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 *
 */
public class PepUtils {

    private static final Log logger = LogFactory.getLog(PepUtils.class);

    public static Class<?> loadClass(String className) {
        ClassLoader currentClassLoader = PepUtils.class.getClassLoader();
        Class<?> clazz;
        try {
            clazz = currentClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                clazz = contextClassLoader.loadClass(className);
            } catch (ClassNotFoundException e1) {
                throw new IllegalArgumentException(e);
            }
        }
        return clazz;
    }

    public static <T> T instantiateClass(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Properties loadProperties(String propertyFile) {
        Properties properties = new Properties();

        // Try the location as a file first.
        File file = new File(propertyFile);
        InputStream in = null;
        if (file.exists() && file.canRead()) {
            if (!file.isAbsolute()) {
                file = file.getAbsoluteFile();
            }
            try {
                in = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                logger.error("Error while accessing file: " + propertyFile);
                throw new IllegalArgumentException(e);
            }
        } else {
            Set<ClassLoader> classLoaders = new HashSet<>();
            classLoaders.add(PepUtils.class.getClassLoader());
            classLoaders.add(Thread.currentThread().getContextClassLoader());
            for(ClassLoader classLoader: classLoaders) {
                in = classLoader.getResourceAsStream(propertyFile);
                if(in != null) {
                    break;
                }
            }
            if(in == null) {
                logger.error("Invalid classpath or file location: " + propertyFile);
                throw new IllegalArgumentException("Invalid classpath or file location: " + propertyFile);
            }
        }

        try {
            properties.load(in);
        } catch (IOException e) {
            logger.error(e);
            throw new IllegalArgumentException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.debug("Error closing stream", e);
                }
            }
        }
        return properties;
    }
}
