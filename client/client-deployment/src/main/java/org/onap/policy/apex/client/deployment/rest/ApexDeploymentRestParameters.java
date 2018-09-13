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

package org.onap.policy.apex.client.deployment.rest;

import java.net.URI;

/**
 * This class reads and handles command line parameters to the Apex RESTful services.
 *
 * @author Michael Watkins (michael.watkins@ericsson.com)
 */
public class ApexDeploymentRestParameters {
    public static final int DEFAULT_REST_PORT = 18989;
    public static final int INFINITY_TIME_TO_LIVE = -1;

    // Base URI the HTTP server will listen on
    private static final String DEFAULT_SERVER_URI_ROOT = "http://localhost:";
    private static final String DEFAULT_REST_PATH = "/apexservices/";
    private static final String DEFAULT_STATIC_PATH = "/";

    // Package that will field REST requests
    private static final String[] DEFAULT_PACKAGES = new String[] {"org.onap.policy.apex.client.deployment.rest"};

    // The services parameters
    private boolean helpSet = false;
    private int restPort = DEFAULT_REST_PORT;
    private long timeToLive = INFINITY_TIME_TO_LIVE;

    /**
     * Validate the parameters.
     *
     * @return the result of the validation
     */
    public String validate() {
        String validationMessage = "";
        validationMessage += validatePort();
        validationMessage += validateTimeToLive();

        return validationMessage;
    }

    /**
     * Gets the base uri.
     *
     * @return the base uri
     */
    public URI getBaseUri() {
        return URI.create(DEFAULT_SERVER_URI_ROOT + restPort + DEFAULT_REST_PATH);
    }

    /**
     * Gets the rest packages.
     *
     * @return the rest packages
     */
    public String[] getRestPackages() {
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
     * @return the string
     */
    private String validatePort() {
        if (restPort < 1024 || restPort > 65535) {
            return "port must be greater than 1023 and less than 65536\n";
        } else {
            return "";
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
     * @return true, if is help set
     */
    public boolean isHelpSet() {
        return helpSet;
    }

    /**
     * Sets the help.
     *
     * @param helpSet the new help
     */
    public void setHelp(final boolean helpSet) {
        this.helpSet = helpSet;
    }

    /**
     * Gets the rest port.
     *
     * @return the rest port
     */
    public int getRestPort() {
        return restPort;
    }

    /**
     * Sets the rest port.
     *
     * @param restPort the new rest port
     */
    public void setRestPort(final int restPort) {
        this.restPort = restPort;
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
     * @param timeToLive the new time to live
     */
    public void setTimeToLive(final long timeToLive) {
        this.timeToLive = timeToLive;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append(this.getClass().getSimpleName()).append(": URI=").append(this.getBaseUri()).append(", TTL=")
                .append(this.getTimeToLive()).append("sec");
        return ret.toString();
    }
}
