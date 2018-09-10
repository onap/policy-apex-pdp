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

package org.onap.policy.apex.model.utilities.typeutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.TreeMap;

import org.junit.Test;
import org.onap.policy.apex.model.utilities.comparison.KeyedMapComparer;
import org.onap.policy.apex.model.utilities.comparison.KeyedMapDifference;

/**
 * Test key map comparisons.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestKeyedMapComparer {

    @Test
    public void test() {
        TreeMap<String, String> leftMap = new TreeMap<String, String>();
        leftMap.put("B", "BBBBB");
        leftMap.put("C", "CCCCC");
        leftMap.put("E", "EEEEE");
        leftMap.put("G", "GGGGG");

        TreeMap<String, String> rightMap = new TreeMap<String, String>();
        rightMap.put("A", "AAAAA");
        rightMap.put("B", "B");
        rightMap.put("D", "DDDDD");
        rightMap.put("E", "EEEEE");
        rightMap.put("F", "FFFFF");
        rightMap.put("G", "G");

        KeyedMapDifference<String, String> kmComparedSame = new KeyedMapComparer<String, String>().compareMaps(leftMap,
                        leftMap);
        KeyedMapDifference<String, String> kmComparedDiff = new KeyedMapComparer<String, String>().compareMaps(leftMap,
                        rightMap);

        assertTrue(kmComparedSame.getIdenticalValues().equals(leftMap));
        assertEquals(1, kmComparedDiff.getLeftOnly().size());
        assertEquals(3, kmComparedDiff.getRightOnly().size());
        assertEquals(2, kmComparedDiff.getDifferentValues().size());
        assertEquals(1, kmComparedDiff.getIdenticalValues().size());

        assertNotNull(kmComparedSame.asString(true, true));
        assertNotNull(kmComparedSame.asString(true, false));
        assertNotNull(kmComparedSame.asString(false, false));
        assertNotNull(kmComparedSame.asString(false, true));

        assertNotNull(kmComparedDiff.asString(true, true));
        assertNotNull(kmComparedDiff.asString(true, false));
        assertNotNull(kmComparedDiff.asString(false, false));
        assertNotNull(kmComparedDiff.asString(false, true));
    }
}
