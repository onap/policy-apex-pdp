/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TestApexStarterCommandLineArguments {

    /**
     * Test method for {@link org.onap.policy.apex.services.onappf.ApexStarterCommandLineArguments
     * #getPropertyFilePath()}.
     * Assert custom option was added to options object from super.
     */
    @Test
    void testCommandLineHasPropertyFileOption() {
        String[] args = {"-p", "someFile.json"};
        ApexStarterCommandLineArguments sut = new ApexStarterCommandLineArguments(args);
        assertEquals("someFile.json", sut.getPropertyFilePath());
    }

    /**
     * Test method for {@link org.onap.policy.apex.services.onappf.ApexStarterCommandLineArguments#version()}.
     * Assert method consults version.txt from Apex module.
     */
    @Test
    void testVersion() {
        String[] args = {"-v"};
        ApexStarterCommandLineArguments sut = new ApexStarterCommandLineArguments(args);
        assertThat(sut.version()).startsWith("ONAP Policy Framework Apex Starter Service");
    }

}
