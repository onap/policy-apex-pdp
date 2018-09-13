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

import java.util.EnumMap;
import java.util.Map;

import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageClient;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageServer;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessager;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of an Apex event producer that sends events using a web socket.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexWebSocketProducer implements ApexEventProducer, WsStringMessageListener {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexWebSocketProducer.class);

    // The web socket messager, may be WS a server or a client
    private WsStringMessager wsStringMessager;

    // The name for this producer
    private String name = null;

    // The peer references for this event handler
    private Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap = new EnumMap<>(EventHandlerPeeredMode.class);

    @Override
    public void init(final String producerName, final EventHandlerParameters producerParameters)
                    throws ApexEventException {
        this.name = producerName;

        // Check and get the web socket Properties
        if (!(producerParameters.getCarrierTechnologyParameters() instanceof WebSocketCarrierTechnologyParameters)) {
            String message = "specified producer properties for " + this.name
                            + "are not applicable to a web socket producer";
            LOGGER.warn(message);
            throw new ApexEventException("specified producer properties are not applicable to a web socket producer");
        }
        // The Web Socket properties
        WebSocketCarrierTechnologyParameters webSocketProducerProperties =
                        (WebSocketCarrierTechnologyParameters) producerParameters.getCarrierTechnologyParameters();

        // Check if this is a server or a client Web Socket
        if (webSocketProducerProperties.isWsClient()) {
            // Create a WS client
            wsStringMessager = new WsStringMessageClient(webSocketProducerProperties.getHost(),
                            webSocketProducerProperties.getPort());
        } else {
            wsStringMessager = new WsStringMessageServer(webSocketProducerProperties.getPort());
        }

        // Start reception of event strings on the web socket
        try {
            wsStringMessager.start(this);
        } catch (final MessagingException e) {
            String message = "could not start web socket producer (" + this.name + ")";
            LOGGER.warn(message);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#getPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#setPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode, org.onap.policy.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.producer.ApexEventProducer#sendEvent(long, java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void sendEvent(final long executionId, final String eventName, final Object event) {
        // Check if this is a synchronized event, if so we have received a reply
        final SynchronousEventCache synchronousEventCache = (SynchronousEventCache) peerReferenceMap
                        .get(EventHandlerPeeredMode.SYNCHRONOUS);
        if (synchronousEventCache != null) {
            synchronousEventCache.removeCachedEventToApexIfExists(executionId);
        }

        wsStringMessager.sendString((String) event);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.producer.ApexEventProducer#stop()
     */
    @Override
    public void stop() {
        if (wsStringMessager != null) {
            wsStringMessager.stop();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WSStringMessageListener#receiveString(java.
     * lang. String)
     */
    @Override
    public void receiveString(final String messageString) {
        String message = "received message \"" + messageString + "\" on web socket producer (" + this.name
                        + ") , no messages should be received on a web socket producer";
        LOGGER.warn(message);
    }
}
