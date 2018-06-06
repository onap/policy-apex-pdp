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

import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

/**
 * This interface is used by technology specific producers and publishers that are handling events
 * output by Apex. Users specify the producer technology to use in the Apex configuration and Apex
 * uses a factory to start the appropriate producer plugin that implements this interface for its
 * output. The technology specific implementation details are hidden behind this interface.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface ApexEventProducer {

    /**
     * Initialize the producer.
     *
     * @param name a name for this producer
     * @param producerParameters the parameters to initialise this producer
     * @throws ApexEventException exception on errors initializing an event producer
     */
    void init(String name, EventHandlerParameters producerParameters) throws ApexEventException;

    /**
     * Get the peered reference object for this producer.
     * 
     * @param peeredMode the peered mode for which to return the reference
     * @return the peered reference object for this producer
     */
    PeeredReference getPeeredReference(EventHandlerPeeredMode peeredMode);

    /**
     * Set the peered reference object for this producer.
     * 
     * @param peeredMode the peered mode for which to return the reference
     * @param peeredReference the peered reference object for this producer
     */
    void setPeeredReference(EventHandlerPeeredMode peeredMode, PeeredReference peeredReference);

    /**
     * Send an event to the producer.
     *
     * @param executionId the unique ID that produced this event
     * @param eventName The name of the event
     * @param event The converted event as an object
     */
    void sendEvent(long executionId, String eventName, Object event);

    /**
     * Get the name of this event producer.
     * 
     * @return the event producer name
     */
    String getName();

    /**
     * Stop the event producer.
     */
    void stop();
}
