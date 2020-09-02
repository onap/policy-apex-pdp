/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Test;
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
public class SyncParameterTests {
    @Test
    public void testSyncBadNoSyncWithPeer() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsNoSyncWithPeer.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("validation error(s) on parameters from "
                + "\"src/test/resources/parameters/syncBadParamsNoSyncWithPeer.json\"\n"
                + "parameter group \"APEX_PARAMETERS\" type "
                + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                + "parameter group has status INVALID\n"
                + "  parameter group map \"eventOutputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncProducer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", specified peered mode \"SYNCHRONOUS\" "
                + "peer is illegal on eventOutputParameters \"SyncProducer0\" \n");
    }

    @Test
    public void testSyncBadNotSyncWithPeer() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsNotSyncWithPeer.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("validation error(s) on parameters from "
                + "\"src/test/resources/parameters/syncBadParamsNotSyncWithPeer.json\"\n"
                + "parameter group \"APEX_PARAMETERS\" type "
                + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                + "parameter group has status INVALID\n"
                + "  parameter group map \"eventOutputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncProducer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", specified peered mode \"SYNCHRONOUS\" peer is illegal "
                + "on eventOutputParameters \"SyncProducer0\" \n");
    }

    @Test
    public void testSyncBadSyncBadPeers() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsBadPeers.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("validation error(s) on parameters from "
                + "\"src/test/resources/parameters/syncBadParamsBadPeers.json\"\n"
                + "parameter group \"APEX_PARAMETERS\" type "
                + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                + "parameter group has status INVALID\n"
                + "  parameter group map \"eventOutputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncProducer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncProducer1 for peered mode SYNCHRONOUS does not exist "
                + "or is not defined with the same peered mode\n" + "    parameter group \"SyncProducer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncProducer0 for peered mode SYNCHRONOUS does not exist "
                + "or is not defined with the same peered mode\n"
                + "  parameter group map \"eventInputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncConsumer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncConsumer1 for peered mode SYNCHRONOUS does not exist "
                + "or is not defined with the same peered mode\n" + "    parameter group \"SyncConsumer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncConsumer0 for peered mode SYNCHRONOUS does not exist "
                + "or is not defined with the same peered mode\n");
    }

    @Test
    public void testSyncBadSyncInvalidTimeout() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsInvalidTimeout.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("validation error(s) on parameters from "
                + "\"src/test/resources/parameters/syncBadParamsInvalidTimeout.json\"\n"
                + "parameter group \"APEX_PARAMETERS\" type "
                + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                + "parameter group has status INVALID\n"
                + "  parameter group map \"eventOutputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncProducer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID,"
                + " specified peered mode \"SYNCHRONOUS\" timeout value \"-10\" is illegal, "
                + "specify a non-negative timeout value in milliseconds\n"
                + "    parameter group \"SyncProducer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" "
                + "INVALID, specified peered mode \"SYNCHRONOUS\" timeout value \"-3\" is illegal, "
                + "specify a non-negative timeout value in milliseconds\n"
                + "  parameter group map \"eventInputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncConsumer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" "
                + "INVALID, specified peered mode \"SYNCHRONOUS\" timeout value \"-1\" is illegal, "
                + "specify a non-negative timeout value in milliseconds\n"
                + "    parameter group \"SyncConsumer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID,"
                + " specified peered mode \"SYNCHRONOUS\" timeout value \"-99999999\" is illegal, "
                + "specify a non-negative timeout value in milliseconds\n");
    }

    @Test
    public void testSyncBadSyncBadTimeout() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsBadTimeout.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("validation error(s) on parameters from "
                + "\"src/test/resources/parameters/syncBadParamsBadTimeout.json\"\n"
                + "parameter group \"APEX_PARAMETERS\" type "
                + "\"org.onap.policy.apex.service.parameters.ApexParameters\" "
                + "INVALID, parameter group has status INVALID\n"
                + "  parameter group map \"eventOutputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"MyOtherProducer\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" "
                + "INVALID, specified peered mode \"SYNCHRONOUS\" "
                + "timeout is illegal on eventOutputParameters \"MyOtherProducer\"\n");
    }

    @Test
    public void testSyncBadSyncUnpairedTimeout() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncBadParamsUnpairedTimeout.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("validation error(s) on parameters from "
                + "\"src/test/resources/parameters/syncBadParamsUnpairedTimeout.json\"\n"
                + "parameter group \"APEX_PARAMETERS\" type "
                + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                + "parameter group has status INVALID\n"
                + "  parameter group map \"eventOutputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncProducer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncConsumer0 for peered mode SYNCHRONOUS timeout 10 on event handler "
                + "\"SyncProducer0\" does not equal timeout 1 on event handler \"SyncConsumer0\"\n"
                + "    parameter group \"SyncProducer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncConsumer1 for peered mode SYNCHRONOUS timeout 3 on event handler "
                + "\"SyncProducer1\" does not equal timeout 99999999 on event handler \"SyncConsumer1\"\n"
                + "  parameter group map \"eventInputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncConsumer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncProducer0 for peered mode SYNCHRONOUS timeout 1 on event handler "
                + "\"SyncConsumer0\" does not equal timeout 10 on event handler \"SyncProducer0\"\n"
                + "    parameter group \"SyncConsumer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncProducer1 for peered mode SYNCHRONOUS timeout 99999999 on event handler "
                + "\"SyncConsumer1\" does not equal timeout 3 on event handler \"SyncProducer1\"\n" + "");
    }

    @Test
    public void testSyncGoodSyncGoodTimeoutProducer() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncGoodParamsProducerTimeout.json"};
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
    public void testSyncGoodSyncGoodTimeoutConsumer() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncGoodParamsConsumerTimeout.json"};
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
    public void testSyncGoodSyncGoodTimeoutBoth() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncGoodParamsBothTimeout.json"};
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
    public void testSyncUnusedConsumerPeers() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncUnusedConsumerPeers.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("validation error(s) on parameters from "
                + "\"src/test/resources/parameters/syncUnusedConsumerPeers.json\"\n"
                + "parameter group \"APEX_PARAMETERS\" type "
                + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                + "parameter group has status INVALID\n"
                + "  parameter group map \"eventOutputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncProducer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" "
                + "INVALID, peer \"SyncConsumer0 for peered mode SYNCHRONOUS, "
                + "value \"SyncProducer0\" on peer \"SyncConsumer0\" "
                + "does not equal event handler \"SyncProducer1\"\n"
                + "  parameter group map \"eventInputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncConsumer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" "
                + "INVALID, peer \"SyncProducer1 for peered mode SYNCHRONOUS, "
                + "value \"SyncConsumer0\" on peer \"SyncProducer1\" "
                + "does not equal event handler \"SyncConsumer1\"\n");
    }

    @Test
    public void testSyncMismatchedPeers() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncMismatchedPeers.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("validation error(s) on parameters from "
                + "\"src/test/resources/parameters/syncMismatchedPeers.json\"\n"
                + "parameter group \"APEX_PARAMETERS\" type "
                + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                + "parameter group has status INVALID\n"
                + "  parameter group map \"eventOutputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncProducer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncConsumer1 for peered mode SYNCHRONOUS, value \"SyncProducer1\" "
                + "on peer \"SyncConsumer1\" does not equal event handler \"SyncProducer0\"\n"
                + "    parameter group \"SyncProducer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncConsumer0 for peered mode SYNCHRONOUS, value \"SyncProducer0\" "
                + "on peer \"SyncConsumer0\" does not equal event handler \"SyncProducer1\"\n"
                + "  parameter group map \"eventInputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncConsumer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncProducer0 for peered mode SYNCHRONOUS, value \"SyncConsumer1\" "
                + "on peer \"SyncProducer0\" does not equal event handler \"SyncConsumer0\"\n"
                + "    parameter group \"SyncConsumer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncProducer1 for peered mode SYNCHRONOUS, value \"SyncConsumer0\" "
                + "on peer \"SyncProducer1\" does not equal event handler \"SyncConsumer1\"\n");
    }

    @Test
    public void testSyncUnusedProducerPeers() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncUnusedProducerPeers.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("validation error(s) on parameters from "
                + "\"src/test/resources/parameters/syncUnusedProducerPeers.json\"\n"
                + "parameter group \"APEX_PARAMETERS\" type "
                + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                + "parameter group has status INVALID\n"
                + "  parameter group map \"eventOutputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncProducer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncConsumer0 for peered mode SYNCHRONOUS, value \"SyncProducer1\" on peer "
                + "\"SyncConsumer0\" does not equal event handler \"SyncProducer0\"\n"
                + "  parameter group map \"eventInputParameters\" INVALID, parameter group map has status "
                + "INVALID\n    parameter group \"SyncConsumer0\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                + ", peer \"SyncProducer1 for peered mode SYNCHRONOUS, value \"SyncConsumer1\" on peer "
                + "\"SyncProducer1\" does not equal event handler \"SyncConsumer0\"\n");
    }

    @Test
    public void testSyncMismatchedTimeout() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/syncMismatchedTimeout.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("validation error(s) on parameters from "
                + "\"src/test/resources/parameters/syncMismatchedTimeout.json\"\n"
                + "parameter group \"APEX_PARAMETERS\" type "
                + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                + "parameter group has status INVALID\n" + "  parameter group map \"eventOutputParameters\" "
                + "INVALID, parameter group map has status INVALID\n"
                + "    parameter group \"SyncProducer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID,"
                + " peer \"SyncConsumer1 for peered mode SYNCHRONOUS timeout 456 "
                + "on event handler \"SyncProducer1\" does not equal timeout 123 "
                + "on event handler \"SyncConsumer1\"\n"
                + "  parameter group map \"eventInputParameters\" INVALID, "
                + "parameter group map has status INVALID\n" + "    parameter group \"SyncConsumer1\" type "
                + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID,"
                + " peer \"SyncProducer1 for peered mode SYNCHRONOUS timeout 123 "
                + "on event handler \"SyncConsumer1\" does not equal timeout 456 "
                + "on event handler \"SyncProducer1\"\n");
    }

    @Test
    public void testSyncGoodParametersTest() throws ParameterException {
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

        assertTrue(consCT1 instanceof SuperDooperCarrierTechnologyParameters);
        assertTrue(consEP1 instanceof SuperTokenDelimitedEventProtocolParameters);

        final SuperDooperCarrierTechnologyParameters superDooperParameters =
                (SuperDooperCarrierTechnologyParameters) consCT1;
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

        final String[] consumerTopics = {"apex-in"};
        assertEquals(Arrays.asList(consumerTopics), superDooperParameters.getConsumerTopicList());
    }
}
