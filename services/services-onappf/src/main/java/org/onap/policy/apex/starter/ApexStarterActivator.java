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

package org.onap.policy.apex.starter;

import java.util.List;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

import org.onap.policy.apex.starter.comm.PdpStateChangeListener;
import org.onap.policy.apex.starter.comm.PdpStatusPublisher;
import org.onap.policy.apex.starter.comm.PdpUpdateListener;
import org.onap.policy.apex.starter.exception.ApexStarterException;
import org.onap.policy.apex.starter.exception.ApexStarterRunTimeException;
import org.onap.policy.apex.starter.handler.PdpMessageHandler;
import org.onap.policy.apex.starter.parameters.ApexStarterParameterGroup;
import org.onap.policy.common.endpoints.event.comm.TopicEndpoint;
import org.onap.policy.common.endpoints.event.comm.TopicSink;
import org.onap.policy.common.endpoints.event.comm.TopicSource;
import org.onap.policy.common.endpoints.listeners.MessageTypeDispatcher;
import org.onap.policy.common.utils.services.Registry;
import org.onap.policy.common.utils.services.ServiceManager;
import org.onap.policy.common.utils.services.ServiceManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class activates the ApexStarter as a complete service together with all its handlers.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexStarterActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApexStarterActivator.class);
    private final ApexStarterParameterGroup apexStarterParameterGroup;
    private List<TopicSink> topicSinks;// topics to which apex-pdp sends pdp status
    private List<TopicSource> topicSources; // topics to which apex-pdp listens to for messages from pap.
    private static final String[] MSG_TYPE_NAMES = { "messageName" };
    /**
     * Listens for messages on the topic, decodes them into a message, and then dispatches them.
     */
    private final MessageTypeDispatcher msgDispatcher;

    /**
     * Used to manage the services.
     */
    private ServiceManager manager;

    @Getter
    @Setter(lombok.AccessLevel.PRIVATE)
    private volatile boolean alive = false;

    public ApexStarterActivator(final ApexStarterParameterGroup apexStarterParameterGroup,
            final Properties topicProperties) {

        topicSinks = TopicEndpoint.manager.addTopicSinks(topicProperties);
        topicSources = TopicEndpoint.manager.addTopicSources(topicProperties);

        // TODO: instanceId currently set as a random string, could be fetched from actual deployment
        final int random = (int) (Math.random() * 100);
        final String instanceId = "apex_" + random;

        try {
            this.apexStarterParameterGroup = apexStarterParameterGroup;
            this.msgDispatcher = new MessageTypeDispatcher(MSG_TYPE_NAMES);
        } catch (final RuntimeException e) {
            throw new ApexStarterRunTimeException(e);
        }

        final PdpUpdateListener pdpUpdateListener = new PdpUpdateListener();
        final PdpStateChangeListener pdpStateChangeListener = new PdpStateChangeListener();
        // @formatter:off
        this.manager = new ServiceManager()
                .addAction("topics",
                        () -> TopicEndpoint.manager.start(),
                        () -> TopicEndpoint.manager.shutdown())
                .addAction("set alive",
                        () -> setAlive(true),
                        () -> setAlive(false))
                .addAction("register pdp status context object",
                        () -> Registry.register(ApexStarterConstants.REG_PDP_STATUS_OBJECT,
                                new PdpMessageHandler().createPdpStatusFromParameters(instanceId,
                                        apexStarterParameterGroup.getPdpStatusParameters())),
                        () -> Registry.unregister(ApexStarterConstants.REG_PDP_STATUS_OBJECT))
                .addAction("topic sinks",
                        () -> Registry.register(ApexStarterConstants.REG_APEX_PDP_TOPIC_SINKS, topicSinks),
                        () -> Registry.unregister(ApexStarterConstants.REG_APEX_PDP_TOPIC_SINKS))
                .addAction("Pdp Status publisher",
                        () -> Registry.register(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER,
                                new PdpStatusPublisher(topicSinks,
                                        apexStarterParameterGroup.getPdpStatusParameters().getTimeIntervalMs())),
                        () -> stopAndRemovePdpStatusPublisher())
                .addAction("Register pdp update listener",
                    () -> msgDispatcher.register("PdpUpdate", pdpUpdateListener),
                    () -> msgDispatcher.unregister("PdpUpdate"))
                .addAction("Register pdp state change request dispatcher",
                        () -> msgDispatcher.register("PdpStateChange", pdpStateChangeListener),
                        () -> msgDispatcher.unregister("PdpStateChange"))
                .addAction("Message Dispatcher",
                    () -> registerMsgDispatcher(),
                    () -> unregisterMsgDispatcher());
        // @formatter:on
    }

    /**
     * Method to stop and unregister the pdp status publisher.
     */
    private void stopAndRemovePdpStatusPublisher() {
        final PdpStatusPublisher pdpStatusPublisher =
                Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER, PdpStatusPublisher.class);
        // send a final heartbeat with terminated status
        pdpStatusPublisher.send(new PdpMessageHandler().getTerminatedPdpStatus());
        pdpStatusPublisher.terminate();
        Registry.unregister(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER);
    }

    /**
     * Initialize ApexStarter service.
     *
     * @throws ApexStarterException on errors in initializing the service
     */
    public void initialize() throws ApexStarterException {
        if (isAlive()) {
            throw new IllegalStateException("activator already initialized");
        }

        try {
            LOGGER.debug("ApexStarter starting as a service . . .");
            manager.start();
            LOGGER.debug("ApexStarter started as a service");
        } catch (final ServiceManagerException exp) {
            LOGGER.error("ApexStarter service startup failed");
            throw new ApexStarterException(exp.getMessage(), exp);
        }
    }

    /**
     * Terminate ApexStarter.
     *
     * @throws ApexStarterException on errors in terminating the service
     */
    public void terminate() throws ApexStarterException {
        if (!isAlive()) {
            throw new IllegalStateException("activator is not running");
        }
        try {
            manager.stop();
            Registry.unregister(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR);
        } catch (final ServiceManagerException exp) {
            LOGGER.error("ApexStarter termination failed");
            throw new ApexStarterException(exp.getMessage(), exp);
        }
    }

    /**
     * Get the parameters used by the activator.
     *
     * @return the parameters of the activator
     */
    public ApexStarterParameterGroup getParameterGroup() {
        return apexStarterParameterGroup;
    }

    /**
     * Registers the dispatcher with the topic source(s).
     */
    private void registerMsgDispatcher() {
        for (final TopicSource source : topicSources) {
            source.register(msgDispatcher);
        }
    }

    /**
     * Unregisters the dispatcher from the topic source(s).
     */
    private void unregisterMsgDispatcher() {
        for (final TopicSource source : topicSources) {
            source.unregister(msgDispatcher);
        }
    }
}
