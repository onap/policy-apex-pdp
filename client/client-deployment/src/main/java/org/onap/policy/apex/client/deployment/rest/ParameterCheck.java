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

import java.util.Map;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ParameterCheck is used to check parameters passed to the servlet.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class ParameterCheck {
    private static final int MAX_PORT = 65535;

    /**
     * private constructor to prevent subclassing of this utility class.
     */
    private ParameterCheck() {}

    /**
     * The Enum StartStop is used to hold.
     *
     * @author Liam Fallon (liam.fallon@ericsson.com)
     */
    public enum StartStop {
        /** Start of an Apex engine has been ordered. */
        START,
        /** Stop of an Apex engine has been ordered. */
        STOP
    }

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ParameterCheck.class);

    private static final String HOSTNAME_PAR = "hostname";
    private static final String PORT_PAR = "port";
    private static final String AXARTIFACTKEY_PAR = "AxArtifactKey";

    /**
     * Gets the host name.
     *
     * @param parameterMap the parameter map
     * @return the host name
     */
    public static String getHostName(final Map<String, String[]> parameterMap) {
        if (!parameterMap.containsKey(HOSTNAME_PAR)) {
            LOGGER.warn("parameter \"" + HOSTNAME_PAR + "\" not found");
            return null;
        }

        final String[] hostNameValue = parameterMap.get(HOSTNAME_PAR);

        if (hostNameValue.length == 0 || hostNameValue[0].trim().length() == 0) {
            LOGGER.warn("value of parameter \"" + HOSTNAME_PAR + "\" not found");
            return null;
        }

        return hostNameValue[0];
    }

    /**
     * Gets the port.
     *
     * @param parameterMap the parameter map
     * @return the port
     */
    public static int getPort(final Map<String, String[]> parameterMap) {
        if (!parameterMap.containsKey(PORT_PAR)) {
            LOGGER.warn("parameter \"" + PORT_PAR + "\" not found");
            return -1;
        }

        final String[] portValue = parameterMap.get(PORT_PAR);

        if (portValue.length == 0 || portValue[0].trim().length() == 0) {
            LOGGER.warn("value of parameter \"" + PORT_PAR + "\" not found");
            return -1;
        }

        int port = -1;
        try {
            port = Integer.parseInt(portValue[0]);
        } catch (final Exception e) {
            LOGGER.warn("value \"" + portValue[0] + "\"of parameter \"" + PORT_PAR + "\" not a valid integer", e);
            return -1;
        }

        if (port <= 0 || port > MAX_PORT) {
            LOGGER.warn("value \"" + portValue[0] + "\"of parameter \"" + PORT_PAR
                    + "\" not a valid port between 0 and 65535");
            return -1;
        }

        return port;
    }

    /**
     * Gets the engine key.
     *
     * @param parameterMap the parameter map
     * @return the engine key
     */
    public static AxArtifactKey getEngineKey(final Map<String, String[]> parameterMap) {
        String artifactKeyParameter = null;
        for (final String parameter : parameterMap.keySet()) {
            // Check for an AxArtifactKey parameter
            if (parameter.startsWith(AXARTIFACTKEY_PAR)) {
                artifactKeyParameter = parameter;
                break;
            }
        }
        if (artifactKeyParameter == null) {
            LOGGER.warn("parameter \"" + AXARTIFACTKEY_PAR + "\" not found");
            return null;
        }

        final String[] axArtifactKeyArray = artifactKeyParameter.split("#");

        if (axArtifactKeyArray.length != 2) {
            LOGGER.warn("value \"" + artifactKeyParameter + "\" of parameter \"" + AXARTIFACTKEY_PAR + "\" not valid");
            return null;
        }

        return new AxArtifactKey(axArtifactKeyArray[1]);
    }

    /**
     * Gets the start stop.
     *
     * @param parameterMap the parameter map
     * @param engineKey the engine key
     * @return the start stop
     */
    public static ParameterCheck.StartStop getStartStop(final Map<String, String[]> parameterMap,
            final AxArtifactKey engineKey) {
        final String startStopPar = AXARTIFACTKEY_PAR + '#' + engineKey.getID();
        if (!parameterMap.containsKey(startStopPar)) {
            LOGGER.warn("parameter \"" + startStopPar + "\" not found");
            return null;
        }

        final String[] startStopValue = parameterMap.get(startStopPar);

        if (startStopValue.length == 0 || startStopValue[0].trim().length() == 0) {
            LOGGER.warn("value of parameter \"" + startStopPar + "\" not found");
            return null;
        }

        ParameterCheck.StartStop startStop;
        if (startStopValue[0].equalsIgnoreCase("start")) {
            startStop = ParameterCheck.StartStop.START;
        } else if (startStopValue[0].equalsIgnoreCase("stop")) {
            startStop = ParameterCheck.StartStop.STOP;
        } else {
            LOGGER.warn("value \"" + startStopValue[0] + "\"of parameter \"" + startStopPar
                    + "\" not \"start\" or \"stop\"");
            return null;
        }

        return startStop;
    }

    /**
     * Find and return a long value with the given name.
     *
     * @param parameterMap The parameter map containing the value
     * @param longName The name of the long parameter
     * @return The long value
     */
    public static long getLong(final Map<String, String[]> parameterMap, final String longName) {
        if (!parameterMap.containsKey(longName)) {
            LOGGER.warn("parameter \"" + longName + "\" not found");
            return -1;
        }

        final String[] longValue = parameterMap.get(longName);

        if (longValue.length == 0 || longValue[0].trim().length() == 0) {
            LOGGER.warn("value of parameter \"" + longName + "\" not found");
            return -1;
        }

        try {
            return Long.parseLong(longValue[0]);
        } catch (final Exception e) {
            LOGGER.warn("value \"" + longValue[0] + "\"of parameter \"" + longName + "\" not a valid long", e);
            return -1;
        }
    }
}
