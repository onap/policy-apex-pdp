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

package org.onap.policy.apex.plugins.event.carrier.cds;

import lombok.Getter;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;

// @formatter:off
/**
 * Apex parameters for CDS as an event carrier technology.
 *
 * <p>The parameters for this plugin are:
 * <ol>
 * <li>host: The host on which CDS is running. This parameter is mandatory and defaults to "localhost".
 * <li>port: The port on the CDS host to connect to for CDS. This parameter is mandatory and defaults to 9111.
 * <li>basicAuth: The basic authentication credentials to use to connect to CDS. This parameter is mandatory.
 * <li>timeout: The timeout in seconds for CDS requests. This parameter is mandatory and defaults to 60 seconds.
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@est.tech)
 */
//@formatter:on
@Getter
@ToString
public class CdsCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // @formatter:off
    /** The label of this carrier technology. */
    public static final String CDS_CARRIER_TECHNOLOGY_LABEL = "CDS";

    /** The producer plugin class for the CDS carrier technology. */
    public static final String CDS_EVENT_PRODUCER_PLUGIN_CLASS = ApexCdsProducer.class.getName();

    /** The consumer plugin class for the CDS carrier technology. */
    public static final String CDS_EVENT_CONSUMER_PLUGIN_CLASS = ApexCdsConsumer.class.getName();

    // Port range constants
    private static final int MIN_USER_PORT =  1024;
    private static final int MAX_USER_PORT = 65535;

    // Default parameter values
    private static final String DEFAULT_CDS_HOST       = "localhost";
    private static final int    DEFAULT_CDS_PORT       = 9111;
    private static final String DEFAULT_CDS_BASIC_AUTH = null;
    private static final int    DEFAULT_CDS_TIMEOUT    = 60;

    // CDS carrier parameters
    private String host      = DEFAULT_CDS_HOST;
    private int    port      = DEFAULT_CDS_PORT;
    private String basicAuth = DEFAULT_CDS_BASIC_AUTH;
    private int    timeout   = DEFAULT_CDS_TIMEOUT;
    // @formatter:on

    /**
     * Constructor to create a CDS carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public CdsCarrierTechnologyParameters() {
        super();

        // Set the carrier technology properties for the CDS carrier technology
        this.setLabel(CDS_CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(CDS_EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(CDS_EVENT_CONSUMER_PLUGIN_CLASS);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.parameters.ApexParameterValidator#validate()
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = super.validate();

        if (host != null && host.trim().length() == 0) {
            result.setResult("host", ValidationStatus.INVALID, "must be specified as a string");
        }

        // Check if port is defined
        if (port < MIN_USER_PORT || port > MAX_USER_PORT) {
            result.setResult("port", ValidationStatus.INVALID, "must be specified as 1024 <= port <= 65535");
        }

        // Check for basic authentication
        if (StringUtils.isBlank(basicAuth)) {
            result.setResult("basicAuth", ValidationStatus.INVALID, "must be specified as a string");
        }

        // Check if timeout is defined
        if (timeout <= 0) {
            result.setResult("timeout", ValidationStatus.INVALID, "must be specified as an integer greater than zero");
        }

        return result;
    }
}
