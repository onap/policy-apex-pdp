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

/**
 * This interface is used by an Apex event consumer {@link ApexEventConsumer} consumer to pass a
 * received event to Apex.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface ApexEventReceiver {
    /**
     * Receive an event from a consumer for processing.
     *
     * @param executionId the unique ID for execution of this event
     * @param event the event to receive
     * @throws ApexEventException on exceptions receiving an event into Apex
     */
    void receiveEvent(long executionId, Object event) throws ApexEventException;

    /**
     * Receive an event from a consumer for processing.
     *
     * @param event the event to receive
     * @throws ApexEventException on exceptions receiving an event into Apex
     */
    void receiveEvent(Object event) throws ApexEventException;
}
