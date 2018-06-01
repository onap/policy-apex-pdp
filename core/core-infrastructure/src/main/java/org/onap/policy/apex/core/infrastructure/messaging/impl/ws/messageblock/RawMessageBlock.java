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

import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;

/**
 * A container for a raw message block and the connection on which it is handled.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 */
public final class RawMessageBlock {
    // The raw message
    private final ByteBuffer message;

    // The web socket on which the message is handled
    private final WebSocket webSocket;

    /**
     * Constructor, instantiate the bean.
     *
     * @param message {@link ByteBuffer} message from the web socket
     * @param webSocket {@link WebSocket} the web socket on which the message is handled
     */
    public RawMessageBlock(final ByteBuffer message, final WebSocket webSocket) {
        this.message = message;
        this.webSocket = webSocket;
    }

    /**
     * A getter method for message.
     *
     * @return the message
     */
    public ByteBuffer getMessage() {
        return message;
    }

    /**
     * A getter method for the web socket.
     *
     * @return the web socket
     */
    public WebSocket getConn() {
        return webSocket;
    }
}
