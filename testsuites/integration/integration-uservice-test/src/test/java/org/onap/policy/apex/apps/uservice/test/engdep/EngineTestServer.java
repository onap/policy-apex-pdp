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

package org.onap.policy.apex.apps.uservice.test.engdep;

import java.util.Date;

import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.engdep.EngDepMessagingService;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.runtime.ApexEventListener;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.onap.policy.apex.service.engine.runtime.EngineServiceEventInterface;
import org.onap.policy.apex.service.engine.runtime.impl.EngineServiceImpl;
import org.onap.policy.apex.service.parameters.engineservice.EngineServiceParameters;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class EngineTestServer is a test Apex service used to test the performance of Apex engines.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EngineTestServer implements Runnable, EngineServiceEventInterface {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EngineTestServer.class);

    private static final int TEST_SERVER_WAIT_TIME = 200;

    // The engine service for sending events to the Apex engines and the EngDEp service for engine
    // administration
    private EngineService engineService = null;
    private EngDepMessagingService messageService = null;

    // The inner class used to receive and process events
    private TestApexListener testApexListener = null;

    // Status flags
    private boolean starting = true;
    private boolean interrupted = false;

    // Parameters for the test
    private final EngineServiceParameters parameters;

    // Apex performance statistics
    private Date statsStartDate = null;
    private long actionEventsReceivedCount = 0;
    private long accumulatedExecutionTime = 0;
    private long totalActionEventsReceivedCount = 0;

    private ApexEvent lastEventReceived = null;

    /**
     * Instantiates a new engine test server to test Apex performance.
     *
     * @param parameters the parameters
     */
    public EngineTestServer(final EngineServiceParameters parameters) {
        this.parameters = parameters;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        LOGGER.debug("engine<-->deployment  test server starting . . .");

        // Set the name of the test server thread
        Thread.currentThread().setName(EngineTestServer.class.getName());

        try {
            // Create the engine service and set the listener for events emitted by the Apex service
            engineService = EngineServiceImpl.create(parameters);
            testApexListener = new TestApexListener();
            engineService.registerActionListener("testApexListener", testApexListener);

            // Create the EngDep messaging service and start it
            messageService = new EngDepMessagingService(engineService, parameters.getDeploymentPort());
            messageService.start();

            // Record the start date for statistics
            statsStartDate = new Date();
        } catch (final Exception e) {
            LOGGER.error("engine<-->deployment test server exception", e);
            e.printStackTrace();
            return;
        }
        LOGGER.debug("engine<-->deployment test server started");

        starting = false;

        while (!interrupted) {
            if (!ThreadUtilities.sleep(TEST_SERVER_WAIT_TIME)) {
                interrupted = true;
            }
        }
    }

    /**
     * Stop the test server.
     */
    public void stopServer() {
        LOGGER.debug("engine<-->deployment test server stopping . . .");

        interrupted = true;
        messageService.stop();

        LOGGER.debug("engine<-->deployment test server stopped");
    }

    /**
     * Checks if the test server is interrupted.
     *
     * @return true, if is interrupted
     */
    public boolean isInterrupted() {
        return interrupted;
    }

    /**
     * Gets the total action events received.
     *
     * @return the total action events received
     */
    public long getTotalActionEventsReceived() {
        return totalActionEventsReceivedCount;
    }

    /**
     * Gets the last action events received.
     *
     * @return the last action event received
     */
    public ApexEvent getLastActionEvent() {
        return lastEventReceived;
    }

    /**
     * Gets the Apex statistics and resets them.
     *
     * @return the statistics
     */
    public long[] getAndResetStats() {
        // Check if we have statistics
        if (statsStartDate == null || actionEventsReceivedCount == 0) {
            return null;
        }

        // Calculate, save, and reset the statistics
        final long[] stats = new long[2];
        synchronized (statsStartDate) {
            final long averageExecutionTime = accumulatedExecutionTime / actionEventsReceivedCount;
            final long measuringTime = new Date().getTime() - statsStartDate.getTime();
            final long transactionsPerMillisecond = actionEventsReceivedCount / measuringTime;
            stats[0] = averageExecutionTime;
            stats[1] = transactionsPerMillisecond;
            statsStartDate = new Date();

            actionEventsReceivedCount = 0;
            accumulatedExecutionTime = 0;
        }

        // Return the statistics
        return stats;
    }

    /**
     * Checks if the test server is starting.
     *
     * @return true, if the server is starting
     */
    public boolean isStarting() {
        return starting;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.service.engine.runtime.EngineServiceEventInterface#sendEvent(org.onap.
     * policy.apex.service.engine.event.ApexEvent)
     */
    @Override
    public void sendEvent(final ApexEvent event) {
        // Send the event onto the service being tested
        engineService.getEngineServiceEventInterface().sendEvent(event);
    }

    /**
     * The listener interface for receiving testApex events. The class that is interested in
     * processing a testApex event implements this interface, and the object created with that class
     * is registered with a component using the component's {@code addTestApexListener} method. When
     * the testApex event occurs, that object's appropriate method is invoked.
     *
     * This class listens for events from the Apex engine
     *
     * @see TestApexEvent
     */
    private final class TestApexListener implements ApexEventListener {

        /*
         * (non-Javadoc)
         *
         * @see
         * org.onap.policy.apex.service.engine.runtime.ApexEventListener#onApexEvent(org.onap.policy
         * .apex.service.engine.event.ApexEvent)
         */
        @Override
        public synchronized void onApexEvent(final ApexEvent apexEvent) {
            LOGGER.debug("result is:" + apexEvent);

            // Check the result event is correct
            checkResult(apexEvent);

            // Calculate the performance of the Apex engine service on this policy execution run and
            // accumulate the total statistics
            final Date testStartTime = new Date((Long) apexEvent.get("TestTimestamp"));
            final Date testEndTime = new Date();
            final long testTime = testEndTime.getTime() - testStartTime.getTime();
            LOGGER.debug("policy execution time: " + testTime + "ms");
            synchronized (statsStartDate) {
                actionEventsReceivedCount++;
                totalActionEventsReceivedCount++;
                accumulatedExecutionTime += testTime;
            }
            lastEventReceived = apexEvent;
        }

        /**
         * Check that a reply event from the Apex engine is valid.
         *
         * @param result the result event from the Apex engine
         */
        private void checkResult(final ApexEvent result) {
            assert result.getName().startsWith("Event0004") || result.getName().startsWith("Event0104");

            // CHECKSTYLE:OFF: checkstyle:magicNumber
            assert result.get("TestSlogan").equals("This is a test slogan");
            assert result.get("TestMatchCase").equals(new Byte((byte) 123));
            assert result.get("TestTemperature").equals(34.5445667);
            assert ((byte) result.get("TestMatchCaseSelected") >= 0 && (byte) result.get("TestMatchCaseSelected") <= 3);
            assert ((byte) result.get("TestEstablishCaseSelected") >= 0
                    && (byte) result.get("TestEstablishCaseSelected") <= 3);
            assert ((byte) result.get("TestDecideCaseSelected") >= 0
                    && (byte) result.get("TestDecideCaseSelected") <= 3);
            assert ((byte) result.get("TestActCaseSelected") >= 0 && (byte) result.get("TestActCaseSelected") <= 3);
            // CHECKSTYLE:ON: checkstyle:magicNumber
        }
    }
}
