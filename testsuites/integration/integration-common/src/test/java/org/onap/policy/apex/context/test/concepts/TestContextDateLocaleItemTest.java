/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2024 Nordix Foundation. All rights reserved.
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

package org.onap.policy.apex.context.test.concepts;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.TimeZone;
import org.junit.jupiter.api.Test;


class TestContextDateLocaleItemTest {

    @Test
    void contextDateLocaleItemTest() {
        TestContextDateLocaleItem tcdli = new TestContextDateLocaleItem();
        TestContextDateItem tcdi = new TestContextDateItem();
        String tzValue = TimeZone.getTimeZone("Europe/London").getDisplayName();
        assertNotEquals(new TestContextDateLocaleItem(tcdi, tzValue, false, 0, "English", "England"),
                new TestContextDateLocaleItem());
        assertNotEquals(tcdli, new TestContextDateLocaleItem(tcdli));
        tcdli.setTzValue(TimeZone.getTimeZone("Europe/Dublin").getDisplayName());
        TestContextDateLocaleItem nullTcdli = new TestContextDateLocaleItem();
        nullTcdli.setTzValue(null);
        assertNotEquals(tcdli, nullTcdli);
    }
}
