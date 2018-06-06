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

package org.onap.policy.apex.service.engine.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestApexCommandLineArguments {

    @Test
    public void testCommandLineArguments() {
        final ApexCommandLineArguments apexArguments = new ApexCommandLineArguments();

        final String[] args00 = {""};
        try {
            apexArguments.parse(args00);
            apexArguments.validate();
            fail("Test should throw an exception here");
        } catch (final ApexException e) {
            assertEquals("Apex configuration file was not specified as an argument", e.getMessage());
        }

        final String[] args01 = {"-h"};
        try {
            final String result = apexArguments.parse(args01);
            assertTrue(result.startsWith("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));
        } catch (final ApexException e) {
            e.printStackTrace();
            fail("Test should not throw an exception");
        }

        final String[] args02 = {"-v"};
        try {
            final String result = apexArguments.parse(args02);
            assertTrue(result.startsWith("Apex Adaptive Policy Engine"));
        } catch (final ApexException e) {
            e.printStackTrace();
            fail("Test should not throw an exception");
        }

        final String[] args03 = {"-v", "-h"};
        try {
            final String result = apexArguments.parse(args03);
            assertTrue(result.startsWith("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));
        } catch (final ApexException e) {
            e.printStackTrace();
            fail("Test should not throw an exception");
        }

        final String[] args04 = {"-h", "-v"};
        try {
            final String result = apexArguments.parse(args04);
            assertTrue(result.startsWith("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));
        } catch (final ApexException e) {
            e.printStackTrace();
            fail("Test should not throw an exception");
        }

        final String[] args05 = {"-a"};
        try {
            apexArguments.parse(args05);
        } catch (final ApexException e) {
            assertEquals("invalid command line arguments specified : Unrecognized option: -a", e.getMessage());
        }

        final String[] args06 = {"-c", "hello", "-m", "goodbye", "-h", "-v"};
        try {
            final String result = apexArguments.parse(args06);
            assertTrue(result.startsWith("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));
        } catch (final ApexException e) {
            assertEquals("invalid command line arguments specified : Unrecognized option: -a", e.getMessage());
        }

        final String[] args07 = {"-c", "hello", "-m", "goodbye", "-h", "aaa"};
        try {
            final String result = apexArguments.parse(args07);
            assertTrue(result.startsWith("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));
        } catch (final ApexException e) {
            assertEquals("too many command line arguments specified : [-c, hello, -m, goodbye, -h, aaa]",
                    e.getMessage());
        }
    }

    @Test
    public void testCommandLineFileParameters() {
        final ApexCommandLineArguments apexArguments = new ApexCommandLineArguments();

        final String[] args00 = {"-c", "zooby"};
        try {
            apexArguments.parse(args00);
            apexArguments.validate();
            fail("Test should throw an exception here");
        } catch (final ApexException e) {
            assertEquals("Apex configuration file \"zooby\" does not exist", e.getMessage());
        }

        final String[] args01 = {"-c"};
        try {
            apexArguments.parse(args01);
            apexArguments.validate();
            fail("Test should throw an exception here");
        } catch (final ApexException e) {
            assertEquals("invalid command line arguments specified : Missing argument for option: c", e.getMessage());
        }

        final String[] args02 = {"-c", "src/test/resources/parameters/goodParams.json"};
        try {
            apexArguments.parse(args02);
            apexArguments.validate();
        } catch (final ApexException e) {
            e.printStackTrace();
            fail("Test should not throw an exception");
        }

        final String[] args03 = {"-c", "src/test/resources/parameters/goodParams.json", "-m", "zooby"};
        try {
            apexArguments.parse(args03);
            apexArguments.validate();
            fail("Test should throw an exception here");
        } catch (final ApexException e) {
            assertEquals("Apex model file \"zooby\" does not exist", e.getMessage());
        }

        final String[] args04 = {"-m"};
        try {
            apexArguments.parse(args04);
            apexArguments.validate();
            fail("Test should throw an exception here");
        } catch (final ApexException e) {
            assertEquals("invalid command line arguments specified : Missing argument for option: m", e.getMessage());
        }

        final String[] args05 = {"-c", "src/test/resources/parameters/goodParams.json", "-m"};
        try {
            apexArguments.parse(args05);
            apexArguments.validate();
            fail("Test should throw an exception here");
        } catch (final ApexException e) {
            assertEquals("invalid command line arguments specified : Missing argument for option: m", e.getMessage());
        }

        final String[] args06 = {"-c", "src/test/resources/parameters/goodParams.json", "-m",
                "src/test/resources/main/DummyModelFile.json"};
        try {
            apexArguments.parse(args06);
            apexArguments.validate();
        } catch (final ApexException e) {
            e.printStackTrace();
            fail("Test should not throw an exception");
        }

        final String[] args07 = {"-c", "parameters/goodParams.json", "-m", "main/DummyModelFile.json"};
        try {
            apexArguments.parse(args07);
            apexArguments.validate();
        } catch (final ApexException e) {
            e.printStackTrace();
            fail("Test should not throw an exception");
        }
    }
}
