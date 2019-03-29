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

import org.onap.policy.apex.starter.parameters.PdpStatusParameters;
import org.onap.policy.common.capabilities.Startable;
import org.onap.policy.common.endpoints.event.comm.TopicSink;
import org.onap.policy.common.endpoints.event.comm.TopicSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages the communication between apex pdp and pap.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class CommunicationHandler implements Startable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationHandler.class);

    private List<TopicSink> topicSinks; // topics to which apex-pdp sends pdp status
    private List<TopicSource> topicSources; // topics to which apex-pdp listens to for messages from pap.

    private PdpStatusPublisher pdpStatusPublisher;
    private PdpStatusParameters pdpStatusParameters;

    /**
     * Constructor for instantiating CommunicationHandler
     *
     * @param topicSinks topics to which apex-pdp sends pdp status
     * @param topicSources topics to which apex-pdp listens to for messages from pap.
     * @param pdpStatusParameters pdp status parameters read from the configuration file
     */
    public CommunicationHandler(final List<TopicSink> topicSinks, final List<TopicSource> topicSources,
            final PdpStatusParameters pdpStatusParameters) {
        this.topicSinks = topicSinks;
        this.topicSources = topicSources;
        this.pdpStatusParameters = pdpStatusParameters;
    }

    @Override
    public boolean start() {
        try {
            pdpStatusPublisher = new PdpStatusPublisher(topicSinks, pdpStatusParameters.getTimeInterval());
        } catch (final Exception e) {
            LOGGER.error("Failed to start communication handler for apex-pdp", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean stop() {
        try {
            pdpStatusPublisher.terminate();
        } catch (final Exception e) {
            LOGGER.error("Failed to stop communication handler for apex-pdp", e);
            return false;
        }
        return true;
    }

    @Override
    public void shutdown() {
        stop();
    }

    @Override
    public boolean isAlive() {
        return pdpStatusPublisher.isAlive();
    }

}
