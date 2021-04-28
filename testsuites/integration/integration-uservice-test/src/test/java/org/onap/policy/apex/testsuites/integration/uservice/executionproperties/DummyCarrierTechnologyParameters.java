/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.uservice.executionproperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * Dummy carrier technology parameters.
 *
 * <p>The parameters for this plugin are:
 * <ol>
 * <li>testToRun: The name of the test to run.
 * </ol>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DummyCarrierTechnologyParameters extends CarrierTechnologyParameters {

    /** The label of this carrier technology. */
    public static final String DUMMY_CARRIER_TECHNOLOGY_LABEL = "DUMMY";

    /** The producer plugin class for the dummy carrier technology. */
    public static final String DUMMY_EVENT_PRODUCER_PLUGIN_CLASS = DummyApexEventProducer.class.getName();

    /** The consumer plugin class for the dummy carrier technology. */
    public static final String DUMMY_EVENT_CONSUMER_PLUGIN_CLASS = DummyApexEventConsumer.class.getName();

    private @NotNull @NotBlank String testToRun = null;
    private @NotNull @NotBlank String propertyFileName = null;

    /**
     * Constructor to create a dummy carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public DummyCarrierTechnologyParameters() {
        super();

        // Set the carrier technology properties for the web socket carrier technology
        this.setLabel(DUMMY_CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(DUMMY_EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(DUMMY_EVENT_CONSUMER_PLUGIN_CLASS);

    }
}
