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

package org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock;

import java.util.List;

import org.java_websocket.WebSocket;

/**
 * This class encapsulate messages and the web socket on which they are handled.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @param <MESSAGE> the generic type of message being handled
 */
public final class MessageBlock<MESSAGE> {

    // List of Messages received on a web socket
    private final List<MESSAGE> messages;

    // The web socket on which the messages are handled
    private final WebSocket webSocket;

    /**
     * Instantiates a new message block.
     *
     * @param messages the messages in the message block
     * @param webSocket the web socket used to handle the message block
     */
    public MessageBlock(final List<MESSAGE> messages, final WebSocket webSocket) {
        this.messages = messages;
        this.webSocket = webSocket;
    }

    /**
     * Gets the messages.
     *
     * @return the messages
     */
    public List<MESSAGE> getMessages() {
        return messages;
    }

    /**
     * Gets the web socket.
     *
     * @return the web socket
     */
    public WebSocket getConnection() {
        return webSocket;
    }

}
