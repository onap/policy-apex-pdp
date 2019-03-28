/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.starter.handler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lombok.Getter;
import lombok.Setter;

import org.onap.policy.common.endpoints.event.comm.TopicSink;
import org.onap.policy.common.endpoints.event.comm.client.TopicSinkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to send pdp status messages to pap using TopicSinkClient.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class PdpStatusPublisher extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationHandler.class);

    private List<TopicSink> topicSinks;
    private TopicSinkClient topicSinkClient;
    private Timer timer;
    private PdpMessageHandler pdpMessageHandler;

    @Getter
    @Setter(lombok.AccessLevel.PRIVATE)
    private volatile boolean alive = false;

    /**
     * Constructor for instantiating PdpStatusPublisher
     *
     * @param pdpStatus
     * @param topicSinks
     * @param apexStarterParameterGroup
     */
    public PdpStatusPublisher(final List<TopicSink> topicSinks, final int interval) {
        this.topicSinks = topicSinks;
        this.topicSinkClient = new TopicSinkClient(topicSinks.get(0));
        this.pdpMessageHandler = new PdpMessageHandler();
        timer = new Timer(false);
        timer.scheduleAtFixedRate(this, 0, interval * 1000L);
        setAlive(true);
    }

    @Override
    public void run() {
        topicSinkClient.send(pdpMessageHandler.createPdpStatusFromContext());
        LOGGER.info("Sent heartbeat to PAP");
    }

    public void terminate() {
        timer.cancel();
        timer.purge();
        setAlive(false);
    }

    public PdpStatusPublisher updateInterval(final int interval) {
        terminate();
        return new PdpStatusPublisher(topicSinks, interval);
    }

    public void send() {
        topicSinkClient.send(pdpMessageHandler.createPdpStatusFromContext());
        LOGGER.info("Sent pdp status response message to PAP");
    }

}
