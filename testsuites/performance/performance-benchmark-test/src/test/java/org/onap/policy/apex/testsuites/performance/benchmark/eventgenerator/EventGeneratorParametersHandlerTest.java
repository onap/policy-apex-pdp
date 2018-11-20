/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.EventGenerator;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.EventGeneratorParameterHandler;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.EventGeneratorParameters;

/**
 * Test event generator parameters.
 */
public class EventGeneratorParametersHandlerTest {

    @Test
    public void testEventGeneratorParameterhandler() {
        EventGeneratorParameterHandler handler = new EventGeneratorParameterHandler();
        assertNotNull(handler);

        try {
            String[] args =
                { "-h" };
            EventGeneratorParameters parameters = handler.parse(args);
            assertNull(parameters);
            assertEquals("usage: EventGenerator [options...]",
                            handler.getHelp(EventGenerator.class.getSimpleName()).substring(0, 34));
        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                {};
            EventGeneratorParameters parameters = handler.parse(args);
            assertEquals("localhost", parameters.getHost());
            assertEquals(32801, parameters.getPort());
        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                { "-H", "MyHost" };
            EventGeneratorParameters parameters = handler.parse(args);
            assertEquals("MyHost", parameters.getHost());
        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                { "-p", "12345" };
            EventGeneratorParameters parameters = handler.parse(args);
            assertEquals(12345, parameters.getPort());
        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                { "-H", "MyHost", "-p", "12345" };
            EventGeneratorParameters parameters = handler.parse(args);
            assertEquals("MyHost", parameters.getHost());
            assertEquals(12345, parameters.getPort());
        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/Valid.json" };
            EventGeneratorParameters parameters = handler.parse(args);
            assertEquals("ValidPars", parameters.getName());
            assertEquals("FileHost", parameters.getHost());
            assertEquals(54321, parameters.getPort());
        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/Default.json" };
            EventGeneratorParameters parameters = handler.parse(args);
            assertEquals("localhost", parameters.getHost());
            assertEquals(32801, parameters.getPort());
        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/Default.json", "-bc", "100" };
            EventGeneratorParameters parameters = handler.parse(args);
            assertEquals(100, parameters.getBatchCount());
        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/Default.json", "-bc", "-1" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("specified parameters are not valid: parameter group \"EventGeneratorParameters\" "
                            + "type \"org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator."
                            + "EventGeneratorParameters\" INVALID, parameter group has status INVALID\n"
                            + "  field \"batchCount\" type \"int\" value \"-1\" INVALID, "
                            + "batchCount must be an integer with a value of zero or more, "
                            + "zero means generate batches forever\n", pe.getMessage());
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/Default.json", "-bs", "12345" };
            EventGeneratorParameters parameters = handler.parse(args);
            assertEquals(12345, parameters.getBatchSize());
        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/Default.json", "-bs", "0" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("specified parameters are not valid: parameter group \"EventGeneratorParameters\" "
                            + "type \"org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator."
                            + "EventGeneratorParameters\" INVALID, parameter group has status INVALID\n"
                            + "  field \"batchSize\" type \"int\" value \"0\" INVALID, "
                            + "batchSize must be an integer greater than zero\n", pe.getMessage());
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/Default.json", "-bd", "1000" };
            EventGeneratorParameters parameters = handler.parse(args);
            assertEquals(1000, parameters.getDelayBetweenBatches());
        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/Default.json", "-bd", "-1" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("specified parameters are not valid: parameter group \"EventGeneratorParameters\" "
                            + "type \"org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator."
                            + "EventGeneratorParameters\" INVALID, parameter group has status INVALID\n"
                            + "  field \"batchSize\" type \"int\" value \"1\" INVALID, "
                            + "batchSize must be an integer with a value of zero or more\n", pe.getMessage());
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/Default.json", "-o", "Zooby" };
            EventGeneratorParameters parameters = handler.parse(args);
            assertEquals("Zooby", parameters.getOutFile());
        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                { "-z" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("Unrecognized option: -z", pe.getMessage());
        }

        try {
            String[] args =
                { "-H" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("Missing argument for option: H", pe.getMessage());
        }

        try {
            String[] args =
                { "-p" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("Missing argument for option: p", pe.getMessage());
        }

        try {
            String[] args =
                { "-p", "12345", "-z" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("Unrecognized option: -z", pe.getMessage());
        }

        try {
            String[] args =
                { "-p", "12345", "somethingElse" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("too many command line arguments specified : [somethingElse]", pe.getMessage());
        }

        try {
            String[] args =
                { "-c" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("Missing argument for option: c", pe.getMessage());
        }

        try {
            String[] args =
                { "-H", "MyHost", "-c", "src/test/resources/parameters/unit/Valid.json" };
            EventGeneratorParameters pars = handler.parse(args);
            assertEquals("MyHost", pars.getHost());

        } catch (ParseException pe) {
            fail("test should not throw an exception");
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/NonExistant.json" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("Could not read parameters from configuration file "
                            + "\"src/test/resources/parameters/unit/NonExistant.json\": "
                            + "src/test/resources/parameters/unit/NonExistant.json", pe.getMessage());
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/BadHost.json" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("Error parsing JSON parameters from configuration file "
                            + "\"src/test/resources/parameters/unit/BadHost.json\": "
                            + "com.google.gson.stream.MalformedJsonException: "
                            + "Unexpected value at line 3 column 14 path $.host", pe.getMessage());
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/BadPort.json" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("Error parsing JSON parameters from configuration file "
                            + "\"src/test/resources/parameters/unit/BadPort.json\": "
                            + "java.lang.IllegalStateException: Expected an int "
                            + "but was BOOLEAN at line 4 column 18 path $.port", pe.getMessage());
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/Empty.json" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("No parameters found in configuration file "
                            + "\"src/test/resources/parameters/unit/Empty.json\"", pe.getMessage());
        }

        try {
            String[] args =
                { "-c", "src/test/resources/parameters/unit/NullHost.json" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("specified parameters are not valid: parameter group \"ValidPars\" "
                            + "type \"org.onap.policy.apex.testsuites.performance."
                            + "benchmark.eventgenerator.EventGeneratorParameters\" INVALID, "
                            + "parameter group has status INVALID\n" + "  field \"host\" type \"java.lang.String\" "
                            + "value \"null\" INVALID, host must be a non-blank string\n", pe.getMessage());
        }

        try {
            String[] args =
                { "-p", "1023" };
            handler.parse(args);
            fail("test should throw an exception");
        } catch (ParseException pe) {
            assertEquals("specified parameters are not valid: parameter group \""
                            + "EventGeneratorParameters\" type \"org.onap.policy.apex.testsuites.performance.benchmark."
                            + "eventgenerator.EventGeneratorParameters\" INVALID, parameter group has status INVALID\n"
                            + "  field \"port\" type \"int\" value \"1023\" INVALID, "
                            + "port must be an integer between 1024 and 65535 inclusive\n" + "", pe.getMessage());
        }
    }
}
