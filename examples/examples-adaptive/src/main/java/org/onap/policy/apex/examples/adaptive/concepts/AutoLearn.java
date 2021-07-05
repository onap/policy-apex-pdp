/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (c) 2021 Nordix Foundation.
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
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The Class AutoLearn is used as a Java context for Adaptive auto-learning of trends towards a fixed value in the
 * adaptive domain.
 */
@EqualsAndHashCode
@ToString
public class AutoLearn implements Serializable {
    private static final long serialVersionUID = 3825970380434170754L;

    private transient List<Double> avDiffs = null;

    private transient List<Long> counts = null;

    /**
     * Checks if the Autolearn instance is initialized.
     *
     * @return true, if the Autolearn instance is initialized
     */
    public boolean isInitialized() {
        return (avDiffs != null && counts != null);
    }

    /**
     * initializes the auto learning algorithm with the number of convergent variables to use.
     *
     * @param size the number of convergent variables to use
     */
    public void init(final int size) {
        if (avDiffs == null || avDiffs.isEmpty()) {
            avDiffs = new ArrayList<>(size);
            for (var i = 0; i < size; i++) {
                avDiffs.add(i, Double.NaN);
            }
        }

        if (counts == null || counts.isEmpty()) {
            counts = new ArrayList<>(size);
            for (var i = 0; i < size; i++) {
                counts.add(i, 0L);
            }
        }
    }

    /**
     * Gets the average difference values of the algorithm.
     *
     * @return the average difference values of the algorithm
     */
    public List<Double> getAvDiffs() {
        return avDiffs;
    }

    /**
     * Sets the average difference values of the algorithm.
     *
     * @param avDiffs the average difference values of the algorithm
     */
    public void setAvDiffs(final List<Double> avDiffs) {
        this.avDiffs = avDiffs;
    }

    /**
     * Check if the average difference values of the algorithm are set.
     *
     * @return true, if check set av diffs
     */
    public boolean checkSetAvDiffs() {
        return ((avDiffs != null) && (!avDiffs.isEmpty()));
    }

    /**
     * Unset the average difference values of the algorithm.
     */
    public void unsetAvDiffs() {
        avDiffs = null;
    }

    /**
     * Gets the count values of the algorithm.
     *
     * @return the count values of the algorithm
     */
    public List<Long> getCounts() {
        return counts;
    }

    /**
     * Sets the count values of the algorithm.
     *
     * @param counts the count values of the algorithm
     */
    public void setCounts(final List<Long> counts) {
        this.counts = counts;
    }

    /**
     * Check if the count values of the algorithm are set.
     *
     * @return true, if the count values of the algorithm are set
     */
    public boolean checkSetCounts() {
        return ((counts != null) && (!counts.isEmpty()));
    }

    /**
     * Unset the count values of the algorithm.
     */
    public void unsetCounts() {
        counts = null;
    }


}
