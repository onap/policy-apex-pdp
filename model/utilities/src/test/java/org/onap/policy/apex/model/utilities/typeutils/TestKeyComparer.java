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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.apex.model.utilities.comparison.KeyComparer;
import org.onap.policy.apex.model.utilities.comparison.KeyDifference;

/**
 * Test key comparisons.
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestKeyComparer {

    @Test
    public void test() {
        KeyDifference<String> keyDifference = new KeyComparer<String>().compareKeys("Hello", "Goodbye");
        
        assertFalse(keyDifference.isEqual());
        assertTrue("Hello".equals(keyDifference.getLeftKey().toString()));
        assertTrue("Goodbye".equals(keyDifference.getRightKey().toString()));

        assertTrue("left key Hello and right key Goodbye differ\n".equals(keyDifference.asString(true)));
        assertTrue("left key Hello and right key Goodbye differ\n".equals(keyDifference.asString(false)));
        
        KeyDifference<String> keyDifference2 = new KeyComparer<String>().compareKeys("Here", "Here");
        assertTrue("".equals(keyDifference2.asString(true)));
        assertTrue("left key Here equals right key Here\n".equals(keyDifference2.asString(false)));
    }
}
