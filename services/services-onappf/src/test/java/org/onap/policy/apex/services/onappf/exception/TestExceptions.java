/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.services.onappf.exception;

import org.junit.Test;
import org.onap.policy.common.utils.test.ExceptionsTester;

/**
 * Class to perform unit test of {@link ApexStarterException ApexStarterRunTimeException}}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class TestExceptions {

    @Test
    public void test() {
        new ExceptionsTester().test(ApexStarterException.class);
        new ExceptionsTester().test(ApexStarterRunTimeException.class);
    }
}
