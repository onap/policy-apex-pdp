/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.model.utilities;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * This class provides utility functions for tree maps. A function to find the nearest match in the tree map to an input
 * string is provided.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TreeMapUtils {

    /**
     * Find the list of entries that matches a given word, for example "p" will match "put", "policy", and "push".
     *
     * @param <T> the generic type for the value of the tree map
     * @param searchMap the map that the method operates on
     * @param word the word to search for
     * @return the list of entries in the {@code searchMap} that match the {@code word}
     */
    public static <T> List<Entry<String, T>> findMatchingEntries(final NavigableMap<String, T> searchMap,
            final String word) {
        final List<Entry<String, T>> foundNodes = new ArrayList<>();

        // A straight match check
        if (searchMap.containsKey(word)) {
            foundNodes.add(new SimpleEntry<>(word, searchMap.get(word)));
            return foundNodes;
        }

        // Set up the beginning point for our search for a list of near matches
        String foundKeyword = searchMap.floorKey(word);
        if (foundKeyword == null) {
            foundKeyword = searchMap.firstKey();
        } else {
            foundKeyword = searchMap.higherKey(foundKeyword);
        }

        // Find all the nodes that start with the word we are searching for
        while (foundKeyword != null) {
            if (foundKeyword.startsWith(word)) {
                foundNodes.add(new SimpleEntry<>(foundKeyword, searchMap.get(foundKeyword)));
                foundKeyword = searchMap.higherKey(foundKeyword);
            } else {
                break;
            }
        }
        return foundNodes;
    }

    /**
     * Compares two maps.
     * @param <K> Key type
     * @param <V> Value type
     * @param leftMap left map
     * @param rightMap right map
     * @return an integer indicating how different the maps are
     */
    @SuppressWarnings("unchecked")
    public static <K, V> int compareMaps(Map<? extends Comparable<K>, ? extends Comparable<V>> leftMap,
                    Map<? extends Comparable<K>, ? extends Comparable<V>> rightMap) {
        if (leftMap == rightMap) {
            return 0;
        }

        Iterator<?> leftIt = leftMap.entrySet().iterator();
        Iterator<?> rightIt = rightMap.entrySet().iterator();

        while (leftIt.hasNext() && rightIt.hasNext()) {
            Map.Entry<?, ?> leftEntry = (Entry<?, ?>) leftIt.next();
            Map.Entry<?, ?> rightEntry = (Entry<?, ?>) rightIt.next();

            K leftKey = (K) leftEntry.getKey();
            K rightKey = (K) rightEntry.getKey();
            int result = ((Comparable<K>) leftKey).compareTo(rightKey);
            if (result != 0) {
                return result;
            }

            V leftValue = (V) leftEntry.getValue();
            V rightValue = (V) rightEntry.getValue();
            result = ((Comparable<V>) leftValue).compareTo(rightValue);
            if (result != 0) {
                return result;
            }
        }

        if (leftIt.hasNext()) {
            return 1;
        } else if (rightIt.hasNext()) {
            return -1;
        } else {
            return 0;
        }
    }
}
