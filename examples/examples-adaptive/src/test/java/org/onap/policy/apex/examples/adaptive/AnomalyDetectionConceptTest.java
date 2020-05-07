/*-
 * ============LICENSE_START=======================================================
 *  Copyright (c) 2020 Nordix Foundation.
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

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.onap.policy.apex.examples.adaptive.concepts.AnomalyDetection;

public class AnomalyDetectionConceptTest {

    @Test
    public void testToString(){
        AnomalyDetection anomalyDetection = new AnomalyDetection();
        List<Double> newAnomalyScores = new LinkedList<>();
        newAnomalyScores.add((double) 55);
        anomalyDetection.setAnomalyScores(newAnomalyScores);
        anomalyDetection.setFrequency(55);
        assertEquals(newAnomalyScores, anomalyDetection.getAnomalyScores());
        assertTrue(anomalyDetection.checkSetAnomalyScores());
        assertEquals(55,anomalyDetection.getFrequency());
        assertEquals(true,anomalyDetection.getFirstRound());
        assertEquals("AnomalyDetection [firstRound=true, frequency=55, anomalyScores=[55.0], frequencyForecasted=null]", anomalyDetection.toString());
    }

    @Test
    public void testHashCode(){
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
    public void testEquals() {
        AnomalyDetection anomalyDetection = new AnomalyDetection();
        AnomalyDetection comparisonDetection = new AnomalyDetection();
        assertTrue(anomalyDetection.equals(comparisonDetection));
        //Compare object to itself
        assertTrue(anomalyDetection.equals(anomalyDetection));
        //Compare object to null
        assertFalse(anomalyDetection.equals(null));
        //compare object to string
        assertFalse(anomalyDetection.equals("test"));
        // Anomaly Scores comparison
        anomalyDetection.setAnomalyScores(null);
        assertFalse(anomalyDetection.equals(comparisonDetection));
        comparisonDetection.setAnomalyScores(null);
        assertTrue(anomalyDetection.equals(comparisonDetection));
        List<Double> anomalyScores = new LinkedList<>();
        anomalyScores.add((double) 20);
        anomalyDetection.setAnomalyScores(anomalyScores);
        assertFalse(anomalyDetection.equals(comparisonDetection));
        comparisonDetection.setAnomalyScores(anomalyScores);
        assertTrue(anomalyDetection.checkSetAnomalyScores());
        //First Round Checks
        anomalyDetection.setFirstRound(false);
        assertFalse(anomalyDetection.equals(comparisonDetection));
        anomalyDetection.setFirstRound(true);
        //Frequency Checks
        anomalyDetection.setFrequency(55);
        assertFalse(anomalyDetection.equals(comparisonDetection));
        anomalyDetection.setFrequency(0);
        //FrequencyForecasted Checks
        List<Double> comparisonFrequency = new LinkedList<>();
        comparisonDetection.setFrequencyForecasted(comparisonFrequency);
        assertFalse(anomalyDetection.equals(comparisonDetection));
        anomalyDetection.setFrequencyForecasted(anomalyScores);
        assertFalse(anomalyDetection.equals(comparisonDetection));
        anomalyDetection.setFrequencyForecasted(comparisonFrequency);
        assertTrue(anomalyDetection.equals(comparisonDetection));
    }

    @Test
    public void testCheckSets(){
        AnomalyDetection anomalyDetection = new AnomalyDetection();
        assertFalse(anomalyDetection.checkSetAnomalyScores());
        List<Double> anomalyScores = new LinkedList<>();
        anomalyDetection.setAnomalyScores(anomalyScores);
        assertFalse(anomalyDetection.checkSetAnomalyScores());
        anomalyScores.add((double)2);
        anomalyDetection.setAnomalyScores(anomalyScores);
        assertTrue(anomalyDetection.checkSetAnomalyScores());
        anomalyDetection.unsetAnomalyScores();
        assertFalse(anomalyDetection.checkSetAnomalyScores());
        assertEquals(null, anomalyDetection.getFrequencyForecasted());
        assertFalse(anomalyDetection.checkSetFrequencyForecasted());
        List<Double> frequencyForecasted = new LinkedList<>();
        anomalyDetection.setFrequencyForecasted(frequencyForecasted);
        assertFalse(anomalyDetection.checkSetFrequencyForecasted());
        frequencyForecasted.add((double)2);
        anomalyDetection.setFrequencyForecasted(frequencyForecasted);
        assertTrue(anomalyDetection.checkSetFrequencyForecasted());
        anomalyDetection.unsetFrequencyForecasted();
        assertFalse(anomalyDetection.checkSetFrequencyForecasted());
    }
}
