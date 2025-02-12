/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024-2025 Nordix Foundation.
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

/**
 * Test the engine statistics.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class EngineStatsTest {
    private static final Object WAIT_LOCK = new Object();
    private static final String ENGINE_KEY = "EngineKey";
    private static final String ENGINE_VERSION = "0.0.1";

    @Test
    void testEngineStats() {
        final AxReferenceKey statsKey = new AxReferenceKey(ENGINE_KEY, ENGINE_VERSION, "EngineStats");
        final AxEngineStats stats = new AxEngineStats(statsKey);

        assertThatThrownBy(() -> stats.setKey(null)).hasMessage("key may not be null");
        stats.setKey(statsKey);
        assertEquals("EngineKey:0.0.1:NULL:EngineStats", stats.getKey().getId());
        assertEquals("EngineKey:0.0.1:NULL:EngineStats", stats.getKeys().get(0).getId());

        assertSetValuesToStats(stats);

        synchronized (WAIT_LOCK) {
            try {
                WAIT_LOCK.wait(10);
            } catch (InterruptedException e) {
                fail("test should not throw an exception");
            }
        }

        stats.executionExit();
        checkAvgExecTimeMetric(stats);
        checkEventsCountMetric(stats);
        checkLastExecTimeMetric(stats);
        final double avExecutionTime = stats.getAverageExecutionTime();
        assertTrue(avExecutionTime >= 2.0 && avExecutionTime < 10.0);
        stats.engineStop();
        checkEngineStartTimestampMetric(stats);

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

        assertCompareTo(stats, statsKey);
    }

    private void assertSetValuesToStats(AxEngineStats stats) {
        stats.setAverageExecutionTime(123.45);
        assertEquals(Double.valueOf(123.45), Double.valueOf(stats.getAverageExecutionTime()));

        stats.setEventCount(987);
        assertEquals(987, stats.getEventCount());

        final long lastExecutionTime = System.currentTimeMillis();
        stats.setLastExecutionTime(lastExecutionTime);
        assertEquals(lastExecutionTime, stats.getLastExecutionTime());

        final long timestamp = System.currentTimeMillis();
        stats.setTimeStamp(timestamp);
        assertEquals(timestamp, stats.getTimeStamp());
        assertNotNull(stats.getTimeStampString());

        final long upTime = System.currentTimeMillis() - timestamp;
        stats.setUpTime(upTime);
        assertEquals(upTime, stats.getUpTime());

        stats.engineStart();
        assertTrue(stats.getUpTime() > -1);
        checkEngineStartTimestampMetric(stats);
        stats.engineStop();
        assertTrue(stats.getUpTime() >= 0);

        stats.engineStop();
        checkEngineStartTimestampMetric(stats);

        stats.reset();

        stats.setEventCount(-2);
        stats.executionEnter(new AxArtifactKey());
        assertEquals(2, stats.getEventCount());
        checkEventsCountMetric(stats);

        stats.setEventCount(10);
        stats.executionEnter(new AxArtifactKey());
        assertEquals(11, stats.getEventCount());

        stats.reset();
        stats.engineStart();
        stats.setEventCount(3);
        stats.executionEnter(new AxArtifactKey());
        checkEventsCountMetric(stats);
        checkEngineStartTimestampMetric(stats);
    }

    private void assertCompareTo(AxEngineStats stats, AxReferenceKey statsKey) {
        final AxEngineStats clonedStats = new AxEngineStats(stats);
        assertEquals("AxEngineStats:(engineKey=AxReferenceKey:(parentKey", clonedStats.toString().substring(0, 50));

        assertNotNull(stats.getKeys());

        assertNotEquals(0, stats.hashCode());

        // disabling sonar because this code tests the equals() method
        assertEquals(stats, stats); // NOSONAR
        assertEquals(stats, clonedStats);
        assertNotNull(stats);

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
        checkEngineStartTimestampMetric(stats);
        final AxEngineStats newStats = new AxEngineStats(statsKey);
        newStats.setTimeStamp(stats.getTimeStamp());
        assertNotEquals(stats, newStats);
        assertNotEquals(0, stats.compareTo(newStats));
        stats.engineStop();
        checkEngineStartTimestampMetric(stats);
        stats.reset();
        assertEquals(stats, new AxEngineStats(statsKey));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
    }

    private void checkEventsCountMetric(AxEngineStats stats) {
        PrometheusRegistry registry = new PrometheusRegistry();
        Counter eventsCountCounter = Counter.builder()
            .name("pdpa_engine_event_executions")
            .help("Number of PDPA engine event executions")
            .labelNames("engine_instance_id")
            .register(registry);

        String labelValue = ENGINE_KEY + ":" + ENGINE_VERSION;
        eventsCountCounter.labelValues(labelValue).inc(stats.getEventCount());

        double eventsCountMetric = eventsCountCounter.labelValues(labelValue).get();
        assertEquals(stats.getEventCount(), (long) eventsCountMetric);
    }

    private void checkLastExecTimeMetric(AxEngineStats stats) {
        PrometheusRegistry registry = new PrometheusRegistry();
        Gauge lastExecTimeGauge = Gauge.builder()
            .name("pdpa_engine_last_execution_time")
            .help("Last execution time of PDPA engine")
            .labelNames("engine_instance_id")
            .register(registry);

        String labelValue = ENGINE_KEY + ":" + ENGINE_VERSION;
        lastExecTimeGauge.labelValues(labelValue).set(stats.getLastExecutionTime() / 1000.0);

        double lastExecTimeMetric = lastExecTimeGauge.labelValues(labelValue).get() * 1000d;
        assertEquals(stats.getLastExecutionTime(), lastExecTimeMetric, 0.001);
    }


    private void checkEngineStartTimestampMetric(AxEngineStats stats) {
        PrometheusRegistry registry = new PrometheusRegistry();
        Gauge engineStartTimestampGauge = Gauge.builder()
            .name("pdpa_engine_last_start_timestamp_epoch")
            .help("Last start timestamp of PDPA engine in epoch seconds")
            .labelNames("engine_instance_id")
            .register(registry);

        String labelValue = ENGINE_KEY + ":" + ENGINE_VERSION;
        engineStartTimestampGauge.labelValues(labelValue).set(stats.getLastStart());

        double engineStartTimestampMetric = engineStartTimestampGauge.labelValues(labelValue).get();
        assertEquals(stats.getLastStart(), (long) engineStartTimestampMetric);
    }

    private void checkAvgExecTimeMetric(AxEngineStats stats) {
        PrometheusRegistry registry = new PrometheusRegistry();
        Gauge avgExecTimeGauge = Gauge.builder()
            .name("pdpa_engine_average_execution_time_seconds")
            .help("Average execution time of PDPA engine in seconds")
            .labelNames("engine_instance_id")
            .register(registry);

        String labelValue = ENGINE_KEY + ":" + ENGINE_VERSION;
        avgExecTimeGauge.labelValues(labelValue).set(stats.getAverageExecutionTime() / 1000.0);

        double avgExecTimeMetric = avgExecTimeGauge.labelValues(labelValue).get() * 1000d;
        assertEquals(stats.getAverageExecutionTime(), avgExecTimeMetric, 0.001);
    }
}
