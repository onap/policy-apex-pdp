/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.service.engine.parameters;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperCarrierTechnologyParameters;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperTokenDelimitedEventProtocolParameters;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.onap.policy.common.parameters.ParameterException;

/**
 * Test for an empty parameter file.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class SyncParameterTests {

    @Test
    void testSyncBadNoSyncWithPeer() {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsNoSyncWithPeer.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/syncBadParamsNoSyncWithPeer.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer0\" INVALID")
            .hasMessageContaining("peer is illegal");
    }

    @Test
    void testSyncBadNotSyncWithPeer() {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsNotSyncWithPeer.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/syncBadParamsNotSyncWithPeer.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer0\" INVALID")
            .hasMessageContaining("peer is illegal");
    }

    @Test
    void testSyncBadSyncBadPeers() {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsBadPeers.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/syncBadParamsBadPeers.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer0\" INVALID")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer1\" INVALID")
            .hasMessageContaining("\"eventInputParameters\" value \"SyncConsumer0\" INVALID")
            .hasMessageContaining("\"eventInputParameters\" value \"SyncConsumer1\" INVALID")
            .hasMessageContaining("does not exist or is not defined");
    }

    @Test
    void testSyncBadSyncInvalidTimeout() {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsInvalidTimeout.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/syncBadParamsInvalidTimeout.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer0\" INVALID")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer1\" INVALID")
            .hasMessageContaining("\"eventInputParameters\" value \"SyncConsumer0\" INVALID")
            .hasMessageContaining("\"eventInputParameters\" value \"SyncConsumer1\" INVALID")
            .hasMessageContaining("timeout value \"-10\" is illegal")
            .hasMessageContaining("timeout value \"-3\" is illegal")
            .hasMessageContaining("timeout value \"-1\" is illegal")
            .hasMessageContaining("timeout value \"-99999999\" is illegal");
    }

    @Test
    void testSyncBadSyncBadTimeout() {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsBadTimeout.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/syncBadParamsBadTimeout.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("\"eventOutputParameters\" value \"MyOtherProducer\" INVALID, "
                + "specified peered mode \"SYNCHRONOUS\" timeout is illegal");
    }

    @Test
    void testSyncBadSyncUnpairedTimeout() {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsUnpairedTimeout.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/syncBadParamsUnpairedTimeout.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer0\" INVALID, peer \"SyncConsumer0\"")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer1\" INVALID, peer \"SyncConsumer1\"")
            .hasMessageContaining("\"eventInputParameters\" value \"SyncConsumer0\" INVALID, peer \"SyncProducer0\"")
            .hasMessageContaining("\"eventInputParameters\" value \"SyncConsumer1\" INVALID, peer \"SyncProducer1\"")
            .hasMessageContaining("timeout 10 does not equal peer timeout 1")
            .hasMessageContaining("timeout 3 does not equal peer timeout 99999999")
            .hasMessageContaining("timeout 1 does not equal peer timeout 10")
            .hasMessageContaining("timeout 99999999 does not equal peer timeout 3");
    }

    @Test
    void testSyncGoodSyncGoodTimeoutProducer() throws ParameterException {
        verifySyncGoodSyncGoodTimeout("src/test/resources/parameters/syncGoodParamsProducerTimeout.json");
    }

    @Test
    void testSyncGoodSyncGoodTimeoutConsumer() throws ParameterException {
        verifySyncGoodSyncGoodTimeout("src/test/resources/parameters/syncGoodParamsConsumerTimeout.json");
    }

    @Test
    void testSyncGoodSyncGoodTimeoutBoth() throws ParameterException {
        verifySyncGoodSyncGoodTimeout("src/test/resources/parameters/syncGoodParamsBothTimeout.json");
    }

    private void verifySyncGoodSyncGoodTimeout(String fileName) throws ParameterException {
        final String[] args = {"-p", fileName};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
        assertEquals(12345, parameters.getEventInputParameters().get("SyncConsumer0")
            .getPeerTimeout(EventHandlerPeeredMode.SYNCHRONOUS));
        assertEquals(1, parameters.getEventInputParameters().get("SyncConsumer1")
            .getPeerTimeout(EventHandlerPeeredMode.SYNCHRONOUS));
        assertEquals(12345, parameters.getEventOutputParameters().get("SyncProducer0")
            .getPeerTimeout(EventHandlerPeeredMode.SYNCHRONOUS));
        assertEquals(1, parameters.getEventOutputParameters().get("SyncProducer1")
            .getPeerTimeout(EventHandlerPeeredMode.SYNCHRONOUS));
    }

    @Test
    void testSyncUnusedConsumerPeers() {
        final String[] args = {"-p", "src/test/resources/parameters/syncUnusedConsumerPeers.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/syncUnusedConsumerPeers.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer1\" INVALID, peer \"SyncConsumer0\"")
            .hasMessageContaining("\"eventInputParameters\" value \"SyncConsumer1\" INVALID, peer \"SyncProducer1\"")
            .hasMessageContaining("value \"SyncProducer0\" on peer does not equal event handler")
            .hasMessageContaining("value \"SyncConsumer0\" on peer does not equal event handler");
    }

    @Test
    void testSyncMismatchedPeers() {
        final String[] args = {"-p", "src/test/resources/parameters/syncMismatchedPeers.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/syncMismatchedPeers.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer0\" INVALID, peer \"SyncConsumer1\"")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer1\" INVALID, peer \"SyncConsumer0\"")
            .hasMessageContaining("\"eventInputParameters\" value \"SyncConsumer0\" INVALID, peer \"SyncProducer0\"")
            .hasMessageContaining("\"eventInputParameters\" value \"SyncConsumer1\" INVALID, peer \"SyncProducer1\"")
            .hasMessageContaining("value \"SyncProducer1\" on peer does not equal event handler")
            .hasMessageContaining("value \"SyncProducer0\" on peer does not equal event handler")
            .hasMessageContaining("value \"SyncConsumer1\" on peer does not equal event handler")
            .hasMessageContaining("value \"SyncConsumer0\" on peer does not equal event handler");
    }

    @Test
    void testSyncUnusedProducerPeers() {
        final String[] args = {"-p", "src/test/resources/parameters/syncUnusedProducerPeers.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/syncUnusedProducerPeers.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer0\" INVALID, peer \"SyncConsumer0\"")
            .hasMessageContaining("\"eventInputParameters\" value \"SyncConsumer0\" INVALID, peer \"SyncProducer1\"")
            .hasMessageContaining("value \"SyncProducer1\" on peer does not equal event handler")
            .hasMessageContaining("value \"SyncConsumer1\" on peer does not equal event handler");
    }

    @Test
    void testSyncMismatchedTimeout() {
        final String[] args = {"-p", "src/test/resources/parameters/syncMismatchedTimeout.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/syncMismatchedTimeout.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("\"eventOutputParameters\" value \"SyncProducer1\" INVALID, peer \"SyncConsumer1\"")
            .hasMessageContaining("\"eventInputParameters\" value \"SyncConsumer1\" INVALID, peer \"SyncProducer1\"")
            .hasMessageContaining("timeout 123 does not equal peer timeout 456")
            .hasMessageContaining("timeout 456 does not equal peer timeout 123");
    }

    @Test
    void testSyncGoodParametersTest() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncGoodParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        assertEquals("MyApexEngine", parameters.getEngineServiceParameters().getName());
        assertEquals("0.0.1", parameters.getEngineServiceParameters().getVersion());
        assertEquals(45, parameters.getEngineServiceParameters().getId());
        assertEquals(19, parameters.getEngineServiceParameters().getInstanceCount());
        assertEquals(65522, parameters.getEngineServiceParameters().getDeploymentPort());

        final CarrierTechnologyParameters prodCT0 =
            parameters.getEventOutputParameters().get("SyncProducer0").getCarrierTechnologyParameters();
        final EventProtocolParameters prodEP0 =
            parameters.getEventOutputParameters().get("SyncProducer0").getEventProtocolParameters();
        final CarrierTechnologyParameters consCT0 =
            parameters.getEventInputParameters().get("SyncConsumer0").getCarrierTechnologyParameters();
        final EventProtocolParameters consEP0 =
            parameters.getEventInputParameters().get("SyncConsumer0").getEventProtocolParameters();
        final CarrierTechnologyParameters prodCT1 =
            parameters.getEventOutputParameters().get("SyncProducer1").getCarrierTechnologyParameters();
        final EventProtocolParameters prodEP1 =
            parameters.getEventOutputParameters().get("SyncProducer1").getEventProtocolParameters();
        final CarrierTechnologyParameters consCT1 =
            parameters.getEventInputParameters().get("SyncConsumer1").getCarrierTechnologyParameters();
        final EventProtocolParameters consEP1 =
            parameters.getEventInputParameters().get("SyncConsumer1").getEventProtocolParameters();

        assertEquals("FILE", prodCT0.getLabel());
        assertEquals("JSON", prodEP0.getLabel());
        assertEquals("FILE", consCT0.getLabel());
        assertEquals("JSON", consEP0.getLabel());
        assertEquals("FILE", prodCT1.getLabel());
        assertEquals("JSON", prodEP1.getLabel());
        assertEquals("SUPER_DOOPER", consCT1.getLabel());
        assertEquals("SUPER_TOK_DEL", consEP1.getLabel());

        assertInstanceOf(SuperDooperCarrierTechnologyParameters.class, consCT1);
        assertInstanceOf(SuperTokenDelimitedEventProtocolParameters.class, consEP1);

        final SuperDooperCarrierTechnologyParameters superDooperParameters =
            (SuperDooperCarrierTechnologyParameters) consCT1;
        assertEqualsOnFields(superDooperParameters);

        final String[] consumerTopics = {"apex-in"};
        assertEquals(Arrays.asList(consumerTopics), superDooperParameters.getConsumerTopicList());
    }

    private static void assertEqualsOnFields(SuperDooperCarrierTechnologyParameters superDooperParameters) {
        assertEquals("localhost:9092", superDooperParameters.getBootstrapServers());
        assertEquals("all", superDooperParameters.getAcks());
        assertEquals(0, superDooperParameters.getRetries());
        assertEquals(16384, superDooperParameters.getBatchSize());
        assertEquals(1, superDooperParameters.getLingerTime());
        assertEquals(33554432, superDooperParameters.getBufferMemory());
        assertEquals("default-group-id", superDooperParameters.getGroupId());
        assertTrue(superDooperParameters.isEnableAutoCommit());
        assertEquals(1000, superDooperParameters.getAutoCommitTime());
        assertEquals(30000, superDooperParameters.getSessionTimeout());
        assertEquals("apex-out", superDooperParameters.getProducerTopic());
        assertEquals(100, superDooperParameters.getConsumerPollTime());
        assertEquals("org.apache.superDooper.common.serialization.StringSerializer",
            superDooperParameters.getKeySerializer());
        assertEquals("org.apache.superDooper.common.serialization.StringSerializer",
            superDooperParameters.getValueSerializer());
        assertEquals("org.apache.superDooper.common.serialization.StringDeserializer",
            superDooperParameters.getKeyDeserializer());
        assertEquals("org.apache.superDooper.common.serialization.StringDeserializer",
            superDooperParameters.getValueDeserializer());
    }
}
