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

package org.onap.policy.apex.service.engine.runtime;

import org.onap.policy.apex.service.engine.event.ApexEvent;

/**
 * The run time interface for APEX engine users. APEX engine implementations expose this interface
 * and external users use it to send events to the engine.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com), John Keeney
 *         (john.keeney@ericsson.com)
 */
@FunctionalInterface
public interface EngineServiceEventInterface {
    /**
     * This method forwards an event to the APEX engine.
     *
     * @param event is an instance {@link ApexEvent}
     */
    void sendEvent(ApexEvent event);
}
