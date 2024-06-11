/*
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

/**
 * Test the tree map utilities.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TreeMapUtilsTest {

    private static final int KEY1 = 10;
    private static final int KEY2 = 20;
    private static final int KEY3 = 30;
    private static final String VALUE1 = "a-one";
    private static final String VALUE2 = "b-two";
    private static final String VALUE3 = "c-three";
    private static final String VALUE4 = "d-four";

    @Test
    public void testFindMatchingEntries() {
        TreeMap<String, String> testTreeMap = new TreeMap<String, String>();
        testTreeMap.put("G", "G");
        testTreeMap.put("H", "H");
        testTreeMap.put("JA", "JA");
        testTreeMap.put("JAM", "JAM");
        testTreeMap.put("JOE", "JOE");
        testTreeMap.put("JOSH", "JOSH");
        testTreeMap.put("K", "K");

        List<Entry<String, String>> foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "F");
        assertEquals(0, foundKeyList.size());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "G");
        assertEquals("G", foundKeyList.get(0).getKey());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "H");
        assertEquals("H", foundKeyList.get(0).getKey());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "I");
        assertEquals(0, foundKeyList.size());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "J");
        assertEquals("JA", foundKeyList.get(0).getKey());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "JA");
        assertEquals("JA", foundKeyList.get(0).getKey());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "JB");
        assertEquals(0, foundKeyList.size());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "JO");
        assertEquals("JOE", foundKeyList.get(0).getKey());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "JOE");
        assertEquals("JOE", foundKeyList.get(0).getKey());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "JOS");
        assertEquals("JOSH", foundKeyList.get(0).getKey());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "JOSH");
        assertEquals("JOSH", foundKeyList.get(0).getKey());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "K");
        assertEquals("K", foundKeyList.get(0).getKey());

        foundKeyList = TreeMapUtils.findMatchingEntries(testTreeMap, "L");
        assertEquals(0, foundKeyList.size());
    }

    @Test
    public void testCompareMaps() {
        Map<Integer, String> map1 = Map.of();
        Map<Integer, String> map2 = Map.of();

        // note: using TreeMap so we can control the ordering of the entries

        // compare with self
        assertThat(TreeMapUtils.compareMaps(map1, map1)).isZero();

        // two empty maps
        assertThat(TreeMapUtils.compareMaps(map1, map2)).isZero();

        // same content
        map1 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY2, VALUE2, KEY3, VALUE3));
        map2 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY2, VALUE2, KEY3, VALUE3));
        assertThat(TreeMapUtils.compareMaps(map1, map1)).isZero();
        assertThat(TreeMapUtils.compareMaps(map1, map2)).isZero();

        // one is shorter than the other
        map1 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY2, VALUE2, KEY3, VALUE3));
        map2 = new TreeMap<>(Map.of(KEY1, VALUE1));
        assertThat(TreeMapUtils.compareMaps(map1, map2)).isPositive();
        assertThat(TreeMapUtils.compareMaps(map2, map1)).isNegative();

        map2 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY2, VALUE2));
        assertThat(TreeMapUtils.compareMaps(map1, map2)).isPositive();
        assertThat(TreeMapUtils.compareMaps(map2, map1)).isNegative();

        // first key is different
        map1 = new TreeMap<>(Map.of(KEY3, VALUE1, KEY2, VALUE2));
        map2 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY2, VALUE2));
        assertThat(TreeMapUtils.compareMaps(map1, map2)).isPositive();
        assertThat(TreeMapUtils.compareMaps(map2, map1)).isNegative();

        // second key is different
        map1 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY3, VALUE2));
        map2 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY2, VALUE2));
        assertThat(TreeMapUtils.compareMaps(map1, map2)).isPositive();
        assertThat(TreeMapUtils.compareMaps(map2, map1)).isNegative();

        // first value is different
        map1 = new TreeMap<>(Map.of(KEY1, VALUE4, KEY2, VALUE2, KEY3, VALUE3));
        map2 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY2, VALUE2, KEY3, VALUE3));
        assertThat(TreeMapUtils.compareMaps(map1, map2)).isPositive();
        assertThat(TreeMapUtils.compareMaps(map2, map1)).isNegative();

        // second value is different
        map1 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY2, VALUE4, KEY3, VALUE3));
        map2 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY2, VALUE2, KEY3, VALUE3));
        assertThat(TreeMapUtils.compareMaps(map1, map2)).isPositive();
        assertThat(TreeMapUtils.compareMaps(map2, map1)).isNegative();

        // third value is different
        map1 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY2, VALUE2, KEY3, VALUE4));
        map2 = new TreeMap<>(Map.of(KEY1, VALUE1, KEY2, VALUE2, KEY3, VALUE3));
        assertThat(TreeMapUtils.compareMaps(map1, map2)).isPositive();
        assertThat(TreeMapUtils.compareMaps(map2, map1)).isNegative();
    }
}
