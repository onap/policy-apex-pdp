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

package org.onap.policy.apex.core.protocols;

import java.io.Serializable;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * The Class Message is used to pass protocol messages between Apex components.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 */
public abstract class Message implements Serializable {
    private static final int HASH_PRIME = 31;

    // Serialization ID
    private static final long serialVersionUID = 2271443377544488309L;

    // Default timeout on server side should be used
    private static final int DEFAULT_REPLY_TIMEOUT = -1;

    // The Action or message type of the message
    private Action action = null;

    // The artifact key of the artifact to which this message is related
    private AxArtifactKey targetKey = null;

    // The data of the message
    private String messageData = null;

    // The timeout time for replies in milliseconds
    private int replyTimeout = DEFAULT_REPLY_TIMEOUT;

    /**
     * Instantiates a new message.
     *
     * @param action the action or message type of the message
     * @param targetKey the artifact key of the artifact to which this message relates
     */
    public Message(final Action action, final AxArtifactKey targetKey) {
        this(action, targetKey, null);
    }

    /**
     * Instantiates a new message.
     *
     * @param action the action or message type of the message
     * @param targetKey the artifact key of the artifact to which this message relates
     * @param messageData the message data to deliver
     */
    public Message(final Action action, final AxArtifactKey targetKey, final String messageData) {
        this.action = action;
        this.targetKey = targetKey;
        this.messageData = messageData;
    }

    /**
     * Set the message timeout.
     *
     * @param replyTimeout the timeout on reply messages in milliseconds
     */
    public void setReplyTimeout(final int replyTimeout) {
        this.replyTimeout = replyTimeout;
    }

    /**
     * Sets the message data.
     *
     * @param messageData the new message data
     */
    public void setMessageData(final String messageData) {
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

    /**
     * Gets the action or message type of this message.
     *
     * @return the action
     */
    public final Action getAction() {
        return action;
    }

    /**
     * Gets the message data.
     *
     * @return the message data
     */
    public final String getMessageData() {
        return messageData;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final Message message = (Message) object;

        if (action != null ? !action.equals(message.action) : message.action != null) {
            return false;
        }
        if (targetKey != null ? !targetKey.equals(message.targetKey) : message.targetKey != null) {
            return false;
        }
        return !(messageData != null ? !messageData.equals(message.messageData) : message.messageData != null);

    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = HASH_PRIME * result + (targetKey != null ? targetKey.hashCode() : 0);
        result = HASH_PRIME * result + (messageData != null ? messageData.hashCode() : 0);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Message [action=" + action + ", targetKey=" + targetKey + ", data=" + messageData + "]";
    }

    /**
     * Get the timeout to wait for a reply.
     *
     * @return the timeout in milliseconds
     */
    public int getReplyTimeout() {
        return replyTimeout;
    }
}
