/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (c) 2021 Nordix Foundation.
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

package org.onap.policy.apex.examples.adaptive.concepts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The Class AnomalyDetection is used as a Java context for Adaptive anomaly detection in the adaptive domain.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AnomalyDetection implements Serializable {
    private static final long serialVersionUID = -823013127095523727L;

    private boolean firstRound = true;
    private int frequency = 0;

    private List<Double> anomalyScores = new LinkedList<>();
    private List<Double> frequencyForecasted;

    /**
     * The Constructor creates an AnomalyDetection instance.
     */
    public AnomalyDetection() {
        firstRound = true;
        frequency = 0;
    }

    /**
     * Checks if the AnomalyDetection instance is initialized.
     *
     * @return true, if the AnomalyDetection instance is initialized
     */
    public boolean isInitialized() {
        return (frequencyForecasted != null);
    }

    /**
     * Initializes the AnomalyDetection instance.
     *
     * @param incomingFrequency the frequency
     */
    public void init(final int incomingFrequency) {
        frequencyForecasted = new ArrayList<>(incomingFrequency);
        for (var i = 0; i < incomingFrequency; i++) {
            frequencyForecasted.add(null);
        }
    }

    /**
     * Check if the anomaly score values of the algorithm are set.
     *
     * @return true, if the anomaly score values of the algorithm are set
     */
    public boolean checkSetAnomalyScores() {
        return ((anomalyScores != null) && (!anomalyScores.isEmpty()));
    }

    /**
     * Unset the anomaly score values of the algorithm.
     */
    public void unsetAnomalyScores() {
        anomalyScores = null;
    }

    /**
     * Check if the frequency forecasted by the algorithm is set.
     *
     * @return true, if the frequency forecasted by the algorithm is set
     */
    public boolean checkSetFrequencyForecasted() {
        return ((frequencyForecasted != null) && (!frequencyForecasted.isEmpty()));
    }

    /**
     * Unset the frequency forecasted by the algorithm.
     */
    public void unsetFrequencyForecasted() {
        frequencyForecasted = null;
    }
}
