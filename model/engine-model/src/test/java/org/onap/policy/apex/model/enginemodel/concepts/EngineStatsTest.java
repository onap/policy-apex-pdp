/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Bell Canada.
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

package org.onap.policy.apex.model.enginemodel.concepts;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.prometheus.client.CollectorRegistry;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

/**
 * Test the engine statistics.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EngineStatsTest {
    private static final Object WAIT_LOCK = new Object();
    private static final String ENGINE_KEY = "EngineKey";
    private static final String ENGINE_VERSION = "0.0.1";

    @Test
    public void testEngineStats() {
        assertNotNull(new AxEngineStats());
        assertNotNull(new AxEngineStats(new AxReferenceKey()));

        final AxReferenceKey statsKey = new AxReferenceKey(ENGINE_KEY, ENGINE_VERSION, "EngineStats");
        final AxEngineStats stats = new AxEngineStats(statsKey);

        assertThatThrownBy(() -> stats.setKey(null))
            .hasMessage("key may not be null");
        stats.setKey(statsKey);
        assertEquals("EngineKey:0.0.1:NULL:EngineStats", stats.getKey().getId());
        assertEquals("EngineKey:0.0.1:NULL:EngineStats", stats.getKeys().get(0).getId());

        stats.setAverageExecutionTime(123.45);
        assertEquals(Double.valueOf(123.45), Double.valueOf(stats.getAverageExecutionTime()));
        checkAvgExecTimeMetric(stats);

        stats.setEventCount(987);
        assertEquals(987, stats.getEventCount());
        checkEventsCountMetric(stats);

        final long lastExecutionTime = System.currentTimeMillis();
        stats.setLastExecutionTime(lastExecutionTime);
        assertEquals(lastExecutionTime, stats.getLastExecutionTime());
        checkLastExecTimeMetric(stats);

        final long timestamp = System.currentTimeMillis();
        stats.setTimeStamp(timestamp);
        assertEquals(timestamp, stats.getTimeStamp());
        assertNotNull(stats.getTimeStampString());

        final long upTime = System.currentTimeMillis() - timestamp;
        stats.setUpTime(upTime);
        assertEquals(upTime, stats.getUpTime());
        checkUpTimeMetric(stats);

        stats.engineStart();
        assertTrue(stats.getUpTime() > -1);
        checkEngineStartTimestampMetric(stats);
        checkLastExecTimeMetric(stats);
        stats.engineStop();
        assertTrue(stats.getUpTime() >= 0);

        stats.engineStop();
        checkUpTimeMetric(stats);
        checkEngineStartTimestampMetric(stats);

        stats.reset();

        stats.setEventCount(-2);
        stats.executionEnter(new AxArtifactKey());
        assertEquals(2, stats.getEventCount());
        checkEventsCountMetric(stats);

        stats.setEventCount(10);
        stats.executionEnter(new AxArtifactKey());
        assertEquals(11, stats.getEventCount());
        checkEventsCountMetric(stats);

        stats.reset();
        stats.engineStart();
        stats.setEventCount(4);
        checkUpTimeMetric(stats);
        stats.executionEnter(new AxArtifactKey());
        checkEventsCountMetric(stats);
        checkAvgExecTimeMetric(stats);
        checkEngineStartTimestampMetric(stats);

        synchronized (WAIT_LOCK) {
            try {
                WAIT_LOCK.wait(10);
            } catch (InterruptedException e) {
                fail("test should not throw an exception");
            }
        }

        stats.executionExit();
        final double avExecutionTime = stats.getAverageExecutionTime();
        assertTrue(avExecutionTime >= 2.0 && avExecutionTime < 10.0);
        stats.engineStop();
        checkUpTimeMetric(stats);

        AxValidationResult result = new AxValidationResult();
        result = stats.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        stats.setKey(new AxReferenceKey());
        result = new AxValidationResult();
        result = stats.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        stats.setKey(statsKey);
        result = new AxValidationResult();
        result = stats.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        stats.clean();
        stats.reset();
        checkAllPrometheusMetrics(stats);

        final AxEngineStats clonedStats = new AxEngineStats(stats);
        assertEquals("AxEngineStats:(engineKey=AxReferenceKey:(parentKey", clonedStats.toString().substring(0, 50));

        assertNotNull(stats.getKeys());

        assertNotEquals(0, stats.hashCode());

        // disabling sonar because this code tests the equals() method
        assertEquals(stats, stats); // NOSONAR
        assertEquals(stats, clonedStats);
        assertNotNull(stats);
        checkAllPrometheusMetrics(clonedStats);

        Object helloObject = "Hello";
        assertNotEquals(stats, helloObject);
        assertNotEquals(stats, new AxEngineStats(new AxReferenceKey()));

        assertEquals(0, stats.compareTo(stats));
        assertEquals(0, stats.compareTo(clonedStats));
        assertNotEquals(0, stats.compareTo(new AxArtifactKey()));
        assertNotEquals(0, stats.compareTo(null));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(new AxReferenceKey())));

        stats.setTimeStamp(1);
        assertNotEquals(stats, new AxEngineStats(statsKey));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        stats.setTimeStamp(0);
        assertEquals(stats, new AxEngineStats(statsKey));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        checkAllPrometheusMetrics(clonedStats);

        stats.setEventCount(1);
        assertNotEquals(stats, new AxEngineStats(statsKey));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        stats.setEventCount(0);
        assertEquals(stats, new AxEngineStats(statsKey));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));

        stats.setLastExecutionTime(1);
        assertNotEquals(stats, new AxEngineStats(statsKey));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        stats.setLastExecutionTime(0);
        assertEquals(stats, new AxEngineStats(statsKey));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));

        stats.setAverageExecutionTime(1);
        assertNotEquals(stats, new AxEngineStats(statsKey));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        stats.setAverageExecutionTime(0);
        assertEquals(stats, new AxEngineStats(statsKey));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));

        stats.setUpTime(1);
        assertNotEquals(stats, new AxEngineStats(statsKey));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        stats.setUpTime(0);
        assertEquals(stats, new AxEngineStats(statsKey));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));

        assertEquals(-1, stats.compareTo(new AxEngineStats(statsKey, 0, 0, 0, 0.0, 0, 1)));

        stats.engineStart();
        assertNotEquals(stats, new AxEngineStats(statsKey));
        final AxEngineStats newStats = new AxEngineStats(statsKey);
        newStats.setTimeStamp(stats.getTimeStamp());
        assertNotEquals(stats, newStats);
        assertNotEquals(0, stats.compareTo(newStats));
        stats.engineStop();
        checkUpTimeMetric(stats);
        checkEngineStartTimestampMetric(stats);
        stats.reset();
        assertEquals(stats, new AxEngineStats(statsKey));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        checkAllPrometheusMetrics(stats);
    }

    private void checkUpTimeMetric(AxEngineStats stats) {
        Double upTimeMetric = CollectorRegistry.defaultRegistry.getSampleValue("apex_engine_uptime",
                new String[]{AxEngineStats.ENGINE_INSTANCE_ID},
                new String[]{ENGINE_KEY + ":" + ENGINE_VERSION}) * 1000d;
        assertEquals(upTimeMetric.longValue(), stats.getUpTime());
    }

    private void checkEventsCountMetric(AxEngineStats stats) {
        Double eventsCountMetric = CollectorRegistry.defaultRegistry
                .getSampleValue("apex_engine_events_executed_count",
                        new String[]{AxEngineStats.ENGINE_INSTANCE_ID},
                        new String[]{ENGINE_KEY + ":" + ENGINE_VERSION});
        assertEquals(eventsCountMetric.longValue(), stats.getEventCount());
    }

    private void checkLastExecTimeMetric(AxEngineStats stats) {
        Double lastExecTimeMetric = CollectorRegistry.defaultRegistry
                .getSampleValue("apex_engine_last_execution_time_sum", new String[]{AxEngineStats.ENGINE_INSTANCE_ID},
                        new String[]{ENGINE_KEY + ":" + ENGINE_VERSION}) * 1000d;
        assertEquals(lastExecTimeMetric.longValue(), stats.getLastExecutionTime());
    }

    private void checkEngineStartTimestampMetric(AxEngineStats stats) {
        Double engineStartTimestampMetric = CollectorRegistry.defaultRegistry
                .getSampleValue("apex_engine_last_start_timestamp_epoch",
                        new String[]{AxEngineStats.ENGINE_INSTANCE_ID},
                        new String[]{ENGINE_KEY + ":" + ENGINE_VERSION});
        assertEquals(engineStartTimestampMetric.longValue(), stats.getLastStart());
    }

    private void checkAvgExecTimeMetric(AxEngineStats stats) {
        Double avgExecTimeMetric = CollectorRegistry.defaultRegistry
                .getSampleValue("apex_engine_average_execution_time_seconds",
                        new String[]{AxEngineStats.ENGINE_INSTANCE_ID},
                        new String[]{ENGINE_KEY + ":" + ENGINE_VERSION}) * 1000d;
        assertEquals(avgExecTimeMetric, Double.valueOf(stats.getAverageExecutionTime()));
    }

    private void checkAllPrometheusMetrics(AxEngineStats stats) {
        checkEventsCountMetric(stats);
        checkUpTimeMetric(stats);
        checkAvgExecTimeMetric(stats);
        checkEngineStartTimestampMetric(stats);
        checkEngineStartTimestampMetric(stats);
    }
}
