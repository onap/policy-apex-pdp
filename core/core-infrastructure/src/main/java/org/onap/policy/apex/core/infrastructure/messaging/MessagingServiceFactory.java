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

import java.net.InetSocketAddress;
import java.net.URI;

import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.client.MessagingClient;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.server.MessageServerImpl;

/**
 * A factory class to create a "server" or "client" type Messaging Service.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @param <MESSAGE> the generic type of message to be handled by this messaging service
 */
public class MessagingServiceFactory<MESSAGE> {

    /**
     * Create a web socket server instance and returns to the caller.
     *
     * @param address the address of the server machine
     * @return the messaging service
     */
    public MessagingService<MESSAGE> createServer(final InetSocketAddress address) {
        return new MessageServerImpl<>(address);
    }

    /**
     * Create a web socket client instance and returns to the caller.
     *
     * @param uri the URI of the server to connect to
     * @return an instance of {@link MessagingService}
     */
    public MessagingService<MESSAGE> createClient(final URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        return new MessagingClient<>(uri);
    }
}
