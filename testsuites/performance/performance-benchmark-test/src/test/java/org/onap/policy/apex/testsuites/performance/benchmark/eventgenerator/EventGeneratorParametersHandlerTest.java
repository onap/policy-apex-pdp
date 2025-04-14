/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 20242025 OpenInfra Foundation Europe. All rights reserved.
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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

/**
 * Test event generator parameters.
 */
class EventGeneratorParametersHandlerTest {

    @Test
    void testEventGeneratorParameterHandler() throws ParseException {
        EventGeneratorParameterHandler handler = new EventGeneratorParameterHandler();
        assertNotNull(handler);

        assertValidInputs(handler);

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-c", "src/test/resources/parameters/unit/Default.json", "-bc", "-1"};
            handler.parse(arguments);
        }).hasMessageContaining("specified parameters are not valid", "EventGeneratorParameters",
            "\"batchCount\" value \"-1\" INVALID, is below the minimum");

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-c", "src/test/resources/parameters/unit/Default.json", "-bs", "0"};
            handler.parse(arguments);
        }).hasMessageContaining("specified parameters are not valid", "EventGeneratorParameters",
            "\"batchSize\" value \"0\" INVALID, is below the minimum");


        String[] args = new String[] {"-c", "src/test/resources/parameters/unit/Default.json", "-bd", "1000"};
        EventGeneratorParameters parameters = handler.parse(args);
        assertEquals(1000, parameters.getDelayBetweenBatches());

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-c", "src/test/resources/parameters/unit/Default.json", "-bd", "-1"};
            handler.parse(arguments);
        }).hasMessageContaining("specified parameters are not valid", "EventGeneratorParameters",
            "\"batchSize\" value \"1\" INVALID, is below the minimum");

        args = new String[] {"-c", "src/test/resources/parameters/unit/Default.json", "-o", "Zooby"};
        parameters = handler.parse(args);
        assertEquals("Zooby", parameters.getOutFile());

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-z"};
            handler.parse(arguments);
        }).hasMessage("Unrecognized option: -z");

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-H"};
            handler.parse(arguments);
        }).hasMessage("Missing argument for option: H");

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-p"};
            handler.parse(arguments);
        }).hasMessage("Missing argument for option: p");

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-p", "12345", "-z"};
            handler.parse(arguments);
        }).hasMessage("Unrecognized option: -z");

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-p", "12345", "somethingElse"};
            handler.parse(arguments);
        }).hasMessage("too many command line arguments specified : [somethingElse]");

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-c"};
            handler.parse(arguments);
        }).hasMessage("Missing argument for option: c");

        args = new String[] {"-H", "MyHost", "-c", "src/test/resources/parameters/unit/Valid.json"};
        parameters = handler.parse(args);
        assertEquals("MyHost", parameters.getHost());

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-c", "src/test/resources/parameters/unit/NonExistant.json"};
            handler.parse(arguments);
        }).hasMessageStartingWith("Could not read parameters from configuration file ");

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-c", "src/test/resources/parameters/unit/BadHost.json"};
            handler.parse(arguments);
        }).hasMessageContaining("Error parsing JSON parameters from configuration file "
            + "\"src/test/resources/parameters/unit/BadHost.json\": "
            + "com.google.gson.stream.MalformedJsonException: "
            + "Unexpected value at line 3 column 14 path $.host");

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-c", "src/test/resources/parameters/unit/BadPort.json"};
            handler.parse(arguments);
        }).hasMessageContaining("Error parsing JSON parameters from configuration file "
            + "\"src/test/resources/parameters/unit/BadPort.json\": "
            + "java.lang.IllegalStateException: Expected an int "
            + "but was BOOLEAN at line 4 column 18 path $.port");

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-c", "src/test/resources/parameters/unit/Empty.json"};
            handler.parse(arguments);
        }).hasMessageContaining("No parameters found in configuration file "
            + "\"src/test/resources/parameters/unit/Empty.json\"");

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-c", "src/test/resources/parameters/unit/NullHost.json"};
            handler.parse(arguments);
        }).hasMessageContaining("specified parameters are not valid", "EventGeneratorParameters",
            "\"host\" value \"null\" INVALID, is null");

        assertThatThrownBy(() -> {
            String[] arguments = new String[] {"-p", "1023"};
            handler.parse(arguments);
        }).hasMessageContaining("specified parameters are not valid", "EventGeneratorParameters",
            "\"port\" value \"1023\" INVALID, is below the minimum");
    }

    private static void assertValidInputs(EventGeneratorParameterHandler handler) throws ParseException {
        String[] args = {"-h"};
        EventGeneratorParameters parameters = handler.parse(args);
        assertNull(parameters);
        assertEquals("usage: EventGenerator [options...]",
            handler.getHelp(EventGenerator.class.getSimpleName()).substring(0, 34));

        args = new String[] {};
        parameters = handler.parse(args);
        assertEquals("localhost", parameters.getHost());
        assertEquals(32801, parameters.getPort());

        args = new String[] {"-H", "MyHost"};
        parameters = handler.parse(args);
        assertEquals("MyHost", parameters.getHost());

        args = new String[] {"-p", "12345"};
        parameters = handler.parse(args);
        assertEquals(12345, parameters.getPort());


        args = new String[] {"-H", "MyHost", "-p", "12345"};
        parameters = handler.parse(args);
        assertEquals("MyHost", parameters.getHost());
        assertEquals(12345, parameters.getPort());

        args = new String[] {"-c", "src/test/resources/parameters/unit/Valid.json"};
        parameters = handler.parse(args);
        assertEquals("ValidPars", parameters.getName());
        assertEquals("FileHost", parameters.getHost());
        assertEquals(54321, parameters.getPort());

        args = new String[] {"-c", "src/test/resources/parameters/unit/Default.json"};
        parameters = handler.parse(args);
        assertEquals("localhost", parameters.getHost());
        assertEquals(32801, parameters.getPort());

        args = new String[] {"-c", "src/test/resources/parameters/unit/Default.json", "-bc", "100"};
        parameters = handler.parse(args);
        assertEquals(100, parameters.getBatchCount());

        args = new String[] {"-c", "src/test/resources/parameters/unit/Default.json", "-bs", "12345"};
        parameters = handler.parse(args);
        assertEquals(12345, parameters.getBatchSize());
    }
}
