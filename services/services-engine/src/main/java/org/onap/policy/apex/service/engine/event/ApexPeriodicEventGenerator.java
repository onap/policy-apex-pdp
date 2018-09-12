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

package org.onap.policy.apex.service.engine.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.onap.policy.apex.service.engine.runtime.EngineServiceEventInterface;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is used to generate periodic events into an Apex engine service. It is used to trigger
 * policies that perform housekeeping operations.
 *
 * @author eeilfn
 */
public class ApexPeriodicEventGenerator extends TimerTask {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexPeriodicEventGenerator.class);

    /** The name of the periodic event. */
    public static final String PERIODIC_EVENT_NAME = "PERIODIC_EVENT";

    /** The version of the periodic event. */
    public static final String PERIODIC_EVENT_VERSION = "0.0.1";

    /** The name space of the periodic event. */
    public static final String PERIODIC_EVENT_NAMESPACE = "org.onap.policy.apex.service.engine.event";

    /** The source of the periodic event. */
    public static final String PERIODIC_EVENT_SOURCE = "internal";

    /** The target of the periodic event. */
    public static final String PERIODIC_EVENT_TARGET = "internal";

    /**
     * The field name in the periodic event for the delay between occurrences of the periodic event.
     */
    public static final String PERIODIC_DELAY = "PERIODIC_DELAY";

    /**
     * The field name in the periodic event for the time at which the first periodic event will
     * occur.
     */
    public static final String PERIODIC_FIRST_TIME = "PERIODIC_FIRST_TIME";

    /**
     * The field name in the periodic event for the time at which the last periodic event will
     * occur.
     */
    public static final String PERIODIC_LAST_TIME = "PERIODIC_LAST_TIME";

    /** The field name in the periodic event for the time at which the event was sent. */
    public static final String PERIODIC_CURRENT_TIME = "PERIODIC_CURRENT_TIME";

    /**
     * The field name in the periodic event for the number of occurrences of this event that have
     * been sent to date, this is a sequence number for the periodic event.
     */
    public static final String PERIODIC_EVENT_COUNT = "PERIODIC_EVENT_COUNT";

    // The Java timer used to send periodic events
    private Timer timer = null;

    // The engine service interface we'll send periodic events to
    private final EngineServiceEventInterface engineServiceEventInterface;

    // Timing information
    private long period = 0;
    private long firstEventTime = 0;
    private long lastEventTime = 0;
    private long eventCount = 0;

    /**
     * Constructor, save a reference to the event stream handler.
     *
     * @param engineServiceEventInterface the engine service event interface on which to send
     *        periodic events
     * @param period The period in milliseconds between events
     */
    public ApexPeriodicEventGenerator(final EngineServiceEventInterface engineServiceEventInterface,
            final long period) {
        // Save the engine service reference and delay
        this.engineServiceEventInterface = engineServiceEventInterface;
        this.period = period;

        timer = new Timer(ApexPeriodicEventGenerator.class.getSimpleName(), true);
        timer.schedule(this, period, period);
    }

    /**
     * Output the metrics for stream loading.
     */
    @Override
    public void run() {
        final Map<String, Object> periodicEventMap = new HashMap<>();

        // Record the current event time
        final long currentEventTime = System.currentTimeMillis();

        // Check if this is the first periodic event
        if (firstEventTime == 0) {
            firstEventTime = currentEventTime;
            lastEventTime = currentEventTime;
        }

        // Increment the event counter
        eventCount++;

        // Set the fields in the periodic event
        periodicEventMap.put(PERIODIC_DELAY, period);
        periodicEventMap.put(PERIODIC_FIRST_TIME, firstEventTime);
        periodicEventMap.put(PERIODIC_LAST_TIME, lastEventTime);
        periodicEventMap.put(PERIODIC_CURRENT_TIME, currentEventTime);
        periodicEventMap.put(PERIODIC_EVENT_COUNT, eventCount);

        // Send the periodic event
        try {
            final ApexEvent periodicEvent = new ApexEvent(PERIODIC_EVENT_NAME, PERIODIC_EVENT_VERSION,
                    PERIODIC_EVENT_NAMESPACE, PERIODIC_EVENT_SOURCE, PERIODIC_EVENT_TARGET);
            periodicEvent.putAll(periodicEventMap);
            engineServiceEventInterface.sendEvent(periodicEvent);
        } catch (final ApexEventException e) {
            LOGGER.warn("could not send Apex periodic event " + PERIODIC_EVENT_NAME + ":" + PERIODIC_EVENT_VERSION, e);
            return;
        }

        // Save the current time as the last time
        lastEventTime = currentEventTime;
    }

    /**
     * Cancel the timer.
     *
     * @return true, if cancel
     */
    @Override
    public boolean cancel() {
        // Cancel the timer
        if (timer != null) {
            timer.cancel();
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ApexPeriodicEventGenerator [period=" + period + ", firstEventTime=" + firstEventTime
                + ", lastEventTime=" + lastEventTime + ", eventCount=" + eventCount + "]";
    }
}
