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

package org.onap.policy.apex.client.editor.rest;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class reads and handles command line parameters to the Apex CLI editor.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexEditorParameters {
    /** The default port for connecting to the Web editor on. */
    public static final int DEFAULT_REST_PORT = 18989;

    /** The connection is held up until killed on demand. */
    public static final int INFINITY_TIME_TO_LIVE = -1;

    // Base URI the HTTP server will listen on
    private static final String DEFAULT_SERVER_URI_PREFIX = "http://";
    /** The server listens on all available interfaces/addresses. */
    public static final String DEFAULT_SERVER_URI_ROOT = "0.0.0.0";
    private static final String DEFAULT_REST_PATH = "/apexservices/";
    private static final String DEFAULT_STATIC_PATH = "/";

    // Constants for port checks
    private static final int MIN_USER_PORT = 1024;
    private static final int MAX_USER_PORT = 65535;


    // Package that will field REST requests
    private static final String[] DEFAULT_PACKAGES = new String[] { "org.onap.policy.apex.client.editor.rest" };

    // The editor parameters
    private boolean helpSet = false;
    private int restPort = DEFAULT_REST_PORT;
    private long timeToLive = INFINITY_TIME_TO_LIVE;
    private String listenAddress = DEFAULT_SERVER_URI_ROOT;

    /**
     * Validate.
     *
     * @return the string
     */
    public String validate() {
        String validationMessage = "";
        validationMessage += validatePort();
        validationMessage += validateTimeToLive();
        validationMessage += validateUrl();

        return validationMessage;
    }

    /**
     * Gets the base URI.
     *
     * @return the base URI
     */
    public URI getBaseURI() {
        return URI.create(DEFAULT_SERVER_URI_PREFIX + listenAddress + ':' + restPort + DEFAULT_REST_PATH);
    }

    /**
     * Gets the REST packages.
     *
     * @return the REST packages
     */
    public String[] getRESTPackages() {
        return DEFAULT_PACKAGES;
    }

    /**
     * Gets the static path.
     *
     * @return the static path
     */
    public String getStaticPath() {
        return DEFAULT_STATIC_PATH;
    }

    /**
     * Validate port.
     *
     * @return a warning string, or an empty string
     */
    private String validatePort() {
        if (restPort < MIN_USER_PORT || restPort > MAX_USER_PORT) {
            return "port must be between " + MIN_USER_PORT + " and " + MAX_USER_PORT + "\n";
        } else {
            return "";
        }
    }

    /**
     * Validate URL.
     *
     * @return a warning string, or an empty string
     */
    private String validateUrl() {
        try {
            new URI(getBaseURI().toString()).parseServerAuthority();
            return "";
        } catch (final URISyntaxException e) {
            return "listen address is not valid. " + e.getMessage() + "\n";
        }
    }

    /**
     * Validate time to live.
     *
     * @return the string
     */
    private String validateTimeToLive() {
        if (timeToLive < -1) {
            return "time to live must be greater than -1 (set to -1 to wait forever)\n";
        } else {
            return "";
        }
    }

    /**
     * Checks if is help set.
     *
     * @return true, if checks if is help set
     */
    public boolean isHelpSet() {
        return helpSet;
    }

    /**
     * Sets the help.
     *
     * @param help the help
     */
    public void setHelp(final boolean help) {
        this.helpSet = help;
    }

    /**
     * Gets the REST port.
     *
     * @return the REST port
     */
    public int getRESTPort() {
        return restPort;
    }

    /**
     * Sets the REST port.
     *
     * @param incomingRestPort the REST port
     */
    public void setRESTPort(final int incomingRestPort) {
        this.restPort = incomingRestPort;
    }

    /**
     * Gets the time to live.
     *
     * @return the time to live
     */
    public long getTimeToLive() {
        return timeToLive;
    }

    /**
     * Sets the time to live.
     *
     * @param timeToLive the time to live
     */
    public void setTimeToLive(final long timeToLive) {
        this.timeToLive = timeToLive;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append(this.getClass().getSimpleName()).append(": URI=").append(this.getBaseURI()).append(", TTL=")
                .append(this.getTimeToLive()).append("sec");
        return ret.toString();
    }

    /**
     * Gets the base address to listen on.
     *
     * @return the listenAddress
     */
    public String getListenAddress() {
        return listenAddress;
    }

    /**
     * Sets the base address to listen on.
     *
     * @param listenAddress the new listenAddress
     */
    public void setListenAddress(final String listenAddress) {
        this.listenAddress = listenAddress;
    }
}
