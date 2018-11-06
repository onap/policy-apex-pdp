/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;
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
public class ParameterTests {
    @Test
    public void invalidParametersNoFileTest() throws ParameterException {
        final String[] args =
            { "-c", "src/test/resources/parameters/invalidNoFile.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertTrue(e.getMessage().startsWith("error reading parameters from \"src"));
            assertTrue(e.getMessage().contains("FileNotFoundException"));
        }
    }

    @Test
    public void invalidParametersEmptyTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/empty.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertTrue(e.getMessage().startsWith(
                            "validation error(s) on parameters from \"src/test/resources/parameters/empty.json\""));
        }
    }

    @Test
    public void invalidParametersNoParamsTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/noParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("validation error(s) on parameters from \"src/test/resources/parameters/noParams.json\"\n"
                            + "parameter group \"APEX_PARAMETERS\" type "
                            + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                            + "parameter group has status INVALID\n" + "  parameter group \"UNDEFINED\" INVALID, "
                            + "engine service parameters are not specified\n"
                            + "  parameter group map \"eventOutputParameters\" INVALID, "
                            + "at least one event output must be specified\n"
                            + "  parameter group map \"eventInputParameters\" INVALID, "
                            + "at least one event input must be specified\n", e.getMessage());
        }
    }

    @Test
    public void invalidParametersBlankParamsTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/blankParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {

            assertEquals("validation error(s) on parameters from \"src/test/resources/parameters/blankParams.json\"\n"
                            + "parameter group \"APEX_PARAMETERS\" type "
                            + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                            + "parameter group has status INVALID\n"
                            + "  parameter group \"ENGINE_SERVICE_PARAMETERS\" type "
                            + "\"org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters\" "
                            + "INVALID, parameter group has status INVALID\n"
                            + "    field \"id\" type \"int\" value \"-1\" INVALID, "
                            + "id not specified or specified value [-1] invalid, must be specified as id >= 0\n"
                            + "  parameter group map \"eventOutputParameters\" INVALID, "
                            + "at least one event output must be specified\n"
                            + "  parameter group map \"eventInputParameters\" INVALID, "
                            + "at least one event input must be specified\n", e.getMessage());
        }
    }

    @Test
    public void invalidParametersTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/badParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("validation error(s) on parameters from \"src/test/resources/parameters/badParams.json\"\n"
                            + "parameter group \"APEX_PARAMETERS\" type "
                            + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                            + "parameter group has status INVALID\n" + "  parameter group \"hello there\" type "
                            + "\"org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters\" "
                            + "INVALID, parameter group has status INVALID\n"
                            + "    field \"name\" type \"java.lang.String\" value \"hello there\" INVALID, "
                            + "name is invalid, it must match regular expression[A-Za-z0-9\\-_\\.]+\n"
                            + "    field \"id\" type \"int\" value \"-45\" INVALID, id not specified or "
                            + "specified value [-45] invalid, must be specified as id >= 0\n"
                            + "    field \"instanceCount\" type \"int\" value \"-345\" INVALID, "
                            + "instanceCount [-345] invalid, must be specified as instanceCount >= 1\n"
                            + "    field \"deploymentPort\" type \"int\" value \"65536\" INVALID, "
                            + "deploymentPort [65536] invalid, must be specified as 1024 <= port <= 65535\n"
                            + "    field \"policyModelFileName\" type \"java.lang.String\" "
                            + "value \"/some/file/name.xml\" INVALID, not found\n"
                            + "  parameter group map \"eventOutputParameters\" INVALID, "
                            + "parameter group has status INVALID\n" + "    parameter group \"FirstProducer\" type "
                            + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                            + ", parameter group has status INVALID\n" + "      parameter group \"FILE\" type "
                            + "\"org.onap.policy.apex.service.engine.event.impl."
                            + "filecarrierplugin.FileCarrierTechnologyParameters\" INVALID, "
                            + "parameter group has status INVALID\n"
                            + "        field \"fileName\" type \"java.lang.String\" value \"null\" INVALID, "
                            + "\"null\" invalid, must be specified as a non-empty string\n"
                            + "  parameter group map \"eventInputParameters\" INVALID, "
                            + "parameter group has status INVALID\n" + "    parameter group \"TheFileConsumer1\" type "
                            + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                            + ", parameter group has status INVALID\n" + "      parameter group \"FILE\" type "
                            + "\"org.onap.policy.apex.service.engine.event.impl."
                            + "filecarrierplugin.FileCarrierTechnologyParameters\" INVALID, "
                            + "parameter group has status INVALID\n"
                            + "        field \"fileName\" type \"java.lang.String\" value \"null\" INVALID, "
                            + "\"null\" invalid, must be specified as a non-empty string\n", e.getMessage());
        }
    }

    @Test
    public void modelNotFileTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/badParamsModelNotFile.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("validation error(s) on parameters from "
                            + "\"src/test/resources/parameters/badParamsModelNotFile.json\"\n"
                            + "parameter group \"APEX_PARAMETERS\" type "
                            + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                            + "parameter group has status INVALID\n" + "  parameter group \"MyApexEngine\" type "
                            + "\"org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters\" "
                            + "INVALID, parameter group has status INVALID\n" + "    field \"policyModelFileName\" "
                            + "type \"java.lang.String\" value \"src/test\" INVALID, is not a plain file\n",
                            e.getMessage());
        }
    }

    @Test
    public void goodParametersTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/goodParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

            assertEquals(2, parameters.getEventInputParameters().size());
            assertEquals(2, parameters.getEventOutputParameters().size());

            assertTrue(parameters.getEventOutputParameters().containsKey("FirstProducer"));
            assertTrue(parameters.getEventOutputParameters().containsKey("MyOtherProducer"));
            assertEquals("FILE", parameters.getEventOutputParameters().get("FirstProducer")
                            .getCarrierTechnologyParameters().getLabel());
            assertEquals("FILE", parameters.getEventOutputParameters().get("MyOtherProducer")
                            .getCarrierTechnologyParameters().getLabel());
            assertEquals(ApexFileEventProducer.class.getCanonicalName(), parameters.getEventOutputParameters()
                            .get("MyOtherProducer").getCarrierTechnologyParameters().getEventProducerPluginClass());
            assertEquals(ApexFileEventConsumer.class.getCanonicalName(), parameters.getEventOutputParameters()
                            .get("MyOtherProducer").getCarrierTechnologyParameters().getEventConsumerPluginClass());
            assertEquals("JSON", parameters.getEventOutputParameters().get("FirstProducer").getEventProtocolParameters()
                            .getLabel());
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
            assertEquals(ApexFileEventProducer.class.getCanonicalName(), parameters.getEventInputParameters()
                            .get("TheFileConsumer1").getCarrierTechnologyParameters().getEventProducerPluginClass());
            assertEquals(ApexFileEventConsumer.class.getCanonicalName(), parameters.getEventInputParameters()
                            .get("TheFileConsumer1").getCarrierTechnologyParameters().getEventConsumerPluginClass());
            assertEquals(SuperDooperEventProducer.class.getCanonicalName(),
                            parameters.getEventInputParameters().get("MySuperDooperConsumer1")
                                            .getCarrierTechnologyParameters().getEventProducerPluginClass());
            assertEquals(SuperDooperEventSubscriber.class.getCanonicalName(),
                            parameters.getEventInputParameters().get("MySuperDooperConsumer1")
                                            .getCarrierTechnologyParameters().getEventConsumerPluginClass());
        } catch (final ParameterException e) {
            fail("This test should not throw an exception");
        }
    }

    @Test
    public void superDooperParametersTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/superDooperParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

            assertEquals("MyApexEngine", parameters.getEngineServiceParameters().getName());
            assertEquals("0.0.1", parameters.getEngineServiceParameters().getVersion());
            assertEquals(45, parameters.getEngineServiceParameters().getId());
            assertEquals(345, parameters.getEngineServiceParameters().getInstanceCount());
            assertEquals(65522, parameters.getEngineServiceParameters().getDeploymentPort());

            final CarrierTechnologyParameters prodCarrierTech = parameters.getEventOutputParameters()
                            .get("FirstProducer").getCarrierTechnologyParameters();
            final EventProtocolParameters prodEventProt = parameters.getEventOutputParameters().get("FirstProducer")
                            .getEventProtocolParameters();
            final CarrierTechnologyParameters consCarrierTech = parameters.getEventInputParameters()
                            .get("MySuperDooperConsumer1").getCarrierTechnologyParameters();
            final EventProtocolParameters consEventProt = parameters.getEventInputParameters()
                            .get("MySuperDooperConsumer1").getEventProtocolParameters();

            assertEquals("SUPER_DOOPER", prodCarrierTech.getLabel());
            assertEquals("SUPER_TOK_DEL", prodEventProt.getLabel());
            assertEquals("SUPER_DOOPER", consCarrierTech.getLabel());
            assertEquals("JSON", consEventProt.getLabel());

            assertTrue(prodCarrierTech instanceof SuperDooperCarrierTechnologyParameters);

            final SuperDooperCarrierTechnologyParameters superDooperParameters
                = (SuperDooperCarrierTechnologyParameters) prodCarrierTech;
            assertEquals("somehost:12345", superDooperParameters.getBootstrapServers());
            assertEquals("0", superDooperParameters.getAcks());
            assertEquals(25, superDooperParameters.getRetries());
            assertEquals(98765, superDooperParameters.getBatchSize());
            assertEquals(21, superDooperParameters.getLingerTime());
            assertEquals(50505050, superDooperParameters.getBufferMemory());
            assertEquals("first-group-id", superDooperParameters.getGroupId());
            assertFalse(superDooperParameters.isEnableAutoCommit());
            assertEquals(441, superDooperParameters.getAutoCommitTime());
            assertEquals(987, superDooperParameters.getSessionTimeout());
            assertEquals("producer-out", superDooperParameters.getProducerTopic());
            assertEquals(101, superDooperParameters.getConsumerPollTime());
            assertEquals("some.key.serailizer", superDooperParameters.getKeySerializer());
            assertEquals("some.value.serailizer", superDooperParameters.getValueSerializer());
            assertEquals("some.key.deserailizer", superDooperParameters.getKeyDeserializer());
            assertEquals("some.value.deserailizer", superDooperParameters.getValueDeserializer());

            final String[] consumerTopics =
                { "consumer-out-0", "consumer-out-1", "consumer-out-2" };
            assertEquals(Arrays.asList(consumerTopics), superDooperParameters.getConsumerTopicList());
        } catch (final ParameterException e) {
            fail("This test should not throw an exception");
        }
    }
}
