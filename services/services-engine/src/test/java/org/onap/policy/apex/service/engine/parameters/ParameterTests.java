/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
 * Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
 * Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.ApexFileEventConsumer;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.producer.ApexFileEventProducer;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperCarrierTechnologyParameters;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperEventProducer;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperEventSubscriber;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;
import org.onap.policy.common.parameters.ParameterException;

/**
 * Test for an empty parameter file.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ParameterTests {
    @Test
    void testInvalidParametersNoFileTest() {
        final String[] args = {"-p", "src/test/resources/parameters/invalidNoFile.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasRootCauseInstanceOf(NoSuchFileException.class);
    }

    @Test
    void testInvalidParametersEmptyTest() {
        final String[] args = {"-p", "src/test/resources/parameters/empty.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageStartingWith("error reading parameters from \"src/test/resources/parameters/empty.json");
    }

    @Test
    void testInvalidParametersNoParamsTest() {
        final String[] args = {"-p", "src/test/resources/parameters/noParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/noParams.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("\"engineServiceParameters\" value \"null\" INVALID, is null")
            .hasMessageContaining("\"eventOutputParameters\" value \"{}\" INVALID, minimum number of elements: 1")
            .hasMessageContaining("\"eventInputParameters\" value \"{}\" INVALID, minimum number of elements: 1");
    }

    @Test
    void testInvalidParametersBlankParamsTest() {
        final String[] args = {"-p", "src/test/resources/parameters/blankParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/blankParams.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("EngineServiceParameters")
            .hasMessageContaining("\"id\" value \"-1\" INVALID, is below the minimum")
            .hasMessageContaining("\"policyModel\" value \"null\" INVALID, is null")
            .hasMessageContaining("\"eventOutputParameters\" value \"{}\" INVALID, minimum number of elements")
            .hasMessageContaining("\"eventInputParameters\" value \"{}\" INVALID, minimum number of elements");
    }

    @Test
    void testInvalidParametersTest() {
        final String[] args = {"-p", "src/test/resources/parameters/badParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/badParams.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("EngineServiceParameters")
            .hasMessageContaining("\"name\" value \"hello there\" INVALID, does not match")
            .hasMessageContaining("\"id\" value \"-45\" INVALID, is below the minimum")
            .hasMessageContaining("\"instanceCount\" value \"-345\" INVALID, is below the minimum")
            .hasMessageContaining("\"deploymentPort\" value \"65536\" INVALID, exceeds the maximum")
            .hasMessageContaining("eventOutputParameters", "FirstProducer", "EventHandlerParameters",
                "FileCarrierTechnologyParameters")
            .hasMessageContaining("\"fileName\" value \"null\" INVALID, is blank")
            .hasMessageContaining("eventInputParameters", "TheFileConsumer1", "EventHandlerParameters",
                "FileCarrierTechnologyParameters")
            .hasMessageContaining("\"fileName\" value \"null\" INVALID, is blank");
    }

    @Test
    void testGoodParametersTest() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/goodParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        assertEquals(2, parameters.getEventInputParameters().size());
        assertEquals(2, parameters.getEventOutputParameters().size());
        assertTrue(parameters.getEventOutputParameters().containsKey("FirstProducer"));
        assertTrue(parameters.getEventOutputParameters().containsKey("MyOtherProducer"));
        assertEquals("FILE", parameters.getEventOutputParameters().get("FirstProducer")
            .getCarrierTechnologyParameters().getLabel());
        assertEquals("FILE", parameters.getEventOutputParameters().get("MyOtherProducer")
            .getCarrierTechnologyParameters().getLabel());
        assertEquals(ApexFileEventProducer.class.getName(), parameters.getEventOutputParameters()
            .get("MyOtherProducer").getCarrierTechnologyParameters().getEventProducerPluginClass());
        assertEquals(ApexFileEventConsumer.class.getName(), parameters.getEventOutputParameters()
            .get("MyOtherProducer").getCarrierTechnologyParameters().getEventConsumerPluginClass());
        assertEquals("JSON",
            parameters.getEventOutputParameters().get("FirstProducer").getEventProtocolParameters().getLabel());
        assertEquals("JSON", parameters.getEventOutputParameters().get("MyOtherProducer")
            .getEventProtocolParameters().getLabel());

        assertTrue(parameters.getEventInputParameters().containsKey("TheFileConsumer1"));
        assertTrue(parameters.getEventInputParameters().containsKey("MySuperDooperConsumer1"));
        assertEquals("FILE", parameters.getEventInputParameters().get("TheFileConsumer1")
            .getCarrierTechnologyParameters().getLabel());
        assertEquals("SUPER_DOOPER", parameters.getEventInputParameters().get("MySuperDooperConsumer1")
            .getCarrierTechnologyParameters().getLabel());
        assertEquals("JSON", parameters.getEventInputParameters().get("TheFileConsumer1")
            .getEventProtocolParameters().getLabel());
        assertEquals("SUPER_TOK_DEL", parameters.getEventInputParameters().get("MySuperDooperConsumer1")
            .getEventProtocolParameters().getLabel());
        assertEquals(ApexFileEventProducer.class.getName(), parameters.getEventInputParameters()
            .get("TheFileConsumer1").getCarrierTechnologyParameters().getEventProducerPluginClass());
        assertEquals(ApexFileEventConsumer.class.getName(), parameters.getEventInputParameters()
            .get("TheFileConsumer1").getCarrierTechnologyParameters().getEventConsumerPluginClass());
        assertEquals(SuperDooperEventProducer.class.getName(), parameters.getEventInputParameters()
            .get("MySuperDooperConsumer1").getCarrierTechnologyParameters().getEventProducerPluginClass());
        assertEquals(SuperDooperEventSubscriber.class.getName(), parameters.getEventInputParameters()
            .get("MySuperDooperConsumer1").getCarrierTechnologyParameters().getEventConsumerPluginClass());
    }

    @Test
    void testSuperDooperParametersTest() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/superDooperParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        assertEquals("MyApexEngine", parameters.getEngineServiceParameters().getName());
        assertEquals("0.0.1", parameters.getEngineServiceParameters().getVersion());
        assertEquals(45, parameters.getEngineServiceParameters().getId());
        assertEquals(345, parameters.getEngineServiceParameters().getInstanceCount());
        assertEquals(65522, parameters.getEngineServiceParameters().getDeploymentPort());

        final CarrierTechnologyParameters prodCarrierTech =
            parameters.getEventOutputParameters().get("FirstProducer").getCarrierTechnologyParameters();
        final EventProtocolParameters prodEventProt =
            parameters.getEventOutputParameters().get("FirstProducer").getEventProtocolParameters();
        final CarrierTechnologyParameters consCarrierTech =
            parameters.getEventInputParameters().get("MySuperDooperConsumer1").getCarrierTechnologyParameters();
        final EventProtocolParameters consEventProt =
            parameters.getEventInputParameters().get("MySuperDooperConsumer1").getEventProtocolParameters();

        assertEquals("SUPER_DOOPER", prodCarrierTech.getLabel());
        assertEquals("SUPER_TOK_DEL", prodEventProt.getLabel());
        assertEquals("SUPER_DOOPER", consCarrierTech.getLabel());
        assertEquals("JSON", consEventProt.getLabel());

        assertInstanceOf(SuperDooperCarrierTechnologyParameters.class, prodCarrierTech);

        final SuperDooperCarrierTechnologyParameters superDooperParameters =
            (SuperDooperCarrierTechnologyParameters) prodCarrierTech;
        assertFalse(superDooperParameters.isEnableAutoCommit());
        assertEqualsOnFields(superDooperParameters);

        final String[] consumerTopics = {"consumer-out-0", "consumer-out-1", "consumer-out-2"};
        assertEquals(Arrays.asList(consumerTopics), superDooperParameters.getConsumerTopicList());

    }

    private static void assertEqualsOnFields(SuperDooperCarrierTechnologyParameters superDooperParameters) {
        assertEquals("somehost:12345", superDooperParameters.getBootstrapServers());
        assertEquals("0", superDooperParameters.getAcks());
        assertEquals(25, superDooperParameters.getRetries());
        assertEquals(98765, superDooperParameters.getBatchSize());
        assertEquals(21, superDooperParameters.getLingerTime());
        assertEquals(50505050, superDooperParameters.getBufferMemory());
        assertEquals("first-group-id", superDooperParameters.getGroupId());
        assertEquals(441, superDooperParameters.getAutoCommitTime());
        assertEquals(987, superDooperParameters.getSessionTimeout());
        assertEquals("producer-out", superDooperParameters.getProducerTopic());
        assertEquals(101, superDooperParameters.getConsumerPollTime());
        assertEquals("some.key.serailizer", superDooperParameters.getKeySerializer());
        assertEquals("some.value.serailizer", superDooperParameters.getValueSerializer());
        assertEquals("some.key.deserailizer", superDooperParameters.getKeyDeserializer());
        assertEquals("some.value.deserailizer", superDooperParameters.getValueDeserializer());
    }
}
