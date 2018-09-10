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

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;
import org.onap.policy.apex.model.utilities.TreeMapUtils;

/**
 * Test the tree map utilities.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TreeMapUtilsTest {

    @Test
    public void test() {
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
}
