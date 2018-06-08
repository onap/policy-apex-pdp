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

package org.onap.policy.apex.plugins.event.carrier.websocket;

import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;

/**
 * Apex parameters for Kafka as an event carrier technology.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class WEBSOCKETCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // @formatter:off
    private static final int MIN_USER_PORT =  1024;
    private static final int MAX_USER_PORT = 65535;

    /** The label of this carrier technology. */
    public static final String WEB_SCOKET_CARRIER_TECHNOLOGY_LABEL = "WEBSOCKET";

    /** The producer plugin class for the web socket carrier technology. */
    public static final String WEB_SCOKET_EVENT_PRODUCER_PLUGIN_CLASS = ApexWebSocketProducer.class.getCanonicalName();

    /** The consumer plugin class for the web socket carrier technology. */
    public static final String KWEB_SCOKET_EVENT_CONSUMER_PLUGIN_CLASS = ApexWebSocketConsumer.class.getCanonicalName();

    // Default parameter values
    private static final String DEFAULT_HOST = "localhost";
    private static final int    DEFAULT_PORT = -1;

    // Web socket parameters
    private boolean wsClient = true;
    private String  host     = DEFAULT_HOST;
    private int     port     = DEFAULT_PORT;
    // @formatter:on

    /**
     * Constructor to create a web socket carrier technology parameters instance and register the instance with the
     * parameter service.
     */
    public WEBSOCKETCarrierTechnologyParameters() {
        super(WEBSOCKETCarrierTechnologyParameters.class.getCanonicalName());

        // Set the carrier technology properties for the web socket carrier technology
        this.setLabel(WEB_SCOKET_CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(WEB_SCOKET_EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(KWEB_SCOKET_EVENT_CONSUMER_PLUGIN_CLASS);
    }

    /**
     * Gets the host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Checks if is ws client.
     *
     * @return true, if checks if is ws client
     */
    public boolean isWsClient() {
        return wsClient;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.parameters.ApexParameterValidator#validate()
     */
    @Override
    public String validate() {
        final StringBuilder errorMessageBuilder = new StringBuilder();

        errorMessageBuilder.append(super.validate());

        if (wsClient && (host == null || host.trim().length() == 0)) {
            errorMessageBuilder.append("  host not specified, must be host as a string\n");
        }

        if (port < MIN_USER_PORT || port > MAX_USER_PORT) {
            errorMessageBuilder.append("  port [" + port + "] invalid, must be specified as 1024 <= port <= 6535\n");
        }

        return errorMessageBuilder.toString();
    }
}
