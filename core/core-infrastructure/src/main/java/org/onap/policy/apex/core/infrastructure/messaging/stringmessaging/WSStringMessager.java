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

package org.onap.policy.apex.core.infrastructure.messaging.stringmessaging;

import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;

/**
 * This interface is used to call a String Web socket message server or client to send a string.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public interface WSStringMessager {

    /**
     * Start the string message sender.
     *
     * @param wsStringMessageListener the listener to use for listening for string messages
     * @throws MessagingException the messaging exception
     */
    void start(WSStringMessageListener wsStringMessageListener) throws MessagingException;

    /**
     * Stop the string messaging sender.
     */
    void stop();

    /**
     * Send a string on a web socket.
     *
     * @param stringMessage the string message to send
     */
    void sendString(String stringMessage);
}
