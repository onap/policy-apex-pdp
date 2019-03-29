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

import org.onap.policy.apex.starter.exception.ApexStarterException;
import org.onap.policy.apex.starter.handler.CommunicationHandler;
import org.onap.policy.apex.starter.handler.PdpMessageHandler;
import org.onap.policy.apex.starter.parameters.ApexStarterParameterGroup;
import org.onap.policy.apex.starter.parameters.PdpStatusParameters;
import org.onap.policy.common.endpoints.event.comm.TopicEndpoint;
import org.onap.policy.common.endpoints.event.comm.TopicSink;
import org.onap.policy.common.endpoints.event.comm.TopicSource;
import org.onap.policy.common.parameters.ParameterService;
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
    private CommunicationHandler communicationHandler;

    /**
     * Used to manage the services.
     */
    private ServiceManager manager;

    @Getter
    @Setter(lombok.AccessLevel.PRIVATE)
    private volatile boolean alive = false;

    public ApexStarterActivator(final ApexStarterParameterGroup apexStarterParameterGroup,
            final Properties topicProperties) {

        final List<TopicSink> topicSinks = TopicEndpoint.manager.addTopicSinks(topicProperties);
        final List<TopicSource> topicSources = TopicEndpoint.manager.addTopicSources(topicProperties);
        this.apexStarterParameterGroup = apexStarterParameterGroup;

        // TODO: instanceId currently set as a random string, could be fetched from actual deployment
        final int random = (int) (Math.random() * 100);
        final String instanceId = "apex_" + random;

        // @formatter:off
        this.manager = new ServiceManager()
                .addAction("topics",
                        () -> TopicEndpoint.manager.start(),
                        () -> TopicEndpoint.manager.shutdown())
                .addAction("set alive",
                        () -> setAlive(true),
                        () -> setAlive(false))
                .addAction("register context map",
                        () -> Registry.register(ApexStarterConstants.REG_PDP_STATUS_OBJECT,
                                new PdpMessageHandler().createPdpStatusFromParameters(instanceId,
                                        apexStarterParameterGroup.getPdpStatusParameters())),
                        () -> Registry.unregister(ApexStarterConstants.REG_PDP_STATUS_OBJECT))
                .addAction("register parameters",
                        () -> registerToParameterService(apexStarterParameterGroup),
                        () -> deregisterToParameterService(apexStarterParameterGroup))
                .addAction("Communication handler",
                        () -> startCommunicationHandler(topicSinks, topicSources,
                                apexStarterParameterGroup.getPdpStatusParameters()),
                        () -> communicationHandler.stop());
        // @formatter:on
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
     * Method to register the parameters to Common Parameter Service.
     *
     * @param apexStarterParameterGroup the apex starter parameter group
     */
    public void registerToParameterService(final ApexStarterParameterGroup apexStarterParameterGroup) {
        ParameterService.register(apexStarterParameterGroup);
    }

    /**
     * Method to deregister the parameters from Common Parameter Service.
     *
     * @param apexStarterParameterGroup the apex starter parameter group
     */
    public void deregisterToParameterService(final ApexStarterParameterGroup apexStarterParameterGroup) {
        ParameterService.deregister(apexStarterParameterGroup.getName());
    }

    /**
     * Starts the communication handler which handles the communication between apex pdp and pap.
     *
     * @param pdpStatusParameters
     * @param topicSources
     * @param topicSinks
     *
     * @throws ApexStarterException if the handler start fails
     */
    public void startCommunicationHandler(final List<TopicSink> topicSinks, final List<TopicSource> topicSources,
            final PdpStatusParameters pdpStatusParameters) throws ApexStarterException {
        communicationHandler = new CommunicationHandler(topicSinks, topicSources, pdpStatusParameters);
        if (!communicationHandler.start()) {
            throw new ApexStarterException("Failed to start the communication handler for ApexStarter");
        }
    }
}
