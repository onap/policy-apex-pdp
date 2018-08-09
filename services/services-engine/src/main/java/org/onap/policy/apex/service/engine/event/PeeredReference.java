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

package org.onap.policy.apex.service.engine.event;

import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * This class holds a reference to an event consumer and producer that have been peered.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class PeeredReference {
    // The consumer putting events into APEX
    private final ApexEventConsumer peeredConsumer;

    // The synchronous producer taking events out of APEX
    private final ApexEventProducer peeredProducer;

    /**
     * Create a peered consumer/producer reference.
     * 
     * @param peeredMode the peered mode for which to return the reference
     * @param consumer the consumer that is receiving event
     * @param producer the producer that is sending events
     */
    public PeeredReference(final EventHandlerPeeredMode peeredMode, final ApexEventConsumer consumer,
            final ApexEventProducer producer) {
        this.peeredConsumer = consumer;
        this.peeredProducer = producer;

        // Set the peered reference on the producer and consumer
        peeredConsumer.setPeeredReference(peeredMode, this);
        peeredProducer.setPeeredReference(peeredMode, this);
    }

    /**
     * Gets the synchronous consumer putting events into the cache.
     *
     * @return the source synchronous consumer
     */
    public ApexEventConsumer getPeeredConsumer() {
        return peeredConsumer;
    }

    /**
     * Gets the synchronous producer taking events from the cache.
     *
     * @return the synchronous producer that is taking events from the cache
     */
    public ApexEventProducer getPeeredProducer() {
        return peeredProducer;
    }
}
