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

package org.onap.policy.apex.core.infrastructure.messaging;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class MessageHolder holds a set of messages to be sent as a single block of messages in this messaging
 * implementation.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @param <M> the generic type of message being handled by a message holder instance
 */
@Getter
@ToString
@EqualsAndHashCode
public class MessageHolder<M> implements Serializable {

    // Serial ID
    private static final long serialVersionUID = 1235487535388793719L;

    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(MessageHolder.class);

    // Properties of the message holder
    private final long creationTime;
    private final InetAddress senderHostAddress;

    // Sequence of message in the message holder
    @ToString.Exclude
    private final List<M> messages;

    /**
     * Constructor, create the message holder.
     *
     * @param senderHostAddress the host address of the sender of the message holder container
     */
    public MessageHolder(final InetAddress senderHostAddress) {
        LOGGER.entry(senderHostAddress);
        messages = new ArrayList<>();
        this.senderHostAddress = senderHostAddress;
        creationTime = System.currentTimeMillis();
    }

    /**
     * Adds a message to this message holder.
     *
     * @param message the message to add
     */
    public void addMessage(final M message) {
        if (!messages.contains(message)) {
            messages.add(message);
        } else {
            LOGGER.warn("duplicate message {} added to message holder", message);
        }
    }
}
