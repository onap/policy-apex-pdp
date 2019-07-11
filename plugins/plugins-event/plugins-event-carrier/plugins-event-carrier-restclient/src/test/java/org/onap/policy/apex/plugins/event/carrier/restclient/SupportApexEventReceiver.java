/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.restclient;

import java.util.Properties;

import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;

/**
 * Support Apex event reveiver for unit test.
 *
 */
public class SupportApexEventReceiver implements ApexEventReceiver {
    private long lastExecutionId;
    private Object lastEvent;
    private int eventCount;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void receiveEvent(final long executionId, final Properties executionProperties, final Object event)
            throws ApexEventException {
        this.lastExecutionId = executionId;
        this.lastEvent = event;
        this.eventCount++;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void receiveEvent(final Properties executionProperties, final Object event) throws ApexEventException {
        this.lastEvent = event;
        this.eventCount++;
    }

    public long getLastExecutionId() {
        return lastExecutionId;
    }

    public Object getLastEvent() {
        return lastEvent;
    }

    /**
     * Get the number of events received.
     *
     * @return the number of events received
     */
    public int getEventCount() {
        return eventCount;
    }
}
