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

package org.onap.policy.apex.testsuites.performance.benchmark.engine.benchmark;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.testsuites.integration.common.model.EvalDomainModelFactory;
import org.onap.policy.apex.testsuites.performance.benchmark.engine.utils.Utils;
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

    private String apexEcaModelString;
    private String apexOodaModelString;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        apexEcaModelString = Utils.getModelString(new EvalDomainModelFactory().getEcaPolicyModel());
        apexOodaModelString = Utils.getModelString(new EvalDomainModelFactory().getOodaPolicyModel());
    }

    /**
     * Test benchmark singleton worker.
     *
     * @throws Exception the exception
     */
    @Test
    public void testBenchmark_SingletonWorker() throws Exception {
        executeTest(apexEcaModelString, 100, 1, 20);
        executeTest(apexOodaModelString, 100, 1, 20);
    }

    /**
     * Test benchmark 3 thread worker.
     *
     * @throws Exception the exception
     */
    @Test
    public void testBenchmark_3ThreadWorker() throws Exception {
        executeTest(apexEcaModelString, 1000, 3, 10);
        executeTest(apexOodaModelString, 100, 3, 10);
    }

    /**
     * Test benchmark 10 thread worker.
     *
     * @throws Exception the exception
     */
    @Test
    public void testBenchmark_10ThreadWorker() throws Exception {
        executeTest(apexEcaModelString, 2000, 10, 10);
        executeTest(apexOodaModelString, 2000, 10, 10);
    }

    /**
     * Test benchmark 50 thread worker.
     *
     * @throws Exception the exception
     */
    @Test
    public void testBenchmark_50ThreadWorker() throws Exception {
        executeTest(apexEcaModelString, 3000, 50, 10);
        executeTest(apexOodaModelString, 3000, 50, 10);
    }

    /**
     * Test available processors thread worker.
     *
     * @throws Exception the exception
     */
    @Test
    public void testAvailableProcessorsThreadWorker() throws Exception {
        final int cores = Runtime.getRuntime().availableProcessors();
        executeTest(apexEcaModelString, 3000, cores, 10);
        executeTest(apexOodaModelString, 3000, cores, 10);
    }

    /**
     * Execute test.
     *
     * @param policyModel the policy model
     * @param eventsCount the events count
     * @param threads the threads
     * @param loop the loop
     * @throws Exception the exception
     */
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

    /**
     * Send events.
     *
     * @param apexBaseBenchMarkTest the apex base bench mark test
     * @param eventsCount the events count
     * @throws Exception the exception
     */
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
