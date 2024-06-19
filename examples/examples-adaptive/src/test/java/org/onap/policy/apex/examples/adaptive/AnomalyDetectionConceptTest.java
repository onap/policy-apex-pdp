/*-
 * ============LICENSE_START=======================================================
 *  Copyright (c) 2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.examples.adaptive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.examples.adaptive.concepts.AnomalyDetection;

class AnomalyDetectionConceptTest {

    @Test
    void testToString() {
        AnomalyDetection anomalyDetection = new AnomalyDetection();
        List<Double> newAnomalyScores = new LinkedList<>();
        newAnomalyScores.add((double) 55);
        anomalyDetection.setAnomalyScores(newAnomalyScores);
        anomalyDetection.setFrequency(55);
        assertEquals(newAnomalyScores, anomalyDetection.getAnomalyScores());
        assertTrue(anomalyDetection.checkSetAnomalyScores());
        assertEquals(55, anomalyDetection.getFrequency());
        assertTrue(anomalyDetection.isFirstRound());
        assertEquals("AnomalyDetection(firstRound=true, frequency=55, anomalyScores=[55.0], frequencyForecasted=null)",
            anomalyDetection.toString());
    }

    @Test
    void testHashCode() {
        AnomalyDetection detection = new AnomalyDetection();
        AnomalyDetection compareDetection = new AnomalyDetection();
        assertEquals(detection.hashCode(), compareDetection.hashCode());
        detection.init(1);
        assertTrue(detection.isInitialized());
        assertFalse(compareDetection.isInitialized());
        compareDetection.setAnomalyScores(null);
        compareDetection.setFirstRound(false);
        compareDetection.setFrequencyForecasted(new LinkedList<>());
        assertNotEquals(detection.hashCode(), compareDetection.hashCode());
    }

    @Test
    void testEquals() {
        AnomalyDetection anomalyDetection = new AnomalyDetection();
        AnomalyDetection comparisonDetection = new AnomalyDetection();
        assertEquals(anomalyDetection, comparisonDetection);
        //Compare object to itself
        assertEquals(anomalyDetection, anomalyDetection);
        //Compare object to null
        assertNotNull(anomalyDetection);
        //compare object to string
        assertNotEquals("test", anomalyDetection);
        // Anomaly Scores comparison
        anomalyDetection.setAnomalyScores(null);
        assertNotEquals(anomalyDetection, comparisonDetection);
        comparisonDetection.setAnomalyScores(null);
        assertEquals(anomalyDetection, comparisonDetection);
        List<Double> anomalyScores = new LinkedList<>();
        anomalyScores.add((double) 20);
        anomalyDetection.setAnomalyScores(anomalyScores);
        assertNotEquals(anomalyDetection, comparisonDetection);
        comparisonDetection.setAnomalyScores(anomalyScores);
        assertTrue(anomalyDetection.checkSetAnomalyScores());
        //First Round Checks
        anomalyDetection.setFirstRound(false);
        assertNotEquals(anomalyDetection, comparisonDetection);
        anomalyDetection.setFirstRound(true);
        //Frequency Checks
        anomalyDetection.setFrequency(55);
        assertNotEquals(anomalyDetection, comparisonDetection);
        anomalyDetection.setFrequency(0);
        //FrequencyForecasted Checks
        List<Double> comparisonFrequency = new LinkedList<>();
        comparisonDetection.setFrequencyForecasted(comparisonFrequency);
        assertNotEquals(anomalyDetection, comparisonDetection);
        anomalyDetection.setFrequencyForecasted(anomalyScores);
        assertNotEquals(anomalyDetection, comparisonDetection);
        anomalyDetection.setFrequencyForecasted(comparisonFrequency);
        assertEquals(anomalyDetection, comparisonDetection);
    }

    @Test
    void testCheckSets() {
        AnomalyDetection anomalyDetection = new AnomalyDetection();
        assertFalse(anomalyDetection.checkSetAnomalyScores());
        List<Double> anomalyScores = new LinkedList<>();
        anomalyDetection.setAnomalyScores(anomalyScores);
        assertFalse(anomalyDetection.checkSetAnomalyScores());
        anomalyScores.add((double) 2);
        anomalyDetection.setAnomalyScores(anomalyScores);
        assertTrue(anomalyDetection.checkSetAnomalyScores());
        anomalyDetection.unsetAnomalyScores();
        assertFalse(anomalyDetection.checkSetAnomalyScores());
        assertNull(anomalyDetection.getFrequencyForecasted());
        assertFalse(anomalyDetection.checkSetFrequencyForecasted());
        List<Double> frequencyForecasted = new LinkedList<>();
        anomalyDetection.setFrequencyForecasted(frequencyForecasted);
        assertFalse(anomalyDetection.checkSetFrequencyForecasted());
        frequencyForecasted.add((double) 2);
        anomalyDetection.setFrequencyForecasted(frequencyForecasted);
        assertTrue(anomalyDetection.checkSetFrequencyForecasted());
        anomalyDetection.unsetFrequencyForecasted();
        assertFalse(anomalyDetection.checkSetFrequencyForecasted());
    }
}
