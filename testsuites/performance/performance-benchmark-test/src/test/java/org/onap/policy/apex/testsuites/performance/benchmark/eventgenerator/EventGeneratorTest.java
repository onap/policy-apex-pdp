/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 Bell Canada. All rights reserved.
 *  Modifications Copyright (C) 2024 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;


/**
 * This class tests the event generator.
 */
class EventGeneratorTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final PrintStream stdout = System.out;

    /**
     * Test event generation.
     *
     * @throws ApexException on Apex exceptions
     */
    @Test
    void testEventGeneration() throws ApexException {
        EventGeneratorParameters pars = new EventGeneratorParameters();
        pars.setBatchCount(1);
        pars.setBatchSize(10);

        EventGenerator eventGenerator = new EventGenerator(pars);
        final String[] args = {"-rfr", "target", "-p",
            "target/examples/config/SampleDomain/REST2RESTJsonEventJavascript.json"};

        final ApexMain apexMain = new ApexMain(args);

        while (!eventGenerator.isFinished()) {
            ThreadUtilities.sleep(200);
        }

        apexMain.shutdown();

        ThreadUtilities.sleep(5000);
        eventGenerator.tearDown();

        assertTrue(eventGenerator.getEventGenerationStats().contains("\"apexClient\": \"TOTAL\""));
    }

    @Test
    void testEventGeneratorBadParams() {
        System.setOut(new PrintStream(outContent));

        final String[] args = {"-zzz"};

        EventGenerator.main(args);

        final String outString = outContent.toString();

        System.setOut(stdout);

        assertTrue(outString.contains("Start of event generator failed: Unrecognized option: -zzz"));
    }

    @Test
    void testEventGeneratorHelp() {
        System.setOut(new PrintStream(outContent));

        final String[] args = {"-h"};

        EventGenerator.main(args);

        final String outString = outContent.toString();

        System.setOut(stdout);

        assertTrue(outString.contains("outputs the usage of this command"));
    }

    @Test
    void testEventGeneratorStart() {

        System.setOut(new PrintStream(outContent));

        (new Thread(() -> EventGenerator.main(null))).start();

        ThreadUtilities.sleep(1000);
        final String outString = outContent.toString();

        System.setOut(stdout);

        assertTrue(outString.contains("Event generator started"));
        assertTrue(outString.contains("Event generator shut down"));
    }

    @Test
    void testEventGeneratorOutfileGood() {
        EventGeneratorParameters pars = new EventGeneratorParameters();
        pars.setOutFile("target/statsOutFile.json");

        EventGenerator generator = new EventGenerator(pars);
        assertNotNull(generator);

        generator.tearDown();

        File outFile = new File("target/statsOutFile.json");
        assertTrue(outFile.exists());
        assertTrue(outFile.delete());
    }

    @Test
    void testEventGeneratorOutfileBad() {
        EventGeneratorParameters pars = new EventGeneratorParameters();
        pars.setOutFile("/I/Dont/Exist\0");

        EventGenerator generator = new EventGenerator(pars);
        assertNotNull(generator);

        System.setOut(new PrintStream(outContent));

        generator.tearDown();

        final String outString = outContent.toString();
        System.setOut(stdout);
        assertThat(outString).contains("could not output statistics to file \"/I/Dont/Exist\0\"");
    }
}
