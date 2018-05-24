/*
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

package org.onap.policy.apex.model.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This is common utility class with static methods for handling Java resources on the class path. It is an abstract class to prevent any direct instantiation
 * and private constructor to prevent extending this class.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class ResourceUtils {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ResourceUtils.class);

    // The length of byte buffers used to read resources into strings
    private static final int BYTE_BUFFER_LENGH = 1024;

    /**
     * Private constructor used to prevent sub class instantiation.
     */
    private ResourceUtils() {
    }

    /**
     * Method to resolve a resource; the local file system is checked first and then the class path is checked.
     *
     * @param resourceName The resource name
     * @return A URL to a resource
     */
    public static URL getURL4Resource(final String resourceName) {
        // Check the local fine system first
        final URL urlToResource = getLocalFile(resourceName);

        // Check if this is a local file
        if (urlToResource != null) {
            return urlToResource;
        }
        else {
            // Resort to the class path
            return getURLResource(resourceName);
        }
    }

    /**
     * Method to return a resource as a string. The resource can be on the local file system or in the class path. The resource is resolved and loaded into a
     * string.
     *
     * @param resourceName The resource name
     * @return A string containing the resource
     */
    public static String getResourceAsString(final String resourceName) {
        // Get the resource as a stream, we'll convert it to a string then
        final InputStream resourceStream = getResourceAsStream(resourceName);
        if (resourceStream == null) {
            return null;
        }

        // Read the stream contents in to an output stream
        final ByteArrayOutputStream resourceOutputStreamBuffer = new ByteArrayOutputStream();
        final byte[] resourceBuffer = new byte[BYTE_BUFFER_LENGH];
        int length;
        try {
            while ((length = resourceStream.read(resourceBuffer)) != -1) {
                resourceOutputStreamBuffer.write(resourceBuffer, 0, length);
            }
        }
        catch (final IOException e) {
            LOGGER.debug("error reading resource stream \"{}\" : " + e.getMessage(), resourceName, e);
            return null;
        }

        return resourceOutputStreamBuffer.toString();
    }

    /**
     * Method to return a resource as a stream. The resource can be on the local file system or in the class path. The resource is resolved and returned as a
     * stream.
     *
     * @param resourceName The resource name
     * @return A stream attached to the resource
     */
    public static InputStream getResourceAsStream(final String resourceName) {
        // Find a URL to the resource first
        final URL urlToResource = getURL4Resource(resourceName);

        // Check if the resource exists
        if (urlToResource == null) {
            // No resource found
            LOGGER.debug("cound not find resource \"{}\" : ", resourceName);
            return null;
        }

        // Read the resource into a string
        try {
            return urlToResource.openStream();
        }
        catch (final IOException e) {
            // Any of many IO exceptions such as the resource is a directory
            LOGGER.debug("error attaching resource \"{}\" to stream : " + e.getMessage(), resourceName, e);
            return null;
        }
    }

    /**
     * Method to get a URL resource from the class path.
     *
     * @param resourceName The resource name
     * @return The URL to the resource
     */
    public static URL getURLResource(final String resourceName) {
        try {
            final ClassLoader classLoader = ResourceUtils.class.getClassLoader();

            final String[] fileParts = resourceName.split("/");
            // Read the resource
            URL url = classLoader.getResource(resourceName);

            // Check if the resource is defined
            if (url != null) {
                // Return the resource as a file name
                LOGGER.debug("found URL resource \"{}\" : ", url);
                return url;
            }
            else {
                url = classLoader.getResource(fileParts[fileParts.length - 1]);
                if (url == null) {
                    LOGGER.debug("cound not find URL resource \"{}\" : ", resourceName);
                    return null;
                }
                LOGGER.debug("found URL resource \"{}\" : ", url);
                return url;
            }
        }
        catch (final Exception e) {
            LOGGER.debug("error getting URL resource \"{}\" : " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to get a URL resource from the local machine.
     *
     * @param resourceName The resource name
     * @return The URL to the resource
     */
    public static URL getLocalFile(final String resourceName) {
        try {
            // Input might already be in URL format
            final URL ret = new URL(resourceName);
            final File f = new File(ret.toURI());
            if (f.exists()) {
                return ret;
            }
        }
        catch (final Exception ignore) {
        		// We ignore exceptions here and catch them below
        }

        try {
            final File f = new File(resourceName);
            // Check if the file exists
            if (f.exists()) {
                final URL urlret = f.toURI().toURL();
                LOGGER.debug("resource \"{}\" was found on the local file system", f.toURI().toURL());
                return urlret;
            }
            else {
                LOGGER.debug("resource \"{}\" does not exist on the local file system", resourceName);
                return null;
            }
        }
        catch (final Exception e) {
            LOGGER.debug("error finding resource \"{}\" : " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets the file path for a resource on the local file system or on the class path.
     *
     * @param resource the resource to the get the file path for
     * @return the resource file path
     */
    public static String getFilePath4Resource(final String resource) {
        if (resource == null) {
            return null;
        }

        URL modelFileURL = getURL4Resource(resource);
        if (modelFileURL != null) {
            return modelFileURL.getPath();
        }
        else {
            return resource;
        }
    }

}
