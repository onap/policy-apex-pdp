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

package org.onap.policy.apex.starter.comm;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.starter.ApexStarterActivator;
import org.onap.policy.apex.starter.ApexStarterCommandLineArguments;
import org.onap.policy.apex.starter.ApexStarterConstants;
import org.onap.policy.apex.starter.exception.ApexStarterException;
import org.onap.policy.apex.starter.handler.PdpMessageHandler;
import org.onap.policy.apex.starter.parameters.ApexStarterParameterGroup;
import org.onap.policy.apex.starter.parameters.ApexStarterParameterHandler;
import org.onap.policy.common.endpoints.event.comm.Topic.CommInfrastructure;
import org.onap.policy.common.utils.services.Registry;
import org.onap.policy.models.pdp.concepts.PdpStatus;
import org.onap.policy.models.pdp.concepts.PdpUpdate;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;

/**
 * Class to perform unit test of {@link PdpUpdateListener}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class TestPdpUpdateListener {
    private PdpUpdateListener pdpUpdateMessageListener;
    private static final CommInfrastructure INFRA = CommInfrastructure.NOOP;
    private static final String TOPIC = "my-topic";
    private ApexStarterActivator activator;

    @Before
    public void setUp() throws ApexStarterException {
        Registry.newRegistry();
        final String[] apexStarterConfigParameters = { "-c", "src/test/resources/ApexStarterConfigParameters.json",
            "-p", "src/test/resources/topic.properties" };
        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        ApexStarterParameterGroup apexStarterParameterGroup;
        try {
            // The arguments return a string if there is a message to print and we should
            // exit
            final String argumentMessage = arguments.parse(apexStarterConfigParameters);
            if (argumentMessage != null) {
                return;
            }
            // Validate that the arguments are sane
            arguments.validate();
        } catch (final ApexStarterException e) {
            return;
        }

        // Read the parameters
        try {
            apexStarterParameterGroup = new ApexStarterParameterHandler().getParameters(arguments);
        } catch (final Exception e) {
            return;
        }

        // Read the properties
        final Properties topicProperties = new Properties();
        try {
            final String propFile = arguments.getFullPropertyFilePath();
            try (FileInputStream stream = new FileInputStream(propFile)) {
                topicProperties.load(stream);
            }
        } catch (final Exception e) {
            return;
        }
        activator = new ApexStarterActivator(apexStarterParameterGroup, topicProperties);
        Registry.register(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR, activator);
        activator.initialize();
        pdpUpdateMessageListener = new PdpUpdateListener();
    }

    /**
     * Method for cleanup after each test.
     *
     * @throws Exception if an error occurs
     */
    @After
    public void teardown() throws Exception {

        // clear the apex starter activator
        if (activator != null && activator.isAlive()) {
            activator.terminate();
        }
    }

    @Test
    public void testPdpUpdateMssageListener() {
        final PdpStatus pdpStatus = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT);
        final PdpUpdate pdpUpdateMsg = new PdpUpdate();
        pdpUpdateMsg.setDescription("dummy pdp status for test");
        pdpUpdateMsg.setPdpGroup("pdpGroup");
        pdpUpdateMsg.setPdpSubgroup("pdpSubgroup");
        pdpUpdateMsg.setName(pdpStatus.getInstance());
        pdpUpdateMsg.setPdpHeartbeatIntervalMs(3000);
        final ToscaPolicy toscaPolicy = new ToscaPolicy();
        toscaPolicy.setType("apexpolicytype");
        toscaPolicy.setVersion("1.0");
        toscaPolicy.setName("apex policy name");
        final Map<String, Object> propertiesMap = new LinkedHashMap<>();
        String properties;
        try {
            properties = new String(Files.readAllBytes(Paths.get("src\\test\\resources\\dummyProperties.json")));
            propertiesMap.put("content", properties);
        } catch (final IOException e) {
            propertiesMap.put("content", "");
        }
        toscaPolicy.setProperties(propertiesMap);
        final List<ToscaPolicy> toscaPolicies = new ArrayList<ToscaPolicy>();
        toscaPolicies.add(toscaPolicy);
        pdpUpdateMsg.setPolicies(toscaPolicies);
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, pdpUpdateMsg);
        assertEquals(pdpStatus.getPdpGroup(), pdpUpdateMsg.getPdpGroup());
        assertEquals(pdpStatus.getPdpSubgroup(), pdpUpdateMsg.getPdpSubgroup());
        assertEquals(pdpStatus.getPolicies(),
                new PdpMessageHandler().getToscaPolicyIdentifiers(pdpUpdateMsg.getPolicies()));
    }
}
