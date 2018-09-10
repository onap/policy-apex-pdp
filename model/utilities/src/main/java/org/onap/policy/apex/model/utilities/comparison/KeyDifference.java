/*
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

package org.onap.policy.apex.model.utilities.comparison;

/**
 * This class is used to template key differences for bulk key comparisons in models. It performs a difference check
 * between two keys.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <K> the generic type
 */
public class KeyDifference<K> {
    // The keys being compared
    private K leftKey;
    private K rightKey;

    /**
     * Constructor used to set the keys being compared.
     *
     * @param leftKey the left key that is being compared
     * @param rightKey the right key that is being compared
     */
    public KeyDifference(final K leftKey, final K rightKey) {
        this.leftKey = leftKey;
        this.rightKey = rightKey;
    }

    /**
     * Gets the left key.
     *
     * @return the left key
     */
    public K getLeftKey() {
        return leftKey;
    }

    /**
     * Gets the right key.
     *
     * @return the right key
     */
    public K getRightKey() {
        return rightKey;
    }

    /**
     * Checks if the left and right keys are equal.
     *
     * @return true, if checks if is equal
     */
    public boolean isEqual() {
        return leftKey.equals(rightKey);
    }

    /**
     * Gets a string representation of the difference between the keys.
     *
     * @param diffsOnly if set, then a blank string is returned if the keys are equal
     * @return the difference between the keys as a string
     */
    public String asString(final boolean diffsOnly) {
        StringBuilder builder = new StringBuilder();

        if (leftKey.equals(rightKey)) {
            if (!diffsOnly) {
                builder.append("left key ");
                builder.append(leftKey);
                builder.append(" equals right key ");
                builder.append(rightKey);
                builder.append('\n');
            }
        } else {
            builder.append("left key ");
            builder.append(leftKey);
            builder.append(" and right key ");
            builder.append(rightKey);
            builder.append(" differ\n");
        }

        return builder.toString();
    }
}
