/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.services.onappf.comm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.main.ApexPolicyStatisticsManager;
import org.onap.policy.apex.services.onappf.ApexStarterActivator;
import org.onap.policy.apex.services.onappf.ApexStarterCommandLineArguments;
import org.onap.policy.apex.services.onappf.ApexStarterConstants;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.apex.services.onappf.handler.ApexEngineHandler;
import org.onap.policy.apex.services.onappf.handler.PdpMessageHandler;
import org.onap.policy.apex.services.onappf.parameters.ApexStarterParameterGroup;
import org.onap.policy.apex.services.onappf.parameters.ApexStarterParameterHandler;
import org.onap.policy.common.message.bus.event.Topic.CommInfrastructure;
import org.onap.policy.common.utils.cmd.CommandLineException;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.services.Registry;
import org.onap.policy.models.pdp.concepts.PdpStateChange;
import org.onap.policy.models.pdp.concepts.PdpStatus;
import org.onap.policy.models.pdp.concepts.PdpUpdate;
import org.onap.policy.models.pdp.enums.PdpState;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;

/**
 * Class to perform unit test of {@link PdpUpdateListener}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
class TestPdpUpdateListener {
    private PdpUpdateListener pdpUpdateMessageListener;
    private PdpStateChangeListener pdpStateChangeListener;
    private static final CommInfrastructure INFRA = CommInfrastructure.NOOP;
    private static final String TOPIC = "my-topic";
    private ApexStarterActivator activator;
    private ApexEngineHandler apexEngineHandler;
    private final PrintStream stdout = System.out;

    /**
     * Method for setup before each test.
     *
     * @throws ApexStarterException if some error occurs while starting up the apex starter
     * @throws CommandLineException if any parsing of args has errors
     */
    @BeforeEach
    void setUp() throws ApexStarterException, CommandLineException {
        Registry.newRegistry();
        final String[] apexStarterConfigParameters = {"-c", "src/test/resources/ApexStarterConfigParametersNoop.json"};
        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        ApexStarterParameterGroup parameterGroup;
        // The arguments return a string if there is a message to print and we should
        // exit
        final String argumentMessage = arguments.parse(apexStarterConfigParameters);
        if (argumentMessage != null) {
            return;
        }
        // Validate that the arguments are sane
        arguments.validate();

        // Read the parameters
        parameterGroup = new ApexStarterParameterHandler().getParameters(arguments);

        activator = new ApexStarterActivator(parameterGroup);
        Registry.register(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR, activator);
        activator.initialize();
        pdpUpdateMessageListener = new PdpUpdateListener();
        pdpStateChangeListener = new PdpStateChangeListener();
        Registry.register(ApexPolicyStatisticsManager.REG_APEX_PDP_POLICY_COUNTER,
            new ApexPolicyStatisticsManager());
    }

    /**
     * Method for cleanup after each test.
     *
     * @throws Exception if an error occurs
     */
    @AfterEach
    void teardown() throws Exception {
        System.setOut(stdout);
        apexEngineHandler =
            Registry.getOrDefault(ApexStarterConstants.REG_APEX_ENGINE_HANDLER, ApexEngineHandler.class, null);
        if (null != apexEngineHandler && apexEngineHandler.isApexEngineRunning()) {
            apexEngineHandler.shutdown();
        }
        // clear the apex starter activator
        if (activator != null && activator.isAlive()) {
            activator.terminate();
        }
    }

    @Test
    void testPdpUpdateMessageListener() throws CoderException {
        final PdpStatus pdpStatus = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT);
        final ToscaPolicy toscaPolicy =
            TestListenerUtils.createToscaPolicy("apex policy name", "1.0", "src/test/resources/dummyProperties.json");
        final List<ToscaPolicy> toscaPolicies = new ArrayList<ToscaPolicy>();
        toscaPolicies.add(toscaPolicy);
        final PdpUpdate pdpUpdateMsg = TestListenerUtils.createPdpUpdateMsg(pdpStatus, toscaPolicies,
            new LinkedList<>());
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null, pdpUpdateMsg);
        assertEquals(pdpStatus.getPdpGroup(), pdpUpdateMsg.getPdpGroup());
        assertEquals(pdpStatus.getPdpSubgroup(), pdpUpdateMsg.getPdpSubgroup());
        assertEquals(pdpStatus.getPolicies(),
            new PdpMessageHandler().getToscaPolicyIdentifiers(pdpUpdateMsg.getPoliciesToBeDeployed()));
    }

    @Test
    void testPdpUpdateMessageListener_success() throws CoderException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        final PdpStatus pdpStatus = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT);
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null,
            TestListenerUtils.createPdpUpdateMsg(pdpStatus, new ArrayList<>(),
                new ArrayList<>()));
        PdpStateChange pdpStateChangeMsg =
            TestListenerUtils.createPdpStateChangeMsg(PdpState.ACTIVE, "pdpGroup", "pdpSubgroup", pdpStatus.getName());
        pdpStateChangeListener.onTopicEvent(INFRA, TOPIC, null, pdpStateChangeMsg);
        final ToscaPolicy toscaPolicy =
            TestListenerUtils.createToscaPolicy("apex_policy_name", "1.0", "src/test/resources/dummyProperties.json");
        final List<ToscaPolicy> toscaPolicies = new ArrayList<ToscaPolicy>();
        toscaPolicies.add(toscaPolicy);
        final PdpUpdate pdpUpdateMsg = TestListenerUtils.createPdpUpdateMsg(pdpStatus, toscaPolicies,
            new LinkedList<>());
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null, pdpUpdateMsg);
        final String outString = outContent.toString();
        assertEquals(pdpStatus.getPdpGroup(), pdpUpdateMsg.getPdpGroup());
        assertEquals(pdpStatus.getPdpSubgroup(), pdpUpdateMsg.getPdpSubgroup());
        assertEquals(pdpStatus.getPolicies(),
            new PdpMessageHandler().getToscaPolicyIdentifiers(pdpUpdateMsg.getPoliciesToBeDeployed()));
        assertThat(outString).contains("Apex engine started. Deployed policies are: apex_policy_name:1.0");
    }

    @Test
    void testPdpUpdateMessageListener_undeploy() throws CoderException {
        final PdpStatus pdpStatus = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT);
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null,
            TestListenerUtils.createPdpUpdateMsg(pdpStatus, new ArrayList<>(),
                new ArrayList<>()));
        PdpStateChange pdpStateChangeMsg =
            TestListenerUtils.createPdpStateChangeMsg(PdpState.ACTIVE, "pdpGroup", "pdpSubgroup", pdpStatus.getName());
        pdpStateChangeListener.onTopicEvent(INFRA, TOPIC, null, pdpStateChangeMsg);
        final ToscaPolicy toscaPolicy =
            TestListenerUtils.createToscaPolicy("apex_policy_name", "1.0", "src/test/resources/dummyProperties.json");
        final List<ToscaPolicy> toscaPolicies = new ArrayList<ToscaPolicy>();
        toscaPolicies.add(toscaPolicy);
        final PdpUpdate pdpUpdateMsg = TestListenerUtils.createPdpUpdateMsg(pdpStatus, toscaPolicies,
            new ArrayList<>());
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null, pdpUpdateMsg);
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null,
            TestListenerUtils.createPdpUpdateMsg(pdpStatus, new ArrayList<>(),
                toscaPolicies.stream().map(ToscaPolicy::getIdentifier)
                    .collect(Collectors.toList())));
        final String outString = outContent.toString();
        assertThat(outString).contains("Pdp update successful. No policies are running.");
    }

    @Test
    void testPdpUpdateMessageListener_multi_policy_duplicate() throws CoderException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        final PdpStatus pdpStatus = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT);
        final ToscaPolicy toscaPolicy =
            TestListenerUtils.createToscaPolicy("apex_policy_name", "1.0", "src/test/resources/dummyProperties.json");
        final ToscaPolicy toscaPolicy2 =
            TestListenerUtils.createToscaPolicy("apexpolicy2", "1.0", "src/test/resources/dummyProperties.json");
        final List<ToscaPolicy> toscaPolicies = new ArrayList<ToscaPolicy>();
        toscaPolicies.add(toscaPolicy);
        toscaPolicies.add(toscaPolicy2);
        final PdpUpdate pdpUpdateMsg = TestListenerUtils.createPdpUpdateMsg(pdpStatus, toscaPolicies,
            new LinkedList<>());
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null, pdpUpdateMsg);
        PdpStateChange pdpStateChangeMsg =
            TestListenerUtils.createPdpStateChangeMsg(PdpState.ACTIVE, "pdpGroup", "pdpSubgroup", pdpStatus.getName());
        pdpStateChangeListener.onTopicEvent(INFRA, TOPIC, null, pdpStateChangeMsg);
        final String outString = outContent.toString();
        assertTrue(outString.contains(
            "Apex engine started. But, only the following polices are running - apex_policy_name:1.0  . "
                + "Other policies failed execution. Please see the logs for more details."));
    }

    @Test
    void testPdpUpdateMessageListener_pdpUpdateMessageIntervalZero() throws CoderException {
        final PdpStatus pdpStatus = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT);
        final ToscaPolicy toscaPolicy =
            TestListenerUtils.createToscaPolicy("apex policy name", "1.0", "src/test/resources/dummyProperties.json");
        final List<ToscaPolicy> toscaPolicies = new ArrayList<ToscaPolicy>();
        toscaPolicies.add(toscaPolicy);
        final PdpUpdate pdpUpdateMsg = TestListenerUtils.createPdpUpdateMsg(pdpStatus, toscaPolicies,
            new LinkedList<>());
        pdpUpdateMsg.setPdpHeartbeatIntervalMs((long) -1);
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null, pdpUpdateMsg);
        assertEquals(pdpStatus.getPdpGroup(), pdpUpdateMsg.getPdpGroup());
        assertEquals(pdpStatus.getPdpSubgroup(), pdpUpdateMsg.getPdpSubgroup());
        assertEquals(pdpStatus.getPolicies(),
            new PdpMessageHandler().getToscaPolicyIdentifiers(pdpUpdateMsg.getPoliciesToBeDeployed()));
    }
}
