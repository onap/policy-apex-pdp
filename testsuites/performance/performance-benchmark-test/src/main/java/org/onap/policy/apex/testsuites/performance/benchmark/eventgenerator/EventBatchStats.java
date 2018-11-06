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

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.events.InputEvent;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.events.OutputEvent;

/**
 * This POJO class returns statistics on a event batch execution in Apex.
 */
public class EventBatchStats {
    private final int batchNumber;
    private final int batchSize;
    private final String apexClient;

    // @formatter:off
    private long eventsNotSent         = 0;
    private long eventsSent            = 0;
    private long eventsNotReceived     = 0;
    private long eventsReceived        = 0;
    private long averageRoundTrip      = 0;
    private long shortestRoundTrip     = Long.MAX_VALUE;
    private long longestRoundTrip      = 0;
    private long averageApexExecution  = 0;
    private long shortestApexExecution = Long.MAX_VALUE;
    private long longestApexExecution  = 0;
    // @formatter:on

    /**
     * Create a statistics object for an event batch.
     * 
     * @param eventBatch the event batch for these statistics
     */
    public EventBatchStats(final EventBatch eventBatch) {
        this.batchNumber = eventBatch.getBatchNumber();
        this.batchSize = eventBatch.getBatchSize();
        this.apexClient = eventBatch.getApexClient();
        
        calcutateStats(eventBatch);
    }

    /**
     * Create a total statistics object for a list of event batches.
     * 
     * @param eventBatchStatsList the event batch for these statistics
     */
    public EventBatchStats(final List<EventBatchStats> eventBatchStatsList) {
        this.batchNumber = -1;
        this.apexClient = "TOTAL";
        
        calcutateStats(eventBatchStatsList);
        
        this.batchSize = (int)(eventsNotSent + eventsSent);
    }

    /**
     * Compile the statistics.
     * @param eventBatch the event batch for which statisticss should be calculated
     */
    private void calcutateStats(final EventBatch eventBatch) {
        long accumulatedRoundTripTime = 0;
        long accumulatedApexExecutionTime = 0;
        
        for (int eventNo = 0; eventNo < batchSize; eventNo++) {
            Pair<Long, Long> eventTimings = calculateEventTimings(eventBatch, eventNo);
            if (eventTimings == null) {
                // The event has not been sent yet or the response has not been received yet
                continue;
            }
            
            accumulatedRoundTripTime += eventTimings.getLeft();
            accumulatedApexExecutionTime += eventTimings.getRight();
        }
        
        if (eventsReceived != 0) {
            averageRoundTrip = accumulatedRoundTripTime / eventsReceived;
            averageApexExecution = accumulatedApexExecutionTime / eventsReceived;
        }
    }

    /**
     * Compile the statistics.
     * @param eventBatchStatsList the event batch list for which statistics should be calculated
     */
    private void calcutateStats(final List<EventBatchStats> eventBatchStatsList) {
        long accumulatedRoundTripTime = 0;
        long accumulatedApexExecutionTime = 0;
        
        for (EventBatchStats eventBatchStats: eventBatchStatsList) {
            // @formatter:off
            eventsNotSent     += eventBatchStats.getEventsNotSent();
            eventsSent        += eventBatchStats.getEventsSent();
            eventsNotReceived += eventBatchStats.getEventsNotReceived();
            eventsReceived    += eventBatchStats.getEventsReceived();
            // @formatter:on
            
            if (shortestRoundTrip > eventBatchStats.getShortestRoundTrip()) {
                shortestRoundTrip = eventBatchStats.getShortestRoundTrip(); 
            }

            if (shortestApexExecution > eventBatchStats.getShortestApexExecution()) {
                shortestApexExecution = eventBatchStats.getShortestApexExecution(); 
            }

            if (longestRoundTrip < eventBatchStats.getLongestRoundTrip()) {
                longestRoundTrip = eventBatchStats.getLongestRoundTrip();
            }

            if (longestApexExecution < eventBatchStats.getLongestApexExecution()) {
                longestApexExecution = eventBatchStats.getLongestApexExecution();
            }

            accumulatedRoundTripTime += eventBatchStats.getAverageRoundTrip();
            accumulatedApexExecutionTime += eventBatchStats.getAverageApexExecution();
        }
        
        if (!eventBatchStatsList.isEmpty()) {
            averageRoundTrip = accumulatedRoundTripTime / eventBatchStatsList.size();
            averageApexExecution = accumulatedApexExecutionTime / eventBatchStatsList.size();
        }
    }

    /**
     * Calculate statistics for a single event.
     * @param eventBatch the event batch for the event
     * @param eventNo the event number of the event
     * @return
     */
    private Pair<Long, Long> calculateEventTimings(EventBatch eventBatch, int eventNo) {
        InputEvent inputEvent = eventBatch.getInputEvent(eventNo);
        OutputEvent outputEvent = eventBatch.getOutputEvent(eventNo);
        
        // Event not sent yet
        if (inputEvent == null) {
            eventsNotSent++;
            return null;
        }
        else {
            eventsSent++;
        }
        
        if (outputEvent == null) {
            eventsNotReceived++;
            return null;
            
        }
        else {
            eventsReceived++;
        }

        long roundTrip = outputEvent.getTestReceviedTimestamp() - outputEvent.getTestTimestamp();
        long apexExecution = outputEvent.getTestActStateTime() - outputEvent.getTestMatchStateTime();

        
        if (shortestRoundTrip > roundTrip) {
            shortestRoundTrip = roundTrip; 
        }

        if (shortestApexExecution > apexExecution) {
            shortestApexExecution = apexExecution; 
        }

        if (longestRoundTrip < roundTrip) {
            longestRoundTrip = roundTrip;
        }

        if (longestApexExecution < apexExecution) {
            longestApexExecution = apexExecution;
        }

        return new ImmutablePair<>(roundTrip, apexExecution);
    }

    public int getBatchNumber() {
        return batchNumber;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public String getApexClient() {
        return apexClient;
    }

    public long getEventsNotSent() {
        return eventsNotSent;
    }

    public long getEventsSent() {
        return eventsSent;
    }

    public long getEventsNotReceived() {
        return eventsNotReceived;
    }

    public long getEventsReceived() {
        return eventsReceived;
    }

    public long getAverageRoundTrip() {
        return averageRoundTrip;
    }

    public long getShortestRoundTrip() {
        return shortestRoundTrip;
    }

    public long getLongestRoundTrip() {
        return longestRoundTrip;
    }

    public long getAverageApexExecution() {
        return averageApexExecution;
    }

    public long getShortestApexExecution() {
        return shortestApexExecution;
    }

    public long getLongestApexExecution() {
        return longestApexExecution;
    }
}
