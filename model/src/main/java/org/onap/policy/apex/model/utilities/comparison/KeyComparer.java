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
 * This class compares two keys and returns their differences. It is used in bulk comparisons in models where maps of
 * keys are being compared. The {@link KeyComparer} that is returned does the actual comparison
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <K> the type of key being compared
 */
public class KeyComparer<K> {

    /**
     * Compare two keys and return their differences.
     *
     * @param leftKey The left key of the comparison
     * @param rightKey The right key of the comparison
     * @return The difference between the keys
     */
    public KeyDifference<K> compareKeys(final K leftKey, final K rightKey) {
        return new KeyDifference<>(leftKey, rightKey);
    }
}
