package org.openliberty.openaz.pepapi.std;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Properties;

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

        //Try the location as a file first.
        File file = new File(propertyFile);
        InputStream in;
        if(file.exists() && file.canRead()) {
            if (!file.isAbsolute()) {
                file = file.getAbsoluteFile();
            }
            try {
                in = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                logger.info(propertyFile + " is not a file.");
            }
        }

        in = PepUtils.class.getResourceAsStream(propertyFile);

        if(in == null) {
            logger.error("Invalid classpath of file location: " + propertyFile);
            throw new IllegalArgumentException("Invalid classpath or file location: " + propertyFile);
        }

        try {
            properties.load(in);
        } catch (IOException e) {
            logger.error(e);
            throw new IllegalArgumentException(e);
        } finally {
            if(in != null) {
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
