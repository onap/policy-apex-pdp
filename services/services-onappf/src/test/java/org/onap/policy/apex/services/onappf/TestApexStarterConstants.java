/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019, 2023-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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

package org.onap.policy.apex.services.onappf;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/**
 * Class to perform unit test of {@link ApexStarterConstants}}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
class TestApexStarterConstants {

    @Test
    void test() {
        // verify that constructor does not throw an exception
        assertThatCode(() -> {
            Constructor<ApexStarterConstants> c = ApexStarterConstants.class.getDeclaredConstructor();
            c.setAccessible(true);
            c.newInstance();
        }).doesNotThrowAnyException();
    }
}
