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

package org.onap.policy.apex.model.utilities;

import java.util.List;
import java.util.ListIterator;

/**
 * This is common utility class with static methods for handling collections.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class CollectionUtils {
    /**
     * Private constructor used to prevent sub class instantiation.
     */
    private CollectionUtils() {
    }

    /**
     * Compare two lists, checks for equality, then for equality on members.
     *
     * @param <T> The type of the lists being compared
     * @param leftList The leftmost List
     * @param rightList The rightmost list
     * @return an integer indicating how different the lists are
     */
    public static <T> int compareLists(final List<? extends Comparable<T>> leftList, final List<? extends Comparable<T>> rightList) {
        // Check for nulls
        if (leftList == null && rightList == null) {
            return 0;
        }
        if (leftList != null && rightList == null) {
            return -1;
        }
        if (leftList == null) {
            return 1;
        }

        // Check for equality
        if (leftList.equals(rightList)) {
            return 0;
        }
        
        return compareListEntries(leftList, rightList);
    }

    /**
     * Compare two lists for equality on members.
     *
     * @param <T> The type of the lists being compared
     * @param leftList The leftmost List
     * @param rightList The rightmost list
     * @return an integer indicating how different the lists are
     */
    private static <T> int compareListEntries(final List<? extends Comparable<T>> leftList, final List<? extends Comparable<T>> rightList) {
        
        // Iterate down the lists till we find a difference
        final ListIterator<?> leftIterator = leftList.listIterator();
        final ListIterator<?> rightIterator = rightList.listIterator();

        while (true) {
            // Check the iterators
            if (!leftIterator.hasNext() && !rightIterator.hasNext()) {
                return 0;
            }
            if (leftIterator.hasNext() && !rightIterator.hasNext()) {
                return -1;
            }
            if (!leftIterator.hasNext() && rightIterator.hasNext()) {
                return 1;
            }

            // Get the next objects
            @SuppressWarnings("unchecked")
            final T leftObject = (T) leftIterator.next();
            @SuppressWarnings("unchecked")
            final T rightObject = (T) rightIterator.next();

            // Compare the objects
            @SuppressWarnings("unchecked")
            final int comparisonResult = ((Comparable<T>) leftObject).compareTo(rightObject);

            // Check the comparison result
            if (comparisonResult != 0) {
                return comparisonResult;
            }
        }
	}
}
