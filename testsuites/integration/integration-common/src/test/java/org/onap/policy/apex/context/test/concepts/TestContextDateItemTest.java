/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2024 Nordix Foundation.
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

import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestContextDateItemTest {

    @Test
    void contextDateItemDateTest() {
        TestContextDateItem tcdi = new TestContextDateItem();
        Assertions.assertNotEquals(tcdi.getDateValue(), new Date(2323223232L));
        Assertions.assertNotEquals(new TestContextDateItem(null), new TestContextDateItem(new Date(2323223232L)));
        tcdi.setDateValue(new Date(32323232223L));
        Assertions.assertNotEquals(new TestContextDateItem(2323223232L), tcdi);
        tcdi.setDateValue(null);
    }
}
