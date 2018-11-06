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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator;

import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.EventGenerator;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.EventGeneratorParameters;

/**
 * This class teste the event generator.
 */
public class EventGeneratorTest {
    /**
     * Test event generation.
     * @throws ApexException on Apex exceptions
     */
    @Test
    public void testEventGeneration() throws ApexException {
        EventGeneratorParameters pars = new EventGeneratorParameters();
        pars.setBatchCount(1);
        pars.setBatchSize(2);

        EventGenerator eventGenerator = new EventGenerator(pars);
        
        final String[] args =
            { "src/test/resources/prodcons/Rest2Rest.json" };
        final ApexMain apexMain = new ApexMain(args);

        while (!eventGenerator.isFinshed()) {
            ThreadUtilities.sleep(200);
        }
        
        System.err.println("Event generator statistics\n" + eventGenerator.getEventGenerationStats());

        apexMain.shutdown();
        eventGenerator.tearDown();
    }
}
