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

package org.onap.policy.apex.examples.adaptive.concepts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class AnomalyDetection is used as a Java context for Adaptive anomaly detection in the adaptive domain.
 */
public class AnomalyDetection implements Serializable {
    private static final long serialVersionUID = -823013127095523727L;

    private static final int HASH_PRIME_1 = 31;
    private static final int HASH_PRIME_2 = 1231;
    private static final int HASH_PRIME_3 = 1237;

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
        for (int i = 0; i < incomingFrequency; i++) {
            frequencyForecasted.add(null);
        }
    }

    /**
     * Indicates if this is the first round of the algorithm.
     *
     * @return true if this is the first round of the algorithm
     */
    public boolean getFirstRound() {
        return firstRound;
    }

    /**
     * Sets the first round indicator of the algorithm.
     *
     * @param firstRound the first round indicator of the algorithm
     */
    public void setFirstRound(final boolean firstRound) {
        this.firstRound = firstRound;
    }

    /**
     * Gets the frequency value of the algorithm.
     *
     * @return the frequency value of the algorithm
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency value of the algorithm.
     *
     * @param frequency the frequency value of the algorithm
     */
    public void setFrequency(final int frequency) {
        this.frequency = frequency;
    }

    /**
     * Gets the anomaly score values of the algorithm.
     *
     * @return the anomaly score values of the algorithm
     */
    public List<Double> getAnomalyScores() {
        return anomalyScores;
    }

    /**
     * Sets the anomaly score values of the algorithm.
     *
     * @param anomalyScores the anomaly score values of the algorithm
     */
    public void setAnomalyScores(final List<Double> anomalyScores) {
        this.anomalyScores = anomalyScores;
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
     * Gets the frequency forecasted by the algorithm.
     *
     * @return the frequency forecasted by the algorithm
     */
    public List<Double> getFrequencyForecasted() {
        return frequencyForecasted;
    }

    /**
     * Sets the frequency forecasted by the algorithm.
     *
     * @param frequencyForecasted the frequency forecasted by the algorithm
     */
    public void setFrequencyForecasted(final List<Double> frequencyForecasted) {
        this.frequencyForecasted = frequencyForecasted;
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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AnomalyDetection [firstRound=" + firstRound + ", frequency=" + frequency + ", anomalyScores="
                + anomalyScores + ", frequencyForecasted=" + frequencyForecasted + "]";
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = HASH_PRIME_1;
        int result = 1;
        result = prime * result + ((anomalyScores == null) ? 0 : anomalyScores.hashCode());
        result = prime * result + (firstRound ? HASH_PRIME_2 : HASH_PRIME_3);
        result = prime * result + frequency;
        result = prime * result + ((frequencyForecasted == null) ? 0 : frequencyForecasted.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AnomalyDetection other = (AnomalyDetection) obj;
        if (anomalyScores == null) {
            if (other.anomalyScores != null) {
                return false;
            }
        } else if (!anomalyScores.equals(other.anomalyScores)) {
            return false;
        }
        if (firstRound != other.firstRound) {
            return false;
        }
        if (frequency != other.frequency) {
            return false;
        }
        if (frequencyForecasted == null) {
            if (other.frequencyForecasted != null) {
                return false;
            }
        } else if (!frequencyForecasted.equals(other.frequencyForecasted)) {
            return false;
        }
        return true;
    }
}
