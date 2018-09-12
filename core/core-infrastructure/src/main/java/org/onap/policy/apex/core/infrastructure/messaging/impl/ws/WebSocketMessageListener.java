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

package org.onap.policy.apex.core.infrastructure.messaging.impl.ws;

import org.onap.policy.apex.core.infrastructure.messaging.MessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.RawMessageBlock;

/**
 * The listener interface for receiving webSocketMessage events. The class that is interested in processing a
 * webSocketMessage event implements this interface, and the object created with that class is registered with a
 * component using the component's addWebSocketMessageListener method. When the webSocketMessage event occurs, that
 * object's appropriate method is invoked.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @param <M> the generic type
 * @see RawMessageBlock
 */
public interface WebSocketMessageListener<M> extends MessageListener<M>, Runnable {

    /**
     * This method is called by the class with which this message listener has been registered.
     *
     * @param incomingData the data forwarded by the message reception class
     */
    void onMessage(RawMessageBlock incomingData);

    /**
     * Register a data forwarder to which messages coming in on the web socket will be forwarded.
     *
     * @param listener The listener to register
     */
    void registerDataForwarder(MessageListener<M> listener);

    /**
     * Unregister a data forwarder that was previously registered on the web socket listener.
     *
     * @param listener The listener to unregister
     */
    void unRegisterDataForwarder(MessageListener<M> listener);
}
