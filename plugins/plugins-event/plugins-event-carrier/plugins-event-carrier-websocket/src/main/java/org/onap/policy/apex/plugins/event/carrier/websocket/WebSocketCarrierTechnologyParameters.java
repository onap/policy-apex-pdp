/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.parameters.annotations.Max;
import org.onap.policy.common.parameters.annotations.Min;
import org.onap.policy.models.base.Validated;

/**
 * Apex parameters for Kafka as an event carrier technology.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class WebSocketCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // @formatter:off
    private static final int MIN_USER_PORT =  1024;
    private static final int MAX_USER_PORT = 65535;

    /** The label of this carrier technology. */
    public static final String WEB_SCOKET_CARRIER_TECHNOLOGY_LABEL = "WEBSOCKET";

    /** The producer plugin class for the web socket carrier technology. */
    public static final String WEB_SCOKET_EVENT_PRODUCER_PLUGIN_CLASS = ApexWebSocketProducer.class.getName();

    /** The consumer plugin class for the web socket carrier technology. */
    public static final String KWEB_SCOKET_EVENT_CONSUMER_PLUGIN_CLASS = ApexWebSocketConsumer.class.getName();

    // Default parameter values
    private static final String DEFAULT_HOST = "localhost";
    private static final int    DEFAULT_PORT = -1;

    // Web socket parameters
    private boolean wsClient = true;
    private String  host     = DEFAULT_HOST;
    @Min(MIN_USER_PORT)
    @Max(MAX_USER_PORT)
    private int     port     = DEFAULT_PORT;
    // @formatter:on

    /**
     * Constructor to create a web socket carrier technology parameters instance and register the instance with the
     * parameter service.
     */
    public WebSocketCarrierTechnologyParameters() {
        super();

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

    /**
     * {@inheritDoc}.
     */
    @Override
    public BeanValidationResult validate() {
        final BeanValidationResult result = super.validate();

        if (wsClient && StringUtils.isBlank(host)) {
            result.addResult("host", host, ValidationStatus.INVALID, Validated.IS_BLANK);
        }

        return result;
    }
}
