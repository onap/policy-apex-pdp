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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * This class holds the result of a difference check between two keyed maps. Four results are returned in the class. The {@code leftOnly} result is the entries
 * that appear only in the left map. the {@code rightOnly} result is the entries that appear only in the right map. The {@code differentValues} result are the
 * entries that have the same key but different values in the maps being compared. The {@code identicalValues} result are the entries with identical keys and
 * values in both maps being compared.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @param <K> the generic type
 * @param <V> the generic type
 */
public class KeyedMapDifference<K, V> {
	private static final String KEY = "key=";
	private static final String VALUE = ",value=";

	// Three maps to hold the comparison result
	private Map<K, V> leftOnly = new TreeMap<>();
	private Map<K, V> rightOnly = new TreeMap<>();
	private Map<K, V> identicalValues = new TreeMap<>();
	private Map<K, List<V>> differentValues = new TreeMap<>();

	/**
	 * Gets the entries that were found only in the left map.
	 *
	 * @return the entries only in the left map
	 */
	public Map<K, V> getLeftOnly() {
		return leftOnly;
	}

	/**
	 * Gets the entries that were found only in the right map.
	 *
	 * @return the entries only in the right map
	 */
	public Map<K, V> getRightOnly() {
		return rightOnly;
	}

	/**
	 * Gets the entries that were identical (keys and values the same) in both maps.
	 *
	 * @return the identical entries
	 */
	public Map<K, V> getIdenticalValues() {
		return identicalValues;
	}

	/**
	 * Gets the entries that had the same key but different values in both maps.
	 *
	 * @return the entries that were different. There are two values in the list of values for each entry. The first value is the value that was in the left map
	 *         and the second value is the value that was in the right map.
	 */
	public Map<K, List<V>> getDifferentValues() {
		return differentValues;
	}

	/**
	 * Return a string representation of the differences.
	 *
	 * @param diffsOnly if set, then a blank string is returned if the maps are equal
	 * @param keysOnly if set, then a terse string that prints only the keys is returned, otherwise both keys and values are printed
	 * @return the string
	 */
	public String asString(final boolean diffsOnly, final boolean keysOnly) {
		StringBuilder builder = new StringBuilder();

		if (leftOnly.isEmpty()) {
			if (!diffsOnly) {
				builder.append("*** all left keys in right\n");
			}
		}
		else {
			builder.append(getInOneSideOnlyAsString(leftOnly, "left",  keysOnly));
		}

		if (leftOnly.isEmpty()) {
			if (!diffsOnly) {
				builder.append("*** all right keys in left\n");
			}
		}
		else {
			builder.append(getInOneSideOnlyAsString(rightOnly, "right",  keysOnly));
		}

		if (differentValues.isEmpty()) {
			if (!diffsOnly) {
				builder.append("*** all values in left and right are identical\n");
			}
		}
		else {
			builder.append(getDifferencesAsString(keysOnly));
		}

		if (!diffsOnly) {
			builder.append(getIdenticalsAsString(keysOnly));
		}

		return builder.toString();
	}

	/**
	 * Output the entries in a map with entries that are in one side only as a string
	 * @param sideMap the map for the side being checked
	 * @param sideMapString the string that represents the map in output strings
	 * @param keysOnly if true, just add key information and not entries
	 * @return the entries as a string
	 */
	private Object getInOneSideOnlyAsString(final Map<K, V> sideMap, final String sideMapString, final boolean keysOnly) {
		StringBuilder builder = new StringBuilder();

		builder.append("*** list of keys on " + sideMapString + " only\n");
		for (Entry<K, V> leftEntry : sideMap.entrySet()) {
			builder.append(KEY);
			builder.append(leftEntry.getKey());
			if (!keysOnly) {
				builder.append(VALUE);
				builder.append(leftEntry.getValue());
			}
			builder.append('\n');
		}

		return builder.toString();
	}

	/**
	 * Output the differences between two the maps as a string
	 * @param keysOnly if true, just add key information and not entries
	 * @return the differences as a string
	 */
	private String getDifferencesAsString(final boolean keysOnly) {
		StringBuilder builder = new StringBuilder();

		builder.append("*** list of differing entries between left and right\n");
		for (Entry<K, List<V>> differentEntry : differentValues.entrySet()) {
			builder.append(KEY);
			builder.append(differentEntry.getKey());
			if (!keysOnly) {
				builder.append(",values={");
				boolean first = true;
				for (V differentEntryValue : differentEntry.getValue()) {
					builder.append(differentEntryValue);
					if (first) {
						first = false;
					}
					else {
						builder.append(',');
					}
				}
				builder.append("}");
			}
			builder.append('\n');
		}

		return builder.toString();
	}

	/**
	 * Output the identical entries in the maps as a string
	 * @param keysOnly if true, just add key information and not entries
	 * @return the identical entries as a string
	 */
	private String getIdenticalsAsString(final boolean keysOnly) {
		StringBuilder builder = new StringBuilder();

		builder.append("*** list of identical entries in left and right\n");
		for (Entry<K, V> identicalEntry : identicalValues.entrySet()) {
			builder.append(KEY);
			builder.append(identicalEntry.getKey());
			if (!keysOnly) {
				builder.append(VALUE);
				builder.append(identicalEntry.getValue());
			}
			builder.append('\n');
		}

		return builder.toString();
	}
}
