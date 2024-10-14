/*-
 * ============LICENSE_START=======================================================
 *  Copyright (c) 2024 Nordix Foundation.
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

package org.onap.policy.apex.examples.adaptive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.examples.adaptive.concepts.AutoLearn;


class AutoLearnConceptTest {

    @Test
    void testToString() {
        AutoLearn autoLearn = new AutoLearn();
        AutoLearn compareAutoLearn = new AutoLearn();
        assertEquals(autoLearn.hashCode(), compareAutoLearn.hashCode());
        List<Double> avDiffs = new ArrayList<>();
        avDiffs.add(27d);
        List<Long> counts = new ArrayList<>();
        counts.add(2L);
        autoLearn.setCounts(counts);
        autoLearn.setAvDiffs(avDiffs);
        assertEquals(avDiffs, autoLearn.getAvDiffs());
        assertEquals(counts, autoLearn.getCounts());
        assertTrue(autoLearn.isInitialized());
        assertEquals("AutoLearn(avDiffs=[27.0], counts=[2])", autoLearn.toString());
    }

    @Test
    void testConditions() {
        AutoLearn a1 = new AutoLearn();
        a1.setCounts(List.of(1L, 3L));
        assertTrue(a1.checkSetCounts());
        assertFalse(a1.checkSetAvDiffs());
        AutoLearn a2 = new AutoLearn();
        a2.init(1);
        assertNotNull(a2.getCounts());
        a1.unsetAvDiffs();
        a1.unsetCounts();
        assertNull(a1.getCounts());
        assertNull(a1.getAvDiffs());
    }

}
