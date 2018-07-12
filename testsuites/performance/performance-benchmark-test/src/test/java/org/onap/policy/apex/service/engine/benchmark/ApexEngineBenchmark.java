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

package org.onap.policy.apex.service.engine.benchmark;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.utils.Utils;
import org.onap.policy.apex.test.common.model.EvalDomainModelFactory;
import org.python.icu.impl.Assert;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ApexEngineBenchmark.
 */
public class ApexEngineBenchmark {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexEngineBenchmark.class);

    private static final String TARGET = "apex";
    private static final String SOURCE = "test";
    private static final String NAME = "Event0000";
    private static final String VERSION = "0.0.1";
    private static final String PACKAGE = "org.onap.policy.apex.domains.sample.events";

    private static final long TIME_OUT_IN_MILLISEC = TimeUnit.MINUTES.toMillis(1);

    private String apexECAModelString;
    private String apexOODAModelString;

    @Before
    public void setUp() throws Exception {
        apexECAModelString = Utils.getModelString(new EvalDomainModelFactory().getECAPolicyModel());
        apexOODAModelString = Utils.getModelString(new EvalDomainModelFactory().getOODAPolicyModel());
    }

    @Test
    public void testBenchmark_SingletonWorker() throws Exception {
        executeTest(apexECAModelString, 100, 1, 20);
        executeTest(apexOODAModelString, 100, 1, 20);
    }

    @Test
    public void testBenchmark_3ThreadWorker() throws Exception {
        executeTest(apexECAModelString, 1000, 3, 10);
        executeTest(apexOODAModelString, 100, 3, 10);
    }

    @Test
    public void testBenchmark_10ThreadWorker() throws Exception {
        executeTest(apexECAModelString, 2000, 10, 10);
        executeTest(apexOODAModelString, 2000, 10, 10);
    }

    @Test
    public void testBenchmark_50ThreadWorker() throws Exception {
        executeTest(apexECAModelString, 3000, 50, 10);
        executeTest(apexOODAModelString, 3000, 50, 10);
    }

    @Test
    public void TestE_AvailableProcessorsThreadWorker() throws Exception {
        final int cores = Runtime.getRuntime().availableProcessors();
        executeTest(apexECAModelString, 3000, cores, 10);
        executeTest(apexOODAModelString, 3000, cores, 10);
    }

    private void executeTest(final String policyModel, final int eventsCount, final int threads, final int loop)
            throws Exception {

        LOGGER.info("Running Test with Event count: {}, Instance count: {} and loop: {}", eventsCount, threads, loop);
        final TestApexEventListener apexEventListener = new TestApexEventListener();

        final ApexBaseBenchMarkTest apexBaseBenchMarkTest =
                new ApexBaseBenchMarkTest(policyModel, threads, apexEventListener);

        try {
            apexBaseBenchMarkTest.setUp();
            for (int i = 0; i < loop; i++) {
                sendEvents(apexBaseBenchMarkTest, eventsCount);
                final long currentTimeInMillSec = System.currentTimeMillis();
                while (apexEventListener.getEventReceived() < eventsCount) {
                    if (System.currentTimeMillis() - currentTimeInMillSec > TIME_OUT_IN_MILLISEC) {
                        LOGGER.warn("Wait timed out ... ");
                        break;
                    }
                    ThreadUtilities.sleep(500);
                }
                assertEquals(eventsCount, apexEventListener.getEventReceived());
                apexEventListener.printResult();
                apexEventListener.reset();
            }
        } catch (final Exception exception) {
            Assert.fail(exception);
        } finally {
            apexBaseBenchMarkTest.destroy();
            LOGGER.info("Finished Running Test with Event count: {}, Instance count: {} and loop: {}", eventsCount,
                    threads, loop);
        }
    }

    public void sendEvents(final ApexBaseBenchMarkTest apexBaseBenchMarkTest, final int eventsCount) throws Exception {
        for (int eventNum = 0; eventNum < eventsCount; eventNum++) {
            final long currentTimeMillis = System.currentTimeMillis();
            final ApexEvent event = new ApexEvent(NAME, VERSION, PACKAGE, SOURCE, TARGET);
            event.put("TestTemperature", eventNum);
            event.put("FirstEventTimestamp", currentTimeMillis);
            event.put("SentTimestamp", currentTimeMillis);
            event.put("EventNumber", eventNum);
            apexBaseBenchMarkTest.sendEvent(event);
        }
    }
}
