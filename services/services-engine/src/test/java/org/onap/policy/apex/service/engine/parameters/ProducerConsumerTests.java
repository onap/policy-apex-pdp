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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.FileCarrierTechnologyParameters;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.common.parameters.ParameterException;

/**
 * Test for an empty parameter file.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ProducerConsumerTests {
    @Test
    void testGoodParametersTest() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/goodParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        assertEquals("MyApexEngine", parameters.getEngineServiceParameters().getName());
        assertEquals("0.0.1", parameters.getEngineServiceParameters().getVersion());
        assertEquals(45, parameters.getEngineServiceParameters().getId());
        assertEquals(19, parameters.getEngineServiceParameters().getInstanceCount());
        assertEquals(65522, parameters.getEngineServiceParameters().getDeploymentPort());
        assertEquals("FILE",
            parameters.getEventOutputParameters().get("FirstProducer").getCarrierTechnologyParameters().getLabel());
        assertEquals("JSON",
            parameters.getEventOutputParameters().get("FirstProducer").getEventProtocolParameters().getLabel());
        assertEquals("FILE",
            parameters.getEventOutputParameters().get("MyOtherProducer").getCarrierTechnologyParameters().getLabel());
        assertEquals("JSON",
            parameters.getEventOutputParameters().get("MyOtherProducer").getEventProtocolParameters().getLabel());
        assertEquals("FILE",
            parameters.getEventInputParameters().get("TheFileConsumer1").getCarrierTechnologyParameters().getLabel());
        assertEquals("JSON",
            parameters.getEventInputParameters().get("TheFileConsumer1").getEventProtocolParameters().getLabel());
        assertEquals("SUPER_DOOPER", parameters.getEventInputParameters().get("MySuperDooperConsumer1")
            .getCarrierTechnologyParameters().getLabel());
        assertEquals("SUPER_TOK_DEL",
            parameters.getEventInputParameters().get("MySuperDooperConsumer1").getEventProtocolParameters().getLabel());
    }

    @Test
    void testNoCarrierTechnology() {
        final String[] args = {"-p", "src/test/resources/parameters/prodConsNoCT.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/prodConsNoCT.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("eventInputParameters", "aConsumer", "EventHandlerParameters",
                "carrierTechnologyParameters")
            .hasMessageContaining("is null");
    }

    @Test
    void testNoEventProcol() {
        final String[] args = {"-p", "src/test/resources/parameters/prodConsNoEP.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/prodConsNoEP.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("eventOutputParameters", "aProducer", "EventHandlerParameters",
                "eventProtocolParameters")
            .hasMessageContaining("eventInputParameters", "aConsumer", "EventHandlerParameters",
                "FileCarrierTechnologyParameters", "fileName")
            .hasMessageContaining("is null")
            .hasMessageContaining("is blank");
    }

    @Test
    void testNoCarrierTechnologyParClass() {
        final String[] args = {"-p", "src/test/resources/parameters/prodConsNoCTParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from \"src/test/resources/parameters/prodConsNoCTParClass.json\"\n"
                + "(ParameterRuntimeException):carrier technology \"SUPER_DOOPER\" "
                + "parameter \"parameterClassName\" value \"null\" invalid in JSON file");
    }

    @Test
    void testMismatchCarrierTechnologyParClass() {
        final String[] args = {"-p", "src/test/resources/parameters/prodConsMismatchCTParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments)).hasMessage(
            "error reading parameters from " + "\"src/test/resources/parameters/prodConsMismatchCTParClass.json\"\n"
                + "(ParameterRuntimeException):carrier technology \"SUPER_LOOPER\" "
                + "does not match plugin \"SUPER_DOOPER\" in \"" + "org.onap.policy.apex.service.engine."
                + "parameters.dummyclasses.SuperDooperCarrierTechnologyParameters"
                + "\", specify correct carrier technology parameter plugin " + "in parameter \"parameterClassName\"");
    }

    @Test
    void testWrongTypeCarrierTechnologyParClass() {
        final String[] args = {"-p", "src/test/resources/parameters/prodConsWrongTypeCTParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining(
            "error reading parameters from " + "\"src/test/resources/parameters/prodConsWrongTypeCTParClass.json\"\n")
            .hasMessageContaining("(ParameterRuntimeException):could not create default parameters for "
                + "carrier technology \"SUPER_DOOPER\"\n"
                + "class org.onap.policy.apex.service.engine.parameters.dummyclasses."
                + "SuperTokenDelimitedEventProtocolParameters cannot be cast to class "
                + "org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters (org.onap."
                + "policy.apex.service.engine.parameters.dummyclasses.SuperTokenDelimitedEventProtocolParameters "
                + "and org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters are in"
                + " unnamed module of loader 'app')");
    }

    @Test
    void testOkFileNameCarrierTechnology() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/prodConsOKFileName.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
        final FileCarrierTechnologyParameters fileParams = (FileCarrierTechnologyParameters) parameters
            .getEventOutputParameters().get("aProducer").getCarrierTechnologyParameters();
        assertTrue(fileParams.getFileName().endsWith("target/aaa.json"));
        assertFalse(fileParams.isStandardError());
        assertFalse(fileParams.isStandardIo());
        assertFalse(fileParams.isStreamingMode());

    }

    @Test
    void testBadFileNameCarrierTechnology() {
        final String[] args = {"-p", "src/test/resources/parameters/prodConsBadFileName.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("src/test/resources/parameters/prodConsBadFileName.json")
            .hasMessageContaining("ApexParameters")
            .hasMessageContaining("eventOutputParameters", "aProducer", "EventHandlerParameters",
                "FileCarrierTechnologyParameters", "fileName")
            .hasMessageContaining("is blank");
    }

    @Test
    void testBadEventProtocolParClass() {
        final String[] args = {"-p", "src/test/resources/parameters/prodConsBadEPParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from \"src/test/resources/parameters/prodConsBadEPParClass.json\"\n"
                + "(ParameterRuntimeException):event protocol \"SUPER_TOK_DEL\" "
                + "does not match plugin \"JSON\" in \"org.onap.policy.apex.service.engine.event.impl"
                + ".jsonprotocolplugin.JsonEventProtocolParameters"
                + "\", specify correct event protocol parameter plugin in parameter \"parameterClassName\"");
    }

    @Test
    void testNoEventProtocolParClass() {
        final String[] args = {"-p", "src/test/resources/parameters/prodConsNoEPParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from \"src/test/resources/parameters/prodConsNoEPParClass.json\"\n"
                + "(ParameterRuntimeException):event protocol \"SUPER_TOK_DEL\" parameter "
                + "\"parameterClassName\" value \"null\" invalid in JSON file");
    }

    @Test
    void testMismatchEventProtocolParClass() {
        final String[] args = {"-p", "src/test/resources/parameters/prodConsMismatchEPParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments)).hasMessage(
            "error reading parameters from " + "\"src/test/resources/parameters/prodConsMismatchEPParClass.json\"\n"
                + "(ParameterRuntimeException):event protocol \"SUPER_TOK_BEL\" "
                + "does not match plugin \"SUPER_TOK_DEL\" in "
                + "\"org.onap.policy.apex.service.engine.parameters.dummyclasses."
                + "SuperTokenDelimitedEventProtocolParameters\", "
                + "specify correct event protocol parameter plugin in parameter \"parameterClassName\"");
    }

    @Test
    void testWrongTypeEventProtocolParClass() {
        final String[] args = {"-p", "src/test/resources/parameters/prodConsWrongTypeEPParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("error reading parameters from "
                + "\"src/test/resources/parameters/prodConsWrongTypeEPParClass.json\"\n")
            .hasMessageContaining("(ParameterRuntimeException):could not create default parameters for event protocol "
                + "\"SUPER_TOK_DEL\"\nclass org.onap.policy.apex.service.engine."
                + "parameters.dummyclasses.SuperDooperCarrierTechnologyParameters "
                + "cannot be cast to class org.onap.policy.apex.service."
                + "parameters.eventprotocol.EventProtocolParameters ")
            .hasMessageContaining("org.onap.policy.apex.service.engine.parameters"
                + ".dummyclasses.SuperDooperCarrierTechnologyParameters and org.onap.policy.apex.service.parameters"
                + ".eventprotocol.EventProtocolParameters are in unnamed module of loader 'app')");
    }
}
