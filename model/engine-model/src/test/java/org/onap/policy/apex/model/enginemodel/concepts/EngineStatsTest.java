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

package org.onap.policy.apex.model.enginemodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

/**
 * Test engine statistics.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class EngineStatsTest {
    private static final Object WAIT_LOCK = new Object();

    @Test
    public void testEngineStats() {
        assertNotNull(new AxEngineStats());
        assertNotNull(new AxEngineStats(new AxReferenceKey()));

        final AxReferenceKey statsKey = new AxReferenceKey("EngineKey", "0.0.1", "EngineStats");
        final AxEngineStats stats = new AxEngineStats(statsKey);

        try {
            stats.setKey(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("key may not be null", e.getMessage());
        }

        stats.setKey(statsKey);
        assertEquals("EngineKey:0.0.1:NULL:EngineStats", stats.getKey().getId());
        assertEquals("EngineKey:0.0.1:NULL:EngineStats", stats.getKeys().get(0).getId());

        stats.setAverageExecutionTime(123.45);
        assertEquals(new Double(123.45), new Double(stats.getAverageExecutionTime()));

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
        stats.engineStop();
        assertTrue(stats.getUpTime() >= 0);

        stats.engineStop();

        stats.reset();

        stats.setEventCount(-2);
        stats.executionEnter(new AxArtifactKey());
        assertEquals(2, stats.getEventCount());

        stats.setEventCount(10);
        stats.executionEnter(new AxArtifactKey());
        assertEquals(11, stats.getEventCount());

        stats.reset();
        stats.engineStart();
        stats.setEventCount(4);
        stats.executionEnter(new AxArtifactKey());
        
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

        final AxEngineStats clonedStats = new AxEngineStats(stats);
        assertEquals("AxEngineStats:(engineKey=AxReferenceKey:(parentKey", clonedStats.toString().substring(0, 50));

        assertNotNull(stats.getKeys());

        assertFalse(stats.hashCode() == 0);

        assertTrue(stats.equals(stats));
        assertTrue(stats.equals(clonedStats));
        assertFalse(stats.equals(null));
        assertFalse(stats.equals("Hello"));
        assertFalse(stats.equals(new AxEngineStats(new AxReferenceKey())));

        assertEquals(0, stats.compareTo(stats));
        assertEquals(0, stats.compareTo(clonedStats));
        assertNotEquals(0, stats.compareTo(new AxArtifactKey()));
        assertNotEquals(0, stats.compareTo(null));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(new AxReferenceKey())));

        stats.setTimeStamp(1);
        assertFalse(stats.equals(new AxEngineStats(statsKey)));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        stats.setTimeStamp(0);
        assertTrue(stats.equals(new AxEngineStats(statsKey)));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));

        stats.setEventCount(1);
        assertFalse(stats.equals(new AxEngineStats(statsKey)));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        stats.setEventCount(0);
        assertTrue(stats.equals(new AxEngineStats(statsKey)));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));

        stats.setLastExecutionTime(1);
        assertFalse(stats.equals(new AxEngineStats(statsKey)));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        stats.setLastExecutionTime(0);
        assertTrue(stats.equals(new AxEngineStats(statsKey)));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));

        stats.setAverageExecutionTime(1);
        assertFalse(stats.equals(new AxEngineStats(statsKey)));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        stats.setAverageExecutionTime(0);
        assertTrue(stats.equals(new AxEngineStats(statsKey)));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));

        stats.setUpTime(1);
        assertFalse(stats.equals(new AxEngineStats(statsKey)));
        assertNotEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
        stats.setUpTime(0);
        assertTrue(stats.equals(new AxEngineStats(statsKey)));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));

        assertEquals(-1, stats.compareTo(new AxEngineStats(statsKey, 0, 0, 0, 0.0, 0, 1)));

        stats.engineStart();
        assertFalse(stats.equals(new AxEngineStats(statsKey)));
        final AxEngineStats newStats = new AxEngineStats(statsKey);
        newStats.setTimeStamp(stats.getTimeStamp());
        assertFalse(stats.equals(newStats));
        assertNotEquals(0, stats.compareTo(newStats));
        stats.engineStop();
        stats.reset();
        assertTrue(stats.equals(new AxEngineStats(statsKey)));
        assertEquals(0, stats.compareTo(new AxEngineStats(statsKey)));
    }

}
