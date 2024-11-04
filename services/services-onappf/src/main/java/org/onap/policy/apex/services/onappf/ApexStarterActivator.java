/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021, 2023-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2019, 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.services.onappf;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.services.onappf.comm.PdpStateChangeListener;
import org.onap.policy.apex.services.onappf.comm.PdpStatusPublisher;
import org.onap.policy.apex.services.onappf.comm.PdpUpdateListener;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.apex.services.onappf.exception.ApexStarterRunTimeException;
import org.onap.policy.apex.services.onappf.handler.PdpMessageHandler;
import org.onap.policy.apex.services.onappf.parameters.ApexStarterParameterGroup;
import org.onap.policy.apex.services.onappf.rest.HealthCheckRestControllerV1;
import org.onap.policy.common.endpoints.http.server.RestServer;
import org.onap.policy.common.endpoints.listeners.MessageTypeDispatcher;
import org.onap.policy.common.message.bus.event.TopicEndpointManager;
import org.onap.policy.common.message.bus.event.TopicSink;
import org.onap.policy.common.message.bus.event.TopicSource;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.common.utils.services.Registry;
import org.onap.policy.common.utils.services.ServiceManager;
import org.onap.policy.common.utils.services.ServiceManagerException;
import org.onap.policy.models.pdp.enums.PdpMessageType;
import org.onap.policy.models.tosca.authorative.concepts.ToscaConceptIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class activates the ApexStarter as a complete service together with all its handlers.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class ApexStarterActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApexStarterActivator.class);
    @Getter
    private final ApexStarterParameterGroup parameterGroup;
    private final List<TopicSink> topicSinks; // topics to which apex-pdp sends pdp status
    private final List<TopicSource> topicSources; // topics to which apex-pdp listens to for messages from pap.
    private static final String[] MSG_TYPE_NAMES = { "messageName" };

    /**
     * Listens for messages on the topic, decodes them into a message, and then dispatches them.
     */
    private final MessageTypeDispatcher msgDispatcher;

    /**
     * Used to manage the services.
     */
    private final ServiceManager manager;

    /**
     * The ApexStarter REST API server.
     */
    private RestServer restServer;

    @Getter
    @Setter(lombok.AccessLevel.PRIVATE)
    private volatile boolean alive = false;

    @Getter
    private List<ToscaConceptIdentifier> supportedPolicyTypes;

    @Getter
    private final String instanceId;

    /**
     * Instantiate the activator for onappf PDP-A.
     *
     * @param apexStarterParameterGroup the parameters for the onappf PDP-A service
     */
    public ApexStarterActivator(final ApexStarterParameterGroup apexStarterParameterGroup) {

        topicSinks = TopicEndpointManager.getManager()
                        .addTopicSinks(apexStarterParameterGroup.getTopicParameterGroup().getTopicSinks());

        topicSources = TopicEndpointManager.getManager()
                        .addTopicSources(apexStarterParameterGroup.getTopicParameterGroup().getTopicSources());

        instanceId = NetworkUtil.genUniqueName("apex");
        LOGGER.debug("ApexStarterActivator initializing with instance id: {}", instanceId);
        try {
            this.parameterGroup = apexStarterParameterGroup;
            this.msgDispatcher = new MessageTypeDispatcher(MSG_TYPE_NAMES);
        } catch (final RuntimeException e) {
            throw new ApexStarterRunTimeException(e);
        }

        final var pdpUpdateListener = new PdpUpdateListener();
        final var pdpStateChangeListener = new PdpStateChangeListener();
        final var pdpMessageHandler = new PdpMessageHandler();
        supportedPolicyTypes =
            pdpMessageHandler.getSupportedPolicyTypesFromParameters(apexStarterParameterGroup.getPdpStatusParameters());

        // @formatter:off
        this.manager = new ServiceManager()
                .addAction("topics",
                    () -> TopicEndpointManager.getManager().start(),
                    () -> TopicEndpointManager.getManager().shutdown())
                .addAction("set alive",
                    () -> setAlive(true),
                    () -> setAlive(false))
                .addAction("register pdp status context object",
                    () -> Registry.register(ApexStarterConstants.REG_PDP_STATUS_OBJECT,
                                pdpMessageHandler.createPdpStatusFromParameters(instanceId,
                                        apexStarterParameterGroup.getPdpStatusParameters())),
                    () -> Registry.unregister(ApexStarterConstants.REG_PDP_STATUS_OBJECT))
                .addAction("topic sinks",
                    () -> Registry.register(ApexStarterConstants.REG_APEX_PDP_TOPIC_SINKS, topicSinks),
                    () -> Registry.unregister(ApexStarterConstants.REG_APEX_PDP_TOPIC_SINKS))
                .addAction("Pdp Status publisher",
                    () -> Registry.register(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER,
                                new PdpStatusPublisher(topicSinks,
                                        apexStarterParameterGroup.getPdpStatusParameters().getTimeIntervalMs())),
                    this::stopAndRemovePdpStatusPublisher)
                .addAction("Register pdp update listener",
                    () -> msgDispatcher.register(PdpMessageType.PDP_UPDATE.name(), pdpUpdateListener),
                    () -> msgDispatcher.unregister(PdpMessageType.PDP_UPDATE.name()))
                .addAction("Register pdp state change request dispatcher",
                    () -> msgDispatcher.register(PdpMessageType.PDP_STATE_CHANGE.name(), pdpStateChangeListener),
                    () -> msgDispatcher.unregister(PdpMessageType.PDP_STATE_CHANGE.name()))
                .addAction("Message Dispatcher",
                    this::registerMsgDispatcher,
                    this::unregisterMsgDispatcher)
                .addAction("Create REST server",
                    () -> restServer = new RestServer(apexStarterParameterGroup.getRestServerParameters(),
                                List.of(), List.of(HealthCheckRestControllerV1.class)),
                    () -> restServer = null)
                .addAction("Rest Server",
                    () -> restServer.start(),
                    () -> restServer.stop());

        // @formatter:on
    }

    /**
     * Method to stop and unregister the pdp status publisher.
     */
    private void stopAndRemovePdpStatusPublisher() {
        final var pdpStatusPublisher =
                Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER, PdpStatusPublisher.class);
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
            final var pdpStatusPublisher =
                    Registry.get(ApexStarterConstants.REG_PDP_STATUS_PUBLISHER, PdpStatusPublisher.class);
            // send a final heartbeat with terminated status
            pdpStatusPublisher.send(new PdpMessageHandler().getTerminatedPdpStatus());
            manager.stop();
            Registry.unregister(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR);
        } catch (final ServiceManagerException exp) {
            LOGGER.error("ApexStarter termination failed");
            throw new ApexStarterException(exp.getMessage(), exp);
        }
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
