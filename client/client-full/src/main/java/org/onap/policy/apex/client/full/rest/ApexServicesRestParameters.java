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

package org.onap.policy.apex.client.full.rest;

import java.net.URI;

/**
 * This class reads and handles command line parameters to the Apex RESTful services.
 *
 * <p>User: ewatkmi Date: 31 Jul 2017
 */
public class ApexServicesRestParameters {
    public static final int DEFAULT_REST_PORT = 18989;
    public static final int INFINITY_TIME_TO_LIVE = -1;

    // Base URI the HTTP server will listen on
    private static final String DEFAULT_SERVER_URI_ROOT = "http://localhost:";
    private static final String DEFAULT_REST_PATH = "/apexservices/";
    private static final String DEFAULT_STATIC_PATH = "/";

    // Package that will field REST requests
    private static final String[] DEFAULT_PACKAGES = new String[] { "org.onap.policy.apex.client.deployment.rest",
            "org.onap.policy.apex.client.editor.rest", "org.onap.policy.apex.client.monitoring.rest" };

    // The services parameters
    private boolean helpSet = false;
    private int restPort = DEFAULT_REST_PORT;
    private long timeToLive = INFINITY_TIME_TO_LIVE;

    public String validate() {
        String validationMessage = "";
        validationMessage += validatePort();
        validationMessage += validateTimeToLive();

        return validationMessage;
    }

    public URI getBaseURI() {
        return URI.create(DEFAULT_SERVER_URI_ROOT + restPort + DEFAULT_REST_PATH);
    }

    public String[] getRESTPackages() {
        return DEFAULT_PACKAGES;
    }

    public String getStaticPath() {
        return DEFAULT_STATIC_PATH;
    }

    private String validatePort() {
        if (restPort < 1024 || restPort > 65535) {
            return "port must be greater than 1023 and less than 65536\n";
        } else {
            return "";
        }
    }

    private String validateTimeToLive() {
        if (timeToLive < -1) {
            return "time to live must be greater than -1 (set to -1 to wait forever)\n";
        } else {
            return "";
        }
    }

    public boolean isHelpSet() {
        return helpSet;
    }

    public void setHelp(final boolean helpSet) {
        this.helpSet = helpSet;
    }

    public int getRESTPort() {
        return restPort;
    }

    public void setRESTPort(final int restPort) {
        this.restPort = restPort;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(final long timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append(this.getClass().getSimpleName()).append(": URI=").append(this.getBaseURI()).append(", TTL=")
                .append(this.getTimeToLive()).append("sec");
        return ret.toString();
    }
}
