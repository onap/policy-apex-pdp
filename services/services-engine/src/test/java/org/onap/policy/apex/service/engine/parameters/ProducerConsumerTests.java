/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
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
public class ProducerConsumerTests {
    @Test
    public void testGoodParametersTest() {
        final String[] args = {"-c", "src/test/resources/parameters/goodParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

            assertEquals("MyApexEngine", parameters.getEngineServiceParameters().getName());
            assertEquals("0.0.1", parameters.getEngineServiceParameters().getVersion());
            assertEquals(45, parameters.getEngineServiceParameters().getId());
            assertEquals(19, parameters.getEngineServiceParameters().getInstanceCount());
            assertEquals(65522, parameters.getEngineServiceParameters().getDeploymentPort());
            assertEquals("FILE", parameters.getEventOutputParameters().get("FirstProducer")
                    .getCarrierTechnologyParameters().getLabel());
            assertEquals("JSON",
                    parameters.getEventOutputParameters().get("FirstProducer").getEventProtocolParameters().getLabel());
            assertEquals("FILE", parameters.getEventOutputParameters().get("MyOtherProducer")
                    .getCarrierTechnologyParameters().getLabel());
            assertEquals("JSON", parameters.getEventOutputParameters().get("MyOtherProducer")
                    .getEventProtocolParameters().getLabel());
            assertEquals("FILE", parameters.getEventInputParameters().get("TheFileConsumer1")
                    .getCarrierTechnologyParameters().getLabel());
            assertEquals("JSON", parameters.getEventInputParameters().get("TheFileConsumer1")
                    .getEventProtocolParameters().getLabel());
            assertEquals("SUPER_DOOPER", parameters.getEventInputParameters().get("MySuperDooperConsumer1")
                    .getCarrierTechnologyParameters().getLabel());
            assertEquals("SUPER_TOK_DEL", parameters.getEventInputParameters().get("MySuperDooperConsumer1")
                    .getEventProtocolParameters().getLabel());
        } catch (final ParameterException e) {
            fail("This test should not throw an exception");
        }
    }

