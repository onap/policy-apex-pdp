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

import com.google.common.eventbus.Subscribe;

import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.MessageBlock;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The listener interface for receiving testMessage events. The class that is interested in processing a testMessage
 * event implements this interface, and the object created with that class is registered with a component using the
 * component's <code>addTestMessageListener</code> method. When the testMessage event occurs, that object's appropriate
 * method is invoked.
 *
 */
public abstract class TestMessageListener implements MessageListener<String> {

    /** The Constant logger. */
    private static final XLogger logger = XLoggerFactory.getXLogger(TestMessageListener.class);

    /**
     * On command.
     *
     * @param data the data
     */
    public abstract void onCommand(MessageBlock<String> data);

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.infrastructure.messaging.MessageListener#onMessage(org.onap.policy.apex.core.
     * infrastructure. messaging.impl.ws.data.Data)
     */
    @Subscribe
    @Override
    public final void onMessage(final MessageBlock<String> data) {
        if (data != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("{} command recieved from machine {} ", data.getMessages().size(),
                        data.getConnection().getRemoteSocketAddress().getHostString());
            }
            onCommand(data);
        }
    }
}
