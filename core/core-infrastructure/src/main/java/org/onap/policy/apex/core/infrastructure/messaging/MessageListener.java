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

package org.onap.policy.apex.core.infrastructure.messaging;

import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.MessageBlock;

/**
 * The listener interface for receiving message events. The class that is interested in processing a message event
 * implements this interface.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @param <M> of message of any given type that is being listened for and handled
 */
public interface MessageListener<M> {

    /**
     * This method is called when a message block is received on a web socket and is to be forwarded to a listener.
     *
     * @param data the message data containing a message
     */
    void onMessage(MessageBlock<M> data);

    /**
     * This method is called when a string message is received on a web socket and is to be forwarded to a listener.
     *
     * @param messageString the message string
     */
    void onMessage(String messageString);
}
