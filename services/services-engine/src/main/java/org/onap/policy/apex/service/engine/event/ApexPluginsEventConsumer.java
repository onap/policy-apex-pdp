/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2020 Nordix Foundation.
 * Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public abstract class ApexPluginsEventConsumer implements ApexEventConsumer, Runnable {
    // The name for this consumer
    @Getter
    protected String name = null;

    // The peer references for this event handler
    protected Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap =
            new EnumMap<>(EventHandlerPeeredMode.class);

    // The consumer thread and stopping flag
    protected Thread consumerThread;
    protected boolean stopOrderedFlag = false;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void start() {
        // Configure and start the event reception thread
        final String threadName = this.getClass().getName() + ":" + this.name;
        consumerThread = new ApplicationThreadFactory(threadName).newThread(this);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

}
