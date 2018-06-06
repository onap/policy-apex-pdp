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
 * This interface is used by technology specific consumers and listeners that are are listening for
 * or collecting events for input into Apex. Users specify the consumer technology to use in the
 * Apex configuration and Apex uses a factory to start the appropriate consumer plugin that
 * implements this interface for its input. The technology specific implementation details are
 * hidden behind this interface.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface ApexEventConsumer {
    /**
     * Initialize the consumer.
     *
     * @param name a name for this consumer
     * @param consumerParameters the parameters to initialize this consumer
     * @param apexEventReceiver the apex event receiver that should be used to pass events received
     *        by the consumer into Apex
     * @throws ApexEventException container exception on errors initializing event handling
     */
    void init(String name, EventHandlerParameters consumerParameters, ApexEventReceiver apexEventReceiver)
            throws ApexEventException;

    /**
     * Start the consumer, start input of events into Apex.
     */
    void start();

    /**
     * Get the peered reference object for this consumer.
     * 
     * @param peeredMode the peered mode for which to return the reference
     * @return the peered reference object for this consumer
     */
    PeeredReference getPeeredReference(EventHandlerPeeredMode peeredMode);

    /**
     * Set the peered reference object for this consumer.
     * 
     * @param peeredMode the peered mode for which to return the reference
     * @param peeredReference the peered reference object for this consumer
     */
    void setPeeredReference(EventHandlerPeeredMode peeredMode, PeeredReference peeredReference);

    /**
     * Get the name of this event consumer.
     * 
     * @return the event consumer name
     */
    String getName();

    /**
     * Stop the event consumer.
     */
    void stop();
}
