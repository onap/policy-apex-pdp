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
package org.onap.policy.apex.service.engine.benchmark;

import static org.junit.Assert.assertNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.runtime.ApexEventListener;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class TestApexEventListener implements ApexEventListener {

    private static final String SENT_TIMESTAMP = "SentTimestamp";
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestApexEventListener.class);
    private static final String RECVD_TIMESTAMP = "RecvdTimestamp";
    private Queue<ApexEvent> queue;

    private final AtomicLong eventReceived = new AtomicLong();

    public TestApexEventListener() {
        this.queue = new ConcurrentLinkedQueue<ApexEvent>();
    }

    @Override
    public void onApexEvent(final ApexEvent apexEvent) {
        apexEvent.put(RECVD_TIMESTAMP, System.currentTimeMillis());
        eventReceived.incrementAndGet();
        queue.add(apexEvent);
    }

    public void printResult() {
        if (!queue.isEmpty()) {
            long maxTimeInMilliSeconds = 0;
            long minTimeInMilliSeconds = Long.MAX_VALUE;
            final long numEvents = queue.size();
            long totalTimeInMilliSeconds = 0;
            for (final ApexEvent apexEvent : queue) {
                assertNull(apexEvent.getExceptionMessage());
                final Long endTimeInMilliSeconds = (Long) apexEvent.get(RECVD_TIMESTAMP);
                final Long startTimeInMilliSeconds = (Long) apexEvent.get(SENT_TIMESTAMP);
                final long timeTaken = endTimeInMilliSeconds - startTimeInMilliSeconds;
                totalTimeInMilliSeconds += timeTaken;
                if (timeTaken > maxTimeInMilliSeconds) {
                    maxTimeInMilliSeconds = timeTaken;
                }
                if (timeTaken < minTimeInMilliSeconds) {
                    minTimeInMilliSeconds = timeTaken;
                }
            }
            LOGGER.info("Average Time Taken to process {} events: {} ms", numEvents,
                    (totalTimeInMilliSeconds / numEvents));
            LOGGER.info("Max Time Taken: {} ms", maxTimeInMilliSeconds);
            LOGGER.info("Min Time Taken: {} ms", minTimeInMilliSeconds);
        }
    }

    public void reset() {
        this.queue = new ConcurrentLinkedQueue<ApexEvent>();
        eventReceived.set(0);;
    }

    public Queue<ApexEvent> getQueue() {
        return queue;
    }

    public long getEventReceived() {
        return eventReceived.get();
    }

}
