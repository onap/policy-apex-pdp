/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022-2024 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.jms;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsServerRunner implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsServerRunner.class);

    // Embedded JMS server
    private final EmbeddedActiveMQ embedded;

    // Thread to run the JMS server in
    private final Thread jmsServerRunnerThread;

    // Config fields
    private final String serverName;
    private final String serverUri;

    /**
     * Create the JMS Server.
     *
     * @param serverName Name of the server
     * @param serverUri  URI for the server
     * @throws Exception on errors
     */
    public JmsServerRunner(String serverName, String serverUri) throws Exception {
        this.serverName = serverName;
        this.serverUri = serverUri;

        ConfigurationImpl config = new ConfigurationImpl();

        config.addAcceptorConfiguration(serverName, serverUri);
        config.setSecurityEnabled(false);
        config.setJournalDirectory("target/artemisActiveMq/data/journal");
        config.setBindingsDirectory("target/artemisActiveMq/data/bindings");
        config.setLargeMessagesDirectory("target/artemisActiveMq/data/largemessages");
        config.setPagingDirectory("target/artemisActiveMq/data/paging");

        embedded = new EmbeddedActiveMQ();
        embedded.setConfiguration(config);

        LOGGER.debug("starting JMS Server {} on URI {} . . .", serverName, serverUri);

        jmsServerRunnerThread = new Thread(this);
        jmsServerRunnerThread.start();

        LOGGER.debug("requested start on JMS Server {} on URI {}", serverName, serverUri);
    }

    @Override
    public void run() {
        try {
            LOGGER.debug("starting JMS Server thread {} on URI {} . . .", serverName, serverUri);
            embedded.start();

            await().atMost(30, TimeUnit.SECONDS).until(() -> embedded.getActiveMQServer().isActive());

            LOGGER.debug("started JMS Server thread {} on URI {} ", serverName, serverUri);
        } catch (Exception e) {
            LOGGER.warn("failed to start JMS Server thread {} on URI {}. {}", serverName, serverUri, e.getMessage());
        }
    }

    /**
     * Stop the JMS server.
     *
     * @throws Exception on stop errors
     */
    public void stop() throws Exception {
        LOGGER.debug("stopping JMS Server {} on URI {} . . .", serverName, serverUri);

        if (!embedded.getActiveMQServer().isActive()) {
            LOGGER.debug("JMS Server {} already stopped on URI {} . . .", serverName, serverUri);
            return;
        }

        embedded.stop();

        LOGGER.debug("waiting on JMS Server {} to stop on URI {} . . .", serverName, serverUri);

        await().atMost(30, TimeUnit.SECONDS)
            .until(() -> !embedded.getActiveMQServer().isActive() && !jmsServerRunnerThread.isAlive());

        LOGGER.debug("stopping JMS Server {} on URI {} . . .", serverName, serverUri);
    }
}