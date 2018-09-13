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

package org.onap.policy.apex.service.engine.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.plugins.executor.mvel.MvelExecutorParameters;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.runtime.impl.EngineServiceImpl;
import org.onap.policy.apex.service.engine.utils.Utils;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.onap.policy.apex.test.common.model.SampleDomainModelFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ApexServiceTest.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexServiceTest {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexServiceTest.class);

    private static final long MAX_STOP_WAIT = 5000; // 5 sec
    private static final long MAX_START_WAIT = 5000; // 5 sec
    private static final long MAX_RECV_WAIT = 5000; // 5 sec

    private static final AxArtifactKey engineServiceKey = new AxArtifactKey("Machine-1_process-1_engine-1", "0.0.0");
    private static final EngineServiceParameters parameters = new EngineServiceParameters();
    private static EngineService service = null;
    private static TestListener listener = null;
    private static AxPolicyModel apexPolicyModel = null;
    private static int actionEventsReceived = 0;

    private static String apexModelString;

    private boolean waitFlag = true;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        // create engine with 3 threads
        parameters.setInstanceCount(3);
        parameters.setName(engineServiceKey.getName());
        parameters.setVersion(engineServiceKey.getVersion());
        parameters.setId(100);
        parameters.getEngineParameters().getExecutorParameterMap().put("MVEL", new MvelExecutorParameters());
        service = EngineServiceImpl.create(parameters);


        LOGGER.debug("Running TestApexEngine. . .");

        apexPolicyModel = new SampleDomainModelFactory().getSamplePolicyModel("MVEL");
        assertNotNull(apexPolicyModel);

        apexModelString = Utils.getModelString(apexPolicyModel);

        // create engine
        listener = new TestListener();
        service.registerActionListener("Listener", listener);
    }

    /**
     * Update the engine then test the engine with 2 sample events.
     *
     * @throws ApexException if there is a problem
     */
    @Test
    public void testExecutionSet1() throws ApexException {
        service.updateModel(parameters.getEngineKey(), apexModelString, true);
        // Start the service
        service.startAll();
        final long starttime = System.currentTimeMillis();
        for (final AxArtifactKey engineKey : service.getEngineKeys()) {
            LOGGER.info("{}", service.getStatus(engineKey));
        }
        while (!service.isStarted() && System.currentTimeMillis() - starttime < MAX_START_WAIT) {
            ThreadUtilities.sleep(200);
        }
        if (!service.isStarted()) {
            fail("Apex Service " + service.getKey() + " failed to start after " + MAX_START_WAIT + " ms");
        }

        final EngineServiceEventInterface engineServiceEventInterface = service.getEngineServiceEventInterface();

        // Send some events
        final Date testStartTime = new Date();
        final Map<String, Object> eventDataMap = new HashMap<String, Object>();
        eventDataMap.put("TestSlogan", "This is a test slogan");
        eventDataMap.put("TestMatchCase", (byte) 123);
        eventDataMap.put("TestTimestamp", testStartTime.getTime());
        eventDataMap.put("TestTemperature", 34.5445667);

        final ApexEvent event =
                new ApexEvent("Event0000", "0.0.1", "org.onap.policy.apex.domains.sample.events", "test", "apex");
        event.setExecutionId(System.nanoTime());
        event.putAll(eventDataMap);
        engineServiceEventInterface.sendEvent(event);

        final ApexEvent event2 =
                new ApexEvent("Event0100", "0.0.1", "org.onap.policy.apex.domains.sample.events", "test", "apex");
        event2.setExecutionId(System.nanoTime());
        event2.putAll(eventDataMap);
        engineServiceEventInterface.sendEvent(event2);

        // Wait for results
        final long recvtime = System.currentTimeMillis();
        while (actionEventsReceived < 2 && System.currentTimeMillis() - recvtime < MAX_RECV_WAIT) {
            ThreadUtilities.sleep(100);
        }
        ThreadUtilities.sleep(500);
        assertEquals(2, actionEventsReceived);
        actionEventsReceived = 0;


        // Stop all engines on this engine service
        final long stoptime = System.currentTimeMillis();
        service.stop();
        while (!service.isStopped() && System.currentTimeMillis() - stoptime < MAX_STOP_WAIT) {
            ThreadUtilities.sleep(200);
        }
        if (!service.isStopped()) {
            fail("Apex Service " + service.getKey() + " failed to stop after " + MAX_STOP_WAIT + " ms");
        }
    }

    /**
     * Update the engine then test the engine with 2 sample events.
     *
     * @throws ApexException if there is a problem
     */
    @Test
    public void testExecutionSet1Sync() throws ApexException {
        service.updateModel(parameters.getEngineKey(), apexModelString, true);
        // Start the service
        service.startAll();
        final long starttime = System.currentTimeMillis();
        for (final AxArtifactKey engineKey : service.getEngineKeys()) {
            LOGGER.info("{}", service.getStatus(engineKey));
        }
        while (!service.isStarted() && System.currentTimeMillis() - starttime < MAX_START_WAIT) {
            ThreadUtilities.sleep(200);
        }
        if (!service.isStarted()) {
            fail("Apex Service " + service.getKey() + " failed to start after " + MAX_START_WAIT + " ms");
        }

        // Send some events
        final Date testStartTime = new Date();
        final Map<String, Object> eventDataMap = new HashMap<String, Object>();
        eventDataMap.put("TestSlogan", "This is a test slogan");
        eventDataMap.put("TestMatchCase", (byte) 123);
        eventDataMap.put("TestTimestamp", testStartTime.getTime());
        eventDataMap.put("TestTemperature", 34.5445667);

        final ApexEvent event1 =
                new ApexEvent("Event0000", "0.0.1", "org.onap.policy.apex.domains.sample.events", "test", "apex");
        event1.putAll(eventDataMap);
        event1.setExecutionId(System.nanoTime());

        final ApexEventListener myEventListener1 = new ApexEventListener() {
            @Override
            public void onApexEvent(final ApexEvent responseEvent) {
                assertNotNull("Synchronous sendEventWait failed", responseEvent);
                assertEquals(event1.getExecutionId(), responseEvent.getExecutionId());
                waitFlag = false;
            }
        };

        waitFlag = true;
        service.registerActionListener("Listener1", myEventListener1);
        service.getEngineServiceEventInterface().sendEvent(event1);

        while (waitFlag) {
            ThreadUtilities.sleep(100);
        }

        final ApexEvent event2 =
                new ApexEvent("Event0100", "0.0.1", "org.onap.policy.apex.domains.sample.events", "test", "apex");
        event2.setExecutionId(System.nanoTime());
        event2.putAll(eventDataMap);

        final ApexEventListener myEventListener2 = new ApexEventListener() {
            @Override
            public void onApexEvent(final ApexEvent responseEvent) {
                assertNotNull("Synchronous sendEventWait failed", responseEvent);
                assertEquals(event2.getExecutionId(), responseEvent.getExecutionId());
                assertEquals(2, actionEventsReceived);
                waitFlag = false;
            }
        };

        waitFlag = true;
        service.deregisterActionListener("Listener1");
        service.registerActionListener("Listener2", myEventListener2);
        service.getEngineServiceEventInterface().sendEvent(event2);

        while (waitFlag) {
            ThreadUtilities.sleep(100);
        }
        service.deregisterActionListener("Listener2");

        actionEventsReceived = 0;

        // Stop all engines on this engine service
        final long stoptime = System.currentTimeMillis();
        service.stop();
        while (!service.isStopped() && System.currentTimeMillis() - stoptime < MAX_STOP_WAIT) {
            ThreadUtilities.sleep(200);
        }
        if (!service.isStopped()) {
            fail("Apex Service " + service.getKey() + " failed to stop after " + MAX_STOP_WAIT + " ms");
        }
    }

    /**
     * Update the engine then test the engine with 2 sample events - again.
     *
     * @throws ApexException if there is a problem
     */
    @Test
    public void testExecutionSet2() throws ApexException {
        service.updateModel(parameters.getEngineKey(), apexModelString, true);
        // Start the service
        service.startAll();
        final long starttime = System.currentTimeMillis();
        for (final AxArtifactKey engineKey : service.getEngineKeys()) {
            LOGGER.info("{}", service.getStatus(engineKey));
        }
        while (!service.isStarted() && System.currentTimeMillis() - starttime < MAX_START_WAIT) {
            ThreadUtilities.sleep(200);
        }
        if (!service.isStarted()) {
            fail("Apex Service " + service.getKey() + " failed to start after " + MAX_START_WAIT + " ms");
        }

        final EngineServiceEventInterface engineServiceEventInterface = service.getEngineServiceEventInterface();

        // Send some events
        final Date testStartTime = new Date();
        final Map<String, Object> eventDataMap = new HashMap<String, Object>();
        eventDataMap.put("TestSlogan", "This is a test slogan");
        eventDataMap.put("TestMatchCase", (byte) 123);
        eventDataMap.put("TestTimestamp", testStartTime.getTime());
        eventDataMap.put("TestTemperature", 34.5445667);

        final ApexEvent event =
                new ApexEvent("Event0000", "0.0.1", "org.onap.policy.apex.domains.sample.events", "test", "apex");
        event.setExecutionId(System.nanoTime());
        event.putAll(eventDataMap);
        engineServiceEventInterface.sendEvent(event);

        final ApexEvent event2 =
                new ApexEvent("Event0100", "0.0.1", "org.onap.policy.apex.domains.sample.events", "test", "apex");
        event2.setExecutionId(System.nanoTime());
        event2.putAll(eventDataMap);
        engineServiceEventInterface.sendEvent(event2);

        // Wait for results
        final long recvtime = System.currentTimeMillis();
        while (actionEventsReceived < 2 && System.currentTimeMillis() - recvtime < MAX_RECV_WAIT) {
            ThreadUtilities.sleep(100);
        }
        ThreadUtilities.sleep(500);
        assertEquals(2, actionEventsReceived);
        actionEventsReceived = 0;

        // Stop all engines on this engine service
        final long stoptime = System.currentTimeMillis();
        service.stop();
        while (!service.isStopped() && System.currentTimeMillis() - stoptime < MAX_STOP_WAIT) {
            ThreadUtilities.sleep(200);
        }
        if (!service.isStopped()) {
            fail("Apex Service " + service.getKey() + " failed to stop after " + MAX_STOP_WAIT + " ms");
        }
    }

    /**
     * Update the engine then test the engine with 2 sample events - again.
     *
     * @throws ApexException if there is a problem
     */
    @Test
    public void testExecutionSet2Sync() throws ApexException {
        service.updateModel(parameters.getEngineKey(), apexModelString, true);
        // Start the service
        service.startAll();
        final long starttime = System.currentTimeMillis();
        for (final AxArtifactKey engineKey : service.getEngineKeys()) {
            LOGGER.info("{}", service.getStatus(engineKey));
        }
        while (!service.isStarted() && System.currentTimeMillis() - starttime < MAX_START_WAIT) {
            ThreadUtilities.sleep(200);
        }
        if (!service.isStarted()) {
            fail("Apex Service " + service.getKey() + " failed to start after " + MAX_START_WAIT + " ms");
        }

        // Send some events
        final Date testStartTime = new Date();
        final Map<String, Object> eventDataMap = new HashMap<String, Object>();
        eventDataMap.put("TestSlogan", "This is a test slogan");
        eventDataMap.put("TestMatchCase", (byte) 123);
        eventDataMap.put("TestTimestamp", testStartTime.getTime());
        eventDataMap.put("TestTemperature", 34.5445667);

        final ApexEvent event1 =
                new ApexEvent("Event0000", "0.0.1", "org.onap.policy.apex.domains.sample.events", "test", "apex");
        event1.putAll(eventDataMap);

        final ApexEventListener myEventListener1 = new ApexEventListener() {
            @Override
            public void onApexEvent(final ApexEvent responseEvent) {
                assertNotNull("Synchronous sendEventWait failed", responseEvent);
                assertEquals(event1.getExecutionId(), responseEvent.getExecutionId());
                waitFlag = false;
            }
        };

        waitFlag = true;
        service.registerActionListener("Listener1", myEventListener1);
        service.getEngineServiceEventInterface().sendEvent(event1);

        while (waitFlag) {
            ThreadUtilities.sleep(100);
        }

        final ApexEvent event2 =
                new ApexEvent("Event0100", "0.0.1", "org.onap.policy.apex.domains.sample.events", "test", "apex");
        event2.putAll(eventDataMap);

        final ApexEventListener myEventListener2 = new ApexEventListener() {
            @Override
            public void onApexEvent(final ApexEvent responseEvent) {
                assertNotNull("Synchronous sendEventWait failed", responseEvent);
                assertEquals(event2.getExecutionId(), responseEvent.getExecutionId());
                waitFlag = false;
            }
        };

        waitFlag = true;
        service.registerActionListener("Listener2", myEventListener2);
        service.deregisterActionListener("Listener1");
        service.getEngineServiceEventInterface().sendEvent(event2);

        while (waitFlag) {
            ThreadUtilities.sleep(100);
        }

        service.deregisterActionListener("Listener2");

        assertEquals(2, actionEventsReceived);

        actionEventsReceived = 0;

        // Stop all engines on this engine service
        final long stoptime = System.currentTimeMillis();
        service.stop();
        while (!service.isStopped() && System.currentTimeMillis() - stoptime < MAX_STOP_WAIT) {
            ThreadUtilities.sleep(200);
        }
        if (!service.isStopped()) {
            fail("Apex Service " + service.getKey() + " failed to stop after " + MAX_STOP_WAIT + " ms");
        }
    }

    /**
     * Tear down the the test infrastructure.
     *
     * @throws ApexException if there is an error
     */
    @AfterClass
    public static void tearDown() throws Exception {
        // Stop all engines on this engine service
        final long stoptime = System.currentTimeMillis();
        service.stop();
        while (!service.isStopped() && System.currentTimeMillis() - stoptime < MAX_STOP_WAIT) {
            ThreadUtilities.sleep(200);
        }
        if (!service.isStopped()) {
            fail("Apex Service " + service.getKey() + " failed to stop after " + MAX_STOP_WAIT + " ms");
        }
        service = null;
    }

    /**
     * The listener interface for receiving test events. The class that is interested in processing
     * a test event implements this interface, and the object created with that class is registered
     * with a component using the component's <code>addTestListener</code> method. When the test
     * event occurs, that object's appropriate method is invoked.
     *
     * @see TestEvent
     */
    private static final class TestListener implements ApexEventListener {

        /*
         * (non-Javadoc)
         *
         * @see
         * org.onap.policy.apex.service.engine.runtime.ApexEventListener#onApexEvent(org.onap.policy
         * .apex.service.engine.event.ApexEvent)
         */
        @Override
        public synchronized void onApexEvent(final ApexEvent event) {
            LOGGER.debug("result 1 is:" + event);
            checkResult(event);
            actionEventsReceived++;

            final Date testStartTime = new Date((Long) event.get("TestTimestamp"));
            final Date testEndTime = new Date();

            LOGGER.info("policy execution time: " + (testEndTime.getTime() - testStartTime.getTime()) + "ms");
        }

        /**
         * Check result.
         *
         * @param result the result
         */
        private void checkResult(final ApexEvent result) {
            assertTrue(result.getName().startsWith("Event0004") || result.getName().startsWith("Event0104"));

            assertTrue(result.get("TestSlogan").equals("This is a test slogan"));
            assertTrue(result.get("TestMatchCase").equals(new Byte((byte) 123)));
            assertTrue(result.get("TestTemperature").equals(34.5445667));
            assertTrue(((byte) result.get("TestMatchCaseSelected")) >= 0
                    && ((byte) result.get("TestMatchCaseSelected") <= 3));
            assertTrue(((byte) result.get("TestEstablishCaseSelected")) >= 0
                    && ((byte) result.get("TestEstablishCaseSelected") <= 3));
            assertTrue(((byte) result.get("TestDecideCaseSelected")) >= 0
                    && ((byte) result.get("TestDecideCaseSelected") <= 3));
            assertTrue(
                    ((byte) result.get("TestActCaseSelected")) >= 0 && ((byte) result.get("TestActCaseSelected") <= 3));
        }
    }
}
