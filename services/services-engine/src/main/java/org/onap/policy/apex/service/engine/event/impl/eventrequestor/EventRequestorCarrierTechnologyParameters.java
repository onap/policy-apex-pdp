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

package org.onap.policy.apex.service.engine.event.impl.eventrequestor;

import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.GroupValidationResult;

/**
 * This class holds the parameters that allows an output event to to be sent back into APEX as one
 * or multiple input events, there are no user defined parameters.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EventRequestorCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // @formatter:off
    /** The label of this carrier technology. */
    public static final String EVENT_REQUESTOR_CARRIER_TECHNOLOGY_LABEL = "EVENT_REQUESTOR";

    /** The producer plugin class for the EVENT_REQUESTOR carrier technology. */
    public static final String EVENT_REQUESTOR_EVENT_PRODUCER_PLUGIN_CLASS =
            EventRequestorProducer.class.getCanonicalName();

    /** The consumer plugin class for the EVENT_REQUESTOR carrier technology. */
    public static final String EVENT_REQUESTOR_EVENT_CONSUMER_PLUGIN_CLASS =
            EventRequestorConsumer.class.getCanonicalName();
    // @formatter:on

    /**
     * Constructor to create an event requestor carrier technology parameters instance and register
     * the instance with the parameter service.
     */
    public EventRequestorCarrierTechnologyParameters() {
        super();

        // Set the carrier technology properties for the EVENT_REQUESTOR carrier technology
        this.setLabel(EVENT_REQUESTOR_CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(EVENT_REQUESTOR_EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(EVENT_REQUESTOR_EVENT_CONSUMER_PLUGIN_CLASS);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.parameters.ApexParameterValidator#validate()
     */
    @Override
    public GroupValidationResult validate() {
        return new GroupValidationResult(this);
    }

    /* (non-Javadoc)
     * @see org.onap.policy.common.parameters.ParameterGroup#getName()
     */
    @Override
    public String getName() {
        return this.getLabel();
    }
}
