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

package org.onap.policy.apex.core.protocols.engdep.messages;

import org.onap.policy.apex.core.protocols.Message;
import org.onap.policy.apex.core.protocols.engdep.EngDepAction;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * The Class StartEngine is a message that requests that an Apex engine in an engine service be started.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class StartPeriodicEvents extends Message {
    private static final long serialVersionUID = -9172376034035242135L;

    /**
     * Instantiates a new StartPeriodiEvents message.
     *
     * @param engineKey the key of the engine to start
     */
    public StartPeriodicEvents(final AxArtifactKey engineKey) {
        this(engineKey, null);
    }

    /**
     * Instantiates a new StartEngine message.
     *
     * @param engineKey the key of the engine to start
     * @param messageData the message data that may give specifics on what way to start
     */
    public StartPeriodicEvents(final AxArtifactKey engineKey, final String messageData) {
        super(EngDepAction.START_PERIODIC_EVENTS, engineKey, messageData);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.model.protocols.Message#toString()
     */
    @Override
    public String toString() {
        return "StartPeriodicEvents {" + super.toString() + "}[]";
    }
}
