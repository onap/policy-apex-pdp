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

package org.onap.policy.apex.service.engine.runtime.impl;

import org.onap.policy.apex.core.engine.engine.EnEventListener;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.impl.enevent.ApexEvent2EnEventConverter;
import org.onap.policy.apex.service.engine.runtime.ApexEventListener;

/**
 * The Class EnEventListenerImpl is used by the Apex engine implementation to listen for events
 * coming from the core APEX engine. This listener converts the {@link EnEvent} instances into
 * {@link ApexEvent} instances using an {@link ApexEvent2EnEventConverter} instance and forwards the
 * events to an {@link ApexEventListener} instance for outputting to listening applications. The
 * {@link ApexEventListener} is implemented in the external application communicating with Apex.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class EnEventListenerImpl implements EnEventListener {
    // Listener for ApexEvents
    private ApexEventListener apexEventListener = null;

    // Converter for Engine events to Apex Events
    private ApexEvent2EnEventConverter apexEnEventConverter = null;

    /**
     * Instantiates a new listener implementation.
     *
     * @param apexEventListener the apex event listener
     * @param apexEnEventConverter the ApexEvent to enEvent converter
     */
    public EnEventListenerImpl(final ApexEventListener apexEventListener,
            final ApexEvent2EnEventConverter apexEnEventConverter) {
        this.apexEventListener = apexEventListener;
        this.apexEnEventConverter = apexEnEventConverter;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.engine.engine.EnEventListener#onEnEvent(org.onap.policy.apex.core.
     * engine.event.EnEvent)
     */
    @Override
    public void onEnEvent(final EnEvent enEvent) throws ApexException {
        for (final ApexEvent apexEvent : apexEnEventConverter.toApexEvent(enEvent.getName(), enEvent)) {
            apexEventListener.onApexEvent(apexEvent);
        }
    }
}
