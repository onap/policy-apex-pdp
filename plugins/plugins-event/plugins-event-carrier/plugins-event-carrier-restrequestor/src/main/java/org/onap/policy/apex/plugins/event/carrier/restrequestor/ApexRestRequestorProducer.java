/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.restrequestor;

import java.util.Properties;
import lombok.Getter;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventProducer;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * Concrete implementation of an Apex event requestor that manages the producer side of a REST request.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 *
 */
public class ApexRestRequestorProducer extends ApexPluginsEventProducer {
    // The number of events sent
    @Getter
    private int eventsSent = 0;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final String producerName, final EventHandlerParameters producerParameters)
        throws ApexEventException {
        this.name = producerName;

        // Check and get the REST Properties
        if (!(producerParameters
            .getCarrierTechnologyParameters() instanceof RestRequestorCarrierTechnologyParameters)) {
            final String errorMessage =
                "specified producer properties are not applicable to REST requestor producer (" + this.name + ")";
            throw new ApexEventException(errorMessage);
        }
        RestRequestorCarrierTechnologyParameters restProducerProperties =
            (RestRequestorCarrierTechnologyParameters) producerParameters.getCarrierTechnologyParameters();

        // Check if we are in peered mode
        if (!producerParameters.isPeeredMode(EventHandlerPeeredMode.REQUESTOR)) {
            final String errorMessage = "REST Requestor producer (" + this.name
                + ") must run in peered requestor mode with a REST Requestor consumer";
            throw new ApexEventException(errorMessage);
        }

        // Check if the HTTP URL has been set
        if (restProducerProperties.getUrl() != null) {
            final String errorMessage = "URL may not be specified on REST Requestor producer (" + this.name + ")";
            throw new ApexEventException(errorMessage);
        }

        // Check if the HTTP method has been set
        if (restProducerProperties.getHttpMethod() != null) {
            final String errorMessage =
                "HTTP method may not be specified on REST Requestor producer (" + this.name + ")";
            throw new ApexEventException(errorMessage);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void sendEvent(final long executionId, final Properties executionProperties, final String eventName,
        final Object event) {
        super.sendEvent(executionId, executionProperties, eventName, event);

        // Find the peered consumer for this producer
        final PeeredReference peeredRequestorReference = peerReferenceMap.get(EventHandlerPeeredMode.REQUESTOR);
        if (peeredRequestorReference != null) {
            // Find the REST Response Consumer that will handle this request
            final ApexEventConsumer consumer = peeredRequestorReference.getPeeredConsumer();
            if (!(consumer instanceof ApexRestRequestorConsumer)) {
                final String errorMessage = "send of event failed,"
                    + " REST response consumer is not an instance of ApexRestRequestorConsumer\n" + event;
                throw new ApexEventRuntimeException(errorMessage);
            }

            // Use the consumer to handle this event
            final ApexRestRequestorConsumer restRequstConsumer = (ApexRestRequestorConsumer) consumer;
            restRequstConsumer
                .processRestRequest(new ApexRestRequest(executionId, executionProperties, eventName, event));

            eventsSent++;
        } else {
            // No peered consumer defined
            final String errorMessage = "send of event failed," + " REST response consumer is not defined\n" + event;
            throw new ApexEventRuntimeException(errorMessage);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop() {
        // For REST requestor, all the implementation is in the consumer
    }
}
