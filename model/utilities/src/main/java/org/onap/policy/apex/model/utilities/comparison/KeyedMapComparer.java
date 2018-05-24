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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Compare two maps and returns their differences. The types of the keys and the values in the two maps being comapred must be the same. The class returns
 * entries that are only in the left map, only in the right map, entries that have identical keys and different values and entries that have different keys and
 * different values in a {@link KeyedMapDifference} instance.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <K> the type of the keys in the maps being compared
 * @param <V> the type of the values in the maps being compared
 */
public class KeyedMapComparer<K, V> {
    /**
     * Compare two maps and return their differences in a {@link KeyedMapDifference} instance.
     *
     * @param leftMap The left map to be compared
     * @param rightMap The right map to be compared
     * @return The common, left only, and right only maps in a {@link KeyedMapDifference} instance
     */
    public KeyedMapDifference<K, V> compareMaps(final Map<K, V> leftMap, final Map<K, V> rightMap) {
        KeyedMapDifference<K, V> result = new KeyedMapDifference<>();

        // Get the keys that are only in the left map
        Set<K> leftOnlyKeys = new TreeSet<>(leftMap.keySet());
        leftOnlyKeys.removeAll(rightMap.keySet());

        // Get the keys that are only in the right map
        Set<K> rightOnlyKeys = new TreeSet<>(rightMap.keySet());
        rightOnlyKeys.removeAll(leftMap.keySet());

        // Find the keys common across both maps
        Set<K> commonKeys = new TreeSet<>(rightMap.keySet());
        commonKeys.addAll(leftMap.keySet());
        commonKeys.removeAll(leftOnlyKeys);
        commonKeys.removeAll(rightOnlyKeys);

        // Now save the left values
        for (K key : leftOnlyKeys) {
            result.getLeftOnly().put(key, leftMap.get(key));
        }

        // Now save the right values
        for (K key : rightOnlyKeys) {
            result.getRightOnly().put(key, rightMap.get(key));
        }

        // Save the common values to two maps, an identical and different map
        for (K key : commonKeys) {
            // Check if the values are identical in each map
            V leftValue = leftMap.get(key);
            V rightValue = rightMap.get(key);

            // Store as appropriate
            if (leftValue.equals(rightValue)) {
                result.getIdenticalValues().put(key, leftValue);
            }
            else {
                // Store the two values
                List<V> valueList = new ArrayList<>();
                valueList.add(leftValue);
                valueList.add(rightValue);

                result.getDifferentValues().put(key, valueList);
            }
        }

        return result;
    }
}
