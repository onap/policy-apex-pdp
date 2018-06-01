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
 * The Class UpdateModel is a message that requests an Apex engine to update its model using the data provided in the
 * message.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class UpdateModel extends Message {
    private static final long serialVersionUID = 5885214410842753037L;

    // The reply timeout value for update messages
    private static final int UPDATE_MODEL_REPLY_TIMEOUT = 30000;

    // Flags indicating whether conflicts in context should be ignored and whether the model should be forced even if it
    // is incompatible
    private boolean ignoreConflicts = false;
    private boolean forceInstall = false;

    /**
     * Instantiates a new update model message.
     *
     * @param engineServiceKey the key of the engine service in which the model of all engines will be updated
     */
    public UpdateModel(final AxArtifactKey engineServiceKey) {
        this(engineServiceKey, null, false, false);
    }

    /**
     * Instantiates a new update model message.
     *
     * @param engineServiceKey the key of the engine service in which the model of all engines will be updated
     * @param messageData the message data that indicates to the Apex engine the manner in which its model should be
     *        updated
     * @param ignoreConflicts true if conflicts between context in polices is to be ignored
     * @param force true if the model is to be applied even if it is incompatible with the existing model
     */
    public UpdateModel(final AxArtifactKey engineServiceKey, final String messageData, final boolean ignoreConflicts,
            final boolean force) {
        super(EngDepAction.UPDATE_MODEL, engineServiceKey, messageData);

        this.ignoreConflicts = ignoreConflicts;
        this.forceInstall = force;

        // Update messages have a longer timeout
        setReplyTimeout(UPDATE_MODEL_REPLY_TIMEOUT);
    }

    /**
     * Check if context conflicts should be ignored.
     *
     * @return true if conflicts should be ignored
     */
    public boolean isIgnoreConflicts() {
        return ignoreConflicts;
    }

    /**
     * Check if version checks should be overridden.
     *
     * @return true if version checks should be overridden
     */
    public boolean isForceInstall() {
        return forceInstall;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.model.protocols.Message#toString()
     */
    @Override
    public String toString() {
        return "UpdateModel {" + super.toString() + "}[]";
    }
}
