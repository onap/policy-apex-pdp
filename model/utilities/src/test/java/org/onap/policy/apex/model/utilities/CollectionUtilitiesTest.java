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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.onap.policy.apex.model.utilities.CollectionUtils;

public class CollectionUtilitiesTest {

    @Test
    public void testNullLists() {
        int result = 0;
        
        result = CollectionUtils.compareLists(null, null);
        assertEquals(0, result);

        List<String> leftList  = new ArrayList<String>();

        result = CollectionUtils.compareLists(leftList, null);
        assertEquals(-1, result);

        List<String> rightList = new ArrayList<String>();
        
        result = CollectionUtils.compareLists(null, rightList);
        assertEquals(1, result);

        result = CollectionUtils.compareLists(leftList, rightList);
        assertEquals(0, result);

        leftList.add("AAA");
        result = CollectionUtils.compareLists(leftList, rightList);
        assertEquals(-1, result);

        rightList.add("AAA");
        result = CollectionUtils.compareLists(leftList, rightList);
        assertEquals(0, result);

        rightList.add("BBB");
        result = CollectionUtils.compareLists(leftList, rightList);
        assertEquals(1, result);

        leftList.add("BBB");
        result = CollectionUtils.compareLists(leftList, rightList);
        assertEquals(0, result);

        leftList.add("CCA");
        rightList.add("CCB");
        result = CollectionUtils.compareLists(leftList, rightList);
        assertEquals(-1, result);
        
        leftList.remove(leftList.size() - 1);
        rightList.remove(rightList.size() - 1);
        result = CollectionUtils.compareLists(leftList, rightList);
        assertEquals(0, result);

        leftList.add("CCB");
        rightList.add("CCA");
        result = CollectionUtils.compareLists(leftList, rightList);
        assertEquals(1, result);
        
        leftList.remove(leftList.size() - 1);
        rightList.remove(rightList.size() - 1);
        result = CollectionUtils.compareLists(leftList, rightList);
        assertEquals(0, result);
    }
}
