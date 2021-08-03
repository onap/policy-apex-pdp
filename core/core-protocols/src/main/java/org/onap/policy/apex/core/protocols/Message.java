/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.core.protocols;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * The Class Message is used to pass protocol messages between Apex components.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class Message implements Serializable {

    // Serialization ID
    private static final long serialVersionUID = 2271443377544488309L;

    // Default timeout on server side should be used
    private static final int DEFAULT_REPLY_TIMEOUT = -1;

    // The Action or message type of the message
    private Action action = null;

    // The artifact key of the artifact to which this message is related
    @Getter(AccessLevel.NONE)
    private AxArtifactKey targetKey = null;

    // The data of the message
    @Setter
    private String messageData = null;

    // The timeout time for replies in milliseconds
    @Setter
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private int replyTimeout = DEFAULT_REPLY_TIMEOUT;

    /**
     * Instantiates a new message.
     *
     * @param action the action or message type of the message
     * @param targetKey the artifact key of the artifact to which this message relates
     */
    protected Message(final Action action, final AxArtifactKey targetKey) {
        this(action, targetKey, null);
    }

    /**
     * Instantiates a new message.
     *
     * @param action the action or message type of the message
     * @param targetKey the artifact key of the artifact to which this message relates
     * @param messageData the message data to deliver
     */
    protected Message(final Action action, final AxArtifactKey targetKey, final String messageData) {
        this.action = action;
        this.targetKey = targetKey;
        this.messageData = messageData;
    }

    /**
     * Append to the message data.
     *
     * @param newMessageData the message data
     */
    public void appendMessageData(final String newMessageData) {
        if (this.messageData == null) {
            this.messageData = newMessageData;
        } else {
            this.messageData += newMessageData;
        }
    }

    /**
     * Gets the artifact key of the target of the message.
     *
     * @return the target
     */
    public final AxArtifactKey getTarget() {
        return targetKey;
    }

    /**
     * Gets the artifact key name of the target of the message.
     *
     * @return the target name
     */
    public final String getTargetName() {
        return targetKey.getName();
    }
}