    @Test
    public void testNoCarrierTechnology() {
        final String[] args = {"-c", "src/test/resources/parameters/prodConsNoCT.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("validation error(s) on parameters from \"src/test/resources/parameters/prodConsNoCT.json\"\n"
                    + "parameter group \"APEX_PARAMETERS\" type "
                    + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                    + "parameter group has status INVALID\n"
                    + "  parameter group map \"eventInputParameters\" INVALID, "
                    + "parameter group map has status INVALID\n" + "    parameter group \"aConsumer\" type "
                    + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID,"
                    + " parameter group has status INVALID\n" + "      parameter group \"UNDEFINED\" INVALID, "
                    + "event handler carrierTechnologyParameters not specified or blank\n", e.getMessage());
        }
    }

    @Test
    public void testNoEventProcol() {
        final String[] args = {"-c", "src/test/resources/parameters/prodConsNoEP.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("validation error(s) on parameters from \"src/test/resources/parameters/prodConsNoEP.json\"\n"
                    + "parameter group \"APEX_PARAMETERS\" type "
                    + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                    + "parameter group has status INVALID\n"
                    + "  parameter group map \"eventOutputParameters\" INVALID, "
                    + "parameter group map has status INVALID\n" + "    parameter group \"aProducer\" type "
                    + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" INVALID"
                    + ", parameter group has status INVALID\n" + "      parameter group \"UNDEFINED\" INVALID, "
                    + "event handler eventProtocolParameters not specified or blank\n"
                    + "  parameter group map \"eventInputParameters\" INVALID, "
                    + "parameter group map has status INVALID\n" + "    parameter group \"aConsumer\" type "
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
    public void testNoCarrierTechnologyParClass() {
        final String[] args = {"-c", "src/test/resources/parameters/prodConsNoCTParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from \"src/test/resources/parameters/prodConsNoCTParClass.json\"\n"
                    + "(ParameterRuntimeException):carrier technology \"SUPER_DOOPER\" "
                    + "parameter \"parameterClassName\" value \"null\" invalid in JSON file", e.getMessage());
        }
    }

    @Test
    public void testMismatchCarrierTechnologyParClass() {
        final String[] args = {"-c", "src/test/resources/parameters/prodConsMismatchCTParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from "
                    + "\"src/test/resources/parameters/prodConsMismatchCTParClass.json\"\n"
                    + "(ParameterRuntimeException):carrier technology \"SUPER_LOOPER\" "
                    + "does not match plugin \"SUPER_DOOPER\" in \"" + "org.onap.policy.apex.service.engine."
                    + "parameters.dummyclasses.SuperDooperCarrierTechnologyParameters"
                    + "\", specify correct carrier technology parameter plugin "
                    + "in parameter \"parameterClassName\"", e.getMessage());
        }
    }

    @Test
    public void testWrongTypeCarrierTechnologyParClass() {
        final String[] args = {"-c", "src/test/resources/parameters/prodConsWrongTypeCTParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from "
                    + "\"src/test/resources/parameters/prodConsWrongTypeCTParClass.json\"\n"
                    + "(ParameterRuntimeException):could not create default parameters for carrier technology "
                    + "\"SUPER_DOOPER\"\n" + "class org.onap.policy.apex.service.engine.parameters.dummyclasses."
                    + "SuperTokenDelimitedEventProtocolParameters cannot be cast to class "
                    + "org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters (org.onap."
                    + "policy.apex.service.engine.parameters.dummyclasses.SuperTokenDelimitedEventProtocolParameters "
                    + "and org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters are in"
                    + " unnamed module of loader 'app')", e.getMessage());
        }
    }

    @Test
    public void testOkFileNameCarrierTechnology() {
        final String[] args = {"-c", "src/test/resources/parameters/prodConsOKFileName.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
            final FileCarrierTechnologyParameters fileParams = (FileCarrierTechnologyParameters) parameters
                    .getEventOutputParameters().get("aProducer").getCarrierTechnologyParameters();
            assertTrue(fileParams.getFileName().endsWith("target/aaa.json"));
            assertEquals(false, fileParams.isStandardError());
            assertEquals(false, fileParams.isStandardIo());
            assertEquals(false, fileParams.isStreamingMode());
        } catch (final ParameterException e) {
            fail("This test should not throw an exception");
        }
    }

    @Test
    public void testBadFileNameCarrierTechnology() {
        final String[] args = {"-c", "src/test/resources/parameters/prodConsBadFileName.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("validation error(s) on parameters from "
                    + "\"src/test/resources/parameters/prodConsBadFileName.json\"\n"
                    + "parameter group \"APEX_PARAMETERS\" type "
                    + "\"org.onap.policy.apex.service.parameters.ApexParameters\" INVALID, "
                    + "parameter group has status INVALID\n"
                    + "  parameter group map \"eventOutputParameters\" INVALID, "
                    + "parameter group map has status INVALID\n" + "    parameter group \"aProducer\" type "
                    + "\"org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters\" "
                    + "INVALID, parameter group has status INVALID\n" + "      parameter group \"FILE\" type "
                    + "\"org.onap.policy.apex.service.engine.event.impl."
                    + "filecarrierplugin.FileCarrierTechnologyParameters\" INVALID, "
                    + "parameter group has status INVALID\n" + "        field \"fileName\" type "
                    + "\"java.lang.String\" value \"null\" INVALID, "
                    + "\"null\" invalid, must be specified as a non-empty string\n", e.getMessage());
        }
    }

    @Test
    public void testBadEventProtocolParClass() {
        final String[] args = {"-c", "src/test/resources/parameters/prodConsBadEPParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals(
                    "error reading parameters from \"src/test/resources/parameters/prodConsBadEPParClass.json\"\n"
                            + "(ParameterRuntimeException):event protocol \"SUPER_TOK_DEL\" "
                            + "does not match plugin \"JSON\" in \"org.onap.policy.apex.service.engine.event.impl"
                            + ".jsonprotocolplugin.JsonEventProtocolParameters"
                            + "\", specify correct event protocol parameter plugin in parameter \"parameterClassName\"",
                    e.getMessage());
        }
    }

    @Test
    public void testNoEventProtocolParClass() {
        final String[] args = {"-c", "src/test/resources/parameters/prodConsNoEPParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from \"src/test/resources/parameters/prodConsNoEPParClass.json\"\n"
                    + "(ParameterRuntimeException):event protocol \"SUPER_TOK_DEL\" parameter "
                    + "\"parameterClassName\" value \"null\" invalid in JSON file", e.getMessage());
        }
    }

    @Test
    public void testMismatchEventProtocolParClass() {
        final String[] args = {"-c", "src/test/resources/parameters/prodConsMismatchEPParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals(
                    "error reading parameters from "
                            + "\"src/test/resources/parameters/prodConsMismatchEPParClass.json\"\n"
                            + "(ParameterRuntimeException):event protocol \"SUPER_TOK_BEL\" "
                            + "does not match plugin \"SUPER_TOK_DEL\" in "
                            + "\"org.onap.policy.apex.service.engine.parameters.dummyclasses."
                            + "SuperTokenDelimitedEventProtocolParameters\", "
                            + "specify correct event protocol parameter plugin in parameter \"parameterClassName\"",
                    e.getMessage());
        }
    }

    @Test
    public void testWrongTypeEventProtocolParClass() {
        final String[] args = {"-c", "src/test/resources/parameters/prodConsWrongTypeEPParClass.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from "
                    + "\"src/test/resources/parameters/prodConsWrongTypeEPParClass.json\"\n"
                    + "(ParameterRuntimeException):could not create default parameters for event protocol "
                    + "\"SUPER_TOK_DEL\"\n" + "class org.onap.policy.apex.service.engine."
                    + "parameters.dummyclasses.SuperDooperCarrierTechnologyParameters "
                    + "cannot be cast to class org.onap.policy.apex.service."
                    + "parameters.eventprotocol.EventProtocolParameters (org.onap.policy.apex.service.engine.parameters"
                    + ".dummyclasses.SuperDooperCarrierTechnologyParameters and org.onap.policy.apex.service.parameters"
                    + ".eventprotocol.EventProtocolParameters are in unnamed module of loader 'app')", e.getMessage());
        }
    }
}
