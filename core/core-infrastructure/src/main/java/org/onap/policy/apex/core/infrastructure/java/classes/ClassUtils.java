/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.core.infrastructure.java.classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is a utility class used to find Java classes on the class path, in directories, and in Jar files.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class ClassUtils {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ClassUtils.class);

    // Repeated string constants
    private static final String CLASS_PATTERN = "\\.class$";

    // The boot directory in Java for predefined JARs
    private static final String SUN_BOOT_LIBRARY_PATH = "sun.boot.library.path";

    // Token for Classes directory in paths
    private static final String CLASSES_TOKEN = "/classes/";

    // Token for library fragment in path
    private static final String LIBRARAY_PATH_TOKEN = "/lib";

    /**
     * Private constructor used to prevent sub class instantiation.
     */
    private ClassUtils() {}

    /**
     * Get the class names of all classes on the class path. WARNING: This is a heavy call, use sparingly
     *
     * @return a set of class names for all classes in the class path
     */
    public static Set<String> getClassNames() {
        // The return set of class names
        final Set<String> classNameSet = new TreeSet<>();

        try {
            // The library path for predefined classes in Java
            String sunBootLibraryPathString = System.getProperty(SUN_BOOT_LIBRARY_PATH);

            // Check it exists and has a "lib" in it
            if (sunBootLibraryPathString != null && sunBootLibraryPathString.contains(LIBRARAY_PATH_TOKEN)) {
                // Strip any superfluous trailer from path
                sunBootLibraryPathString = sunBootLibraryPathString.substring(0,
                        sunBootLibraryPathString.lastIndexOf(LIBRARAY_PATH_TOKEN) + LIBRARAY_PATH_TOKEN.length());

                final File bootLibraryFile = new File(sunBootLibraryPathString);
                // The set used to hold class names is populated with predefined Java classes
                classNameSet.addAll(processDir(bootLibraryFile, ""));
            }

            // Get the entries on the class path
            URL[] urls = ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs();

            // Try get the classes in the bootstrap loader
            try {
                final Class<?> nullclassloader = Class.forName("sun.misc.Launcher");
                if (nullclassloader != null) {
                    Method mmethod = nullclassloader.getMethod("getBootstrapClassPath");
                    if (mmethod != null) {
                        final Object cp = mmethod.invoke(null, (Object[]) null);
                        if (cp != null) {
                            mmethod = cp.getClass().getMethod("getURLs");
                            if (mmethod != null) {
                                final URL[] moreurls = (URL[]) (mmethod.invoke(cp, (Object[]) null));
                                if (moreurls != null && moreurls.length > 0) {
                                    if (urls.length == 0) {
                                        urls = moreurls;
                                    } else {
                                        final URL[] result = Arrays.copyOf(urls, urls.length + moreurls.length);
                                        System.arraycopy(moreurls, 0, result, urls.length, moreurls.length);
                                        urls = result;
                                    }
                                }
                            }
                        }
                    }
                    // end long way!
                }
            } catch (final ClassNotFoundException e) {
                LOGGER.warn("Failed to find default path for JRE libraries", e);
            }

            // Iterate over the class path entries
            for (final URL url : urls) {
                if (url == null || url.getFile() == null) {
                    continue;
                }
                final File urlFile = new File(url.getFile());
                // Directories may contain ".class" files
                if (urlFile.isDirectory()) {
                    classNameSet.addAll(processDir(urlFile, url.getFile()));
                }
                // JARs are processed as well
                else if (url.getFile().endsWith(".jar")) {
                    classNameSet.addAll(processJar(urlFile));
                }
                // It's a resource or some other non-executable thing
            }
        } catch (final Exception e) {
            LOGGER.warn("could not get the names of Java classes", e);
        }

        return classNameSet;
    }

    /**
     * Find all classes in directories and JARs in those directories.
     *
     * @param classDirectory The directory to search for classes
     * @param rootDir The root directory, to be removed from absolute paths
     * @return a set of classes which may be empty
     * @throws Exception on errors processing directories
     */
    public static Set<String> processDir(final File classDirectory, final String rootDir) throws Exception {
        // The return set
        final TreeSet<String> classNameSet = new TreeSet<>();

        // Iterate over the directory
        if (classDirectory == null || !classDirectory.isDirectory()) {
            return classNameSet;
        }
        for (final File child : classDirectory.listFiles()) {
            if (child.isDirectory()) {
                // Recurse down
                classNameSet.addAll(processDir(child, rootDir));
            } else if (child.getName().endsWith(".jar")) {
                // Process the JAR
                classNameSet.addAll(processJar(child));
            } else if (child.getName().endsWith(".class") && !child.getName().contains("$")) {
                // Process the ".class" file
                classNameSet.add(
                        child.getAbsolutePath().replace(rootDir, "").replaceFirst(CLASS_PATTERN, "").replace('/', '.'));
            }
        }
        return classNameSet;
    }

    /**
     * Condition the file name as a class name.
     *
     * @param fileNameIn The file name to convert to a class name
     * @return the conditioned class name
     */
    public static String processFileName(final String fileNameIn) {
        String fileName = fileNameIn;

        if (fileName == null) {
            return null;
        }
        final int classesPos = fileName.indexOf(CLASSES_TOKEN);

        if (classesPos != -1) {
            fileName = fileName.substring(classesPos + CLASSES_TOKEN.length());
        }

        return fileName.replaceFirst(CLASS_PATTERN, "").replace('/', '.');
    }

    /**
     * Read all the class names from a Jar.
     *
     * @param jarFile the JAR file
     * @return a set of class names
     * @throws IOException on errors processing JARs
     */
    public static Set<String> processJar(final File jarFile) throws IOException {
        // Pass the file as an input stream
        return processJar(new FileInputStream(jarFile.getAbsolutePath()));
    }

    /**
     * Read all the class names from a Jar.
     *
     * @param jarInputStream the JAR input stream
     * @return a set of class names
     * @throws IOException on errors processing JARs
     */
    public static Set<String> processJar(final InputStream jarInputStream) throws IOException {
        // The return set
        final TreeSet<String> classPathSet = new TreeSet<>();

        if (jarInputStream == null) {
            return classPathSet;
        }
        // JARs are ZIP files
        final ZipInputStream zip = new ZipInputStream(jarInputStream);

        // Iterate over each entry in the JAR
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class") && !entry.getName().contains("$")) {
                classPathSet.add(entry.getName().replaceFirst(CLASS_PATTERN, "").replace('/', '.'));
            }
        }
        zip.close();
        return classPathSet;
    }

    /**
     * The main method exercises this class for test purposes.
     *
     * @param args the args
     */
    public static void main(final String[] args) {
        for (final String clz : getClassNames()) {
            LOGGER.info("Found class: {}", clz);
        }
    }
}
