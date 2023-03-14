/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019,2023 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.restserver;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.models.base.Validated;

/**
 * Apex parameters for REST as an event carrier technology with Apex as a REST client.
 *
 * <p>The parameters for this plugin are:
 * <ol>
 * <li>standalone: A flag indicating if APEX should start a standalone HTTP server to process REST requests (true) or
 * whether it should use an underlying servlet infrastructure such as Apache Tomcat (False). This parameter is legal
 * only on REST server event inputs.
 * <li>host: The host name to use when setting up a standalone HTTP server. This parameter is legal only on REST server
 * event inputs in standalone mode.
 * <li>port: The port to use when setting up a standalone HTTP server. This parameter is legal only on REST server event
 * inputs in standalone mode.
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
public class RestServerCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // @formatter:off
    private static final int MIN_USER_PORT =  1024;
    private static final int MAX_USER_PORT = 65535;

    /** The label of this carrier technology. */
    public static final String RESTSERVER_CARRIER_TECHNOLOGY_LABEL = "RESTSERVER";

    /** The producer plugin class for the REST carrier technology. */
    public static final String RESTSERVER_EVENT_PRODUCER_PLUGIN_CLASS = ApexRestServerProducer.class.getName();

    /** The consumer plugin class for the REST carrier technology. */
    public static final String RESTSERVER_EVENT_CONSUMER_PLUGIN_CLASS = ApexRestServerConsumer.class.getName();

    // REST server parameters
    @Setter(AccessLevel.PACKAGE)
    private boolean standalone = false;
    @Setter(AccessLevel.PACKAGE)
    private String  host       = null;
    @Setter(AccessLevel.PACKAGE)
    private int     port       = -1;
    private String userName;
    private String password;
    private boolean https;
    private boolean sniHostCheck;
    private boolean aaf;
    // @formatter:on

    /**
     * Constructor to create a REST carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public RestServerCarrierTechnologyParameters() {
        super();

        // Set the carrier technology properties for the web socket carrier technology
        this.setLabel(RESTSERVER_CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(RESTSERVER_EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(RESTSERVER_EVENT_CONSUMER_PLUGIN_CLASS);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public BeanValidationResult validate() {
        final BeanValidationResult result = super.validate();

        // Check if host is defined, it is only defined on REST server consumers
        if (standalone) {
            if (StringUtils.isBlank(host)) {
                result.addResult("host", host, ValidationStatus.INVALID, Validated.IS_BLANK);
            }

            // Check if port is defined, it is only defined on REST server consumers
            if (port != -1 && port < MIN_USER_PORT || port > MAX_USER_PORT) {
                result.addResult("port", port, ValidationStatus.INVALID, "must be between 1024 and 65535");
            }
        } else {
            if (host != null) {
                result.addResult("host", host, ValidationStatus.INVALID, "should be specified only in standalone mode");
            }
            if (port != -1) {
                result.addResult("port", port, ValidationStatus.INVALID, "should be specified only in standalone mode");
            }
        }

        return result;
    }
}
