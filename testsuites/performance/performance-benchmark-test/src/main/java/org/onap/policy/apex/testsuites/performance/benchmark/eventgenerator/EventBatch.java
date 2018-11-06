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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.events.InputEvent;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.events.OutputEvent;

/**
 * This class keeps track of a batch of events sent to an Apex instance.
 */
public class EventBatch {
    private static AtomicInteger nextBatchNumber = new AtomicInteger();
    
    private final int batchNumber = nextBatchNumber.getAndIncrement();
    private final Map<Integer, InputEvent>  inputEventMap  = new ConcurrentHashMap<>();
    private final Map<Integer, OutputEvent> outputEventMap = new ConcurrentHashMap<>();
    
    private final int batchSize;
    private final String apexClient;

    /**
     * Create an event batch.
     * 
     * @param batchSize the size of the batch
     * @param apexClient the apex client to which the event batch will be sent
     */
    public EventBatch(final int batchSize, final String apexClient) {
        this.batchSize = batchSize;
        this.apexClient = apexClient;
        
        // Create the events for the batch of events
        for (int eventNumber = 0; eventNumber < batchSize; eventNumber++) {
            InputEvent inputEvent = new InputEvent();
            inputEvent.setTestSlogan(getEventSlogan(eventNumber));
            inputEventMap.put(eventNumber, inputEvent);
        }
    }

    /**
     * Get the batch of events as a JSON string.
     * 
     * @return the JSON string representation of the batch of events.
     */
    public String getBatchAsJsonString() {
        if (batchSize == 1) {
            return inputEventMap.get(0).asJson();
        }
        
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[\n");
        boolean first = true;
        for (InputEvent inputEvent : inputEventMap.values()) {
            if (first) {
                first = false;
            }
            else {
                jsonBuilder.append(",\n");
            }
            jsonBuilder.append(inputEvent.asJson());
        }
        jsonBuilder.append("\n]\n");
        
        return jsonBuilder.toString();
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

    /**
     * Get the event slogan.
     * 
     * @param eventNumber the number of this event
     * @return the event slogan
     */
    private String getEventSlogan(final int eventNumber) {
        StringBuilder testSloganBuilder = new StringBuilder();
        testSloganBuilder.append(batchNumber);
        testSloganBuilder.append('-');
        testSloganBuilder.append(eventNumber);
        testSloganBuilder.append(": ");
        testSloganBuilder.append(apexClient);
        
        return testSloganBuilder.toString();
    }

    /**
     * Handle a response event.
     * 
     * @param responseEvent the response event
     */
    public void handleResponse(OutputEvent responseEvent) {
        outputEventMap.put(responseEvent.findEventNumber(), responseEvent);
    }
    
    /**
     * Get the statistics on this event batch.
     * @return the event batch statistics
     */
    public EventBatchStats getStats() {
        return new EventBatchStats(this);
    }

    /**
     * Get an input event for an event number.
     * @param eventNo the event number
     * @return the event
     */
    public InputEvent getInputEvent(int eventNo) {
        return inputEventMap.get(eventNo);
    }

    /**
     * Get an output event for an event number.
     * @param eventNo the event number
     * @return the event
     */
    public OutputEvent getOutputEvent(int eventNo) {
        return outputEventMap.get(eventNo);
    }
}
